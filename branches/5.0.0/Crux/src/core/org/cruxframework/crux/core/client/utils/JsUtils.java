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
package org.cruxframework.crux.core.client.utils;

import java.util.ArrayList;
import java.util.List;

import org.cruxframework.crux.core.client.collection.FastList;

import com.google.gwt.core.client.JsArrayString;

/**
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class JsUtils
{
	public static String[] toArray(JsArrayString jSArray)
	{
		String[] result = new String[jSArray.length()];
		for (int i = 0; i < jSArray.length(); i++)
		{
			result[i] = jSArray.get(i);
		}
		return result;
	}

	public static List<String> toList(JsArrayString jSArray)
	{
		List<String> result = new ArrayList<String>(jSArray.length());
		for (int i = 0; i < jSArray.length(); i++)
		{
			result.add(jSArray.get(i));
		}
		return result;
	}

	public static FastList<String> toFastList(JsArrayString jSArray)
	{
		FastList<String> result = new FastList<String>();
		for (int i = 0; i < jSArray.length(); i++)
		{
			result.add(jSArray.get(i));
		}
		return result;
	}

	public static JsArrayString toJsArray(List<String> list)
	{
		JsArrayString js = JsArrayString.createArray().cast();
		for (int i = 0; i < list.size(); i++)
		{
			js.set(i, list.get(i));
		}
		return js;
	}

	public static JsArrayString toJsArray(String[] args)
	{
		JsArrayString js = JsArrayString.createArray().cast();
		for (int i = 0; i < args.length; i++)
		{
			js.set(i, args[i]);
		}
		return js;
	}

	public static native String escape(String input)/*-{
		return escape(input);
	}-*/;

	public static native String unescape(String input)/*-{
		return unescape(input);
	}-*/;
	
}
