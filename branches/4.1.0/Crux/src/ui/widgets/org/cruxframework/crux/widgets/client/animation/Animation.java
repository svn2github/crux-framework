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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Animation
{
	private static AnimationHandler animationHandler = null;

	/**
	 * 
	 * @param widget
	 * @param diff
	 * @param callback
	 */
	public static void translateX(Widget widget, int diff, Callback callback)
	{
		getAnimationHandler().translateX(widget, diff, callback);
	}
	
	/**
	 * 
	 * @param widget
	 * @param diff
	 * @param duration
	 * @param callback
	 */
	public static void translateX(Widget widget, int diff, int duration, Callback callback)
	{
		getAnimationHandler().translateX(widget, diff, duration, callback);
	}
	
	/**
	 * 
	 * @param widget
	 * @param height
	 * @param duration
	 * @param callback
	 */
	public static void setHeight(Widget widget, String height, int duration, Callback callback)
	{
		getAnimationHandler().setHeight(widget, height, duration, callback);
	}
	
	/**
	 * 
	 * @param widget
	 * @param height
	 * @param duration
	 * @param callback
	 */
	public static void setHeight(Widget widget, int height, int duration, Callback callback)
	{
		getAnimationHandler().setHeight(widget, height, duration, callback);
	}

	/**
	 * 
	 * @param widget
	 */
	public static void resetTransition(Widget widget)
	{
		getAnimationHandler().resetTransition(widget);
	}
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static interface Callback
	{
		void onTransitionCompleted();
	}

	private static AnimationHandler getAnimationHandler()
	{
		if (animationHandler == null)
		{
			animationHandler = GWT.create(AnimationHandler.class);
		}
		return animationHandler;
	}
	
	static interface AnimationHandler
	{
		void translateX(Widget widget, int diff, Callback callback);
		void translateX(Widget widget, int diff, int duration, Callback callback);
		void setHeight(Widget widget, String height, int duration, Callback callback);
		void setHeight(Widget widget, int height, int duration, Callback callback);
		void resetTransition(Widget currentPanel);
		
	}

	static class WebkitAnimationHandler implements AnimationHandler
	{
		//_transitionEndLabel =  webkitTransitionEnd : transitionend
		//_transitionPrefix =  webkitTransition : MozTransition  : transition
		//_transformLabel =  webkitTransform : MozTransform  : transform


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
        public void setHeight(Widget widget, String height, int duration, Callback callback)
        {
			Element element = widget.getElement();
			if (callback != null)
			{
				addCallbackHandler(element, callback);
			}
			setHeight(element, height, duration);
        }
		
		@Override
        public void setHeight(Widget widget, int height, int duration, Callback callback)
        {
			setHeight(widget, height+"px", duration, callback);
        }

		private native void setHeight(Element el, String height, int duration)/*-{
			el.style.webkitTransitionProperty = 'height';
			el.style.webkitTransitionDelay = '0';
			if (duration == 0)
			{
				el.style.webkitTransitionDuration = '';
				el.style.webkitTransitionTimingFunction = '';
			}
			else
			{
				el.style.webkitTransitionDuration = duration+'ms';
				el.style.webkitTransitionTimingFunction = 'ease-out';
			}
	
			el.style.height = height;
		}-*/;


		private native void translateX(Element el, int diff)/*-{
			el.style.webkitTransitionProperty = 'all';//-webkit-transform
			el.style.webkitTransitionDuration = '';
			el.style.webkitTransitionTimingFunction = '';
			el.style.webkitTransitionDelay = '0';
			el.style.webkitTransform = 'translate3d(' + diff + 'px,0px,0px)';
		}-*/;

		private native void translateX(Element el, int diff, int duration)/*-{
			el.style.webkitTransitionProperty = 'all';//-webkit-transform
			el.style.webkitTransitionDelay = '0';
			if (duration == 0)
			{
				el.style.webkitTransitionDuration = '';
				el.style.webkitTransitionTimingFunction = '';
			}
			else
			{
				el.style.webkitTransitionDuration = duration+'ms';
				el.style.webkitTransitionTimingFunction = 'ease-out';
			}

			el.style.webkitTransform = 'translate3d(' + diff + 'px,0px,0px)';
		}-*/;

		private native void addCallbackHandler(Element el, Callback callback)/*-{
			var func;
			func = function(e) 
			{
				callback.@org.cruxframework.crux.widgets.client.animation.Animation.Callback::onTransitionCompleted()();
				el.removeEventListener('webkitTransitionEnd', func);
			};
 			el.addEventListener('webkitTransitionEnd', func); 			
		}-*/;

		private native void resetTransition(Element el)/*-{
			el.style.webkitTransform = 'translate3d(0px,0px,0px)';
		}-*/;
	}
}