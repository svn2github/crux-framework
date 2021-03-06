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


import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyTag;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.client.screen.factory.HasClickHandlersFactory;
import br.com.sysmap.crux.gwt.client.align.AlignmentAttributeParser;
import br.com.sysmap.crux.gwt.client.align.HorizontalAlignment;
import br.com.sysmap.crux.gwt.client.align.VerticalAlignment;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class HTMLTableFactory <T extends HTMLTable> extends PanelFactory<T>
       implements HasClickHandlersFactory<T>
{	
	@Override
	@TagAttributes({
		@TagAttribute(value="borderWidth",type=Integer.class),
		@TagAttribute(value="cellPadding",type=Integer.class),
		@TagAttribute(value="cellSpacing",type=Integer.class)
	})
	public void processAttributes(WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	@TagChildAttributes(tagName="row", minOccurs="0", maxOccurs="unbounded")
	public static class TableRowProcessor<T extends HTMLTable> extends WidgetChildProcessor<T>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration("styleName"),
			@TagAttributeDeclaration(value="visible", type=Boolean.class, defaultValue="true"),
			@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
		})
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException
		{
			int index;
			
			Object rowIndexAttr = context.getAttribute("rowIndex");
			if (rowIndexAttr != null)
			{    // Do not use auto-boxing during arithmetic operations and always try to use primitive types
				 // It can improve the GWT generated code performance greatly
				index= ((Integer) rowIndexAttr).intValue()+1;
			}
			else
			{
				index = 0;
			}
			try
			{
				String styleName = context.readChildProperty("styleName");
				RowFormatter rowFormatter = context.getRootWidget().getRowFormatter();
				if (styleName != null && styleName.length() > 0)
				{
					rowFormatter.setStyleName(index, styleName);
				}
				String visible = context.readChildProperty("visible");
				if (visible != null && visible.length() > 0)
				{
					rowFormatter.setVisible(index, Boolean.parseBoolean(visible));
				}

				String verticalAlignment = context.readChildProperty("verticalAlignment");
				rowFormatter.setVerticalAlign(index, 
						AlignmentAttributeParser.getVerticalAlignment(verticalAlignment));
			}
			finally
			{
				context.setAttribute("rowIndex", (index));
				context.setAttribute("colIndex", null);
			}
		}
	}

	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded")
	public static class TableCellProcessor<T extends HTMLTable> extends WidgetChildProcessor<T>
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
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException
		{
			HTMLTable widget = context.getRootWidget();

			int indexRow = ((Integer) context.getAttribute("rowIndex")).intValue();
			int indexCol;;
			Object colIndexAttr = context.getAttribute("colIndex");
			if (colIndexAttr != null)
			{// Do not use auto-boxing during arithmetic operations and always try to use primitive types
			 // It can improve the GWT generated code performance greatly
				indexCol= ((Integer) colIndexAttr).intValue()+1;
			}
			else
			{
				indexCol = 0;
			}
			try
			{
				String styleName = context.readChildProperty("styleName");
				CellFormatter cellFormatter = widget.getCellFormatter();
				if (styleName != null && styleName.length() > 0)
				{
					cellFormatter.setStyleName(indexRow, indexCol, styleName);
				}
				String visible = context.readChildProperty("visible");
				if (visible != null && visible.length() > 0)
				{
					cellFormatter.setVisible(indexRow, indexCol, Boolean.parseBoolean(visible));
				}
				String height = context.readChildProperty("height");
				if (height != null && height.length() > 0)
				{
					cellFormatter.setHeight(indexRow, indexCol, height);
				}
				String width = context.readChildProperty("width");
				if (width != null && width.length() > 0)
				{
					cellFormatter.setWidth(indexRow, indexCol, width);
				}
				String wordWrap = context.readChildProperty("wordWrap");
				if (wordWrap != null && wordWrap.length() > 0)
				{
					cellFormatter.setWordWrap(indexRow, indexCol, Boolean.parseBoolean(wordWrap));
				}

				String horizontalAlignment = context.readChildProperty("horizontalAlignment");
				if (horizontalAlignment != null && horizontalAlignment.length() > 0)
				{
					cellFormatter.setHorizontalAlignment(indexRow, indexCol, 
						AlignmentAttributeParser.getHorizontalAlignment(horizontalAlignment, HasHorizontalAlignment.ALIGN_DEFAULT));
				}
				String verticalAlignment = context.readChildProperty("verticalAlignment");
				if (verticalAlignment != null && verticalAlignment.length() > 0)
				{
					cellFormatter.setVerticalAlignment(indexRow, indexCol, 
						AlignmentAttributeParser.getVerticalAlignment(verticalAlignment));

				}
			}
			finally
			{
				context.setAttribute("colIndex", indexCol);
			}
		}
	}
	
	@TagChildAttributes(tagName="text", type=String.class)
	public static abstract class CellTextProcessor<T extends HTMLTable> extends WidgetChildProcessor<T>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException 
		{
			Integer indexRow = (Integer) context.getAttribute("rowIndex");
			Integer indexCol = (Integer) context.getAttribute("colIndex");
			context.getRootWidget().setText(indexRow.intValue(), indexCol.intValue(), ScreenFactory.getInstance().getDeclaredMessage(context.getChildElement().getInnerHTML()));
		}
	}
	
	@TagChildAttributes(tagName="html", type=AnyTag.class)
	public static abstract class CellHTMLProcessor<T extends HTMLTable> extends WidgetChildProcessor<T>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException 
		{
			Integer indexRow = (Integer) context.getAttribute("rowIndex");
			Integer indexCol = (Integer) context.getAttribute("colIndex");
			context.getRootWidget().setHTML(indexRow.intValue(), indexCol.intValue(), context.getChildElement().getInnerHTML());
		}
	}
	
	@TagChildAttributes(tagName="widget")
	public static abstract class CellWidgetProcessor<T extends HTMLTable> extends WidgetChildProcessor<T> {}

	@TagChildAttributes(type=AnyWidget.class)
	public static class WidgetProcessor<T extends HTMLTable> extends WidgetChildProcessor<T> 
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException
		{
			Integer indexRow = (Integer) context.getAttribute("rowIndex");
			Integer indexCol = (Integer) context.getAttribute("colIndex");
			Element childElement = context.getChildElement();
			context.getRootWidget().setWidget(indexRow.intValue(), indexCol.intValue(), createChildWidget(childElement, childElement.getId()));
		}
	}
}
