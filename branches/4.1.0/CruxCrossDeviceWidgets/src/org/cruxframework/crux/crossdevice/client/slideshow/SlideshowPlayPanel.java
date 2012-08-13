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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SlideshowPlayPanel extends SlideshowComponent
{
	private SlideshowPlayPanelImpl impl;
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static class SlideshowPlayPanelImpl extends SlideshowComponent
	{
		protected Label play;
		
		public SlideshowPlayPanelImpl()
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
		    return play;
	    }
	}
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static class SlideshowPlayPanelNoTouchImpl extends SlideshowPlayPanelImpl
	{
		@Override
		protected Widget createMainWidget()
		{
		    super.createMainWidget();
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
	
	static class SlideshowPlayPanelTouchImpl extends SlideshowPlayPanelImpl
	{
		@Override
		protected Widget createMainWidget()
		{
		    super.createMainWidget();
			play.addTouchStartHandler(new TouchStartHandler()
			{
				
				@Override
				public void onTouchStart(TouchStartEvent event)
				{
					event.preventDefault();
				}
			});
			play.addTouchMoveHandler(new TouchMoveHandler()
			{
				
				@Override
				public void onTouchMove(TouchMoveEvent event)
				{
					event.preventDefault();
				}
			});
			play.addTouchEndHandler(new TouchEndHandler()
			{
				
				@Override
				public void onTouchEnd(TouchEndEvent event)
				{
					event.preventDefault();
					event.stopPropagation();
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
	
	@Override
	protected Widget createMainWidget()
	{
		impl = GWT.create(SlideshowPlayPanelImpl.class);
	    return impl;
	}
	
	@Override
	public void setSlideShow(Slideshow slideshow)
	{
	    super.setSlideShow(slideshow);
	    impl.setSlideShow(slideshow);
	}
	
	@Override
	protected void onStartPlaying()
	{
		impl.onStartPlaying();
	}
	
	@Override
	protected void onStopPlaying()
	{
		impl.onStopPlaying();
	}
	
	@Override
	protected void onPhotoLoaded(int previousIndex, int nextIndex)
	{
		impl.onPhotoLoaded(previousIndex, nextIndex);
	}
}
