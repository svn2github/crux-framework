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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SlideshowPlayPanel extends SlideshowBaseComponent
{
	private Label play;
	
	public SlideshowPlayPanel()
    {
		super();
		setStyleName("xdev-SlideshowPlayPanel");
    }

	@Override
	public void onStartPlaying()
	{
		play.addStyleDependentName("playing");
	}

	@Override
	public void onStopPlaying()
	{
		play.removeStyleDependentName("playing");
	}

	@Override
	public void onPhotoLoaded(int previousIndex, int nextIndex)
	{
		int lastIndex = getSlideshow().getPhotoCount() -1;
		if (previousIndex == lastIndex && nextIndex < lastIndex)
		{
			play.removeStyleDependentName("disabled");
		}
		else if (nextIndex == lastIndex)
		{
			play.addStyleDependentName("disabled");
		}
	}
	
	
	@Override
    protected Widget createMainWidget()
    {
		play = new Label();
		play.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				if (getSlideshow().isPlaying())
				{
					getSlideshow().stop();
				}
				else
				{
					getSlideshow().play();
				}
			}
		});
		
	    return play;
    }
}
