/*
 * Copyright 2011 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.rebind.screen.widget.creator.event;

import br.com.sysmap.crux.core.rebind.screen.widget.EvtProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreator;

import com.google.gwt.view.client.CellPreviewEvent;

/**
 * Helper Class for cell preview events binding
 * @author Thiago Bustamante
 *
 */
public class CellPreviewEvtBind extends EvtProcessor
{
	public CellPreviewEvtBind(WidgetCreator<?> widgetCreator)
    {
	    super(widgetCreator);
    }

	private static final String EVENT_NAME = "onCellPreview";

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
	    return CellPreviewEvent.class;
    }

	@Override
    public Class<?> getEventHandlerClass()
    {
	    return CellPreviewEvent.Handler.class;
    }
}
