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
package br.com.sysmap.crux.advanced.client.timer;

import java.util.List;

import br.com.sysmap.crux.advanced.client.event.timeout.TimeoutEvtBind;
import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;

import com.google.gwt.dom.client.Element;

/**
 * Factory for Timer widget
 * @author Gess� S. F. Daf� - <code>gessedafe@gmail.com</code>
 */
@DeclarativeFactory(id="timer", library="adv")
public class TimerFactory<T extends Timer> extends WidgetFactory<Timer>
{
	/**
	 * @see br.com.sysmap.crux.core.client.screen.WidgetFactory#instantiateWidget(com.google.gwt.dom.client.Element, java.lang.String)
	 */
	protected Timer instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		long initial = 0;
		boolean regressive = false;
		boolean start = false;
		
		String strInitial = element.getAttribute("_initial");  
		if(strInitial != null && strInitial.trim().length() > 0)
		{
			initial = Long.parseLong(strInitial);
		}
		
		String strRegressive = element.getAttribute("_regressive");  
		if(strRegressive != null && strRegressive.trim().length() > 0)
		{
			regressive = Boolean.parseBoolean(strRegressive);
		}
		
		String strStart = element.getAttribute("_start");  
		if(strStart != null && strStart.trim().length() > 0)
		{
			start = Boolean.parseBoolean(strStart);
		}
		
		return new Timer(initial, regressive, start);
	}
	
	
	@Override
	protected void processAttributes(Timer widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);
		
		List<Element> children = ensureChildrenSpans(element, true);
		for (Element child : children)
		{
			TimeoutEvtBind.bindEventForChildTag(child, widget);
		}		
	}
}