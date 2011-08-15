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
	private JsArray<JsArray<GadgetMetadata>> metadata;
	private boolean cajaEnabled = false;
	private String containerUrl;
	
	public JsArray<JsArray<GadgetMetadata>> getMetadata()
    {
    	return metadata;
    }

	public void setMetadata(JsArray<JsArray<GadgetMetadata>> metadata)
    {
    	this.metadata = metadata;
    }

	public void setCajaEnabled(boolean cajaEnable)
	{
		this.cajaEnabled = cajaEnable;
	}
	
	public boolean isCajaEnabled()
    {
	    return cajaEnabled;
    }

	public void setContainerUrl(String containerUrl)
    {
	   this.containerUrl = containerUrl;
    }

	public String getContainerUrl()
	{
		return containerUrl;
	}

	public GadgetContainer getContainer()
	{
		return GadgetContainer.get();
	}
}
