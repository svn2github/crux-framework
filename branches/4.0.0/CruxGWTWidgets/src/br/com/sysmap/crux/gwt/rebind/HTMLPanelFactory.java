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

import org.json.JSONObject;

import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.GeneratorMessages;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.AnyTag;
import br.com.sysmap.crux.core.rebind.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildren;
import br.com.sysmap.crux.gwt.client.CruxHTMLPanel;

import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="HTMLPanel", library="gwt", targetWidget=HTMLPanel.class, htmlContainer=true)
public class HTMLPanelFactory extends AbstractHTMLPanelFactory
{
	private static GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);
	
	public String instantiateWidget(SourcePrinter out, JSONObject metaElem, String widgetId) throws CruxGeneratorException
	{
		String varName = createVariableName("widget");
		String className = CruxHTMLPanel.class.getCanonicalName();
		String id = metaElem.optString("id");
        if(StringUtils.isEmpty(id))
        {
        	throw new CruxGeneratorException(messages.screenFactoryWidgetIdRequired());
        }
		out.println("final "+className + " " + varName+" = new "+className+"("+EscapeUtils.quote(id)+");");
		createChildren(out, varName, metaElem);
		return varName;
	}

	@Override
	@TagChildren({
		@TagChild(value=ContentProcessor.class, autoProcess=false)
	})
	public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
	}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", type=AnyTag.class)
	public static class ContentProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
}
