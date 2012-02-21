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
package org.cruxframework.crux.core.client.screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.StyleElement;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DeviceDisplayHandler
{
	/**
	 * Normalize the zoom level to ensure that the page will be displayed with the same relative width on different devices.
	 */
	public static void configureDisplayForDevice()
	{
		DisplayHandler displayHandler = GWT.create(DisplayHandler.class); 
//		Window.alert("User Agent:"+Window.Navigator.getUserAgent().toLowerCase());
//		Window.alert("Platform:"+Screen.getPlatform().toString());
//		Window.alert("Device:"+Screen.getCurrentDevice().toString());
		displayHandler.configureScreenZoom();
	}

	static class DisplayHandler
	{
		void configureScreenZoom()
		{
			
		}
	}
	
	static class SmallDisplayHandler extends DisplayHandler
	{
		@Override
		void configureScreenZoom()
		{
			double pixelRatio = getDevicePixelRatio();
//			Window.alert("Pixel Ratio:"+pixelRatio);
			if (pixelRatio > 0)
			{
				StyleContentInjector styleContentInjector = GWT.create(StyleContentInjector.class);
				if (pixelRatio >= 2)
				{
					createStyleElementForHighDensityDevices(styleContentInjector);
				}
				else if (pixelRatio >= 1.5)
				{
					createStyleElementForMediumDensityDevices(styleContentInjector);
				}
				else if (pixelRatio >= 1.0)
				{
					createStyleElementForLowDensityDevices(styleContentInjector);
				}
				else if (pixelRatio < 1.0)
				{
					createStyleElementForVeryLowDensityDevices(styleContentInjector);
				}
			}
		}
		
		/**
		 * 
		 * @return
		 */
		private native double getDevicePixelRatio()/*-{
			if ($wnd.devicePixelRatio)
			{
				return $wnd.devicePixelRatio;
			}
			return -1;
		}-*/;
		
		/**
		 * 
		 * @param styleContentInjector
		 */
		private void createStyleElementForHighDensityDevices(StyleContentInjector styleContentInjector)
	    {
		    StyleElement styleElement = Document.get().createStyleElement();
			styleElement.setType("text/css");
			styleContentInjector.injectContent(styleElement,"HTML{zoom: 0.667;}");
			
			Document.get().getElementsByTagName("head").getItem(0).appendChild(styleElement);
	    }
		
		/**
		 * 
		 * @param styleContentInjector
		 */
		private void createStyleElementForMediumDensityDevices(StyleContentInjector styleContentInjector)
	    {
		    StyleElement styleElement = Document.get().createStyleElement();
			styleElement.setType("text/css");
			styleContentInjector.injectContent(styleElement,"HTML{zoom: 1.0;}");
			
			Document.get().getElementsByTagName("head").getItem(0).appendChild(styleElement);
	    }

		/**
		 * 
		 * @param styleContentInjector
		 */
		private void createStyleElementForLowDensityDevices(StyleContentInjector styleContentInjector)
	    {
		    StyleElement styleElement = Document.get().createStyleElement();
			styleElement.setType("text/css");
			styleContentInjector.injectContent(styleElement,"HTML{zoom: 0.667;}");
			
			Document.get().getElementsByTagName("head").getItem(0).appendChild(styleElement);
	    }

		/**
		 * 
		 * @param styleContentInjector
		 */
		private void createStyleElementForVeryLowDensityDevices(StyleContentInjector styleContentInjector)
	    {
		    StyleElement styleElement = Document.get().createStyleElement();
			styleElement.setType("text/css");
			styleContentInjector.injectContent(styleElement,"HTML{zoom: 0.5;}");
			
			Document.get().getElementsByTagName("head").getItem(0).appendChild(styleElement);
	    }
	}

	static class StyleContentInjector
	{
		void injectContent(StyleElement element, String content)
		{
			element.setInnerText(content);
		}
	}

	static class IEStyleContentInjector extends StyleContentInjector
	{
		void injectContent(StyleElement element, String content)
		{
			element.setCssText(content);
		}
	}	
}
