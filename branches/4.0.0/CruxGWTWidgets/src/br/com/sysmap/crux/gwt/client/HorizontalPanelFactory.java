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
import br.com.sysmap.crux.core.client.screen.factory.HasHorizontalAlignmentFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasVerticalAlignmentFactory;
import br.com.sysmap.crux.core.client.screen.factory.align.HorizontalAlignment;
import br.com.sysmap.crux.core.client.screen.factory.align.VerticalAlignment;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="horizontalPanel", library="gwt")
public class HorizontalPanelFactory extends CellPanelFactory<HorizontalPanel, CellPanelContext>
	   implements HasHorizontalAlignmentFactory<HorizontalPanel, CellPanelContext>, 
	   			  HasVerticalAlignmentFactory<HorizontalPanel, CellPanelContext>
{

	@Override
	public HorizontalPanel instantiateWidget(CruxMetaDataElement element, String widgetId)
	{
		return new HorizontalPanel();
	}
	
	@Override
	@TagChildren({
		@TagChild(HorizontalPanelProcessor.class)
	})		
	public void processChildren(CellPanelContext context) throws InterfaceConfigException {}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded")
	public static class  HorizontalPanelProcessor extends AbstractCellPanelProcessor<HorizontalPanel, CellPanelContext> 
	{
		@Override
		@TagChildren({
			@TagChild(HorizontalProcessor.class),
			@TagChild(HorizontalWidgetProcessor.class)
		})		
		public void processChildren(CellPanelContext context) throws InterfaceConfigException 
		{
			super.processChildren(context);
		}
	}
	
	public static class HorizontalProcessor extends AbstractCellProcessor<HorizontalPanel, CellPanelContext>
	{
		@TagAttributesDeclaration({
			@TagAttributeDeclaration("height"),
			@TagAttributeDeclaration("width"),
			@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign"),
			@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
		})
		@TagChildren({
			@TagChild(value=HorizontalWidgetProcessor.class)
		})		
		public void processChildren(CellPanelContext context) throws InterfaceConfigException 
		{
			super.processChildren(context);
		}
	}
		
	public static class HorizontalWidgetProcessor extends AbstractCellWidgetProcessor<HorizontalPanel, CellPanelContext> 
	{
		@Override
		public void processChildren(CellPanelContext context) throws InterfaceConfigException
		{
			Widget child = createChildWidget(context.getChildElement());
			HorizontalPanel rootWidget = context.getWidget();
			rootWidget.add(child);
			context.child = child;
			super.processChildren(context);
			context.child = null;
		}
	}	
}
