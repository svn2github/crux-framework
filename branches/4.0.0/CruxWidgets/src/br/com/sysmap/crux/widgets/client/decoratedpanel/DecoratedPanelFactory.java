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
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.HTMLTag;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;

/**
 * Factory for Decorated Panel widget
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="decoratedPanel", library="widgets")
public class DecoratedPanelFactory extends AbstractDecoratedPanelFactory<DecoratedPanel>
{
	@Override
	public DecoratedPanel instantiateWidget(CruxMetaDataElement element, String widgetId) throws InterfaceConfigException
	{
		String height = element.getProperty("height");
		String width = element.getProperty("width");
		String styleName = element.getProperty("styleName");
		return new DecoratedPanel(width, height, styleName);
	}

	@Override
	@TagChildren({
		@TagChild(ChildrenProcessor.class)
	})
	public void processChildren(WidgetFactoryContext<DecoratedPanel> context) throws InterfaceConfigException {}
	
	public static class ChildrenProcessor extends ChoiceChildProcessor<DecoratedPanel>
	{
		@Override
		@TagChildren({
			@TagChild(HTMLChildProcessor.class),
			@TagChild(TextChildProcessor.class),
			@TagChild(WidgetProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<DecoratedPanel> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="html", type=HTMLTag.class)
	public static class HTMLChildProcessor extends WidgetChildProcessor<DecoratedPanel>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<DecoratedPanel> context) throws InterfaceConfigException
		{
			context.getRootWidget().setContentHtml(ensureHtmlChild(context.getChildElement(), true));
		}
	}

	@TagChildAttributes(tagName="text", type=String.class)
	public static class TextChildProcessor extends WidgetChildProcessor<DecoratedPanel>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<DecoratedPanel> context) throws InterfaceConfigException
		{
			context.getRootWidget().setContentText(ensureTextChild(context.getChildElement(), true));
		}
	}
	
	@TagChildAttributes(tagName="widget")
	public static class WidgetProcessor extends WidgetChildProcessor<DecoratedPanel>
	{
		@Override
		@TagChildren({
			@TagChild(WidgetContentProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<DecoratedPanel> context) throws InterfaceConfigException {}
	}

	@TagChildAttributes(widgetProperty="contentWidget")
	public static class WidgetContentProcessor extends AnyWidgetChildProcessor<DecoratedPanel> {}

}