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
import br.com.sysmap.crux.widgets.client.event.row.BeforeRowSelectEvent;
import br.com.sysmap.crux.widgets.client.event.row.BeforeRowSelectHandler;
import br.com.sysmap.crux.widgets.client.event.row.RowClickEvent;
import br.com.sysmap.crux.widgets.client.event.row.RowClickHandler;
import br.com.sysmap.crux.widgets.client.event.row.RowDoubleClickEvent;
import br.com.sysmap.crux.widgets.client.event.row.RowDoubleClickHandler;
import br.com.sysmap.crux.widgets.client.event.row.RowRenderEvent;
import br.com.sysmap.crux.widgets.client.event.row.RowRenderHandler;

/**
 * All event binders for grid row events
 * @author Gesse S. F. Dafe
 */
public class RowEventsBind
{
	/**
	 * @author Gesse S. F. Dafe
	 */
	public static class RowClickEvtBind extends EvtProcessor
	{
		public RowClickEvtBind(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		private static final String EVENT_NAME = "onRowClick";

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
		    return RowClickEvent.class;
	    }

		@Override
	    public Class<?> getEventHandlerClass()
	    {
		    return RowClickHandler.class;
	    }		
	}
	
	/**
	 * @author Gesse S. F. Dafe
	 */
	public static class RowDoubleClickEvtBind extends EvtProcessor
	{
		public RowDoubleClickEvtBind(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		private static final String EVENT_NAME = "onRowDoubleClick";

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
		    return RowDoubleClickEvent.class;
	    }

		@Override
	    public Class<?> getEventHandlerClass()
	    {
		    return RowDoubleClickHandler.class;
	    }		
	}
	
	/**
	 * @author Gesse S. F. Dafe
	 */
	public static class RowRenderEvtBind extends EvtProcessor
	{
		public RowRenderEvtBind(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		private static final String EVENT_NAME = "onRowRender";

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
		    return RowRenderEvent.class;
	    }

		@Override
	    public Class<?> getEventHandlerClass()
	    {
		    return RowRenderHandler.class;
	    }		
	}
	
	/**
	 * @author Gesse S. F. Dafe
	 */
	public static class BeforeRowSelectEvtBind extends EvtProcessor
	{
		public BeforeRowSelectEvtBind(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		private static final String EVENT_NAME = "onBeforeRowSelect";

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
		    return BeforeRowSelectEvent.class;
	    }

		@Override
	    public Class<?> getEventHandlerClass()
	    {
		    return BeforeRowSelectHandler.class;
	    }		
	}
}