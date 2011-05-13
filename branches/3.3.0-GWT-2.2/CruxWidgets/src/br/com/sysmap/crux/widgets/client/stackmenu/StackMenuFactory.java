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
package br.com.sysmap.crux.widgets.client.stackmenu;

import java.util.LinkedList;

import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.declarative.TagEvent;
import br.com.sysmap.crux.core.client.declarative.TagEvents;
import br.com.sysmap.crux.core.client.event.bind.SelectionEvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.children.HasPostProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.client.utils.StringUtils;

import com.google.gwt.dom.client.Element;

/**
 * Factory for Stack Menu
 * @author Gesse S. F. Dafe
 */
@br.com.sysmap.crux.core.client.declarative.DeclarativeFactory(id="stackMenu", library="widgets")
public class StackMenuFactory extends WidgetFactory<StackMenu>
{
	@Override
	public StackMenu instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		return new StackMenu();
	}
	
	@Override
	@TagEvents({
		@TagEvent(SelectionEvtBind.class)
	})
	public void processEvents(WidgetFactoryContext<StackMenu> context) throws InterfaceConfigException
	{
	    super.processEvents(context);
	}
	
	@Override
	@TagChildren({
		@TagChild(StackMenuItemProcessor.class)
	})
	public void processChildren(WidgetFactoryContext<StackMenu> context) throws InterfaceConfigException 
	{
		LinkedList<StackMenuItem> stackItems = new LinkedList<StackMenuItem>();
		context.setAttribute("stackItems", stackItems);
	}

	@TagChildAttributes(tagName="item", minOccurs="0", maxOccurs="unbounded")
	public static class StackMenuItemProcessor extends WidgetChildProcessor<StackMenu> implements HasPostProcessor<StackMenu>
	{
		@SuppressWarnings("unchecked")
        @Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="key", required=true),
			@TagAttributeDeclaration(value="label", supportsI18N=true, required=true),
			@TagAttributeDeclaration(value="open", type=Boolean.class),
			@TagAttributeDeclaration(value="styleName"),
			@TagAttributeDeclaration(value="tooltip")
		})
		@TagChildren({
			@TagChild(StackMenuItemProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<StackMenu> context) throws InterfaceConfigException 
		{
			String key = context.readChildProperty("key");
			String label = context.readChildProperty(ScreenFactory.getInstance().getDeclaredMessage("label"));
			
			StackMenuItem item = new StackMenuItem(key, label);
			
			String open = context.readChildProperty("open");
			if (!StringUtils.isEmpty(open))
			{
				item.setOpen(Boolean.parseBoolean(open));
			}
			
			String styleName = context.readChildProperty("styleName");
			if (!StringUtils.isEmpty(styleName))
			{
				item.setStyleName(EscapeUtils.quote(styleName));
			}
			
			String tooltip = context.readChildProperty("tooltip");
			if (!StringUtils.isEmpty(tooltip))
			{
				item.setTitle(EscapeUtils.quote(tooltip));
			}
			
			LinkedList<StackMenuItem> stackItems = (LinkedList<StackMenuItem>) context.getAttribute("stackItems");
			
			if (stackItems.size() == 0)
			{
				context.getRootWidget().add(item);
			}
			else
			{
				stackItems.peek().add(item);
			}
			stackItems.addFirst(item);
		}
		
		@SuppressWarnings("unchecked")
        public void postProcessChildren(WidgetChildProcessorContext<StackMenu> context) throws InterfaceConfigException
		{
			LinkedList<StackMenuItem> stackItems = (LinkedList<StackMenuItem>) context.getAttribute("stackItems");
			stackItems.removeFirst();
		}
	}
}