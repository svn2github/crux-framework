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
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.client.screen.factory.align.AlignmentAttributeParser;
import br.com.sysmap.crux.core.client.screen.factory.align.HorizontalAlignment;
import br.com.sysmap.crux.core.client.screen.factory.align.VerticalAlignment;
import br.com.sysmap.crux.core.client.utils.StringUtils;

import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class CellPanelFactory <T extends CellPanel, C extends CellPanelContext> extends ComplexPanelFactory<T, C>
{
	private static final String DEFAULT_V_ALIGN = HasVerticalAlignment.ALIGN_MIDDLE.getVerticalAlignString();
	private static final String DEFAULT_H_ALIGN = HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString();
	
	@Override
	@TagAttributes({
		@TagAttribute(value="borderWidth",type=Integer.class),
		@TagAttribute(value="spacing",type=Integer.class)
	})
	public void processAttributes(C context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	@Override
	@TagChildren({
		@TagChild(CellPanelProcessor.class)
	})		
	public void processChildren(C context) throws InterfaceConfigException {}
	
	public static class CellPanelProcessor extends AbstractCellPanelProcessor<CellPanel, CellPanelContext>{} 

	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded")
	public static abstract class AbstractCellPanelProcessor<T extends CellPanel, C extends CellPanelContext> extends ChoiceChildProcessor<T, C> 
	{
		@Override
		@TagChildren({
			@TagChild(CellProcessor.class),
			@TagChild(CellWidgetProcessor.class)
		})		
		public void processChildren(C context) throws InterfaceConfigException 
		{
			context.horizontalAlignment = DEFAULT_H_ALIGN;
			context.verticalAlignment = DEFAULT_V_ALIGN;
		}
	}
	
	public static class CellProcessor extends AbstractCellProcessor<CellPanel, CellPanelContext>{}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", tagName="cell")
	public static abstract class AbstractCellProcessor<T extends CellPanel, C extends CellPanelContext> extends WidgetChildProcessor<T, C> 
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration("height"),
			@TagAttributeDeclaration("width"),
			@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign"),
			@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
		})
		@TagChildren({
			@TagChild(value=CellWidgetProcessor.class)
		})		
		public void processChildren(C context) throws InterfaceConfigException 
		{
			context.height = context.readChildProperty("height");
			context.width = context.readChildProperty("width");
			context.horizontalAlignment = context.readChildProperty("horizontalAlignment");
			context.verticalAlignment = context.readChildProperty("verticalAlignment");
		}
	}
	
	public static class CellWidgetProcessor extends AbstractCellWidgetProcessor<CellPanel, CellPanelContext> 
	{
		@Override
		public void processChildren(CellPanelContext context) throws InterfaceConfigException
		{
			Widget child = createChildWidget(context.getChildElement());
			context.child = child;
			super.processChildren(context);
			context.child = null;
		}
	}
	
	@TagChildAttributes(type=AnyWidget.class)
	static class AbstractCellWidgetProcessor<T extends CellPanel, C extends CellPanelContext> extends WidgetChildProcessor<T, C> 
	{
		@SuppressWarnings("unchecked")
		@Override
		public void processChildren(C context) throws InterfaceConfigException
		{
			T parent = (T)context.getWidget();
			if (!StringUtils.isEmpty(context.height))
			{
				parent.setCellHeight(context.child, context.height);
			}
			if (!StringUtils.isEmpty(context.horizontalAlignment))
			{
				parent.setCellHorizontalAlignment(context.child, 
					  AlignmentAttributeParser.getHorizontalAlignment(context.horizontalAlignment, HasHorizontalAlignment.ALIGN_DEFAULT));
			}
			if (!StringUtils.isEmpty(context.verticalAlignment))
			{
				parent.setCellVerticalAlignment(context.child, AlignmentAttributeParser.getVerticalAlignment(context.verticalAlignment));
			}
			if (!StringUtils.isEmpty(context.width))
			{
				parent.setCellWidth(context.child, context.width);
			}
			
			context.height = null;
			context.width = null;
			context.horizontalAlignment = DEFAULT_H_ALIGN;
			context.verticalAlignment = DEFAULT_V_ALIGN;
		}
	}
}