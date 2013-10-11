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

import org.cruxframework.crux.core.client.db.indexeddb.IDBFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * Makes a bridge between IndexedDB api and SQL lite implementation for browsers that does not
 * support IndexedDB API, like androids and iOS devices.
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class DBBridge
{
	private static Logger logger = Logger.getLogger(DBBridge.class.getName());

	/**
	 * Load and install the bridge code 
	 * @param callback Called when the bridge is loaded and installed in current page.
	 */
	public static void installSQLBridge(final Callback callback)
	{
		GWT.runAsync(new RunAsyncCallback()
		{
			@Override
			public void onSuccess()
			{
				DBBridge.registerStaticFunctions();
				DBTransaction.registerStaticFunctions();
				DBKeyRange.registerStaticFunctions();
				DBFactory.registerStaticFunctions();
				DBBridge.enableBridge();
				IDBFactory.init(getIDBContext());
				callback.onSuccess();
			}
			
			@Override
			public void onFailure(Throwable reason)
			{
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.SEVERE, "Error loading Database Web SQL engine. Error message ["+reason.getMessage()+"]");
				}
				callback.onError(reason);
			}
		});
	}
    
    /**
	 * Check if current browser support SQL lite database. If not supported, bridge can not be used.
	 * @return true if supported
	 */
	public static native boolean isWebSQLSupported()/*-{
		var sqlsupport = !!$wnd.openDatabase;
		return sqlsupport;
	}-*/;
	
	static native void enableBridge()/*-{
	    $wnd.indexedDB = $wnd.__db_bridge__.indexedDB;
	    $wnd.IDBKeyRange = $wnd.__db_bridge__.IDBKeyRange;
	    $wnd.IDBTransaction = $wnd.__db_bridge__.IDBTransaction;
    }-*/;

	static native JavaScriptObject getIDBContext()/*-{
		return $wnd.__db_bridge__.indexedDB;
	}-*/;
	
	private native static void registerStaticFunctions()/*-{
		$wnd.__db_bridge__ = $wnd.__db_bridge__ || {};
		$wnd.__db_bridge__.convertKey = function(key)
		{
			if (!key) return null;
			var keys = (Object.prototype.toString.call(key) === '[object Array]')?key:[key];
			return keys; 
		};
	}-*/;
	
	/**
	 * Callback used to detect when bridge is completely loaded.
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static interface Callback
	{
		/**
		 * Called when bridge is loaded successfully 
		 */
		void onSuccess();
		/**
		 * Called when an error occurred in bridge loading process
		 * @param e the error
		 */
		void onError(Throwable e);
	}
}