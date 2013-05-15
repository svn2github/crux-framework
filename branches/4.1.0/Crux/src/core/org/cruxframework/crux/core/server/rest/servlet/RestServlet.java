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
package org.cruxframework.crux.core.server.rest.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.server.rest.annotation.HttpMethod;
import org.cruxframework.crux.core.server.rest.core.HttpHeaders;
import org.cruxframework.crux.core.server.rest.core.MediaType;
import org.cruxframework.crux.core.server.rest.core.dispatch.ResourceMethod.MethodReturn;
import org.cruxframework.crux.core.server.rest.core.dispatch.RestDispatcher;
import org.cruxframework.crux.core.server.rest.spi.HttpRequest;
import org.cruxframework.crux.core.server.rest.spi.HttpResponse;
import org.cruxframework.crux.core.server.rest.spi.LoggableFailure;
import org.cruxframework.crux.core.server.rest.spi.UriInfo;
import org.cruxframework.crux.core.server.rest.util.HttpHeaderNames;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class RestServlet extends HttpServlet 
{
	private static final Log logger = LogFactory.getLog(RestServlet.class);

	private static final long serialVersionUID = -4338760751718522206L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		processRequest(req, resp, HttpMethod.POST);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		processRequest(req, resp, HttpMethod.PUT);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		processRequest(req, resp, HttpMethod.DELETE);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		processRequest(req, resp, HttpMethod.GET);
	}

	protected void processRequest(HttpServletRequest req, HttpServletResponse res, String method) throws IOException
	{
		HttpHeaders headers = null;
		UriInfo uriInfo = null;
		try
		{
			headers = ServletUtil.extractHttpHeaders(req);
			uriInfo = ServletUtil.extractUriInfo(req);
		}
		catch (Exception e)
		{
			res.sendError(HttpServletResponse.SC_BAD_REQUEST);
			logger.warn("Failed to parse request.", e);
			return;
		}
		
		HttpRequest request = new HttpRequest(req, headers, uriInfo, method);
		HttpResponse response = new HttpResponse(res);

		try
		{
			MethodReturn methodReturn = RestDispatcher.dispatch(request);
			ServletUtil.writeResponse(request, response, methodReturn);
		}
		catch (LoggableFailure e) 
		{
			response.sendError(e.getResponseCode(), "Server error processing request.");
			response.getOutputHeaders().putSingle(HttpHeaderNames.CONTENT_TYPE, new MediaType("text", "plain", "UTF-8"));
			logger.error(e.getMessage(), e);
		}
		catch (Exception e) 
		{
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error processing request.");
			response.getOutputHeaders().putSingle(HttpHeaderNames.CONTENT_TYPE, new MediaType("text", "plain", "UTF-8"));
			logger.error(e.getMessage(), e);
		}
	}
}
