/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.views.client.tabcontainer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.views.client.MultipleViewsContainerWidget;
import org.cruxframework.crux.widgets.client.event.focusblur.BeforeBlurEvent;
import org.cruxframework.crux.widgets.client.event.focusblur.BeforeFocusEvent;
import org.cruxframework.crux.widgets.client.event.openclose.BeforeCloseEvent;
import org.cruxframework.crux.widgets.client.rollingtabs.RollingTabPanel;

import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class TabContainer extends MultipleViewsContainerWidget
{
	public static final String DEFAULT_STYLE_NAME = "view-TabContainer";
	private RollingTabPanel tabPanel;
	private LinkedHashMap<String, Tab> tabs = new LinkedHashMap<String, Tab>();

	public TabContainer()
	{
		super(true);
		tabPanel = new RollingTabPanel();
		tabPanel.setStyleName(DEFAULT_STYLE_NAME);
	}

	/**
	 * Closes the tab, skipping any BeforeCloseHandler registered
	 * @param tabId
	 */
	public void closeTab(String tabId)
	{
		closeTab(tabId, true);
	}

	/**
	 * @param tabId
	 * @param skipBeforeCloseHandlers
	 */
	public void closeTab(final String tabId, final boolean skipBeforeCloseHandlers)
	{
		if(skipBeforeCloseHandlers)
		{
			doCloseTab(tabId);
		}
		else
		{
			Tab tab = getTab(tabId);
			BeforeCloseEvent evt = BeforeCloseEvent.fire(tab);

			if (!evt.isCanceled())
			{
				doCloseTab(tabId);
			}
		}
	}
	
	/**
	 * @param tabId
	 */
	public void focusTab(String tabId)
	{
		this.tabPanel.selectTab(getTabIndex(tabId));
	}

	/**
	 * @return
	 */
	public Tab getFocusedTab()
	{
		int index = tabPanel.getSelectedTab();

		if (index >= 0)
		{
			return (Tab) tabPanel.getWidget(index);
		}

		return null;
	}

	/**
	 * @return
	 */
	public int getFocusedTabIndex()
	{
		return tabPanel.getSelectedTab();
	}	
	
	/**
	 * @param tabId
	 * @return
	 */
	public Tab getTab(String tabId)
	{
		return tabs.get(tabId);
	}

	/**
	 * @param tabId
	 * @return
	 */
	public int getTabIndex(String tabId)
	{
		return tabPanel.getWidgetIndex(getTab(tabId));
	}
	
	/**
	 * @param tab
	 * @return
	 */
	public int getTabIndex(Tab tab)
	{
		return tabPanel.getWidgetIndex(tab);
	}
	
	/**
	 * @return
	 */
	public List<Tab> getTabs()
	{
		List<Tab> result = new ArrayList<Tab>(this.tabs.size());
		for (Tab tab : this.tabs.values())
		{
			result.add(tab);
		}
		return result;
	}
	
	@Override
    public Widget asWidget()
    {
	    return tabPanel;
    }

	@Override
    protected Panel getContainerPanel(View view)
    {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    protected void handleViewTitle(String title, Panel containerPanel)
    {
	    // TODO Auto-generated method stub
	    
    }

	/**
	 * @param tabId
	 */
	private void doCloseTab(String tabId)
	{
		int index = getTabIndex(tabId);
		this.tabPanel.remove(index);
		this.tabs.remove(tabId);
		
		if (this.tabPanel.getWidgetCount() > 0)
		{
			int indexToFocus = index == 0 ? 0 : index - 1;
			this.tabPanel.selectTab(indexToFocus);
		}
	}	
	
	/**
	 * @param tabId
	 */
	private BeforeSelectionHandler<Integer> createBeforeSelectionHandler()
	{	
		return new BeforeSelectionHandler<Integer>()
		{
			public void onBeforeSelection(BeforeSelectionEvent<Integer> event)
			{
				Tab selectedTab = getFocusedTab();
				String tabId = getTabId(event.getItem());

				if (selectedTab == null || !selectedTab.getViewId().equals(tabId))
				{
					boolean canceled = false;

					for (Tab tab : tabs.values())
					{
						if (!tab.getViewId().equals(tabId))
						{
							BeforeBlurEvent evt = BeforeBlurEvent.fire(tab.getFlap());
							canceled = canceled || evt.isCanceled();
						}
						else
						{
							BeforeFocusEvent evt = BeforeFocusEvent.fire(tab);
							canceled = canceled || evt.isCanceled();
						}
					}

					if (canceled)
					{
						event.cancel();
					}
				}
			}			
		};
	}
	
	/**
	 * @param tabIndex
	 * @return
	 */
	private String getTabId(int tabIndex)
	{
		return ((Tab) tabPanel.getWidget(tabIndex)).getViewId();
	}
}
