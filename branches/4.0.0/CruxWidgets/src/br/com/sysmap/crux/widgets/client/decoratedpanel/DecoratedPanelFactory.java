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
package br.com.sysmap.crux.widgets.client.decoratedpanel;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.creator.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.HTMLTag;
import br.com.sysmap.crux.gwt.rebind.CellPanelContext;

/**
 * Factory for Decorated Panel widget
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="decoratedPanel", library="widgets")
public class DecoratedPanelFactory extends AbstractDecoratedPanelFactory
{
	@Override
	@TagChildren({
		@TagChild(ChildrenProcessor.class)
	})
	public void processChildren(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException {}
	
	public static class ChildrenProcessor extends ChoiceChildProcessor<CellPanelContext>
	{
		@Override
		@TagChildren({
			@TagChild(HTMLChildProcessor.class),
			@TagChild(TextChildProcessor.class),
			@TagChild(WidgetProcessor.class)
		})
		public void processChildren(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException {}
	}
	
	@TagChildAttributes(tagName="html", type=HTMLTag.class)
	public static class HTMLChildProcessor extends WidgetChildProcessor<CellPanelContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException
		{
			String rootWidget = context.getWidget();
			out.println(rootWidget+".setContentHtml("+EscapeUtils.quote(ensureHtmlChild(context.getChildElement(), true))+");");
		}
	}

	@TagChildAttributes(tagName="text", type=String.class)
	public static class TextChildProcessor extends WidgetChildProcessor<CellPanelContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException
		{
			String rootWidget = context.getWidget();
			out.println(rootWidget+".setContentText("+EscapeUtils.quote(ensureTextChild(context.getChildElement(), true))+");");
		}
	}
	
	@TagChildAttributes(tagName="widget")
	public static class WidgetProcessor extends WidgetChildProcessor<CellPanelContext>
	{
		@Override
		@TagChildren({
			@TagChild(WidgetContentProcessor.class)
		})
		public void processChildren(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException {}
	}

	@TagChildAttributes(widgetProperty="contentWidget")
	public static class WidgetContentProcessor extends AnyWidgetChildProcessor<CellPanelContext> {}

}