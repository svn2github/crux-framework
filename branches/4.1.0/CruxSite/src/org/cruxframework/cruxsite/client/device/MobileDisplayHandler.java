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

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Input;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Size;
import org.cruxframework.crux.core.client.screen.Screen;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.MetaElement;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class MobileDisplayHandler
{

	/**
	 * 
	 * @param appVersion
	 */
	public static void configureMetatags(String appVersion)
    {
		if (Screen.getCurrentDevice().getSize().equals(Size.small))
		{
			createViewport("user-scalable=no, width=320");	    
		}
		else if (!Screen.getCurrentDevice().getInput().equals(Input.mouse))
		{
			createViewport("user-scalable=no, width=device-width, height=device-height");	    
		}
    }

	private static void createViewport(String content)
	{
		MetaElement viewport = Document.get().createMetaElement();
		viewport.setContent(content);
		viewport.setName("viewport");
		Document.get().getElementsByTagName("head").getItem(0).appendChild(viewport);
	}
}
