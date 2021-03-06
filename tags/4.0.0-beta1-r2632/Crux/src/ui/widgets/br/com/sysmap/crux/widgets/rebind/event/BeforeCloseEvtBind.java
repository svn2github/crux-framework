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
package br.com.sysmap.crux.widgets.rebind.event;

import br.com.sysmap.crux.core.rebind.screen.widget.EvtProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreator;
import br.com.sysmap.crux.widgets.client.event.openclose.BeforeCloseEvent;
import br.com.sysmap.crux.widgets.client.event.openclose.BeforeCloseHandler;

public class BeforeCloseEvtBind extends EvtProcessor
{
	public BeforeCloseEvtBind(WidgetCreator<?> widgetCreator)
    {
	    super(widgetCreator);
    }

	private static final String EVENT_NAME = "onBeforeClose";

	/**
	 * @see br.com.sysmap.crux.core.rebind.screen.widget.EvtProcessor#getEventName()
	 */
	public String getEventName()
	{
		return EVENT_NAME;
	}

	@Override
    public Class<?> getEventClass()
    {
	    return BeforeCloseEvent.class;
    }

	@Override
    public Class<?> getEventHandlerClass()
    {
	    return BeforeCloseHandler.class;
    }		
}
