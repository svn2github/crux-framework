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
package br.com.sysmap.crux.basic.client;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.factory.HasChangeHandlersFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Represents a List Box component
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="listBox", library="bas")
public class ListBoxFactory extends FocusWidgetFactory<ListBox> 
       implements HasChangeHandlersFactory<ListBox>
{
	@Override
	public ListBox instantiateWidget(Element element, String widgetId) 
	{
		String multiple = element.getAttribute("_multiple");
		if (multiple != null && multiple.trim().length() > 0)
		{
			return new ListBox(Boolean.parseBoolean(multiple));

		}
		return new ListBox();
	}
	
	@Override
	@TagAttributes({
		@TagAttribute(value="visibleItemCount", type=Integer.class)
	})
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="multiple", type=Boolean.class)
	})	
	
	public void processAttributes(WidgetFactoryContext<ListBox> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}

	@Override
	@TagChildren({
		@TagChild(ItemsProcessor.class)
	})
	public void processChildren(WidgetFactoryContext<ListBox> context) throws InterfaceConfigException {}

	@TagChildAttributes(tagName="item", minOccurs="0", maxOccurs="unbounded")
	public static class ItemsProcessor extends WidgetChildProcessor<ListBox>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration("value"),
			@TagAttributeDeclaration("label"),
			@TagAttributeDeclaration(value="selected", type=Boolean.class)
		})
		public void processChildren(WidgetChildProcessorContext<ListBox> context) throws InterfaceConfigException 
		{
			Integer index = (Integer) context.getAttribute("index");
			if (index == null)
			{
				index = 0;
			}

			Element element = context.getChildElement();
			
			String item = element.getAttribute("_label");
			String value = element.getAttribute("_value");
			if (value == null || value.length() == 0)
			{
				value = item;
			}
			context.getRootWidget().insertItem(item, value, index);

			String selected = element.getAttribute("_selected");
			if (selected != null && selected.trim().length() > 0)
			{
				context.getRootWidget().setItemSelected(index, Boolean.parseBoolean(selected));
			}
			context.setAttribute("index", (index+1));
		}
	}
}
