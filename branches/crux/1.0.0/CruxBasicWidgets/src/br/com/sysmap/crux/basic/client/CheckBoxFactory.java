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
package br.com.sysmap.crux.basic.client;

import br.com.sysmap.crux.core.client.event.bind.ChangeEvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasHTML;

/**
 * CheckBoxFactory WidgetFactory.
 * @author Thiago Bustamante
 *
 */
public class CheckBoxFactory extends FocusWidgetFactory<CheckBox> 
{
	/**
	 * process widget attributes
	 * @throws InterfaceConfigException 
	 * @see #WidgetFactory.processAttributes
	 */
	@Override
	protected void processAttributes(CheckBox widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);
		String checked = element.getAttribute("_checked");
		if (checked != null && checked.trim().length() > 0)
		{
			widget.setValue(Boolean.parseBoolean(checked));
		}

		String innerHtml = element.getInnerHTML();
		String text = element.getAttribute("_text");		
		if ((text == null || text.length() ==0) && innerHtml != null && innerHtml.length() > 0)
		{
			((HasHTML)widget).setHTML(innerHtml);
		}
	}
	
	@Override
	protected void processEvents(CheckBox widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processEvents(widget, element, widgetId);
		ChangeEvtBind.bindValueEvent(element, widget);
	}

	@Override
	protected CheckBox instantiateWidget(Element element, String widgetId) 
	{
		return new CheckBox();
	}
}
