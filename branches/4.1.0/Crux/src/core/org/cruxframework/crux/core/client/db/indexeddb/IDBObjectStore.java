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

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.db.indexeddb.IDBCursor.IDBCursorDirection;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBCursorEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBObjectRetrieveEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBObjectStoreEvent;
import org.cruxframework.crux.core.client.utils.JsUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class IDBObjectStore extends JavaScriptObject
{
	public static class IDBObjectStoreRequest extends IDBRequest<IDBObjectStore>
	{
		protected IDBObjectStoreRequest(){}
		
		public final native void onSuccess(IDBObjectStoreEvent.Handler handler) /*-{
			this.onsuccess = function(evt) {
		    	handler.@org.cruxframework.crux.core.client.db.indexeddb.events.IDBObjectStoreEvent.Handler::onSuccess(Lorg/cruxframework/crux/core/client/db/indexeddb/events/IDBObjectStoreEvent;)(evt);
			};              
		}-*/;
	}
	
	public static class IDBObjectRetrieveRequest extends IDBRequest<IDBObjectStore>
	{
		protected IDBObjectRetrieveRequest(){}
		
		public final native void onSuccess(IDBObjectRetrieveEvent.Handler handler) /*-{
			this.onsuccess = function(evt) {
		    	handler.@org.cruxframework.crux.core.client.db.indexeddb.events.IDBObjectRetrieveEvent.Handler::onSuccess(Lorg/cruxframework/crux/core/client/db/indexeddb/events/IDBObjectRetrieveEvent;)(evt);
			};              
		}-*/;
	}

	public static class IDBObjectCursorRequest extends IDBRequest<IDBObjectStore>
	{
		protected IDBObjectCursorRequest(){}
		
		public final native void onSuccess(IDBCursorEvent.Handler handler) /*-{
			this.onsuccess = function(evt) {
		    	handler.@org.cruxframework.crux.core.client.db.indexeddb.events.IDBCursorEvent.Handler::onSuccess(Lorg/cruxframework/crux/core/client/db/indexeddb/events/IDBCursorEvent;)(evt);
			};              
		}-*/;
	}

	protected IDBObjectStore(){}

	public final native String getName() /*-{
	    return this.name;
	}-*/;
	
    public final native String getKeyPath() /*-{
	    return this.keyPath;
	}-*/;

    public final FastList<String> getCompositeKeyPath()
    {
    	return JsUtils.toFastList(getCompositeKeyPathNative());
    }
    
    private native JsArrayString getCompositeKeyPathNative() /*-{
	    return this.keyPath;
	}-*/;

	public final FastList<String> getIndexNames() 
	{
	    return JsUtils.toFastList(getIndexNamesNative());
	}	
	
	private native JsArrayString getIndexNamesNative() /*-{
	    return this.indexNames;
	}-*/;
	
    public final native IDBTransaction getTransaction() /*-{
	    return this.transaction;
	}-*/;
    
    public final native boolean istAutoIncrement()/*-{
		return this.autoIncrement;
	}-*/;
    
    public final native IDBObjectStoreRequest put(JavaScriptObject value) /*-{
	    return this.put(value);
	}-*/;
	
	public final native IDBObjectStoreRequest put(JavaScriptObject value,JavaScriptObject key) /*-{
	    return this.put(value,key);
	}-*/;

	public final native IDBObjectStoreRequest put(JavaScriptObject value,String key) /*-{
	    return this.put(value,key);
	}-*/;
	
	public final native IDBObjectStoreRequest put(JavaScriptObject value,int key) /*-{
	    return this.put(value,key);
	}-*/;

	public final native IDBObjectStoreRequest add(JavaScriptObject value) /*-{
	    return this.add(value);
	}-*/;
	
	public final native IDBObjectStoreRequest add(JavaScriptObject value,JavaScriptObject key) /*-{
	    return this.add(value,key);
	}-*/;

	public final native IDBObjectStoreRequest add(JavaScriptObject value,String key) /*-{
	    return this.add(value,key);
	}-*/;
	
	public final native IDBObjectStoreRequest add(JavaScriptObject value,int key) /*-{
	    return this.add(value,key);
	}-*/;

	public final native IDBRequest<IDBObjectStore> delete(JavaScriptObject key) /*-{
	    return this["delete"](key);
	}-*/;

	public final native IDBRequest<IDBObjectStore> delete(String key) /*-{
	    return this["delete"](key);
	}-*/;
	
	public final native IDBRequest<IDBObjectStore> delete(int key) /*-{
	    return this["delete"](key);
	}-*/;
	
	public final native IDBObjectRetrieveRequest get(JavaScriptObject key) /*-{
	    return this.get(key);
	}-*/;

	public final native IDBObjectRetrieveRequest get(String key) /*-{
	    return this.get(key);
	}-*/;

	public final native IDBObjectRetrieveRequest get(int key) /*-{
	    return this.get(key);
	}-*/;

	public final native IDBRequest<IDBObjectStore> clear() /*-{
	    return this.clear();
	}-*/;

	public final native IDBObjectCursorRequest openCursor() /*-{
	    return this.openCursor();
	}-*/;
	
	public final native IDBObjectCursorRequest openCursor(IDBKeyRange range) /*-{
	    return this.openCursor(range);
	}-*/;
	
	public final IDBObjectCursorRequest openCursor(IDBKeyRange range, IDBCursorDirection direction)
	{
		return openCursorNative(range, direction.toString());
	}
	
	private native IDBObjectCursorRequest openCursorNative(IDBKeyRange range, String direction) /*-{
	    return this.openCursor(range,direction);
	}-*/;
	
	public final native IDBObjectCursorRequest count() /*-{
	    return this.count();
	}-*/;

	public final native IDBObjectCursorRequest count(IDBKeyRange range) /*-{
	    return this.count(range);
	}-*/;

	public final native IDBIndex createIndex(String name, String keyPath)/*-{
	    return this.createIndex(name, keyPath);
	}-*/;
	
	public final IDBIndex createIndex(String name, String[] keyPath)
	{
	    return this.createIndexNative(name, JsUtils.toJsArray(keyPath));
	};
	
	public final native IDBIndex createIndexNative(String name, JsArrayString keyPath)/*-{
	    return this.createIndex(name, keyPath);
	}-*/;

	public final native IDBIndex createIndex(String name, String keyPath, IDBIndexParameters params)/*-{
	    return this.createIndex(name, keyPath, params);
	}-*/;

	public final IDBIndex createIndex(String name, String[] keyPath, IDBIndexParameters params)
	{
	    return this.createIndexNative(name, JsUtils.toJsArray(keyPath), params);
	};

	private native IDBIndex createIndexNative(String name, JsArrayString keyPath, IDBIndexParameters params)/*-{
	    return this.createIndex(name, keyPath, params);
	}-*/;

    public final native IDBIndex getIndex(String name) /*-{
	    return this.index(name);
	}-*/;

    public final native void deleteIndex(String name) /*-{
	    this.deleteIndex(name);
	}-*/;
}
