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
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import org.cruxframework.crux.core.server.rest.spi.BadRequestException;
import org.cruxframework.crux.core.utils.ClassUtils;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class StringParameterInjector
{
	protected Class<?> type;
	protected Type genericType;
	protected Constructor<?> constructor;
	protected Method valueOf;
	protected String defaultValue;
	protected String paramName;
	protected AccessibleObject target;

	protected StringParameterInjector()
    {
    }
	
	protected StringParameterInjector(Class<?> type, Type genericType, String paramName, String defaultValue, AccessibleObject target, Annotation[] annotations)
	{
		initialize(type, genericType, paramName, defaultValue, target, annotations);
	}

	protected void initialize(Class<?> type, Type genericType, String paramName, String defaultValue, AccessibleObject target, Annotation[] annotations)
	{
		this.type = type;
		this.paramName = paramName;
		this.defaultValue = defaultValue;
		this.target = target;
		this.genericType = genericType;

		if (!type.isPrimitive())
		{
			try
			{
				constructor = type.getConstructor(String.class);
				if (constructor != null && !Modifier.isPublic(constructor.getModifiers()))
				{
					constructor = null;
				}
			}
			catch (NoSuchMethodException ignored)
			{

			}
			if (constructor == null)
			{
				valueOf = findValueOfMethod();
			}
		}
	}

	protected Method findValueOfMethod()
    {
	    Method fromString = null;
	    Method valueOf = null;
	    try
	    {
	    	fromString = type.getDeclaredMethod("fromString", String.class);
	    	if (Modifier.isStatic(fromString.getModifiers()) == false)
	    	{
	    		fromString = null;
	    	}
	    }
	    catch (NoSuchMethodException ignored)
	    {
	    }
	    try
	    {
	    	valueOf = type.getDeclaredMethod("valueOf", String.class);
	    	if (Modifier.isStatic(valueOf.getModifiers()) == false)
	    	{
	    		valueOf = null;
	    	}
	    }
	    catch (NoSuchMethodException ignored)
	    {
	    }
	    if (valueOf == null)
	    {
	    	valueOf = fromString;
	    }
	    if (valueOf == null)
	    {
	    	throw new RuntimeException("Unable to find a constructor that takes a String param or a valueOf() or fromString() method for " + getParamSignature() + " on " + target + " for basetype: " + type.getName());
	    }
	    return valueOf;
    }

	public String getParamSignature()
	{
		return type.getName() + "(\"" + paramName + "\")";
	}

	public Object extractValue(String strVal)
	{
		if (strVal == null)
		{
			if (defaultValue == null)
			{
				if (!type.isPrimitive())
				{
					return null;
				}
			}
			else
			{
				strVal = defaultValue;
			}
		}
		if (type.isPrimitive())
		{
			return ClassUtils.stringToPrimitiveBoxType(type, strVal);
		}
		else if (constructor != null)
		{
			try
			{
				return constructor.newInstance(strVal);
			}
			catch (Exception e)
			{
				throw new BadRequestException("Unable to extract parameter from http request for " + getParamSignature() +" for " + target, e);
			}
		}
		else if (valueOf != null)
		{
			try
			{
				return valueOf.invoke(null, strVal);
			}
			catch (Exception e)
			{
				throw new BadRequestException("Unable to extract parameter from http request: " + getParamSignature() + " for " + target, e);
			}
		}
		return null;
	}
}