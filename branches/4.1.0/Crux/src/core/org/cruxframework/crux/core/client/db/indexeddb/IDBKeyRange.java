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

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class IDBKeyRange extends JavaScriptObject
{
	protected IDBKeyRange() {}

	public final native JavaScriptObject getLowerObject() /*-{
		return this.lower;
	}-*/;

	public final native String getLowerString() /*-{
		return this.lower;
	}-*/;

	public final native int getLowerInt() /*-{
		return this.lower;
	}-*/;

	public final native JavaScriptObject getUpperObject() /*-{
		return this.upper;
	}-*/;

	public final native String getUpperString() /*-{
		return this.upper;
	}-*/;

	public final native int getUpperInt() /*-{
		return this.upper;
	}-*/;

	public final native boolean isLowerOpen() /*-{
		return this.lowerOpen;
	}-*/;

	public final native boolean isUpperOpen() /*-{
		return this.upperOpen;
	}-*/;

	public final native static IDBKeyRange only(JavaScriptObject key) /*-{
		return $wnd.IDBKeyRange.only(key);
	}-*/;

	public final native static IDBKeyRange only(int key) /*-{
		return $wnd.IDBKeyRange.only(key);
	}-*/;

	public final native static IDBKeyRange only(String key) /*-{
		return $wnd.IDBKeyRange.only(key);
	}-*/;

	public final native static IDBKeyRange lowerBound(JavaScriptObject key, boolean open) /*-{
		return $wnd.IDBKeyRange.lowerBound(key,open);
	}-*/;

	public final native static IDBKeyRange lowerBound(int key, boolean open) /*-{
		return $wnd.IDBKeyRange.lowerBound(key,open);
	}-*/;

	public final native static IDBKeyRange lowerBound(String key, boolean open) /*-{
		return $wnd.IDBKeyRange.lowerBound(key,open);
	}-*/;

	public final native static IDBKeyRange lowerBound(JavaScriptObject key) /*-{
		return $wnd.IDBKeyRange.lowerBound(key);
	}-*/;
	
	public final native static IDBKeyRange lowerBound(int key) /*-{
		return $wnd.IDBKeyRange.lowerBound(key);
	}-*/;
	
	public final native static IDBKeyRange lowerBound(String key) /*-{
		return $wnd.IDBKeyRange.lowerBound(key);
	}-*/;

	public final native static IDBKeyRange upperBound(JavaScriptObject key, boolean open)/*-{
		return $wnd.IDBKeyRange.upperBound(key,open);
	}-*/;

	public final native static IDBKeyRange upperBound(int key, boolean open)/*-{
		return $wnd.IDBKeyRange.upperBound(key,open);
	}-*/;

	public final native static IDBKeyRange upperBound(String key, boolean open)/*-{
		return $wnd.IDBKeyRange.upperBound(key,open);
	}-*/;

	public final native static IDBKeyRange upperBound(JavaScriptObject key)/*-{
		return $wnd.IDBKeyRange.upperBound(key);
	}-*/;
	
	public final native static IDBKeyRange upperBound(int key)/*-{
		return $wnd.IDBKeyRange.upperBound(key);
	}-*/;
	
	public final native static IDBKeyRange upperBound(String key)/*-{
		return $wnd.IDBKeyRange.upperBound(key);
	}-*/;

	public final native static IDBKeyRange bound(JavaScriptObject startKey, JavaScriptObject endKey, boolean startOpen, boolean endOpen) /*-{
		return $wnd.IDBKeyRange.bound(startKey,endKey,startOpen,endOpen);
	}-*/;

	public final native static IDBKeyRange bound(int startKey, int endKey, boolean startOpen, boolean endOpen) /*-{
		return $wnd.IDBKeyRange.bound(startKey,endKey,startOpen,endOpen);
	}-*/;

	public final native static IDBKeyRange bound(String startKey, String endKey, boolean startOpen, boolean endOpen) /*-{
		return $wnd.IDBKeyRange.bound(startKey,endKey,startOpen,endOpen);
	}-*/;

	public final native static IDBKeyRange bound(JavaScriptObject startKey, JavaScriptObject endKey) /*-{
		return $wnd.IDBKeyRange.bound(startKey,endKey);
	}-*/;
	
	public final native static IDBKeyRange bound(int startKey, int endKey) /*-{
		return $wnd.IDBKeyRange.bound(startKey,endKey);
	}-*/;
	
	public final native static IDBKeyRange bound(String startKey, String endKey) /*-{
		return $wnd.IDBKeyRange.bound(startKey,endKey);
	}-*/;
}