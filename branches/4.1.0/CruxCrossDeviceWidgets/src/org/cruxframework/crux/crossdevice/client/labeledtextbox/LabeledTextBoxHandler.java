package org.cruxframework.crux.crossdevice.client.labeledtextbox;

import org.cruxframework.crux.core.client.controller.Create;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.TextBox;

/**
 *
 * @author    Daniel Martins - <code>daniel@cruxframework.org</code>
 *
 */
public class LabeledTextBoxHandler
{
	@Create
	protected HTMLPlaceHolder placeholder;

	public void preparePlaceholder(final TextBox textBox, String placeholder)
	{
		this.placeholder.createPlaceholder(textBox, placeholder);
	}


	static class HTMLPlaceHolder
	{
		public void createPlaceholder(final TextBox textBox, String placeholder)
		{
			textBox.getElement().setAttribute("placeholder", placeholder);
		}
	}

	static class JSPlaceHolder extends HTMLPlaceHolder
	{
		@Override
		public void createPlaceholder(final TextBox textBox, final String placeholder)
		{
			textBox.addBlurHandler(new BlurHandler()
			{

				@Override
				public void onBlur(BlurEvent event)
				{
					textBox.setValue(placeholder);
				}
			});

			textBox.addFocusHandler(new FocusHandler()
			{

				@Override
				public void onFocus(FocusEvent event)
				{
					textBox.setValue(null);
				}
			});
		}
	}

}
