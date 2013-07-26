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
package org.cruxframework.crux.core.clientoffline;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.screen.Screen;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Event;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ApplicationCacheHandler implements EntryPoint 
{
	private static Logger logger = Logger.getLogger(ApplicationCacheHandler.class.getName());

    public static final int UNCACHED = 0;
    public static final int IDLE = 1;
    public static final int CHECKING = 2;
    public static final int DOWNLOADING = 3;
    public static final int UPDATEREADY = 4;
    public static final int OBSOLETE = 5;

    private OfflineMessages messages = GWT.create(OfflineMessages.class);
    private OfflineConstants constants = GWT.create(OfflineConstants.class);
    private ApplicationCacheUIHandler uiHandler = GWT.create(ApplicationCacheUIHandler.class);

    private boolean updating = false;
	private boolean obsolete = false;


    /**
     * Initializes and starts the monitoring.
     */
    public void onModuleLoad() 
    {
        hookAllListeners(this);
        scheduleUpdateChecker();
        if (getStatus() == DOWNLOADING)
        {
        	uiHandler.showMessage(messages.downloadingResources());
        }

        // Sometimes android leaves the status indicator spinning and spinning
        // and spinning...
        pollForStatusOnAndroid();
    }

    /**
     * @return The status of the application cache.
     */
    public static native int getStatus()/*-{
        return window.applicationCache.status;
    }-*/;

    /**
     * Asks the application cache to update itself.
     */
    public static native void updateCache()/*-{
        window.applicationCache.update();
    }-*/;

    private void pollForStatusOnAndroid() 
    {
        if (Screen.isAndroid()) 
        {
            Scheduler.get().scheduleFixedPeriod(new Scheduler.RepeatingCommand() {
                        @Override
                        public boolean execute() 
                        {
                            if (updating) 
                            {
                                // The normal listeners are working correctly
                                return false;
                            }
                            switch (getStatus()) 
                            {
                            	case IDLE:
                            		uiHandler.hideMessage();
                            		return false;
                            	case UPDATEREADY:
                            		requestUpdate(false);
                            		return false;
                            	default:
                            		return true;
                            }
                        }
                    }, 500);
        }
    }

    /**
     * Check for updates to the application cache every 30 minutes
     */
    private void scheduleUpdateChecker() {
        Scheduler.get().scheduleFixedPeriod(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() 
            {
                if (obsolete)
                {
                	return false;
                }
            	if (!updating) 
                {
                    updateCache();
                }
                return true;
            }
        }, constants.updateCheckInterval());
    }

    /**
     * Called when a cached event is triggered
     * 
     * @param event The event.
     */
    protected void onCached(Event event) 
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.INFO, "Resources cached.");
		}
    	uiHandler.hideMessage();
    }

    /**
     * Called when a checking event is triggered
     * 
     * @param event The event.
     */
    protected void onChecking(Event event) 
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.INFO, messages.checkingResources());
		}
    }

    /**
     * Called when a downloading event is triggered
     * 
     * @param event The event.
     */
    protected void onDownloading(Event event) 
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.INFO, messages.downloadingResources());
		}
    	updating = true;
    	uiHandler.showMessage(messages.downloadingResources());
    }

    /**
     * Called when a noupdate event is triggered
     * 
     * @param event The event.
     */
    protected void onNoUpdate(Event event) 
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.INFO, "No updates found");
		}
    	uiHandler.hideMessage();
    }

    /**
     * Called when a update ready event is triggered
     * 
     * @param event The event.
     */
    protected void onUpdateReady(Event event) 
    {
    	uiHandler.hideMessage();
    	requestUpdate(false);
    	updating = false;
    }

    /**
     * Called when a progress event is triggered
     * 
     * @param event The event.
     */
    protected void onProgress(ProgressEvent event)
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.INFO, messages.progressStatus(event.getLoaded(), event.getTotal()));
		}
    	uiHandler.showMessage(messages.progressStatus(event.getLoaded(), event.getTotal()));
    }
    
	/**
     * Called when an error event is triggered.
     * 
     * @param event The error event.
     */
    protected void onError(Event event) 
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.INFO, messages.applicationCacheError());
		}
    }

	/**
     * Called when an error event is triggered.
     * 
     * @param event The error event.
     */
    protected void onObsolete(Event event) 
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.INFO, messages.applicationCacheObsolete());
		}
		this.obsolete = true; 
    	uiHandler.showMessage(messages.applicationCacheObsolete());
    }

    /**
     * Called when a new version of the application cache
     * has been detected. Asks the user if we should
     * update now unless forced.
     * 
     * @param force true to force reloading the site without asking the user.
     */
    protected void requestUpdate(boolean force) 
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.INFO, "New updates available. Requesting permission to update.");
		}
        if (force) 
        {
            Screen.reload();
        }
        else
        {
        	uiHandler.confirmReloadPage();
        }
    }

    /**
     * Hooks all listeners to the specified instance.
     * 
     * @param instance
     *            the instance to hook the listeners to.
     */
    protected final native void hookAllListeners(ApplicationCacheHandler instance)/*-{
        window.applicationCache.addEventListener('cached',
            function(event) {
                instance.@org.cruxframework.crux.core.clientoffline.ApplicationCacheHandler::onCached(Lcom/google/gwt/user/client/Event;)(event);
            }, false);
        window.applicationCache.addEventListener('checking',
            function(event) {
                instance.@org.cruxframework.crux.core.clientoffline.ApplicationCacheHandler::onChecking(Lcom/google/gwt/user/client/Event;)(event);
            }, false);
        window.applicationCache.addEventListener('downloading',
            function(event) {
                instance.@org.cruxframework.crux.core.clientoffline.ApplicationCacheHandler::onDownloading(Lcom/google/gwt/user/client/Event;)(event);
            }, false);
        window.applicationCache.addEventListener('noupdate',
            function(event) {
                instance.@org.cruxframework.crux.core.clientoffline.ApplicationCacheHandler::onNoUpdate(Lcom/google/gwt/user/client/Event;)(event);
            }, false);
        window.applicationCache.addEventListener('updateready',
            function(event) {
                instance.@org.cruxframework.crux.core.clientoffline.ApplicationCacheHandler::onUpdateReady(Lcom/google/gwt/user/client/Event;)(event);
            }, false);
        window.applicationCache.addEventListener('progress',
            function(event) {
                instance.@org.cruxframework.crux.core.clientoffline.ApplicationCacheHandler::onProgress(Lorg/cruxframework/crux/core/clientoffline/ProgressEvent;)(event);
            }, false);
        window.applicationCache.addEventListener('obsolete',
            function(event) {
                instance.@org.cruxframework.crux.core.clientoffline.ApplicationCacheHandler::onObsolete(Lcom/google/gwt/user/client/Event;)(event);
            }, false);
        window.applicationCache.addEventListener('error',
            function(event) {
                instance.@org.cruxframework.crux.core.clientoffline.ApplicationCacheHandler::onError(Lcom/google/gwt/user/client/Event;)(event);
            }, false);
    }-*/;
}