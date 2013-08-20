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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class JsonUtil
{
	private static ObjectMapper mapper;
	private static final Lock lock = new ReentrantLock();

	public static ObjectReader createReader(Type type)
	{
		ObjectMapper mapper = getObjectMapper();
		JavaType paramJavaType = mapper.getTypeFactory().constructType(type);
		ObjectReader reader = mapper.reader(paramJavaType);
		return reader;
	}

	public static ObjectWriter createWriter(Type type)
	{
		ObjectMapper mapper = getObjectMapper();
		JavaType paramJavaType = mapper.getTypeFactory().constructType(type);
		ObjectWriter writer = mapper.writerWithType(paramJavaType);
		return writer;
	}
	
	private static ObjectMapper getObjectMapper()
    {
		if (mapper == null)
		{
			lock.lock();
			try
			{
				if (mapper == null)
				{
					mapper = new ObjectMapper();
				}
			}
			finally
			{
				lock.unlock();
			}
		}
	    return mapper;
    }
}