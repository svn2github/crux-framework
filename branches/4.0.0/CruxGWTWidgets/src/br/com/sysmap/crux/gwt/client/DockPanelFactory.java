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

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.widget.creator.HasHorizontalAlignmentFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasVerticalAlignmentFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.gwt.client.DockPanelContext.DockDirection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Widget;

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
public class DockPanelFactory extends CellPanelFactory<DockPanel, DockPanelContext> 
       implements HasHorizontalAlignmentFactory<DockPanel, DockPanelContext>, 
                  HasVerticalAlignmentFactory<DockPanel, DockPanelContext>
{
	
	@Override
	public DockPanel instantiateWidget(CruxMetaDataElement element, String widgetId)
	{
		return new DockPanel();
	}
	
	@Override
	@TagChildren({
		@TagChild(DockPanelProcessor.class)
	})		
	public void processChildren(DockPanelContext context) throws InterfaceConfigException {}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded")
	public static class DockPanelProcessor extends AbstractCellPanelProcessor<DockPanel, DockPanelContext> 
	{
		@Override
		@TagChildren({
			@TagChild(DockCellProcessor.class),
			@TagChild(DockWidgetProcessor.class)
		})		
		public void processChildren(DockPanelContext context) throws InterfaceConfigException 
		{
			super.processChildren(context);
			context.direction = DockDirection.center;
		}
	}
	
	@TagChildAttributes(tagName="cell", minOccurs="0", maxOccurs="unbounded")
	public static class DockCellProcessor extends AbstractCellProcessor<DockPanel, DockPanelContext> 
	{
		protected GWTMessages messages = GWT.create(GWTMessages.class);
		
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="direction", type=DockDirection.class, defaultValue="center")
		})
		@TagChildren({
			@TagChild(value=DockWidgetProcessor.class)
		})		
		public void processChildren(DockPanelContext context) throws InterfaceConfigException 
		{
			super.processChildren(context);

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
					throw new InterfaceConfigException(messages.dockPanelInvalidDirection(childId, context.getWidgetId()));
				}
			}
			else
			{
				context.direction = DockDirection.center;
			}
		}
	}

	@TagChildAttributes(type=AnyWidget.class)
	public static class DockWidgetProcessor extends AbstractCellWidgetProcessor<DockPanel, DockPanelContext> 
	{
		@Override
		public void processChildren(DockPanelContext context) throws InterfaceConfigException
		{
			CruxMetaDataElement childElement = context.getChildElement();
			Widget child = createChildWidget(childElement);
			DockPanel parent = context.getWidget();
			
			switch (context.direction) {
				case center: parent.add(child, DockPanel.CENTER);
				break;
				case lineStart: parent.add(child, DockPanel.LINE_START);
				break;
				case lineEnd: parent.add(child, DockPanel.LINE_END);
				break;
				case east: parent.add(child, DockPanel.EAST);
				break;
				case north: parent.add(child, DockPanel.NORTH);
				break;
				case south: parent.add(child, DockPanel.SOUTH);
				break;
				case west: parent.add(child, DockPanel.WEST);
				break;

			default:
				break;
			}
			context.direction = DockDirection.center;
			context.child = child;
			super.processChildren(context);
			context.child = null;
		}
	}
}
