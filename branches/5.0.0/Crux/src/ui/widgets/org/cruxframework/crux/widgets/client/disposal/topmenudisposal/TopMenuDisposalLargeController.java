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
import org.cruxframework.crux.widgets.client.simplecontainer.SimpleViewContainer;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * @author Gesse Dafe
 */
@Controller("topMenuDisposalLargeController")
public class TopMenuDisposalLargeController extends DeviceAdaptiveController implements TopMenuDisposal
{
	private MenuBar menuBar;
	private SimpleViewContainer viewContainer;
	
	@Override
	public void addMenuEntry(final String label, final String targetView)
	{
		MenuItem menuItem = new MenuItem(new SafeHtmlBuilder().appendEscaped(label).toSafeHtml());
		menuItem.addStyleName("menuEntry");
		menuItem.setScheduledCommand(new ScheduledCommand() 
		{
			@Override
			public void execute() {
				viewContainer.showView(targetView, targetView);
				Window.scrollTo(0, 0);
			}
		});
		menuBar.addItem(menuItem);
	}
	
	@Override
	protected void init()
	{
		menuBar = getChildWidget("menuBar");
		viewContainer = getChildWidget("viewContainer");
		setStyleName("crux-TopMenuDisposal");
	}

	@Override
	public void showMenu()
	{
		// nothing to do
	}

	@Override
	public void showView(String viewName) 
	{
		viewContainer.showView(viewName);
		Window.scrollTo(0, 0);
	}

	@Override
	public void setDefaultView(String viewName) 
	{
		viewContainer.showView(viewName);
		Window.scrollTo(0, 0);
	}
}
