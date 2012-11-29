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
package org.cruxframework.crux.core.client.controller.crossdevice;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.screen.views.ViewAware;
import org.cruxframework.crux.core.client.utils.StyleUtils;

import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class DeviceAdaptiveController
{
	private Widget boundWidget;
	
	
	/**
	 * 
	 */
	public DeviceAdaptiveController()
    {
    }

	/**
	 * Override this method to define default stylename for the widget
	 */
	protected abstract void initWidgetDefaultStyleName();
	
	/**
	 * Override this method to apply dependent stylenames for the widget
	 */
	protected void applyWidgetDependentStyleNames()
	{
		applyWidgetDependentStyleNames(getElement());
	}

	/**
	 * Override this method if you need to add some startup code for component
	 */
	protected void init()
	{
		
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
    protected <T extends IsWidget> T getChildWidget(String id)
	{
		return (T)getControllerView().getWidget(id);
	}

	/**
	 * 
	 */
	protected void addWidget(String id, Widget widget)
	{
		getControllerView().addWidget(id, widget);
	}
	
	
	private View getControllerView()
	{
		String view = ((ViewAware)this).getBoundCruxView();
		return (view != null)?View.getView(view):null;
	}
	
	void setBoundWidget(Widget boundWidget)
	{
		this.boundWidget = boundWidget;
	}
	
	public void setWidth(String width)
	{
		boundWidget.setWidth(width);
	}
	
	public void setVisible(boolean visible)
	{
		boundWidget.setVisible(visible);
	}
	
	public boolean isVisible()
	{
		return boundWidget.isVisible();
	}
	
	public void setStyleName(String style)
	{
		boundWidget.setStyleName(style);
		applyWidgetDependentStyleNames();
	}
	
	public String getStyleName()
	{
		return boundWidget.getStyleName();
	}
	
	public void setTitle(String title)
	{
		boundWidget.setTitle(title);
	}
	
	public String getTitle()
	{
		return boundWidget.getTitle();
	}
	
	public void setHeight(String height)
	{
		boundWidget.setHeight(height);
	}
		
	public Element getElement()
	{
		return boundWidget.getElement();
	}
	
	public HandlerRegistration addAttachHandler(Handler handler)
	{
		return boundWidget.addAttachHandler(handler);
	}

	public <H extends EventHandler> HandlerRegistration addHandler(final H handler, GwtEvent.Type<H> type)
	{
		return boundWidget.addHandler(handler, type);
	}

	public void fireEvent(GwtEvent<?> event)
	{
		boundWidget.fireEvent(event);
	}
	
	public Widget asWidget()
	{
		return boundWidget;
	}
	
	/**
	 * Apply dependent style name on element, based on current device
	 * @param element
	 */
	public static void applyWidgetDependentStyleNames(Element element)
	{
		switch (Screen.getCurrentDevice())
        {
        	case smallDisplayArrows:
        		StyleUtils.addStyleDependentName(element, DeviceAdaptive.Input.arrows.toString());
        		StyleUtils.addStyleDependentName(element, DeviceAdaptive.Size.small.toString());
	        break;
        	case smallDisplayTouch:
        		StyleUtils.addStyleDependentName(element, DeviceAdaptive.Input.touch.toString());
        		StyleUtils.addStyleDependentName(element, DeviceAdaptive.Size.small.toString());
	        break;
        	case largeDisplayTouch:
        		StyleUtils.addStyleDependentName(element, DeviceAdaptive.Input.touch.toString());
        		StyleUtils.addStyleDependentName(element, DeviceAdaptive.Size.large.toString());
	        break;
        	case largeDisplayArrows:
        		StyleUtils.addStyleDependentName(element, DeviceAdaptive.Input.arrows.toString());
        		StyleUtils.addStyleDependentName(element, DeviceAdaptive.Size.large.toString());
	        break;
        	case largeDisplayMouse:
        		StyleUtils.addStyleDependentName(element, DeviceAdaptive.Input.mouse.toString());
        		StyleUtils.addStyleDependentName(element, DeviceAdaptive.Size.large.toString());
	        break;
        }
	}
}
