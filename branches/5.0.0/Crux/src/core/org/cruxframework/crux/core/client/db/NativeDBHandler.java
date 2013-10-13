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
package org.cruxframework.crux.core.client.db;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.db.indexeddb.IDBFactory;
import org.cruxframework.crux.core.client.db.websql.polyfill.DBBridge;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.PartialSupport;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * Initialize the native database APIs used to support Crux Database. On browsers that support IndexedDB natively, 
 * use that API as native DB provider. For Browsers that support only SQL Lite native database, create a bridge to enable
 * SQL usage through indexed DB API. If none of those native databases are supported, Crux Database will not be supported
 * on that browser.
 * 
 * @author Thiago da Rosa de Bustamante
 */
@PartialSupport
class NativeDBHandler
{
	private static Logger logger = Logger.getLogger(NativeDBHandler.class.getName());
	private static boolean nativeDBInitialized = false; 
	private static boolean nativeDBInitializing = false;

	/**
	 * Callback used to detect when native database API is completely initialized.
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static interface Callback
	{
		/**
		 * Called when native database API is successfully initialized.
		 */
		void onSuccess();
		/**
		 * Called when an error occurred in native database API initialization process.
		 * @param e
		 */
		void onError(Throwable e);
	}
	
	/**
	 * Check if native database API is initialized.
	 * @return
	 */
	public static boolean isInitialized()
	{
		return nativeDBInitialized;
	}
	
	/**
	 * Initialize native database API to support Crux Database operations.
	 * @param callback
	 */
	public static void initialize(final Callback callback)
	{
		if (isInitialized())
		{
			callback.onSuccess();
		}
		else
		{
			if (nativeDBInitializing)
	    	{
	    		waitForNativeDBInitialization(callback);
	    	}
	    	else
	    	{
	    		if (!DBBridge.isWebSQLSupported() || (IDBFactory.isSupported() && !Crux.getConfig().preferWebSQLForNativeDB()))
	    		{
					nativeDBInitialized = true;
					callback.onSuccess();
	    		}
	    		else
	    		{
		    		initializeWebSQL(callback);
		    	}
	    	}
		}
	}

    /**
     * Check if browser support one of Crux supoprted native database APIs.
     * @return true if supported
     */
    public static boolean isSupported()
    {
    	return IDBFactory.isSupported() || DBBridge.isWebSQLSupported();
    }

	private static void initializeWebSQL(final Callback callback)
    {
	    nativeDBInitializing = true;

	    try
	    {
	    	DBBridge.installSQLBridge(new DBBridge.Callback()
			{
				@Override
				public void onSuccess()
				{
					nativeDBInitialized = true;
					nativeDBInitializing = false;
					if (LogConfiguration.loggingIsEnabled())
					{
						DBMessages messages = GWT.create(DBMessages.class);
						logger.log(Level.INFO, messages.databaseUsingWebSQL());
					}
					callback.onSuccess();
				}
				
				@Override
				public void onError(Throwable e)
				{
	    			nativeDBInitializing = false;
	    			callback.onError(e);
				}
			});
	    }
	    catch (Exception e)
	    {
	    	nativeDBInitializing = false;
	    	callback.onError(e);
	    }
    }
	
	private static void waitForNativeDBInitialization(final Callback callback)
    {
	    Scheduler.get().scheduleFixedDelay(new RepeatingCommand()
	    {
	    	@Override
	    	public boolean execute()
	    	{
	    		if (nativeDBInitializing)
	    		{
	    			return true;
	    		}
	    		if (nativeDBInitialized)
	    		{
	    			callback.onSuccess();
	    		}
	    		else
	    		{
	    			callback.onError(new RuntimeException("Error initilizing native database. Unkonw reason"));
	    		}
	    		
	    		return false;
	    	}
	    }, 10);
    }
}
