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
package org.cruxframework.crux.core.server.offline;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cruxframework.crux.core.server.Environment;
import org.cruxframework.crux.core.server.http.GZIPResponseWrapper;
import org.cruxframework.crux.core.server.rest.core.CacheControl;
import org.cruxframework.crux.core.server.rest.util.DateUtil;
import org.cruxframework.crux.core.server.rest.util.HttpHeaderNames;
import org.cruxframework.crux.core.server.rest.util.HttpResponseCodes;
import org.cruxframework.crux.core.server.rest.util.header.CacheControlHeaderParser;
import org.cruxframework.crux.core.utils.StreamUtils;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class AppcacheFilter implements Filter
{
	private Map<String, Long> lastModifiedDates = Collections.synchronizedMap(new HashMap<String, Long>());
	private FilterConfig filterConfig;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException
	{
		if (Environment.isProduction())
		{
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) resp;
			
			String ifModifiedHeader = request.getHeader(HttpHeaderNames.IF_MODIFIED_SINCE);
			long dateModified = getDateModified(request);
			if ((ifModifiedHeader == null) || isFileModified(ifModifiedHeader, dateModified))
			{
				sendRequestedFile(chain, request, response, dateModified);
			}
			else
			{
				response.setStatus(HttpResponseCodes.SC_NOT_MODIFIED);
			}
		}
		else
		{
			chain.doFilter(req, resp);
		}
	}

	private boolean isFileModified(String ifModifiedHeader, long dateModified)
	{
		boolean result = true;
		try
		{
			if (dateModified > 0)
			{
				Date date = DateUtil.parseDate(ifModifiedHeader);

				if (date.getTime() >= dateModified)
				{
					result = false;
				}
			}
		}
		catch (Exception e) 
		{
			result = true;
		}
		return result;
	}

	private long getDateModified(HttpServletRequest request) throws IOException
	{
		Long result;
		try
		{
			String file = request.getRequestURI();
			result = lastModifiedDates.get(file);
			if (result == null)
			{
				InputStream stream = filterConfig.getServletContext().getResourceAsStream(file);
				if (stream != null)
				{
					String content = StreamUtils.readAsUTF8(stream);
					int indexStart = content.indexOf("# Build Time [");
					int indexEnd = content.indexOf("]", indexStart);
					if (indexStart > 0 && indexEnd > 0)
					{
						String dateStr = content.substring(indexStart+14, indexEnd);
						result = Long.parseLong(dateStr);
						lastModifiedDates.put(file, result);
					}
					else
					{
						result = 0l;
					}
				}
				else
				{
					result = 0l;
				}
			}
		}
		catch (Exception e) 
		{
			result = 0l;
		}

		return result;
	}

	private void sendRequestedFile(FilterChain chain, HttpServletRequest request, HttpServletResponse response, long dateModified) 
	throws IOException, ServletException
	{
		String ae = request.getHeader(HttpHeaderNames.ACCEPT_ENCODING);
		boolean gzipped = false;
		if (ae != null && ae.indexOf("gzip") != -1) 
		{        
			response = new GZIPResponseWrapper(response);
			gzipped = true;
		}

		response.setContentType("text/cache-manifest");
		response.setCharacterEncoding("UTF-8");
		CacheControl cache = new CacheControl();
		cache.setNoCache(true);
		response.addHeader(HttpHeaderNames.CACHE_CONTROL, CacheControlHeaderParser.toString(cache));
		response.addDateHeader(HttpHeaderNames.EXPIRES, System.currentTimeMillis());
		if (dateModified > 0)
		{
			response.addDateHeader(HttpHeaderNames.LAST_MODIFIED, dateModified);
		}
		chain.doFilter(request, response);
		if (gzipped)
		{
			((GZIPResponseWrapper)response).finishResponse();
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		this.filterConfig = filterConfig;
	}

	@Override
	public void destroy()
	{
	}
}
