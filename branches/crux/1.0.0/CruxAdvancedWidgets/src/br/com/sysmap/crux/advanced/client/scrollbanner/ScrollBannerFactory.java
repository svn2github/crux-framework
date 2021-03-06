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
package br.com.sysmap.crux.advanced.client.scrollbanner;

import java.util.List;

import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;

import com.google.gwt.dom.client.Element;

/**
 * Factory for Scroll Banner widget
 * @author Gess� S. F. Daf� - <code>gessedafe@gmail.com</code>
 */
public class ScrollBannerFactory extends WidgetFactory<ScrollBanner>
{
	@Override
	protected ScrollBanner instantiateWidget(Element element, String widgetId) throws InterfaceConfigException
	{
		String period = element.getAttribute("_messageScrollingPeriod");
		if(period != null && period.trim().length() > 0)
		{
			return new ScrollBanner(Integer.parseInt(period));
		}
		
		return new ScrollBanner();
	}
	
	@Override
	protected void processAttributes(ScrollBanner widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);
		
		List<Element> children = ensureChildrenSpans(element, true);
		for (Element child : children)
		{
			String message = child.getInnerText();
			widget.addMessage(message);
		}		
	}
}