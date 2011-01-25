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
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.AttributeProcessor;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.HasScrollHandlersFactory;

import com.google.gwt.core.client.GWT;

/**
 * Represents a ScrollPanelFactory
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="scrollPanel", library="gwt")
public class ScrollPanelFactory extends PanelFactory<WidgetCreatorContext> 
       implements HasScrollHandlersFactory<WidgetCreatorContext>
{
	public static enum VerticalScrollPosition{top,bottom};
	public static enum HorizontalScrollPosition{left,right};

	@Override
	@TagAttributes({
		@TagAttribute(value="alwaysShowScrollBars", type=Boolean.class),
		@TagAttribute(value="verticalScrollPosition", type=VerticalScrollPosition.class, processor=VerticalScrollPositionAttributeParser.class),
		@TagAttribute(value="horizontalScrollPosition", type=HorizontalScrollPosition.class, processor=HorizontalScrollPositionAttributeParser.class),
		@TagAttribute(value="ensureVisible", processor=EnsureVisibleAttributeParser.class)
	})
	public void processAttributes(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class VerticalScrollPositionAttributeParser extends AttributeProcessor<WidgetCreatorContext>
	{
		public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String propertyValue) 
		{
			String widget = context.getWidget();
			if (StringUtils.unsafeEquals("top", propertyValue))
			{
				out.println(widget+".scrollToTop();");
			}
			else 
			{
				out.println(widget+".scrollToBottom();");
			}
		}
	}

	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class HorizontalScrollPositionAttributeParser extends AttributeProcessor<WidgetCreatorContext>
	{
		public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String propertyValue) 
		{
			String widget = context.getWidget();
			if (StringUtils.unsafeEquals("left", propertyValue))
			{
				out.println(widget+".scrollToLeft();");
			}
			else
			{
				out.println(widget+".scrollToRight();");
			}
		}
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class EnsureVisibleAttributeParser extends AttributeProcessor<WidgetCreatorContext>
	{
		protected GWTMessages messages = GWT.create(GWTMessages.class);
		public void processAttribute(SourcePrinter out, final WidgetCreatorContext context, final String propertyValue) 
		{
			String widget = context.getWidget();
					
			String targetWidget = ViewFactoryCreator.createVariableName("c");
			
			printlnPostProcessing("Widget "+targetWidget+" = Screen.get("+EscapeUtils.quote(propertyValue)+");");
			printlnPostProcessing("if ("+targetWidget+" == null){");
			String widgetId = context.getWidgetId();
			printlnPostProcessing("throw new NullPointerException("+EscapeUtils.quote(messages.scrollPanelWidgetNotFound(widgetId, propertyValue))+");");
			printlnPostProcessing("}");
			printlnPostProcessing(widget+".ensureVisible("+targetWidget+");");
		}
	}	
}
