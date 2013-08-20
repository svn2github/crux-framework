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
import com.google.gwt.dom.client.PartialSupport;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@PartialSupport
public class IDBFactory extends JavaScriptObject 
{
	private static boolean initialized = false;

	private static native void init() /*-{
	    $wnd.IDBKeyRange = $wnd.IDBKeyRange || $wnd.webkitIDBKeyRange;
	    $wnd.indexedDB = $wnd.indexedDB || $wnd.mozIndexedDB || $wnd.webkitIndexedDB;
	}-*/;
	
    protected IDBFactory() {}

    public final static IDBFactory get() 
    {
    	if (!initialized)
    	{
    		init();
    		initialized = true;
    	}
    	return create();
    }
    
    private static native IDBFactory create() /*-{
		return $wnd.indexedDB;
    }-*/;

    public final native IDBOpenDBRequest open(String name) /*-{
		return this.open(name);
    }-*/;
    
    public final native IDBOpenDBRequest open(String name, int version) /*-{
		return this.open(name, version);
	}-*/;

    public final native IDBDeleteDBRequest deleteDatabase(String name) /*-{
		return this.deleteDatabase(name);
    }-*/;
    
    public final native int cmp(JavaScriptObject o1, JavaScriptObject o2) /*-{
		return this.cmp(o1,o2);
    }-*/;

    public final native int cmp(String o1, String o2) /*-{
		return this.cmp(o1,o2);
	}-*/;

    public final native int cmp(int o1, int o2) /*-{
		return this.cmp(o1,o2);
	}-*/;
    
    public final native int cmp(double o1, double o2) /*-{
		return this.cmp(o1,o2);
	}-*/;

    public final int cmp(Date o1, Date o2)
    {
		return this.cmp(o1.getTime(),o2.getTime());
	};

	public static native boolean isSupported()/*-{
	    var IDBKeyRange = $wnd.IDBKeyRange || $wnd.webkitIDBKeyRange;
	    var indexedDB = $wnd.indexedDB || $wnd.mozIndexedDB || $wnd.webkitIndexedDB;
		if (IDBKeyRange && indexedDB) {
			return true;
		}
		return false;
	}-*/;
}
