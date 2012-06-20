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
package org.cruxframework.crux.core.declarativeui.view;
 
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Views 
{
	private static final Lock lock = new ReentrantLock();
	private static final Log logger = LogFactory.getLog(Views.class);
	private static Map<String, URL> views = null;
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public static URL getView(String id)
	{
		if (views == null)
		{
			initialize();
		}
		return views.get(id);
	}
	
	/**
	 * 
	 */
	public static void initialize()
	{
		if (views != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (views != null)
			{
				return;
			}
			
			initializeViews();
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * 
	 */
	public static void restart()
	{
		views = null;
		initialize();
	}
	
	/**
	 * 
	 */
	protected static void initializeViews()
	{
		views = new HashMap<String, URL>();
		logger.info("Searching for view files.");
		ViewsScanner.getInstance().scanArchives();
	}

	/**
	 * 
	 * @param viewId
	 * @param view
	 */
	static void registerView(String viewId, URL view)
	{
		if (views.containsKey(viewId))
		{
			throw new ViewException("Duplicated view found. View: ["+viewId+"].");
		}
		
		views.put(viewId, view);
	}
}
