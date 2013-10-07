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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayString;

/**
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class JsUtils
{

	/**
	 * Read properties from native javascript objects. 
	 * @param object the Object where the property will be read from. 
	 * @param property the name of the property. You can pass inner properties using dot notation (prop1.prop2)
	 * @param output an array where the output will be written
	 */
	public static native void readPropertyValue(JavaScriptObject object, String property, JsArrayMixed output)/*-{
		function getDescendantProp(obj, desc) {
		    var arr = desc.split(".");
		    while(arr.length && (obj = obj[arr.shift()]));
		    return obj;
		}

		output.push(getDescendantProp(object, property));    
    }-*/;
	
	/**
	 * Read properties from native javascript objects. 
	 * @param object the Object where the property will be read from. 
	 * @param property the name of the property. You can pass inner properties using dot notation (prop1.prop2)
	 */
	public static native String readStringPropertyValue(JavaScriptObject object, String property)/*-{
	    var arr = property.split(".");
	    while(arr.length && (object = object[arr.shift()]));
	    return object;
    }-*/;

	/**
	 * Read properties from native javascript objects. 
	 * @param object the Object where the property will be read from. 
	 * @param property the name of the property. You can pass inner properties using dot notation (prop1.prop2)
	 */
	public static native int readIntPropertyValue(JavaScriptObject object, String property)/*-{
	    var arr = property.split(".");
	    while(arr.length && (object = object[arr.shift()]));
	    return object;
    }-*/;

	/**
	 * Read properties from native javascript objects. 
	 * @param object the Object where the property will be read from. 
	 * @param property the name of the property. You can pass inner properties using dot notation (prop1.prop2)
	 */
	public static native double readDoublePropertyValue(JavaScriptObject object, String property)/*-{
	    var arr = property.split(".");
	    while(arr.length && (object = object[arr.shift()]));
	    return object;
    }-*/;

	/**
	 * Read properties from native javascript objects. 
	 * @param object the Object where the property will be read from. 
	 * @param property the name of the property. You can pass inner properties using dot notation (prop1.prop2)
	 */
	public static native JavaScriptObject readObjectPropertyValue(JavaScriptObject object, String property)/*-{
	    var arr = property.split(".");
	    while(arr.length && (object = object[arr.shift()]));
	    return object;
    }-*/;

	/**
	 * Write property to native javascript objects. 
	 * @param object the Object where the property will be written. 
	 * @param property the name of the property. You can pass inner properties using dot notation (prop1.prop2)
	 * @param input an array containing the property value.
	 * @param extractArrayContent It true, the first element from input array is used as property value.
	 */
	public static native void writePropertyValue(JavaScriptObject object, String property, JsArrayMixed intput, boolean extractArrayContent)/*-{
		var obj = object;
		var arr = property.split(".");
		while((arr.length-1) && (obj = obj[arr.shift()]));
	    if (obj)
	    {
		    if (input)
		    {
		    	if (extractArrayContent && input.length > 0)
		    	{
		    		obj[arr.shift()] = input[0];
		    	}
		    	else if (input.length > 0)
		    	{
		    		obj[arr.shift()] = input;
		    	}
		    	else
		    	{
		    		obj[arr.shift()] = null;
		    	}
		    }
		    else
		    {
		    	obj[arr.shift()] = null;
		    }
	    }
    }-*/;

	/**
	 * Write property to native javascript objects. 
	 * @param object the Object where the property will be written. 
	 * @param property the name of the property. You can pass inner properties using dot notation (prop1.prop2)
	 * @param input the property value.
	 */
	public static native void writePropertyValue(JavaScriptObject object, String property, String input)/*-{
		var obj = object;
		var arr = property.split(".");
		while((arr.length-1) && (obj = obj[arr.shift()]));
	    if (obj)
	    {
    		obj[arr.shift()] = input;
	    }
    }-*/;

	/**
	 * Write property to native javascript objects. 
	 * @param object the Object where the property will be written. 
	 * @param property the name of the property. You can pass inner properties using dot notation (prop1.prop2)
	 * @param input the property value.
	 */
	public static native void writePropertyValue(JavaScriptObject object, String property, double input)/*-{
		var obj = object;
		var arr = property.split(".");
		while((arr.length-1) && (obj = obj[arr.shift()]));
	    if (obj)
	    {
    		obj[arr.shift()] = input;
	    }
    }-*/;

	/**
	 * Write property to native javascript objects. 
	 * @param object the Object where the property will be written. 
	 * @param property the name of the property. You can pass inner properties using dot notation (prop1.prop2)
	 * @param input the property value.
	 */
	public static native void writePropertyValue(JavaScriptObject object, String property, JavaScriptObject input)/*-{
		var obj = object;
		var arr = property.split(".");
		while((arr.length-1) && (obj = obj[arr.shift()]));
	    if (obj)
	    {
    		obj[arr.shift()] = input;
	    }
    }-*/;

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
