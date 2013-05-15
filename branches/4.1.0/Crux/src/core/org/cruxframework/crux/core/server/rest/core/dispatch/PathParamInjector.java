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
import java.util.ArrayList;
import java.util.List;

import org.cruxframework.crux.core.server.rest.core.registry.PathSegment;
import org.cruxframework.crux.core.server.rest.spi.HttpRequest;
import org.cruxframework.crux.core.server.rest.spi.InternalServerErrorException;
import org.cruxframework.crux.core.server.rest.spi.UriInfo;
import org.cruxframework.crux.core.utils.ClassUtils;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class PathParamInjector implements ValueInjector
{
	private StringParameterInjector extractor;
	private String paramName;
	private boolean pathSegmentArray = false;
	private boolean pathSegmentList = false;

	public PathParamInjector(Class<?> type, Type genericType, AccessibleObject target, String paramName, String defaultValue, Annotation[] annotations)
	{
		if (isPathSegmentArray(type))
		{
			pathSegmentArray = true;
		}
		else if (isPathSegmentList(type, genericType))
		{
			pathSegmentList = true;
		}
		else if (!type.equals(PathSegment.class))
		{
			extractor = new StringParameterInjector(type, genericType, paramName, defaultValue, target, annotations);
		}
		this.paramName = paramName;
	}

	private boolean isPathSegmentArray(Class<?> type)
	{
		return type.isArray() && type.getComponentType().equals(PathSegment.class);
	}

	private boolean isPathSegmentList(Class<?> type, Type genericType)
	{
		Class<?> collectionBaseType = ClassUtils.getCollectionBaseType(type, genericType);
		return List.class.equals(type) && collectionBaseType != null && collectionBaseType.equals(PathSegment.class);
	}

	public Object inject(HttpRequest request)
	{
		if (extractor == null) // we are a PathSegment
		{
			UriInfo uriInfo = request.getUri();
			List<PathSegment[]> list = null;
			list = uriInfo.getPathParameterPathSegments().get(paramName);
			if (list == null)
			{
				throw new InternalServerErrorException("Unknown @PathParam: " + paramName + " for path: " + uriInfo.getPath());
			}
			PathSegment[] segments = list.get(list.size() - 1);
			if (pathSegmentArray)
			{
				return segments;
			}
			else if (pathSegmentList)
			{
				ArrayList<PathSegment> pathlist = new ArrayList<PathSegment>();
				for (PathSegment seg : segments)
				{
					pathlist.add(seg);
				}
				return pathlist;
			}
			else
			{
				return segments[segments.length - 1];
			}
		}
		else
		{
			List<String> list = request.getUri().getPathParameters(true).get(paramName);
			if (list == null)
			{
				throw new InternalServerErrorException("Unknown @PathParam: " + paramName + " for path: " + request.getUri().getPath());
			}
			if (list != null && list.size() > 0)
			{
				return extractor.extractValue(list.get(list.size() - 1));
			}
			return null;
		}
	}
}