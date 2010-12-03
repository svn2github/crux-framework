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
package br.com.sysmap.crux.core.rebind.scanner.screen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.sysmap.crux.core.client.utils.StringUtils;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class WidgetParserImpl implements WidgetParser
{
	/**
	 * @throws JSONException 
	 * 
	 */
	public void parse(Widget widget, JSONObject elem) throws JSONException 
	{
		parse(elem, widget, true, "");
	}

	/**
	 * 
	 * @param elem
	 * @param widget
	 * @param parseIfWidget
	 * @throws JSONException 
	 */
	private void parse(JSONObject elem, Widget widget, boolean parseIfWidget, String prefixSubTag) throws JSONException
	{
			if(!isWidget(elem) || parseIfWidget)
			{
				extractProperties(elem, widget, prefixSubTag);
				
				if (elem.has("children"))
				{
					JSONArray children = elem.getJSONArray("children");

					int length = children.length();
					if(length > 0)
					{
						for (int i = 0; i < length; i++)
						{
							JSONObject child = children.getJSONObject(i);
							parse(child, widget, false, prefixSubTag+getSubtTagPrefix(child));
						}
					}
				}
			}
	}
	
	/**
	 * @param elem
	 * @return
	 * @throws JSONException
	 */
	private String getSubtTagPrefix(JSONObject elem) throws JSONException
	{
		if (elem.has("childTag"))
		{
			return elem.getString("childTag")+"_";
		}
		return "";
	}
	
	/**
	 * @param elem
	 * @return
	 */
	private boolean isWidget(JSONObject elem)
	{
		return elem.has("type");
	}
	
	/**
	 * 
	 * @param elem
	 * @param widget
	 * @param prefixSubTag
	 * @throws JSONException 
	 */
	private void extractProperties(JSONObject elem, Widget widget, String prefixSubTag) throws JSONException
	{
		if (!StringUtils.isEmpty(prefixSubTag))
		{
			widget.addProperty(prefixSubTag, "");
		}
		
		String[] attributes = JSONObject.getNames(elem);
		
		int length = attributes.length;
		for (int i = 0; i < length; i++) 
		{
			String attrName = attributes[i];
			
			if (attrName.equals("id") || attrName.equals("type") || attrName.equals("childTag") || attrName.equals("children"))
			{
				continue;
			}
			
			if (attrName.startsWith("on"))
			{
				setEvent(widget, prefixSubTag+attrName, elem.getString(attrName));
			}
			else
			{
				widget.addProperty(prefixSubTag+attrName, elem.getString(attrName));
			}
		}
	}

	/**
	 * 
	 * @param widget
	 * @param evtName
	 * @param value
	 */
	protected void setEvent(Widget widget, String evtName, String value)
	{
		Event event = EventFactory.getEvent(evtName, value);
		
		if (event != null)
		{
			widget.addEvent(event);
		}
	}
	
}
