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
package org.cruxframework.crux.core.client.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.db.DBMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.core.client.ScriptInjector.FromString;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.resources.client.TextResource;

public class CryptUtils 
{
	protected static Logger logger = Logger.getLogger(CryptUtils.class.getName());
	private static boolean md5Initialized = false; 
	private static boolean md5Initializing = false;
	
	public interface Resources extends ClientBundle
	{
		@Source("md5.js")
		ExternalTextResource md5();
	}
	
	public static interface Callback
	{
		void onSuccess(String result);
		void onError(Exception e);
	}
	
	public void initialize(String key, Callback callback) 
	{
		initializeMD5(callback);
	}
	
	public native String hexMD5(String key)/*-{
    	return hex_md5(key);
	}-*/;
	
	public native String hexSHA1(String key)/*-{
		return hex_sha1(key);
	}-*/;
	
	private static void initializeMD5(final Callback callback)
    {
	    Resources resources = GWT.create(Resources.class);

	    try
	    {
	    	ResourceCallback<TextResource> resourceCallback = new ResourceCallback<TextResource>()
	    	{
	    		@Override
	    		public void onSuccess(TextResource resource)
	    		{
	    			FromString injector = ScriptInjector.fromString(resource.getText());
	    			injector.setWindow(getWindow());
	    			injector.setRemoveTag(false);
	    			injector.inject();
	    			Scheduler.get().scheduleDeferred(new ScheduledCommand()
	    			{
	    				@Override
	    				public void execute()
	    				{
	    					md5Initialized = true;
	    					md5Initializing = false;
	    					if (LogConfiguration.loggingIsEnabled())
	    					{
	    						DBMessages messages = GWT.create(DBMessages.class);
	    						logger.log(Level.INFO, messages.databaseUsingWebSQL());
	    					}
	    					callback.onSuccess(null);
	    				}
	    			});
	    		}

	    		@Override
	    		public void onError(ResourceException e)
	    		{
	    			md5Initializing = false;
	    			callback.onError(e);
	    		}
	    	};
    		resources.md5().getText(resourceCallback);
	    }
	    catch (ResourceException e)
	    {
	    	md5Initializing = false;
	    	callback.onError(e);
	    }
    }
	
	private static native JavaScriptObject getWindow()/*-{
	    return $wnd;
	}-*/;
}
