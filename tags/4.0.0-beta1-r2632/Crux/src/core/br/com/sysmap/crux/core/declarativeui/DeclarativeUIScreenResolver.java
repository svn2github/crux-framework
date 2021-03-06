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
package br.com.sysmap.crux.core.declarativeui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

import br.com.sysmap.crux.classpath.URLResourceHandler;
import br.com.sysmap.crux.classpath.URLResourceHandlersRegistry;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.ScreenConfigException;
import br.com.sysmap.crux.core.rebind.screen.ScreenResourceResolver;
import br.com.sysmap.crux.core.server.classpath.ClassPathResolverInitializer;
import br.com.sysmap.crux.core.utils.RegexpPatterns;
import br.com.sysmap.crux.core.utils.URLUtils;
import br.com.sysmap.crux.scannotation.URLStreamManager;

/**
 * Custom screen resolver to handle crux HTML tags 
 * @author Gesse S. F. Dafe
 */
public class DeclarativeUIScreenResolver implements ScreenResourceResolver
{
	DeclarativeUIMessages messages = MessagesFactory.getMessages(DeclarativeUIMessages.class);	

	/**
	 * 
	 */
	public Set<String> getAllScreenIDs(String module) throws ScreenConfigException
	{
		return  new DeclarativeUIScreenResourceScanner().getPages(module);
	}

	/**
	 * 
	 */
	public InputStream getScreenResource(String screenId, boolean escapeXML, boolean generateWidgetsMetadata) throws CruxGeneratorException
	{
		try
		{
			URL[] webBaseDirs = ClassPathResolverInitializer.getClassPathResolver().findWebBaseDirs();
			URL screenURL = null;
			InputStream inputStream = null;
			URLStreamManager manager = null;
			
			for (URL webBaseDir: webBaseDirs)
			{
				URLResourceHandler resourceHandler = URLResourceHandlersRegistry.getURLResourceHandler(webBaseDir.getProtocol());

				screenId = RegexpPatterns.REGEXP_BACKSLASH.matcher(screenId).replaceAll("/").replace(".html", ".crux.xml");
				screenURL = resourceHandler.getChildResource(webBaseDir, screenId);
				manager = new URLStreamManager(screenURL);
				inputStream = manager.open();
				if (inputStream != null)
				{
					break;
				}
				else
				{
					manager.close(); // the possible underlying jar must be closed despite of the existence of the referred resource
				}
			}	
			
			if (inputStream == null)
			{
				
				screenURL = URLUtils.isValidURL(screenId);
				
				if (screenURL == null)
				{
					screenURL = new URL("file:///"+screenId);
				}
				
				manager = new URLStreamManager(screenURL);
				inputStream = manager.open();
				
				if (inputStream == null)
				{
					manager.close();
					
					screenURL = getClass().getResource("/"+screenId);
					if (screenURL != null)
					{
						manager = new URLStreamManager(screenURL);
						inputStream = manager.open();
					}
				}
			}

			if (inputStream == null)
			{
				return null;
			}
			
			InputStream result = performTransformation(screenId, inputStream, escapeXML, generateWidgetsMetadata);
			
			if(manager != null)
			{
				manager.close();
			}
			
			return result;			
		}
		catch (Exception e)
		{
			throw new CruxGeneratorException(messages.declarativeUIScreenResolverError(screenId, e.getMessage()), e);
		}
	}

	/**
	 * @param screenId
	 * @param inputStream
	 * @param escapeXML
	 * @param generateWidgetsMetadata
	 * @return
	 * @throws IOException
	 */
	protected InputStream performTransformation(String screenId, InputStream inputStream, boolean escapeXML, boolean generateWidgetsMetadata) 
				throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		CruxToHtmlTransformer.generateHTML(screenId, inputStream, out, escapeXML, generateWidgetsMetadata);			
		return new ByteArrayInputStream(out.toByteArray());
	}

	public InputStream getScreenXMLResource(String screenId) throws CruxGeneratorException
    {
	    return getScreenResource(screenId, true, true);
    }

	public InputStream getScreenResource(String screenId) throws CruxGeneratorException
    {
	    return getScreenResource(screenId, false, false);
    }
	
	
}
