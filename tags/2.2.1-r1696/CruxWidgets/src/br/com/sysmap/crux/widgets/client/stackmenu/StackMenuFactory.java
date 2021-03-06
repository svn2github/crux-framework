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

import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;

import com.google.gwt.dom.client.Element;

/**
 * Factory for Stack Menu
 * @author Gess� S. F. Daf� - <code>gessedafe@gmail.com</code>
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
	@TagChildren({
		@TagChild(StackMenuItemProcessor.class)
	})
	public void processChildren(WidgetFactoryContext<StackMenu> context) throws InterfaceConfigException {}

	@TagChildAttributes(tagName="item", minOccurs="0", maxOccurs="unbounded", type=StackMenuItemFactory.class)
	public static class StackMenuItemProcessor extends WidgetChildProcessor<StackMenu>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<StackMenu> context) throws InterfaceConfigException 
		{
			Element childElement = context.getChildElement();
			StackMenuItem childWidget = (StackMenuItem)createChildWidget(childElement, childElement.getId());
			context.getRootWidget().add(childWidget);
		}
	}
}