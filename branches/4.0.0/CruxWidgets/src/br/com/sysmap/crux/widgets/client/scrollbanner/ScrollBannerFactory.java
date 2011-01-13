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
package br.com.sysmap.crux.widgets.client.scrollbanner;

import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;

/**
 * Factory for Scroll Banner widget
 * @author Gesse S. F. Dafe
 */
@br.com.sysmap.crux.core.client.declarative.DeclarativeFactory(id="scrollBanner", library="widgets")
public class ScrollBannerFactory extends WidgetCreator<ScrollBanner, WidgetCreatorContext>
{
	@Override
	public ScrollBanner instantiateWidget(CruxMetaDataElement element, String widgetId) throws InterfaceConfigException
	{
		String period = element.getProperty("messageScrollingPeriod");
		if(period != null && period.trim().length() > 0)
		{
			return new ScrollBanner(Integer.parseInt(period));
		}
		
		return new ScrollBanner();
	}
	
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("messageScrollingPeriod")
	})
	public void processAttributes(WidgetCreatorContext context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	@Override
	@TagChildren({
		@TagChild(MessageProcessor.class)
	})
	public void processChildren(WidgetCreatorContext context) throws InterfaceConfigException {}
	
	@TagChildAttributes(tagName="message", minOccurs="0", maxOccurs="unbounded", type=String.class)
	public static class MessageProcessor extends WidgetChildProcessor<ScrollBanner, WidgetCreatorContext>
	{
		@Override
		public void processChildren(WidgetCreatorContext context) throws InterfaceConfigException
		{
			String message = ScreenFactory.getInstance().getDeclaredMessage(ensureTextChild(context.getChildElement(), true));
			ScrollBanner rootWidget = context.getWidget();
			rootWidget.addMessage(message);
		}
	}
}