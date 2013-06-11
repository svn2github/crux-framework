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
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;


import org.cruxframework.crux.core.server.rest.annotation.CookieParam;
import org.cruxframework.crux.core.server.rest.annotation.DefaultValue;
import org.cruxframework.crux.core.server.rest.annotation.FormParam;
import org.cruxframework.crux.core.server.rest.annotation.HeaderParam;
import org.cruxframework.crux.core.server.rest.annotation.PathParam;
import org.cruxframework.crux.core.server.rest.annotation.QueryParam;
import org.cruxframework.crux.core.server.rest.spi.BadRequestException;
import org.cruxframework.crux.core.server.rest.spi.ForbiddenException;
import org.cruxframework.crux.core.server.rest.spi.HttpRequest;
import org.cruxframework.crux.core.server.rest.spi.InternalServerErrorException;
import org.cruxframework.crux.core.server.rest.spi.RestFailure;
import org.cruxframework.crux.core.utils.ClassUtils;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class MethodInvoker
{
	protected Method method;
	protected Class<?> rootClass;
	protected ValueInjector[] params;
	protected List<RequestPreprocessor> preprocessors; 
	private Class<?>[] exceptionTypes;

	public MethodInvoker(Class<?> root, Method method, String httpMethod)
	{
		this.method = method;
		this.exceptionTypes = method.getExceptionTypes();
		this.rootClass = root;
		this.params = new ValueInjector[method.getParameterTypes().length];
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		for (int i = 0; i < method.getParameterTypes().length; i++)
		{
			Class<?> type;
			Type genericType;

			// the parameter type might be a type variable defined in a
			// superclass
			if (method.getGenericParameterTypes()[i] instanceof TypeVariable<?>)
			{
				// try to find out the value of the type variable
				genericType = ClassUtils.getActualValueOfTypeVariable(root, (TypeVariable<?>) genericParameterTypes[i]);
				type = ClassUtils.getRawType(genericType);
			}
			else
			{
				type = method.getParameterTypes()[i];
				genericType = genericParameterTypes[i];
			}

			Annotation[] annotations = method.getParameterAnnotations()[i];
			params[i] = createParameterExtractor(root, method, type, genericType, annotations);
		}
		validateParamExtractors(httpMethod);
		//initializePreprocessors();
	}

	public ValueInjector[] getParams()
	{
		return params;
	}

	public Object[] injectArguments(HttpRequest input)
	{
		try
		{
			Object[] args = null;
			if (params != null && params.length > 0)
			{
				args = new Object[params.length];
				int i = 0;
				for (ValueInjector extractor : params)
				{
					args[i++] = extractor.inject(input);
				}
			}
			return args;
		}
		catch (RestFailure f)
		{
			throw f;
		}
		catch (Exception e)
		{
			BadRequestException badRequest = new BadRequestException("Failed processing arguments of " + method.toString(), "Can not invoke requested service with given arguments", e);
			throw badRequest;
		}
	}

	public Object invoke(HttpRequest request, Object resource) throws RestFailure
	{
		preprocess(request);
		
		Object[] args = injectArguments(request);

		try
		{
			Object result = method.invoke(resource, args);
			return result;
		}
		catch (IllegalAccessException e)
		{
			throw new InternalServerErrorException("Not allowed to reflect on method: " + method.toString(), "Can not execute requested service", e);
		}
		catch (InvocationTargetException e)
		{
			Throwable cause = e.getCause();
			if (isCheckedException(cause))
			{
				throw new ForbiddenException("Can not execute requested service. Checked Excpetion occurred on method: "+method.toString(), cause.getMessage(), cause);
			}
			else
			{
				throw new InternalServerErrorException("Can not execute requested service. Unchecked Excpetion occurred on method: "+method.toString(), 
						"Can not execute requested service", cause);
			}
		}
		catch (IllegalArgumentException e)
		{
			String msg = "Bad arguments passed to " + method.toString() + "  (";
			if (args != null)
			{
				boolean first = false;
				for (Object arg : args)
				{
					if (!first)
					{
						first = true;
					}
					else
					{
						msg += ",";
					}
					if (arg == null)
					{
						msg += " null";
						continue;
					}
					msg += " " + arg.getClass().getName();
				}
			}
			msg += " )";
			throw new InternalServerErrorException(msg, "Can not execute requested service", e);
		}
	}

	protected void initializePreprocessors() throws RequestProcessorException
    {
	    preprocessors = new ArrayList<RequestPreprocessor>();
	    //TODO continuar daqui
	    for (RequestPreprocessor preprocessor : preprocessors)
        {
	        preprocessor.createProcessor(method);
        }
    }

	protected void preprocess(HttpRequest request)
    {
		for (RequestPreprocessor preprocessor : preprocessors)
        {
	        preprocessor.preprocess(request);
        }
    }

	protected boolean isCheckedException(Throwable throwable)
	{
		if (exceptionTypes != null)
		{
			for (Class<?> exception : exceptionTypes)
            {
	            if (exception.isAssignableFrom(throwable.getClass()))
	            {
	            	return true;
	            }
            }
		}
		return false;
	}
	
	protected ValueInjector createParameterExtractor(Class<?> injectTargetClass, AccessibleObject injectTarget, Class<?> type, Type genericType, Annotation[] annotations)
	{
		DefaultValue defaultValue = ClassUtils.findAnnotation(annotations, DefaultValue.class);
		String defaultVal = null;
		if (defaultValue != null)
		{
			defaultVal = defaultValue.value();
		}

		QueryParam query;
		HeaderParam header;
		PathParam uriParam;
		CookieParam cookie;
		FormParam formParam;

		if ((query = ClassUtils.findAnnotation(annotations, QueryParam.class)) != null)
		{
			return new QueryParamInjector(type, genericType, injectTarget, query.value(), defaultVal, annotations);
		}
		else if ((header = ClassUtils.findAnnotation(annotations, HeaderParam.class)) != null)
		{
			return new HeaderParamInjector(type, genericType, injectTarget, header.value(), defaultVal, annotations);
		}
		else if ((formParam = ClassUtils.findAnnotation(annotations, FormParam.class)) != null)
		{
			return new FormParamInjector(type, genericType, injectTarget, formParam.value(), defaultVal, annotations);
		}
		else if ((cookie = ClassUtils.findAnnotation(annotations, CookieParam.class)) != null)
		{
			return new CookieParamInjector(type, genericType, injectTarget, cookie.value(), defaultVal, annotations);
		}
		else if ((uriParam = ClassUtils.findAnnotation(annotations, PathParam.class)) != null)
		{
			return new PathParamInjector(type, genericType, injectTarget, uriParam.value(), defaultVal, annotations);
		}
		else
		{
			return new MessageBodyParamInjector(injectTargetClass, injectTarget, type, genericType, annotations);
		}
	}
	
	protected void validateParamExtractors(String httpMethod)
    {
		boolean hasFormParam = false;
		boolean hasBodyParam = false;
		
		for (ValueInjector paramInjector : params)
        {
	        if (paramInjector instanceof FormParamInjector)
	        {
	        	hasFormParam = true;
	        }
	        if (paramInjector instanceof MessageBodyParamInjector)
	        {
	    		if (hasBodyParam)
	    		{
	    			throw new InternalServerErrorException("Invalid rest method: " + method.toString() + ". Can not use receive " +
	    					"more than one parameter through body text", "Can not execute requested service");
	    		}
	        	hasBodyParam = true;
	        }
        }

		if (hasBodyParam && hasFormParam)
		{
			throw new InternalServerErrorException("Invalid rest method: " + method.toString() + ". Can not use both " +
					"types on the same method: FormParam and BodyParam", "Can not execute requested service");
		}
		if ((hasBodyParam || hasFormParam) && httpMethod.equals("GET"))
		{
			throw new InternalServerErrorException("Invalid rest method: " + method.toString() + ". Can receive " +
					"parameters on body for GET methods.", "Can not execute requested service");
		}
    }

	
}