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
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;

import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Controller("slideshowPhotoPanelKeyboardController")
public class SlideshowPhotoPanelKeyboardController extends DeviceAdaptiveController implements SlideshowPhotoPanel
{
	SlideshowSwapPhotoPanel photoPanel;
	
	@Override
    public void onAlbumLoaded()
    {
		photoPanel.onAlbumLoaded();
    }

	@Override
    public void onPhotoLoaded(int previousIndex, int nextIndex)
    {
		photoPanel.onPhotoLoaded(previousIndex, nextIndex);
    }

	@Override
    public void onStartPlaying()
    {
		photoPanel.onStartPlaying();
	    
    }

	@Override
    public void onStopPlaying()
    {
		photoPanel.onStopPlaying();
    }

	@Override
    public void setSlideShow(Slideshow slideshow)
    {
		photoPanel.setSlideShow(slideshow);
    }

	@Override
    public Slideshow getSlideshow()
    {
	    return photoPanel.getSlideshow();
    }

	@Override
	protected void init()
	{
		SimplePanel mainPanel = getChildWidget("mainPanel");
		photoPanel = new SlideshowSwapPhotoPanel();
		mainPanel.add(photoPanel);
		setHeight("100%");
		setWidth("100%");
		super.init();
	}
	
	@Override
    protected void initWidgetDefaultStyleName()
    {
		setStyleName("xdev-SlideshowPhotoPanel");
    }

	@Override
    protected void applyWidgetDependentStyleNames()
    {
	    // TODO Auto-generated method stub
	    
    }


}
