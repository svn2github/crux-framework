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
package br.com.sysmap.crux.core.client.screen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.collection.FastList;
import br.com.sysmap.crux.core.client.collection.FastMap;
import br.com.sysmap.crux.core.client.context.ContextManager;
import br.com.sysmap.crux.core.client.datasource.DataSource;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.formatter.Formatter;
import br.com.sysmap.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.MetaElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstraction for the entire page. It encapsulate all the widgets, containers and 
 * datasources.
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Screen
{
	/**
	 * 
	 * @param id
	 * @param widget
	 */
	public static void add(String id, Widget widget)
	{
		Screen.get().addWidget(id, widget);
	}
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public static HandlerRegistration addCloseHandler(CloseHandler<Window> handler) 
	{
		return Screen.get().addWindowCloseHandler(handler);
	}
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public static HandlerRegistration addClosingHandler(ClosingHandler handler) 
	{
		return Screen.get().addWindowClosingHandler(handler);
	}
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public static HandlerRegistration addHistoryChangedHandler(ValueChangeHandler<String> handler) 
	{
		return Screen.get().addWindowHistoryChangedHandler(handler);
	}
	/**
	 * 
	 * @param handler
	 * @return
	 */
	public static HandlerRegistration addResizeHandler(ResizeHandler handler) 
	{
		return Screen.get().addWindowResizeHandler(handler);
	}
	/**
	 * 
	 * @param token
	 */
	public static void addToHistory(String token)
	{
		Screen.get().addTokenToHistory(token);
	}
	/**
	 * 
	 * @param token
	 * @param issueEvent
	 */
	public static void addToHistory(String token, boolean issueEvent)
	{
		Screen.get().addTokenToHistory(token, issueEvent);
	}
	/**
	 * 
	 * @param url
	 */
	public static String appendDebugParameters(String url)
	{
		if (Crux.getConfig().enableDebugForURL(url))
		{
			try
			{
				if (!StringUtils.isEmpty(url) && !url.contains("gwt.codesvr="))
				{
					String debugSvr = Window.Location.getParameter("gwt.codesvr");
					if (!StringUtils.isEmpty(debugSvr))
					{
						if (url.contains("?"))
						{
							url += "&gwt.codesvr="+URL.encode(debugSvr); 
						}
						else
						{
							url += "?gwt.codesvr="+URL.encode(debugSvr); 
						}
					}
				}
			}
			catch(Throwable e)
			{
				GWT.log(e.getLocalizedMessage(), e);
			}
		}
		return url;
	}
	
	/**
	 * 
	 */
	public static void blockToUser()
	{
		Screen.get().showBlockDiv("crux-DefaultScreenBlocker");
	}
	
	/**
	 * 
	 */
	public static void blockToUser(String blockingDivStyleName)
	{
		Screen.get().showBlockDiv(blockingDivStyleName);
	}
	
	/**
     *
     */
	public static void clearContext()
	{
		ContextManager.clearContext();
	}
	
	/**
	 * @param id
	 * @return
	 */
	public static boolean contains(String id)
	{
		return Screen.get().containsWidget(id);
	}
	
	/**
	 * 
	 */
	public static void createContext()
	{
		ContextManager.createContext();
	}
	
	/**
	 * 
	 * @param dataSource
	 * @return
	 */
	public static DataSource<?> createDataSource(String dataSource)
	{
		return ScreenFactory.getInstance().createDataSource(dataSource);
	}

	/**
	 * If the given widget does not have a non-empty ID attribute, sets the given id into it. 
	 * @param widget
	 * @param id
	 */
	public static void ensureDebugId(Widget widget, String id)
	{
		if(widget != null)
		{
			if(StringUtils.isEmpty(widget.getElement().getId()))
			{
				id = id.replaceAll("[^a-zA-Z0-9\\$]", "_");
				id = id.length() > 100 ? id.substring(0, 100) : id;
				
				if(!id.startsWith("_"))
				{
					id = "_" + id;
				}
				
				id = id.toLowerCase();
				
				id = ensureUniqueId(id);
				
				widget.getElement().setId(id);
			}
		}
	}
	
	/**
	 * Gets the current screen
	 * @return
	 */
	public static Screen get()
	{
		return ScreenFactory.getInstance().getScreen();
	}

	/**
	 * Gets a widget on the current screen
	 * @param id
	 * @return
	 */
	public static Widget get(String id)
	{
		return Screen.get().getWidget(id);
	}

	/**
	 * 
	 * @param <T>
	 * @param id
	 * @param clazz
	 * @return
	 */
	public static <T extends Widget> T get(String id, Class<T> clazz)
	{
		return Screen.get().getWidget(id, clazz);
	}

	/**
	 * @return a list containing all widgets from the current screen 
	 * @deprecated Use listWidgets() instead
	 */
	@Deprecated
	public static List<Widget> getAllWidgets()
	{
		FastList<Widget> keys = listWidgets();
		List<Widget> values = new ArrayList<Widget>(keys.size());		
		for (int i=0; i<keys.size(); i++)
		{
			values.add(keys.get(i));
		}		
		return values;
	}
	
	/**
	 * @return a list containing all widgets ids from the current screen 
	 * @deprecated Use listWidgetIds() instead
	 */
	@Deprecated
	public static List<String> getAllWidgetsIds()
	{
		FastList<String> keys = listWidgetIds();
		List<String> ids = new ArrayList<String>(keys.size());		
		for (int i=0; i<keys.size(); i++)
		{
			ids.add(keys.get(i));
		}		
		return ids;
	}

	/**
	 * @return
	 */
	public static String[] getControllers()
	{
		return Screen.get().getDeclaredControllers();
	}
	
	
	/**
	 * 
	 * @return
	 */
	@Deprecated
	public static ModuleComunicationSerializer getCruxSerializer()
	{
		return Screen.get().serializer;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getCurrentHistoryItem()
	{
		return Screen.get().getCurrentHistoryToken();
	}
	
	/**
	 * @deprecated Use createDataSource(java.lang.String) instead
	 * @param dataSource
	 * @return
	 */
	@Deprecated
	public static DataSource<?> getDataSource(String dataSource)
	{
		return createDataSource(dataSource);
	}
	
	/**
	 * @return
	 */
	public static String[] getDataSources()
	{
		return Screen.get().getDeclaredDataSources();
	}

	/**
	 * 
	 * @param formatter
	 * @return
	 */
	public static Formatter getFormatter(String formatter)
	{
		return ScreenFactory.getInstance().getClientFormatter(formatter);
	}
	
	/**
	 * @return
	 */
	public static String[] getFormatters()
	{
		return Screen.get().getDeclaredFormatters();
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getId() 
	{
		return Screen.get().getIdentifier();
	}

	/**
	 * 
	 * @return the locale specified or null
	 */
	public static String getLocale()
	{
		String locale = Window.Location.getParameter("locale");
		if (locale == null || locale.length() == 0)
		{
			NodeList<Element> metas = Document.get().getElementsByTagName("meta");
			if (metas != null)
			{
				for (int i=0; i< metas.getLength(); i++)
				{
					MetaElement meta = MetaElement.as(metas.getItem(i));
					if ("gwt:property".equals(meta.getName()) && meta.getContent() != null && meta.getContent().startsWith("locale="))
					{
						locale = meta.getContent().replace("locale=", "").trim();
						break;
					}
				}
			}
		}
		else
		{
			locale = locale.trim();
		}
		if ("".equals(locale))
		{
			locale = null;
		}
		
		return locale;
	}
	
	/**
	 * @return
	 */
	@Deprecated
	public static String[] getSerializables()
	{
		return Screen.get().getDeclaredSerializables();
	}

	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	public static void invokeControllerOnAbsoluteTop(String call, Object param) throws ModuleComunicationException
	{
		invokeControllerOnAbsoluteTop(call, param, Object.class);
	}

	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public static <T> T invokeControllerOnAbsoluteTop(String call, Object param, Class<T> resultType) throws ModuleComunicationException
	{
		return (T) Screen.get().serializer.deserialize(callAbsoluteTopControllerAccessor(call, Screen.get().serializer.serialize(param)));
	}

	/**
	 * @param call
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	public static void invokeControllerOnFrame(String frame, String call, Object param) throws ModuleComunicationException
	{
		invokeControllerOnFrame(frame, call, param, Object.class);
	}	
	
	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public static <T> T  invokeControllerOnFrame(String frame, String call, Object param, Class<T> resultType) throws ModuleComunicationException
	{
		return (T) Screen.get().serializer.deserialize(callFrameControllerAccessor(frame, call, Screen.get().serializer.serialize(param)));
	}

	/**
	 * 
	 * @param call
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	public static void invokeControllerOnOpener(String call, Object param) throws ModuleComunicationException
	{
		invokeControllerOnOpener(call, param, Object.class);
	}	
	
	/**
	 * 
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public static <T> T  invokeControllerOnOpener(String call, Object param, Class<T> resultType) throws ModuleComunicationException
	{
		return (T) Screen.get().serializer.deserialize(callOpenerControllerAccessor(call, Screen.get().serializer.serialize(param)));
	}	

	/**
	 * @param call
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	public static void invokeControllerOnParent(String call, Object param) throws ModuleComunicationException
	{
		invokeControllerOnParent(call, param, Object.class);
	}

	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public static <T> T  invokeControllerOnParent(String call, Object param, Class<T> resultType) throws ModuleComunicationException
	{
		return (T) Screen.get().serializer.deserialize(callParentControllerAccessor(call, Screen.get().serializer.serialize(param)));
	}
	
	/**
	 * @param call
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	public static void invokeControllerOnSelf(String call, Object param)
	{
		invokeControllerOnSelf(call, param, Object.class);
	}
	
	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public static <T> T invokeControllerOnSelf(String call, Object param, Class<T> resultType)
	{
		try
		{
			Event event = Events.getEvent("_onInvokeController", call);
			InvokeControllerEvent controllerEvent = new InvokeControllerEvent();
			controllerEvent.setParameter(param);
			Object result = Events.callEvent(event, controllerEvent, false);
			return (T) result; 
		}
		catch (RuntimeException e)
		{
			Crux.getErrorHandler().handleError(e.getLocalizedMessage(), e);
			return null;
		}
	}

	/**
	 * @param call
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	public static void invokeControllerOnSiblingFrame(String frame, String call, Object param) throws ModuleComunicationException
	{
		invokeControllerOnSiblingFrame(frame, call, param, Object.class);
	}

	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public static <T> T  invokeControllerOnSiblingFrame(String frame, String call, Object param, Class<T> resultType) throws ModuleComunicationException
	{
		return (T) Screen.get().serializer.deserialize(callSiblingFrameControllerAccessor(frame, call, Screen.get().serializer.serialize(param)));
	}

	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	public static void invokeControllerOnTop(String call, Object param) throws ModuleComunicationException
	{
		invokeControllerOnTop(call, param, Object.class);
	}
	
	/**
	 * @param call
	 * @param param
	 * @throws ModuleComunicationException
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public static <T> T invokeControllerOnTop(String call, Object param, Class<T> resultType) throws ModuleComunicationException
	{
		return (T) Screen.get().serializer.deserialize(callTopControllerAccessor(call, Screen.get().serializer.serialize(param)));
	}

	/**
	 * @return
	 */
	public static boolean isLoaded()
	{
		return Screen.get().isScreenLoaded();
	}

	/**
	 * 
	 * @return
	 */
	@Deprecated
	public static Iterator<Widget> iterateWidgets()
	{
		return Screen.get().iteratorWidgets();
	}
	
	/**
	 * @return
	 */
	public static FastList<Widget> listWidgets()
	{
		return Screen.get().widgetsList();
	}

	/**
	 * @return
	 */
	public static FastList<String> listWidgetIds()
	{
		return Screen.get().widgetsIdList();
	}
	
	/**
	 * 
	 * @return
	 */
	@Deprecated
	public static Iterator<String> iterateWidgetsIds()
	{
		return Screen.get().iteratorWidgetsIds();
	}
	
	/**
	 * Remove a widget on the current screen
	 * @param id
	 */
	public static void remove(String id)
	{
		Screen.get().removeWidget(id);
	}

	/**
	 * Remove a widget on the current screen
	 * @param id
	 * @param removeFromDOM
	 */
	public static void remove(String id, boolean removeFromDOM)
	{
		Screen.get().removeWidget(id, removeFromDOM);
	}

	/**
	 * 
	 */
	public static void unblockToUser()
	{
		Screen.get().hideBlockDiv();
	}
	
	/**
	 * Update fields mapped with ValueObject from widgets that have similar names.
	 * 
	 * @param caller
	 */
	public static void updateController(Object eventHandler)
	{
		Screen.get().updateControllerObjects(eventHandler);
	}
	
	/**
	 * Update widgets on screen that have the same id of fields mapped with ValueObject
	 * 
	 * @param caller
	 */
	public static void updateScreen(Object eventHandler)
	{
		Screen.get().updateScreenWidgets(eventHandler);
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	static void addScreenLoadHandler(ScreenLoadHandler handler) 
	{
		Screen.get().addLoadHandler(handler);
	}
	
	/**
	 * @param call
	 * @param serializedData
	 * @return
	 */
	@Deprecated
	private static native String callAbsoluteTopControllerAccessor(String call, String serializedData)/*-{
		var who = $wnd.top;
		var op = $wnd.opener;
		while (op != null)
		{
			who = op.top;
			op = op.opener;
		}
		return who._cruxScreenControllerAccessor(call, serializedData);
	}-*/;

	/**
	 * 
	 * @param frame
	 * @param call
	 * @param serializedData
	 * @return
	 */
	@Deprecated
	private static native String callFrameControllerAccessor(String frame, String call, String serializedData)/*-{
		return $wnd.frames[frame]._cruxScreenControllerAccessor(call, serializedData);
	}-*/;
	
	/**
	 * @param call
	 * @param serializedData
	 * @return
	 */
	@Deprecated
	private static native String callOpenerControllerAccessor(String call, String serializedData)/*-{
		return $wnd.opener._cruxScreenControllerAccessor(call, serializedData);
	}-*/;
	
	/**
	 * @param call
	 * @param serializedData
	 * @return
	 */
	@Deprecated
	private static native String callParentControllerAccessor(String call, String serializedData)/*-{
		return $wnd.parent._cruxScreenControllerAccessor(call, serializedData);
	}-*/;
	
	/**
	 * 
	 * @param frame
	 * @param call
	 * @param serializedData
	 * @return
	 */
	@Deprecated
	private static native String callSiblingFrameControllerAccessor(String frame, String call, String serializedData)/*-{
		return $wnd.parent.frames[frame]._cruxScreenControllerAccessor(call, serializedData);
	}-*/;
	
	/**
	 * @param call
	 * @param serializedData
	 * @return
	 */
	@Deprecated
	private static native String callTopControllerAccessor(String call, String serializedData)/*-{
		return $wnd.top._cruxScreenControllerAccessor(call, serializedData);
	}-*/;

	/**
	 * Ensures that the given id is not being used in the current document. If this is not the case, returns an unique id.
	 * @param id
	 * @return
	 */
	private static String ensureUniqueId(String id)
	{
		Object exists = DOM.getElementById(id);
		int i = 0;
		while(exists != null)
		{
			exists = DOM.getElementById(id + "_" + (++i));
		}
		if(i > 0)
		{
			id = id + "_" + i;
		}
		return id;
	}
	
	protected FastList<Element> blockingDivs = new FastList<Element>();

	protected String[] declaredControllers;
	
	protected String[] declaredDataSources;
	
	protected String[] declaredFormatters;
	
	@Deprecated
	protected String[] declaredSerializables;

	protected HandlerManager handlerManager;
	
	protected IFrameElement historyFrame = null;
	
	protected String id;
	
	protected FastMap<String> lazyWidgets = null;

	protected boolean loaded = false;
	
	protected FastList<ScreenLoadHandler>  loadHandlers = new FastList<ScreenLoadHandler>();
	
	protected ScreenBlocker screenBlocker = GWT.create(ScreenBlocker.class);

	@Deprecated
	protected ModuleComunicationSerializer serializer = null;
	
	protected FastMap<Widget> widgets = new FastMap<Widget>();

	@SuppressWarnings("deprecation")
    protected Screen(String id) 
	{
		this.id = id;
		this.handlerManager = new HandlerManager(this);
		initializeLazyWidgets(id);
		this.serializer = new ModuleComunicationSerializer();
		createControllerAccessor(this);
		this.addWindowCloseHandler(new CloseHandler<Window>()
		{
			public void onClose(CloseEvent<Window> event)
			{
				removeControllerAccessor(Screen.this);
			}
		});

		createCrossDocumentAccessor(this);
		this.addWindowCloseHandler(new CloseHandler<Window>()
		{
			public void onClose(CloseEvent<Window> event)
			{
				removeCrossDocumentAccessor(Screen.this);
			}
		});
	}
	
	/**
	 * @param lazy
	 * @param depedentId
	 */
	protected void addLazyWidgetDependency(String lazy, String dependentId)
	{
		if (Crux.getConfig().enableRuntimeLazyWidgetsInitialization())
		{
			this.lazyWidgets.put(lazy, dependentId);
		}
	}

	/**
	 * Adds an event handler that is called only once, when the screen is loaded
	 * @param handler
	 */
	protected void addLoadHandler(final ScreenLoadHandler handler) 
	{
		handlerManager.addHandler(ScreenLoadEvent.TYPE, handler);
		loadHandlers.add(handler);
	}
	
	/**
	 * 
	 * @param token
	 */
	protected void addTokenToHistory(String token)
	{
		History.newItem(token);
	}	
	
	/**
	 * 
	 * @param token
	 * @param issueEvent
	 */
	protected void addTokenToHistory(String token, boolean issueEvent)
	{
		History.newItem(token, issueEvent);
	}	
	
	protected void addWidget(String id, Widget widget)
	{
		widgets.put(id, widget);
	}
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	protected HandlerRegistration addWindowCloseHandler(CloseHandler<Window> handler) 
	{
		return Window.addCloseHandler(handler);
	}	
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	protected HandlerRegistration addWindowClosingHandler(ClosingHandler handler) 
	{
		return Window.addWindowClosingHandler(handler);
	}

	/**
	 * 
	 * @param handler
	 * @return
	 */
	protected HandlerRegistration addWindowHistoryChangedHandler(ValueChangeHandler<String> handler) 
	{
		if (historyFrame == null)
		{
			prepareHistoryFrame();
		}
		return History.addValueChangeHandler(handler);
	}		
	
	/**
	 * 
	 * @param handler
	 * @return
	 */
	protected HandlerRegistration addWindowResizeHandler(ResizeHandler handler) 
	{
		return Window.addResizeHandler(handler);
	}		
	
	/**
	 * @param widgetId
	 */
	protected void cleanLazyDependentWidgets(String widgetId)
	{
		FastList<String> dependentWidgets = getDependentWidgets(widgetId);
		for (int i=0; i<dependentWidgets.size(); i++)
		{
			lazyWidgets.remove(dependentWidgets.get(i));
		}
	}

	/**
	 * @param id
	 * @return
	 */
	protected boolean containsWidget(String id)
	{
		return widgets.containsKey(id);
	}
	
	/**
	 * Fires the load event. This method has no effect when called more than one time.
	 */
	protected void fireEvent(ScreenLoadEvent event) 
	{
		try
		{
			handlerManager.fireEvent(event);
		}
		finally
		{
			cleanLoadhandlers();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getCurrentHistoryToken()
	{
		return History.getToken();
	}
	
	/**
	 * @return
	 */
	protected String[] getDeclaredControllers()
	{
		return declaredControllers;
	}
	
	/**
	 * @return
	 */
	protected String[] getDeclaredDataSources()
	{
		return declaredDataSources;
	}

	/**
	 * @return
	 */
	protected String[] getDeclaredFormatters()
	{
		return declaredFormatters;
	}
	
	/**
	 * @return
	 */
	@Deprecated
	protected String[] getDeclaredSerializables()
	{
		return declaredSerializables;
	}
	
	/**
	 * @return
	 */
	protected String getIdentifier() 
	{
		return id;
	}

	/**
	 * @param id
	 * @return
	 */
	protected Widget getWidget(String id)
	{
		Widget widget = widgets.get(id);
		if (widget == null)
		{
			if (lazyWidgets.containsKey(id))
			{
				initializeLazyDependentWidget(lazyWidgets.get(id));
				widget = widgets.get(id);
			}
		}
		return widget;
	}
	
	/**
	 * Generic version of <code>getWidget</code> method
	 * @param <T>
	 * @param id
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T extends Widget> T getWidget(String id, Class<T> clazz)
	{
		Widget w = getWidget(id);
		return (T) w;
	}
	
	/**
	 * Hides the DIV that is blocking the Screen contents
	 */
	protected void hideBlockDiv()
	{
		if(blockingDivs.size() > 0)
		{
			int last = blockingDivs.size() - 1;
			
			Element blockingDiv = blockingDivs.get(last);
			blockingDivs.remove(last);
			
			Element body = RootPanel.getBodyElement();
			body.removeChild(blockingDiv);
			body.getStyle().setProperty("cursor", "");
		}
		
		if(blockingDivs.size() > 0)
		{
			Element blockingDiv = blockingDivs.get(blockingDivs.size() - 1);
			blockingDiv.getStyle().setProperty("display", "block");
		}
	}
	
	/**
	 * @param string
	 */
	protected void initializeLazyDependentWidget(String widgetId)
	{
		LazyPanel lazyPanel = (LazyPanel) getWidget(widgetId);
		if (lazyPanel != null)
		{
			lazyPanel.ensureWidget();
		}
		else
		{
			cleanLazyDependentWidgets(widgetId);
		}
	}
	
	/**
	 * @param id
	 */
	protected void initializeLazyWidgets(String id)
	{
		if (Crux.getConfig().enableRuntimeLazyWidgetsInitialization())
		{
			this.lazyWidgets = new FastMap<String>();
		}
		else
		{
			DeclaredLazyWidgets declaredLazyWidgets = GWT.create(DeclaredLazyWidgets.class);
			this.lazyWidgets = declaredLazyWidgets.getLazyWidgets(id);
		}
	}

	/**
	 * @return if this screen is completely loaded
	 */
	protected boolean isScreenLoaded()
	{
		return loaded;
	}

	/**
	 * @return
	 * @deprecated Use widgetsList() instead
	 */
	@Deprecated
	protected Iterator<Widget> iteratorWidgets()
	{
		FastList<Widget> widgetList = widgetsList();
		ArrayList<Widget> result = new ArrayList<Widget>();
		for (int i=0; i<widgetList.size(); i++)
		{
			result.add(widgetList.get(i));
		}
		
		return result.iterator();
	}

	/**
	 * @return
	 * @deprecated Use widgetsIdList() instead
	 */
	@Deprecated
	protected Iterator<String> iteratorWidgetsIds()
	{
		FastList<String> idList = widgetsIdList();
		ArrayList<String> result = new ArrayList<String>();
		for (int i=0; i<idList.size(); i++)
		{
			result.add(idList.get(i));
		}
		
		return result.iterator();
	}
	
	/**
	 * @return
	 */
	protected FastList<String> widgetsIdList()
	{
		return widgets.keys();
	}

	/**
	 * @return
	 */
	protected FastList<Widget> widgetsList()
	{
		FastList<String> keys = widgets.keys();
		FastList<Widget> values = new FastList<Widget>();
		for (int i=0; i<keys.size(); i++)
		{
			values.add(widgets.get(keys.get(i)));
		}
		
		return values;
	}

	/**
	 * Fires the load event. This method has no effect when called more than one time.
	 */
	protected void load() 
	{
		if (handlerManager.getHandlerCount(ScreenLoadEvent.TYPE) > 0)
		{
			Scheduler.get().scheduleDeferred(new ScheduledCommand(){
				public void execute()
				{
					try 
					{
						ScreenLoadEvent.fire(Screen.this);
						Screen.this.loaded = true;
					} 
					catch (RuntimeException e) 
					{
						Crux.getErrorHandler().handleError(e);
					}
				}
			});
		}
	}

	/**
	 * 
	 * @param element
	 */
	protected void parse(Element element) 
	{
		String title = element.getAttribute("_title");
		if (title != null && title.length() >0)
		{
			Window.setTitle(ScreenFactory.getInstance().getDeclaredMessage(title));
		}

		this.declaredControllers = extractReferencedResourceList(element, "_useController");
		this.declaredDataSources = extractReferencedResourceList(element, "_useDataSource");
		this.declaredSerializables = extractReferencedResourceList(element, "_useSerializable");
		this.declaredFormatters = extractReferencedResourceList(element, "_useFormatter");
		
		final Event eventHistory = Events.getEvent("onHistoryChanged", element.getAttribute("_onHistoryChanged"));
		if (eventHistory != null)
		{
			addWindowHistoryChangedHandler(new ValueChangeHandler<String>(){
				public void onValueChange(ValueChangeEvent<String> historyEvent)
				{
					Events.callEvent(eventHistory, historyEvent);
				}
			});
		}

		final Event eventClosing = Events.getEvent("onClosing", element.getAttribute("_onClosing"));
		if (eventClosing != null)
		{
			addWindowClosingHandler(new Window.ClosingHandler(){
				public void onWindowClosing(ClosingEvent closingEvent) 
				{
					Events.callEvent(eventClosing, closingEvent);
				}
			});
		}

		final Event eventClose = Events.getEvent("onClose", element.getAttribute("_onClose"));
		if (eventClose != null)
		{
			addWindowCloseHandler(new CloseHandler<Window>(){
				public void onClose(CloseEvent<Window> event) 
				{
					Events.callEvent(eventClose, event);				
				}
			});
		}

		final Event eventResized = Events.getEvent("onResized", element.getAttribute("_onResized"));
		if (eventResized != null)
		{
			addWindowResizeHandler(new ResizeHandler(){
				public void onResize(ResizeEvent event) 
				{
					Events.callEvent(eventResized, event);
				}
			});
		}
		final Event eventLoad = Events.getEvent("onLoad", element.getAttribute("_onLoad"));
		if (eventLoad != null)
		{
			addLoadHandler(new ScreenLoadHandler(){
				public void onLoad(ScreenLoadEvent screenLoadEvent) 
				{
					Events.callEvent(eventLoad, screenLoadEvent);
				}
			});
		}
	}

	/**
	 * 
	 */
	protected void prepareHistoryFrame() 
	{
		Element body = RootPanel.getBodyElement();
		if (historyFrame == null)
		{
			historyFrame = DOM.createIFrame().cast();
			historyFrame.setSrc("javascript:''");
			historyFrame.setId("__gwt_historyFrame");
			historyFrame.getStyle().setProperty("position", "absolute");
			historyFrame.getStyle().setProperty("width", "0");
			historyFrame.getStyle().setProperty("height", "0");
			historyFrame.getStyle().setProperty("border", "0");
			body.appendChild(historyFrame);
		    History.fireCurrentHistoryState();
		}
	}
	
	protected void removeWidget(String id)
	{
		removeWidget(id, true);
	}

	protected void removeWidget(String id, boolean removeFromDOM)
	{
		Widget widget = widgets.remove(id);
		if (widget != null && removeFromDOM)
		{
			widget.removeFromParent();
		}
	}

	/**
	 * Creates and shows a DIV over the screen contents
	 * @param blockingDivStyleName
	 */
	protected void showBlockDiv(String blockingDivStyleName)
	{
		if(blockingDivs.size() > 0)
		{
			Element blockingDiv = blockingDivs.get(blockingDivs.size() - 1);
			blockingDiv.getStyle().setProperty("display", "none");
		}
		
		Element body = RootPanel.getBodyElement();		
		Element blockingDiv = screenBlocker.createBlockingDiv(blockingDivStyleName, body);
		blockingDivs.add(blockingDiv);
		body.appendChild(blockingDiv);
	}

	/**
	 * Update fields mapped with ValueObject from widgets that have similar names.
	 * @param caller
	 */
	protected void updateControllerObjects(Object eventHandler)
	{
		if (eventHandler != null)
		{
			if (!(eventHandler instanceof ScreenBindableObject))
			{
				throw new ClassCastException(Crux.getMessages().screenInvalidObjectError());
			}
			((ScreenBindableObject) eventHandler).updateControllerObjects();

		}
	}
	
	/**
	 * Update widgets on screen that have the same id of fields mapped with ValueObject
	 * @param caller
	 */
	protected void updateScreenWidgets(Object eventHandler)
	{
		if (eventHandler != null)
		{
			if (!(eventHandler instanceof ScreenBindableObject))
			{
				throw new ClassCastException(Crux.getMessages().screenInvalidObjectError());
			}

			((ScreenBindableObject) eventHandler).updateScreenWidgets();
		}
	}
	
	/**
	 * Update each widget main element id with the value contained on Screen object
	 */
	protected void updateWidgetsIds()
	{
		FastList<String> widgetIds = this.widgets.keys();
		for (int i=0; i<widgetIds.size(); i++)
        {
			String widgetId = widgetIds.get(i);
	        Widget widget = Screen.get(widgetId);
	        Element element = widget.getElement();
			if (StringUtils.isEmpty(element.getId()))
	        {
	        	element.setId(widgetId);
	        }
        }
	}
	
	/**
	 * 
	 */
	private void cleanLoadhandlers()
	{
		int numHandlers = loadHandlers.size();
		
		for (int i = 0; i < numHandlers; i++)
		{
			ScreenLoadHandler handler = loadHandlers.get(i);
			handlerManager.removeHandler(ScreenLoadEvent.TYPE, handler);
		}
		
		loadHandlers.clear();
	}
	
	@Deprecated
	private native void createControllerAccessor(Screen handler)/*-{
		$wnd._cruxScreenControllerAccessor = function(call, serializedData){
			var a = handler.@br.com.sysmap.crux.core.client.screen.Screen::invokeController(Ljava/lang/String;Ljava/lang/String;)(call, serializedData);
			return a?a:null;
		};
	}-*/;
	
	/**
	 * Create a hook javascript function, called outside of module.
	 * @param handler
	 */
	private native void createCrossDocumentAccessor(Screen handler)/*-{
		$wnd._cruxCrossDocumentAccessor = function(serializedData){
			var a = handler.@br.com.sysmap.crux.core.client.screen.Screen::invokeCrossDocument(Ljava/lang/String;)(serializedData);
			return a?a:null;
		};
	}-*/;

	/**
	 * @param element
	 * @param attributeName
	 * @return
	 */
	private String[] extractReferencedResourceList(Element element, String attributeName)
	{
		String attr = element.getAttribute(attributeName);
		if (!StringUtils.isEmpty(attr))
		{
			String[] result = attr.split(",");
			for (int i = 0; i < result.length; i++)
			{
				result[i] = result[i].trim();
			}
			return result;
		}
		return new String[0];
	}
	
	/**
	 * @param widgetId
	 * @return
	 */
	private FastList<String> getDependentWidgets(String widgetId)
	{
		FastList<String> dependentWidgets = new FastList<String>();
		FastList<String> keys = lazyWidgets.keys();
		for (int i=0; i<keys.size(); i++)
		{
			String key = keys.get(i);
			if (lazyWidgets.get(key).equals(widgetId))
			{
				dependentWidgets.add(key);
			}
		}
		return dependentWidgets;
	}
	
	@Deprecated
	@SuppressWarnings("unused") // called by native code
	private String invokeController(String call, String serializedData)
	{
		Event event = Events.getEvent("_onInvokeController", call);
		InvokeControllerEvent controllerEvent = new InvokeControllerEvent();
		
		if (serializedData != null)
		{
			try
			{
				controllerEvent.setParameter(serializer.deserialize(serializedData));
			}
			catch (ModuleComunicationException e)
			{
				Crux.getErrorHandler().handleError(e.getLocalizedMessage(), e);
				return null;
			}
		}
		
		Object result = Events.callEvent(event, controllerEvent, true);
		try
		{
			return serializer.serialize(result); 
		}
		catch (ModuleComunicationException e)
		{
			Crux.getErrorHandler().handleError(e.getLocalizedMessage(),e);
			return null;
		}
	}
	
	/**
	 * Make a call to a cross document object.
	 * 
	 * @param serializedData
	 * @return
	 */
	@SuppressWarnings("unused") // called by native code
	private String invokeCrossDocument(String serializedData)
	{
		return Events.getRegisteredControllers().invokeCrossDocument(serializedData);
	}
	
	@Deprecated
	private native void removeControllerAccessor(Screen handler)/*-{
		$wnd._cruxScreenControllerAccessor = null;
	}-*/;		
	
	/**
	 * Remove the cross document hook function
	 * @param handler
	 */
	private native void removeCrossDocumentAccessor(Screen handler)/*-{
		$wnd._cruxCrossDocumentAccessor = null;
	}-*/;		
}
