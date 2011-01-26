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

import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.creator.HasChangeHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasNameFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildAttributes;

/**
 * Base class for implementing factories for many kinds of list boxes.
 * @author Gesse S. F. Dafe - <code>gessedafe@gmail.com</code>
 */
public abstract class AbstractListBoxFactory extends FocusWidgetFactory<ListBoxContext> 
				implements HasChangeHandlersFactory<ListBoxContext>, HasNameFactory<ListBoxContext>
{
	@Override
	@TagAttributes({
		@TagAttribute(value="visibleItemCount", type=Integer.class)
	})
	public void processAttributes(SourcePrinter out, ListBoxContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context); 
	}

	@TagChildAttributes(tagName="item", minOccurs="0", maxOccurs="unbounded")
	public abstract static class ItemsProcessor extends WidgetChildProcessor<ListBoxContext>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration("value"),
			@TagAttributeDeclaration(value="label", supportsI18N=true),
			@TagAttributeDeclaration(value="selected", type=Boolean.class)
		})
		public void processChildren(SourcePrinter out, ListBoxContext context) throws CruxGeneratorException 
		{
			
			String label = context.readChildProperty("label");
			String value = context.readChildProperty("value");
			
			if(label != null && label.length() > 0)
			{
				label = getWidgetCreator().getDeclaredMessage(label);
			}			
			if (value == null || value.length() == 0)
			{
				value = label;
			}
			out.println(context.getWidget()+".insertItem("+EscapeUtils.quote(label)+", "+EscapeUtils.quote(value)+", "+context.index+");");

			String selected = context.readChildProperty("selected");
			if (selected != null && selected.length() > 0)
			{
				out.println(context.getWidget()+".setItemSelected("+context.index+", "+Boolean.parseBoolean(selected)+");");
			}
			context.index += 1;
		}
	}
}