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
package br.com.sysmap.crux.core.client.event.bind;

import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

/**
 * Helper Class for mouse events binding
 * @author Thiago Bustamante
 *
 */
public class MouseOverEvtBind implements EvtBinder<HasMouseOverHandlers>
{
	private static final String EVENT_NAME = "onMouseOver";

	/**
	 * @see br.com.sysmap.crux.core.client.event.bind.EvtBinder#bindEvent(com.google.gwt.dom.client.Element, com.google.gwt.event.shared.HasHandlers)
	 */
	public void bindEvent(Element element, HasMouseOverHandlers widget)
	{
		final Event eventMouseOver = EvtBind.getWidgetEvent(element, EVENT_NAME);
		if (eventMouseOver != null)
		{
			widget.addMouseOverHandler(new MouseOverHandler()
			{
				public void onMouseOver(MouseOverEvent event) 
				{
					Events.callEvent(eventMouseOver, event);					
				}
			});
		}
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.event.bind.EvtBinder#getEventName()
	 */
	public String getEventName()
	{
		return EVENT_NAME;
	}	
}
