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

import org.json.JSONObject;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.declarative.TagEvent;
import br.com.sysmap.crux.core.client.declarative.TagEvents;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.children.AnyWidgetChildProcessor;

import com.google.gwt.user.client.ui.FormPanel;

/**
 * Represents a FormPanelFactory.
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="formPanel", library="gwt")
public class FormPanelFactory extends PanelFactory<WidgetCreatorContext>
{
	
	@Override
	@TagAttributes({
		@TagAttribute("method"),
		@TagAttribute("encoding"),
		@TagAttribute("action")
	})
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("target")
	})
    public void processAttributes(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException 
	{
		super.processAttributes(out, context);
	}
	
	@Override
	@TagEvents({
		@TagEvent(SubmitCompleteEvtBind.class),
		@TagEvent(SubmitEvtBind.class)
	})
	public void processEvents(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException 
	{
		super.processEvents(out, context);
	}
	
	@Override
	public String instantiateWidget(SourcePrinter out, JSONObject metaElem, String widgetId)
	{
		String varName = ViewFactoryCreator.createVariableName("formPanel");
		String className = FormPanel.class.getCanonicalName();
		String target = metaElem.optString("target");
		if (target != null && target.length() >0)
		{
			out.println(className + " " + varName+" = new "+className+"("+EscapeUtils.quote(target)+");");
		}
		else
		{
			out.println(className + " " + varName+" = new "+className+"();");
		}
		return varName;
	}
	
	@Override
	@TagChildren({
		@TagChild(WidgetContentProcessor.class)
	})
	public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
	}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="1")
	public static class WidgetContentProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}	
}
