/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.uploader;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class FileInput extends Widget implements HasChangeHandlers
{
	public FileInput()
    {
		InputElement fileInputElement = Document.get().createFileInputElement();
		setElement(fileInputElement);
    }
	
	public boolean isMultiple()
	{
		return getElement().getPropertyBoolean("multiple");
	}
	
	public void setMultiple(boolean multiple)
	{
		getElement().setPropertyBoolean("multiple", multiple);
	}

	@Override
    public HandlerRegistration addChangeHandler(ChangeHandler handler)
    {
	    return addDomHandler(handler, ChangeEvent.getType());
    }
}
