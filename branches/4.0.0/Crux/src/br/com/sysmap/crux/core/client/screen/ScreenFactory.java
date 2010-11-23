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

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.collection.Array;
import br.com.sysmap.crux.core.client.collection.FastList;
import br.com.sysmap.crux.core.client.datasource.DataSource;
import br.com.sysmap.crux.core.client.datasource.RegisteredDataSources;
import br.com.sysmap.crux.core.client.formatter.Formatter;
import br.com.sysmap.crux.core.client.formatter.RegisteredClientFormatters;
import br.com.sysmap.crux.core.client.i18n.DeclaredI18NMessages;
import br.com.sysmap.crux.core.client.screen.LazyPanelFactory.LazyPanelWrappingType;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaData;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.LazyPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Factory for CRUX screen. It parses the document searching for crux meta tags. 
 * Based on the type (extracted from type attribute from crux meta tag), 
 * determine which class to create for all screen widgets.
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ScreenFactory 
{
	private static Logger logger = Logger.getLogger(ScreenFactory.class.getName());

	protected static final String ENCLOSING_PANEL_PREFIX = "_crux_";
	private static ScreenFactory instance = null;
	 
	private DeclaredI18NMessages declaredI18NMessages = null;
	private boolean parsing = false;
	private RegisteredClientFormatters registeredClientFormatters = null;
	private RegisteredDataSources registeredDataSources = null;
	private RegisteredWidgetFactories registeredWidgetFactories = null;
	private Screen screen = null;
	private FastList<ParserInfo> parserStack = new FastList<ParserInfo>();

	private ParserInfo currentParserInfo;
	
	
	/**
	 * Constructor
	 */
	private ScreenFactory()
	{
		this.declaredI18NMessages = GWT.create(DeclaredI18NMessages.class);
		this.registeredDataSources = GWT.create(RegisteredDataSources.class);
	}
	
	/**
	 * Retrieve the ScreenFactory instance.
	 * @return
	 */
	public static ScreenFactory getInstance()
	{
		if (instance == null)
		{
			instance = new ScreenFactory();
		}
		return instance;
	}
	
	/**
	 * Create a new DataSource instance
	 * @param dataSource dataSource name, declared with <code>@DataSource</code> annotation
	 * @return new dataSource instance
	 */
	public DataSource<?> createDataSource(String dataSource)
	{
		return this.registeredDataSources.getDataSource(dataSource);
	}

	/**
	 * 
	 * @param formatter
	 * @return
	 */
	public Formatter getClientFormatter(String formatter)
	{
		if (this.registeredClientFormatters == null)
		{
			this.registeredClientFormatters = (RegisteredClientFormatters) GWT.create(RegisteredClientFormatters.class);
		}

		return this.registeredClientFormatters.getClientFormatter(formatter);
	}
	
	/**
	 * @deprecated - Use createDataSource(java.lang.String) instead.
	 * @param dataSource
	 * @return
	 */
	@Deprecated
	public DataSource<?> getDataSource(String dataSource)
	{
		return createDataSource(dataSource);
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public String getDeclaredMessage(String key)
	{
		return this.declaredI18NMessages.getMessage(key);
	}
	
	/**
	 * Return the type of a given crux meta tag element. This type could be {@code "screen"} or 
	 * another string referencing a registered {@code WidgetFactory}
	 * @param cruxMetaElement
	 * @return
	 */
	public String getMetaElementType(CruxMetaDataElement cruxMetaElement)
	{
		return cruxMetaElement.getProperty("type");
	}
	
	/**
	 * Get the screen associated with current page. If not created yet, create it.
	 * @return
	 */
	public Screen getScreen()
	{
		if (screen == null)
		{
			this.registeredWidgetFactories = (RegisteredWidgetFactories) GWT.create(RegisteredWidgetFactories.class);
			create();
		}
		return screen;
	}
	
	/**
	 * Creates a new widget based on a HTML SPAN tag and attaches it on the Screen object.
	 * @param metaElem
	 * @param widgetId
	 * @return
	 * @throws InterfaceConfigException
	 */
	public Widget newWidget(CruxMetaDataElement metaElem, String widgetId, String widgetType) throws InterfaceConfigException
	{
		return newWidget(metaElem, widgetId, widgetType, true);
	}
	
	/**
	 * Creates a new widget based on its meta-data element 
	 * @param metaElem
	 * @param widgetId
	 * @param addToScreen
	 * @return
	 * @throws InterfaceConfigException
	 */
	public Widget newWidget(CruxMetaDataElement metaElem, String widgetId, String widgetType, boolean addToScreen) throws InterfaceConfigException
	{
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, "Creating new widget: WidgetID=["+widgetId+"], WidgetType=["+widgetType+"], AddToScreen=["+addToScreen+"]");
		}
		WidgetFactory<? extends Widget> widgetFactory = registeredWidgetFactories.getWidgetFactory(widgetType);
		if (widgetFactory == null)
		{
			throw new InterfaceConfigException(Crux.getMessages().screenFactoryWidgetFactoryNotFound(widgetType));
		}
		
		Widget widget;
		if (mustRenderLazily((DeclarativeWidgetFactory) widgetFactory, metaElem, widgetId))
		{
			widget = LazyPanelFactory.getLazyPanel(metaElem, widgetId, LazyPanelWrappingType.wrapWholeWidget);
			Screen.add(LazyPanelFactory.getLazyPanelId(widgetId, LazyPanelWrappingType.wrapWholeWidget), widget);
		}
		else
		{
			widget = widgetFactory.createWidget(metaElem, widgetId, addToScreen); 
		}
		if (widget == null)
		{
			throw new InterfaceConfigException(Crux.getMessages().screenFactoryErrorCreateWidget(widgetId));
		}
		
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, "widget ["+widgetId+"] created.");
		}
		return widget;
	}
	
	/**
	 * Returns the parent element (a wrapper span element) 
	 * @param widgetId
	 * @return
	 */
	protected Element getEnclosingPanelElement(String widgetId)
	{
		return DOM.getElementById(ENCLOSING_PANEL_PREFIX+widgetId);
	}
	
	/**
	 * @return
	 */
	boolean isParsing()
	{
		return parsing;
	}
	
	/**
	 * 
	 * @param element
	 * @return
	 */
	boolean isValidWidget(CruxMetaDataElement metaElem)
	{
		String type =  metaElem.getProperty("type");
		if (type != null && type.length() > 0 && !StringUtils.unsafeEquals("screen",type))
		{
			return true;
		}
		return false;
	}

	/**
	 * This method receives an array of crux meta elements. Those meta elements contains the information
	 * needed to render widgets and informations about the page itself (represented by a {@code Screen} object).
	 * 
	 * <p>For each widget meta element found, this factory choose a {@code WidgetFactory} that will parse the 
	 * meta element and build the widget. To find out which {@code WidgetFactory} call, the attribute {@code type}
	 * from the meta element is checked.
	 * @param cruxMetaElements the array of elements
	 */
	void parseDocument()
	{
		this.parsing = true;
		try
		{
			if (LogConfiguration.loggingIsEnabled())
			{
				logger.log(Level.FINE, "Starting a new factory parsing cycle...");
			}
			nextFromParserStack();
			while (currentParserInfo != null)
			{
				parseCurrentInfo();
				fireLoadEvent();
				nextFromParserStack();
			}

			if (LogConfiguration.loggingIsEnabled())
			{
				logger.log(Level.FINE, "Factory parsing cycle completed.");
			}
		}
		finally
		{
			this.parsing = false;
		}
	}
	
	/**
	 * Fires the load event. Called when a parsing step is completed.
	 */
	protected void fireEvent(ScreenLoadEvent event) 
	{
		assert(currentParserInfo != null);
		currentParserInfo.fireLoadEvent(event, this);
	}	
	
	/**
	 * Adds an event handler that is called only once, when the screen is loaded
	 * @param handler
	 */
	protected void addLoadHandler(final ScreenLoadHandler handler) 
	{
		assert(currentParserInfo != null);
		currentParserInfo.addLoadHandler(ScreenLoadEvent.TYPE, handler);
	}		
	
	/**
	 * Fires the load event.
	 */
	protected void fireLoadEvent() 
	{
		try 
		{
			ScreenLoadEvent.fire(ScreenFactory.this);
		} 
		catch (RuntimeException e) 
		{
			Crux.getErrorHandler().handleError(e);
		}
	}	
	
	/**
	 * @param widgetFactory
	 * @param metaElem
	 * @param widgetId
	 * @return
	 * @throws InterfaceConfigException
	 */
	private boolean mustRenderLazily(DeclarativeWidgetFactory widgetFactory, CruxMetaDataElement metaElem, String widgetId) throws InterfaceConfigException 
	{
		if (widgetFactory.isPanel())
		{
			String visible = metaElem.getProperty("visible");
			if (!StringUtils.isEmpty(visible) && !Boolean.parseBoolean(visible))
			{
				String lazyId = LazyPanelFactory.getLazyPanelId(widgetId, LazyPanelWrappingType.wrapWholeWidget);
				return !screen.containsWidget(lazyId);
			}
		}
		return false;
	} 

	/**
	 * @param cruxMetaElements
	 */
	private void parseCurrentInfo() 
	{
		Array<CruxMetaDataElement> cruxMetaElements = currentParserInfo.parserElements;
		int elementsLength = cruxMetaElements.size();
		for (int i=0; i<elementsLength; i++)
		{
			CruxMetaDataElement metaElement = cruxMetaElements.get(i);
			if (metaElement != null)
			{
				assert(metaElement.containsKey("type")):Crux.getMessages().screenFactoryMetaElementDoesNotContainsType();
				String type = getMetaElementType(metaElement);
				if (StringUtils.unsafeEquals("screen",type))
				{
					screen.parse(metaElement);
				}
				else
				{
					try 
					{
						createWidget(metaElement, type, screen, currentParserInfo.parentId, currentParserInfo.parentType);
					}
					catch (Throwable e) 
					{
						Crux.getErrorHandler().handleError(Crux.getMessages().screenFactoryGenericErrorCreateWidget(e.getLocalizedMessage()), e);
					}
				}
			}
		}
	}
	
	/**
	 * 
	 */
	private void create()
	{
		CruxMetaData metaData = CruxMetaData.loadMetaData();
		screen = new Screen(metaData.getScreenId(), metaData.getLazyDependencies());
		addToParserStack(null, null, metaData.getElementsMetaData());
		parseDocument();
	}
	
	/**
	 * @param array
	 */
	protected void addToParserStack(String parentType, String parentId, Array<CruxMetaDataElement> parserElements)
	{
		assert(parserElements != null);
		parserStack.add(new ParserInfo(parentType, parentId, parserElements));
	}
	
	/**
	 * @return
	 */
	private void nextFromParserStack()
	{
		currentParserInfo = parserStack.extractFirst();
	}
	
	/**
	 * 
	 * @param element
	 * @param screen
	 * @param parentType 
	 * @param parentId 
	 * @param widgetsElementsAdded
	 * @return
	 * @throws InterfaceConfigException
	 */
	private Widget createWidget(CruxMetaDataElement metaElem, String widgetType, Screen screen, String parentId, String parentType) throws InterfaceConfigException
	{
		assert(metaElem.containsKey("id") && metaElem.getProperty("id") != null) :Crux.getMessages().screenFactoryWidgetIdRequired();
		String widgetId = metaElem.getProperty("id");
		if (widgetId == null || widgetId.length() == 0)
		{
			throw new InterfaceConfigException(Crux.getMessages().screenFactoryWidgetIdRequired());
		}
		Widget widget = screen.getWidget(widgetId, false);//Does not fire any lazy initialization here.
		if (widget != null)
		{
			return widget;
		}
		DeclarativeWidgetFactory widgetFactory = (DeclarativeWidgetFactory) registeredWidgetFactories.getWidgetFactory(widgetType);
		if (!widgetFactory.isAttachToDOM())
		{
			widget = newWidget(metaElem, widgetType, widgetId);
		}
		else if (parentId != null)
		{
			assert(!StringUtils.isEmpty(parentId) && !StringUtils.isEmpty(parentType));
			widget = createWidgetAndAttachToParent(metaElem, widgetId, widgetType, parentId, parentType);
		}
		else if (widgetFactory.isHtmlContainer())
		{
			widget = createHtmlContainerAndAttach(metaElem, widgetId, widgetType);
		}
		else
		{
			widget = createWidgetAndAttach(metaElem, widgetId, widgetType);
		}
		return widget;
	}
	
	/**
	 * @param metaElem
	 * @param widgetId
	 * @param widgetType
	 * @param parentId
	 * @param parentType
	 * @return
	 * @throws InterfaceConfigException 
	 */
	@SuppressWarnings("unchecked")
	private Widget createWidgetAndAttachToParent(CruxMetaDataElement metaElem, String widgetId, String widgetType, String parentId, String parentType) throws InterfaceConfigException 
	{
		Widget widget = newWidget(metaElem, widgetId, widgetType);
		Widget parent = screen.getWidget(parentId);
		assert (parent != null);
		
		if (StringUtils.unsafeEquals(LazyPanelFactory.LAZY_PANEL_TYPE, parentType))
		{
			LazyPanelFactory.getInstance().add((LazyPanel) parent, parentId, widget, widgetId);
		}
		else
		{
			WidgetFactory<?> factory = registeredWidgetFactories.getWidgetFactory(parentType);
			assert (factory instanceof HasWidgetsFactory);
			HasWidgetsFactory<Widget> parentWidgetFactory = (HasWidgetsFactory<Widget>) factory;
			parentWidgetFactory.add(parent, parentId, widget, widgetId);
		}
		return widget;
	}

	/**
	 * 
	 * @param metaElem
	 * @param widgetId
	 * @param widgetType
	 * @return
	 * @throws InterfaceConfigException
	 */
	private Widget createHtmlContainerAndAttach(CruxMetaDataElement metaElem, String widgetId, String widgetType) throws InterfaceConfigException
	{
		Element panelElement = getEnclosingPanelElement(widgetId);
		Element parentElement = panelElement.getParentElement();
		Node previousSibling = panelElement.getPreviousSibling();
		Widget widget = newWidget(metaElem, widgetId, widgetType);

		if (previousSibling != null)
		{
			parentElement.insertAfter(widget.getElement(), previousSibling);
		}
		else
		{
			parentElement.appendChild(widget.getElement());
		}
		((HTMLContainer)widget).onAttach();
	    RootPanel.detachOnWindowClose(widget);		
		return widget;
	}
	
	/**
	 * 
	 * @param metaElem
	 * @param widgetId
	 * @param widgetType
	 * @return
	 * @throws InterfaceConfigException
	 */
	private Widget createWidgetAndAttach(CruxMetaDataElement metaElem, String widgetId, String widgetType) throws InterfaceConfigException
	{
		Element panelElement = getEnclosingPanelElement(widgetId);
		Widget widget = newWidget(metaElem, widgetId, widgetType);
		Panel panel;
		if (widget instanceof RequiresResize)
		{
			boolean hasSize = (WidgetFactory.hasWidth(metaElem) && WidgetFactory.hasHeight(metaElem));
			if (RootPanel.getBodyElement().equals(panelElement.getParentElement()) && !hasSize)
			{
				panel = RootLayoutPanel.get();
			}
			else
			{
				panel = RootPanel.get(panelElement.getId());
				if (!hasSize)
				{
					GWT.log(Crux.getMessages().screenFactoryLayoutPanelWithoutSize(widgetId), null);
				}
			}
		}
		else
		{
			panel = RootPanel.get(panelElement.getId());
		}
		panel.add(widget);
		return widget;
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	private static class ParserInfo
	{
		private ParserInfo(String parentType, String parentId, Array<CruxMetaDataElement> parserElements) 
		{
			this.parentType = parentType;
			this.parentId = parentId;
			this.parserElements = parserElements;
			this.eventBus = new SimpleEventBus();
		}
		
		void addLoadHandler(Type<ScreenLoadHandler> type, ScreenLoadHandler handler)
        {
			eventBus.addHandler(ScreenLoadEvent.TYPE, handler);
		}

		/**
		 * @param event
		 * @param source
		 */
		void fireLoadEvent(final ScreenLoadEvent event, final ScreenFactory source)
        {
			Scheduler.get().scheduleDeferred(new ScheduledCommand(){
				public void execute()
				{
					ParserInfo.this.eventBus.fireEventFromSource(event, source);
				}
			});
        }

		String parentType;
		String parentId;
		Array<CruxMetaDataElement> parserElements;
		SimpleEventBus eventBus;
	}
}
