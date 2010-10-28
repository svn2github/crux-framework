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
import br.com.sysmap.crux.core.client.collection.FastMap;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.utils.JSONUtils;
import br.com.sysmap.crux.core.client.utils.StyleUtils;

import com.google.gwt.dom.client.Element;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * Factory for gwt widgets. It creates widgets based on a meta data array contained
 * in the host HTML page. It provides a declarative way to create widgets.
 * 
 * @author Thiago da Rosa de Bustamante
 */
public abstract class WidgetFactory <T extends Widget>
{
	private static int currentId = 0;
	
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 * @param <W>
	 */
	public static class WidgetFactoryContext<W>
	{
		private W widget;
		private JSONObject element;
		private String widgetId;
		private FastMap<Object> attributes;
		
		WidgetFactoryContext(W widget, JSONObject metaElem, String widgetId)
		{
			this.widget = widget;
			this.element = metaElem;
			this.widgetId = widgetId;
			this.attributes = new FastMap<Object>();
		}
		
		public W getWidget()
		{
			return widget;
		}
		public JSONObject getElement()
		{
			return element;
		}
		public String getWidgetId()
		{
			return widgetId;
		}
		public Object getAttribute(String key)
		{
			return attributes.get(key);
		}
		public void setAttribute(String key, Object value)
		{
			this.attributes.put(key, value);
		}
		public void clearAttributes()
		{
			this.attributes.clear();
		}
		public void removeAttribute(String key)
		{
			this.attributes.remove(key);
		}
		public String readWidgetProperty(String propertyName)
		{
			return WidgetFactory.getProperty(element, propertyName);
		}

		public boolean containsAttribute(String key)
		{
			return attributes.containsKey(key);
		}
	}
	
	/**
	 * 
	 * @param element
	 * @param widgetId
	 * @return
	 * @throws InterfaceConfigException
	 */
	public final T createWidget(JSONObject metaElem, String widgetId) throws InterfaceConfigException
	{
		return createWidget(metaElem, widgetId, true);
	}
	
	/**
	 * 
	 * @param element
	 * @param widgetId
	 * @param addToScreen
	 * @return
	 * @throws InterfaceConfigException
	 */
	public T createWidget(JSONObject metaElem, String widgetId, boolean addToScreen) throws InterfaceConfigException
	{
		WidgetFactoryContext<T> context = createContext(metaElem, widgetId, addToScreen);
		if (context != null)
		{
			processAttributes(context);
			processEvents(context);
			processChildren(context);
			postProcess(context);
			return context.getWidget();
		}
		return null;
	}

	/**
	 * @param element
	 * @param widgetId
	 * @param addToScreen
	 * @return
	 * @throws InterfaceConfigException
	 */
	protected WidgetFactoryContext<T> createContext(JSONObject metaElem, String widgetId, boolean addToScreen) throws InterfaceConfigException
	{
		T widget = instantiateWidget(metaElem, widgetId);
		if (widget != null)
		{
			if(addToScreen)
			{
				Screen.add(widgetId, widget);
			}			

			return new WidgetFactoryContext<T>(widget, metaElem, widgetId);
		}
		return null;
	}
	
	/**
	 * Process element children
	 * @param widget
	 * @param parentElement
	 * @param widgetId
	 * @throws InterfaceConfigException 
	 */
	public void processChildren(WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
	}

	/**
	 * Process element children
	 * @param widget
	 * @param parentElement
	 * @param widgetId
	 * @throws InterfaceConfigException 
	 */
	public void postProcess(WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
	}

