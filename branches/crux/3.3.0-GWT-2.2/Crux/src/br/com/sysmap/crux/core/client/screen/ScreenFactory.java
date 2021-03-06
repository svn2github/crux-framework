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

import java.util.Date;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.collection.FastList;
import br.com.sysmap.crux.core.client.datasource.DataSource;
import br.com.sysmap.crux.core.client.datasource.RegisteredDataSources;
import br.com.sysmap.crux.core.client.formatter.Formatter;
import br.com.sysmap.crux.core.client.formatter.RegisteredClientFormatters;
import br.com.sysmap.crux.core.client.i18n.DeclaredI18NMessages;
import br.com.sysmap.crux.core.client.utils.DOMUtils;
import br.com.sysmap.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * Factory for CRUX screen. It parses the document searching for crux meta tags. 
 * Based on the type (extracted from _type attribute from crux meta tag), 
 * determine which class to create for all screen widgets.
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ScreenFactory {
	
	private static ScreenFactory instance = null;
	 
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
	private DeclaredI18NMessages declaredI18NMessages = null;
	private boolean parsing = false;
	private RegisteredClientFormatters registeredClientFormatters = null;
	private RegisteredDataSources registeredDataSources = null;
	private RegisteredWidgetFactories registeredWidgetFactories = null;
	private Screen screen = null;
	private String screenId = null;
	private StringBuilder traceOutput = null;
	
	/**
	 * Constructor
	 */
	private ScreenFactory()
	{
		this.declaredI18NMessages = GWT.create(DeclaredI18NMessages.class);
		this.registeredDataSources = GWT.create(RegisteredDataSources.class);
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
	 * 
	 * @return
	 */
	public String getScreenId()
	{
		if (screenId == null)
		{
			String fileName = DOMUtils.getDocumentName();
			int indexBeg = fileName.indexOf(GWT.getModuleName());
			int indexEnd = fileName.indexOf("?");
			int begin = (indexBeg == -1) ? 0 : indexBeg;
			int end = (indexEnd == -1) ? fileName.length() : indexEnd;
			screenId = fileName.substring(begin, end);
		}
		return screenId;
	}
	
	/**
	 * Creates a new widget based on a HTML SPAN tag and attaches it on the Screen object.
	 * @param element
	 * @param widgetId
	 * @return
	 * @throws InterfaceConfigException
	 */
	public Widget newWidget(Element element, String widgetId, String widgetType) throws InterfaceConfigException
	{
		return newWidget(element, widgetId, widgetType, true);
	}

	/**
	 * Creates a new widget based on a HTML SPAN tag 
	 * @param element
	 * @param widgetId
	 * @param addToScreen
	 * @return
	 * @throws InterfaceConfigException
	 */
	public Widget newWidget(Element element, String widgetId, String widgetType, boolean addToScreen) throws InterfaceConfigException
	{
		Date start = null;
		if (Crux.getConfig().enableClientFactoryTracing())
		{// This statement is like this to generate a better code when compiling with GWT compiler. Do not join the statements
			if(traceOutput != null)
			{
				start = new Date();
			}
		}
		WidgetFactory<? extends Widget> widgetFactory = registeredWidgetFactories.getWidgetFactory(widgetType);
		if (widgetFactory == null)
		{
			throw new InterfaceConfigException(Crux.getMessages().screenFactoryWidgetFactoryNotFound(widgetType));
		}
		
		Widget widget = widgetFactory.createWidget(element, widgetId, addToScreen); 
		if (widget == null)
		{
			throw new InterfaceConfigException(Crux.getMessages().screenFactoryErrorCreateWidget(widgetId));
		}
		
		if (Crux.getConfig().enableClientFactoryTracing())
		{// This statement is like this to generate a better code when compiling with GWT compiler. Do not join the statements
			if(traceOutput != null)
			{
				Date end = new Date();
				traceOutput.append("newWidget [widgetId = "+widgetId+"; type = "+widgetType+"] - ("+(end.getTime() - start.getTime())+" ms)<br/>");
			}
		}
		return widget;
	}
	
	/**
	 * Returns the parent element, or a wrapper span element, contained on parent on the same position
	 * that the widget meta tag was declared
	 * @param element
	 * @param widgetId
	 * @return
	 */
	protected Element getEnclosingPanelElement(Element element, String widgetId)
	{
		Element panelElement;
		boolean parentHasMoreThanOneChild = (element.getNextSiblingElement() != null || DOMUtils.getPreviousSiblingElement(element) != null);
		if (Crux.getConfig().wrapSiblingWidgets() && parentHasMoreThanOneChild)
		{
			panelElement = DOM.createSpan();
			element.getParentElement().insertBefore(panelElement, element);
		}
		else
		{
			if (parentHasMoreThanOneChild)
			{
				GWT.log(Crux.getMessages().screenFactoryNonDeterministicWidgetPositionInParent(widgetId), null);
			}
			panelElement = element.getParentElement();
		}
		return panelElement;
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
	boolean isValidWidget(Element element)
	{
		String tagName = element.getTagName();
		if (StringUtils.unsafeEquals("SPAN",tagName))
		{
			String type = getMetaElementType(element);
			if (type != null && type.length() > 0 && !StringUtils.unsafeEquals("screen",type))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param element
	 * @return
	 */
	boolean isValidCruxWidgetMetaTag(Element spanElement)
	{
		String type = getMetaElementType(spanElement);
		if (type != null && type.length() > 0 && !StringUtils.unsafeEquals("screen",type))
		{
			return true;
		}
		return false;
	}

	/**
	 * This method search the element's children for crux meta tags. Those meta tags contains the information
	 * needed to render widgets and informations about the page itself (represented by a {@code Screen} object).
	 * 
	 * <p>For each widget meta tag found, this factory choose a {@code WidgetFactory} that will parse the 
	 * meta tag and build the widget. To find out which {@code WidgetFactory} call, the attribute {@code _type}
	 * from the meta tag element is checked.
	 * @param rootElement
	 */
	void parseDocument(Element rootElement)
	{
		this.parsing = true;
		try
		{
			Date start = null;
			if (Crux.getConfig().enableClientFactoryTracing())
			{// This statement is like this to generate a better code when compiling with GWT compiler. Do not extract the previous command.
				traceOutput = new StringBuilder();;
				start = new Date();
			}
			NodeList<Element> spanElements = rootElement.getElementsByTagName("SPAN");
			FastList<String> widgetIds = new FastList<String>();
			Element screenElement = null;

			int spansLength = spanElements.getLength();
			for (int i=0; i<spansLength; i++)
			{
				Element element = spanElements.getItem(i);
				String type = isCruxMetaElement(element);
				if (type != null)
				{
					if (StringUtils.unsafeEquals("screen",type))
					{
						screenElement = element;
						screen.parse(screenElement);
					}
					else
					{
						widgetIds.add(element.getId());
					}
				}
			}

			FastList<Element> widgets = new FastList<Element>();
			for (int i=0; i<widgetIds.size(); i++)
			{
				String elementId = widgetIds.get(i);
				if (!screen.containsWidget(elementId))
				{
					Element element = DOM.getElementById(elementId);
					if (element != null) // Some elements can be handled by its parent's factory
					{
						try 
						{
							createWidget(element, getMetaElementType(element), screen, widgets);
						}
						catch (Throwable e) 
						{
							Crux.getErrorHandler().handleError(e);
							element.setInnerText(Crux.getMessages().screenFactoryGenericErrorCreateWidget(e.getLocalizedMessage()));
						}
					}
				}
			}
			if (screenElement != null)
			{
				clearScreenMetaTag(screenElement);
			}
			clearWidgetsMetaTags(widgets);
			if (Crux.getConfig().renderWidgetsWithIDs())
			{
				screen.updateWidgetsIds();
			}
			if (Crux.getConfig().enableClientFactoryTracing())
			{
				Date end = new Date();
				traceOutput.append("parseDocument - ("+(end.getTime() - start.getTime())+" ms)<br/>");
				createTraceOutput().setInnerHTML(traceOutput.toString());
				traceOutput = null;
			}
		}
		finally
		{
			this.parsing = false;
		}
		screen.load();
	}

	/**
	 * Return the type of a given crux meta tag element. This type could be {@code "screen"} or 
	 * another string referencing a registered {@code WidgetFactory}
	 * @param cruxMetaElement
	 * @return
	 */
	public String getMetaElementType(Element cruxMetaElement)
	{
		return cruxMetaElement.getAttribute("_type");
	}

	/**
	 * 
	 * @param screenElement
	 */
	private void clearScreenMetaTag(Element screenElement)
	{
		while (screenElement.hasChildNodes())
		{
			Node child = screenElement.getFirstChild();
			screenElement.removeChild(child);
			screenElement.getParentNode().insertBefore(child, screenElement);
		}
		Node parent = screenElement.getParentNode();
		if (parent != null)
		{
			parent.removeChild(screenElement);
		}
	}
	
	/**
	 * 
	 * @param widgets
	 */
	private void clearWidgetsMetaTags(FastList<Element> widgets)
	{
		for (int i=0; i<widgets.size(); i++) 
		{
			Element element = widgets.get(i);
			String widgetId = element.getId();
			if (widgetId != null && widgetId.length() >= 0)
			{
				element = DOM.getElementById(widgetId); // Evita que elementos reanexados ao DOM sejam esquecidos
				Element parent;
				if (element != null && (parent = element.getParentElement())!= null)
				{
					parent.removeChild(element);
				}
			}
		}
	}

	/**
	 * 
	 */
	private void create()
	{
		screen = new Screen(getScreenId());
		Element body = RootPanel.getBodyElement();
		parseDocument(body);
	}
	
	/**
	 * @return
	 */
	private Element createTraceOutput()
	{
		Element traceDiv = DOM.createDiv();
		UIObject.setVisible(traceDiv, false);
		RootPanel.getBodyElement().appendChild(traceDiv);
		return traceDiv;
	}
	
	/**
	 * 
	 * @param element
	 * @param screen
	 * @param widgetsElementsAdded
	 * @return
	 * @throws InterfaceConfigException
	 */
	private Widget createWidget(Element element, String widgetType, Screen screen, FastList<Element> widgetsElementsAdded) throws InterfaceConfigException
	{
		String widgetId = element.getId();
		if (widgetId == null || widgetId.length() == 0)
		{
			throw new InterfaceConfigException(Crux.getMessages().screenFactoryWidgetIdRequired());
		}
		Widget widget = screen.getWidget(widgetId);
		if (widget != null)
		{
			return widget;
		}
		DeclarativeWidgetFactory widgetFactory = (DeclarativeWidgetFactory) registeredWidgetFactories.getWidgetFactory(widgetType);
		if (!widgetFactory.isAttachToDOM())
		{
			widget = newWidget(element, widgetId, widgetType);
		}
		else
		{
			Element parentElement = getParentElement(element);
			if (parentElement != null)
			{
				widget = createWidgetWithExplicitParent(element, parentElement, screen, widgetsElementsAdded, widgetId, widgetType);
			}
			else
			{
				widget = createWidgetWithoutExplicitParent(element, parentElement, widgetId, widgetType);
			}
		}
		if (widget != null)
		{
			widgetsElementsAdded.add(element);
		}
		return widget;
	}
	
	/**
	 * 
	 * @param element
	 * @param screen
	 * @param widgetsElementsAdded
	 * @param widgetId
	 * @param parentElement
	 * @return
	 * @throws InterfaceConfigException
	 */
	@SuppressWarnings("unchecked")
	private Widget createWidgetWithExplicitParent(Element element, Element parentElement, Screen screen, FastList<Element> widgetsElementsAdded, String widgetId, String widgetType) throws InterfaceConfigException
	{
		Widget widget;
		Widget parent = screen.getWidget(parentElement.getId());
		String parentWidgetType = getMetaElementType(parentElement);
		if (parent == null)
		{
			String hasWidgetParentId = HasWidgetsHandler.getHasWidgetsId(parentElement);
			if (hasWidgetParentId != null && hasWidgetParentId.length() > 0)
			{
				parent = screen.getWidget(hasWidgetParentId);
			}
			if (parent == null)
			{
				parent = createWidget(parentElement, parentWidgetType, screen, widgetsElementsAdded);
			}
		}
		
		WidgetFactory<?> parentWidgetFactory = registeredWidgetFactories.getWidgetFactory(parentWidgetType);
		if (parentWidgetFactory instanceof HasWidgetsFactory)
		{
			widget = newWidget(element, widgetId, widgetType);
			((HasWidgetsFactory<Widget>)parentWidgetFactory).add(parent, widget, parentElement, element);
		}
		else if (parentWidgetFactory instanceof LazyFactory)
		{
			widget = null;
		}
		else
		{
			widget = screen.getWidget(widgetId);
		}
		return widget;
	}
	
	/**
	 * 
	 * @param element
	 * @param widgetId
	 * @return
	 * @throws InterfaceConfigException
	 */
	private Widget createWidgetWithoutExplicitParent(Element element, Element parentElement, String widgetId, String widgetType) throws InterfaceConfigException
	{
		Element panelElement;
		panelElement = getEnclosingPanelElement(element, widgetId);
		Widget widget = newWidget(element, widgetId, widgetType);

		Panel panel;
		if (widget instanceof RequiresResize)
		{
			boolean hasSize = (WidgetFactory.hasWidth(element) && WidgetFactory.hasHeight(element));
			if (RootPanel.getBodyElement().equals(element.getParentElement()) && !hasSize)
			{
				panel = RootLayoutPanel.get();
			}
			else
			{
				ensureElementIdExists(panelElement);
				panel = RootPanel.get(panelElement.getId());
				if (!hasSize)
				{
					GWT.log(Crux.getMessages().screenFactoryLayoutPanelWithoutSize(widgetId), null);
				}
			}
		}
		else
		{
			if (!StringUtils.isEmpty(panelElement.getId()) && Screen.get(panelElement.getId()) != null)
			{
				panel = (Panel) Screen.get(panelElement.getId());
			}
			else
			{
				ensureElementIdExists(panelElement);
				panel = RootPanel.get(panelElement.getId());
			}
		}
		panel.add(widget);
		return widget;
	}

	/**
	 * @param panelElement
	 */
	private void ensureElementIdExists(Element panelElement)
	{
		if (StringUtils.isEmpty(panelElement.getId()))
		{
			panelElement.setId(WidgetFactory.generateNewId());
		}
	}

	/**
	 * 
	 * @param element
	 * @return
	 */
	private Element getParentElement(Element element) 
	{
		Element elementParent = element.getParentElement();
		while (elementParent != null && !StringUtils.unsafeEquals("BODY",elementParent.getTagName()))
		{
			if (isValidCruxWidgetMetaTag(elementParent))// || HasWidgetsHandler.isValidHasWidgetsPanel(elementParent))TODO check this
			{
				return elementParent;
			}
			elementParent = elementParent.getParentElement();
		}
			
		return null;	
	}

	/**
	 * Check if the span element provided represents a crux meta tag element. If so, return
	 * the crux meta tag type
	 * @param element a span element
	 * @return
	 */
	private String isCruxMetaElement(Element spanElement)
	{
		String type = getMetaElementType(spanElement);
		if (type != null && type.length() > 0)
		{
			return type;
		}
		return null;
	}
}
