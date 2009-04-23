/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.ext.client.component;

import br.com.sysmap.crux.core.client.component.Component;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.EventFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.user.client.ui.FocusWidget;

/**
 * This is the base class for components that can receive focus. 
 * 
 * @author Thiago Bustamante
 *
 */
public class FocusComponent extends Component 
{
	protected FocusWidget focusWidget;
	protected char accessKey;

	/**
	 * Constructor
	 * @param id
	 * @param widget
	 */
	public FocusComponent(String id, FocusWidget widget) 
	{
		super(id, widget);
		focusWidget = (FocusWidget)widget;
	}

	/**
	 * Return the component tabIndex
	 * @return tabIndex
	 */
	public int getTabIndex() 
	{
		return focusWidget.getTabIndex();
	}

	/**
	 * Return true if the component is enabled
	 * @return enabled
	 */
	public boolean isEnabled() 
	{
		return focusWidget.isEnabled();
	}

	/**
	 * Return the component accessKey
	 * @return accessKey
	 */
	public char getAccessKey() 
	{
		return accessKey;
	}

	/**
	 * Set the component accessKey
	 * @param accessKey
	 */
	public void setAccessKey(char accessKey) 
	{
		if (this.accessKey != accessKey)
		{
			focusWidget.setAccessKey(accessKey);
			this.accessKey = accessKey;
		}
	}

	/**
	 * Set the component enabled property
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) 
	{
		focusWidget.setEnabled(enabled);
	}

	/**
	 * Put the focus on component or remove the focus from it
	 * @param focused
	 */
	public void setFocus(boolean focused) 
	{
		focusWidget.setFocus(focused);
	}

	/**
	 * Set the component tabIndex
	 * @param tabIndex
	 */
	public void setTabIndex(int tabIndex) 
	{
		focusWidget.setTabIndex(tabIndex);
	}

	/**
	 * Render component attributes
	 * @see #Component.renderAttributes
	 */
	protected void renderAttributes(Element element)
	{
		super.renderAttributes(element);

		String tabIndex = element.getAttribute("_tabIndex");
		if (tabIndex != null && tabIndex.trim().length() > 0)
		{
			focusWidget.setTabIndex(Integer.parseInt(tabIndex));
		}
		String enabled = element.getAttribute("_enabled");
		if (enabled != null && enabled.trim().length() > 0)
		{
			focusWidget.setEnabled(Boolean.parseBoolean(enabled));
		}
		String accessKey = element.getAttribute("_accessKey");
		if (accessKey != null && accessKey.trim().length() == 1)
		{
			this.accessKey = accessKey.charAt(0);
			focusWidget.setAccessKey(this.accessKey);
		}
	}

	/**
	 * Render component events
	 * @see #Component.attachEvents
	 */
	protected void attachEvents(Element element)
	{	 
		super.attachEvents(element);

		final Event eventFocus = getComponentEvent(element, EventFactory.EVENT_FOCUS);
		if (eventFocus != null)
		{
			focusWidget.addFocusHandler(new FocusHandler()
			{
				@Override
				public void onFocus(FocusEvent event) 
				{
					EventFactory.callEvent(eventFocus, getId());
				}
			});
		}
		
		final Event eventBlur = getComponentEvent(element, EventFactory.EVENT_BLUR);
		if (eventBlur != null)
		{
			focusWidget.addBlurHandler(new BlurHandler()
			{
				@Override
				public void onBlur(BlurEvent event) 
				{
					EventFactory.callEvent(eventBlur, getId());
				}
			});
		}

		final Event eventClick = getComponentEvent(element, EventFactory.EVENT_CLICK);
		if (eventClick != null)
		{
			ClickHandler handler = new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event) 
				{
					EventFactory.callEvent(eventClick, getId());
				}
			};
			focusWidget.addClickHandler(handler);
		}

		final Event eventKeyDown = getComponentEvent(element, EventFactory.EVENT_KEY_DOWN);
		if (eventKeyDown != null)
		{
			focusWidget.addKeyDownHandler(new KeyDownHandler()
			{
				@Override
				public void onKeyDown(KeyDownEvent event) 
				{
					EventFactory.callEvent(eventKeyDown, getId());
				}
			});
		}

		final Event eventKeyPress = getComponentEvent(element, EventFactory.EVENT_KEY_PRESS);
		if (eventKeyPress != null)
		{
			focusWidget.addKeyPressHandler(new KeyPressHandler()
			{
				@Override
				public void onKeyPress(KeyPressEvent event) 
				{
					EventFactory.callEvent(eventKeyPress, getId());
				}
			});
		}
		
		final Event eventKeyUp = getComponentEvent(element, EventFactory.EVENT_KEY_UP);
		if (eventKeyUp != null)
		{
			focusWidget.addKeyUpHandler(new KeyUpHandler()
			{
				@Override
				public void onKeyUp(KeyUpEvent event) 
				{
					EventFactory.callEvent(eventKeyUp, getId());
				}
			});
		}

		final Event eventMouseDown = getComponentEvent(element, EventFactory.EVENT_MOUSE_DOWN);
		if (eventMouseDown != null)
		{
			focusWidget.addMouseDownHandler(new MouseDownHandler()
			{
				@Override
				public void onMouseDown(MouseDownEvent event) 
				{
					EventFactory.callEvent(eventMouseDown, getId());
				}
			});
		}
	
		final Event eventMouseMove = getComponentEvent(element, EventFactory.EVENT_MOUSE_MOVE);
		if (eventMouseMove != null)
		{
			focusWidget.addMouseMoveHandler(new MouseMoveHandler()
			{
				@Override
				public void onMouseMove(MouseMoveEvent event) 
				{
					EventFactory.callEvent(eventMouseMove, getId());
				}
			});
		}

		final Event eventMouseOut = getComponentEvent(element, EventFactory.EVENT_MOUSE_OUT);
		if (eventMouseOut != null)
		{
			focusWidget.addMouseOutHandler(new MouseOutHandler()
			{
				@Override
				public void onMouseOut(MouseOutEvent event) 
				{
					EventFactory.callEvent(eventMouseOut, getId());					
				}
			});
		}
	
		final Event eventMouseOver = getComponentEvent(element, EventFactory.EVENT_MOUSE_OVER);
		if (eventMouseOver != null)
		{
			focusWidget.addMouseOverHandler(new MouseOverHandler()
			{
				@Override
				public void onMouseOver(MouseOverEvent event) 
				{
					EventFactory.callEvent(eventMouseOver, getId());					
				}
			});
		}

		final Event eventMouseUp = getComponentEvent(element, EventFactory.EVENT_MOUSE_UP);
		if (eventMouseUp != null)
		{
			focusWidget.addMouseUpHandler(new MouseUpHandler()
			{
				@Override
				public void onMouseUp(MouseUpEvent event) 
				{
					EventFactory.callEvent(eventMouseUp, getId());					
				}
			});
		}

		final Event eventMouseWheel = getComponentEvent(element, EventFactory.EVENT_MOUSE_WHEEL);
		if (eventMouseWheel != null)
		{
			focusWidget.addMouseWheelHandler(new MouseWheelHandler()
			{
				@Override
				public void onMouseWheel(MouseWheelEvent event) 
				{
					EventFactory.callEvent(eventMouseWheel, getId());					
				}
			});
		}
	}
}
