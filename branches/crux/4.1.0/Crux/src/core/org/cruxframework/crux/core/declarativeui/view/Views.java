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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.classpath.URLResourceHandler;
import org.cruxframework.crux.classpath.URLResourceHandlersRegistry;
import org.cruxframework.crux.core.rebind.module.Module;
import org.cruxframework.crux.core.rebind.module.Modules;
import org.cruxframework.crux.core.server.classpath.ClassPathResolverInitializer;
import org.cruxframework.crux.core.utils.FilePatternHandler;
import org.cruxframework.crux.core.utils.URLUtils;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Views 
{
	private static final Lock lock = new ReentrantLock();
	private static final Log logger = LogFactory.getLog(Views.class);
	private static Map<String, List<URL>> views = null;
	
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
		List<URL> urls = views.get(id);
		return (urls != null && urls.size() > 0)?urls.get(0):null;
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
	 * @param viewsLocator
	 * @return
	 */
	public static List<String> getViews(String viewsLocator)
	{
		if (views == null)
		{
			initialize();
		}
		List<String> result = new ArrayList<String>();
		if (viewsLocator.equals("*"))
		{
			result.addAll(views.keySet());
		}
		else if (isViewName(viewsLocator))
		{
			result.add(viewsLocator);
		}
		else if (isModuleLocator(viewsLocator))
		{
			extractModuleViews(viewsLocator, result);
		}
		else if (isFolderLocator(viewsLocator))
		{
			URL[] webBaseDirs = ClassPathResolverInitializer.getClassPathResolver().findWebBaseDirs();
			FilePatternHandler handler = new FilePatternHandler(viewsLocator, null);
			for (URL url : webBaseDirs)
            {
				extractFolderViews(result, url, handler);
            }
		}
			
		return result;
	}

	/**
	 * 
	 * @param viewsLocator
	 * @return
	 */
	public static boolean isViewName(String viewsLocator)
	{
		return (viewsLocator != null && viewsLocator.matches("[\\w\\.]*"));
	}
	
	/**
	 * 
	 * @param viewsLocator
	 * @return
	 */
	public static boolean isModuleLocator(String viewsLocator)
	{
		return (viewsLocator != null && viewsLocator.startsWith("/") && viewsLocator.lastIndexOf("/") > 1);
	}

	/**
	 * 
	 * @param viewsLocator
	 * @return
	 */
	public static boolean isFolderLocator(String viewsLocator)
	{
		return (viewsLocator != null && viewsLocator.indexOf('/') > 0);
	}
	
	private static void extractModuleViews(String viewsLocator, List<String> result)
    {
	    viewsLocator = viewsLocator.substring(1);
	    int index = viewsLocator.indexOf("/");
	    String moduleId = viewsLocator.substring(0, index);
	    Module module = Modules.getInstance().getModule(moduleId);
	    if (module == null)
	    {
	    	throw new ViewException("Invalid module ["+moduleId+"] referenced by the view locator expression ["+viewsLocator+"]");
	    }
	    if (viewsLocator.length() > index +1)
	    {
	    	viewsLocator = viewsLocator.substring(index+1);
	    	if (isViewName(viewsLocator))
	    	{
	    		List<URL> urls = views.get(viewsLocator);
	    		if (urls != null)
	    		{
	    			for(URL url: urls)
	    			{
	    				if (Modules.getInstance().isResourceOnModulePath(url, moduleId))
	    				{
	    					result.add(viewsLocator);
	    				}
	    			}
	    		}
	    	}
	    	else
	    	{
	    		URL rootURL = Modules.getInstance().getModuleRootURL(moduleId);
	    		URLResourceHandler urlResourceHandler = URLResourceHandlersRegistry.getURLResourceHandler(rootURL.getProtocol());
	    		FilePatternHandler handler = new FilePatternHandler(viewsLocator, null);
	    		for (String path : module.getPublicPaths())
	            {
	    			URL modulePublicPath = urlResourceHandler.getChildResource(rootURL, path);
	    			extractFolderViews(result, modulePublicPath, handler);
	            }
	    	}
	    }
    }
	
	private static void extractFolderViews(List<String> result, URL baseFolder, FilePatternHandler handler)
	{
	//TODO modificar o scanner de templates para identificar duplicatas....assim como o de views	
		for (String viewId : views.keySet())
        {
			List<URL> urls = views.get(viewId);
			if (urls != null)
			{
				for (URL view : urls)
                {
					if (view.toString().startsWith(baseFolder.toString()))
					{
						String relativePath = view.toString().substring(baseFolder.toString().length());
						if (handler.isValidEntry(relativePath))
						{
							result.add(viewId);
							break;
						}
					}
                }
			}
		}
	}

	/**
	 * 
	 */
	protected static void initializeViews()
	{
		views = new HashMap<String, List<URL>>();
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
			List<URL> urls = views.get(viewId);
			if (urls.size() > 0)
			{
				if (!URLUtils.isIdenticResource(urls.get(0), view, viewId+".view.xml"))
				{
					throw new ViewException("Duplicated view identifier. View ["+viewId+"] is already registered.");
				}
			}
			urls.add(view);
		}
		else
		{
			List<URL> urls = new ArrayList<URL>();
			urls.add(view);
			views.put(viewId, urls);
		}
	}
}
