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

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;
import org.cruxframework.crux.core.client.ioc.Inject;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.crossdevice.client.event.SelectEvent;
import org.cruxframework.crux.crossdevice.client.event.SelectHandler;
import org.cruxframework.crux.widgets.client.swapcontainer.SwapContainer;
import org.cruxframework.cruxsite.client.SiteConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Controller("siteFaceLargeController")
public class SiteFaceLargeController extends DeviceAdaptiveController implements SiteFace
{
	private HorizontalPanel menuBar;
	private SwapContainer viewContainer;
	@Inject
	private SiteConstants constants;

	@Expose
	public void onClickGoBlog()
	{
		Window.open(constants.blogUrl(), "blog", null);
	}
	
	@Expose
	public void onClickGoProject()
	{
		Window.open(constants.projectUrl(), "project", null);
	}
	
	@Override
	public void addMenuEntry(String label, String crwalerUrl, String tooltip, final SelectHandler selectHandler)
	{
		if (menuBar.getWidgetCount() > 1)
		{
			Label separator = new Label();
			separator.setStyleName("MenuSeparator");
			menuBar.add(separator);
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
		menuBar.add(menuItem);
	}


	@Override
    public void showView(String viewName, String viewId)
    {
		viewContainer.showView(viewName, viewId);
    }

	public void setConstants(SiteConstants constants)
    {
    	this.constants = constants;
    }

	@Override
	protected void init()
	{
		menuBar = getChildWidget("menuBar");
		viewContainer = getChildWidget("viewContainer");
	}

	@Override
	protected void initWidgetDefaultStyleName()
	{
		setStyleName("site-SiteFace");
	}
}
