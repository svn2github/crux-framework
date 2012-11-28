/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.cruxsite.client.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.ioc.Inject;
import org.cruxframework.crux.core.client.screen.views.BindView;
import org.cruxframework.crux.core.client.screen.views.ViewWrapper;
import org.cruxframework.cruxsite.client.SiteConstants;
import org.cruxframework.cruxsite.client.feed.Error;
import org.cruxframework.cruxsite.client.feed.Feed;
import org.cruxframework.cruxsite.client.feed.FeedApi;
import org.cruxframework.cruxsite.client.feed.FeedCallback;
import org.cruxframework.cruxsite.client.feed.Loader;
import org.cruxframework.cruxsite.client.widget.RssPanel;

import com.google.gwt.logging.client.LogConfiguration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Controller("rssController")
public class RssController
{
	private static Logger logger = Logger.getLogger(RssController.class.getName());

	@Inject
	private SiteConstants constants;
	
	@Inject
	private HomeView view;
	
	@Expose
	public void onLoad()
	{
		Loader.init(constants.googleApiKey(), new Loader.LoaderCallback() 
		{
			public void onError(Throwable t) 
			{
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.SEVERE, "Error loading Google Feed API...");
				}
			}
			
			public void onLoad() 
			{
				loadBlogFeeds();
				loadProjectFeeds();
			}
		});
	}
	
	/**
	 * 
	 */
	private void loadBlogFeeds()
	{
		FeedApi feedApi = FeedApi.create(constants.blogFeedUrl());
		feedApi.includeHistoricalEntries();
	    feedApi.setNumEntries(constants.numFeedEntries());
	    
	    feedApi.load(new FeedCallback()
		{
			@Override
			public void onLoad(Feed feed)
			{
				view.blogFeeds().setFeed(feed);
			}
			
			@Override
			public void onError(Error error)
			{
				logger.log(Level.SEVERE, "Error loading Crux Blog Feed API...");
			}
		});
	}
	
	private void loadProjectFeeds()
	{
		FeedApi feedApi = FeedApi.create(constants.projectFeedUrl());
		feedApi.includeHistoricalEntries();
	    feedApi.setNumEntries(constants.numFeedEntries());
	    
	    feedApi.load(new FeedCallback()
		{
			@Override
			public void onLoad(Feed feed)
			{
				view.projectFeeds().setFeed(feed);
			}
			
			@Override
			public void onError(Error error)
			{
				logger.log(Level.SEVERE, "Error loading Crux Blog Feed API...");
			}
		});
	}
	
	public void setConstants(SiteConstants constants)
    {
    	this.constants = constants;
    }

	public void setView(HomeView view)
    {
    	this.view = view;
    }

	@BindView("home")
	public static interface HomeView extends ViewWrapper
	{
		RssPanel blogFeeds();
		RssPanel projectFeeds();
	}
}