	/**
	 * Process widget attributes
	 * @param element page DOM element representing the widget (Its &lt;span&gt; tag)
	 * @throws InterfaceConfigException 
	 */
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="id", required=true),
		@TagAttributeDeclaration("width"),
		@TagAttributeDeclaration("height"),
		@TagAttributeDeclaration("styleName"),
		@TagAttributeDeclaration(value="visible", type=Boolean.class),
		@TagAttributeDeclaration(value="tooltip", supportsI18N=true),
		@TagAttributeDeclaration("style")
	})
	public void processAttributes(WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
		String styleName = context.readWidgetProperty("styleName");
		T widget = context.getWidget();
		if (styleName != null && styleName.length() > 0){
			widget.setStyleName(styleName);
		}
		String visible = context.readWidgetProperty("visible");
		if (visible != null && visible.length() > 0){
			widget.setVisible(Boolean.parseBoolean(visible));
		}
		String style = context.readWidgetProperty("style");
		if (style != null && style.length() > 0)
		{
			String[] styleAttributes = style.split(";");
			for (int i=0; i<styleAttributes.length; i++)
			{
				String[] attr = styleAttributes[i].split(":");
				if (attr != null && attr.length == 2)
				{
					StyleUtils.addStyleProperty(widget.getElement(), attr[0], attr[1]);
				}
			}
		}
		String width = context.readWidgetProperty("width");
		if (width != null && width.length() > 0){
			widget.setWidth(width);
		}
		String height = context.readWidgetProperty("height");
		if (height != null && height.length() > 0){
			widget.setHeight(height);
		}
		String tooltip = context.readWidgetProperty("tooltip");
		if (tooltip != null && tooltip.length() > 0)
		{
			widget.setTitle(ScreenFactory.getInstance().getDeclaredMessage(tooltip));
		}
	}
	
	/**
	 * Process widget events
	 * @param element page DOM element representing the widget (Its &lt;span&gt; tag)
	 * @throws InterfaceConfigException
	 */
	@TagEventsDeclaration({
		@TagEventDeclaration("onLoadWidget")
	})
	public void processEvents(WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
		final Event eventLoad = EvtBind.getWidgetEvent(context.getElement(), "onLoadWidget");
		if (eventLoad != null)
		{
			addScreenLoadedHandler(new ScreenLoadHandler()
			{
				public void onLoad(ScreenLoadEvent event)
				{
					Events.callEvent(eventLoad, event);
				}
			});
		}
	}
	
	/**
	 * Retrieve a Crux widget attribute from its declaring meta element
	 * @param element the widget metadata element
	 * @param propertyName the name of the attribute
	 * @return attribute value
	 */
	public static String getProperty(JSONObject metaElem, String propertyName)
	{
		return JSONUtils.getStringProperty(metaElem, propertyName);
	}
	
	/**Retrieve the widget child element name
	 * @param childElement the span element representing the child
	 * @return child name
	 */
	public static String getChildName(JSONObject childElement)
	{
		return JSONUtils.getStringProperty(childElement, "childTag");
	}
	
	/**
	 * Adds an event handler, called when the screen is completely loaded
	 * @param loadHandler
	 */
	protected void addScreenLoadedHandler(ScreenLoadHandler loadHandler)
	{
		Screen.get().addLoadHandler(loadHandler);
	}

	/**
	 * Used by widgets that need to create new widgets as children, like tree. 
	 * 
	 * @param element
	 * @param widgetId
	 * @return
	 * @throws InterfaceConfigException
	 */
	protected static Widget createChildWidget(JSONObject metaElem) throws InterfaceConfigException
	{
		ScreenFactory factory = ScreenFactory.getInstance();
		assert(metaElem.containsKey("id")):Crux.getMessages().screenFactoryWidgetIdRequired();
		String widgetId = JSONUtils.getUnsafeStringProperty(metaElem,"id");
		return factory.newWidget(metaElem, widgetId, factory.getMetaElementType(metaElem));
	}

	/**
	 * Used by widgets that need to create new widgets as children, like tree. 
	 * 
	 * @param element
	 * @param widgetId
	 * @param widgetType
	 * @return
	 * @throws InterfaceConfigException
	 */
	protected static Widget createChildWidget(JSONObject metaElem, String widgetId, String widgetType) throws InterfaceConfigException
	{
		return ScreenFactory.getInstance().newWidget(metaElem, widgetId, widgetType);
	}
	
	/**
	 * Creates a sequential id
	 * @return
	 */
	protected static String generateNewId() 
	{
		return "_crux_" + (++currentId );
	}
	
	/**
	 * Returns the element which is the father of the given one. If it does not have an id, creates a random for it
	 * @param child
	 * @return
	 *
	protected Element getParentElement(Element child)
	{
		Element parent = child.getParentElement();
		
		String id = parent.getId();
		if(id == null || id.trim().length() == 0)
		{
			parent.setId(generateNewId());
		}
		
		return parent;
	}
	
	/**
	 * @param widgetId
	 * @return
	 */
	protected Element getEnclosingPanelElement(String widgetId)
	{
		return ScreenFactory.getInstance().getEnclosingPanelElement(widgetId);
	}
	
	/**
	 * 
	 * @param metaElem
	 * @return
	 */
	protected static boolean isWidget(JSONObject metaElem)
	{
		return ScreenFactory.getInstance().isValidWidget(metaElem);
	}
	
	/**
	 * @param lazy
	 * @param dependentId
	 */
	protected static void addLazyWidgetDependency(String lazy, String dependentId)
	{
		ScreenFactory.getInstance().getScreen().addLazyWidgetDependency(lazy, dependentId);
	}
	
	/**
	 * @param metaElem
	 * @param acceptsNoChild
	 * @return
	 */
	protected static JSONObject ensureFirstChild(JSONObject metaElem, boolean acceptsNoChild) throws InterfaceConfigException
	{
		assert(acceptsNoChild || metaElem.containsKey("children")):Crux.getMessages().widgetFactoryEnsureChildrenSpansEmpty();
		if (acceptsNoChild && !metaElem.containsKey("children"))
		{
			return null;
		}
		JSONArray children = metaElem.get("children").isArray();
		assert(acceptsNoChild || (children != null && children.size()>0)):Crux.getMessages().widgetFactoryEnsureChildrenSpansEmpty();
		JSONObject firstChild = children.get(0).isObject();
		assert(acceptsNoChild || firstChild != null):Crux.getMessages().widgetFactoryEnsureChildrenSpansEmpty();
		return firstChild;
	}
	
	/**
	 * 
	 * @param metaElem
	 * @param acceptsNoChild
	 * @return
	 */
	protected static String ensureTextChild(JSONObject metaElem, boolean acceptsNoChild)
	{
		String result = JSONUtils.getStringProperty(metaElem, "_text");
		assert(acceptsNoChild || (result != null && result.length() > 0)):Crux.getMessages().widgetFactoryEnsureTextChildEmpty();
		return result;
	}
	
	/**
	 * @param metaElem
	 * @param acceptsNoChild
	 * @return
	 */
	protected static JSONArray ensureChildren(JSONObject metaElem, boolean acceptsNoChild) 
	{
		assert(acceptsNoChild || metaElem.containsKey("children")):Crux.getMessages().widgetFactoryEnsureChildrenSpansEmpty();
		
		if (acceptsNoChild && !metaElem.containsKey("children"))
		{
			return null;
		}
		JSONArray children = metaElem.get("children").isArray();
		assert(acceptsNoChild || (children != null && children.size()>0 && children.get(0).isObject()!=null)):Crux.getMessages().widgetFactoryEnsureChildrenSpansEmpty();
		return children;
	}
	
	/**
	 * 
	 * @param element
	 * @return
	 */
	protected JSONObject ensureWidget(JSONObject metaElem) 
	{
		assert(isWidget(metaElem)):Crux.getMessages().widgetFactoryEnsureWidgetFail();
		return metaElem;
	}

	/**
	 * 
	 * @param metaElem
	 * @return
	 */
	protected static boolean hasWidth(JSONObject metaElem)
	{
		String width = JSONUtils.getStringProperty(metaElem, "width");
		return width != null && (width.length() > 0);
	}
	
	/**
	 * 
	 * @param metaElem
	 * @return
	 */
	protected static boolean hasHeight(JSONObject metaElem)
	{
		String width = JSONUtils.getStringProperty(metaElem, "height");
		return width != null && (width.length() > 0);
	}
	
	public abstract T instantiateWidget(JSONObject metaElem, String widgetId) throws InterfaceConfigException;
}
