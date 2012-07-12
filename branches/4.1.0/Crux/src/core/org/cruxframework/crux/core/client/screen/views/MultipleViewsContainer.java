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

import org.cruxframework.crux.core.client.collection.FastMap;
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
public abstract class MultipleViewsContainer extends ViewContainer
{
	private FastMap<View> views = new FastMap<View>();
	private View activeView = null;;

	/**
	 * 
	 * @return
	 */
	public View getActiveView()
	{
		return activeView;
	}
	
	@Override
    protected boolean doAdd(View view)
    {
		if (!views.containsKey(view.getId()))
		{
			views.put(view.getId(), view);
			view.load();
			return true;
		}
		return false;
    }

	@Override
    protected boolean doRemove(View view)
    {
		if (views.containsKey(view.getId()))
		{
			if (deactivate(view, getContainerPanel(view)) && view.unload())
			{
				views.remove(view.getId());
				return true;
			}
		}
		return false;
    }
	
	@Override
	protected void activate(View view, Panel containerPanel)
	{
	    if (activeView != null)
	    {
	    	if (deactivate(activeView, getContainerPanel(activeView)))
	    	{
	    		super.activate(view, containerPanel);
	    		activeView = view;
	    	}
	    }
	    else
	    {
    		super.activate(view, containerPanel);
    		activeView = view;
	    }
	}
	
	@Override
	protected boolean deactivate(View view, Panel containerPanel)
	{
		assert(view != null):"Can not deactive a null view";
		boolean deactivated = false;
		if (activeView != null && view.getId().equals(activeView.getId()))
		{
			deactivated = super.deactivate(view, containerPanel);
			if (deactivated)
			{
				activeView = null;
			}
		}
		return deactivated;
	}
	
	@Override
    protected void renderView(View view)
    {
		assert (view!= null && views.containsKey(view.getId())):"Can not render the view["+view.getId()+"]. It was not added to the container";
		Panel containerPanel = getContainerPanel(view);
		activate(view, containerPanel);
		String title = view.getTitle();
		if (!StringUtils.isEmpty(title))
		{
			handleViewTitle(title, containerPanel);
		}
    }

	@Override
    public View getView(String viewId)
    {
		if (viewId == null)
		{
			return null;
		}
	    return views.get(viewId);
    }

	@Override
    protected boolean hasResizeHandlers()
    {
	    return activeView!= null && activeView.hasResizeHandlers();
    }
	
	@Override
	protected boolean hasWindowCloseHandlers()
	{
	    return activeView!= null && activeView.hasWindowCloseHandlers();
	}

	@Override
	protected boolean hasWindowClosingHandlers()
	{
	    return activeView!= null && activeView.hasWindowClosingHandlers();
	}

	@Override
	protected boolean hasOrientationChangeOrResizeHandlers()
	{
	    return activeView!= null && activeView.hasOrientationChangeOrResizeHandlers();
	}
	
	@Override
	protected boolean hasHistoryHandlers()
	{
	    return activeView!= null && activeView.hasHistoryHandlers();
	}
	
	@Override
    protected void notifyViewsAboutWindowResize(ResizeEvent event)
    {
		if (activeView!= null)
		{
			activeView.fireResizeEvent(event);
		}
    }
	
	@Override
    protected void notifyViewsAboutWindowClose(CloseEvent<Window> event)
    {
		if (activeView!= null)
		{
			activeView.fireWindowCloseEvent(event);
		}
    }

	@Override
    protected void notifyViewsAboutWindowClosing(ClosingEvent event)
    {
		if (activeView!= null)
		{
			activeView.fireWindowClosingEvent(event);
		}
    }
	
	@Override
	protected void notifyViewsAboutOrientationChangeOrResize()
	{
		if (activeView!= null)
		{
			activeView.fireOrientationOrResizeEvent();
		}
	}
	
	@Override
	protected void notifyViewsAboutHistoryChange(ValueChangeEvent<String> event)
	{
		if (activeView!= null)
		{
			activeView.fireHistoryChangeEvent(event);
		}
	}

	protected abstract Panel getContainerPanel(View view);
}
