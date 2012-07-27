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

import org.cruxframework.crux.crossdevice.client.slider.TouchSlider;
import org.cruxframework.crux.crossdevice.client.slider.TouchSlider.ContentProvider;
import org.cruxframework.crux.widgets.client.event.swap.SwapEvent;
import org.cruxframework.crux.widgets.client.event.swap.SwapHandler;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class SlideshowTouchPhotoPanel extends SlideshowBaseComponent
{
	private TouchSlider photosPanel ;
	
	@Override
    public void onAlbumLoaded()
    {
		photosPanel.setContentProvider(new ContentProvider()
		{
			
			@Override
			public int size()
			{
				return getSlideshow().getPhotoCount();
			}
			
			@Override
			public Widget loadWidget(int index)
			{
				return getSlideshow().loadImage(index);
			}
		});
    }

	@Override
    public void onPhotoLoaded(int previousIndex, int nextIndex)
    {
		assert(this.getSlideshow() != null):"Slideshow is not initialized. Set component's slideshow property first.";
		
		if (nextIndex == previousIndex + 1)
		{
			photosPanel.next();
		}
		else if (nextIndex == previousIndex - 1)
		{
			photosPanel.previous();
		}
		else if (photosPanel.getCurrentWidget() != nextIndex)
		{
			photosPanel.showWidget(nextIndex);
		}
    }

	@Override
    protected Widget createMainWidget()
    {
		photosPanel = new TouchSlider();
		photosPanel.setWidth("100%");
		photosPanel.setHeight("100%");
		
		photosPanel.addSwapHandler(new SwapHandler()
		{
			@Override
			public void onSwap(SwapEvent event)
			{
				getSlideshow().showPhoto(photosPanel.getCurrentWidget());
			}
		});
		return photosPanel;
    }

}
