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
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.DockLayoutPanel.Direction;

class SplitLayoutPanelContext extends DockLayoutPanelContext
{

	public String minSize;
}

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="splitLayoutPanel", library="gwt")
public class SplitLayoutPanelFactory extends AbstractDockLayoutPanelFactory<SplitLayoutPanel, SplitLayoutPanelContext>
{
	@Override
	public SplitLayoutPanel instantiateWidget(CruxMetaDataElement element, String widgetId)
	{
		return new SplitLayoutPanel();
	}
	
	@Override
	@TagChildren({
		@TagChild(SplitLayoutPanelProcessor.class)
	})		
	public void processChildren(SplitLayoutPanelContext context) throws InterfaceConfigException {}
	
	public static class SplitLayoutPanelProcessor extends AbstractDockLayoutPanelProcessor<SplitLayoutPanel, SplitLayoutPanelContext>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="minSize", type=Integer.class)
		})
		@TagChildren({
			@TagChild(SplitLayoutPanelWidgetProcessor.class)
		})		
		public void processChildren(SplitLayoutPanelContext context) throws InterfaceConfigException
		{
			context.minSize = context.readChildProperty("minSize");
			super.processChildren(context);
		}
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class SplitLayoutPanelWidgetProcessor extends AbstractDockPanelWidgetProcessor<SplitLayoutPanel, SplitLayoutPanelContext> 
	{
		/**
		 * @see br.com.sysmap.crux.gwt.client.AbstractDockLayoutPanelFactory.AbstractDockPanelWidgetProcessor#processAnimatedChild(br.com.sysmap.crux.gwt.client.DockLayoutPanelContext, com.google.gwt.user.client.ui.Widget, com.google.gwt.user.client.ui.DockLayoutPanel.Direction, double)
		 */
		@Override
		protected void processAnimatedChild(final SplitLayoutPanelContext context, final Widget childWidget,
				                            final Direction direction, final double size)
		{
			
			context.addChildWithAnimation(new Command(){
				public void execute()
				{
					processChild(context, childWidget, direction, size, context.minSize);
				}
			});
		}
		
		/**
		 * @see br.com.sysmap.crux.gwt.client.AbstractDockLayoutPanelFactory.AbstractDockPanelWidgetProcessor#processChild(br.com.sysmap.crux.gwt.client.DockLayoutPanelContext, com.google.gwt.user.client.ui.Widget, com.google.gwt.user.client.ui.DockLayoutPanel.Direction, double)
		 */
		@Override
		protected void processChild(SplitLayoutPanelContext context, Widget childWidget, Direction direction, double size)
		{
			processChild(context, childWidget, direction, size, context.minSize);
		}

		/**
		 * @param context
		 * @param childWidget
		 * @param direction
		 * @param size
		 * @param minSize
		 */
		protected void processChild(final SplitLayoutPanelContext context, final Widget childWidget, Direction direction, double size, final String minSize)
		{
			super.processChild(context, childWidget, direction, size);
			if (!StringUtils.isEmpty(minSize))
			{
				Scheduler.get().scheduleDeferred(new ScheduledCommand(){
					public void execute()
					{
						SplitLayoutPanel rootWidget = context.getWidget();
						rootWidget.setWidgetMinSize(childWidget, Integer.parseInt(minSize));
					}
				});
			}
		}
	}
}
