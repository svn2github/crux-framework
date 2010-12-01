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
import br.com.sysmap.crux.core.client.screen.WidgetFactoryContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.client.utils.StringUtils;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

class AbsolutePanelContext extends WidgetFactoryContext
{
	String left;
	String top;
}

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="absolutePanel", library="gwt")
public class AbsolutePanelFactory extends ComplexPanelFactory<AbsolutePanel, AbsolutePanelContext>
{
	@Override
	public AbsolutePanel instantiateWidget(CruxMetaDataElement element, String widgetId)
	{
		return new AbsolutePanel();
	}

	@Override
	@TagChildren({
		@TagChild(AbsoluteChildrenProcessor.class)
	})	
	public void processChildren(AbsolutePanelContext context) throws InterfaceConfigException
	{
	}	
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", tagName="widget" )
	public static class AbsoluteChildrenProcessor extends WidgetChildProcessor<AbsolutePanel, AbsolutePanelContext> 
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration("left"),
			@TagAttributeDeclaration("top")
		})
		@TagChildren({
			@TagChild(AbsoluteWidgetProcessor.class)
		})	
		public void processChildren(AbsolutePanelContext context) throws InterfaceConfigException
		{
			context.left = context.readChildProperty("left");
			context.top = context.readChildProperty("top");
		}
	}

	@TagChildAttributes(type=AnyWidget.class)
	public static class AbsoluteWidgetProcessor extends WidgetChildProcessor<AbsolutePanel, AbsolutePanelContext> 
	{
		@Override
		public void processChildren(AbsolutePanelContext context) throws InterfaceConfigException
		{
			Widget child = createChildWidget(context.getChildElement());
			AbsolutePanel absolutePanel = context.getWidget();
			if (!StringUtils.isEmpty(context.left) && !StringUtils.isEmpty(context.top))
			{
				absolutePanel.add(child, Integer.parseInt(context.left), Integer.parseInt(context.top));
			}
			else
			{
				absolutePanel.add(child);
			}
			
			context.left = null;
			context.top = null;
		}
	}	
}
