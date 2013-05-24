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
package org.cruxframework.crux.core.server.rest.core.dispatch;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.cruxframework.crux.core.server.rest.spi.HttpRequest;
import org.cruxframework.crux.core.server.rest.spi.InternalServerErrorException;
import org.cruxframework.crux.core.server.rest.state.ResourceStateConfig;
import org.cruxframework.crux.core.server.rest.util.HttpMethodHelper;
import org.cruxframework.crux.core.server.rest.util.JsonUtil;
import org.cruxframework.crux.core.utils.ClassUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class ResourceMethod
{
	private static final Lock lock = new ReentrantLock();

	protected String httpMethod;
	protected Method method;
	protected Class<?> resourceClass;
	protected Type genericReturnType;
	protected MethodInvoker methodInvoker;
	protected ObjectWriter writer;
	protected CacheInfo cacheInfo;
	protected boolean hasReturnType;

	public ResourceMethod(Class<?> clazz, Method method, String httpMethod)
	{
		this.httpMethod = httpMethod;
		this.resourceClass = clazz;
		this.method = method;
		this.genericReturnType = ClassUtils.getGenericReturnTypeOfGenericInterfaceMethod(clazz, method);
		this.hasReturnType = genericReturnType != null && !genericReturnType.equals(Void.class) && !genericReturnType.equals(Void.TYPE);
		if (!hasReturnType && httpMethod.equals("GET"))
		{
			throw new InternalServerErrorException("Invalid rest method: " + method.toString() + ". @GET methods " +
					"can not be void.", "Can not execute requested service");
		}
		
		this.methodInvoker = new MethodInvoker(resourceClass, method, httpMethod);
		this.cacheInfo = HttpMethodHelper.getCacheInfoForGET(method);

	}

	public Type getGenericReturnType()
	{
		return genericReturnType;
	}

	public Class<?> getResourceClass()
	{
		return resourceClass;
	}

	public Annotation[] getMethodAnnotations()
	{
		return method.getAnnotations();
	}

	public Method getMethod()
	{
		return method;
	}

	public MethodReturn invoke(HttpRequest request)
	{
        try
        {
        	if (ResourceStateConfig.isResourceStateCacheEnabled())
        	{
        		StateHandler stateHandler = new StateHandler(this, request);
        		MethodReturn ret = stateHandler.handledByCache();
        		if (ret == null)
        		{
        			Object target = resourceClass.newInstance();
        			ret = invoke(request, target);
        			stateHandler.updateState(ret);
        		}
        		return ret;
        	}
        	else
        	{
        		Object target = resourceClass.newInstance();
        		return invoke(request, target);
        	}
        }
        catch (Exception e)
        {
        	throw new InternalServerErrorException("Error invoking rest service endpoint", "Error processing requested service", e); 
        }
	}

	public String getHttpMethod()
	{
		return httpMethod;
	}
	
	private MethodReturn invoke(HttpRequest request, Object target)
	{
		Object rtn = methodInvoker.invoke(request, target);
		String retVal = null;
		if (hasReturnType && rtn != null)
		{
			if (writer == null)
			{
				lock.lock();
				try
				{
					if (writer == null)
					{
						writer = JsonUtil.createWriter(genericReturnType);
					}
				}
				finally
				{
					lock.unlock();
				}
			}
			try
            {
	            retVal = writer.writeValueAsString(rtn);
            }
            catch (JsonProcessingException e)
            {
            	throw new InternalServerErrorException("Error serializing rest service return", "Error processing requested service", e); 
            }
		}
		return new MethodReturn(hasReturnType, retVal, cacheInfo, null);
	}

	public static class MethodReturn
	{
		protected final boolean hasReturnType;
		protected final String ret;
		private final CacheInfo cacheInfo;
		private final ConditionalResponse conditionalResponse;
		protected String etag;
		protected long dateModified;
		
		protected MethodReturn(boolean hasReturnType, String ret, CacheInfo cacheInfo, ConditionalResponse conditionalResponse)
        {
			this.hasReturnType = hasReturnType;
			this.ret = ret;
			this.cacheInfo = cacheInfo;
			this.conditionalResponse = conditionalResponse;
        }

		public boolean hasReturnType()
        {
        	return hasReturnType;
        }

		public String getReturn()
        {
        	return ret;
        }

		public CacheInfo getCacheInfo()
        {
        	return cacheInfo;
        }

		public ConditionalResponse getConditionalResponse()
        {
        	return conditionalResponse;
        }

		public String getEtag()
        {
        	return etag;
        }

		public void setEtag(String etag)
        {
        	this.etag = etag;
        }

		public long getDateModified()
        {
        	return dateModified;
        }

		public void setDateModified(long dateModified)
        {
        	this.dateModified = dateModified;
        }
	}
}