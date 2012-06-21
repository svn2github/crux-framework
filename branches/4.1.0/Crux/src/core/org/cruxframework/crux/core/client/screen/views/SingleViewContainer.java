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
package org.cruxframework.crux.core.client.screen.views;

import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.Panel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class SingleViewContainer extends ViewContainer
{
	private View rootView;
	
	@Override
    protected boolean doAdd(View view)
    {
		if (rootView == null || (rootView != view && remove(rootView)))
		{
			rootView = view;
			rootView.load();
			return true;
		}
		return false;
    }

	@Override
    protected boolean doRemove(View view)
    {
		if (rootView == view)
		{
			assert (rootView != null):"Can not remove a null view from the ViewContainer";
			if (rootView.unload() && deactivate(rootView, getContainerPanel()))
			{
				rootView = null;
				return true;
			}
		}
		return false;
    }
	
	@Override
    protected void renderView(View view)
    {
		assert (rootView!= null && rootView.equals(view)):"Can not render the view["+view.getId()+"]. It was not added to the container";
		Panel containerPanel = getContainerPanel();
		activate(rootView, containerPanel);
		String title = view.getTitle();
		if (!StringUtils.isEmpty(title))
		{
			handleViewTitle(title, containerPanel);
		}
    }

	public View getView()
	{
		return rootView;
	}
	
	@Override
    public View getView(String viewId)
    {
		if (rootView != null && viewId != null && rootView.getId().equals(viewId))
		{
			return rootView;
		}
	    return null;
    }

	@Override
    protected boolean hasResizeHandlers()
    {
	    return rootView!= null && rootView.hasResizeHandlers();
    }
	
	@Override
	protected boolean hasWindowCloseHandlers()
	{
	    return rootView!= null && rootView.hasWindowCloseHandlers();
	}

	@Override
	protected boolean hasWindowClosingHandlers()
	{
	    return rootView!= null && rootView.hasWindowClosingHandlers();
	}

	@Override
	protected boolean hasOrientationChangeOrResizeHandlers()
	{
	    return rootView!= null && rootView.hasOrientationChangeOrResizeHandlers();
	}
	
	@Override
	protected boolean hasHistoryHandlers()
	{
	    return rootView!= null && rootView.hasHistoryHandlers();
	}
	
	@Override
    protected void notifyViewsAboutWindowResize(ResizeEvent event)
    {
		if (rootView!= null)
		{
			rootView.fireResizeEvent(event);
		}
    }
	
	@Override
    protected void notifyViewsAboutWindowClose(CloseEvent<Window> event)
    {
		if (rootView!= null)
		{
			rootView.fireWindowCloseEvent(event);
		}
    }

	@Override
    protected void notifyViewsAboutWindowClosing(ClosingEvent event)
    {
		if (rootView!= null)
		{
			rootView.fireWindowClosingEvent(event);
		}
    }
	
	@Override
	protected void notifyViewsAboutOrientationChangeOrResize()
	{
		if (rootView!= null)
		{
			rootView.fireOrientationOrResizeEvent();
		}
	}
	
	@Override
	protected void notifyViewsAboutHistoryChange(ValueChangeEvent<String> event)
	{
		if (rootView!= null)
		{
			rootView.fireHistoryChangeEvent(event);
		}
	}

	protected abstract Panel getContainerPanel();
}
