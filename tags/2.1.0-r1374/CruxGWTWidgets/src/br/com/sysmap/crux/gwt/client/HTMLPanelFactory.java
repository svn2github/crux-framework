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
package br.com.sysmap.crux.gwt.client;

import java.util.ArrayList;
import java.util.List;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.HasWidgetsFactory;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyTag;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
@DeclarativeFactory(id="HTMLPanel", library="gwt")
public class HTMLPanelFactory extends ComplexPanelFactory<HTMLPanel> implements HasWidgetsFactory<HTMLPanel>
{
	@Override
	public HTMLPanel instantiateWidget(Element element, String widgetId) 
	{
		HTMLPanel ret = new HTMLPanel("");
		List<Node> children = extractChildrenInReverseOrder(element);
		for (Node node : children)
		{
			ret.getElement().appendChild(node);
		}
		ret.getElement().setAttribute("_hasWidgetsPanel", widgetId);
		ret.getElement().setAttribute("_type", "gwt_HTMLPanel");

		return ret;
	}

	/**
	 * @see br.com.sysmap.crux.core.client.screen.HasWidgetsFactory#add(com.google.gwt.user.client.ui.Widget, com.google.gwt.user.client.ui.Widget, com.google.gwt.dom.client.Element, com.google.gwt.dom.client.Element)
	 */
	public void add(HTMLPanel parent, Widget child, Element parentElement, Element childElement) 
	{
		parent.add(child, getParentElement(childElement).getId());
	}
	
	@Override
	@TagChildren({
		@TagChild(value=ContentProcessor.class, autoProcess=false)
	})
	public void processChildren(WidgetFactoryContext<HTMLPanel> context) throws InterfaceConfigException
	{
	}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", type=AnyTag.class)
	public static class ContentProcessor extends WidgetChildProcessor<HTMLPanel> {}
	
	/**
	 * 
	 * @param element
	 * @return
	 */
	protected List<Node> extractChildrenInReverseOrder(Element element)
	{
		List<Node> result = new ArrayList<Node>();
		
		NodeList<Node> childNodes = element.getChildNodes();
		
		for (int i=0; i< childNodes.getLength(); i++)
		{
			Node node = childNodes.getItem(i);
			result.add(0,node);
		}

		for (Node node : result)
		{
			if (node.getParentNode() != null)
			{
				node.getParentNode().removeChild(node);
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param element
	 * @param acceptsNoChild
	 * @return
	 * @throws InterfaceConfigException
	 */
	protected static List<Element> ensureChildrenSpans(Element element, boolean acceptsNoChild) throws InterfaceConfigException
	{
		return new ArrayList<Element>();
	}
	
}
