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
package org.cruxframework.crux.widgets.client.swapcontainer;

import org.cruxframework.crux.core.client.screen.views.SingleViewContainer;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.widgets.client.animation.Animation.Callback;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel.Direction;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class HorizontalSwapContainer extends SingleViewContainer implements HasChangeViewHandlers
{
	public static final String DEFAULT_STYLE_NAME = "crux-HorizontalSwapContainer";
	private HorizontalSwapPanel swapPanel;
	private Panel active;
	private Panel swap;

	public HorizontalSwapContainer()
	{
		super(new HorizontalSwapPanel(), false);
		swapPanel = getMainWidget();
		swapPanel.setStyleName(DEFAULT_STYLE_NAME);
		active = new SimplePanel();
		swap = new SimplePanel();
	}

	/**
	 * 
	 * @param viewId
	 * @param direction
	 */
	public void showView(String viewId, Direction direction)
	{
		assert(views.containsKey(viewId)):"View ["+viewId+"] was not loaded into this container.";
		renderView(getView(viewId), direction);
	}

	/**
	 * 
	 * @param view
	 * @param direction
	 */
	protected void renderView(View view, Direction direction)
	{
		if (activeView == null || !activeView.getId().equals(view.getId()))
		{
			final View previous = activeView;
			final View next = view;
			super.renderView(view);
			if (previous == null || direction == null)
			{
				swapPanel.setCurrentWidget(active);
				swap.clear();
				ChangeViewEvent.fire(HorizontalSwapContainer.this, previous, next);
			}
			else
			{
				swapPanel.transitTo(active, direction, new Callback()
				{
					@Override
					public void onTransitionCompleted()
					{
						swap.clear();
						ChangeViewEvent.fire(HorizontalSwapContainer.this, previous, next);
					}
				});
			}
		}
	}

	@Override
	protected void renderView(View view)
	{
		renderView(view, Direction.FORWARD);
	}

	@Override
	protected void activate(View view, Panel containerPanel)
	{
		super.activate(view, containerPanel);
		swapPanelVariables();
	}

	@Override
	protected Panel getContainerPanel(View view)
	{
		assert(view != null):"Can not retrieve a container for a null view";
		if (activeView != null && activeView.getId().equals(view.getId()))
		{
			return active;
		}
		return swap;
	}

	@Override
	protected void handleViewTitle(String title, Panel containerPanel, String viewId)
	{
		// Do nothing
	}

	private void swapPanelVariables()
	{
		Panel temp = active;
		active = swap;
		swap = temp;
	}

	/**
	 * 
	 * @return
	 */
	public int getTransitionDuration()
	{
		return swapPanel.getTransitionDuration();
	}

	/**
	 * 
	 * @param transitionDuration
	 */
	public void setTransitionDuration(int transitionDuration)
	{
		swapPanel.setTransitionDuration(transitionDuration);
	}

	@Override
	public HandlerRegistration addChangeViewHandler(ChangeViewHandler handler)
	{
		return addHandler(handler, ChangeViewEvent.getType());
	}
}
