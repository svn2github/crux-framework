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
package br.com.sysmap.crux.widgets.client.maskedtextbox;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.formatter.Formatter;
import br.com.sysmap.crux.core.client.screen.AttributeParser;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.WidgetFactoryContext;
import br.com.sysmap.crux.core.client.screen.factory.HasAllFocusHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasAllKeyHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasAllMouseHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasChangeHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasClickHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasDirectionFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasDoubleClickHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasNameFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasValueChangeHandlersFactory;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="maskedTextBox", library="widgets")
public class MaskedTextBoxFactory extends WidgetFactory<MaskedTextBox, WidgetFactoryContext> 
       implements HasDirectionFactory<MaskedTextBox, WidgetFactoryContext>, HasNameFactory<MaskedTextBox, WidgetFactoryContext>, 
                  HasChangeHandlersFactory<MaskedTextBox, WidgetFactoryContext>, HasValueChangeHandlersFactory<MaskedTextBox, WidgetFactoryContext>,
                  HasClickHandlersFactory<MaskedTextBox, WidgetFactoryContext>, HasAllFocusHandlersFactory<MaskedTextBox, WidgetFactoryContext>,
                  HasAllKeyHandlersFactory<MaskedTextBox, WidgetFactoryContext>, HasAllMouseHandlersFactory<MaskedTextBox, WidgetFactoryContext>, 
                  HasDoubleClickHandlersFactory<MaskedTextBox, WidgetFactoryContext>
{
	@Override
	public MaskedTextBox instantiateWidget(CruxMetaDataElement element, String widgetId) throws InterfaceConfigException
	{
		String formatter = element.getProperty("formatter");
		if (formatter != null && formatter.length() > 0)
		{
			Formatter fmt = Screen.getFormatter(formatter);
			if (fmt == null)
			{
				throw new InterfaceConfigException(WidgetMsgFactory.getMessages().maskedTextBoxFormatterNotFound(formatter));
			}
			return new MaskedTextBox(fmt);
		}
		throw new InterfaceConfigException(WidgetMsgFactory.getMessages().maskedTextBoxFormatterRequired());
	}

	@Override
	@TagAttributes({
		@TagAttribute(value="readOnly", type=Boolean.class),
		@TagAttribute(value="tabIndex", type=Integer.class),
		@TagAttribute(value="maxLength", type=Integer.class),
		@TagAttribute(value="accessKey", type=Character.class),
		@TagAttribute(value="focus", type=Boolean.class),
		@TagAttribute(value="value", parser=ValueAttributeParser.class)
	})
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="formatter", required=true)
	})
	public void processAttributes(WidgetFactoryContext context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class ValueAttributeParser implements AttributeParser<WidgetFactoryContext>
	{
		public void processAttribute(WidgetFactoryContext context, String propertyValue)
        {
			MaskedTextBox widget = context.getWidget();
			widget.setUnformattedValue(widget.getFormatter().unformat(propertyValue));
        }
	}
}
