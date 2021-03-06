/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.classpath;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class URLResourceHandlersRegistry
{
	private static final Map<String, URLResourceHandler> resourceHandlers = new HashMap<String, URLResourceHandler>();
	
	static
	{
		registerURLResourceHandler(new FileURLResourceHandler());
		registerURLResourceHandler(new JARURLResourceHandler());
		registerURLResourceHandler(new ZIPURLResourceHandler());
	}
	
	public static URLResourceHandler getURLResourceHandler(String protocol)
	{
		return resourceHandlers.get(protocol);
	}
	
	public static void registerURLResourceHandler(URLResourceHandler resourceHandler)
	{
		if (resourceHandler != null && resourceHandler.getProtocol() != null)
		{
			resourceHandlers.put(resourceHandler.getProtocol(), resourceHandler);
		}
	}
}
