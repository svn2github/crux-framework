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

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;
import org.cruxframework.crux.crossdevice.client.slideshow.data.Photo;
import org.cruxframework.crux.crossdevice.client.slideshow.data.PhotoAlbum;

import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.DockPanel.DockLayoutConstant;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class SlideshowBaseController extends DeviceAdaptiveController implements Slideshow
{
	protected static enum SlideshowEvent{AlbumLoaded, PhotoLoaded, StartPlaying, StopPlaying}

	private PhotoAlbum album;
	private DockPanel table;
	private FastMap<Image> imagesCache;
	private FastList<SlideshowComponent> components;
	private int activeImage = -1;
	private int previousImage = -1;
	private Timer autoPlayTimer;
	private boolean preloadNextImages = true;
	private int transitionDelay = 5000;
	
	/**
	 * 
	 * @param layout
	 */
	public void setLayout(Layout layout)
	{
		table.clear();
		layout.createComponents(this);
		if (album != null)
		{
			notifyComponents(SlideshowEvent.AlbumLoaded);
			if (getActivePhoto() >= 0)
			{
				notifyComponents(SlideshowEvent.PhotoLoaded);
			}
		}
	}

	/**
	 * 
	 */
	public void play()
	{
		if (!isPlaying() && hasMorePhotos())
		{
			autoPlayTimer = new Timer()
			{
				@Override
				public void run()
				{
					if (next())
					{
						if ((getActivePhoto()+1) >= getPhotoCount())
						{
							stop();
						}
					}
					else
					{
						stop();
					}
				}
			};
			autoPlayTimer.schedule(transitionDelay);
			notifyComponents(SlideshowEvent.StartPlaying);
		}
	}
	
	/**
	 * 
	 */
	public void stop()
    {
        if (autoPlayTimer != null)
        {
        	autoPlayTimer.cancel();
        	autoPlayTimer = null;
        	notifyComponents(SlideshowEvent.StopPlaying);
        }
    }

	/**
	 * 
	 * @return
	 */
	public boolean isPlaying()
	{
		return (autoPlayTimer != null);
	}

	/**
	 * 
	 * @return
	 */
	public boolean next()
	{
		if (hasMorePhotos())
		{
			showPhoto(getActivePhoto()+1);
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean previous()
	{
		if (album != null)
		{
			int index = getActivePhoto() - 1;

			if (index >= 0 && index < getPhotoCount())
			{
				showPhoto(index);
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param component
	 * @param direction
	 */
	public void addComponent(SlideshowComponent component, Position position)
	{
		DockLayoutConstant direction;
		switch (position)
        {
        	case center: direction = DockPanel.CENTER; break;
        	case lineStart: direction = DockPanel.LINE_START; break; 
        	case lineEnd: direction = DockPanel.LINE_END; break;
        	case east: direction = DockPanel.EAST; break;
        	case north: direction = DockPanel.NORTH; break;
        	case south: direction = DockPanel.SOUTH; break;
        	case west: direction = DockPanel.WEST; break;
        	case none: direction = null; break;
	        default: direction = DockPanel.CENTER; break;
        }
		
		if (direction != null)
		{
			table.add(component, direction);
		}
		components.add(component);
		component.setSlideShow(this);
	}

	/**
	 * 
	 * @return
	 */
	public int getTransitionDelay()
    {
    	return transitionDelay;
    }

	/**
	 * 
	 * @param transitionDelay
	 */
	public void setTransitionDelay(int transitionDelay)
    {
    	this.transitionDelay = transitionDelay;
    }

	/**
	 * 
	 * @param component
	 * @param align
	 */
	public void setHorizontalAlignment(SlideshowComponent component, HorizontalAlignmentConstant align)
	{
		assert(table.getWidgetIndex(component) != -1):"Slideshow does not contains the requested component";
		table.setCellHorizontalAlignment(component, align);
	}
	
	/**
	 * 
	 * @param component
	 * @param align
	 */
	public void setVerticalAlignment(SlideshowComponent component, VerticalAlignmentConstant align)
	{
		assert(table.getWidgetIndex(component) != -1):"Slideshow does not contains the requested component";
		table.setCellVerticalAlignment(component, align);
	}
	
	/**
	 * 
	 * @param component
	 * @param height
	 */
	public void setHeight(SlideshowComponent component, String height)
	{
		assert(table.getWidgetIndex(component) != -1):"Slideshow does not contains the requested component";
		table.setCellHeight(component, height);
	}

	/**
	 * 
	 * @param component
	 * @param width
	 */
	public void setWidth(SlideshowComponent component, String width)
	{
		assert(table.getWidgetIndex(component) != -1):"Slideshow does not contains the requested component";
		table.setCellWidth(component, width);
	}

	/**
	 * 
	 * @param album
	 */
	public void setAlbum(PhotoAlbum album)
	{
		reset();
		this.album = album;
		if (album != null)
		{
			notifyComponents(SlideshowEvent.AlbumLoaded);
			showFirstPhoto();
		}
	}

	/**
	 * 
	 */
	public void showFirstPhoto()
    {
		assert(this.album != null):"There is no photo album loaded";
	    FastList<Photo> images = album.getImages();
	    if (images != null)
	    {
	    	if (images.size() > 0)
	    	{
	    		showPhoto(0);
	    	}
	    }
    }

	/**
	 * 
	 * @return
	 */
	public PhotoAlbum getAlbum()
	{
		return album;
	}
	
	/**
	 * 
	 * @param index
	 */
	public void showPhoto(final int index)
    {
		assert(this.album != null):"There is no photo album loaded";
		int photoCount = getPhotoCount();
		if (index != activeImage && index < photoCount && index > -1)
		{
			Image image = loadImage(index, preloadNextImages);
			if (image != null)
			{
				previousImage = activeImage;
				activeImage = index;
				notifyComponents(SlideshowEvent.PhotoLoaded);
			}
		}
    }
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public Image loadImage(int index)
	{
		return loadImage(index, false);
	}
	
	/**
	 * 
	 * @return
	 */
	public int getActivePhoto()
	{
		return activeImage;
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public Photo getPhoto(int index)
	{
		if (index < 0 || album == null || album.getImages() == null || album.getImages().size() <= index)
		{
			return null;
		}
		return album.getImages().get(index);
	}

	/**
	 * 
	 * @return
	 */
	public int getPhotoCount()
	{
		if (album == null || album.getImages() == null)
		{
			return 0;
		}
		return album.getImages().size();
	}

	/**
	 * 
	 * @return
	 */
	public boolean isPreloadNextImages()
    {
    	return preloadNextImages;
    }

	/**
	 * 
	 * @param preloadNextImages
	 */
	public void setPreloadNextImages(boolean preloadNextImages)
    {
    	this.preloadNextImages = preloadNextImages;
    }

	@Override
	protected void init()
	{
		imagesCache = new FastMap<Image>();
		components = new FastList<SlideshowComponent>();
		table = getChildWidget("table");
	}
	
	@Override
    protected void initWidgetDefaultStyleName()
    {
		setStyleName("xdev-Slideshow");
    }

	/**
	 * 
	 * @param index
	 * @param preloadNext
	 * @return
	 */
	protected Image loadImage(int index, boolean preloadNext)
    {
	    String key = Integer.toString(index);
		Image image = imagesCache.get(key);
		if (image == null)
		{
			Photo photo = album.getImages().get(index);
			image = new Image(photo.getUrl());
			image.setWidth(photo.getWidth()+"px");
			image.setHeight(photo.getHeight()+"px");
			imagesCache.put(key, image);
		}
		final int nextIndex = index + 1;
		if (preloadNext && nextIndex < getPhotoCount())
		{
			image.addLoadHandler(new LoadHandler()
			{
				@Override
				public void onLoad(LoadEvent event)
				{
					loadImage(nextIndex, false);
				}
			});
		}
		
		return image;
    }

	/**
	 * 
	 * @return
	 */
	protected boolean hasMorePhotos()
	{
		if (album != null)
		{
			int index = getActivePhoto() + 1;

			if (index < getPhotoCount())
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param event
	 */
	protected void notifyComponents(SlideshowEvent event)
    {
	    for (int i = 0; i < components.size(); i++)
	    {
	    	SlideshowComponent component = components.get(i);
	    	switch (event)
            {
	            case AlbumLoaded:
	            	component.onAlbumLoaded();
	            break;
	            case PhotoLoaded:
	            	component.onPhotoLoaded(previousImage, activeImage);
	            break;
	            case StartPlaying:
	            	component.onStartPlaying();
	            break;
	            case StopPlaying:
	            	component.onStopPlaying();
	            break;
            }
	    }
    }

	protected void reset()
    {
	    imagesCache.clear();
		activeImage = -1;
		previousImage = -1;
	}
}
