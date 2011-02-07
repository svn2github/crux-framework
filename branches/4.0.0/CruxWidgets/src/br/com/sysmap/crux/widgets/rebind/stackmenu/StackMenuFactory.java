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
package br.com.sysmap.crux.widgets.rebind.stackmenu;

import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildren;
import br.com.sysmap.crux.widgets.client.stackmenu.StackMenu;

/**
 * Factory for Stack Menu
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="stackMenu", library="widgets", targetWidget=StackMenu.class)
@TagChildren({
	@TagChild(StackMenuFactory.StackMenuItemProcessor.class)
})
public class StackMenuFactory extends WidgetCreator<WidgetCreatorContext>
{
	@TagChildAttributes(tagName="item", minOccurs="0", maxOccurs="unbounded", type=StackMenuItemFactory.class)
	public static class StackMenuItemProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException 
		{
			String childWidget = getWidgetCreator().createChildWidget(out, context.getChildElement());
			String rootWidget = context.getWidget();
			out.println(rootWidget+".add("+childWidget+");");
		}
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}