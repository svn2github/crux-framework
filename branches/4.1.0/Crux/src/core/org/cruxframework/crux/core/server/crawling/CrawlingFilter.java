/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.core.server.crawling;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.zip.GZIPOutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.server.Environment;
import org.cruxframework.crux.core.utils.StreamUtils;


/**
 * Filter to serve search engines, sending static snapshots in the place of DHTML based pages.
 * See this {@link https://developers.google.com/webmasters/ajax-crawling/} 
 * 
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public final class CrawlingFilter implements Filter
{
	private static final int EXPIRES_DELTA = 86400000; // One day
	private static final Log logger = LogFactory.getLog(CrawlingFilter.class);
	private FilterConfig config;
	private String defaultSnaphot;

	/**
	 * 
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException 
	{
		try
		{
			if (!Environment.isProduction())
			{
				chain.doFilter(req, res);
				return;
			}
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response;
			String ae = request.getHeader("accept-encoding");
			boolean gzipped = false;
			if (ae != null && ae.indexOf("gzip") != -1) 
			{        
				response = new GZIPResponseWrapper((HttpServletResponse) res);
				gzipped = true;
			}
			else
			{
				response = (HttpServletResponse)res;
			}

			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			String escapedFragmentEncoded = request.getParameter("_escaped_fragment_");
			if (escapedFragmentEncoded != null)
			{
				if (escapedFragmentEncoded.length() == 0 && defaultSnaphot != null && defaultSnaphot.length() > 0)
				{
					escapedFragmentEncoded=defaultSnaphot;
				}
				
				String escapedFragment = URLDecoder.decode(escapedFragmentEncoded, "UTF-8");
				String page = getRequestedPage(request);
				String pagePath = CrawlingUtils.getStaticPageFor(page, escapedFragment);
				if (pagePath != null && pagePath.length() >0)
				{
					InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(pagePath);
					if (in == null)
					{
						logger.error("snapshot for requested page ["+pagePath+"] not found.");
					}
					else
					{
						try
						{
							StreamUtils.write(in, response.getOutputStream(), true);
							return;
						}
						catch (IOException e) 
						{
							logger.error("Error reading requested page ["+pagePath+"].", e);
						}
					}
				}
			}
			long current = System.currentTimeMillis();
			long expires = current + EXPIRES_DELTA;
			HttpServletResponse httpResponse = ((HttpServletResponse)response);
			httpResponse.addDateHeader("Expires", expires);
			httpResponse.addDateHeader("Last-Modified", current);			
			response.addHeader("Cache-Control", "public, max-age="+(EXPIRES_DELTA/1000));// seconds
			chain.doFilter(request, response);
			if (gzipped)
			{
				((GZIPResponseWrapper)response).finishResponse();
			}
		}
		catch (ServletException e)
		{
			logger.error("Error processing request", e);
		}
	}

	/**
	 * 
	 * @param req
	 * @return
	 */
	protected String getRequestedPage(ServletRequest req)
	{
		HttpServletRequest request = (HttpServletRequest) req;
		String result = request.getPathInfo();
		if (result == null)
		{
			result = request.getRequestURI();
		}
		if (result != null && result.length() > 0)
		{
			if (result.endsWith(".html") && !result.endsWith("hosted.html") && !result.endsWith("cache.html"))
			{
				if (result.startsWith("/"))
				{
					result = result.substring(1);
				}

				String contextPath = config.getServletContext().getContextPath();
				result = removeStringPrefix(result, contextPath);
			}
			else
			{
				result = null;
			}
		}
		else
		{
			result = null;
		}
		return result;
	}

	/**
	 * @param input
	 * @param prefix
	 * @return
	 */
	protected String removeStringPrefix(String input, String prefix)
	{
		if (prefix.startsWith("/"))
		{
			prefix = prefix.substring(1);
		}
		if (input.startsWith(prefix))
		{
			input = input.substring(prefix.length());
		}
		if (input.startsWith("/"))
		{
			input = input.substring(1);
		}
		return input;
	}

	@Override
	public void init(FilterConfig config) throws ServletException
	{
		this.config = config;
		this.defaultSnaphot = config.getInitParameter("defaultSnaphot");
	}

	@Override
	public void destroy()
	{
	}

	static class GZIPResponseStream extends ServletOutputStream 
	{ 
		protected ByteArrayOutputStream baos = null;
		protected GZIPOutputStream gzipstream = null;
		protected boolean closed = false;
		protected HttpServletResponse response = null;
		protected ServletOutputStream output = null;

		public GZIPResponseStream(HttpServletResponse response) throws IOException 
		{
			super();
			closed = false;
			this.response = response;
			this.output = response.getOutputStream();
			baos = new ByteArrayOutputStream();
			gzipstream = new GZIPOutputStream(baos);
		}

		public void close() throws IOException 
		{
			if (closed) 
			{
				throw new IOException("This output stream has already been closed");
			}
			gzipstream.finish();

			byte[] bytes = baos.toByteArray();

			response.addHeader("Content-Length", Integer.toString(bytes.length)); 
			response.addHeader("Content-Encoding", "gzip");
			output.write(bytes);
			output.flush();
			output.close();
			closed = true;
		}

		public void flush() throws IOException 
		{
			if (closed) 
			{
				throw new IOException("Cannot flush a closed output stream");
			}
			gzipstream.flush();
		}

		public void write(int b) throws IOException 
		{
			if (closed) 
			{
				throw new IOException("Cannot write to a closed output stream");
			}
			gzipstream.write((byte)b);
		}

		public void write(byte b[]) throws IOException 
		{
			write(b, 0, b.length);
		}

		public void write(byte b[], int off, int len) throws IOException 
		{
			if (closed) 
			{
				throw new IOException("Cannot write to a closed output stream");
			}
			gzipstream.write(b, off, len);
		}

		public boolean closed() 
		{
			return (this.closed);
		}

		public void reset() 
		{
			//noop
		}
	}

	static class GZIPResponseWrapper extends HttpServletResponseWrapper 
	{
		protected HttpServletResponse origResponse = null;
		protected ServletOutputStream stream = null;
		protected PrintWriter writer = null;

		public GZIPResponseWrapper(HttpServletResponse response) 
		{
			super(response);
			origResponse = response;
		}

		public ServletOutputStream createOutputStream() throws IOException 
		{
			return (new GZIPResponseStream(origResponse));
		}

		public void finishResponse() 
		{
			try 
			{
				if (writer != null) 
				{
					writer.close();
				} 
				else 
				{
					if (stream != null) 
					{
						stream.close();
					}
				}
			} 
			catch (IOException e) 
			{
				//
			}
		}

		public void flushBuffer() throws IOException 
		{
			stream.flush();
		}

		public ServletOutputStream getOutputStream() throws IOException 
		{
			if (writer != null) 
			{
				throw new IllegalStateException("getWriter() has already been called!");
			}

			if (stream == null)
			{
				stream = createOutputStream();
			}
			return (stream);
		}

		public PrintWriter getWriter() throws IOException 
		{
			if (writer != null) 
			{
				return (writer);
			}

			if (stream != null) 
			{
				throw new IllegalStateException("getOutputStream() has already been called!");
			}

			stream = createOutputStream();
			writer = new PrintWriter(new OutputStreamWriter(stream, "UTF-8"));
			return (writer);
		}

		public void setContentLength(int length) {}
	}

}
