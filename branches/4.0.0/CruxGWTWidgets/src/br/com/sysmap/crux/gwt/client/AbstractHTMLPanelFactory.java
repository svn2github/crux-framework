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

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.collection.Array;
import br.com.sysmap.crux.core.client.screen.HTMLContainer;
import br.com.sysmap.crux.core.client.screen.HasWidgetsFactory;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractHTMLPanelFactory<T extends HTMLPanel> extends ComplexPanelFactory<T, WidgetCreatorContext> 
				implements HasWidgetsFactory<T, WidgetCreatorContext>
{
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	protected static class CruxHTMLPanel extends HTMLPanel implements HTMLContainer
	{
		/**
		 * Constructor
		 * @param element
		 */
		public CruxHTMLPanel(CruxMetaDataElement element)
        {
	        super("");
	        assert(element.containsKey("id")):Crux.getMessages().screenFactoryWidgetIdRequired();
	        Element panelElement = WidgetCreator.getEnclosingPanelElement(element.getProperty("id"));
	        assert Document.get().getBody().isOrHasChild(panelElement);
	        panelElement.removeFromParent();
	        getElement().appendChild(panelElement);
        }
		
		@Override
		public void onAttach()
		{
		    super.onAttach();
		}
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.screen.HasWidgetsFactory#add(com.google.gwt.user.client.ui.Widget, java.lang.String, com.google.gwt.user.client.ui.Widget, java.lang.String)
	 */
	public void add(T parent, String parentId, Widget widget, String widgetId) 
	{
		String panelId = getEnclosingPanelPrefix()+widgetId;
		parent.add(widget, panelId);
	}
		
	/**
	 * @param cruxHTMLPanel
	 * @param element
	 * @throws InterfaceConfigException
	 */
	protected void createChildren(String parentId, CruxMetaDataElement element) throws InterfaceConfigException
    {
		Array<CruxMetaDataElement> children = element.getChildren();
		if (children != null)
		{
			addToParserStack(getFactoryType(), parentId, children);
		}
    }
	
	/**
	 * @return
	 */
	protected abstract String getFactoryType();
}
