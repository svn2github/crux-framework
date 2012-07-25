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

import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel.Direction;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SlideshowPhotoPanel extends SlideshowComponent
{
	private HorizontalSwapPanel photosPanel ;
	private FlowPanel mainPanel;
	private Label rightArrow;
	private Label leftArrow;

	public SlideshowPhotoPanel()
    {
		super();
		setStyleName("xdev-SlideshowPhotoPanel");
    }
	
	@Override
    public void onAlbumLoaded()
    {
		photosPanel.clear();
    }

	@Override
    public void onPhotoLoaded(int previousIndex, int nextIndex)
    {
		assert(this.getSlideshow() != null):"Slideshow is not initialized. Set component's slideshow property first.";
		Direction direction = (nextIndex > previousIndex)?Direction.FORWARD:Direction.BACKWARDS;
		photosPanel.transitTo(getSlideshow().loadImage(nextIndex), direction);

		if (previousIndex == 0 && nextIndex > 0)
		{
			leftArrow.removeStyleDependentName("disabled");
		}
		else if (nextIndex == 0)
		{
			leftArrow.addStyleDependentName("disabled");
		}

		int lastIndex = getSlideshow().getPhotoCount() -1;
		if (previousIndex == lastIndex && nextIndex < lastIndex)
		{
			rightArrow.removeStyleDependentName("disabled");
		}
		else if (nextIndex == lastIndex)
		{
			rightArrow.addStyleDependentName("disabled");
		}
    }

	@Override
    protected Widget createMainWidget()
    {
		mainPanel = new FlowPanel();
		mainPanel.getElement().getStyle().setPosition(Position.RELATIVE);
		mainPanel.setWidth("100%");
		
		photosPanel = new HorizontalSwapPanel();
		photosPanel.setWidth("100%");
		photosPanel.setHeight("100%");
		
		leftArrow = new Label();
		leftArrow.setStyleName("leftArrow");
		leftArrow.getElement().getStyle().setPosition(Position.ABSOLUTE);
		leftArrow.getElement().getStyle().setLeft(0, Unit.PX);
		leftArrow.getElement().getStyle().setTop(50, Unit.PCT);
		leftArrow.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				getSlideshow().stop();
				getSlideshow().previous();
			}
		});
		
		rightArrow = new Label();
		rightArrow.setStyleName("rightArrow");
		rightArrow.getElement().getStyle().setPosition(Position.ABSOLUTE);
		rightArrow.getElement().getStyle().setRight(0, Unit.PX);
		rightArrow.getElement().getStyle().setTop(50, Unit.PCT);
		rightArrow.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				getSlideshow().stop();
				getSlideshow().next();
			}
		});
		
		mainPanel.add(photosPanel);
		mainPanel.add(leftArrow);
		mainPanel.add(rightArrow);

		return mainPanel;
    }

}
