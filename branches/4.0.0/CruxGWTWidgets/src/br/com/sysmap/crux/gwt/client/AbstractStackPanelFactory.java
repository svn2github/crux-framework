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
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenLoadHandler;
import br.com.sysmap.crux.core.rebind.widget.AttributeProcessor;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.HTMLTag;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractStackPanelFactory<T extends StackPanel> extends ComplexPanelFactory<T, AbstractStackPanelFactoryContext>
{
	protected GWTMessages messages = GWT.create(GWTMessages.class);
	
	@Override
	@TagAttributes({
		@TagAttribute(value="visibleStack", type=Integer.class, processor=VisibleStackAttributeParser.class)
	})
	public void processAttributes(AbstractStackPanelFactoryContext context) throws InterfaceConfigException 
	{
		super.processAttributes(context);
	}
	
	/**
	 * @author Gesse Dafe
	 */
	public static class VisibleStackAttributeParser implements AttributeProcessor<AbstractStackPanelFactoryContext>
	{
		public void processAttribute(AbstractStackPanelFactoryContext context, final String propertyValue) 
		{
			final StackPanel widget = context.getWidget();
			ScreenFactory.getInstance().addLoadHandler(new ScreenLoadHandler()
			{
				public void onLoad(ScreenLoadEvent event)
				{
					widget.showStack(Integer.parseInt(propertyValue));
				}
			});
		}		
	}

	@TagChildAttributes(tagName="textTitle", type=String.class)
	public abstract static class AbstractTitleTextProcessor<T extends StackPanel> extends WidgetChildProcessor<T, AbstractStackPanelFactoryContext>
	{
		@Override
		public void processChildren(AbstractStackPanelFactoryContext context) throws InterfaceConfigException 
		{
			context.title = ensureTextChild(context.getChildElement(), true);
			context.isHtmlTitle = false;
		}
	}
	
	@TagChildAttributes(tagName="htmlTitle", type=HTMLTag.class)
	public abstract static class AbstractTitleHTMLProcessor<T extends StackPanel> extends WidgetChildProcessor<T, AbstractStackPanelFactoryContext>
	{
		@Override
		public void processChildren(AbstractStackPanelFactoryContext context) throws InterfaceConfigException 
		{
			context.title = ensureHtmlChild(context.getChildElement(), true);
			context.isHtmlTitle = true;
		}
	}
	
	@TagChildAttributes(minOccurs="0", type=AnyWidget.class)
	public abstract static class AbstractContentWidgetProcessor<T extends StackPanel> extends WidgetChildProcessor<T, AbstractStackPanelFactoryContext> 
	{
		@Override
		public void processChildren(AbstractStackPanelFactoryContext context) throws InterfaceConfigException 
		{
			Widget child = createChildWidget(context.getChildElement());
			StackPanel widget = context.getWidget();
			
			if (context.title == null)
			{
				widget.add(child);
			}
			else
			{
				widget.add(child, context.title, context.isHtmlTitle);
			}
			context.title = null;
			context.isHtmlTitle = false;
		}	
	}
}


class AbstractStackPanelFactoryContext extends WidgetCreatorContext
{
	boolean isHtmlTitle = false;
	String title;
}