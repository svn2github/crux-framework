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
package br.com.sysmap.crux.core.client.utils;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class JSONUtils
{
	/**
	 * @param object
	 * @param property
	 * @return
	 */
	public static String getStringProperty(JSONObject object, String property)
	{
		if (object.containsKey(property))
		{
			JSONString jsonString = object.get(property).isString();
			if (jsonString != null)
			{
				return jsonString.stringValue();
			}
		}
		return null;
	}
	
	/**
	 * @param object
	 * @param property
	 * @return
	 */
	public static String getUnsafeStringProperty(JSONObject object, String property)
	{
		assert (object.containsKey(property)): "Invalid property";
		return object.get(property).isString().stringValue();
	}
	
}
