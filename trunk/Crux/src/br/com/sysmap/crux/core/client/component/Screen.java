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
package br.com.sysmap.crux.core.client.component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.EventFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstraction for the entire page. It encapsulate all the widgets, containers and 
 * datasources.
 * @author Thiago
 *
 */
public class Screen
{
	protected String id;
	protected Map<String, Widget> widgets = new HashMap<String, Widget>(30);
	protected Element blockDiv;
	protected boolean manageHistory = false;
	protected IFrameElement historyFrame = null;
	protected HandlerManager handlerManager;
	
	public Screen(String id) 
	{
		this.id = id;
		this.handlerManager = new HandlerManager(this);
	}
	
	public String getId() 
	{
		return id;
	}
	
	public boolean isManageHistory() {
		return manageHistory;
	}

	public void setManageHistory(boolean manageHistory) {
		if (this.manageHistory != manageHistory)
		{
			this.manageHistory = manageHistory;
			Element body = RootPanel.getBodyElement();
			if (manageHistory)
			{
				if (historyFrame == null)
				{
					historyFrame = DOM.createIFrame().cast();
					historyFrame.setSrc("javascript:''");
					historyFrame.setId("__gwt_historyFrame");
					historyFrame.getStyle().setProperty("width", "0");
					historyFrame.getStyle().setProperty("height", "0");
					historyFrame.getStyle().setProperty("border", "0");
					body.appendChild(historyFrame);
				}
			}
			else
			{
				if (historyFrame != null)
				{
					body.removeChild(historyFrame);
					historyFrame = null;
				}			
			}
		}
	}

	public Widget getWidget(String id)
	{
		return widgets.get(id);
	}
	
	void addWidget(String id, Widget widget)
	{
		widgets.put(id, widget);
	}

	public Iterator<String> iteratorWidgetsIds()
	{
		return widgets.keySet().iterator();
	}
	
	public Iterator<Widget> iteratorWidgets()
	{
		return widgets.values().iterator();
	}

	public void blockToUser()
	{
		if (blockDiv == null)
		{
			blockDiv = DOM.createDiv();
			blockDiv.getStyle().setProperty("position","absolute");
			blockDiv.getStyle().setPropertyPx("top", 0);
			blockDiv.getStyle().setPropertyPx("left", 0);
			blockDiv.getStyle().setPropertyPx("height", Window.getClientHeight());
			blockDiv.getStyle().setPropertyPx("width", Window.getClientWidth());
			blockDiv.getStyle().setProperty("zIndex", "99999");
			blockDiv.getStyle().setProperty("backgroundColor", "white");
			blockDiv.getStyle().setProperty("opacity", ".01");
			blockDiv.getStyle().setProperty("cursor", "wait");
			blockDiv.getStyle().setProperty("filter", "alpha(opacity=1)");
			Element body = RootPanel.getBodyElement();
			body.appendChild(blockDiv);
			body.getStyle().setProperty("cursor", "wait");
		}
	}
	
	public void unblockToUser()
	{
		if (blockDiv != null)
		{
			Element body = RootPanel.getBodyElement();
			body.removeChild(blockDiv);
			blockDiv = null;
			body.getStyle().setProperty("cursor", "");
		}
	}
	
	protected void parse(Element element) 
	{
		String manageHistoryStr = element.getAttribute("_manageHistory");
		if (manageHistoryStr != null)
		{
			setManageHistory("true".equals(manageHistoryStr));
		}
		String title = element.getAttribute("_title");
		if (title != null && title.length() >0)
		{
			Window.setTitle(ScreenFactory.getInstance().getDeclaredMessage(title));
		}
		final Event eventClosing = EventFactory.getEvent(EventFactory.EVENT_CLOSING, element.getAttribute(EventFactory.EVENT_CLOSING));
		if (eventClosing != null)
		{
			Window.addWindowClosingHandler(new Window.ClosingHandler(){
				public void onWindowClosing(ClosingEvent closingEvent) 
				{
					EventFactory.callEvent(eventClosing, closingEvent);
				}
			});
		}

		final Event eventClose = EventFactory.getEvent(EventFactory.EVENT_CLOSE, element.getAttribute(EventFactory.EVENT_CLOSE));
		if (eventClose != null)
		{
			Window.addCloseHandler(new CloseHandler<Window>(){
				public void onClose(CloseEvent<Window> event) 
				{
					EventFactory.callEvent(eventClose, event);				
				}
			});
		}

		final Event eventResized = EventFactory.getEvent(EventFactory.EVENT_RESIZED, element.getAttribute(EventFactory.EVENT_RESIZED));
		if (eventResized != null)
		{
			Window.addResizeHandler(new ResizeHandler(){
				public void onResize(ResizeEvent event) 
				{
					EventFactory.callEvent(eventResized, event);
				}
			});
		}
		final Event eventLoad = EventFactory.getEvent(EventFactory.EVENT_LOAD, element.getAttribute(EventFactory.EVENT_LOAD));
		if (eventLoad != null)
		{
			addLoadHandler(new ScreenLoadHandler(){
				public void onLoad(ScreenLoadEvent screenLoadEvent) 
				{
					EventFactory.callEvent(eventLoad, screenLoadEvent);
				}
			});
		}
	}

	/**
	 * Adds an event handler that is called only once, when the screen is loaded
	 * @param handler
	 */
	void addLoadHandler(final ScreenLoadHandler handler) 
	{
		handlerManager.addHandler(ScreenLoadEvent.TYPE, handler);
	}

	/**
	 * Fires the load event. This method has no effect when called more than one time.
	 */
	void load() 
	{
		if (handlerManager.getHandlerCount(ScreenLoadEvent.TYPE) > 0)
		{
			new Timer()
			{
				public void run() 
				{
					for (int i=0; i < handlerManager.getHandlerCount(ScreenLoadEvent.TYPE); i++) 
					{
						try 
						{
							DeferredCommand.addCommand(new Command() {
						        public void execute() 
						        {
						        	ScreenLoadEvent.fire(Screen.this);
						        }
						      });							
						} 
						catch (RuntimeException e) 
						{
							GWT.log(e.getLocalizedMessage(), e);
						}
					}
				}
			}.schedule(1); // Waits for browser starts the rendering process
		}
	}

		/**
	 * Fires the load event. This method has no effect when called more than one time.
	 */
	void fireEvent(ScreenLoadEvent event) 
	{
		handlerManager.fireEvent(event);
	}
	
	/**
	 * Gets the current screen
	 * @return
	 */
	public static Screen get()
	{
		return ScreenFactory.getInstance().getScreen();
	}
}
