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
package org.cruxframework.cruxsite.client.widget;

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.screen.views.ViewFactory.CreateCallback;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.crossdevice.client.event.SelectEvent;
import org.cruxframework.crux.crossdevice.client.event.SelectHandler;
import org.cruxframework.crux.widgets.client.swapcontainer.HorizontalSwapContainer;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel.Direction;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Controller("siteFaceSmallController")
public class SiteFaceSmallController extends DeviceAdaptiveController implements SiteFace
{
	private FlowPanel menu;
	private HorizontalSwapContainer viewContainer;
	private int currentView = -1;
	private static FastList<String> availableViews;
	
	@Override
	public void addMenuEntry(String label, String crwalerUrl, String tooltip, final SelectHandler selectHandler)
	{
		if (menu.getWidgetCount() > 1)
		{
			Label separator = new Label();
			separator.setStyleName("MenuSeparator");
			menu.add(separator);
		}
		
		Anchor menuItem = new Anchor();
		menuItem.setText(label);
		menuItem.setStyleName("MenuItem");
		menuItem.setHref(crwalerUrl);
		if (!StringUtils.isEmpty(tooltip))
		{
			menuItem.setTitle(tooltip);
		}
		if (selectHandler != null)
		{
			menuItem.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					selectHandler.onSelect(new SelectEvent());
					event.preventDefault();
				}
			});
		}
		menu.add(menuItem);
	}

	@Override
    public void showView(String viewName, final String viewId)
    {
		final int viewOrder = getViewOrder(viewId);
		final Direction direction = (currentView < viewOrder)?Direction.FORWARD:Direction.BACKWARDS; 
		if (viewContainer.getView(viewId) != null)
		{
			viewContainer.showView(viewId, direction);
			currentView = viewOrder;
		}
		else
		{
			HorizontalSwapContainer.createView(viewName, viewId, new CreateCallback()
			{
				@Override
				public void onViewCreated(View view)
				{
					viewContainer.add(view);
					viewContainer.showView(viewId, direction);
					currentView = viewOrder;
				}
			});
		}
    }

	/**
	 * 
	 * @param viewName
	 * @return
	 */
	protected int getViewOrder(String viewName)
	{
		return getAvailableViews().indexOf(viewName); 
	}

	/**
	 * 
	 * @return
	 */
	protected FastList<String> getAvailableViews()
	{
		if (availableViews == null)
		{
			availableViews = new FastList<String>();
			availableViews.add("home");
			availableViews.add("download");
			availableViews.add("learn");
			availableViews.add("community");
		}
		return availableViews;
	}
	
	@Override
	protected void init()
	{
		menu = getChildWidget("menu");
		viewContainer = getChildWidget("viewContainer");
	}

	@Override
	protected void initWidgetDefaultStyleName()
	{
		setStyleName("site-SiteFace");
	}
}
