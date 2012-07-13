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
package org.cruxframework.crux.views.client;

import org.cruxframework.crux.core.client.screen.views.SingleViewContainer;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class MultipleViewsContainerWidget extends SingleViewContainer implements IsWidget, HasHandlers
{
	private HandlerManager handlerManager;

	public MultipleViewsContainerWidget(boolean clearPanelsForDeactivatedViews)
    {
	    super(clearPanelsForDeactivatedViews);
    }

	/**
	 * 
	 * @param width
	 */
	public void setWidth(String width)
	{
		asWidget().setWidth(width);
	}

	/**
	 * 
	 * @param height
	 */
	public void setHeight(String height)
	{
		asWidget().setHeight(height);
	}

	/**
	 * 
	 * @param styleName
	 */
	public void setStyleName(String styleName)
	{
		asWidget().setStyleName(styleName);
	}

	/**
	 * 
	 * @return
	 */
	public String getStyleName()
	{
		return asWidget().getStyleName();
	}
	
	/**
	 * 
	 * @param title
	 */
	public void setTitle(String title)
	{
		asWidget().setTitle(title);
	}

	/**
	 * 
	 * @return
	 */
	public String getTitle()
	{
		return asWidget().getTitle();
	}

	/**
	 * 
	 * @return
	 */
	public boolean isVisible()
	{
		return asWidget().isVisible();
	}
	
	/**
	 * 
	 * @param visible
	 */
	public void setVisible(boolean visible)
	{
		asWidget().setVisible(visible);
	}

	/**
	 * 
	 * @return
	 */
	public Element getElement()
	{
		return asWidget().getElement();
	}
	
	@Override
    public void fireEvent(GwtEvent<?> event)
    {
		if (handlerManager != null) 
		{
			handlerManager.fireEvent(event);
		}
    }
	
	public final <H extends EventHandler> HandlerRegistration addHandler(final H handler, GwtEvent.Type<H> type) 
	{
		return ensureHandlers().addHandler(type, handler);
	}

	private HandlerManager ensureHandlers() 
	{
		return handlerManager == null ? handlerManager = new HandlerManager(this) : handlerManager;
	}
}
