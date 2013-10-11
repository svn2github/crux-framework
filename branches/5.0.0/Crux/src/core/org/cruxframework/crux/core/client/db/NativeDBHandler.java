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

import org.cruxframework.crux.core.client.db.indexeddb.IDBFactory;
import org.cruxframework.crux.core.client.db.websql.polyfill.DBBridge;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.PartialSupport;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@PartialSupport
class NativeDBHandler
{
	protected static Logger logger = Logger.getLogger(NativeDBHandler.class.getName());
	private static boolean nativeDBInitialized = false; 
	private static boolean nativeDBInitializing = false;
	private static boolean preferWebSQL = false;//TODO debug

	public static interface Callback
	{
		void onSuccess();
		void onError(Throwable e);
	}
	
	public static boolean isInitialized()
	{
		return nativeDBInitialized;
	}
	
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
	    		if (!DBBridge.isWebSQLSupported() || (IDBFactory.isSupported() && !preferWebSQL))
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
     * 
     * @return
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
