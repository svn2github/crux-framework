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

import org.cruxframework.crux.core.client.db.indexeddb.events.IDBObjectStoreEvent;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class IDBCursor extends JavaScriptObject
{
	public static enum IDBCursorDirection {next, nextunique, prev, prevunique}

	public static class IDBCursorStoreRequest extends IDBRequest<IDBCursor>
	{
		protected IDBCursorStoreRequest(){}
		
		public final native void onSuccess(IDBObjectStoreEvent.Handler handler) /*-{
			this.onsuccess = function(evt) {
		    	handler.@org.cruxframework.crux.core.client.db.indexeddb.events.IDBObjectStoreEvent.Handler::onSuccess(Lorg/cruxframework/crux/core/client/db/indexeddb/events/IDBObjectStoreEvent;)(evt);
			};              
		}-*/;
	}

    protected IDBCursor() {}
    
    public final native JavaScriptObject getSource()/*-{
    	return this.source;
    }-*/;
    
    public final IDBCursorDirection getDirection()
    {
    	return IDBCursorDirection.valueOf(getDirectionNative());
    }
    
    private native String getDirectionNative()/*-{
		return this.source;
	}-*/;

    public final native JsArrayMixed getObjectKey() /*-{
	    return this.key;
	}-*/;
	
    public final native int getIntKey() /*-{
	    return this.key;
	}-*/;
    
    public final native String getStringKey() /*-{
    	return this.key;
	}-*/;

	public final native JsArrayMixed getObjectPrimaryKey() /*-{
	    return this.primaryKey;
	}-*/;
	
	public final native String getStringPrimaryKey() /*-{
	    return this.primaryKey;
	}-*/;

	public final native int getIntPrimaryKey() /*-{
	    return this.primaryKey;
	}-*/;

	public final native IDBCursorStoreRequest update(JavaScriptObject value) /*-{
	    return this.update(value);
	}-*/;
    
	public final native void advance(int count) /*-{
	    this.advance(count);
	}-*/; 
	
	public final native void continueCursor()/*-{
	      this["continue"]();
	}-*/;
	
	public final native void continueCursor(JsArrayMixed key)/*-{
	      this["continue"](key);
	}-*/;
	
	public final native void continueCursor(String key)/*-{
	    this["continue"](key);
	}-*/;
	
	public final native void continueCursor(int key)/*-{
	    this["continue"](key);
	}-*/;

	public final native IDBRequest<IDBCursor> delete(JsArrayMixed key) /*-{
	    return this["delete"](key);
	}-*/;

	public final native IDBRequest<IDBCursor> delete(String key) /*-{
	    return this["delete"](key);
	}-*/;

	public final native IDBRequest<IDBCursor> delete(int key) /*-{
	    return this["delete"](key);
	}-*/;
}
