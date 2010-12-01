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
import br.com.sysmap.crux.core.client.screen.WidgetFactoryContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.HTMLTag;
import br.com.sysmap.crux.core.client.screen.factory.HasClickHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasDoubleClickHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.align.AlignmentAttributeParser;
import br.com.sysmap.crux.core.client.screen.factory.align.HorizontalAlignment;
import br.com.sysmap.crux.core.client.screen.factory.align.VerticalAlignment;

import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;

class HTMLTableFactoryContext extends WidgetFactoryContext
{
	int rowIndex = -1;
	int colIndex = -1;
}

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class HTMLTableFactory <T extends HTMLTable, C extends HTMLTableFactoryContext> extends PanelFactory<T, C>
       implements HasClickHandlersFactory<T, C>, HasDoubleClickHandlersFactory<T, C>
{	
	@Override
	@TagAttributes({
		@TagAttribute(value="borderWidth",type=Integer.class),
		@TagAttribute(value="cellPadding",type=Integer.class),
		@TagAttribute(value="cellSpacing",type=Integer.class)
	})
	public void processAttributes(C context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	@TagChildAttributes(tagName="row", minOccurs="0", maxOccurs="unbounded")
	public static class TableRowProcessor<T extends HTMLTable, C extends HTMLTableFactoryContext> extends WidgetChildProcessor<T, C>
	{
		@SuppressWarnings("unchecked")
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration("styleName"),
			@TagAttributeDeclaration(value="visible", type=Boolean.class, defaultValue="true"),
			@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
		})
		public void processChildren(C context) throws InterfaceConfigException
		{
			context.rowIndex++;
			try
			{
				String styleName = context.readChildProperty("styleName");
				T rootWidget = (T)context.getWidget();
				RowFormatter rowFormatter = rootWidget.getRowFormatter();
				if (styleName != null && styleName.length() > 0)
				{
					rowFormatter.setStyleName(context.rowIndex, styleName);
				}
				String visible = context.readChildProperty("visible");
				if (visible != null && visible.length() > 0)
				{
					rowFormatter.setVisible(context.rowIndex, Boolean.parseBoolean(visible));
				}

				String verticalAlignment = context.readChildProperty("verticalAlignment");
				rowFormatter.setVerticalAlign(context.rowIndex, 
						AlignmentAttributeParser.getVerticalAlignment(verticalAlignment));
			}
			finally
			{
				context.colIndex = -1;
			}
		}
	}

	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded")
	public static class TableCellProcessor<T extends HTMLTable, C extends HTMLTableFactoryContext> extends WidgetChildProcessor<T, C>
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
		public void processChildren(C context) throws InterfaceConfigException
		{
			HTMLTable widget = context.getWidget();

			context.colIndex++;
			String styleName = context.readChildProperty("styleName");
			CellFormatter cellFormatter = widget.getCellFormatter();
			if (styleName != null && styleName.length() > 0)
			{
				cellFormatter.setStyleName(context.rowIndex, context.colIndex, styleName);
			}
			String visible = context.readChildProperty("visible");
			if (visible != null && visible.length() > 0)
			{
				cellFormatter.setVisible(context.rowIndex, context.colIndex, Boolean.parseBoolean(visible));
			}
			String height = context.readChildProperty("height");
			if (height != null && height.length() > 0)
			{
				cellFormatter.setHeight(context.rowIndex, context.colIndex, height);
			}
			String width = context.readChildProperty("width");
			if (width != null && width.length() > 0)
			{
				cellFormatter.setWidth(context.rowIndex, context.colIndex, width);
			}
			String wordWrap = context.readChildProperty("wordWrap");
			if (wordWrap != null && wordWrap.length() > 0)
			{
				cellFormatter.setWordWrap(context.rowIndex, context.colIndex, Boolean.parseBoolean(wordWrap));
			}

			String horizontalAlignment = context.readChildProperty("horizontalAlignment");
			if (horizontalAlignment != null && horizontalAlignment.length() > 0)
			{
				cellFormatter.setHorizontalAlignment(context.rowIndex, context.colIndex, 
						AlignmentAttributeParser.getHorizontalAlignment(horizontalAlignment, HasHorizontalAlignment.ALIGN_DEFAULT));
			}
			String verticalAlignment = context.readChildProperty("verticalAlignment");
			if (verticalAlignment != null && verticalAlignment.length() > 0)
			{
				cellFormatter.setVerticalAlignment(context.rowIndex, context.colIndex, 
						AlignmentAttributeParser.getVerticalAlignment(verticalAlignment));

			}
		}
	}
	
	@TagChildAttributes(tagName="text", type=String.class)
	public static abstract class CellTextProcessor<T extends HTMLTable, C extends HTMLTableFactoryContext> extends WidgetChildProcessor<T, C>
	{
		@SuppressWarnings("unchecked")
		@Override
		public void processChildren(C context) throws InterfaceConfigException 
		{
			T rootWidget = (T)context.getWidget();
			rootWidget.setText(context.rowIndex, context.colIndex, 
					ScreenFactory.getInstance().getDeclaredMessage(ensureTextChild(context.getChildElement(), true)));
		}
	}
	
	@TagChildAttributes(tagName="html", type=HTMLTag.class)
	public static abstract class CellHTMLProcessor<T extends HTMLTable, C extends HTMLTableFactoryContext> extends WidgetChildProcessor<T, C>
	{
		@SuppressWarnings("unchecked")
		@Override
		public void processChildren(C context) throws InterfaceConfigException 
		{
			T rootWidget = (T)context.getWidget();
			rootWidget.setHTML(context.rowIndex, context.colIndex, ensureHtmlChild(context.getChildElement(), true));
		}
	}
	
	@TagChildAttributes(tagName="widget")
	public static abstract class CellWidgetProcessor<T extends HTMLTable, C extends HTMLTableFactoryContext> extends WidgetChildProcessor<T, C> {}

	@TagChildAttributes(type=AnyWidget.class)
	public static class WidgetProcessor<T extends HTMLTable, C extends HTMLTableFactoryContext> extends WidgetChildProcessor<T, C> 
	{
		@SuppressWarnings("unchecked")
		@Override
		public void processChildren(C context) throws InterfaceConfigException
		{
			T rootWidget = (T)context.getWidget();
			rootWidget.setWidget(context.rowIndex, context.colIndex, createChildWidget(context.getChildElement()));
		}
	}
}
