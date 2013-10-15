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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.cruxframework.crux.core.client.bean.JsonEncoder.JsonSubTypes;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class JsonUtil
{
	private static Map<Type,ObjectMapper> mapper;
	private static final Lock lock = new ReentrantLock();

	public static ObjectReader createReader(Type type)
	{
		ObjectMapper mapper = getObjectMapper(type);
		registerSubtypes(type, mapper);
		JavaType paramJavaType = mapper.getTypeFactory().constructType(type);
		ObjectReader reader = mapper.reader(paramJavaType);
		return reader;
	}

	private static void registerSubtypes(Type type, ObjectMapper mapper) 
	{
		if (type instanceof Class) {
			Class<?> clazz = (Class<?>) type;
			JsonSubTypes jsonSubTypes = clazz.getAnnotation(JsonSubTypes.class);
			if (jsonSubTypes != null && jsonSubTypes.value() != null)
			{
				mapper.enableDefaultTypingAsProperty(DefaultTyping.NON_FINAL, "type");
				Class<?>[] innerClasses = new Class<?>[jsonSubTypes.value().length];
				int i=0;
				for(JsonSubTypes.Type innerObject : jsonSubTypes.value())
				{
					innerClasses[i++] = innerObject.value();
			    }
				mapper.registerSubtypes(innerClasses);
			}
		}
	}

	public static ObjectWriter createWriter(Type type)
	{
		ObjectMapper mapper = getObjectMapper(type);
		registerSubtypes(type, mapper);
		JavaType paramJavaType = mapper.getTypeFactory().constructType(type);
		ObjectWriter writer = mapper.writerWithType(paramJavaType);
		return writer;
	}
	
	private static ObjectMapper getObjectMapper(Type type)
    {
		if (mapper == null || mapper.get(type) == null)
		{
			lock.lock();
			try
			{
				if (mapper == null)
				{
					mapper = new HashMap<Type,ObjectMapper>();
				}
				
				if(mapper.get(type) == null)
				{
					mapper.put(type, new ObjectMapper());
				}
			}
			finally
			{
				lock.unlock();
			}
		}
	    return mapper.get(type);
    }
}