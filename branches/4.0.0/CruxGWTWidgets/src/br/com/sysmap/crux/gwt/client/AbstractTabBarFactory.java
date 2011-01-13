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
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.event.bind.ClickEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyDownEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyPressEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyUpEvtBind;
import br.com.sysmap.crux.core.client.screen.AttributeProcessor;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenLoadHandler;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.HTMLTag;
import br.com.sysmap.crux.core.client.screen.factory.HasBeforeSelectionHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasSelectionHandlersFactory;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;

import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabBar.Tab;
import com.google.gwt.user.client.ui.Widget;

class TabBarContext extends WidgetCreatorContext
{
	public CruxMetaDataElement tabElement;
}


/**
 * Factory for TabBar widgets
 * @author Thiago da Rosa de Bustamante
 */
public abstract class AbstractTabBarFactory<T extends TabBar> extends CompositeFactory<T, TabBarContext> 
       implements HasBeforeSelectionHandlersFactory<T, TabBarContext>, HasSelectionHandlersFactory<T, TabBarContext>
{
	@Override
	@TagAttributes({
		@TagAttribute(value="visibleTab", type=Integer.class, processor=VisibleTabAttributeParser.class)
	})
	public void processAttributes(TabBarContext context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class VisibleTabAttributeParser implements AttributeProcessor<TabBarContext>
	{
		public void processAttribute(TabBarContext context, final String propertyValue)
        {
			final TabBar widget = context.getWidget();
			ScreenFactory.getInstance().addLoadHandler(new ScreenLoadHandler()
			{
				public void onLoad(ScreenLoadEvent event)
				{
					widget.selectTab(Integer.parseInt(propertyValue));
				}
			});
        }
	}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", tagName="tab" )
	public abstract static class AbstractTabProcessor<T extends TabBar> extends WidgetChildProcessor<T, TabBarContext> 
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="enabled", type=Boolean.class, defaultValue="true"),
			@TagAttributeDeclaration(value="wordWrap", type=Boolean.class, defaultValue="true")
		})
		@TagEventsDeclaration({
			@TagEventDeclaration("onClick"),
			@TagEventDeclaration("onKeyUp"),
			@TagEventDeclaration("onKeyDown"),
			@TagEventDeclaration("onKeyPress")
		})
		public void processChildren(TabBarContext context) throws InterfaceConfigException
		{
			context.tabElement =context.getChildElement();
		}
	}
	
	@TagChildAttributes(tagName="text", type=String.class)
	public abstract static class AbstractTextTabProcessor<T extends TabBar> extends WidgetChildProcessor<T, TabBarContext>
	{
		@SuppressWarnings("unchecked")
		@Override
		public void processChildren(TabBarContext context) throws InterfaceConfigException 
		{
			String title = ensureTextChild(context.getChildElement(), true);
			T widget = (T)context.getWidget();
			widget.addTab(title);
			updateTabState(context);
		}
	}
	
	@TagChildAttributes(tagName="html", type=HTMLTag.class)
	public abstract static class AbstractHTMLTabProcessor<T extends TabBar> extends WidgetChildProcessor<T, TabBarContext>
	{
		@SuppressWarnings("unchecked")
		@Override
		public void processChildren(TabBarContext context) throws InterfaceConfigException 
		{
			String title = ensureHtmlChild(context.getChildElement(), true);
			T widget = (T)context.getWidget();
			widget.addTab(title, true);
			updateTabState(context);
		}
	}
	
	@TagChildAttributes(type=AnyWidget.class)
	public abstract static class AbstractWidgetProcessor<T extends TabBar> extends WidgetChildProcessor<T, TabBarContext> 
	{
		@SuppressWarnings("unchecked")
		@Override
		public void processChildren(TabBarContext context) throws InterfaceConfigException
		{
			Widget titleWidget = createChildWidget(context.getChildElement());
			T widget = (T)context.getWidget();
			widget.addTab(titleWidget);
			updateTabState(context);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends TabBar> void updateTabState(TabBarContext context)
	{
		String enabled = context.tabElement.getProperty("enabled");
		T widget = (T)context.getWidget();
		int tabCount = widget.getTabCount();
		if (enabled != null && enabled.length() >0)
		{
			widget.setTabEnabled(tabCount-1, Boolean.parseBoolean(enabled));
		}

		Tab currentTab = widget.getTab(tabCount-1);

		String wordWrap = context.tabElement.getProperty("wordWrap");
		if (wordWrap != null && wordWrap.trim().length() > 0)
		{
			currentTab.setWordWrap(Boolean.parseBoolean(wordWrap));
		}

		clickEvtBind.bindEvent(context.tabElement, currentTab);
		keyUpEvtBind.bindEvent(context.tabElement, currentTab);
		keyPressEvtBind.bindEvent(context.tabElement, currentTab);
		keyDownEvtBind.bindEvent(context.tabElement, currentTab);
		
		context.tabElement = null;
	}
	private static ClickEvtBind clickEvtBind = new ClickEvtBind();
	private static KeyUpEvtBind keyUpEvtBind = new KeyUpEvtBind();
	private static KeyPressEvtBind keyPressEvtBind = new KeyPressEvtBind();
	private static KeyDownEvtBind keyDownEvtBind = new KeyDownEvtBind();
}
