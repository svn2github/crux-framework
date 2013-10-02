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
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class FileButton extends Composite implements HasChangeHandlers
{
	private FlowPanel mainPanel;
	private Button visibleButton;
	private FileInput fileInput;

	public FileButton()
    {
		mainPanel = new FlowPanel();
		mainPanel.getElement().getStyle().setPosition(Position.RELATIVE);
		
		fileInput = new FileInput();
		
		Style style = fileInput.getElement().getStyle();
		style.setPosition(Position.RELATIVE);
		style.setTextAlign(TextAlign.RIGHT);
		style.setOpacity(0);
		style.setZIndex(0);
		style.setWidth(100, Unit.PCT);
		style.setHeight(100, Unit.PCT);
		mainPanel.add(fileInput);
		
		visibleButton = new Button();
		visibleButton.setStyleName("chooseButton");
		style = visibleButton.getElement().getStyle();
		style.setPosition(Position.ABSOLUTE);
		style.setTop(0, Unit.PX);
		style.setLeft(0, Unit.PX);
		style.setZIndex(1);
		style.setWidth(100, Unit.PCT);
		style.setHeight(100, Unit.PCT);
		mainPanel.add(visibleButton);
		
		visibleButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				//Try createclickEvent() too.
				NativeEvent nevent = Document.get().createFocusEvent();
				DomEvent.fireNativeEvent(nevent, fileInput); 
//				((InputElement) fileInput.getElement()). <InputElement>cast().click()
			}
		});
		
		initWidget(mainPanel);
    }
	
	public void setText(String text)
	{
		visibleButton.setText(text);
	}

	public String getText()
	{
	    return visibleButton.getText();
	}
	
	public boolean isMultiple()
	{
		return fileInput.isMultiple();
	}
	
	public void setMultiple(boolean multiple)
	{
		fileInput.setMultiple(multiple);
	}

    public HandlerRegistration addChangeHandler(ChangeHandler handler)
    {
	    return fileInput.addChangeHandler(handler);
    }
}

class FileInput extends Widget implements HasChangeHandlers
{
	public FileInput()
    {
		FileUpload fileInput = new FileUpload();
		setElement(fileInput.getElement());
    }
	
	public boolean isMultiple()
	{
		return getElement().getPropertyBoolean("multiple");
	}
	
	public void setMultiple(boolean multiple)
	{
		getElement().setPropertyBoolean("multiple", multiple);
	}

    public HandlerRegistration addChangeHandler(ChangeHandler handler)
    {
	    return addDomHandler(handler, ChangeEvent.getType());
    }
}
