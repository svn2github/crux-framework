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
package br.com.sysmap.crux.advanced.client.maskedtextbox;

import br.com.sysmap.crux.core.client.formatter.MaskedFormatter;

import com.google.gwt.user.client.ui.Widget;

/**
 * Base class for MaskedTextBox  Formatters
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 */
public abstract class MaskedTextBoxBaseFormatter implements MaskedFormatter
{
	private MaskedInput maskedInput;

	public void applyMask(Widget widget)
	{
		if (widget instanceof MaskedTextBox)
		{
			maskedInput = new MaskedInput(((MaskedTextBox) widget).textBox, getMask(), getPlaceHolder());
		}		
	}

	public void removeMask(Widget widget)
	{
		if (maskedInput != null && maskedInput.getTextBox() != null && maskedInput.getTextBox().equals(widget));
		{
			maskedInput.removeMask();
		}
	}
	
	protected char getPlaceHolder()
	{
		return '_';
	}	
}
