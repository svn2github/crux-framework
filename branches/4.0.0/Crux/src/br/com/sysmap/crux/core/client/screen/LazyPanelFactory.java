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

import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.sysmap.crux.core.client.collection.Array;
import br.com.sysmap.crux.core.client.collection.CollectionFactory;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.ui.LazyPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Factory that wraps an element with a panel which content is only rendered when it is accessed for the first time.
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class LazyPanelFactory implements HasWidgetsFactory<LazyPanel, WidgetFactoryContext>
{
	static final String LAZY_PANEL_TYPE = "_CRUX_LAZY_PANEL_";
	
	private static LazyPanelFactory instance = null;
	private static final String LAZY_CHILDREN_PANEL_PREFIX = "_chld_";
	private static final String LAZY_PANEL_PREFIX = "_lazy_";
	private static final int LAZY_PANEL_PREFIX_LENGTH = LAZY_CHILDREN_PANEL_PREFIX.length();
	
	private static Logger logger = Logger.getLogger(LazyPanelFactory.class.getName()); 
	
	/**
	 * Singleton constructor
	 */
	private LazyPanelFactory() 
	{
	}
	
	/**
	 * @return
	 */
	public static LazyPanelFactory getInstance()
	{
		if (instance == null)
		{
			instance = new LazyPanelFactory();
		}
		return instance;
	}
	
	/**
	 * Create an wrapper lazyPanel capable of creating an widget for the given CruxMetaData element. 
	 * 
	 * @param element CruxMetaData element that will be used to create the wrapped widget
	 * @param panelId Identifier of the parent panel, that required the lazy wrapping operation.
	 * @param wrappingType the lazyPanel wrapping model.
	 * @return
	 */
	public static LazyPanel getLazyPanel(final CruxMetaDataElement element, String panelId, LazyPanelWrappingType wrappingType) 
	{
		String widgetId = getLazyPanelId(panelId, wrappingType);
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, "Delaying the widget ["+element.getProperty("id")+"] creation. Instantiating a new lazyPanel ["+widgetId+"] to wrap this widget...");
		}
		
		return new CruxLazyPanel(element, widgetId);
	}
	
	/**
	 * Return the id created to the panel that wraps the given widget id.
	 * @param wrappedWidgetId
	 * @param wrappingType 
	 * @return
	 */
	public static String getLazyPanelId(String wrappedWidgetId, LazyPanelWrappingType wrappingType)
	{
		if (wrappingType == LazyPanelWrappingType.wrapChildren)
		{
			return LAZY_CHILDREN_PANEL_PREFIX+wrappedWidgetId;
		}
		else
		{
			return LAZY_PANEL_PREFIX+wrappedWidgetId;
		}
	}
	
	/**
	 * Return the id of the widget wrapped by the given lazy panel id.
	 * @param lazyPanelId
	 * @return
	 */
	public static String getWrappedWidgetIdFromLazyPanel(String lazyPanelId)
	{
		assert(lazyPanelId != null && lazyPanelId.length() > LAZY_PANEL_PREFIX_LENGTH);
		return lazyPanelId.substring(LAZY_PANEL_PREFIX_LENGTH);
	}

	/**
	 * Check if the wrappedWidgetId is a valid lazy id generated for a {@code LazyPanelWrappingType.wrapChidren}
	 * lazy model 
	 * @param wrappedWidgetId
	 * @return
	 */
	public static boolean isChildrenWidgetLazyWrapper(String wrappedWidgetId)
	{
		assert(wrappedWidgetId != null);
		return wrappedWidgetId.startsWith(LAZY_CHILDREN_PANEL_PREFIX);
	}
	
	/**
	 * Check if the wrappedWidgetId is a valid lazy id generated for a {@code LazyPanelWrappingType.wrapWholeWidget}
	 * lazy model 
	 * @param wrappedWidgetId
	 * @return
	 */
	public static boolean isWholeWidgetLazyWrapper(String wrappedWidgetId)
	{
		assert(wrappedWidgetId != null);
		return wrappedWidgetId.startsWith(LAZY_PANEL_PREFIX);
	}

	/**
	 * @see br.com.sysmap.crux.core.client.screen.HasWidgetsFactory#add(com.google.gwt.user.client.ui.Widget, java.lang.String, com.google.gwt.user.client.ui.Widget, java.lang.String)
	 */
	public void add(LazyPanel parent, String parentId, Widget widget, String widgetId)
	{
		parent.add(widget);
	}
	
	/**
	 * A default lazy panel implementation, used by Crux engine to lazily parse the entire Document.
	 * When a panel is declaratively created with visible attribute equals to false, an instance of 
	 * that class is used to only parse the panel element and create the widget when it is first accessed
	 * by {@code Screen.getWidget(String)} or when it becomes visible.
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class CruxLazyPanel extends LazyPanel implements br.com.sysmap.crux.core.client.screen.LazyPanel{
		private boolean initialized = false;
		private final String lazyId;
		private CruxMetaDataElement metaData;
		
		/**
		 * Constructor
		 * @param innerHTML
		 * @param lazyId
		 */
		public CruxLazyPanel(CruxMetaDataElement metaData, String lazyId)
		{
			this.metaData = metaData;
			this.lazyId = lazyId;
		}
		
		/**
		 * @see com.google.gwt.user.client.ui.LazyPanel#ensureWidget()
		 */
		@Override
		public void ensureWidget()
		{
			if (!initialized)
			{
				ScreenFactory.getInstance().getScreen().cleanLazyDependentWidgets(lazyId);
				initialized = true;
			}
			super.ensureWidget();
		}

		/**
		 * @see com.google.gwt.user.client.ui.LazyPanel#createWidget()
		 */
		@Override
		protected Widget createWidget() 
		{
			if (LogConfiguration.loggingIsEnabled())
			{
				logger.log(Level.FINE, "Creating ["+lazyId+"] wrapped widget...");
			}
			Array<CruxMetaDataElement> children = CollectionFactory.createArray();
			children.add(metaData);
			metaData = null;
			ScreenFactory factory = ScreenFactory.getInstance();
			factory.addToParserStack(LAZY_PANEL_TYPE, lazyId, children);
			
			if (!factory.isParsing())
			{
				factory.parseDocument();
			}
			if (LogConfiguration.loggingIsEnabled())
			{
				logger.log(Level.FINE, "["+lazyId+"] wrapped widget created.");
			}
			return getWidget();			
		}			
	}
	
	/**
	 * Contains the available lazyPanel wrapping models. {@code wrapChildren} is used 
	 * by widgets that needs to create some of its children lazily. {@code wrapWholeWidget}
	 * is used when the whole widget must be rendered lazily, like when {@code ScreenFactory}
	 * is parsing the CruxMetaData and find an invisible panel.
	 * 
	 * @author Thiago da Rosa de Bustamante
	 */
	public static enum LazyPanelWrappingType{wrapChildren, wrapWholeWidget}
}
