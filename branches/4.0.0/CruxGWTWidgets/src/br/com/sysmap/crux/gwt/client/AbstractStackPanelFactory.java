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
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.AttributeProcessor;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.HTMLTag;

import com.google.gwt.core.client.GWT;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractStackPanelFactory extends ComplexPanelFactory<AbstractStackPanelFactoryContext>
{
	protected GWTMessages messages = GWT.create(GWTMessages.class);
	
	@Override
	@TagAttributes({
		@TagAttribute(value="visibleStack", type=Integer.class, processor=VisibleStackAttributeParser.class)
	})
	public void processAttributes(SourcePrinter out, AbstractStackPanelFactoryContext context) throws CruxGeneratorException 
	{
		super.processAttributes(out, context);
	}
	
	/**
	 * @author Gesse Dafe
	 */
	public static class VisibleStackAttributeParser extends AttributeProcessor<AbstractStackPanelFactoryContext>
	{
		@Override
        public void processAttribute(SourcePrinter out, AbstractStackPanelFactoryContext context, String attributeValue)
        {
			printlnPostProcessing(context.getWidget()+".showStack("+Integer.parseInt(attributeValue)+");");
        }		
	}

	@TagChildAttributes(tagName="textTitle", type=String.class)
	public abstract static class AbstractTitleTextProcessor extends WidgetChildProcessor<AbstractStackPanelFactoryContext>
	{
		@Override
		public void processChildren(SourcePrinter out, AbstractStackPanelFactoryContext context) throws CruxGeneratorException 
		{
			context.title = ensureTextChild(context.getChildElement(), true);
			context.isHtmlTitle = false;
		}
	}
	
	@TagChildAttributes(tagName="htmlTitle", type=HTMLTag.class)
	public abstract static class AbstractTitleHTMLProcessor extends WidgetChildProcessor<AbstractStackPanelFactoryContext>
	{
		@Override
		public void processChildren(SourcePrinter out, AbstractStackPanelFactoryContext context) throws CruxGeneratorException 
		{
			context.title = ensureHtmlChild(context.getChildElement(), true);
			context.isHtmlTitle = true;
		}
	}
	
	@TagChildAttributes(minOccurs="0", type=AnyWidget.class)
	public abstract static class AbstractContentWidgetProcessor extends WidgetChildProcessor<AbstractStackPanelFactoryContext> 
	{
		@Override
		public void processChildren(SourcePrinter out,AbstractStackPanelFactoryContext context) throws CruxGeneratorException 
		{
			String child = getWidgetCreator().createChildWidget(out, context.getChildElement());
			String widget = context.getWidget();
			
			if (context.title == null)
			{
				out.println(widget+".add("+child+");");
			}
			else
			{
				out.println(widget+".add("+child+", "+EscapeUtils.quote(context.title)+", "+context.isHtmlTitle+");");
			}
			context.title = null;
			context.isHtmlTitle = false;
		}	
	}
}


class AbstractStackPanelFactoryContext extends WidgetCreatorContext
{
	boolean isHtmlTitle = false;
	String title;
}