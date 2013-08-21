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
package org.cruxframework.crux.core.client.db.indexeddb;

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class IDBCursorWithValue extends IDBCursor
{
	protected IDBCursorWithValue(){}
	
	public final native JavaScriptObject getValue()/*-{
		return this.value;
	}-*/;
	
    public final native JsArrayMixed getArrayValue() /*-{
	    return this.value;
	}-*/;
	
    public final native JavaScriptObject getObjectValue() /*-{
	    return this.value;
	}-*/;

	public final native int getIntValue() /*-{
	    return this.value;
	}-*/;
	
	public final native String getStringValue() /*-{
		return this.value;
	}-*/;
	
	public final Date getDateValue() 
	{
		return new Date((long)getDoubleValue());
	}
	
	public final  native double getDoubleValue() /*-{
		return this.value;
	}-*/;
}
