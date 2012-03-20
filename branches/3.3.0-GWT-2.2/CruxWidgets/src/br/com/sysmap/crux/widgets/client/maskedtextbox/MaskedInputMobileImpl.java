/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.sysmap.crux.widgets.client.maskedtextbox;

import br.com.sysmap.crux.core.client.collection.FastList;
import br.com.sysmap.crux.core.client.collection.FastMap;
import br.com.sysmap.crux.widgets.client.event.paste.PasteEvent;
import br.com.sysmap.crux.widgets.client.event.paste.PasteHandler;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.TextBox;


/**
 * Masks an TextBox
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class MaskedInputMobileImpl implements MaskedInput, KeyDownHandler, KeyPressHandler, FocusHandler, BlurHandler, PasteHandler
{
	private static FastMap<String> definitions = new FastMap<String>();
	static
	{
		definitions.put("9", "[0-9]");
		definitions.put("a", "[A-Za-z]");
		definitions.put("*", "[A-Za-z0-9]");
	}
	
	private TextBox textBox;
	private FastList<String> tests = new FastList<String>();
	private char placeHolder;
	private char[] buffer;
	private String focusText;

	private HandlerRegistration keyDownHandlerRegistration;
	private HandlerRegistration keyPressHandlerRegistration;
	private HandlerRegistration focusHandlerRegistration;
	private HandlerRegistration blurHandlerRegistration;
	private HandlerRegistration pasteHandlerRegistration;
	private MaskedTextBox maskedTextBox;
	private int numTypeableChars;
	private boolean ignoreKey;
	
	/**
	 * Constructor
	 * @param textBox
	 * @param mask
	 * @param placeHolder
	 */
	public void applyMask (MaskedTextBox maskedTextBox, String mask, char placeHolder, boolean clearIfNotValid)
	{
		this.maskedTextBox = maskedTextBox;
		this.placeHolder = placeHolder;
		this.buffer = new char[mask.length()];
		this.numTypeableChars = 0;
		
		for (int i=0; i< mask.length(); i++)
		{
			char c = mask.charAt(i);
			String key = c+"";
			if (definitions.containsKey(key))
			{
				this.tests.add(definitions.get(key));
				this.buffer[i] = placeHolder;
				this.numTypeableChars++;
			}
			else
			{
				this.buffer[i] = c;
			}
		}

		this.textBox = maskedTextBox.textBox;
		keyDownHandlerRegistration = this.textBox.addKeyDownHandler(this);
		keyPressHandlerRegistration = this.textBox.addKeyPressHandler(this);
		focusHandlerRegistration = this.textBox.addFocusHandler(this);
		blurHandlerRegistration = this.textBox.addBlurHandler(this);
		pasteHandlerRegistration = this.maskedTextBox.addPasteHandler(this);
		
		this.textBox.getElement().setAttribute("placeholder", new String(this.buffer));
		String textValue = this.textBox.getText();
		trySetValue(textValue, true);
	}
	
	/**
	 * Unmask the current textBox
	 */
	public void removeMask()
	{
		if (keyDownHandlerRegistration != null)
		{
			keyDownHandlerRegistration.removeHandler();
		}
		if (keyPressHandlerRegistration != null)
		{
			keyPressHandlerRegistration.removeHandler();
		}
		if (focusHandlerRegistration != null)
		{
			focusHandlerRegistration.removeHandler();
		}
		if (blurHandlerRegistration != null)
		{
			blurHandlerRegistration.removeHandler();
		}
		if (pasteHandlerRegistration != null)
		{
			pasteHandlerRegistration.removeHandler();
		}
		this.textBox = null;
		this.maskedTextBox = null;
	}
	
	/**
	 * keyDown event handler
	 */
	public void onKeyDown(KeyDownEvent event)
	{
		if (textBox.isReadOnly())
		{
			event.preventDefault();
		}
		else
		{
			int code = event.getNativeKeyCode();
			this.ignoreKey = (code == KeyCodes.KEY_BACKSPACE || code == KeyCodes.KEY_DELETE || 
					code == KeyCodes.KEY_LEFT || code == KeyCodes.KEY_RIGHT || 
					code == KeyCodes.KEY_HOME || code == KeyCodes.KEY_END || 
					code == KeyCodes.KEY_SHIFT || code == KeyCodes.KEY_TAB);
			if (!this.ignoreKey)
			{
				boolean block = (code < 8 || (code > 8 && code < 16) || (code > 16 && code < 32) || (code > 32 && code < 41));
				if (block)
				{
					event.preventDefault();
				}
			}
		}
	}

	/**
	 * keyPress event handler
	 */
	public void onKeyPress(KeyPressEvent event)
	{
		if (event.isControlKeyDown() || event.isAltKeyDown())
		{
			return;
		}

		if (!this.ignoreKey)
		{
			char code = event.getCharCode();
			if (!((code >= 41 && code <= 122) || code == 32 || code > 186)) //non typeable characters
			{
				event.preventDefault();
			}
			else
			{
				String textValue = textBox.getText();
				int textLength = textValue.length();
				int selectionLength = textBox.getSelectionLength();
				if (textLength >= tests.size() && selectionLength == 0)
				{
					event.preventDefault();
				}
				else
				{
					String c = ""+event.getCharCode();
					if (selectionLength >0)
					{
						textLength = textBox.getCursorPos();
					}
					if (!c.matches(tests.get(textLength)))
					{
						event.preventDefault();
					}
				}
			}
		}
	}

	/**
	 * Focus event handler
	 */
	public void onFocus(FocusEvent event)
	{
		focusText = textBox.getText();
		textBox.setText(unmask(focusText));
	}

	/**
	 * Blur event handler
	 */
	public void onBlur(BlurEvent event)
	{
		textBox.setText(mask(textBox.getText()));
		if (textBox.getText() != focusText)
		{
			ValueChangeEvent.fire(textBox, textBox.getText());
		}
	}
	
	public void onPaste(PasteEvent event)
	{
		String textValue = this.textBox.getText();
		trySetValue(textValue, false);
	}

	/**
	 * @param textValue
	 * @param applyMask
	 */
	private void trySetValue(String textValue, boolean applyMask)
	{
		int textValueLength = textValue.length();
		if (textValueLength == tests.size())
		{
			trySetValueUnmasked(textValue, applyMask, textValueLength);
		}
		else if (textValueLength > tests.size() && textValueLength <= buffer.length)
		{
			trySetValueMasked(textValue, applyMask, textValueLength);
		}
		else
		{
			textBox.setText("");
		}
	}

	/**
	 * @param textValue
	 * @param applyMask
	 * @param textValueLength
	 */
	private void trySetValueUnmasked(String textValue, boolean applyMask, int textValueLength)
	{
		for(int i=0; i<textValueLength; i++)
		{
			String c = ""+textValue.charAt(i);
			if (!c.matches(tests.get(i)))
			{
				textBox.setText("");
				return;
			}
		}
		if (applyMask)
		{
			textBox.setText(mask(textValue));
		}
		else
		{
			textBox.setText(textValue);
		}
		return;
	}

	/**
	 * @param textValue
	 * @param applyMask
	 * @param textValueLength
	 */
	private void trySetValueMasked(String textValue, boolean applyMask, int textValueLength)
	{
		StringBuilder unmasked = new StringBuilder();
		int j=0;
		for(int i=0; i<textValueLength; i++)
		{
			String c = ""+textValue.charAt(i);
			if (buffer[i] != placeHolder)
			{
				if (buffer[i] != textValue.charAt(i))
				{
					textBox.setText("");
					return;
				}
			}
			else if (!c.matches(tests.get(j++)))
			{
				textBox.setText("");
				return;
			}
			else
			{
				unmasked.append(c);
			}
		}
		if (applyMask)
		{
			textBox.setText(mask(unmasked.toString()));
		}
		else
		{
			textBox.setText(unmasked.toString());
		}
	}

	/**
	 * 
	 */
	private String mask(String textVal)
	{
		StringBuilder str = new StringBuilder();

		if (textVal.length() == this.numTypeableChars)
		{
			int textBoxIndex = 0;

			for (char c : buffer)
			{
				if (c == placeHolder)
				{
					str.append(textVal.charAt(textBoxIndex++));
				}
				else
				{
					str.append(c);
				}
			}

			return str.toString();
		}
		else
		{
			return "";
		}
	}
	
	/**
	 * 
	 */
	private String unmask(String textVal)
	{
		StringBuilder str = new StringBuilder();

		if (textVal.length() == this.buffer.length)
		{
			for (int i=0; i<this.buffer.length; i++)
			{
				char c = this.buffer[i];
				if (c == placeHolder)
				{
					str.append(textVal.charAt(i));
				}
			}

			return str.toString();
		}
		else
		{
			return "";
		}
	}
}
