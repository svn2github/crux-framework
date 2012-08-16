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

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class MozAnimationHandler implements AnimationHandler
{
	@Override
	public void translateX(Widget widget, int diff, Callback callback)
	{
		Element element = widget.getElement();
		if (callback != null)
		{
			addCallbackHandler(element, callback);
		}
		translateX(element, diff);
	}

	@Override
	public void resetTransition(Widget widget)
	{
		resetTransition(widget.getElement());
	}

	@Override
	public void translateX(Widget widget, int diff, int duration, Callback callback)
	{

		Element element = widget.getElement();
		if (callback != null)
		{
			addCallbackHandler(element, callback);
		}
		translateX(element, diff, duration);
	}
	
	@Override
    public void setHeight(Widget widget, int height, int duration, Callback callback)
    {
		Element element = widget.getElement();
		if (callback != null)
		{
			addCallbackHandler(element, callback);
		}
		setHeight(element, height+"px", duration);
    }

	private native void setHeight(Element el, String height, int duration)/*-{
		el.style.MozTransitionProperty = 'height';
		el.style.MozTransitionDelay = '0';
		if (duration == 0)
		{
			el.style.MozTransitionDuration = '';
			el.style.MozTransitionTimingFunction = '';
		}
		else
		{
			el.style.MozTransitionDuration = duration+'ms';
			el.style.MozTransitionTimingFunction = 'ease-out';
		}

		el.style.height = height;
	}-*/;


	private native void translateX(Element el, int diff)/*-{
		el.style.MozTransitionProperty = 'all';
		el.style.MozTransitionDuration = '';
		el.style.MozTransitionTimingFunction = '';
		el.style.MozTransitionDelay = '0';
		el.style.MozTransform = 'translate(' + diff + 'px,0px)';
	}-*/;

	private native void translateX(Element el, int diff, int duration)/*-{
		el.style.MozTransitionProperty = 'all';
		el.style.MozTransitionDelay = '0';
		if (duration == 0)
		{
			el.style.MozTransitionDuration = '';
			el.style.MozTransitionTimingFunction = '';
		}
		else
		{
			el.style.MozTransitionDuration = duration+'ms';
			el.style.MozTransitionTimingFunction = 'ease-out';
		}

		el.style.MozTransform = 'translate(' + diff + 'px,0px)';
	}-*/;

	private native void addCallbackHandler(Element el, Callback callback)/*-{
		var func;
		func = function(e) 
		{
			callback.@org.cruxframework.crux.widgets.client.animation.Animation.Callback::onTransitionCompleted()();
			el.removeEventListener('transitionend', func);
		};
			el.addEventListener('transitionend', func); 			
	}-*/;

	private native void resetTransition(Element el)/*-{
		el.style.MozTransform = 'translate(0px,0px)';
	}-*/;
}