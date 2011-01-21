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

import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenLoadHandler;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.AttributeProcessor;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.HasBeforeSelectionHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasSelectionHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.HTMLTag;
import br.com.sysmap.crux.core.rebind.widget.creator.event.ClickEvtBind;
import br.com.sysmap.crux.core.rebind.widget.creator.event.KeyDownEvtBind;
import br.com.sysmap.crux.core.rebind.widget.creator.event.KeyPressEvtBind;
import br.com.sysmap.crux.core.rebind.widget.creator.event.KeyUpEvtBind;

import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabBar.Tab;
import com.google.gwt.user.client.ui.Widget;

class TabBarContext extends WidgetCreatorContext
{
	public JSONObject tabElement;
}


/**
 * Factory for TabBar widgets
 * @author Thiago da Rosa de Bustamante
 */
public abstract class AbstractTabBarFactory extends CompositeFactory<TabBarContext> 
       implements HasBeforeSelectionHandlersFactory<TabBarContext>, HasSelectionHandlersFactory<TabBarContext>
{
	@Override
	@TagAttributes({
		@TagAttribute(value="visibleTab", type=Integer.class, processor=VisibleTabAttributeParser.class)
	})
	public void processAttributes(SourcePrinter out, TabBarContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class VisibleTabAttributeParser extends AttributeProcessor<TabBarContext>
	{
		@Override
        public void processAttribute(SourcePrinter out, TabBarContext context, String attributeValue)
        {
			printlnPostProcessing(context.getWidget()+".selectTab("+Integer.parseInt(attributeValue)+");");
        }
	}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", tagName="tab" )
	public abstract static class AbstractTabProcessor extends WidgetChildProcessor<TabBarContext> 
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
		public void processChildren(SourcePrinter out, TabBarContext context) throws CruxGeneratorException
		{
			context.tabElement =context.getChildElement();
		}
	}
	
	@TagChildAttributes(tagName="text", type=String.class)
	public abstract static class AbstractTextTabProcessor extends WidgetChildProcessor<TabBarContext>
	{
		@SuppressWarnings("unchecked")
		@Override
		public void processChildren(SourcePrinter out, TabBarContext context) throws CruxGeneratorException 
		{
			String title = getWidgetCreator().getDeclaredMessage(ensureTextChild(context.getChildElement(), true));
			out.println(context.getWidget()+".addTab("+EscapeUtils.quote(title)+");");
			updateTabState(context);
		}
	}
	
	@TagChildAttributes(tagName="html", type=HTMLTag.class)
	public abstract static class AbstractHTMLTabProcessor extends WidgetChildProcessor<TabBarContext>
	{
		@SuppressWarnings("unchecked")
		@Override
		public void processChildren(SourcePrinter out, TabBarContext context) throws CruxGeneratorException 
		{
			String title = ensureHtmlChild(context.getChildElement(), true);
			out.println(context.getWidget()+".addTab("+EscapeUtils.quote(title)+", true);");
			updateTabState(context);
		}
	}
	
	@TagChildAttributes(type=AnyWidget.class)
	public abstract static class AbstractWidgetProcessor extends WidgetChildProcessor<TabBarContext> 
	{
		@SuppressWarnings("unchecked")
		@Override
		public void processChildren(SourcePrinter out, TabBarContext context) throws CruxGeneratorException
		{
			String titleWidget = getWidgetCreator().createChildWidget(out, context.getChildElement());
			out.println(context.getWidget()+".addTab("+titleWidget+");");
			updateTabState(context);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends TabBar> void updateTabState(TabBarContext context)
	{
		String enabled = context.tabElement.optString("enabled");
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
