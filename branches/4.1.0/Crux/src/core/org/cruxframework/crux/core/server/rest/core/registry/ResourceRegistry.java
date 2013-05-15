/*
 * Copyright 2011 cruxframework.org
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
package org.cruxframework.crux.core.server.rest.core.registry;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.server.rest.annotation.Path;
import org.cruxframework.crux.core.server.rest.core.UriBuilder;
import org.cruxframework.crux.core.server.rest.core.dispatch.ResourceMethod;
import org.cruxframework.crux.core.server.rest.spi.HttpRequest;
import org.cruxframework.crux.core.server.rest.util.HttpMethodHelper;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ResourceRegistry
{
	private static final Log logger = LogFactory.getLog(ResourceRegistry.class);
	private static final ResourceRegistry instance = new ResourceRegistry();
	private static final Lock lock = new ReentrantLock();
	private static boolean initialized = false;
	
	protected int size;
	protected RootSegment rootSegment = new RootSegment();
	
	/**
	 * Singleton constructor
	 */
	private ResourceRegistry() {}

	/**
	 * Singleton accessor
	 * @return
	 */
	public static ResourceRegistry getInstance()
	{
		return instance;
	}
	
	public RootSegment getRoot()
	{
		if (!initialized)
		{
			initialize();
		}
		return rootSegment;
	}

	/**
	 * Number of endpoints registered
	 * 
	 * @return
	 */
	public int getSize()
	{
		if (!initialized)
		{
			initialize();
		}
		return size;
	}

	/**
	 * Find a resource to invoke on
	 * 
	 * @return
	 */
	public ResourceMethod getResourceMethod(HttpRequest request)
	{
		if (!initialized)
		{
			initialize();
		}
		List<String> matchedUris = request.getUri().getMatchedURIs(false);
		if (matchedUris == null || matchedUris.size() == 0)
		{
			return rootSegment.matchRoot(request);
		}
		// resource location
		String currentUri = request.getUri().getMatchedURIs(false).get(0);
		return rootSegment.matchRoot(request, currentUri.length());
	}

	/**
	 * 
	 * @param clazz
	 * @param base
	 */
	protected void addResource(Class<?> clazz, String base)
	{
		for (Method method : clazz.getMethods())
		{
			if (!method.isSynthetic())
			{
				processMethod(base, clazz, method);
			}
		}
	}

	/**
	 * 
	 * @param classes
	 * @param base
	 */
	protected void addResource(Class<?>[] classes, String base)
	{
		for (Class<?> clazz : classes)
		{
			addResource(clazz, base);
		}
	}

	/**
	 * 
	 * @param base
	 * @param clazz
	 * @param method
	 */
	protected void processMethod(String base, Class<?> clazz, Method method)
	{
		if (method != null)
		{
			Path path = method.getAnnotation(Path.class);
			Set<String> httpMethods = HttpMethodHelper.getHttpMethods(method);

			boolean pathPresent = path != null;
			boolean restAnnotationPresent = pathPresent || (httpMethods != null);
			
			UriBuilder builder = new UriBuilder();
			if (base != null)
				builder.path(base);
			if (clazz.isAnnotationPresent(Path.class))
			{
				builder.path(clazz);
			}
			if (path != null)
			{
				builder.path(method);
			}
			String pathExpression = builder.getPath();
			if (pathExpression == null)
				pathExpression = "";

			if (httpMethods != null)
			{
				ResourceMethod invoker = new ResourceMethod(clazz, method, httpMethods);
				rootSegment.addPath(pathExpression, invoker);
				size++;
			}
			else 
			{
				if (restAnnotationPresent)
				{
					logger.error("Method: " + method.getDeclaringClass().getName() + "." + method.getName() + "() declares rest annotations, but it does not inform the methods it must handle. Use one of @PUT, @POST, @GET or @DELETE.");
				}
				else if (logger.isDebugEnabled())
				{
					logger.debug("Method: " + method.getDeclaringClass().getName() + "." + method.getName() + "() ignored. It is not a rest method.");
				}
			}
			if (restAnnotationPresent && !Modifier.isPublic(method.getModifiers()))
			{
				logger.error("Rest annotations found at non-public method: " + method.getDeclaringClass().getName() + "." + method.getName() + "(); Only public methods may be exposed as resource methods.");
			}
		}
		//TODO nao permitir metodos GET que sejam void
	}
	
	/**
	 * 
	 */
	public static void initialize()
	{
		if (initialized)
		{
			return;
		}
		try
		{
			lock.lock();
			if (initialized)	
			{
				return;
			}
			
			initializeRegistry();
		}
		finally
		{
			lock.unlock();
		}
	}

	private static void initializeRegistry()
    {
	    initialized = true;
	    RestServiceScanner serviceScanner = new RestServiceScanner();
		Iterator<String> restServices = serviceScanner.iterateRestServices();
		
		while (restServices.hasNext())
		{
			String service = restServices.next();
			String serviceClassName = serviceScanner.getServiceClassName(service);
			try
            {
	            instance.addResource(Class.forName(serviceClassName), "");
            }
            catch (ClassNotFoundException e)
            {
            	logger.error("Error initializing rest service class [{"+serviceClassName+"}]", e);
            }
		}
    }
}