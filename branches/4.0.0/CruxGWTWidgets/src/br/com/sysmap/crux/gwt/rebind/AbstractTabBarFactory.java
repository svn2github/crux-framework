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
package br.com.sysmap.crux.gwt.rebind;

import org.json.JSONObject;

import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.client.utils.StringUtils;
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
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagEventsDeclaration;

import com.google.gwt.user.client.ui.TabBar.Tab;

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
	public abstract static class AbstractTextTabProcessor extends AbstractTabTitleProcessor
	{
		@Override
		public void processChildren(SourcePrinter out, TabBarContext context) throws CruxGeneratorException 
		{
			String title = getWidgetCreator().getDeclaredMessage(ensureTextChild(context.getChildElement(), true));
			out.println(context.getWidget()+".addTab("+EscapeUtils.quote(title)+");");
			updateTabState(out, context);
		}
	}
	
	@TagChildAttributes(tagName="html", type=HTMLTag.class)
	public abstract static class AbstractHTMLTabProcessor extends AbstractTabTitleProcessor
	{
		@Override
		public void processChildren(SourcePrinter out, TabBarContext context) throws CruxGeneratorException 
		{
			String title = ensureHtmlChild(context.getChildElement(), true);
			out.println(context.getWidget()+".addTab("+EscapeUtils.quote(title)+", true);");
			updateTabState(out, context);
		}
	}
	
	@TagChildAttributes(type=AnyWidget.class)
	public abstract static class AbstractWidgetProcessor extends AbstractTabTitleProcessor
	{
		@Override
		public void processChildren(SourcePrinter out, TabBarContext context) throws CruxGeneratorException
		{
			String titleWidget = getWidgetCreator().createChildWidget(out, context.getChildElement());
			out.println(context.getWidget()+".addTab("+titleWidget+");");
			updateTabState(out, context);
		}
	}
	public abstract static class AbstractTabTitleProcessor extends WidgetChildProcessor<TabBarContext>
	{
		protected void updateTabState(SourcePrinter out, TabBarContext context)
		{
			String enabled = context.tabElement.optString("enabled");
			String widget = context.getWidget();

			if (enabled != null && enabled.length() >0)
			{
				out.println(widget+".setTabEnabled("+widget+".getTabCount()-1, "+Boolean.parseBoolean(enabled)+");");
			}

			String currentTab = getWidgetCreator().createVariableName("currentTab");
			out.println(Tab.class.getCanonicalName()+" "+currentTab+" = "+widget+".getTab("+widget+".getTabCount()-1);");

			String wordWrap = context.tabElement.optString("wordWrap");
			if (wordWrap != null && wordWrap.trim().length() > 0)
			{
				out.println(currentTab+".setWordWrap("+Boolean.parseBoolean(wordWrap)+");");
			}

			String clickEvt = context.tabElement.optString(clickEvtBind.getEventName());
			if (!StringUtils.isEmpty(clickEvt))
			{
				clickEvtBind.processEvent(out, clickEvt, currentTab, null);
			}
			String keyUpEvt = context.tabElement.optString(keyUpEvtBind.getEventName());
			if (!StringUtils.isEmpty(keyUpEvt))
			{
				keyUpEvtBind.processEvent(out, keyUpEvt, currentTab, null);
			}
			String keyPressEvt = context.tabElement.optString(keyPressEvtBind.getEventName());
			if (!StringUtils.isEmpty(keyPressEvt))
			{
				keyPressEvtBind.processEvent(out, keyPressEvt, currentTab, null);
			}
			String keyDownEvt = context.tabElement.optString(keyDownEvtBind.getEventName());
			if (!StringUtils.isEmpty(keyDownEvt))
			{
				keyDownEvtBind.processEvent(out, keyDownEvt, currentTab, null);
			}

			context.tabElement = null;
		}
		private ClickEvtBind clickEvtBind = new ClickEvtBind();
		private KeyUpEvtBind keyUpEvtBind = new KeyUpEvtBind();
		private KeyPressEvtBind keyPressEvtBind = new KeyPressEvtBind();
		private KeyDownEvtBind keyDownEvtBind = new KeyDownEvtBind();
	}
}
