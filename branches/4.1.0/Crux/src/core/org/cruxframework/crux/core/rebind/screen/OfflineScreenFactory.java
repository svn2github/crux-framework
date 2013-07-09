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
package org.cruxframework.crux.core.rebind.screen;

import java.io.InputStream;
import java.net.URL;

import org.cruxframework.crux.core.utils.XMLUtils;
import org.cruxframework.crux.core.utils.XMLUtils.XMLException;
import org.cruxframework.crux.scannotation.URLStreamManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class OfflineScreenFactory
{
	private static OfflineScreenFactory instance = new OfflineScreenFactory();
	
	/**
	 * Singleton Constructor
	 */
	private OfflineScreenFactory() 
	{
	}
	
	/**
	 * Singleton method
	 * @return
	 */
	public static OfflineScreenFactory getInstance()
	{
		return instance;
	}
	
	public OfflineScreen getOfflineScreen(URL screenURL) throws ScreenConfigException
	{
		URLStreamManager manager = new URLStreamManager(screenURL);
		InputStream stream = manager.open();
		try
        {
	        Document screen = XMLUtils.createNSUnawareDocument(stream);
	        return parseDocument(screenURL.toString(), screen);
        }
        catch (XMLException e)
        {
	        throw new ScreenConfigException("Error reading offline screen.", e);
        }
        finally
        {
        	manager.close();
        }
	}

	private OfflineScreen parseDocument(String id, Document screen)
    {
		Element screenElement = screen.getDocumentElement();
		String moduleName = screenElement.getAttribute("moduleName");
		String screenId = screenElement.getAttribute("screenId");
		OfflineScreen result = new OfflineScreen(id, moduleName, screenId);

		NodeList nodes = screenElement.getChildNodes();
		for (int i=0; i < nodes.getLength(); i++)
		{
			Node item = nodes.item(i);
			if (item instanceof Element)
			{
				String resourceName = ((Element)item).getAttribute("resourceName");
				result.addOfflineResource(resourceName);
			}
		}
	    
	    return result;
    }

}
