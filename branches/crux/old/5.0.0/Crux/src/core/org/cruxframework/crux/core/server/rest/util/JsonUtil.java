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
package org.cruxframework.crux.core.server.rest.util;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.cruxframework.crux.core.shared.json.annotations.JsonSubTypes;
import org.cruxframework.crux.core.utils.ClassUtils;
import org.cruxframework.crux.core.utils.ClassUtils.PropertyInfo;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class JsonUtil
{
	private static ObjectMapper defaultMapper;
	private static ObjectMapper subTypeAwareMapper;
	
	private static final Lock lock = new ReentrantLock();

	public static ObjectReader createReader(Type type)
	{
		ObjectMapper mapper = getObjectMapper(type);
		setGlobalConfigurations(mapper);
		JavaType paramJavaType = mapper.getTypeFactory().constructType(type);
		ObjectReader reader = mapper.reader(paramJavaType);
		return reader;
	}

	public static ObjectWriter createWriter(Type type)
	{
		ObjectMapper mapper = getObjectMapper(type);
		setGlobalConfigurations(mapper);
		JavaType paramJavaType = mapper.getTypeFactory().constructType(type);
		ObjectWriter writer = mapper.writerWithType(paramJavaType);
		return writer;
	}
	
	private static void setGlobalConfigurations(ObjectMapper mapper) 
	{
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}

	private static ObjectMapper getObjectMapper(Type type)
    {
		if(defaultMapper == null)
		{
			lock.lock();
			try
			{
				if(defaultMapper == null)
				{
					defaultMapper = new ObjectMapper();
					subTypeAwareMapper = new ObjectMapper();
					TypeResolverBuilder<?> builder = new SubTypeResolverBuilder();
					builder = builder.init(JsonTypeInfo.Id.CLASS, null);
					builder = builder.inclusion(JsonTypeInfo.As.PROPERTY);
					builder = builder.typeProperty(JsonSubTypes.SUB_TYPE_SELECTOR);			
					subTypeAwareMapper.setDefaultTyping(builder);
				}
			}
			finally
			{
				lock.unlock();
			}
		}
		Class<?> clazz = ClassUtils.getRawType(type);
		return getObjectMapper(type, clazz);
    }

	private static ObjectMapper getObjectMapper(Type type, Class<?> clazz)
    {
		if (clazz != null && hasJsonSubTypes(type, clazz, new HashSet<Class<?>>())) 
		{
			return subTypeAwareMapper;
		}
		return defaultMapper;
    }

	private static boolean hasJsonSubTypes(Type type, Class<?> clazz, Set<Class<?>> searched)
    {
		while (ClassUtils.isCollection(clazz))
		{
			type = ClassUtils.getCollectionBaseType(clazz, type);
			clazz = ClassUtils.getRawType(type);
		}
		if (!searched.contains(clazz))
		{
			searched.add(clazz);
			JsonSubTypes jsonSubTypes = clazz.getAnnotation(JsonSubTypes.class);
			if (jsonSubTypes != null && jsonSubTypes.value() != null)
			{
				if (jsonSubTypes.value().length > 0)
				{
					return true;
				}
			}
			
			PropertyInfo[] propertiesInfo = ClassUtils.extractBeanPropertiesInfo(type);
			if (propertiesInfo != null)
			{
				for (PropertyInfo propertyInfo : propertiesInfo)
                {
	                if (hasJsonSubTypes(propertyInfo.getType(), ClassUtils.getRawType(propertyInfo.getType()), searched))
	                {
	                	return true;
	                }
                }
			}
			
		}
		return false;
    }
	
	private static class SubTypeResolverBuilder extends DefaultTypeResolverBuilder
	{
        private static final long serialVersionUID = -7357920769133327574L;

		public SubTypeResolverBuilder()
        {
	        super(DefaultTyping.NON_FINAL);
        }
		
		@Override
		public boolean useForType(JavaType t)
		{
			JsonSubTypes jsonSubTypes = t.getRawClass().getAnnotation(JsonSubTypes.class);
			if (jsonSubTypes != null && jsonSubTypes.value() != null)
			{
				return (jsonSubTypes.value().length > 0);
			}
			
		    return false;
		}
	}
}