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
package br.com.sysmap.crux.widgets.rebind.maskedlabel;

import br.com.sysmap.crux.core.client.formatter.Formatter;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.formatter.Formatters;
import br.com.sysmap.crux.core.rebind.screen.widget.AttributeProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasAllMouseHandlersFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasAutoHorizontalAlignmentFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasClickHandlersFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasDirectionFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasHorizontalAlignmentFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasWordWrapFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.widgets.client.maskedlabel.MaskedLabel;
import br.com.sysmap.crux.widgets.rebind.WidgetGeneratorMessages;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="maskedLabel", library="widgets", targetWidget=MaskedLabel.class)
@TagAttributes({
	@TagAttribute(value="text", processor=MaskedLabelFactory.TextAttributeParser.class)
})
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="formatter", required=true)
})
public class MaskedLabelFactory extends WidgetCreator<WidgetCreatorContext> 
				implements HasDirectionFactory<WidgetCreatorContext>, HasClickHandlersFactory<WidgetCreatorContext>, 
						   HasAllMouseHandlersFactory<WidgetCreatorContext>, 
				           HasWordWrapFactory<WidgetCreatorContext>, 
				           HasAutoHorizontalAlignmentFactory<WidgetCreatorContext>, 
				           HasHorizontalAlignmentFactory<WidgetCreatorContext>
{
	protected static WidgetGeneratorMessages widgetMessages = (WidgetGeneratorMessages)MessagesFactory.getMessages(WidgetGeneratorMessages.class);

	/**
	 * @param metaElem
	 * @param widgetId
	 * @return
	 * @throws CruxGeneratorException
	 */
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();

		String formatter = context.readWidgetProperty("formatter");
		if (formatter != null && formatter.length() > 0)
		{
			String fmt = createVariableName("fmt");

			out.println(Formatter.class.getCanonicalName()+" "+fmt+" = "+Formatters.getFormatterInstantionCommand(formatter)+";");
			out.println("assert ("+fmt+" != null):"+EscapeUtils.quote(widgetMessages.maskedLabelFormatterNotFound(formatter))+";");
			out.println(className + " " + context.getWidget()+" = new "+className+"("+fmt+");");
		}	
		else
		{
			throw new CruxGeneratorException(widgetMessages.maskedLabelFormatterRequired());	
		}
	}	
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class TextAttributeParser extends AttributeProcessor<WidgetCreatorContext>
	{
		public TextAttributeParser(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String propertyValue)
        {
			String widget = context.getWidget();
			out.println(widget+".setUnformattedValue("+widget+".getFormatter().unformat("+EscapeUtils.quote(propertyValue)+"));");
        }
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
