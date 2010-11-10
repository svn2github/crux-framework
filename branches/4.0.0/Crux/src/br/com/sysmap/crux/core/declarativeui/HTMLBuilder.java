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
package br.com.sysmap.crux.core.declarativeui;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.WidgetFactory.WidgetFactoryContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.scanner.screen.config.WidgetConfig;
import br.com.sysmap.crux.core.utils.HTMLUtils;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class HTMLBuilder
{
	private static DeclarativeUIMessages messages = (DeclarativeUIMessages)MessagesFactory.getMessages(DeclarativeUIMessages.class);

	private static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";
	private static final String WIDGETS_NAMESPACE_PREFIX= "http://www.sysmap.com.br/crux/";
	private static final String CRUX_CORE_NAMESPACE= "http://www.sysmap.com.br/crux";
	private DocumentBuilder documentBuilder;
	private Map<String, String> referenceWidgetsList;

	private Set<String> htmlPanelContainers;

	/**
	 * @throws HTMLBuilderException 
	 * 
	 */
	public HTMLBuilder() throws HTMLBuilderException
    {
		try
        {
	        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	        builderFactory.setNamespaceAware(false);
	        builderFactory.setIgnoringComments(true);
	        
	        documentBuilder = builderFactory.newDocumentBuilder();
	        referenceWidgetsList = generateReferenceWidgetsList();
	        htmlPanelContainers = generateHtmlPanelWidgetsList();
        }
        catch (Exception e)
        {
        	throw new HTMLBuilderException(e.getMessage(), e);
        }
    }
	
	/**
	 * @param cruxPageDocument
	 * @param out
	 * @throws HTMLBuilderException
	 */
	public void build(Document cruxPageDocument, Writer out) throws HTMLBuilderException
	{
		try
        {
			Document htmlDocument = createHTMLDocument(cruxPageDocument);
			translateDocument(cruxPageDocument, htmlDocument);
	        write(htmlDocument, out);
        }
        catch (IOException e)
        {
        	throw new HTMLBuilderException(e.getMessage(), e);
        }
	}

	/**
	 * @param cruxPageDocument
	 * @param htmlDocument
	 */
	private void translateDocument(Document cruxPageDocument, Document htmlDocument)
    {
		Element cruxPageElement = cruxPageDocument.getDocumentElement();
		Node htmlElement = htmlDocument.importNode(cruxPageElement, false);
		htmlDocument.appendChild(htmlElement);
		translateDocument(cruxPageElement, htmlElement, htmlDocument);
    }

	/**
	 * @param cruxPageElement
	 * @param htmlElement
	 * @param htmlDocument
	 */
	private void translateDocument(Node cruxPageElement, Node htmlElement, Document htmlDocument)
    {
		NodeList childNodes = cruxPageElement.getChildNodes();
		if (childNodes != null)
		{
			for (int i=0; i<childNodes.getLength(); i++)
			{
				Node child = childNodes.item(i);
				String namespaceURI = child.getNamespaceURI();
				
				if (namespaceURI != null && namespaceURI.equals(CRUX_CORE_NAMESPACE))
				{
				    translateCruxCoreElements((Element)child, (Element)htmlElement, htmlDocument);
				}
				else if (namespaceURI != null && namespaceURI.startsWith(WIDGETS_NAMESPACE_PREFIX))
				{
					translateCruxInnerTags((Element)child, (Element)htmlElement, htmlDocument);
				}
				else
				{
					Node htmlChild = htmlDocument.importNode(child, false);
					htmlElement.appendChild(htmlChild);
					translateDocument(child, htmlChild, htmlDocument);
					String localName = child.getLocalName();
					if (namespaceURI != null && namespaceURI.equals(XHTML_NAMESPACE) && localName != null && localName.equals("body"))
					{
						//generateCruxMetaData
					}
				}
			}
		}
    }

	/**
	 * @param cruxPageElement
	 * @param htmlElement
	 * @param htmlDocument
	 */
	private void translateCruxInnerTags(Element cruxPageElement, Element htmlElement, Document htmlDocument)
    {
		
		if (isHtmlContainerWidget(cruxPageElement) || ((isReferencedWidget(cruxPageElement) || isWidget(cruxPageElement)) && isHTMLChild(cruxPageElement)))
		{
			Element widgetHolder = htmlDocument.createElement("div");
			widgetHolder.setAttribute("id", "_crux_"+cruxPageElement.getAttribute("id"));
			htmlElement.appendChild(widgetHolder);
			translateDocument(cruxPageElement, widgetHolder, htmlDocument);
		}
		else
		{
			translateDocument(cruxPageElement, htmlElement, htmlDocument);
		}
    }

	/**
	 * @param node
	 * @param elementName
	 * @return
	 */
	private String getTagName(Node node, String elementName)
	{
		String libraryName = getLibraryName(node);
		Node parentNode = node.getParentNode();
		if (StringUtils.isEmpty(libraryName) || isWidget(parentNode))
		{
			return libraryName+"_"+parentNode.getLocalName()+"_"+elementName;
		}
		else
		{
			return getTagName(parentNode, elementName);
		}
	}
	
	/**
	 * @param node
	 * @return
	 */
	private boolean isHTMLChild(Node node)
	{
		Node parentNode = node.getParentNode();
		String namespaceURI = parentNode.getNamespaceURI();
		return (namespaceURI != null && namespaceURI.equals(XHTML_NAMESPACE) || isHtmlContainerWidget(parentNode));
	}
	
	/**
	 * @param cruxPageElement
	 * @param htmlElement
	 * @param htmlDocument
	 */
	private void translateCruxCoreElements(Element cruxPageElement, Element htmlElement, Document htmlDocument)
    {
	    String nodeName = cruxPageElement.getLocalName();
	    if (nodeName.equals("splashScreen"))
	    {
	    	translateSplashScreen(cruxPageElement, htmlElement, htmlDocument);
	    }
	    else if (nodeName.equals("screen"))
	    {
	    	translateDocument(cruxPageElement, htmlElement, htmlDocument);
	    }
    }

	/**
	 * @param cruxPageNode
	 * @param htmlNode
	 * @param htmlDocument
	 */
	private void translateSplashScreen(Element cruxPageNode, Element htmlNode, Document htmlDocument)
	{
		Element splashScreen = htmlDocument.createElement("div");
		splashScreen.setAttribute("id", "cruxSplashScreen");
		
		String style = cruxPageNode.getAttribute("style");
		if (!StringUtils.isEmpty(style))
		{
			splashScreen.setAttribute("style", style);
		}
		String transactionDelay = cruxPageNode.getAttribute("transactionDelay");
		if (!StringUtils.isEmpty(transactionDelay))
		{
			splashScreen.setAttribute("transactionDelay", transactionDelay);
		}
		htmlNode.appendChild(splashScreen);
	}
	
	/**
	 * @param cruxPageDocument
	 * @return
	 */
	private Document createHTMLDocument(Document cruxPageDocument)
    {
	    Document htmlDocument;
		DocumentType doctype = cruxPageDocument.getDoctype();
		if (doctype != null)
		{
			htmlDocument = documentBuilder.getDOMImplementation().createDocument(XHTML_NAMESPACE, "html", doctype);
		}
		else
		{
			htmlDocument = documentBuilder.newDocument();
		}
	    return htmlDocument;
    }
	
	/**
	 * @param node
	 * @return
	 */
	private boolean isWidget(Node node)
	{
		if (node instanceof Element)
		{
			return isWidget(node.getLocalName(), getLibraryName(node));
		}
		
		return false;
	}
	
	/**
	 * @param localName
	 * @param libraryName
	 * @return
	 */
	private boolean isWidget(String localName, String libraryName)
    {
	    return (WidgetConfig.getClientClass(libraryName, localName) != null);
    }

	/**
	 * @param tagName
	 * @return
	 */
	private boolean isReferencedWidget(String tagName)
    {
	    return referenceWidgetsList.containsKey(tagName);
    }

	/**
	 * @param localName
	 * @param libraryName
	 * @return
	 */
	private boolean isHtmlContainerWidget(String localName, String libraryName)
	{
	    return htmlPanelContainers.contains(libraryName+"_"+localName);
	}
	
	/**
	 * @param node
	 * @return
	 */
	private boolean isHtmlContainerWidget(Node node)
	{
		if (node instanceof Element)
		{
			return isHtmlContainerWidget(node.getLocalName(), getLibraryName(node));
		}
		return false;
	}

	/**
	 * @param node
	 * @return
	 */
	private boolean isReferencedWidget(Node node)
    {
	    return isReferencedWidget(getTagName(node, node.getLocalName()));
    }

	/**
	 * @param node
	 * @return
	 */
	private String getLibraryName(Node node)
	{
		String namespaceURI = node.getNamespaceURI();
		
		if (namespaceURI != null && namespaceURI.startsWith(WIDGETS_NAMESPACE_PREFIX))
		{
			return namespaceURI.substring(WIDGETS_NAMESPACE_PREFIX.length());
		}
		return null;
	}
	
	/**
	 * @param out
	 * @throws IOException
	 */
	private void write(Document htmlDocument, Writer out) throws IOException
	{
		DocumentType doctype = htmlDocument.getDoctype();
		
		if (doctype != null)
		{
			out.write("<!DOCTYPE " + doctype.getName() + ">\n");
		}
	    HTMLUtils.write(htmlDocument.getDocumentElement(), out);
	}
	
	
	/**
	 * 
	 * @return
	 * @throws HTMLBuilderException 
	 */
	private Map<String, String> generateReferenceWidgetsList() throws HTMLBuilderException
	{
		Map<String, String> referencedWidgets = new HashMap<String, String>();
		Set<String> registeredLibraries = WidgetConfig.getRegisteredLibraries();
		for (String library : registeredLibraries)
		{
			Set<String> factories = WidgetConfig.getRegisteredLibraryFactories(library);
			for (String widget : factories)
			{
				try
				{
					Class<?> clientClass = Class.forName(WidgetConfig.getClientClass(library, widget));
					Method method = clientClass.getMethod("processChildren", new Class[]{WidgetFactoryContext.class});
					generateReferenceWidgetsListFromTagChildren(referencedWidgets, method.getAnnotation(TagChildren.class), 
																		library, widget, new HashSet<String>());
				}
				catch (Exception e)
				{
					throw new HTMLBuilderException(messages.transformerErrorGeneratingWidgetsReferenceList(), e);
				}
			}
		}
		
		return referencedWidgets;
	}

	/**
	 * 
	 * @param widgetList 
	 * @param tagChildren
	 * @param parentLibrary
	 * @throws HTMLBuilderException 
	 */
	private void generateReferenceWidgetsListFromTagChildren(Map<String, String> referencedWidgets, TagChildren tagChildren, 
																    String parentLibrary, String parentWidget, Set<String> added) throws HTMLBuilderException
	{
		if (tagChildren != null)
		{
			for (TagChild child : tagChildren.value())
			{
				Class<? extends WidgetChildProcessor<?>> processorClass = child.value();
				if (!added.contains(processorClass.getCanonicalName()))
				{
					added.add(processorClass.getCanonicalName());
					TagChildAttributes childAttributes = processorClass.getAnnotation(TagChildAttributes.class);
					if (childAttributes!= null)
					{
						if (WidgetFactory.class.isAssignableFrom(childAttributes.type()))
						{
							DeclarativeFactory declarativeFactory = childAttributes.type().getAnnotation(DeclarativeFactory.class);
							if (declarativeFactory != null)
							{
								referencedWidgets.put(parentLibrary+"_"+parentWidget+"_"+childAttributes.tagName(),
													  declarativeFactory.library()+"_"+declarativeFactory.id());
							}
						}
					}
					
					try
					{
						Method method = processorClass.getMethod("processChildren", new Class[]{WidgetChildProcessorContext.class});
						generateReferenceWidgetsListFromTagChildren(referencedWidgets, method.getAnnotation(TagChildren.class), 
																	parentLibrary, parentWidget, added);
					}
					catch (Exception e)
					{
						throw new HTMLBuilderException(messages.transformerErrorGeneratingWidgetsList(), e);
					}
				}
			}
		}				
	}
	
	/**
	 * 
	 * @return
	 * @throws HTMLBuilderException 
	 */
	private Set<String> generateHtmlPanelWidgetsList() throws HTMLBuilderException
	{
		Set<String> htmlContainers = new HashSet<String>();
		Set<String> registeredLibraries = WidgetConfig.getRegisteredLibraries();
		for (String library : registeredLibraries)
		{
			Set<String> factories = WidgetConfig.getRegisteredLibraryFactories(library);
			for (String widget : factories)
			{
				try
				{
					Class<?> clientClass = Class.forName(WidgetConfig.getClientClass(library, widget));
					DeclarativeFactory factory = clientClass.getAnnotation(DeclarativeFactory.class);
					if (factory.htmlContainer())
					{
						htmlContainers.add(library+"_"+widget);				
					}
				}
				catch (Exception e)
				{
					throw new HTMLBuilderException(messages.transformerErrorGeneratingWidgetsReferenceList(), e);
				}
			}
		}
		
		return htmlContainers;
	}	
}
