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
package br.com.sysmap.crux.core.client.screen.children;

import br.com.sysmap.crux.core.client.screen.WidgetFactory.WidgetFactoryContext;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class WidgetChildProcessorContext
{
	private CruxMetaDataElement childElement;
	
	private WidgetFactoryContext context;
	
	public WidgetChildProcessorContext(WidgetFactoryContext context)
	{
		this.context = context;
	}

	@SuppressWarnings("unchecked")
	public <W> W getRootWidget()
	{
		return (W) context.getWidget();
	}

	public CruxMetaDataElement getRootElement()
	{
		return context.getElement();
	}

	public String getRootWidgetId()
	{
		return context.getWidgetId();
	}

	public String readRootWidgetProperty(String propertyName)
	{
		return context.getElement().getProperty(propertyName);
	}
	
	public String readChildProperty(String propertyName)
	{
		return childElement.getProperty(propertyName);
	}
	
	public CruxMetaDataElement getChildElement()
	{
		return childElement;
	}
	
	public Object getAttribute(String key)
	{
		return context.getAttribute(key);
	}

	public boolean containsAttribute(String key)
	{
		return context.containsAttribute(key);
	}
	
	public void setChildElement(CruxMetaDataElement childElement)
	{
		this.childElement = childElement;
	}
	
	public void setAttribute(String key, Object value)
	{
		this.context.setAttribute(key, value);
	}
	
	public void clearAttributes()
	{
		this.context.clearAttributes();
	}

	public void removeAttribute(String key)
	{
		this.context.removeAttribute(key);
	}
}
