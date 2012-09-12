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
package org.cruxframework.crux.core.rebind;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.cruxframework.crux.core.declarativeui.view.Views;
import org.cruxframework.crux.core.rebind.screen.Screen;
import org.cruxframework.crux.core.rebind.screen.ScreenConfigException;
import org.cruxframework.crux.core.rebind.screen.ScreenFactory;
import org.cruxframework.crux.core.rebind.screen.ScreenResourceResolverInitializer;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.rebind.screen.ViewFactory;
import org.cruxframework.crux.core.server.CruxBridge;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.SelectionProperty;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.JRealClassType;
import com.google.gwt.dev.javac.rebind.CachedRebindResult;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * 
 * Base class for all generators that create a smart stub for a base interface
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractInterfaceWrapperProxyCreator extends AbstractProxyCreator
{
	private static final String PROXY_SUFFIX = "_Impl";
	protected JClassType baseIntf;
	private boolean cacheable;
	private boolean cacheableVersionFound;

	public AbstractInterfaceWrapperProxyCreator(TreeLogger logger, GeneratorContextExt context, JClassType baseIntf, boolean cacheable)
    {
	    super(logger, context);
		this.baseIntf = baseIntf;
		this.cacheable = cacheable;
    }

	@Override
	public String create() throws CruxGeneratorException
	{
	    if (this.cacheable)
	    {
	    	if (findCacheableImplementationAndMarkForReuseIfAvailable())
	    	{
	    		this.cacheableVersionFound = true;
	    		return getProxyQualifiedName();
	    	}
	    }
		
		return super.create();
	}
	
	/**
	 * @return the full qualified name of the proxy object.
	 */
	@Override
	public String getProxyQualifiedName()
	{
		return baseIntf.getPackage().getName() + "." + getProxySimpleName();
	}
	
	/**
	 * @return the simple name of the proxy object.
	 */
	@Override
	public String getProxySimpleName()
	{
		JClassType enclosingType = baseIntf.getEnclosingType();
		String enclosingTypeName = (enclosingType==null?"":enclosingType.getSimpleSourceName()+"_");
		return enclosingTypeName+baseIntf.getSimpleSourceName() + PROXY_SUFFIX;
	}

	/**
	 * 
	 * @return
	 */
	protected String getUserAgent()
	{
		try
		{
			SelectionProperty userAgent = context.getPropertyOracle().getSelectionProperty(logger, "user.agent");
			return userAgent==null?null:userAgent.getCurrentValue();
		}
		catch (BadPropertyValueException e)
		{
			logger.log(TreeLogger.ERROR, "Can not read user.agent property.",e);
			throw new CruxGeneratorException();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getDeviceFeatures()
	{
		try
		{
			SelectionProperty device = context.getPropertyOracle().getSelectionProperty(logger, "device.features");
			return device==null?null:device.getCurrentValue();
		}
		catch (BadPropertyValueException e)
		{
			throw new CruxGeneratorException("Can not read device.features property.", e);
		}
	}
	
	/**
	 * @return
	 * @throws ScreenConfigException
	 */
	protected Screen getCurrentScreen()  
	{
		try 
		{
			Screen requestedScreen = getRequestedScreen();

			for(Screen screen: getScreens())
			{
				if (screen.getModule().equals(requestedScreen.getModule()) && 
						screen.getRelativeId().equals(requestedScreen.getRelativeId()))
				{
					return screen;
				}
			}
		}
		catch (ScreenConfigException e) 
		{
			throw new CruxGeneratorException(e.getMessage(), e);
		}
		throw new CruxGeneratorException("Error Generating registered element. Can not retrieve current screen.");
	}	

	protected String getModule()
	{
		try
		{
			Screen requestedScreen = getRequestedScreen();

			if(requestedScreen != null)
			{
				return requestedScreen.getModule();
			}
			return null;
		}
		catch (ScreenConfigException e)
		{
			logger.log(TreeLogger.ERROR, "Error Generating registered element. Can not retrieve current module.",e);
			throw new CruxGeneratorException();
        }
	}
	
	/**
	 * 
	 * @param logger
	 * @return
	 * @throws CruxGeneratorException
	 */
	protected List<Screen> getScreens() throws CruxGeneratorException
	{
		try
        {
	        List<Screen> screens = new ArrayList<Screen>();

	        Screen requestedScreen = getRequestedScreen();
	        
	        if(requestedScreen != null)
	        {
	        	Set<String> screenIDs = ScreenResourceResolverInitializer.getScreenResourceResolver().getAllScreenIDs(requestedScreen.getModule());
	        	
	        	if (screenIDs == null)
	        	{
	        		throw new ScreenConfigException("Can not find the module ["+requestedScreen.getModule()+"].");
	        	}
	        	for (String screenID : screenIDs)
	        	{
	        		Screen screen = ScreenFactory.getInstance().getScreen(screenID, getDeviceFeatures());
	        		if(screen != null)
	        		{
	        			screens.add(screen);
	        		}
	        	}
	        }
	        
	        return screens;
        }
        catch (ScreenConfigException e)
        {
			logger.log(TreeLogger.ERROR, "Error Generating registered element. Can not retrieve module's list of screens.",e);
			throw new CruxGeneratorException();
        }
	}

	/**
	 * 
	 * @return
	 */
	protected List<View> getViews()
	{
		List<View> views = new ArrayList<View>();
		List<Screen> screens = getScreens();
		HashSet<String> added = new HashSet<String>();
		for (Screen screen : screens)
        {
			findViews(screen, views, added);
        }
		return views;
	}
	
	/**
	 * 
	 * @return
	 */
	protected List<View> getViewsForCurrentScreen()
	{
		List<View> views = new ArrayList<View>();
		findViews(getCurrentScreen(), views, new HashSet<String>());
		return views;
	}
	
	/**
	 * @return a sourceWriter for the proxy class
	 */
	@Override
	protected SourcePrinter getSourcePrinter()
	{
		JPackage pkg = baseIntf.getPackage();
		String packageName = pkg == null ? "" : pkg.getName();
		PrintWriter printWriter = context.tryCreate(logger, packageName, getProxySimpleName());

		if (printWriter == null)
		{
			return null;
		}

		ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, getProxySimpleName());

		String[] imports = getImports();
		for (String imp : imports)
		{
			composerFactory.addImport(imp);
		}

		composerFactory.addImplementedInterface(baseIntf.getQualifiedSourceName());

		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);
	}
	
	/**
	 * @return
	 */
	protected boolean findCacheableImplementationAndMarkForReuseIfAvailable()
	{
		CachedRebindResult lastResult = context.getCachedGeneratorResult();
		if (lastResult == null || !context.isGeneratorResultCachingEnabled())
		{
			return false;
		}

		String proxyName = getProxyQualifiedName();

		// check that it is available for reuse
		if (!lastResult.isTypeCached(proxyName))
		{
			return false;
		}

		try
		{
			long lastModified = 0L;
			if (baseIntf instanceof JRealClassType)
			{
				lastModified = ((JRealClassType)baseIntf).getLastModifiedTime();
			}

			if (lastModified != 0L && lastModified < lastResult.getTimeGenerated())
			{
				return context.reuseTypeFromCacheIfAvailable(proxyName);
			}
		}
		catch (RuntimeException ex)
		{
			// could get an exception checking modified time
			return false;
		}

		return false;
	}
	
	@Override
	protected boolean isCacheable()
	{
		return cacheable;
	}
	
	protected boolean cacheableVersionFound()
	{
		return cacheableVersionFound;
	}
	
	/**
	 * 
	 * @param screen
	 * @param views
	 * @param added
	 */
	private void findViews(Screen screen, List<View> views, Set<String> added) 
	{
		View rootView = screen.getRootView();
		if (!added.contains(rootView.getId()))
		{
			added.add(rootView.getId());
			views.add(rootView);
			findViews(rootView, views, added);
		}
	}
	
	/**
	 * 
	 * @param view
	 * @param views
	 * @param added
	 */
	private void findViews(View view, List<View> views, Set<String> added) 
	{
		try
		{
			Iterator<String> iterator = view.iterateViews();
			while (iterator.hasNext())
			{
				String viewLocator = iterator.next();
				if (!added.contains(viewLocator))
				{
					added.add(viewLocator);
					
					List<String> viewList = Views.getViews(viewLocator);
					for (String viewName : viewList)
                    {
						View innerView = ViewFactory.getInstance().getView(viewName, getDeviceFeatures());
						views.add(innerView);
						findViews(innerView, views, added);
                    }
				}
			}
		}
		catch (ScreenConfigException e)
		{
			logger.log(TreeLogger.ERROR, "Error Generating registered element. Can not retrieve screen's list of views.",e);
			throw new CruxGeneratorException();
		}
	}

	/**
	 * 
	 * @param logger
	 * @return
	 * @throws CruxGeneratorException
	 * @throws ScreenConfigException
	 */
	private Screen getRequestedScreen() throws CruxGeneratorException, ScreenConfigException
	{
		String screenID; 
		try
		{
			screenID = CruxBridge.getInstance().getLastPageRequested();
		}
		catch (Throwable e) 
		{
			throw new CruxGeneratorException("Error retrieving screen Identifier.");
		}
		
        Screen screen = ScreenFactory.getInstance().getScreen(screenID, getDeviceFeatures());
        return screen;
	}
	
	/**
	 * @return the list of imports required by proxy
	 */
	protected abstract String[] getImports();
}
