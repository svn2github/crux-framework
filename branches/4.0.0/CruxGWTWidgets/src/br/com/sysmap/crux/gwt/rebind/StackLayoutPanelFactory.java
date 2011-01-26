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

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.HasBeforeSelectionHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasSelectionHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.HTMLTag;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.StackLayoutPanel;

class StackLayoutContext extends WidgetCreatorContext
{
	boolean selected = false;
	double headerSize;
	String headerWidget;
	String title;
	boolean isHtmlTitle;
	
	public void clearAttributes() 
	{
		title = null;
		isHtmlTitle = false;
		selected = false;
		headerSize = 0;
		headerWidget = null;
	}
}

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="stackLayoutPanel", library="gwt", targetWidget=StackLayoutPanel.class)
public class StackLayoutPanelFactory extends WidgetCreator<StackLayoutContext> 
	   implements HasBeforeSelectionHandlersFactory<StackLayoutContext>, 
	   			  HasSelectionHandlersFactory<StackLayoutContext>
{
	public String instantiateWidget(SourcePrinter out, JSONObject metaElem, String widgetId) throws CruxGeneratorException
	{
		String varName = createVariableName("widget");
		String className = getWidgetClassName();
		Unit unit = AbstractLayoutPanelFactory.getUnit(metaElem.optString("unit"));
		out.println(className + " " + varName+" = new "+className+"("+Unit.class.getCanonicalName()+"."+unit.toString()+");");
		return varName;
	}
	
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="unit", type=Unit.class, defaultValue="PX")
	})
	public void processAttributes(SourcePrinter out, StackLayoutContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
	}
	
	@Override
	@TagChildren({
		@TagChild(StackItemProcessor.class)
	})
	public void processChildren(SourcePrinter out, StackLayoutContext context) throws CruxGeneratorException
	{
		super.processChildren(out, context);
	}

	@TagChildAttributes(tagName="item", maxOccurs="unbounded")
	public static class StackItemProcessor extends WidgetChildProcessor<StackLayoutContext>
	{
		@Override
		@TagChildren({
			@TagChild(StackHeaderProcessor.class),
			@TagChild(StackContentProcessor.class)
		})
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="selected", type=Boolean.class, defaultValue="false")
		})
		public void processChildren(SourcePrinter out, StackLayoutContext context) throws CruxGeneratorException 
		{
			context.clearAttributes();
			String selected = context.readChildProperty("selected");
			if (!StringUtils.isEmpty(selected))
			{
				context.selected = Boolean.parseBoolean(selected);
			}
		}
	}

	@TagChildAttributes(tagName="header")
	public static class StackHeaderProcessor extends WidgetChildProcessor<StackLayoutContext>
	{
		@Override
		@TagChildren({
			@TagChild(StackHeader.class)
		})
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="size", type=Double.class, required=true)
		})
		public void processChildren(SourcePrinter out, StackLayoutContext context) throws CruxGeneratorException 
		{
			context.headerSize = Double.parseDouble(context.readChildProperty("size"));
		}
	}

	public static class StackHeader extends ChoiceChildProcessor<StackLayoutContext>
	{
		@Override
		@TagChildren({
			@TagChild(StackHeaderWidgetProcessor.class),
			@TagChild(StackHeaderTextProcessor.class),
			@TagChild(StackHeaderHTMLProcessor.class)
		})
		public void processChildren(SourcePrinter out, StackLayoutContext context) throws CruxGeneratorException 
		{
		}
	}
	
	@TagChildAttributes(tagName="text", type=String.class)
	public static class StackHeaderTextProcessor extends WidgetChildProcessor<StackLayoutContext>
	{
		@Override
		public void processChildren(SourcePrinter out, StackLayoutContext context) throws CruxGeneratorException 
		{
			context.title = getWidgetCreator().getDeclaredMessage(ensureTextChild(context.getChildElement(), true));
			context.isHtmlTitle = false;
		}
	}
	
	@TagChildAttributes(tagName="html", type=HTMLTag.class)
	public static class StackHeaderHTMLProcessor extends WidgetChildProcessor<StackLayoutContext>
	{
		@Override
		public void processChildren(SourcePrinter out, StackLayoutContext context) throws CruxGeneratorException 
		{
			context.title = ensureHtmlChild(context.getChildElement(), true);
			context.isHtmlTitle = true;
		}
	}

	@TagChildAttributes(tagName="widget", type=AnyWidget.class)
	public static class StackHeaderWidgetProcessor extends WidgetChildProcessor<StackLayoutContext>
	{
		@Override
		public void processChildren(SourcePrinter out, StackLayoutContext context) throws CruxGeneratorException
		{
			String childWidget = getWidgetCreator().createChildWidget(out, context.getChildElement());
			context.headerWidget = childWidget;
		}
	}

	@TagChildAttributes(tagName="content")
	public static class StackContentProcessor extends WidgetChildProcessor<StackLayoutContext>
	{
		@Override
		@TagChildren({
			@TagChild(StackContentWidgetProcessor.class)
		})
		public void processChildren(SourcePrinter out, StackLayoutContext context) throws CruxGeneratorException {}
	}

	@TagChildAttributes(type=AnyWidget.class)
	public static class StackContentWidgetProcessor extends WidgetChildProcessor<StackLayoutContext>
	{
		@Override
		public void processChildren(SourcePrinter out, StackLayoutContext context) throws CruxGeneratorException
		{
			String contentWidget = getWidgetCreator().createChildWidget(out, context.getChildElement());
			String rootWidget = context.getWidget();
			
			if (context.headerWidget != null)
			{
				out.println(rootWidget+".add("+contentWidget+", "+context.headerWidget+", "+context.headerSize+");");
			}
			else
			{
				out.println(rootWidget+".add("+contentWidget+", "+EscapeUtils.quote(context.title)+", "+context.isHtmlTitle+", "+context.headerSize+");");
			}

			if (context.selected)
			{
				out.println(rootWidget+".showWidget("+contentWidget+");");
			}
		}
	}
}