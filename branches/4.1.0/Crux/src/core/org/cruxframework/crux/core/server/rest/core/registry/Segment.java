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
package org.cruxframework.crux.core.server.rest.core.registry;

import java.util.ArrayList;
import java.util.List;


import org.cruxframework.crux.core.server.rest.core.MediaType;
import org.cruxframework.crux.core.server.rest.core.dispatch.ResourceMethod;
import org.cruxframework.crux.core.server.rest.spi.AmbiguousServiceException;
import org.cruxframework.crux.core.server.rest.spi.HttpRequest;
import org.cruxframework.crux.core.server.rest.spi.MethodNotAllowedException;
import org.cruxframework.crux.core.server.rest.spi.NotAcceptableException;
import org.cruxframework.crux.core.server.rest.spi.NotFoundException;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class Segment
{
	private List<ResourceMethod> methods = new ArrayList<ResourceMethod>();

	protected boolean isEmpty()
	{
		return methods.size() == 0;
	}

	protected ResourceMethod match(String httpMethod, HttpRequest request)
	{
		// MediaType contentType = request.getHttpHeaders().getMediaType();
		List<MediaType> accepts = request.getHttpHeaders().getAcceptableMediaTypes();

		if (!isResponseMediaTypeAllowed(accepts))
		{
			throw new NotAcceptableException("No match for accept header");
		}

		List<ResourceMethod> list = new ArrayList<ResourceMethod>();

		for (ResourceMethod invoker : methods)
		{
			if (invoker.getHttpMethods().contains(httpMethod))
			{
				list.add(invoker);
			}
		}

		if (list.size() == 0)
		{
			if (methods == null || methods.size() == 0)
			{
				throw new NotFoundException("Could not find resource for full path: " + request.getUri().getRequestUri());
			}
			throw new MethodNotAllowedException("No resource method found for " + httpMethod);
		}

		if (list.size() > 1)
		{
		}
		return list.get(0);
	}

	protected void addMethod(ResourceMethod invoker)
	{
		for(String httpMethod: invoker.getHttpMethods())
		{
			for (ResourceMethod res : methods)
			{
				if (res.getHttpMethods().contains(httpMethod))
				{
					throw new AmbiguousServiceException("Ambiguous service methods. Methods ["+invoker.getMethod().getName()+"] " +
							"and ["+res.getMethod().getName()+"], declared on class ["+invoker.getResourceClass().getCanonicalName()+"] tries to serve" +
							" the same rest path and HTTP method.");
				}
			}
		}
		methods.add(invoker);
	}
	
	protected boolean isResponseMediaTypeAllowed(List<MediaType> accepts)
	{
		boolean responseMediaTypeAllowed = false;
		if (accepts == null)
		{ // assumes accepts '*' if not informed
			responseMediaTypeAllowed = true;
		}
		else
		{
			for (MediaType accept : accepts)
			{
				if (MediaType.APPLICATION_JSON_TYPE.isCompatible(accept))
				{
					responseMediaTypeAllowed = true;
					break;
				}
			}
		}
		return responseMediaTypeAllowed;
	}
}