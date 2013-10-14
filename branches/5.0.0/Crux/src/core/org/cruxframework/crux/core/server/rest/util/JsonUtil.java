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

import org.cruxframework.crux.core.client.bean.JsonEncoder.JsonSubTypes;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.jsontype.NamedType;

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
				
				//StdTypeResolverBuilder typeResolverBuilder = new ObjectMapper.DefaultTypeResolverBuilder(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);
				//typeResolverBuilder = typeResolverBuilder.inclusion(JsonTypeInfo.As.PROPERTY);
		        
//		        typeResolverBuilder.init(JsonTypeInfo.Id.NAME, new ObjectMapper.DefaultType DefaultTyp ClassNameIdResolver(SimpleType.construct(Base.class), TypeFactory.defaultInstance()) {
//		            private HashMap<Class, Class> classes = new HashMap<Class, Class>() {
//		                {
//		                    put(ConcreteA.class, ConcreteAAdapter.class);
//		                    put(ConcreteB.class, ConcreteBAdapter.class);
//		                    put(ConcreteC.class, ConcreteCAdapter.class);
//		                }
//		            };
//
//		            @Override
//		            public String idFromValue(Object value) {
//		                return (classes.containsKey(value.getClass())) ? value.getClass().getName() : null;
//		            }
//
//		            @Override
//		            public JavaType typeFromId(String id) {
//		                try {
//		                    return classes.get(Class.forName(id)) == null ? super.typeFromId(id) : _typeFactory.constructSpecializedType(_baseType, classes.get(Class.forName(id)));
//		                } catch (ClassNotFoundException e) {
//		                    // todo catch the e
//		                }
//		                return super.typeFromId(id);
//		            }
//		        });
		        //mapper.setDefaultTyping(typeResolverBuilder);
				
				for(JsonSubTypes.Type innerObject : jsonSubTypes.value())
				{
					//mapper.registerSubtypes(innerObject.value());
					mapper.registerSubtypes(new NamedType(innerObject.value(), innerObject.name()));
					
					
			    }
			}
		}
	}

	public static ObjectWriter createWriter(Type type)
	{
		ObjectMapper mapper = getObjectMapper();
		registerSubtypes(type, mapper);
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