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

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.collection.Array;
import br.com.sysmap.crux.core.client.collection.CollectionFactory;
import br.com.sysmap.crux.core.client.screen.WidgetLazyRuntimeCheckers.WidgetLazyChecker;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaData;
import br.com.sysmap.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.LazyPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Panel which content is only rendered when it becomes visible for the first time.
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class LazyPanelFactory implements HasWidgetsFactory<LazyPanel>
{
	static final String LAZY_PANEL_TYPE = "_CRUX_LAZY_PANEL_";
	public static final String LAZY_PANEL_PREFIX = "_lazy_";
	
	private static LazyPanelFactory instance = null; 
	
	/**
	 * 
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
	 * @see br.com.sysmap.crux.core.client.screen.HasWidgetsFactory#add(com.google.gwt.user.client.ui.Widget, java.lang.String, com.google.gwt.user.client.ui.Widget, java.lang.String)
	 */
	public void add(LazyPanel parent, String parentId, Widget widget, String widgetId)
	{
		parent.add(widget);
	}
	
	/**
	 * @param element
	 * @param widgetId
	 * @return
	 */
	public static LazyPanel getLazyPanel(final CruxMetaData element, String widgetId) 
	{
		if (Crux.getConfig().enableRuntimeLazyWidgetsInitialization())
		{
			maybeBuildLazyDependencyList(element, widgetId);
		}		
		
		return new CruxLazyPanel(element, widgetId);
	}

	/**
	 * @param element
	 * @param widgetId
	 */
	private static void maybeBuildLazyDependencyList(final CruxMetaData element, String widgetId)
	{
		if (Crux.getConfig().enableRuntimeLazyWidgetsInitialization())
		{
			WidgetLazyRuntimeCheckers checkers = GWT.create(WidgetLazyRuntimeCheckers.class);
			buildLazyDependencyList(element, widgetId, checkers);
		}
	}
	/**
	 * @param element
	 * @param widgetId
	 */
	private static void buildLazyDependencyList(final CruxMetaData element, String widgetId, WidgetLazyRuntimeCheckers checkers)
	{
		Array<CruxMetaData> children = element.getChildren();
		if (children != null)
		{
			ScreenFactory factory = ScreenFactory.getInstance();

			int size = children.size();
			String childId = widgetId;
			for (int i=0; i<size; i++)
			{
				CruxMetaData child = children.get(i);
				if (child != null)
				{
					String childWidgetId = widgetId;
					boolean childIsLazyPanel = false;
					if (factory.isValidWidget(child))
					{
						childId = child.getProperty("id");
						factory.getScreen().addLazyWidgetDependency(childId, widgetId);
						if (mustRenderLazily(child, checkers))
						{
							childIsLazyPanel = true;
							childWidgetId = childId;
						}
					}
					if (!childIsLazyPanel || !factory.getScreen().containsLazyDependents(childWidgetId))
					{
						buildLazyDependencyList(child, childWidgetId, checkers);
					}
				}
			}
		}
	}
	
	/**
	 * @param child
	 * @param checkers
	 * @return
	 */
	private static boolean mustRenderLazily(CruxMetaData child, WidgetLazyRuntimeCheckers checkers)
	{
		String visible = child.getProperty("visible");
		if (!StringUtils.isEmpty(visible) && !Boolean.parseBoolean(visible))
		{
			return true;
		}
		WidgetLazyChecker checker = checkers.getWidgetLazyChecker(ScreenFactory.getInstance().getMetaElementType(child));
		return checker != null && checker.isLazy(child);
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
		private CruxMetaData metaData;
		private final String lazyId;
		
		/**
		 * Constructor
		 * @param innerHTML
		 * @param lazyId
		 */
		public CruxLazyPanel(CruxMetaData metaData, String lazyId)
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
			Array<CruxMetaData> children = CollectionFactory.createArray();
			children.add(metaData);
			metaData = null;
			ScreenFactory factory = ScreenFactory.getInstance();
			factory.addToParserStack(LAZY_PANEL_TYPE, lazyId, children);
			
			if (!factory.isParsing())
			{
				factory.parseDocument();
			}
			return getWidget();			
		}			
	}
}
