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
package br.com.sysmap.crux.widgets.client.maskedlabel;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.formatter.Formatter;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.rebind.widget.AttributeProcessor;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.HasAllMouseHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasAutoHorizontalAlignmentFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasClickHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasDirectionFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasHorizontalAlignmentFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasWordWrapFactory;
import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="maskedLabel", library="widgets")
public class MaskedLabelFactory extends WidgetCreator<WidgetCreatorContext> 
				implements HasDirectionFactory<WidgetCreatorContext>, HasClickHandlersFactory<WidgetCreatorContext>, 
						   HasAllMouseHandlersFactory<WidgetCreatorContext>, 
				           HasWordWrapFactory<WidgetCreatorContext>, 
				           HasAutoHorizontalAlignmentFactory<WidgetCreatorContext>, 
				           HasHorizontalAlignmentFactory<WidgetCreatorContext>
{
	@Override
	public MaskedLabel instantiateWidget(CruxMetaDataElement element, String widgetId) throws InterfaceConfigException
	{
		String formatter = element.getProperty("formatter");
		if (formatter != null && formatter.length() > 0)
		{
			Formatter fmt = Screen.getFormatter(formatter);
			if (fmt == null)
			{
				throw new InterfaceConfigException(WidgetMsgFactory.getMessages().maskedLabelFormatterNotFound(formatter));
			}
			return new MaskedLabel(fmt);
		}
		throw new InterfaceConfigException(WidgetMsgFactory.getMessages().maskedLabelFormatterRequired());	
	}

	@Override
	@TagAttributes({
		@TagAttribute(value="text", processor=TextAttributeParser.class)
	})
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="formatter", required=true)
	})
	public void processAttributes(WidgetCreatorContext context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class TextAttributeParser implements AttributeProcessor<WidgetCreatorContext>
	{
		public void processAttribute(WidgetCreatorContext context, String propertyValue)
        {
			MaskedLabel widget = context.getWidget();
			widget.setUnformattedValue(widget.getFormatter().unformat(propertyValue));
        }
	}
}
