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


import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.creator.HasClickHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasDoubleClickHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.align.AlignmentAttributeParser;
import br.com.sysmap.crux.core.rebind.widget.creator.align.HorizontalAlignment;
import br.com.sysmap.crux.core.rebind.widget.creator.align.VerticalAlignment;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.HTMLTag;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildAttributes;

import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class HTMLTableFactory <C extends HTMLTableFactoryContext> extends PanelFactory<C>
       implements HasClickHandlersFactory<C>, HasDoubleClickHandlersFactory<C>
{	
	@Override
	@TagAttributes({
		@TagAttribute(value="borderWidth",type=Integer.class),
		@TagAttribute(value="cellPadding",type=Integer.class),
		@TagAttribute(value="cellSpacing",type=Integer.class)
	})
	public void processAttributes(SourcePrinter out, C context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
	}
	
	@TagChildAttributes(tagName="row", minOccurs="0", maxOccurs="unbounded")
	public static class TableRowProcessor<C extends HTMLTableFactoryContext> extends WidgetChildProcessor<C>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration("styleName"),
			@TagAttributeDeclaration(value="visible", type=Boolean.class, defaultValue="true"),
			@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
		})
		public void processChildren(SourcePrinter out, C context) throws CruxGeneratorException
		{
			context.rowIndex++;
			try
			{
				String styleName = context.readChildProperty("styleName");
				String rootWidget = context.getWidget();
				String rowFormatter = ViewFactoryCreator.createVariableName("rowFormatter");

				out.println(RowFormatter.class.getCanonicalName() + " " + rowFormatter +" = "+rootWidget+".getRowFormatter();");
				if (styleName != null && styleName.length() > 0)
				{
					out.println(rowFormatter+".setStyleName("+context.rowIndex+", "+EscapeUtils.quote(styleName)+");");
				}
				String visible = context.readChildProperty("visible");
				if (visible != null && visible.length() > 0)
				{
					out.println(rowFormatter+".setVisible("+context.rowIndex+", "+Boolean.parseBoolean(visible)+");");
				}

				String verticalAlignment = context.readChildProperty("verticalAlignment");
				out.println(rowFormatter+".setVerticalAlign("+context.rowIndex+", "+
						AlignmentAttributeParser.getVerticalAlignment(verticalAlignment)+");");
			}
			finally
			{
				context.colIndex = -1;
			}
		}
	}

	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded")
	public static class TableCellProcessor<C extends HTMLTableFactoryContext> extends WidgetChildProcessor<C>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration("styleName"),
			@TagAttributeDeclaration("width"),
			@TagAttributeDeclaration("height"),
			@TagAttributeDeclaration(value="visible", type=Boolean.class, defaultValue="true"),
			@TagAttributeDeclaration(value="wordWrap", type=Boolean.class, defaultValue="true"),
			@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign"),
			@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
		})
		public void processChildren(SourcePrinter out, C context) throws CruxGeneratorException
		{
			String widget = context.getWidget();

			context.colIndex++;
			String styleName = context.readChildProperty("styleName");
			String cellFormatter = ViewFactoryCreator.createVariableName("cellFormatter");

			out.println(CellFormatter.class.getCanonicalName() + " " + cellFormatter +" = "+widget+".getCellFormatter();");

			if (styleName != null && styleName.length() > 0)
			{
				out.println(cellFormatter+".setStyleName("+context.rowIndex+", "+context.colIndex+", "+EscapeUtils.quote(styleName)+");");
			}
			String visible = context.readChildProperty("visible");
			if (visible != null && visible.length() > 0)
			{
				out.println(cellFormatter+".setVisible("+context.rowIndex+", "+context.colIndex+", "+Boolean.parseBoolean(visible)+");");
			}
			String height = context.readChildProperty("height");
			if (height != null && height.length() > 0)
			{
				out.println(cellFormatter+".setHeight("+context.rowIndex+", "+context.colIndex+", "+EscapeUtils.quote(height)+");");
			}
			String width = context.readChildProperty("width");
			if (width != null && width.length() > 0)
			{
				out.println(cellFormatter+".setWidth("+context.rowIndex+", "+context.colIndex+", "+EscapeUtils.quote(width)+");");
			}
			String wordWrap = context.readChildProperty("wordWrap");
			if (wordWrap != null && wordWrap.length() > 0)
			{
				out.println(cellFormatter+".setWordWrap("+context.rowIndex+", "+context.colIndex+", "+Boolean.parseBoolean(wordWrap)+");");
			}

			String horizontalAlignment = context.readChildProperty("horizontalAlignment");
			if (horizontalAlignment != null && horizontalAlignment.length() > 0)
			{
				out.println(cellFormatter+".setHorizontalAlignment("+context.rowIndex+", "+context.colIndex+", "+ 
						AlignmentAttributeParser.getHorizontalAlignment(horizontalAlignment, HasHorizontalAlignment.class.getCanonicalName()+".ALIGN_DEFAULT")+");");
			}
			String verticalAlignment = context.readChildProperty("verticalAlignment");
			if (verticalAlignment != null && verticalAlignment.length() > 0)
			{
				out.println(cellFormatter+".setVerticalAlignment("+context.rowIndex+", "+context.colIndex+", "+
						AlignmentAttributeParser.getVerticalAlignment(verticalAlignment)+");");

			}
		}
	}
	
	@TagChildAttributes(tagName="text", type=String.class)
	public static abstract class CellTextProcessor<C extends HTMLTableFactoryContext> extends WidgetChildProcessor<C>
	{
		@Override
		public void processChildren(SourcePrinter out, C context) throws CruxGeneratorException 
		{
			String rootWidget = context.getWidget();
			out.println(rootWidget+".setText("+context.rowIndex+", "+context.colIndex+", "+EscapeUtils.quote(
					getWidgetCreator().getDeclaredMessage(ensureTextChild(context.getChildElement(), true)))+");");
		}
	}
	
	@TagChildAttributes(tagName="html", type=HTMLTag.class)
	public static abstract class CellHTMLProcessor<C extends HTMLTableFactoryContext> extends WidgetChildProcessor<C>
	{
		@Override
		public void processChildren(SourcePrinter out, C context) throws CruxGeneratorException 
		{
			String rootWidget = context.getWidget();
			out.println(rootWidget+".setHTML("+context.rowIndex+", "+context.colIndex+", "+EscapeUtils.quote(ensureHtmlChild(context.getChildElement(), true))+");");
		}
	}
	
	@TagChildAttributes(tagName="widget")
	public static abstract class CellWidgetProcessor<C extends HTMLTableFactoryContext> extends WidgetChildProcessor<C> {}

	@TagChildAttributes(type=AnyWidget.class)
	public static class WidgetProcessor<C extends HTMLTableFactoryContext> extends WidgetChildProcessor<C> 
	{
		@Override
		public void processChildren(SourcePrinter out, C context) throws CruxGeneratorException
		{
			String rootWidget = context.getWidget();
			String childWidget = getWidgetCreator().createChildWidget(out, context.getChildElement());
			EscapeUtils.quote(rootWidget+".setWidget("+context.rowIndex+", "+context.colIndex+", "+childWidget+");");
		}
	}
}
