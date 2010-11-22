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
package br.com.sysmap.crux.core.client.screen.parser;

import br.com.sysmap.crux.core.client.collection.Array;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CruxMetaData extends JavaScriptObject
{
	/**
	 * Retrive the screen identifier
	 * @return
	 */
	public native static String getScreenId()/*-{
		return $wnd.__CruxScreenId_;
	}-*/;
	
	/**
	 * Loads the Crux Meta Data information. It is represented in JSON format and is retrieved through 
	 * a native JS function.
	 * 
	 * @param metadata Crux metadata to parse
	 * @return a List that has been built by parsing the metadata string
	 * @throws CruxMetaDataException 
	 */
	public static Array<CruxMetaData> loadMetaData() throws CruxMetaDataException
	{
		try
		{
			return callMetaDataNativeLoader();
		}
		catch (JavaScriptException ex)
		{
			throw new CruxMetaDataException(ex);
		}
	}

	/**
	 * Call the native function that loads the CruxMetaData array.
	 */
	private static native Array<CruxMetaData> callMetaDataNativeLoader() /*-{
		return $wnd.__CruxMetaData_();
	}-*/;

	/**
	 * 
	 */
	protected CruxMetaData()
    {
    }
	
	/**
	 * @param key
	 * @return
	 */
	public final boolean containsKey(String key)
	{
		return key != null && jsniContainsKey(key);
	}
	
	/**
	 * @return
	 */
	public final Array<CruxMetaData> getChildren()
	{
		return jsniGetArray("children");
	}
	
	/**
	 * @param key
	 * @return
	 */
	public final String getProperty(String key)
	{
		return key == null ? null : jsniGetString(key);
	}

	/**
	 * @param key
	 * @param property
	 */
	public final void setProperty(String key, String property)
	{
		assert key != null;
		jsniPut(key, property);
	}	
	
	/**
	 * @param index
	 * @return
	 */
	private native boolean jsniContainsKey(String index) /*-{
		return this[index] !== undefined;
	}-*/;	
	
	/**
	 * @param index
	 * @return
	 */
	private native Array<CruxMetaData> jsniGetArray(String index) /*-{
		return this[index];
	}-*/;

	/**
	 * @param index
	 * @return
	 */
	private native String jsniGetString(String index) /*-{
		return this[index];
	}-*/;
	
	/**
	 * @param index
	 * @param value
	 */
	private native void jsniPut(String index, String value) /*-{
		this[index] = value;
	}-*/;	
}
