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
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.AttributeProcessor;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.HasBeforeSelectionHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasSelectionHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.HTMLTag;
import br.com.sysmap.crux.core.rebind.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildren;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.TabLayoutPanel;

class TabLayoutPanelContext extends WidgetCreatorContext
{

	public String title;
	public boolean isTitleHTML;
	public String titleWidget;
}

/**
 * Factory for TabLayoutPanel widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="tabLayoutPanel", library="gwt", targetWidget=TabLayoutPanel.class)
public class TabLayoutPanelFactory extends CompositeFactory<TabLayoutPanelContext> 
       implements HasBeforeSelectionHandlersFactory<TabLayoutPanelContext>, 
                  HasSelectionHandlersFactory<TabLayoutPanelContext>
{
	public String instantiateWidget(SourcePrinter out, JSONObject metaElem, String widgetId) throws CruxGeneratorException
	{
		String varName = createVariableName("widget");
		String className = getWidgetClassName();
		String height = metaElem.optString("barHeight");
		if (StringUtils.isEmpty(height))
		{
			out.println(className + " " + varName+" = new "+className+"(20,"+Unit.class.getCanonicalName()+".PX);");
		}
		else
		{
			Unit unit = AbstractLayoutPanelFactory.getUnit(metaElem.optString("unit"));
			out.println(className + " " + varName+" = new "+className+"("+Double.parseDouble(height)+","+Unit.class.getCanonicalName()+"."+unit.toString()+");");
		}
		return varName;
	}
	
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="barHeight", type=Integer.class, defaultValue="20"),
		@TagAttributeDeclaration(value="unit", type=Unit.class)
	})
	@TagAttributes({
		@TagAttribute(value="visibleTab", type=Integer.class, processor=VisibleTabAttributeParser.class)
	})
	public void processAttributes(SourcePrinter out, TabLayoutPanelContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class VisibleTabAttributeParser extends AttributeProcessor<TabLayoutPanelContext>
	{
		public void processAttribute(SourcePrinter out, TabLayoutPanelContext context, final String propertyValue)
        {
			String widget = context.getWidget();
			printlnPostProcessing(widget+".selectTab("+Integer.parseInt(propertyValue)+");");
        }
	}
		
	@Override
	@TagChildren({
		@TagChild(TabProcessor.class)
	})	
	public void processChildren(SourcePrinter out, TabLayoutPanelContext context) throws CruxGeneratorException 
	{
	}

	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", tagName="tab" )
	public static class TabProcessor extends WidgetChildProcessor<TabLayoutPanelContext> 
	{
		@Override
		@TagChildren({
			@TagChild(TabTitleProcessor.class), 
			@TagChild(TabWidgetProcessor.class)
		})	
		public void processChildren(SourcePrinter out, TabLayoutPanelContext context) throws CruxGeneratorException {}
	}

	@TagChildAttributes(minOccurs="0")
	public static class TabTitleProcessor extends ChoiceChildProcessor<TabLayoutPanelContext> 
	{
		@Override
		@TagChildren({
			@TagChild(TextTabProcessor.class),
			@TagChild(HTMLTabProcessor.class),
			@TagChild(WidgetTitleTabProcessor.class)
		})		
		public void processChildren(SourcePrinter out, TabLayoutPanelContext context) throws CruxGeneratorException {}
		
	}
	
	@TagChildAttributes(tagName="tabText", type=String.class)
	public static class TextTabProcessor extends WidgetChildProcessor<TabLayoutPanelContext>
	{
		@Override
		public void processChildren(SourcePrinter out, TabLayoutPanelContext context) throws CruxGeneratorException 
		{
			context.title = getWidgetCreator().getDeclaredMessage(ensureTextChild(context.getChildElement(), true));
			context.isTitleHTML = false;
		}
	}
	
	@TagChildAttributes(tagName="tabHtml", type=HTMLTag.class)
	public static class HTMLTabProcessor extends WidgetChildProcessor<TabLayoutPanelContext>
	{
		@Override
		public void processChildren(SourcePrinter out, TabLayoutPanelContext context) throws CruxGeneratorException 
		{
			context.title = ensureHtmlChild(context.getChildElement(), true);
			context.isTitleHTML = true;
		}
	}
	
	@TagChildAttributes(tagName="tabWidget")
	public static class WidgetTitleTabProcessor extends WidgetChildProcessor<TabLayoutPanelContext> 
	{
		@Override
		@TagChildren({
			@TagChild(WidgetTitleProcessor.class)
		})	
		public void processChildren(SourcePrinter out, TabLayoutPanelContext context) throws CruxGeneratorException {}
	}

	@TagChildAttributes(type=AnyWidget.class)
	public static class WidgetTitleProcessor extends WidgetChildProcessor<TabLayoutPanelContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, TabLayoutPanelContext context) throws CruxGeneratorException
		{
			context.titleWidget = getWidgetCreator().createChildWidget(out, context.getChildElement());
		}
	}
	
	@TagChildAttributes(tagName="panelContent")
	public static class TabWidgetProcessor extends WidgetChildProcessor<TabLayoutPanelContext> 
	{
		@Override
		@TagChildren({
			@TagChild(WidgetContentProcessor.class)
		})	
		public void processChildren(SourcePrinter out, TabLayoutPanelContext context) throws CruxGeneratorException {}
	}

	@TagChildAttributes(type=AnyWidget.class)
	public static class WidgetContentProcessor extends WidgetChildProcessor<TabLayoutPanelContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, TabLayoutPanelContext context) throws CruxGeneratorException
		{
			String widget = getWidgetCreator().createChildWidget(out, context.getChildElement());
			
			String rootWidget = context.getWidget();
			
			if (context.titleWidget != null)
			{
				out.println(rootWidget+".add("+widget+", "+context.titleWidget+");");
			}
			else
			{
				out.println(rootWidget+".add("+widget+", "+EscapeUtils.quote(context.title)+", "+context.isTitleHTML+");");
			}
		}
	}
}
