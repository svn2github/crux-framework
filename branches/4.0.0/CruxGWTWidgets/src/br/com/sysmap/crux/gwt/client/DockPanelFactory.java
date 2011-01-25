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
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.creator.HasHorizontalAlignmentFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasVerticalAlignmentFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.gwt.client.DockPanelContext.DockDirection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.DockPanel;

@SuppressWarnings("deprecation")
class DockPanelContext extends CellPanelContext
{
	public static enum DockDirection{center, lineStart, lineEnd, east, north, south, west}
	public DockDirection direction;
	
}

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@SuppressWarnings("deprecation")
@DeclarativeFactory(id="dockPanel", library="gwt")
public class DockPanelFactory extends CellPanelFactory<DockPanelContext> 
       implements HasHorizontalAlignmentFactory<DockPanelContext>, 
                  HasVerticalAlignmentFactory<DockPanelContext>
{
	@Override
	@TagChildren({
		@TagChild(DockPanelProcessor.class)
	})		
	public void processChildren(SourcePrinter out, DockPanelContext context) throws CruxGeneratorException {}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded")
	public static class DockPanelProcessor extends AbstractCellPanelProcessor<DockPanelContext> 
	{
		@Override
		@TagChildren({
			@TagChild(DockCellProcessor.class),
			@TagChild(DockWidgetProcessor.class)
		})		
		public void processChildren(SourcePrinter out, DockPanelContext context) throws CruxGeneratorException 
		{
			super.processChildren(out, context);
			context.direction = DockDirection.center;
		}
	}
	
	@TagChildAttributes(tagName="cell", minOccurs="0", maxOccurs="unbounded")
	public static class DockCellProcessor extends AbstractCellProcessor<DockPanelContext> 
	{
		protected GWTMessages messages = GWT.create(GWTMessages.class);
		
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="direction", type=DockDirection.class, defaultValue="center")
		})
		@TagChildren({
			@TagChild(value=DockWidgetProcessor.class)
		})		
		public void processChildren(SourcePrinter out, DockPanelContext context) throws CruxGeneratorException 
		{
			super.processChildren(out, context);

			String directionProp = context.readChildProperty("direction");
			if (!StringUtils.isEmpty(directionProp))
			{
				try
				{
					context.direction = DockDirection.valueOf(directionProp);
				}
				catch (Exception e) 
				{
					String childId = context.readChildProperty("id");
					throw new CruxGeneratorException(messages.dockPanelInvalidDirection(childId, context.getWidgetId()));
				}
			}
			else
			{
				context.direction = DockDirection.center;
			}
		}
	}

	@TagChildAttributes(type=AnyWidget.class)
	public static class DockWidgetProcessor extends AbstractCellWidgetProcessor<DockPanelContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, DockPanelContext context) throws CruxGeneratorException
		{
			JSONObject childElement = context.getChildElement();
			String child = getWidgetCreator().createChildWidget(out, childElement);
			String parent = context.getWidget();
			
			switch (context.direction) {
				case center: out.println(parent+".add("+child+", "+DockPanel.class.getCanonicalName()+".CENTER);");
				break;
				case lineStart: out.println(parent+".add("+child+", "+DockPanel.class.getCanonicalName()+".LINE_START);");
				break;
				case lineEnd: out.println(parent+".add("+child+", "+DockPanel.class.getCanonicalName()+".LINE_END);");
				break;
				case east: out.println(parent+".add("+child+", "+DockPanel.class.getCanonicalName()+".EAST);");
				break;
				case north: out.println(parent+".add("+child+", "+DockPanel.class.getCanonicalName()+".NORTH);");
				break;
				case south: out.println(parent+".add("+child+", "+DockPanel.class.getCanonicalName()+".SOUTH);");
				break;
				case west: out.println(parent+".add("+child+", "+DockPanel.class.getCanonicalName()+".WEST);");
				break;

			default:
				break;
			}
			context.direction = DockDirection.center;
			context.child = child;
			super.processChildren(out, context);
			context.child = null;
		}
	}
}
