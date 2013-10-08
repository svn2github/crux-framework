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

import org.cruxframework.crux.core.client.utils.JsUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DBRequest extends JavaScriptObject
{
	protected DBRequest(){}
	
	public static DBRequest create()
	{
		DBRequest request = DBRequest.createObject().cast();
		request.initNativeFunctions();
		return request;
	}

	private native void initNativeFunctions()/*-{
        this.onblocked = this.onsuccess = this.onerror = this.result = this.error = this.source = this.transaction = null;
        this.readyState = "pending";
	}-*/;
	
	protected final native void setTransaction(DBTransaction dbTransaction)/*-{
		this.transaction = dbTransaction;
    }-*/;

	public final native DBTransaction getTransaction() /*-{
		return this.transaction;
	}-*/;

	protected final native void setSource(JavaScriptObject src)/*-{
		this.source = src;
    }-*/;
	
	public final native JavaScriptObject getSource() /*-{
		return this.source;
	}-*/;
	
	public final native String readyState()/*-{
		return this.readyState;
	}-*/;
	
	protected final native void setReadyState(String state)/*-{
		this.readyState = state;
	}-*/;

	protected final native void setError(DBError error)/*-{
		this.error = error;
	}-*/;

	protected final native void setResult(JavaScriptObject object)/*-{
	    this.result = object;
    }-*/;

	protected final native void setResult(String value)/*-{
	    this.result = value;
    }-*/;

	protected final native void setResult(double value)/*-{
	    this.result = value;
    }-*/;

	protected final void setContentAsResult(JsArrayMixed object)
	{
		JsUtils.writePropertyValue(this, "result", object, true);
	}
}
