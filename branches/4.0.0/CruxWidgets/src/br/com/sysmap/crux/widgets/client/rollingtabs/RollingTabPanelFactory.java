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
package br.com.sysmap.crux.widgets.client.rollingtabs;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.event.bind.ClickEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyDownEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyPressEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyUpEvtBind;
import br.com.sysmap.crux.core.client.screen.AttributeParser;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenLoadHandler;
import br.com.sysmap.crux.core.client.screen.WidgetFactoryContext;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.HTMLTag;
import br.com.sysmap.crux.core.client.screen.factory.HasAnimationFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasBeforeSelectionHandlersFactory;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.gwt.client.CompositeFactory;
import br.com.sysmap.crux.widgets.client.rollingtabs.RollingTabBar.Tab;

import com.google.gwt.user.client.ui.Widget;

class RollingTabPanelContext extends WidgetFactoryContext
{

	public CruxMetaDataElement tabElement;
	public boolean isHTMLTitle;
	public String title;
	public Widget titleWidget;
	public void clearAttributes()
    {
	    isHTMLTitle = false;
	    title = null;
	    titleWidget = null;
	    tabElement = null;
    }
}

/**
 * Factory for TabPanel widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="rollingTabPanel", library="widgets")
public class RollingTabPanelFactory extends CompositeFactory<RollingTabPanel, RollingTabPanelContext> 
implements HasAnimationFactory<RollingTabPanel, RollingTabPanelContext>, 
HasBeforeSelectionHandlersFactory<RollingTabPanel, RollingTabPanelContext>
{
	@Override
	public RollingTabPanel instantiateWidget(CruxMetaDataElement element, String widgetId) 
	{
		return new RollingTabPanel();
	}
	
	@Override
	@TagAttributes({
		@TagAttribute(value="visibleTab", type=Integer.class, parser=VisibleTabAttributeParser.class)
	})
	public void processAttributes(RollingTabPanelContext context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}

	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class VisibleTabAttributeParser implements AttributeParser<RollingTabPanelContext>
	{
		public void processAttribute(RollingTabPanelContext context, final String propertyValue)
        {
			final RollingTabPanel widget = context.getWidget();
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
	public void processChildren(RollingTabPanelContext context) throws InterfaceConfigException 
	{
	}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", tagName="tab" )
	public static class TabProcessor extends WidgetChildProcessor<RollingTabPanel, RollingTabPanelContext>
	{
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="tabEnabled", type=Boolean.class, defaultValue="true"),
			@TagAttributeDeclaration(value="tabWordWrap", type=Boolean.class, defaultValue="true")
		})
		@TagEventsDeclaration({
			@TagEventDeclaration("onClick"),
			@TagEventDeclaration("onKeyUp"),
			@TagEventDeclaration("onKeyDown"),
			@TagEventDeclaration("onKeyPress")
		})
		@TagChildren({
			@TagChild(TabTitleProcessor.class), 
			@TagChild(TabWidgetProcessor.class)
		})	
		public void processChildren(RollingTabPanelContext context) throws InterfaceConfigException
		{
			context.tabElement = context.getChildElement();
		}
		
	}
	
	@TagChildAttributes(minOccurs="0")
	public static class TabTitleProcessor extends ChoiceChildProcessor<RollingTabPanel, RollingTabPanelContext> 
	{
		@Override
		@TagChildren({
			@TagChild(TextTabProcessor.class),
			@TagChild(HTMLTabProcessor.class),
			@TagChild(WidgetTitleTabProcessor.class)
		})		
		public void processChildren(RollingTabPanelContext context) throws InterfaceConfigException {}
		
	}
	
	@TagChildAttributes(tagName="tabText", type=String.class)
	public static class TextTabProcessor extends WidgetChildProcessor<RollingTabPanel, RollingTabPanelContext> 
	{
		@Override
		public void processChildren(RollingTabPanelContext context) throws InterfaceConfigException 
		{
			context.title = ScreenFactory.getInstance().getDeclaredMessage(ensureTextChild(context.getChildElement(), true));
			context.isHTMLTitle = false;
		}
	}
	
	@TagChildAttributes(tagName="tabHtml", type=HTMLTag.class)
	public static class HTMLTabProcessor extends WidgetChildProcessor<RollingTabPanel, RollingTabPanelContext>
	{
		@Override
		public void processChildren(RollingTabPanelContext context) throws InterfaceConfigException 
		{
			context.title = ensureHtmlChild(context.getChildElement(), true);
			context.isHTMLTitle = true;
		}
	}
	
	@TagChildAttributes(tagName="tabWidget")
	public static class WidgetTitleTabProcessor extends WidgetChildProcessor<RollingTabPanel, RollingTabPanelContext> 
	{
		@Override
		@TagChildren({
			@TagChild(WidgetTitleProcessor.class)
		})	
		public void processChildren(RollingTabPanelContext context) throws InterfaceConfigException {}
	}

	@TagChildAttributes(type=AnyWidget.class)
	public static class WidgetTitleProcessor extends WidgetChildProcessor<RollingTabPanel, RollingTabPanelContext> 
	{
		@Override
		public void processChildren(RollingTabPanelContext context) throws InterfaceConfigException
		{
			context.titleWidget = createChildWidget(context.getChildElement());
		}
	}
	
	@TagChildAttributes(tagName="panelContent")
	public static class TabWidgetProcessor extends WidgetChildProcessor<RollingTabPanel, RollingTabPanelContext> 
	{
		@Override
		@TagChildren({
			@TagChild(WidgetContentProcessor.class)
		})	
		public void processChildren(RollingTabPanelContext context) throws InterfaceConfigException {}
	}

	@TagChildAttributes(type=AnyWidget.class)
	public static class WidgetContentProcessor extends WidgetChildProcessor<RollingTabPanel, RollingTabPanelContext> 
	{
		@Override
		public void processChildren(RollingTabPanelContext context) throws InterfaceConfigException
		{
			Widget widget = createChildWidget(context.getChildElement());
			RollingTabPanel rootWidget = context.getWidget();
			
			if (context.titleWidget != null)
			{
				rootWidget.add(widget, context.titleWidget);
			}
			else
			{
				rootWidget.add(widget, context.title, context.isHTMLTitle);
			}
			updateTabState(context);
		}
		
		private void updateTabState(RollingTabPanelContext context)
		{
			String enabled = context.tabElement.getProperty("enabled");
			RollingTabPanel rootWidget = context.getWidget();
			int tabCount = rootWidget.getTabBar().getTabCount();
			if (enabled != null && enabled.length() >0)
			{
				rootWidget.getTabBar().setTabEnabled(tabCount-1, Boolean.parseBoolean(enabled));
			}

			Tab currentTab = rootWidget.getTabBar().getTab(tabCount-1);
			
			String wordWrap = context.tabElement.getProperty("wordWrap");
			if (wordWrap != null && wordWrap.trim().length() > 0)
			{
				currentTab.setWordWrap(Boolean.parseBoolean(wordWrap));
			}

			clickEvtBind.bindEvent(context.tabElement, currentTab);
			keyUpEvtBind.bindEvent(context.tabElement, currentTab);
			keyPressEvtBind.bindEvent(context.tabElement, currentTab);
			keyDownEvtBind.bindEvent(context.tabElement, currentTab);

			context.clearAttributes();
		}	
		private static ClickEvtBind clickEvtBind = new ClickEvtBind();
		private static KeyUpEvtBind keyUpEvtBind = new KeyUpEvtBind();
		private static KeyPressEvtBind keyPressEvtBind = new KeyPressEvtBind();
		private static KeyDownEvtBind keyDownEvtBind = new KeyDownEvtBind();
	}
}
