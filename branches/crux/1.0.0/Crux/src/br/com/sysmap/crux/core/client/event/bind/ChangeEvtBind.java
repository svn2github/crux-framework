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
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

/**
 * Helper Class for change events binding
 * @author Thiago Bustamante
 *
 */
public class ChangeEvtBind extends EvtBind
{
	public static <I> void bindValueEvent(Element element, HasValueChangeHandlers<I> widget)
	{
		final Event eventChange = getWidgetEvent(element, Events.EVENT_CHANGE);
		if (eventChange != null)
		{
			widget.addValueChangeHandler(new ValueChangeHandler<I>()
			{
				public void onValueChange(ValueChangeEvent<I> event) 
				{
					Events.callEvent(eventChange, event);
				}
			});
		}
	}

	public static void bindEvent(Element element, HasChangeHandlers widget)
	{
		final Event eventChange = getWidgetEvent(element, Events.EVENT_CHANGE);
		if (eventChange != null)
		{
			widget.addChangeHandler(new ChangeHandler()
			{
				public void onChange(ChangeEvent event) 
				{
					Events.callEvent(eventChange, event);
				}
			});
		}
	}
}
