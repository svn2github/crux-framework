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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DBBridge
{
	protected static Logger logger = Logger.getLogger(DBBridge.class.getName());

	public static void installSQLBridge(final Callback callback)
	{
		GWT.runAsync(new RunAsyncCallback()
		{
			@Override
			public void onSuccess()
			{
				DBTransaction.registerStaticFunctions();
				DBKeyRange.registerStaticFunctions();
				DBFactory.registerStaticFunctions();
				DBBridge.enableBridge();
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
	 * 
	 * @return
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

	public static interface Callback
	{
		void onSuccess();
		void onError(Throwable e);
	}
}