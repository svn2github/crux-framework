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
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.HasAnimationFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasCloseHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.HTMLTag;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

class MenuBarContext extends WidgetCreatorContext
{
	String caption;
	boolean isHtml;
	
	public void clearAttributes() 
	{
		caption = null;
		isHtml = false;
	}
}

/**
 * A Factory for MenuBar widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="menuBar", library="gwt")
public class MenuBarFactory extends WidgetCreator<MenuBar, MenuBarContext> 
       implements HasAnimationFactory<MenuBar, MenuBarContext>, HasCloseHandlersFactory<MenuBar, MenuBarContext>
{
	protected GWTMessages messages = GWT.create(GWTMessages.class);
	
	@Override
	public MenuBar instantiateWidget(CruxMetaDataElement element, String widgetId) 
	{
		return new MenuBar(isMenuVertical(element));
	}

	@Override
	@TagAttributes({
		@TagAttribute(value="autoOpen", type=Boolean.class), 
		@TagAttribute(value="focusOnHoverEnabled", type=Boolean.class) 
	})
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="vertical", type=Boolean.class)
	})
	public void processAttributes(MenuBarContext context) throws InterfaceConfigException
	{
		super.processAttributes(context);		
	}

	@Override
	@TagChildren({
		@TagChild(MenutItemsProcessor.class)
	})
	public void processChildren(MenuBarContext context) throws InterfaceConfigException {}

	private boolean isMenuVertical(CruxMetaDataElement element)
	{
		String verticalStr = element.getProperty("vertical");
		boolean vertical = false;
		if (verticalStr != null && verticalStr.length() > 0)
		{
			vertical = (Boolean.parseBoolean(verticalStr));
		}
		return vertical;
	}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded")
	public static class MenutItemsProcessor extends ChoiceChildProcessor<MenuBar, MenuBarContext>
	{
		@Override
		@TagChildren({
			@TagChild(MenutItemProcessor.class),
			@TagChild(MenutItemSeparatorProcessor.class)
		})
		public void processChildren(MenuBarContext context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="menuItem")
	public static class MenutItemProcessor extends WidgetChildProcessor<MenuBar, MenuBarContext>
	{
		@Override
		@TagChildren({
			@TagChild(CaptionProcessor.class),
			@TagChild(MenuChildrenProcessor.class)
		})
		public void processChildren(MenuBarContext context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="separator")
	public static class MenutItemSeparatorProcessor extends WidgetChildProcessor<MenuBar, MenuBarContext>
	{
		@Override
		public void processChildren(final MenuBarContext context) throws InterfaceConfigException
		{
			MenuBar widget = context.getWidget();
			widget.addSeparator();
		}
	}
	
	public static class CaptionProcessor extends ChoiceChildProcessor<MenuBar, MenuBarContext>
	{
		@Override
		@TagChildren({
			@TagChild(TextCaptionProcessor.class),
			@TagChild(HtmlCaptionProcessor.class)
		})
		public void processChildren(MenuBarContext context) throws InterfaceConfigException {}
	}

	@TagChildAttributes(tagName="textCaption")
	public static class TextCaptionProcessor extends WidgetChildProcessor<MenuBar, MenuBarContext>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="text", required=true, supportsI18N=true)
		})
		public void processChildren(final MenuBarContext context) throws InterfaceConfigException
		{
			context.caption = context.readChildProperty("text");
			context.isHtml = false;
		}
	}
	
	@TagChildAttributes(tagName="htmlCaption", type=HTMLTag.class)
	public static class HtmlCaptionProcessor extends WidgetChildProcessor<MenuBar, MenuBarContext>
	{
		@Override
		public void processChildren(final MenuBarContext context) throws InterfaceConfigException
		{
			context.caption = ensureHtmlChild(context.getChildElement(), true);
			context.isHtml = true;
		}
	}

	public static class MenuChildrenProcessor extends ChoiceChildProcessor<MenuBar, MenuBarContext>
	{
		@Override
		@TagChildren({
			@TagChild(CommandProcessor.class),
			@TagChild(SubMenuProcessor.class)
		})
		public void processChildren(MenuBarContext context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="command")
	public static class CommandProcessor extends WidgetChildProcessor<MenuBar, MenuBarContext>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="onExecute", required=true)
		})
		public void processChildren(final MenuBarContext context) throws InterfaceConfigException 
		{
			final Event evt = EvtBind.getWidgetEvent(context.getChildElement(), "onExecute");
			if (evt != null)
			{
				MenuItem item = createMenuItem(context);
				Command cmd =  new Command()
				{
					public void execute() 
					{
						MenuBar widget= context.getWidget();
						Events.callEvent(evt, new ExecuteEvent<MenuBar>(widget, context.getWidgetId()));
					}
				};
				item.setCommand(cmd);
			}
			context.clearAttributes();
		}
	}

	@TagChildAttributes(tagName="subMenu", type=MenuBarFactory.class)
	public static class SubMenuProcessor extends WidgetChildProcessor<MenuBar, MenuBarContext>
	{
		@Override
		public void processChildren(MenuBarContext context) throws InterfaceConfigException 
		{
			MenuBar subMenu = getSubMenu(context);
			MenuItem item = createMenuItem(context);
			item.setSubMenu(subMenu);
		}
	}
	
	/**
	 * Creates a subMenu, based on inner span tags
	 * @param element
	 * @return
	 * @throws InterfaceConfigException 
	 */
	protected static MenuBar getSubMenu(MenuBarContext context) throws InterfaceConfigException
	{
		MenuBar widget = context.getWidget();
		MenuBar subMenu = (MenuBar) createChildWidget(context.getChildElement());	
		subMenu.setAutoOpen(widget.getAutoOpen());
		subMenu.setAnimationEnabled(widget.isAnimationEnabled());
		return subMenu;
	}
	
	/**
	 * @param context
	 * @return
	 */
	protected static  MenuItem createMenuItem(final MenuBarContext context)
	{
		MenuBar widget = context.getWidget();
		return widget.addItem(new MenuItem(context.caption, context.isHtml, (Command)null));
	}
}
