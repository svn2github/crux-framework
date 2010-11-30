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
package br.com.sysmap.crux.core.client.screen;

import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;

import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class WidgetFactoryContext
{
	private CruxMetaDataElement childElement;
	private CruxMetaDataElement widgetElement;
	private Widget widget;
	private String widgetId;

	protected WidgetFactoryContext()
	{
	}
	public CruxMetaDataElement getWidgetElement()
	{
		return widgetElement;
	}
	@SuppressWarnings("unchecked")
	public <W extends Widget> W getWidget()
	{
		return (W) widget;
	}
	public String getWidgetId()
	{
		return widgetId;
	}
	public String readWidgetProperty(String propertyName)
	{
		return widgetElement.getProperty(propertyName);
	}
	public String readChildProperty(String propertyName)
	{
		return childElement.getProperty(propertyName);
	}	
	public void setChildElement(CruxMetaDataElement childElement)
	{
		this.childElement = childElement;
	}
	public CruxMetaDataElement getChildElement()
	{
		return childElement;
	}
	void setWidgetElement(CruxMetaDataElement widgetElement) 
	{
		this.widgetElement = widgetElement;
	}
	void setWidget(Widget widget) 
	{
		this.widget = widget;
	}
	void setWidgetId(String widgetId) 
	{
		this.widgetId = widgetId;
	}
}
