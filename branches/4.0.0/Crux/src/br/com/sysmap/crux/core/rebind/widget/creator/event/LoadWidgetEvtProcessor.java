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
package br.com.sysmap.crux.core.rebind.widget.creator.event;

import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.rebind.widget.EvtProcessor;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;

/**
 * Helper Class for widget load events binding
 * @author Thiago Bustamante
 *
 */
public class LoadWidgetEvtProcessor extends EvtProcessor
{
	private static final String EVENT_NAME = "onLoadWidget";

	/**
	 * @see br.com.sysmap.crux.core.rebind.widget.EvtProcessor#processEvent(br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter, br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext, java.lang.String)
	 */
	public void processEvent(SourcePrinter out, WidgetCreatorContext context, String eventValue)
	{
		String event = ViewFactoryCreator.createVariableName("evt");
		printlnPostProcessing("Event "+event+" = Events.getEvent("+EscapeUtils.quote(getEventName())+", "+EscapeUtils.quote(eventValue)+");");
		printlnPostProcessing("Events.callEvent("+event+", new WidgetLoadEvent("+context.getWidget()+","+EscapeUtils.quote(context.getWidgetId())+"));");
	}

	/**
	 * @see br.com.sysmap.crux.core.rebind.widget.EvtProcessor#getEventName()
	 */
	public String getEventName()
	{
		return EVENT_NAME;
	}	
}
