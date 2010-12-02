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
package br.com.sysmap.crux.widgets.client.timer;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.WidgetFactoryContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.widgets.client.event.timeout.TimeoutEvtBind;

/**
 * Factory for Timer widget
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="timer", library="widgets")
public class TimerFactory extends WidgetFactory<Timer, WidgetFactoryContext>
{
	/**
	 * @see br.com.sysmap.crux.core.client.screen.WidgetFactory#instantiateWidget(com.google.gwt.dom.client.Element, java.lang.String)
	 */
	public Timer instantiateWidget(CruxMetaDataElement element, String widgetId) throws InterfaceConfigException
	{
		long initial = 0;
		boolean regressive = false;
		boolean start = false;
		
		String strInitial = element.getProperty("initial");  
		if(strInitial != null && strInitial.trim().length() > 0)
		{
			initial = Long.parseLong(strInitial);
		}
		
		String strRegressive = element.getProperty("regressive");  
		if(strRegressive != null && strRegressive.trim().length() > 0)
		{
			regressive = Boolean.parseBoolean(strRegressive);
		}
		
		String strStart = element.getProperty("start");  
		if(strStart != null && strStart.trim().length() > 0)
		{
			start = Boolean.parseBoolean(strStart);
		}
		
		return new Timer(initial, regressive, element.getProperty("pattern"), start);
	}
	
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="start", type=Boolean.class, defaultValue="false"),
		@TagAttributeDeclaration(value="initial", type=Integer.class, defaultValue="0"),
		@TagAttributeDeclaration(value="regressive", type=Boolean.class, defaultValue="false")
	})
	@TagAttributes({
		@TagAttribute(value="pattern")
	})
	public void processAttributes(WidgetFactoryContext context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	@Override
	@TagChildren({
		@TagChild(TimerChildrenProcessor.class)
	})
	public void processChildren(WidgetFactoryContext context) throws InterfaceConfigException {}
	
	@TagChildAttributes(tagName="onTimeout", minOccurs="0", maxOccurs="unbounded")
	public static class TimerChildrenProcessor extends WidgetChildProcessor<Timer, WidgetFactoryContext>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="time", required=true, type=Integer.class),
			@TagAttributeDeclaration(value="execute", required=true)
		})
		public void processChildren(WidgetFactoryContext context) throws InterfaceConfigException
		{
			Timer rootWidget = context.getWidget();
			TimeoutEvtBind.bindEventForChildTag(context.getChildElement(), rootWidget);
		}
	}
}