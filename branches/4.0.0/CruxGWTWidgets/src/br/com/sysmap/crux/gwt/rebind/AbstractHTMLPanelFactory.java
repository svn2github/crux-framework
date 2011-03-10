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
package br.com.sysmap.crux.gwt.rebind;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.sysmap.crux.core.client.screen.ViewFactoryUtils;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractHTMLPanelFactory extends ComplexPanelFactory<WidgetCreatorContext>
{
	/**
	 * @param cruxHTMLPanel
	 * @param metaElem
	 * @throws CruxGeneratorException
	 */
	protected void createChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
    {
		out.println(context.getWidget()+".addAttachHandler(new "+Handler.class.getCanonicalName()+"(){");
		out.println("public void onAttachOrDetach("+AttachEvent.class.getCanonicalName()+" event){");
		out.println("if (event.isAttached()){");
		
		JSONArray children = ensureChildren(context.getWidgetElement(), true);
		if (children != null)
		{
			for(int i=0; i< children.length(); i++)
			{
				JSONObject child = children.optJSONObject(i);
				String childWidget = createChildWidget(out, child, context);
				boolean childPartialSupport = hasChildPartialSupport(child);
				if (childPartialSupport)
				{
					out.println("if ("+getChildWidgetClassName(child)+".isSupported()){");
				}
				String panelId = ViewFactoryUtils.getEnclosingPanelPrefix()+child.optString("id");
				out.println(context.getWidget()+".add("+childWidget+", "+EscapeUtils.quote(panelId)+");");
				if (childPartialSupport)
				{
					out.println("}");
				}
			}
		}

		out.println("}");
		out.println("}");
		out.println("});");
    }
	
	@Override
	public WidgetCreatorContext instantiateContext()
	{
	    return new WidgetCreatorContext();
	}
}
