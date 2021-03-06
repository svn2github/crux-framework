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
package br.com.sysmap.crux.basic.client;

import java.util.List;

import br.com.sysmap.crux.core.client.event.bind.BeforeSelectionEvtBind;
import br.com.sysmap.crux.core.client.event.bind.ClickEvtBind;
import br.com.sysmap.crux.core.client.event.bind.KeyEvtBind;
import br.com.sysmap.crux.core.client.event.bind.SelectionEvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenLoadHandler;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.TabBar.Tab;

/**
 * Factory for TabBar widgets
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 */
public class TabBarFactory extends CompositeFactory<TabBar>
{
	@Override
	protected void processAttributes(final TabBar widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);
		
		processTabs(widget, element, widgetId);
		
		final String visibleTab = element.getAttribute("_visibleTab");
		if (visibleTab != null && visibleTab.length() > 0)
		{
			addScreenLoadedHandler(new ScreenLoadHandler()
			{
				public void onLoad(ScreenLoadEvent event)
				{
					widget.selectTab(Integer.parseInt(visibleTab));
				}
			});
		}
	}
	
	@Override
	protected TabBar instantiateWidget(Element element, String widgetId) 
	{
		return new TabBar();
	}

	protected void processTabs(TabBar widget, Element element, String widgetId) throws InterfaceConfigException 
	{
		List<Element> tabs = ensureChildrenSpans(element, true);
		for (Element tabElement : tabs)
		{
			processTab(widget, tabElement, widgetId);
		}
	}		
		
	protected void processTab(TabBar widget, Element element, String widgetId) throws InterfaceConfigException
	{
		String tabText = element.getAttribute("_widgetTitle");
		// tab caption as text
		if (tabText != null && tabText.trim().length() > 0)
		{
			widget.addTab(tabText);
		}
		// tab caption as html
		else
		{
			Element tabTextSpan = ensureFirstChildSpan(element, true); 
			if (tabTextSpan != null && !isWidget(tabTextSpan))
			{
				widget.addTab(tabTextSpan.getInnerHTML(), true);
			}
			else
			{
				Widget titleWidget = createChildWidget(tabTextSpan, tabTextSpan.getId());
				widget.addTab(titleWidget);
			}
		}
		String enabled = element.getAttribute("_enabled");
		int tabCount = widget.getTabCount();
		if (enabled != null && enabled.length() >0)
		{
			widget.setTabEnabled(tabCount-1, Boolean.parseBoolean(enabled));
		}

		Tab currentTab = widget.getTab(tabCount-1);

		String wordWrap = element.getAttribute("_wordWrap");
		if (wordWrap != null && wordWrap.trim().length() > 0)
		{
			currentTab.setWordWrap(Boolean.parseBoolean(wordWrap));
		}

		ClickEvtBind.bindEvent(element, currentTab);
		KeyEvtBind.bindEvents(element, currentTab);
	}
	
	@Override
	protected void processEvents(TabBar widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processEvents(widget, element, widgetId);
		
		BeforeSelectionEvtBind.bindEvent(element, widget);
		SelectionEvtBind.bindEvent(element, widget);
	}
}
