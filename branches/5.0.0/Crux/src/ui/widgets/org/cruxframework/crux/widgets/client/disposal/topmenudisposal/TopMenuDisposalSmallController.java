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
package org.cruxframework.crux.widgets.client.disposal.topmenudisposal;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;
import org.cruxframework.crux.widgets.client.button.Button;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;
import org.cruxframework.crux.widgets.client.simplecontainer.SimpleViewContainer;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel.Direction;

import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author Gesse Dafe
 */
@Controller("topMenuDisposalSmallController")
public class TopMenuDisposalSmallController extends DeviceAdaptiveController implements TopMenuDisposal
{
	private HorizontalSwapPanel swapPanel;
	private FlowPanel menuPanel;
	private SimpleViewContainer viewContainer;
	private String lastVisitedView = null;
	
	@Override
	public void addMenuEntry(String label, final String targetView)
	{
		Button menuItem = new Button();
		menuItem.addStyleName("menuEntry");
		menuItem.setText(label);
		menuItem.addSelectHandler(new SelectHandler()
		{
			@Override
			public void onSelect(SelectEvent event)
			{
				viewContainer.showView(targetView);
				swapPanel.transitTo(viewContainer, Direction.FORWARD);
				Window.scrollTo(0, 0);
			}
		});
		
		menuPanel.add(menuItem);
	}

	@Override
	protected void init()
	{
		viewContainer = getChildWidget("viewContainer");
		swapPanel = getChildWidget("swapPanel");
		menuPanel = getChildWidget("menuPanel");
		menuPanel.removeFromParent();
		setStyleName("crux-TopMenuDisposal");
	}

	@Expose
	public void onShowMenu(TouchStartEvent evt)
	{
		evt.preventDefault();
		evt.stopPropagation();
		showMenu();
	}

	@Override
	public void showMenu() 
	{
		if(swapPanel.getCurrentWidget().equals(menuPanel) && lastVisitedView != null)
		{
			showView(lastVisitedView);
		}
		else
		{
			swapPanel.transitTo(menuPanel, Direction.BACKWARDS);
		}
	}

	@Override
	public void showView(String viewName) 
	{
		viewContainer.showView(viewName);
		swapPanel.transitTo(viewContainer, Direction.FORWARD);
		lastVisitedView = viewName;
		Window.scrollTo(0, 0);
	}

	@Override
	public void setDefaultView(String viewName) 
	{
		if(lastVisitedView == null)
		{
			viewContainer.showView(viewName);
			lastVisitedView = viewName;
			Window.scrollTo(0, 0);
		}
	}
}
