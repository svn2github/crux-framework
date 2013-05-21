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

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.cruxframework.crux.core.server.rest.core.MediaType;
import org.cruxframework.crux.core.server.rest.spi.BadRequestException;
import org.cruxframework.crux.core.server.rest.spi.HttpRequest;
import org.cruxframework.crux.core.server.rest.spi.UnsupportedMediaTypeException;
import org.cruxframework.crux.core.server.rest.util.JsonUtil;
import org.cruxframework.crux.core.utils.StreamUtils;

import com.fasterxml.jackson.databind.ObjectReader;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class MessageBodyParamInjector extends StringParameterInjector implements ValueInjector
{
	private static final Lock lock = new ReentrantLock();
	private ObjectReader reader;

	public MessageBodyParamInjector(Class<?> declaringClass, AccessibleObject target, Class<?> type, Type genericType, Annotation[] annotations)
	{
		super(type, genericType, "body", null, target, annotations);
	}

	public Object inject(HttpRequest request)
	{
		InputStream is = request.getInputStream();
		String body;
		try
		{
			body = StreamUtils.readAsUTF8(is);
		}
		catch (IOException e)
		{
			throw new BadRequestException("Can not read request body for path: " + request.getUri().getPath());
		}
		return extractValue(body, request);
	}

	public Object extractValue(String strVal, HttpRequest request)
	{
		Object value = extractValue(strVal);
		if (strVal != null && value == null)
		{
			MediaType mediaType = request.getHttpHeaders().getMediaType();
			if (mediaType == null)
			{
				mediaType = MediaType.WILDCARD_TYPE;
			}
			if (mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE))
			{
				if (this.reader == null)
				{
					lock.lock();
					try
					{
						if (this.reader == null)
						{
							this.reader = JsonUtil.createReader(genericType);
						}
					}
					finally
					{
						lock.unlock();
					}
				}
				try
                {
	                value = this.reader.readValue(strVal);
                }
                catch (Exception e)
                {
        			throw new BadRequestException("Can not read request body for path: " + request.getUri().getPath());
                }
			}
			else
			{
				throw new UnsupportedMediaTypeException("Media type not supported: " + mediaType.toString());
			}
		}
		return value;
	}
	//TODO nao aceitar parametro no corpo do email que venham de metodos GET... pra GET, forcar que os parametros venham da URI
}