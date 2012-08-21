/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.core.client.utils;

import java.util.ArrayList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.ScriptElement;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class HTMLUtils
{
	/**
	 * 
	 * @param element
	 */
	public static void evaluateScripts(Element element)
	{
		NodeList<Element> scriptElements = element.getElementsByTagName("script");
		
		if (scriptElements != null)
		{
			ArrayList<Element> scripts = new ArrayList<Element>();
			for (int i = 0; i < scriptElements.getLength(); i++)
			{
				scripts.add(scriptElements.getItem(i));
			}
			for (int i = 0; i < scripts.size(); i++)
			{
				ScriptElement script = scripts.get(i).cast();
				ScriptElement cloneScript = script.cloneNode(!script.hasAttribute("src")).cast();
				script.getParentNode().replaceChild(cloneScript, script);
			}
		}
	}
}
