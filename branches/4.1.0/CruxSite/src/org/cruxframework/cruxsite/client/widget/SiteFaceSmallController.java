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
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.screen.views.ViewFactory.CreateCallback;
import org.cruxframework.crux.core.client.utils.StyleUtils;
import org.cruxframework.crux.crossdevice.client.button.Button;
import org.cruxframework.crux.crossdevice.client.event.SelectEvent;
import org.cruxframework.crux.crossdevice.client.event.SelectHandler;
import org.cruxframework.crux.widgets.client.swapcontainer.HorizontalSwapContainer;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel.Direction;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

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
	private FlowPanel header;
	private static FastList<String> availableViews;
	private boolean menuOpened = false;
	
	@Expose
	public void openOrCloseMenu()
	{
		menuOpened = !menuOpened;
		if (menuOpened)
		{
			StyleUtils.addStyleDependentName(header.getElement(), "opened");
			StyleUtils.addStyleDependentName(menu.getElement(), "opened");
			StyleUtils.addStyleDependentName(viewContainer.getElement(), "opened");
		}
		else
		{
			StyleUtils.removeStyleDependentName(header.getElement(), "opened");
			StyleUtils.removeStyleDependentName(menu.getElement(), "opened");
			StyleUtils.removeStyleDependentName(viewContainer.getElement(), "opened");
		}
	}
	
	@Override
	public void addMenuEntry(String label, final String url, String tooltip, final SelectHandler selectHandler)
	{
		Button menuItem = new Button();
		menuItem.setPreventDefaultTouchEvents(true);
		menuItem.setText(label);
		menuItem.setStyleName("MenuItem");
		menuItem.addSelectHandler(new SelectHandler()
		{
			@Override
			public void onSelect(SelectEvent event)
			{
				if (selectHandler != null)
				{
					selectHandler.onSelect(event);
					openOrCloseMenu();
				}
				else
				{
					Window.open(url, "_blank", null);
				}
			}
		});
		menu.add(menuItem);
	}

	@Override
	public void showView(String viewName, final String viewId)
	{
		showView(viewName, viewId, false);
	}
	
	@Override
    public void showView(String viewName, final String viewId, boolean animated)
    {
		final int viewOrder = getViewOrder(viewId);
		final Direction direction = (animated?((currentView < viewOrder)?Direction.FORWARD:Direction.BACKWARDS):null); 
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
		header = getChildWidget("header");
	}

	@Override
	protected void initWidgetDefaultStyleName()
	{
		setStyleName("site-SiteFace");
	}
}
