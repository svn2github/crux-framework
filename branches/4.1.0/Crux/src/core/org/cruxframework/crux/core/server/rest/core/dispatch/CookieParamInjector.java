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
import java.lang.reflect.Type;


import org.cruxframework.crux.core.server.rest.core.Cookie;
import org.cruxframework.crux.core.server.rest.spi.HttpRequest;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class CookieParamInjector extends StringParameterInjector implements ValueInjector
{

	public CookieParamInjector(Class<?> type, Type genericType, AccessibleObject target, String cookieName, String defaultValue, Annotation[] annotations)
	{
		if (type.equals(Cookie.class))
		{
			this.type = type;
			this.paramName = cookieName;
			this.defaultValue = defaultValue;
		}
		else
		{
			initialize(type, genericType, cookieName, defaultValue, target, annotations);
		}
	}

	public Object inject(HttpRequest request)
	{
		Cookie cookie = request.getHttpHeaders().getCookies().get(paramName);
		if (type.equals(Cookie.class))
		{
			return cookie;
		}

		if (cookie == null)
		{
			return extractValue(null);
		}
		return extractValue(cookie.getValue());
	}
}