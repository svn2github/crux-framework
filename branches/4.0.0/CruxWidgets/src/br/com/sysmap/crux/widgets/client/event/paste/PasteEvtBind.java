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
package br.com.sysmap.crux.widgets.client.event.paste;

import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.rebind.widget.EvtProcessor;

public class PasteEvtBind implements EvtProcessor<HasPasteHandlers>
{
	private static final String EVENT_NAME = "onPaste";

	/**
	 * @param element
	 * @param widget
	 */
	public void bindEvent(CruxMetaDataElement element, HasPasteHandlers widget)
	{
		final Event pasteEvent = EvtBind.getWidgetEvent(element, EVENT_NAME);
		
		if (pasteEvent != null)
		{
			widget.addPasteHandler(new PasteHandler()
			{
				public void onPaste(PasteEvent event)
				{
					br.com.sysmap.crux.core.client.event.Events.callEvent(pasteEvent, event);
				}
			});
		}
	}

	/**
	 * @see br.com.sysmap.crux.core.rebind.widget.EvtProcessor#getEventName()
	 */
	public String getEventName()
	{
		return EVENT_NAME;
	}
}
