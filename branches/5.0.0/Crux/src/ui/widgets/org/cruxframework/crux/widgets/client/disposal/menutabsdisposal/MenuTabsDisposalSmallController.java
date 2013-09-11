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

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;
import org.cruxframework.crux.widgets.client.button.Button;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;
import org.cruxframework.crux.widgets.client.swapcontainer.HorizontalSwapContainer;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel.Direction;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Gesse Dafe
 */
@Controller("menuTabsDisposalSmallController")
public class MenuTabsDisposalSmallController extends DeviceAdaptiveController implements MenuTabsDisposal
{
	private FlowPanel menuPanel;
	private HorizontalSwapContainer viewContainer;
	
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
				viewContainer.showView(targetView, Direction.FORWARD);
			}
		});
		
		menuPanel.add(menuItem);
	}

	@Override
	protected void init()
	{
		viewContainer = getChildWidget("viewContainer");
		viewContainer.showView("small-menu");
		menuPanel = (FlowPanel) viewContainer.getActiveView().getWidget("menuPanel");
		setStyleName("crux-MenuTabsDisposal");
	}

	@Override
	public void showMenu()
	{
		viewContainer.showView("small-menu", Direction.BACKWARDS);
	}

	@Override
	public void addMenuSection(String label)
	{
		Label separator = new Label();
		separator.setStyleName("menuSection");
		separator.getElement().getStyle().setDisplay(Display.BLOCK);
		separator.setText(label);
		menuPanel.add(separator);
	}
}
