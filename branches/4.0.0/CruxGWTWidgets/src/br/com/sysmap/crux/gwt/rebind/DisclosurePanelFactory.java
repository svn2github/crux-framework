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

import br.com.sysmap.crux.core.client.screen.LazyPanel;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.HasAnimationFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasCloseHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasOpenHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildLazyCondition;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildLazyConditions;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildren;

import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;

/**
 * Factory for DisclosurePanel widgets
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="disclosurePanel", library="gwt", targetWidget=DisclosurePanel.class)
public class DisclosurePanelFactory extends CompositeFactory<WidgetCreatorContext> 
       implements HasAnimationFactory<WidgetCreatorContext>, 
                  HasOpenHandlersFactory<WidgetCreatorContext>, 
                  HasCloseHandlersFactory<WidgetCreatorContext>
{
	protected GWTMessages messages = MessagesFactory.getMessages(GWTMessages.class);
	
	
	@Override
	public String instantiateWidget(SourcePrinter out, JSONObject metaElem, String widgetId)
	{
		String headerText = metaElem.optString("headerText");
		String varName = ViewFactoryCreator.createVariableName("dialogBox");
		String className = DisclosurePanel.class.getCanonicalName();
		if (!StringUtils.isEmpty(headerText))
		{
			out.println(className + " " + varName+" = new "+className+"("+EscapeUtils.quote(headerText)+");");
		}
		else
		{
			out.println(className + " " + varName+" = new "+className+"();");
		}
		
		String open = metaElem.optString("open");
		if (open == null || !StringUtils.unsafeEquals(open, "true"))
		{
			out.println(varName+".addOpenHandler(new "+OpenHandler.class.getCanonicalName()+"<"+className+">() {");
			out.println("private boolean loaded = false;");
			out.println("public void onOpen("+OpenEvent.class.getCanonicalName()+"<"+className+"> event){"); 
			out.println("if (!loaded){");
			out.println(LazyPanel.class.getCanonicalName() +"widget = ("+LazyPanel.class.getCanonicalName()+")"+varName+".getContent();");
			out.println("widget.ensureWidget();");
			out.println("loaded = true;");
			out.println("}");
			out.println("}");
			out.println("});");
		}
		return varName;
	}
	
	@Override
	@TagAttributes({
		@TagAttribute(value="open", type=Boolean.class)
	})
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("headerText")
	})
	public void processAttributes(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
	}

	@Override
	@TagChildren({
		@TagChild(HeaderProcessor.class),
		@TagChild(ContentProcessor.class)
	})	
	public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException {}

	@TagChildAttributes(minOccurs="0", tagName="widgetHeader")
	public static class HeaderProcessor extends WidgetChildProcessor<WidgetCreatorContext> 
	{
		@Override
		@TagChildren({
			@TagChild(WidgetHeaderProcessor.class)
		})	
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException {}
	}
		
	@TagChildAttributes(minOccurs="0", tagName="widgetContent")
	public static class ContentProcessor extends WidgetChildProcessor<WidgetCreatorContext> 
	{
		@Override
		@TagChildren({
			@TagChild(WidgetProcessor.class)
		})	
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException {}
	}

	@TagChildAttributes(widgetProperty="content")
	@TagChildLazyConditions(all={
		@TagChildLazyCondition(property="open", notEquals="true")
	})
	public static class WidgetProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagChildAttributes(widgetProperty="header")
	public static class WidgetHeaderProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}
}
