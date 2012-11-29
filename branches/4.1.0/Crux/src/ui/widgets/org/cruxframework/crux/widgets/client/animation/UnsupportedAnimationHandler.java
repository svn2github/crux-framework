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
package org.cruxframework.crux.widgets.client.animation;

import org.cruxframework.crux.widgets.client.animation.Animation.AnimationHandler;
import org.cruxframework.crux.widgets.client.animation.Animation.Callback;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class UnsupportedAnimationHandler implements AnimationHandler
{
	private Animation animation;

	@Override
	public void translateX(Widget widget, int diff, Callback callback)
	{
		Element element = widget.getElement();
		if (!hasOriginalLeft(element))
		{
			setOriginalLeft(element, element.getOffsetLeft());
		}
		else
		{
			diff = getOriginalLeft(element) + diff;
		}
		
		setLeft(element, diff);
		if (callback != null)
		{
			callback.onTransitionCompleted();
		}
	}
	
	@Override
	public void resetTransition(Widget widget)
	{
		Element element = widget.getElement();
		if (hasOriginalLeft(element))
		{
			setLeft(element, getOriginalLeft(element));
			clearOriginalLeft(element);
		}
	}

	@Override
	public void translateX(Widget widget, final int diff, int duration, final Callback callback)
	{
		translateX(widget, diff, callback);
	}
		
	@Override
    public void setHeight(Widget widget, int height, int duration, Callback callback)
    {
		setHeight(widget, height+"px", duration, callback);
    }

	@Override
    public void setHeight(Widget widget, String height, int duration, Callback callback)
    {
		widget.setHeight(height);
		if (callback != null)
		{
			callback.onTransitionCompleted();
		}
    }
	
	@Override
    public void fade(Widget outWidget, Widget inWidget, int duration, Callback callback)
    {
		outWidget.getElement().getStyle().setOpacity(0);
		inWidget.getElement().getStyle().setOpacity(1);
		if (callback != null)
		{
			callback.onTransitionCompleted();
		}
    }
	
	@Override
    public void fadeOut(Widget outWidget, int duration, Callback callback)
    {
		outWidget.getElement().getStyle().setOpacity(0);
		if (callback != null)
		{
			callback.onTransitionCompleted();
		}
    }

	@Override
    public void fadeIn(Widget inWidget, int duration, Callback callback)
    {
		inWidget.getElement().getStyle().setOpacity(1);
		if (callback != null)
		{
			callback.onTransitionCompleted();
		}
    }
	
	@Override
	public void clearFadeTransitions(Widget widget)
	{
		widget.getElement().getStyle().setOpacity(1);
	}

	@Override
	public void hideBackface(Widget widget)
	{
	}

	private void setLeft(Element element, double left)
    {
	    element.getStyle().setLeft(left, Unit.PX);
    }

	private native int getOriginalLeft(Element el)/*-{
		return el._originalLeft;
	}-*/;
	
	private native boolean hasOriginalLeft(Element el)/*-{
		return (el._originalLeft !== null);
	}-*/;

	private native void setOriginalLeft(Element el, int left)/*-{
		el._originalLeft = left;
	}-*/;

	private native void clearOriginalLeft(Element el)/*-{
		el._originalLeft = null;
	}-*/;
}