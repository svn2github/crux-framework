/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.rebind;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.sysmap.crux.core.rebind.screen.Screen;
import br.com.sysmap.crux.core.rebind.screen.ScreenConfigException;
import br.com.sysmap.crux.core.rebind.screen.ScreenFactory;
import br.com.sysmap.crux.core.rebind.screen.ScreenResourceResolverInitializer;
import br.com.sysmap.crux.core.server.CruxBridge;

import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.dev.javac.rebind.CachedRebindResult;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

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
	 * @return the list of imports required by proxy
	 */
	protected abstract String[] getImports();
	
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
		return baseIntf.getSimpleSourceName() + PROXY_SUFFIX;
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
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredElementInvalidScreenID(),e);
			throw new CruxGeneratorException();
		}
		Screen screen = ScreenFactory.getInstance().getScreen(screenID);
		return screen;
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
		throw new CruxGeneratorException(messages.errorGeneratingRegisteredElementCanNotFoundCurrentScreen());
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
	        		throw new ScreenConfigException(messages.errorGeneratingRegisteredElementModuleNotFound(requestedScreen.getModule()));
	        	}
	        	for (String screenID : screenIDs)
	        	{
	        		Screen screen = ScreenFactory.getInstance().getScreen(screenID);
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
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredElementCanNotFoundScreens(),e);
			throw new CruxGeneratorException();
        }
	}
	
	/**
	 * @return a sourceWriter for the proxy class
	 */
	@Override
	protected SourceWriter getSourceWriter()
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

		return composerFactory.createSourceWriter(context, printWriter);
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
			long lastModified = context.getSourceLastModifiedTime(baseIntf);

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
}
