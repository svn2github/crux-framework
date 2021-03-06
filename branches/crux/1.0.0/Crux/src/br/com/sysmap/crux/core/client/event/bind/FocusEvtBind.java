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
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;

/**
 * Helper Class for focus events binding
 * @author Thiago Bustamante
 *
 */
public class FocusEvtBind extends EvtBind
{
	public static void bindEvents(Element element, HasAllFocusHandlers widget)
	{
		final Event eventFocus = getWidgetEvent(element, Events.EVENT_FOCUS);
		if (eventFocus != null)
		{
			widget.addFocusHandler(new FocusHandler()
			{
				public void onFocus(FocusEvent event) 
				{
					Events.callEvent(eventFocus, event);
				}
			});
		}
		
		final Event eventBlur = getWidgetEvent(element, Events.EVENT_BLUR);
		if (eventBlur != null)
		{
			widget.addBlurHandler(new BlurHandler()
			{
				public void onBlur(BlurEvent event) 
				{
					Events.callEvent(eventBlur, event);
				}
			});
		}
	}
}
