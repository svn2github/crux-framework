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
package br.com.sysmap.crux.widgets.client.event.row;

import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.rebind.widget.EvtProcessor;

/**
 * All event binders for grid row events
 * @author Gesse S. F. Dafe
 */
public class RowEventsBind
{
	/**
	 * @author Gesse S. F. Dafe
	 */
	public static class RowClickEvtBind implements EvtProcessor<HasRowClickHandlers>
	{
		private static final String EVENT_NAME = "onrowclick";

		/**
		 * @see br.com.sysmap.crux.core.rebind.widget.EvtProcessor#bindEvent(br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement, java.lang.Object)
		 */
		public void bindEvent(CruxMetaDataElement element, HasRowClickHandlers widget)
		{
			final Event eventRowClick = EvtBind.getWidgetEvent(element, EVENT_NAME);
			if (eventRowClick != null)
			{
				RowClickHandler handler = new RowClickHandler()
				{
					public void onRowClick(RowClickEvent event)
					{
						Events.callEvent(eventRowClick, event);						
					}
				};
				
				widget.addRowClickHandler(handler);
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
	
	/**
	 * @author Gesse S. F. Dafe
	 */
	public static class RowDoubleClickEvtBind implements EvtProcessor<HasRowDoubleClickHandlers>
	{
		private static final String EVENT_NAME = "onrowdoubleclick";

		/**
		 * @see br.com.sysmap.crux.core.rebind.widget.EvtProcessor#bindEvent(br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement, java.lang.Object)
		 */
		public void bindEvent(CruxMetaDataElement element, HasRowDoubleClickHandlers widget)
		{
			final Event rowEvent = EvtBind.getWidgetEvent(element, EVENT_NAME);
			if (rowEvent != null)
			{
				RowDoubleClickHandler handler = new RowDoubleClickHandler()
				{
					public void onRowDoubleClick(RowDoubleClickEvent event)
					{
						Events.callEvent(rowEvent, event);						
					}
				};
				
				widget.addRowDoubleClickHandler(handler);
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
	
	/**
	 * @author Gesse S. F. Dafe
	 */
	public static class RowRenderEvtBind implements EvtProcessor<HasRowRenderHandlers>
	{
		private static final String EVENT_NAME = "onrowrender";

		/**
		 * @see br.com.sysmap.crux.core.rebind.widget.EvtProcessor#bindEvent(br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement, java.lang.Object)
		 */
		public void bindEvent(CruxMetaDataElement element, HasRowRenderHandlers widget)
		{
			final Event rowEvent = EvtBind.getWidgetEvent(element, EVENT_NAME);
			if (rowEvent != null)
			{
				RowRenderHandler handler = new RowRenderHandler()
				{
					public void onRowRender(RowRenderEvent event)
					{
						Events.callEvent(rowEvent, event);						
					}
				};
				
				widget.addRowRenderHandler(handler);
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
	
	/**
	 * @author Gesse S. F. Dafe
	 */
	public static class BeforeRowSelectEvtBind implements EvtProcessor<HasBeforeRowSelectHandlers>
	{
		private static final String EVENT_NAME = "onbeforerowselect";

		/**
		 * @see br.com.sysmap.crux.core.rebind.widget.EvtProcessor#bindEvent(br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement, java.lang.Object)
		 */
		public void bindEvent(CruxMetaDataElement element, HasBeforeRowSelectHandlers widget)
		{
			final Event rowEvent = EvtBind.getWidgetEvent(element, EVENT_NAME);
			if (rowEvent != null)
			{
				BeforeRowSelectHandler handler = new BeforeRowSelectHandler()
				{
					public void onBeforeRowSelect(BeforeRowSelectEvent event)
					{
						Events.callEvent(rowEvent, event);
					}
				};
				
				widget.addBeforeRowSelectHandler(handler);
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
}