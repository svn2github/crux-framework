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

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.screen.HTMLContainer;
import br.com.sysmap.crux.core.client.screen.ViewFactoryUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.ui.HTMLPanel;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractHTMLPanelFactory extends ComplexPanelFactory<WidgetCreatorContext>
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
		public CruxHTMLPanel(String wrapperElementId)
        {
	        super("");
	        assert(wrapperElementId != null):Crux.getMessages().screenFactoryWidgetIdRequired();
	        Element panelElement = ViewFactoryUtils.getEnclosingPanelElement(wrapperElementId);
	        assert Document.get().getBody().isOrHasChild(panelElement);
	        panelElement.removeFromParent();
	        getElement().appendChild(panelElement);
        }
		
		@Override
		public void onAttach()
		{
		    super.onAttach();//TODO verificar se precisa disso ainda
		}
	}
		
	/**
	 * @param cruxHTMLPanel
	 * @param metaElem
	 * @throws CruxGeneratorException
	 */
	protected void createChildren(SourcePrinter out, String widget, JSONObject metaElem) throws CruxGeneratorException
    {
		out.println(widget+"addAttachHandler(new "+Handler.class.getCanonicalName()+"(){");
		out.println("public void onAttachOrDetach("+AttachEvent.class.getCanonicalName()+" event){");
		
		JSONArray children = metaElem.optJSONArray("_children");
		if (children != null)
		{
			for(int i=0; i< children.length(); i++)
			{
				JSONObject child = children.optJSONObject(i);
				String childWidget = createChildWidget(out, child);
				String panelId = ViewFactoryUtils.getEnclosingPanelPrefix()+child.optString("id");
				out.println(widget+".add("+childWidget+", "+panelId);
			}
		}

		out.println("}");
		out.println("});");
    }
}
