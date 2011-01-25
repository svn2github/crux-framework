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
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.HTMLTag;

/**
 * Factory for CaptionPanel widgets
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="captionPanel", library="gwt")
public class CaptionPanelFactory extends CompositeFactory<WidgetCreatorContext>
{
	@Override
	@TagAttributes({
		@TagAttribute("captionText")
	})
	public void processAttributes(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
	}
	
	@Override
	@TagChildren({
		@TagChild(CaptionProcessor.class),
		@TagChild(ContentProcessor.class)
	})	
	public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException {}
	
	@TagChildAttributes(minOccurs="0")
	public static class CaptionProcessor extends ChoiceChildProcessor<WidgetCreatorContext>
	{
		@Override
		@TagChildren({
			@TagChild(CaptionTextProcessor.class),
			@TagChild(CaptionHTMLProcessor.class)
		})	
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException {}
	}
	
	@TagChildAttributes(minOccurs="0", tagName="widget")
	public static class ContentProcessor extends WidgetChildProcessor<WidgetCreatorContext> 
	{
		@Override
		@TagChildren({
			@TagChild(WidgetProcessor.class)
		})	
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException {}
	}

	@TagChildAttributes(minOccurs="0", widgetProperty="contentWidget")
	public static class WidgetProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagChildAttributes(tagName="captionText", type=String.class)
	public static class CaptionTextProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException 
		{
			String title = getWidgetCreator().getDeclaredMessage(WidgetCreator.ensureTextChild(context.getChildElement(), false));
			out.println(context.getWidget()+".setCaptionText("+EscapeUtils.quote(title)+");");
		}
	}
	
	@TagChildAttributes(tagName="captionHTML", type=HTMLTag.class)
	public static class CaptionHTMLProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException 
		{
			String title = WidgetCreator.ensureHtmlChild(context.getChildElement(), false);
			out.println(context.getWidget()+".setCaptionHTML("+EscapeUtils.quote(title)+");");
		}
	}
}
