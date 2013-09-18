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
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;
import org.cruxframework.crux.widgets.client.button.Button;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;
import org.cruxframework.crux.widgets.client.simplecontainer.SimpleViewContainer;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel.Direction;

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
			}
		});
		
		menuPanel.add(menuItem);
	}

	@Override
	protected void init()
	{
		viewContainer = new SimpleViewContainer();
		swapPanel = getChildWidget("swapPanel");
		menuPanel = getChildWidget("menuPanel");
		setStyleName("crux-TopMenuDisposal");
	}

	@Override
	public void showMenu()
	{
		swapPanel.transitTo(menuPanel, Direction.BACKWARDS);
	}
}
