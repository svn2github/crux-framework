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
package org.cruxframework.crux.widgets.client.disposal.menutabsdisposal;

import java.util.HashMap;
import java.util.Map;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.widgets.client.button.Button;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;
import org.cruxframework.crux.widgets.client.tabcontainer.TabContainer;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author Gesse Dafe
 */
@Controller("menuTabsDisposalLargeController")
public class MenuTabsDisposalLargeController extends DeviceAdaptiveController implements MenuTabsDisposal
{
	private static final String HISTORY_PREFIX = "menuTabsDisposal:";

	private FlowPanel menuPanel;
	private TabContainer viewContainer;
	private Map<String, FlowPanel> sections = new HashMap<String, FlowPanel>();
	private FlowPanel lastSectionAdded = null;
	
	@Override
	public void addMenuEntry(final String label, final String targetView)
	{
		Button menuItem = new Button();
		menuItem.addStyleName("menuEntry");
		menuItem.setText(label);
		menuItem.addSelectHandler(new SelectHandler()
		{
			@Override
			public void onSelect(SelectEvent event)
			{
				showView(targetView, true);
			}
		});
		
		if(lastSectionAdded == null)
		{
			menuPanel.add(menuItem);
		}
		else
		{
			lastSectionAdded.add(menuItem);
		}
	}
	
	@Override
	protected void init()
	{
		menuPanel = getChildWidget("menuPanel");
		viewContainer = getChildWidget("viewContainer");
		setStyleName("crux-MenuTabsDisposal");
		
		Screen.addHistoryChangedHandler(new ValueChangeHandler<String>() 
		{
			@Override
			public void onValueChange(ValueChangeEvent<String> event) 
			{
				String token = event.getValue();
				if(token != null && token.startsWith(HISTORY_PREFIX))
				{
					showView(token.replace(HISTORY_PREFIX, ""), false);
				}
			}
		});
	}

	protected void showView(String targetView, boolean saveHistory) 
	{
		if(saveHistory)
		{
			History.newItem(HISTORY_PREFIX + targetView);
		}
		else
		{
			viewContainer.showView(targetView, targetView);
			viewContainer.focusView(targetView);
		}
	}

	@Override
	public void showMenu()
	{
		int index = viewContainer.getFocusedViewIndex();
		if(index >= 0)
		{
			String viewId = viewContainer.getViewId(index);
			viewContainer.closeView(viewId, true);
		}
	}

	@Override
	public void addMenuSection(final String label, String additionalStyleName)
	{
		Button separator = new Button();
		separator.setStyleName("menuSection");
		separator.getElement().getStyle().setDisplay(Display.BLOCK);
		separator.setText(label);
		
		if(!StringUtils.isEmpty(additionalStyleName))
		{
			separator.addStyleName(additionalStyleName);
		}
		
		separator.addSelectHandler(new SelectHandler() 
		{
			@Override
			public void onSelect(SelectEvent event) 
			{
				FlowPanel items = sections.get(label);
				if(items != null)
				{
					if(itemsVisible(items))
					{
						items.addStyleDependentName("closed");
					}
					else
					{
						items.removeStyleDependentName("closed");
					}
				}
			}

			private boolean itemsVisible(FlowPanel items) 
			{
				return !items.getStyleName().contains("-closed");
			}
		});
		
		FlowPanel sectionItems = new FlowPanel();
		sectionItems.setStyleName("menuSectionEntries");
		sectionItems.addStyleDependentName("closed");
		
		sections.put(label, sectionItems);
		lastSectionAdded = sectionItems;
		
		menuPanel.add(separator);
		menuPanel.add(sectionItems);
	}
}
