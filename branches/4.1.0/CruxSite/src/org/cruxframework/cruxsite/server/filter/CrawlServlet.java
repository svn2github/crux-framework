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
package org.cruxframework.cruxsite.server.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author Thiago da Rosa de Bustamante
 * 
 */
public final class CrawlServlet implements Filter
{
	private static final Log logger = LogFactory.getLog(CrawlServlet.class);
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException 
	{
		String escapedFragment = request.getParameter("_escaped_fragment_");
		if (escapedFragment != null && escapedFragment.length() > 0)
		{
			String url = rewriteUrl(request);
			// use the headless browser to obtain an HTML snapshot
			final WebClient webClient = new WebClient();
			HtmlPage page = webClient.getPage(url);
			
			// important!  Give the headless browser enough time to execute JavaScript
			// The exact time to wait may depend on your application.
			webClient.waitForBackgroundJavaScript(4000);
			
			// return the snapshot
			PrintWriter out = response.getWriter();
			out.println(page.asXml());
		} 
		else 
		{
			try
            {
	            chain.doFilter(request, response);
            }
            catch (ServletException e)
            {
	            logger.error("Error processing request", e);
            }
		}
	}

	private String rewriteUrl(ServletRequest request)
    {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }

	@Override
    public void destroy()
    {
    }
}
