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
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.AttributeProcessor;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenLoadHandler;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.HTMLTag;
import br.com.sysmap.crux.core.client.screen.factory.HasBeforeSelectionHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasSelectionHandlersFactory;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class TabLayoutPanelContext extends WidgetCreatorContext
{

	public String title;
	public boolean isTitleHTML;
	public Widget titleWidget;
}

/**
 * Factory for TabLayoutPanel widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="tabLayoutPanel", library="gwt")
public class TabLayoutPanelFactory extends CompositeFactory<TabLayoutPanel, TabLayoutPanelContext> 
       implements HasBeforeSelectionHandlersFactory<TabLayoutPanel, TabLayoutPanelContext>, 
                  HasSelectionHandlersFactory<TabLayoutPanel, TabLayoutPanelContext>
{
	@Override
	public TabLayoutPanel instantiateWidget(CruxMetaDataElement element, String widgetId) throws InterfaceConfigException
	{
		String height = element.getProperty("barHeight");
		if (StringUtils.isEmpty(height))
		{
			return new TabLayoutPanel(20, Unit.PX);
		}
		else
		{
			return new TabLayoutPanel(Double.parseDouble(height), AbstractLayoutPanelFactory.getUnit(element.getProperty("unit")));
		}
	}
	
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="barHeight", type=Integer.class, defaultValue="20"),
		@TagAttributeDeclaration(value="unit", type=Unit.class)
	})
	@TagAttributes({
		@TagAttribute(value="visibleTab", type=Integer.class, processor=VisibleTabAttributeParser.class)
	})
	public void processAttributes(TabLayoutPanelContext context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class VisibleTabAttributeParser implements AttributeProcessor<TabLayoutPanelContext>
	{
		public void processAttribute(TabLayoutPanelContext context, final String propertyValue)
        {
			final TabLayoutPanel widget = context.getWidget();
			ScreenFactory.getInstance().addLoadHandler(new ScreenLoadHandler()
			{
				public void onLoad(ScreenLoadEvent event)
				{
					widget.selectTab(Integer.parseInt(propertyValue));
				}
			});
        }
	}
		
	@Override
	@TagChildren({
		@TagChild(TabProcessor.class)
	})	
	public void processChildren(TabLayoutPanelContext context) throws InterfaceConfigException 
	{
	}

	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", tagName="tab" )
	public static class TabProcessor extends WidgetChildProcessor<TabLayoutPanel, TabLayoutPanelContext> 
	{
		@Override
		@TagChildren({
			@TagChild(TabTitleProcessor.class), 
			@TagChild(TabWidgetProcessor.class)
		})	
		public void processChildren(TabLayoutPanelContext context) throws InterfaceConfigException {}
	}

	@TagChildAttributes(minOccurs="0")
	public static class TabTitleProcessor extends ChoiceChildProcessor<TabLayoutPanel, TabLayoutPanelContext> 
	{
		@Override
		@TagChildren({
			@TagChild(TextTabProcessor.class),
			@TagChild(HTMLTabProcessor.class),
			@TagChild(WidgetTitleTabProcessor.class)
		})		
		public void processChildren(TabLayoutPanelContext context) throws InterfaceConfigException {}
		
	}
	
	@TagChildAttributes(tagName="tabText", type=String.class)
	public static class TextTabProcessor extends WidgetChildProcessor<TabLayoutPanel, TabLayoutPanelContext>
	{
		@Override
		public void processChildren(TabLayoutPanelContext context) throws InterfaceConfigException 
		{
			context.title = ScreenFactory.getInstance().getDeclaredMessage(ensureTextChild(context.getChildElement(), true));
			context.isTitleHTML = false;
		}
	}
	
	@TagChildAttributes(tagName="tabHtml", type=HTMLTag.class)
	public static class HTMLTabProcessor extends WidgetChildProcessor<TabLayoutPanel, TabLayoutPanelContext>
	{
		@Override
		public void processChildren(TabLayoutPanelContext context) throws InterfaceConfigException 
		{
			context.title = ensureHtmlChild(context.getChildElement(), true);
			context.isTitleHTML = true;
		}
	}
	
	@TagChildAttributes(tagName="tabWidget")
	public static class WidgetTitleTabProcessor extends WidgetChildProcessor<TabLayoutPanel, TabLayoutPanelContext> 
	{
		@Override
		@TagChildren({
			@TagChild(WidgetTitleProcessor.class)
		})	
		public void processChildren(TabLayoutPanelContext context) throws InterfaceConfigException {}
	}

	@TagChildAttributes(type=AnyWidget.class)
	public static class WidgetTitleProcessor extends WidgetChildProcessor<TabLayoutPanel, TabLayoutPanelContext> 
	{
		@Override
		public void processChildren(TabLayoutPanelContext context) throws InterfaceConfigException
		{
			context.titleWidget = createChildWidget(context.getChildElement());
		}
	}
	
	@TagChildAttributes(tagName="panelContent")
	public static class TabWidgetProcessor extends WidgetChildProcessor<TabLayoutPanel, TabLayoutPanelContext> 
	{
		@Override
		@TagChildren({
			@TagChild(WidgetContentProcessor.class)
		})	
		public void processChildren(TabLayoutPanelContext context) throws InterfaceConfigException {}
	}

	@TagChildAttributes(type=AnyWidget.class)
	public static class WidgetContentProcessor extends WidgetChildProcessor<TabLayoutPanel, TabLayoutPanelContext> 
	{
		@Override
		public void processChildren(TabLayoutPanelContext context) throws InterfaceConfigException
		{
			Widget widget = createChildWidget(context.getChildElement());
			
			TabLayoutPanel rootWidget = context.getWidget();
			
			if (context.titleWidget != null)
			{
				rootWidget.add(widget, context.titleWidget);
			}
			else
			{
				rootWidget.add(widget, context.title, context.isTitleHTML);
			}
		}
	}
}
