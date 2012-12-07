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
package org.cruxframework.cruxsite.client.device;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Size;
import org.cruxframework.crux.core.client.screen.Screen;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LinkElement;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CssLoader
{
	/**
	 * 
	 * @param version
	 */
	public static void loadCssForDevice(String version)
    {
	    String cssRef;
	    if (Screen.getCurrentDevice().getSize().equals(Size.small))
	    {
	    	cssRef = "css/site-small.css?v="+version;//TODO colocar o -sprite
	    }
	    else
	    {
	    	cssRef = "css/site-large.css?v="+version;
	    }
	    
	    LinkElement linkElement = Document.get().createLinkElement();
	    linkElement.setRel("stylesheet");
	    linkElement.setType("text/css");
	    linkElement.setHref(cssRef);
	    
	    Document.get().getElementsByTagName("head").getItem(0).appendChild(linkElement);
    }
}
