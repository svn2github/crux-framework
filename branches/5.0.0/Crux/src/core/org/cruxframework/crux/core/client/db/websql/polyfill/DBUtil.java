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
package org.cruxframework.crux.core.client.db.websql.polyfill;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.file.Blob;
import org.cruxframework.crux.core.client.file.FileReader;
import org.cruxframework.crux.core.client.file.FileReader.ReaderStringCallback;
import org.cruxframework.crux.core.client.utils.FileUtils;
import org.cruxframework.crux.core.client.utils.JsUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DBUtil
{
	protected static Logger logger = Logger.getLogger(DBUtil.class.getName());

	public static String encodeKey(JavaScriptObject object)
	{
		if (object == null)
		{
			return JSONNull.getInstance().toString();
		}
		
		JSONValue jsonObject = JsUtils.toJSONValue(object);
		JSONArray jsonArray = jsonObject.isArray();
		if (jsonArray != null)
		{
			if (jsonArray.size() > 1)
			{
				return jsonArray.toString();
			}
			if (jsonArray.size() > 0)
			{
				return jsonArray.get(0).toString();
			}
		}
		return jsonObject.toString();
	}
	
	public static void encodeValue(JavaScriptObject object, final EncodeCallback callback)
	{
		if (object == null)
		{
			callback.onEncode(JSONNull.getInstance().toString());
			return;
		}
		if (object instanceof Blob)
		{
			FileReader fileReader = FileReader.createIfSupported();
			assert(fileReader != null):"Unsupported browser";
			fileReader.readAsDataURL((Blob)object, new ReaderStringCallback()
			{
				@Override
				public void onComplete(String result)
				{
					callback.onEncode(result);
				}
			});
		}
		callback.onEncode(JsUtils.toJSONValue(object).toString());
		return;
	}
	
	public static <T extends JavaScriptObject> T decodeValue(String encodedObject)
	{
		if (StringUtils.isEmpty(encodedObject))
		{
			return null;
		}
		if (encodedObject.startsWith("data:"))
		{
			return FileUtils.fromDataURI(encodedObject).cast();
		}
		JSONValue jsonValue = JSONParser.parseStrict(encodedObject);
		return JsUtils.fromJSONValue(jsonValue).cast();
	}
	
	public static JsArrayMixed decodeKey(String encodedKey)
    {
		if (StringUtils.isEmpty(encodedKey))
		{
			return null;
		}
		JSONValue jsonValue = JSONParser.parseStrict(encodedKey);
		if (jsonValue.isArray() == null)
		{
			jsonValue = new JSONArray().set(0, jsonValue);
		}
	    return JsUtils.fromJSONValue(jsonValue).cast();
    }

	public static void throwDOMException(String name, String message)
	{
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.SEVERE, "An error has occurred. Error name [" +name+"]. Error message ["+message+"]");
		}
		throwDOMExceptionNative(name, message);
    }
	
	private static native void throwDOMExceptionNative(String name, String message)/*-{
        var e = new DOMException.constructor(0, message);
        e.name = name;
        e.message = message;
        throw e;
    }-*/;
	
	public static interface EncodeCallback
	{
		void onEncode(String encoded);
	}
}
