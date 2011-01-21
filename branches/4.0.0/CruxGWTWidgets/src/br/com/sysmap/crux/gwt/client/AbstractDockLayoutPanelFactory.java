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

import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.AnyWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.DockLayoutPanel.Direction;

class DockLayoutPanelContext extends AbstractLayoutPanelContext
{
	String left;
	String top;
	Direction direction;
	Double size = -1.0;
}

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractDockLayoutPanelFactory<T extends DockLayoutPanel, C extends DockLayoutPanelContext> 
	  extends AbstractLayoutPanelFactory<T, C>
{
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", tagName="cell")
	public static abstract class AbstractDockLayoutPanelProcessor<W extends DockLayoutPanel, C extends DockLayoutPanelContext> 
	                       extends WidgetChildProcessor<W, C> 
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="direction", type=Direction.class, defaultValue="CENTER"),
			@TagAttributeDeclaration(value="size", type=Double.class)
		})
		public void processChildren(C context) throws InterfaceConfigException 
		{
			context.direction = getDirection(context.readChildProperty("direction"));
			String sizeStr = context.readChildProperty("size");
			if (StringUtils.isEmpty(sizeStr))
			{
				context.size = -1.0;
			}
			else
			{
				context.size = Double.parseDouble(sizeStr);
			}
		}

		private Direction getDirection(String direction)
		{
			Direction result;
			if (!StringUtils.isEmpty(direction))
			{
				result = Direction.valueOf(direction);
			}
			else
			{
				result = Direction.CENTER;
			}
			return result;
		}
	}
	
	
	@TagChildAttributes(type=AnyWidget.class)
	public static class AbstractDockPanelWidgetProcessor<W extends DockLayoutPanel, C extends DockLayoutPanelContext> extends WidgetChildProcessor<W, C> 
	{
		GWTMessages messages = GWT.create(GWTMessages.class);
		
		@Override
		public void processChildren(C context) throws InterfaceConfigException 
		{
			Widget childWidget = createChildWidget(context.getChildElement());
			
			
			if (!context.direction.equals(Direction.CENTER) && context.size == -1)
			{
				throw new InterfaceConfigException(messages.dockLayoutPanelRequiredSize(context.getWidgetId()));
			}
			
			if (context.animationDuration > 0)
			{
				processAnimatedChild(context, childWidget, context.direction, context.size);
			}
			else
			{
				processChild(context, childWidget, context.direction, context.size);
			}
		}

		/**
		 * @param context
		 * @param childWidget
		 * @param direction
		 * @param size
		 */
		protected void processAnimatedChild(final C context, final Widget childWidget, final Direction direction, final double size)
		{
			context.addChildWithAnimation(new Command(){
				public void execute()
				{
					processChild(context, childWidget, direction, size);
				}
			});
		}

		/**
		 * 
		 * @param context
		 * @param childWidget
		 * @param direction
		 * @param size
		 */
		@SuppressWarnings("unchecked")
		protected void processChild(C context, Widget childWidget, Direction direction, double size)
		{
			W rootWidget = (W)context.getWidget();
			if (direction.equals(Direction.CENTER))
			{
				rootWidget.add(childWidget);	
			}
			else if (direction.equals(Direction.EAST))
			{
				rootWidget.addEast(childWidget, size);
			}
			else if (direction.equals(Direction.NORTH))
			{
				rootWidget.addNorth(childWidget, size);
			}
			else if (direction.equals(Direction.SOUTH))
			{
				rootWidget.addSouth(childWidget, size);				
			}
			else if (direction.equals(Direction.WEST))
			{
				rootWidget.addWest(childWidget, size);
			}
			else if (direction.equals(Direction.LINE_START))
			{
				rootWidget.addLineStart(childWidget, size);
			}
			else if (direction.equals(Direction.LINE_END))
			{
				rootWidget.addLineEnd(childWidget, size);
			}
		}
	}
}
