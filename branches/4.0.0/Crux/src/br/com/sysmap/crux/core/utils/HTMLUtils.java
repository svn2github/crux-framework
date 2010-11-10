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
package br.com.sysmap.crux.core.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * Helper class for HTML generation, according with {@link http://dev.w3.org/html5/spec/syntax.html#elements-0}
 * @author Thiago da Rosa de Bustamante
 *
 */
public class HTMLUtils
{
	private static Set<String> voidElements = new HashSet<String>();
	private static Set<String> rawTextElements = new HashSet<String>();
	private static Set<String> rcDataElements = new HashSet<String>();

	static
	{
		voidElements.addAll(Arrays.asList(new String[]{
				"area", "base", "br", "col", "command", "embed", "hr", "img", "input", "keygen", "link", "meta", "param", "source", "track", "wbr"
		}));
		
		rawTextElements.addAll(Arrays.asList(new String[]{"style", "script"}));
		rcDataElements.addAll(Arrays.asList(new String[]{"textarea", "title"}));
	}
	
	
	/**
	 * @param document
	 */
	private HTMLUtils()
    {
    }

	/**
	 * @param node
	 * @param out
	 * @throws IOException
	 */
	public static void write(Node node, Writer out) throws IOException
	{
		if (node.getNodeType() == Node.ELEMENT_NODE)
		{
			String name = ((Element)node).getNodeName().toLowerCase();
			out.write("<");
			out.write(name);
			writeAttributes(node, out);
			out.write(">");

			if (!voidElements.contains(name))
			{
				NodeList children = node.getChildNodes();
				if (children != null)
				{
					for (int i=0; i< children.getLength(); i++)
					{
						Node child = children.item(i);
						if (rawTextElements.contains(name))
						{
							writeRawText(child, out);
						}
						else if (rcDataElements.contains(name))
						{
							writeRcData(child, out);
						}
						else
						{
							write(child, out);
						}
					}
				}
				out.write("</");
				out.write(name);
				out.write(">");
			}
		}
		else if (node.getNodeType() == Node.TEXT_NODE)
		{
			out.write(escapeHTML(node.getNodeValue()));
		}
	}
	
	/**
	 * @param child
	 * @param out
	 * @throws DOMException
	 * @throws IOException
	 */
	public static void writeRcData(Node child, Writer out) throws DOMException, IOException
    {
	    out.write(child.getNodeValue()); 
    }
	
	/**
	 * @param child
	 * @param out
	 * @throws IOException 
	 * @throws DOMException 
	 */
	public static void writeRawText(Node child, Writer out) throws DOMException, IOException
    {
	    out.write(child.getNodeValue()); 
    }
	
	/**
	 * @param node
	 * @param out
	 * @throws IOException
	 */
	public static void writeAttributes(Node node, Writer out) throws IOException
    {
	    NamedNodeMap attributes = node.getAttributes();
	    for (int i=0; i<attributes.getLength(); i++)
	    {
	    	Node attribute = attributes.item(i);
	    	String name = attribute.getNodeName();
	    	if (!name.toLowerCase().startsWith("xmlns"))
	    	{
	    		out.write(" "+name+"=\""+escapeHTMLAttribute(attribute.getNodeValue())+"\"");
	    	}
	    }
    }

	/**
	 * @param s
	 * @return
	 */
	public static String escapeHTML(String s)
	{
		return escapeHTML(s, true);
	}
	
	/**
	 * @param s
	 * @return
	 */
	public static String escapeHTMLAttribute(String s)
	{
		return escapeHTML(s, false);
	}

	/**
	 * @param s
	 * @return
	 */
	private static String escapeHTML(String s, boolean trim)
	{
		StringBuilder sb = new StringBuilder();
		s = s.replaceAll("&#10;", " ");
		s = s.replaceAll("&#13;", " ");
		int n = s.length();
		boolean lastIsSpace = false;
		
		for (int i = 0; i < n; i++)
		{
			char c = s.charAt(i);
			if (c != ' ' && c != '\n' && c != '\r' && c != '\t')
			{
				lastIsSpace = false;
			}
			switch (c)
			{
				case '\n': 
				case '\r': 
				case '\t': 
				case ' ': 
					if (!lastIsSpace)
					{
						sb.append(" ");
						lastIsSpace = true;
					}
				break;
				case '&': sb.append("&#38;"); break;
				case '"': sb.append("&quot;"); break;
				case '\'': sb.append("&#39;"); break;
				case '<': sb.append("&lt;"); 
				case '>': sb.append("&gt;"); break;
	
				default: sb.append(c); break;
			}
		}
		return (trim?sb.toString().trim():sb.toString());
	}	
}
