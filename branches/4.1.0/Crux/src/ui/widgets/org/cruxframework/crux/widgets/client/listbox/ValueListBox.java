/*
 * Copyright 2012 cruxframework.org.
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

package org.cruxframework.crux.widgets.client.listbox;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.GestureChangeHandler;
import com.google.gwt.event.dom.client.GestureEndHandler;
import com.google.gwt.event.dom.client.GestureStartHandler;
import com.google.gwt.event.dom.client.HasAllDragAndDropHandlers;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.HasAllGestureHandlers;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.shared.DirectionEstimator;
import com.google.gwt.i18n.shared.HasDirectionEstimator;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.view.client.ProvidesKey;

/**
 *  @author daniel.martins - <code>daniel@cruxframework.org</code>
 *
 */
public class ValueListBox<T> extends com.google.gwt.user.client.ui.ValueListBox<T> implements
							HasChangeHandlers, HasName, HasDirectionEstimator, HasClickHandlers, HasDoubleClickHandlers, Focusable, HasEnabled,
						    HasAllDragAndDropHandlers, HasAllFocusHandlers, HasAllGestureHandlers,
						    HasAllKeyHandlers, HasAllMouseHandlers, HasAllTouchHandlers
{

	private ListBox listBox;

	public ValueListBox(Renderer<T> renderer, ProvidesKey<T> keyProvider)
	{
		super(renderer, keyProvider);
		this.listBox = (ListBox) getWidget();
	}


	@Override
	public void setFocus(boolean focused)
	{
		listBox.setFocus(focused);
	}

	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler)
	{
		return listBox.addChangeHandler(handler);
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler)
	{
		return listBox.addFocusHandler(handler);
	}


	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler)
	{
		return listBox.addBlurHandler(handler);
	}


	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler)
	{
		return listBox.addMouseDownHandler(handler);
	}


	@Override
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler)
	{
		return listBox.addMouseUpHandler(handler);
	}


	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler)
	{
		return listBox.addMouseOutHandler(handler);
	}


	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler)
	{
		return listBox.addMouseOverHandler(handler);
	}


	@Override
	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler)
	{
		return listBox.addMouseWheelHandler(handler);
	}


	@Override
	public HandlerRegistration addTouchStartHandler(TouchStartHandler handler)
	{
		return listBox.addTouchStartHandler(handler);
	}


	@Override
	public HandlerRegistration addTouchMoveHandler(TouchMoveHandler handler)
	{
		return listBox.addTouchMoveHandler(handler);
	}


	@Override
	public HandlerRegistration addTouchEndHandler(TouchEndHandler handler)
	{
		return listBox.addTouchEndHandler(handler);
	}


	@Override
	public HandlerRegistration addTouchCancelHandler(TouchCancelHandler handler)
	{
		return listBox.addTouchCancelHandler(handler);
	}


	@Override
	public boolean isEnabled()
	{
		return listBox.isEnabled();
	}


	@Override
	public void setEnabled(boolean enabled)
	{
		listBox.setEnabled(enabled);
	}


	@Override
	public int getTabIndex()
	{
		return listBox.getTabIndex();
	}


	@Override
	public void setAccessKey(char key)
	{
		listBox.setAccessKey(key);
	}


	@Override
	public void setTabIndex(int index)
	{
		listBox.setTabIndex(index);
	}


	@Override
	public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler)
	{
		return listBox.addDoubleClickHandler(handler);
	}


	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler)
	{
		return listBox.addClickHandler(handler);
	}


	@Override
	public void setName(String name)
	{
		listBox.setName(name);
	}


	@Override
	public String getName()
	{
		return listBox.getName();
	}


	@Override
	public HandlerRegistration addDragEndHandler(DragEndHandler handler)
	{
		return listBox.addDragEndHandler(handler);
	}


	@Override
	public HandlerRegistration addDragEnterHandler(DragEnterHandler handler)
	{
		return listBox.addDragEnterHandler(handler);
	}


	@Override
	public HandlerRegistration addDragLeaveHandler(DragLeaveHandler handler)
	{
		return listBox.addDragLeaveHandler(handler);
	}


	@Override
	public HandlerRegistration addDragHandler(DragHandler handler)
	{
		return listBox.addDragHandler(handler);
	}


	@Override
	public HandlerRegistration addDragOverHandler(DragOverHandler handler)
	{
		return listBox.addDragOverHandler(handler);
	}


	@Override
	public HandlerRegistration addDragStartHandler(DragStartHandler handler)
	{
		return listBox.addDragStartHandler(handler);
	}


	@Override
	public HandlerRegistration addDropHandler(DropHandler handler)
	{
		return listBox.addDropHandler(handler);
	}


	@Override
	public HandlerRegistration addGestureStartHandler(GestureStartHandler handler)
	{
		return listBox.addGestureStartHandler(handler);
	}


	@Override
	public HandlerRegistration addGestureChangeHandler(GestureChangeHandler handler)
	{
		return listBox.addGestureChangeHandler(handler);
	}


	@Override
	public HandlerRegistration addGestureEndHandler(GestureEndHandler handler)
	{
		return listBox.addGestureEndHandler(handler);
	}


	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler)
	{
		return listBox.addKeyUpHandler(handler);
	}


	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler)
	{
		return listBox.addKeyDownHandler(handler);
	}


	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler)
	{
		return listBox.addKeyPressHandler(handler);
	}


	@Override
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler)
	{
		return listBox.addMouseMoveHandler(handler);
	}


	@Override
	public DirectionEstimator getDirectionEstimator()
	{
		return listBox.getDirectionEstimator();
	}


	@Override
	public void setDirectionEstimator(boolean enabled)
	{
		listBox.setDirectionEstimator(enabled);
	}


	@Override
	public void setDirectionEstimator(DirectionEstimator directionEstimator)
	{
		listBox.setDirectionEstimator(directionEstimator);
	}
}
