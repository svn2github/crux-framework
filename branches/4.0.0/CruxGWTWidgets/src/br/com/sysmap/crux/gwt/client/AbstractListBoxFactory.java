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
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.WidgetFactoryContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.factory.HasChangeHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasNameFactory;

import com.google.gwt.user.client.ui.ListBox;

class ListBoxContext extends WidgetFactoryContext
{
	int index = 0;
}


/**
 * Base class for implementing factories for many kinds of list boxes.
 * @author Gesse S. F. Dafe - <code>gessedafe@gmail.com</code>
 */
public abstract class AbstractListBoxFactory<T extends ListBox> extends FocusWidgetFactory<T, ListBoxContext> 
				implements HasChangeHandlersFactory<T, ListBoxContext>, HasNameFactory<T, ListBoxContext>
{
	@Override
	@TagAttributes({
		@TagAttribute(value="visibleItemCount", type=Integer.class)
	})
	public void processAttributes(ListBoxContext context) throws InterfaceConfigException
	{
		super.processAttributes(context); 
	}

	@TagChildAttributes(tagName="item", minOccurs="0", maxOccurs="unbounded")
	public abstract static class ItemsProcessor<T extends ListBox> extends WidgetChildProcessor<T, ListBoxContext>
	{
		@SuppressWarnings("unchecked")
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration("value"),
			@TagAttributeDeclaration("label"),
			@TagAttributeDeclaration(value="selected", type=Boolean.class)
		})
		public void processChildren(ListBoxContext context) throws InterfaceConfigException 
		{
			
			String label = context.readChildProperty("label");
			String value = context.readChildProperty("value");
			
			if(label != null && label.length() > 0)
			{
				label = ScreenFactory.getInstance().getDeclaredMessage(label);
			}			
			if (value == null || value.length() == 0)
			{
				value = label;
			}
			T widget = (T)context.getWidget();
			widget.insertItem(label, value, context.index);

			String selected = context.readChildProperty("selected");
			if (selected != null && selected.length() > 0)
			{
				widget.setItemSelected(context.index, Boolean.parseBoolean(selected));
			}
			context.index += 1;
		}
	}
}