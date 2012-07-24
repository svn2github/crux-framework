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
import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.utils.StyleUtils;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.FocusPanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Controller("slideshowKeyboardController")
public class SlideshowKeyboardController extends SlideshowBaseController
{
	@Override
	protected void init()
	{
	    super.init();
	    FocusPanel focusPanel = getChildWidget("focusPanel");
		focusPanel.addKeyDownHandler(new KeyDownHandler()
		{
			@Override
			public void onKeyDown(KeyDownEvent event)
			{
				switch(event.getNativeKeyCode())
				{
					case KeyCodes.KEY_UP: 
					case KeyCodes.KEY_LEFT:
						stop();
						previous();
					break;
					case KeyCodes.KEY_DOWN: 
					case KeyCodes.KEY_RIGHT:
						stop();
						next();
					case 32://SPACE
						if (isPlaying())
						{
							stop();
						}
						else
						{
							play();
						}
					break;
				}
			}
		});
	}
	
	@Override
    protected void applyWidgetDependentStyleNames()
    {
		//TODO melhorar isso...da pra automatizar no rebind..... ta dando muito trabalho
		StyleUtils.addStyleDependentName(getElement(), DeviceAdaptive.Input.arrows.toString());
    }

}
