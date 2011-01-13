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
package br.com.sysmap.crux.core.rebind.widget;

import org.json.JSONObject;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class WidgetCreatorContext
{
	private JSONObject childElement;
	private JSONObject widgetElement;
	private String widget;
	private String widgetId;

	public WidgetCreatorContext()
	{
	}
	public JSONObject getWidgetElement()
	{
		return widgetElement;
	}
	public String getWidget()
	{
		return widget;
	}
	public String getWidgetId()
	{
		return widgetId;
	}
	public String readWidgetProperty(String propertyName)
	{
        return widgetElement.optString(propertyName);
	}
	public String readChildProperty(String propertyName)
	{
		return childElement.optString(propertyName);
	}	
	public void setChildElement(JSONObject childElement)
	{
		this.childElement = childElement;
	}
	public JSONObject getChildElement()
	{
		return childElement;
	}
	void setWidgetElement(JSONObject widgetElement) 
	{
		this.widgetElement = widgetElement;
	}
	void setWidget(String widget) 
	{
		this.widget = widget;
	}
	void setWidgetId(String widgetId) 
	{
		this.widgetId = widgetId;
	}
}
