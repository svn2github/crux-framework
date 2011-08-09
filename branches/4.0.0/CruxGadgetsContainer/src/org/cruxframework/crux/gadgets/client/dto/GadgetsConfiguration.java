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
	private boolean debug = true;
	private JsArray<JsArray<GadgetMetadata>> metadata;
	private boolean caja = false;
	private String containerParentUrl = "http://localhost:8080/";

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

	public void setUseCaja(boolean caja)
	{
		this.caja = caja;
	}
	
	public boolean useCaja()
    {
	    return caja;
    }

	public String getContainerParentUrl()
    {
	    return containerParentUrl;
    }

	public void setContainerParentUrl(String containerParentUrl)
    {
    	this.containerParentUrl = containerParentUrl;
    }
}
