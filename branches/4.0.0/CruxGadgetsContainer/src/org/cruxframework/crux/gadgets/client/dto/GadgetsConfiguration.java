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
package org.cruxframework.crux.gadgets.client.dto;

import com.google.gwt.core.client.JsArray;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class GadgetsConfiguration
{
	public static enum ContainerView
	{
		profile("profile"),
		canvas("canvas");
		
		private String viewName;
		ContainerView(String viewName)
		{
			this.viewName = viewName;
		}
		
		@Override
		public String toString()
		{
		    return viewName;
		}
	}
	
	private boolean cacheEnabled = true;
	private boolean debug = false;
	private JsArray<JsArray<GadgetMetadata>> metadata;
	private boolean cajaEnabled = false;
	private String containerParentUrl = "http://localhost:8080/";
	private String containerUrl;
	private String country;
	private String language;
	private ContainerView currentView;
	private String gadgetUrl;
	
	public JsArray<JsArray<GadgetMetadata>> getMetadata()
    {
    	return metadata;
    }

	public void setMetadata(JsArray<JsArray<GadgetMetadata>> metadata)
    {
    	this.metadata = metadata;
    }

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}
	
	public boolean isDebug()
    {
	    return debug;
    }

	public void setCajaEnabled(boolean cajaEnable)
	{
		this.cajaEnabled = cajaEnable;
	}
	
	public boolean isCajaEnabled()
    {
	    return cajaEnabled;
    }

	public String getContainerParentUrl()
    {
	    return containerParentUrl;
    }

	public void setContainerParentUrl(String containerParentUrl)
    {
    	this.containerParentUrl = containerParentUrl;
    }

	public String getCountry()
    {
    	return country;
    }

	public void setCountry(String country)
    {
    	this.country = country;
    }

	public String getLanguage()
    {
    	return language;
    }

	public void setLanguage(String language)
    {
    	this.language = language;
    }

	public ContainerView getCurrentView()
    {
    	return currentView;
    }

	public void setCurrentView(ContainerView currentView)
    {
    	this.currentView = currentView;
    }

	public String getGadgetUrl()
    {
    	return gadgetUrl;
    }

	public void setGadgetUrl(String gadgetUrl)
    {
    	this.gadgetUrl = gadgetUrl;
    }

	public void setContainerUrl(String containerUrl)
    {
	   this.containerUrl = containerUrl;
    }

	public String getContainerUrl()
	{
		return containerUrl;
	}

	public boolean isCacheEnabled()
    {
    	return cacheEnabled;
    }

	public void setCacheEnabled(boolean cacheEnabled)
    {
    	this.cacheEnabled = cacheEnabled;
    }
	
	
}
