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
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.creator.align.AlignmentAttributeParser;
import br.com.sysmap.crux.core.rebind.widget.creator.align.HorizontalAlignment;
import br.com.sysmap.crux.core.rebind.widget.creator.align.VerticalAlignment;
import br.com.sysmap.crux.core.rebind.widget.creator.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildren;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class CellPanelFactory <C extends CellPanelContext> extends ComplexPanelFactory<C>
{
	private static final String DEFAULT_V_ALIGN = HasVerticalAlignment.ALIGN_MIDDLE.getVerticalAlignString();
	private static final String DEFAULT_H_ALIGN = HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString();
	
	@Override
	@TagAttributes({
		@TagAttribute(value="borderWidth",type=Integer.class),
		@TagAttribute(value="spacing",type=Integer.class)
	})
	public void processAttributes(SourcePrinter out, C context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
	}
	
	@Override
	@TagChildren({
		@TagChild(CellPanelProcessor.class)
	})		
	public void processChildren(SourcePrinter out, C context) throws CruxGeneratorException {}
	
	public static class CellPanelProcessor extends AbstractCellPanelProcessor<CellPanelContext>{} 

	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded")
	public static abstract class AbstractCellPanelProcessor<C extends CellPanelContext> extends ChoiceChildProcessor<C> 
	{
		@Override
		@TagChildren({
			@TagChild(CellProcessor.class),
			@TagChild(CellWidgetProcessor.class)
		})		
		public void processChildren(SourcePrinter out, C context) throws CruxGeneratorException 
		{
			context.horizontalAlignment = DEFAULT_H_ALIGN;
			context.verticalAlignment = DEFAULT_V_ALIGN;
		}
	}
	
	public static class CellProcessor extends AbstractCellProcessor<CellPanelContext>{}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", tagName="cell")
	public static abstract class AbstractCellProcessor<C extends CellPanelContext> extends WidgetChildProcessor<C> 
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
		public void processChildren(SourcePrinter out, C context) throws CruxGeneratorException 
		{
			context.height = context.readChildProperty("height");
			context.width = context.readChildProperty("width");
			context.horizontalAlignment = context.readChildProperty("horizontalAlignment");
			context.verticalAlignment = context.readChildProperty("verticalAlignment");
		}
	}
	
	public static class CellWidgetProcessor extends AbstractCellWidgetProcessor<CellPanelContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException
		{
			String child = getWidgetCreator().createChildWidget(out, context.getChildElement());
			context.child = child;
			super.processChildren(out, context);
			context.child = null;
		}
	}
	
	@TagChildAttributes(type=AnyWidget.class)
	static class AbstractCellWidgetProcessor<C extends CellPanelContext> extends WidgetChildProcessor<C> 
	{
		@Override
		public void processChildren(SourcePrinter out, C context) throws CruxGeneratorException
		{
			String parent = context.getWidget();
			if (!StringUtils.isEmpty(context.height))
			{
				out.println(parent+".setCellHeight("+context.child+", "+EscapeUtils.quote(context.height)+");");
			}
			if (!StringUtils.isEmpty(context.horizontalAlignment))
			{
				out.println(parent+".setCellHorizontalAlignment("+context.child+", "+
					  AlignmentAttributeParser.getHorizontalAlignment(context.horizontalAlignment, HasHorizontalAlignment.class.getCanonicalName()+".ALIGN_DEFAULT")+");");
			}
			if (!StringUtils.isEmpty(context.verticalAlignment))
			{
				out.println(parent+".setCellVerticalAlignment("+context.child+", "+AlignmentAttributeParser.getVerticalAlignment(context.verticalAlignment)+");");
			}
			if (!StringUtils.isEmpty(context.width))
			{
				out.println(parent+".setCellWidth("+context.child+", "+EscapeUtils.quote(context.width)+");");
			}
			
			context.height = null;
			context.width = null;
			context.horizontalAlignment = DEFAULT_H_ALIGN;
			context.verticalAlignment = DEFAULT_V_ALIGN;
		}
	}
}