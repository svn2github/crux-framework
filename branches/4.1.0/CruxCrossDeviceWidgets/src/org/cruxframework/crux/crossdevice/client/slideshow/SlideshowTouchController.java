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
package org.cruxframework.crux.crossdevice.client.slideshow;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;

import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Controller("slideshowTouchController")
public class SlideshowTouchController extends SlideshowBaseController
{
	private DialogBox dialog;
	private SimplePanel mainPanel;

	@Expose
	public void onTouchEndDialog()
	{
		if (dialog.isShowing())
		{
			dialog.hide();
		}
	}

	@Override
	protected void init()
	{
	    super.init();
	    dialog = getChildWidget("dialog");
	    dialog.setGlassEnabled(true);
	    mainPanel = getChildWidget("mainPanel");
	}

	@Override
    public void configurePhotoPanel()
    {
		mainPanel.add(photoPanel);
		mainPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
    }
	
	@Override
	protected void showComponents()
	{
	    dialog.show();
	}
}
