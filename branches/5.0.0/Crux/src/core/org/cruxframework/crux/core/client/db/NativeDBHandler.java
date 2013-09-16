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
import org.cruxframework.crux.core.client.db.websql.WebSQLResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.core.client.ScriptInjector.FromString;
import com.google.gwt.dom.client.PartialSupport;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.resources.client.TextResource;

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
	private static boolean debugMode = false;

	public static interface Callback
	{
		void onSuccess();
		void onError(Exception e);
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
	    		if (IDBFactory.isSupported() && !debugMode)
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
    	return IDBFactory.isSupported() || isWebSQLSupported();
    }
    
    /**
	 * 
	 * @return
	 */
	public static native boolean isWebSQLSupported()/*-{
		var sqlsupport = !!$wnd.openDatabase;
		return sqlsupport;
	}-*/;

	private static void initializeWebSQL(final Callback callback)
    {
	    nativeDBInitializing = true;
	    WebSQLResources resources = GWT.create(WebSQLResources.class);

	    try
	    {
	    	ResourceCallback<TextResource> resourceCallback = new ResourceCallback<TextResource>()
	    	{
	    		@Override
	    		public void onSuccess(TextResource resource)
	    		{
	    			FromString injector = ScriptInjector.fromString(resource.getText());
	    			injector.setWindow(getWindow());
	    			injector.setRemoveTag(!debugMode);
	    			injector.inject();
	    			Scheduler.get().scheduleDeferred(new ScheduledCommand()
	    			{
	    				@Override
	    				public void execute()
	    				{
	    					nativeDBInitialized = true;
	    					nativeDBInitializing = false;
	    	    			if (debugMode)
	    	    			{
	    	    				forceWebSQLUsage();
	    	    			}
	    					if (LogConfiguration.loggingIsEnabled())
	    					{
	    						DBMessages messages = GWT.create(DBMessages.class);
	    						logger.log(Level.INFO, messages.databaseUsingWebSQL());
	    					}
	    					callback.onSuccess();
	    				}
	    			});
	    		}

	    		@Override
	    		public void onError(ResourceException e)
	    		{
	    			nativeDBInitializing = false;
	    			callback.onError(e);
	    		}
	    	};
	    	if (debugMode)
	    	{
	    		resources.indexeddbshimDebug().getText(resourceCallback);
	    	}
	    	else
	    	{
	    		resources.indexeddbshim().getText(resourceCallback);
	    	}
	    }
	    catch (ResourceException e)
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
	
	private static native JavaScriptObject getWindow()/*-{
	    return $wnd;
	}-*/;

    private static native void forceWebSQLUsage()/*-{
    	$wnd.shimIndexedDB.__useShim();
    }-*/;
    
    static native boolean usesWebSQL()/*-{
		if ($wnd.shimIndexedDB)
		{
			return true;
		}
		return false;
	}-*/;
    
}
