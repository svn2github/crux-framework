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

import java.util.logging.Logger;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.screen.InterfaceConfigException;
import org.cruxframework.crux.core.client.screen.views.View.RenderCallback;
import org.cruxframework.crux.core.client.screen.views.ViewFactory.CreateCallback;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class ViewContainer extends Composite
{
	private static ViewFactory viewFactory;	
	private final boolean clearPanelsForDeactivatedViews;
	
	protected static Logger logger = Logger.getLogger(ViewContainer.class.getName());
	protected FastMap<View> views = new FastMap<View>();

	/**
	 * Constructor
	 */
	public ViewContainer()
    {
		this(true);
    }

	/**
	 * Constructor
	 * @param clearPanelsForDeactivatedViews If true, makes the container clear the container panel for a view, when the view is deactivated.
	 */
	public ViewContainer(boolean clearPanelsForDeactivatedViews)
    {
		this.clearPanelsForDeactivatedViews = clearPanelsForDeactivatedViews;
		ViewHandlers.initializeWindowContainers();
    }
	
	/**
	 * Loads a new view into the container
	 * @param view View to be added
	 * @return
	 */
	public boolean add(View view)
	{
		return addView(view, false);
	}

	/**
	 * Adds a new view into the container, but does not load the view. 
	 * @param view View to be added
	 * @return
	 */
	public boolean addLazy(View view)
	{
		return addView(view, true);
	}
	
	/**
	 * Remove the view from this container
	 * @param view View to be removed
	 * @return
	 */
	public boolean remove(View view)
	{
		return remove(view, false);
	}
	
	/**
	 * Remove the view from this container
	 * @param view View to be removed
	 * @param skipEvents skip the events fired during view removal
	 * @return
	 */
	public boolean remove(View view, boolean skipEvents)
	{
		assert (view != null):"Can not remove a null view from the ViewContainer";
		if (doRemove(view, skipEvents))
		{
			view.setContainer(null);
			return true;
		}
		return false;
	}

	/**
	 * Loads a new view into the container
	 * @param view View to be added
	 * @param render If true, call the render method
	 * @return
	 */
    public boolean add(View view, boolean render)
    {
		if (add(view))
		{
			if (render)
			{
				renderView(view);
			}
			return true;
		}
		return false;
    }

    /**
     * Render the requested view into the container.
     * @param viewId View identifier
     */
	public void showView(String viewId)
	{
		assert(views.containsKey(viewId)):"View ["+viewId+"] was not loaded into this container.";
		renderView(getView(viewId));
	}



	/**
	 * Retrieve the view associated to viewId
	 * @param viewId View identifier
	 * @return The view
	 */
    public View getView(String viewId)
    {
		if (viewId == null)
		{
			return null;
		}
	    return views.get(viewId);
    }

	/**
	 * Retrieve the views factory associated with this screen.
	 * @return
	 */
	public static ViewFactory getViewFactory()
	{
		if (viewFactory == null)
		{
			viewFactory = (ViewFactory) GWT.create(ViewFactory.class);
		}
		return viewFactory;
	}

	/**
	 * Creates the view referenced by the given name
	 * 
	 * @param viewName View name
	 * @param callback Called when the view creation is completed.
	 */
	public static void createView(String viewName, CreateCallback callback)
	{
		getViewFactory().createView(viewName, callback);
	}

	/**
	 * Creates the view referenced by the given name and associate a custom identifier with the view created
	 * 
	 * @param viewName View name
	 * @param viewId View identifier
	 * @param callback Called when the view creation is completed.
	 */
	public static void createView(String viewName, String viewId, CreateCallback callback)
	{
		getViewFactory().createView(viewName, viewId, callback);
	}

	/**
	 * Loads a view into the current container
	 * 
	 * @param viewName View name
	 * @param render If true also render the view
	 */
	public void loadView(String viewName, final boolean render)
	{
		loadView(viewName, viewName, render);
	}

	/**
	 * Loads a view into the current container
	 * 
	 * @param viewName View name
	 * @param viewId View identifier
	 * @param render If true also render the view
	 */
	public void loadView(final String viewName, final String viewId, final boolean render)
	{
		try
		{
			if (LogConfiguration.loggingIsEnabled())
			{
				logger.info(Crux.getMessages().viewContainerCreatingView(viewId));
			}
			createView(viewName, viewId, new CreateCallback()
			{
				@Override
				public void onViewCreated(View view)
				{
					if (!add(view, render))
					{
						Crux.getErrorHandler().handleError(Crux.getMessages().viewContainerErrorCreatingView(viewId));
					}
				}
			});
		}
		catch (InterfaceConfigException e)
		{
			Crux.getErrorHandler().handleError(Crux.getMessages().viewContainerErrorCreatingView(viewId), e);
		}
	}
	
	/**
	 * This method must be called by subclasses when the container is attached to DOM
	 */
	protected void bindToDOM()
	{
		ViewHandlers.bindToDOM(this);
	}
	
	/**
	 * This method must be called by subclasses when the container is detached from DOM
	 */
	protected void unbindToDOM()
	{
		ViewHandlers.unbindToDOM(this);
	}

	/**
	 * This method must be called by subclasses when any of your views is rendered.
	 * @param view
	 * @param containerPanel
	 */
	protected void activate(final View view, Panel containerPanel)
	{
		if (!view.isLoaded())
		{
			view.load();
		}
		view.render(containerPanel, new RenderCallback()
		{
			@Override
			public void onRendered()
			{
				view.setActive();
			}
		});
		ViewHandlers.ensureViewContainerHandlers(this);
	}

	/**
	 * This method must be called by subclasses when any of your views currently rendered is removed from view.
	 * 
	 * @param view
	 * @param containerPanel
	 * @param skipEvent
	 * @return True if view is deactivated
	 */
	protected boolean deactivate(View view, Panel containerPanel, boolean skipEvent)
    {
		if (view.isActive())
		{
			if (view.setDeactivated(skipEvent))
			{
				if (this.clearPanelsForDeactivatedViews)
				{
					containerPanel.clear();
				}
				ViewHandlers.removeViewContainerHandlers();
				return true;
			}
			return false;
		}
		return true;
    }
	
	/**
	 * Loads a new view into the container
	 * @param view View to be added
	 * @return
	 */
    protected boolean doAdd(View view, boolean lazy)
    {
		if (!views.containsKey(view.getId()))
		{
			views.put(view.getId(), view);
			if (!lazy)
			{
				view.load();
			}
			return true;
		}
		return false;
    }

    /**
     * 
     * @param view
     * @param skipEvent
     * @return
     */
	protected boolean doRemove(View view, boolean skipEvent)
    {
	    if (views.containsKey(view.getId()))
		{
			if (deactivate(view, getContainerPanel(view), skipEvent) && (skipEvent || view.unload()))
			{
				views.remove(view.getId());
				return true;
			}
		}
		return false;
    }

    /**
     * Render the view into the container
     * @param view
     */
    protected void renderView(View view)
    {
		assert (view!= null && views.containsKey(view.getId())):"Can not render the view["+view.getId()+"]. It was not added to the container";
		Panel containerPanel = getContainerPanel(view);
		activate(view, containerPanel);
		String title = view.getTitle();
		if (!StringUtils.isEmpty(title))
		{
			handleViewTitle(title, containerPanel, view.getId());
		}
    }	
    
    /**
     * 
     * @param view
     * @param lazy
     * @return
     */
    protected boolean addView(View view, boolean lazy)
    {
	    assert (view != null):"Can not add a null view to the ViewContainer";
		assert (getView(view.getId()) == null):"This container already contains a view with the given identifier ["+view.getId()+"].";
		if (doAdd(view, lazy))
		{
			adoptView(view);
			return true;
		}
		return false;
    }

    /**
     * 
     * @param view
     */
    protected void adoptView(View view)
    {
    	view.setContainer(this);
    }
    
	protected abstract boolean hasResizeHandlers();
	protected abstract boolean hasWindowCloseHandlers();
	protected abstract boolean hasWindowClosingHandlers();
	protected abstract boolean hasOrientationChangeOrResizeHandlers();
	protected abstract boolean hasHistoryHandlers();
	protected abstract void notifyViewsAboutWindowResize(ResizeEvent event);
	protected abstract void notifyViewsAboutWindowClose(CloseEvent<Window> event);
	protected abstract void notifyViewsAboutWindowClosing(ClosingEvent event);
	protected abstract void notifyViewsAboutOrientationChangeOrResize();
	protected abstract void notifyViewsAboutHistoryChange(ValueChangeEvent<String> event);
	protected abstract Panel getContainerPanel(View view);
	protected abstract void handleViewTitle(String title, Panel containerPanel, String viewId);
}
