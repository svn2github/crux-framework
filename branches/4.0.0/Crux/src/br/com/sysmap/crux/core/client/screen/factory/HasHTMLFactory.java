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
package br.com.sysmap.crux.core.client.screen.factory;

import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.screen.AttributeParser;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.WidgetFactoryContext;

import com.google.gwt.user.client.ui.HasHTML;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface HasHTMLFactory<T extends HasHTML, C extends WidgetFactoryContext> extends HasTextFactory<T, C>
{
	@TagAttributes({
		@TagAttribute(value="_html", supportsI18N=true, xsdIgnore=true, parser=HTMLParser.class)
	})	
	void processAttributes(C context) throws InterfaceConfigException;
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	class HTMLParser implements AttributeParser<WidgetFactoryContext>
	{
		public void processAttribute(WidgetFactoryContext context, String propertyValue) 
		{
			HasHTML widget = (HasHTML)context.getWidget();
			String text = context.readWidgetProperty("text");
			if (text == null || text.length() ==0)
			{
				widget.setHTML(propertyValue);
			}
		}
	}
}
