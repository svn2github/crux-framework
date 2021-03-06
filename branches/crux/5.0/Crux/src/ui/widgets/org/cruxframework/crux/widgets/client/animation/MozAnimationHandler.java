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
		if(widget == null)
		{
			return;
		}
		
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
		if(widget == null)
		{
			return;
		}
		resetTransition(widget.getElement());
	}

	@Override
	public void translateX(Widget widget, int diff, int duration, Callback callback)
	{
		if(widget == null)
		{
			return;
		}
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
		if(widget == null)
		{
			return;
		}
		setHeight(widget, height+"px", duration, callback);
    }

	@Override
    public void setHeight(Widget widget, String height, int duration, final Callback callback)
    {
		if(widget == null)
		{
			return;
		}
		final Element element = widget.getElement();
		addCallbackHandler(element, new Callback()
		{
			@Override
			public void onTransitionCompleted()
			{
				clearTransitionProperties(element);
				if (callback != null)
				{
					callback.onTransitionCompleted();
				}
			}
		});
		setHeight(element, height, duration);
    }

	@Override
	public void hideBackface(Widget widget)
	{
		if(widget == null)
		{
			return;
		}
		widget.getElement().getStyle().setProperty("mozBackfaceVisibility", "hidden");
	}
	
	@Override
	public void fade(Widget outWidget, Widget inWidget, int duration, final Callback callback)
	{
		if(outWidget == null || inWidget == null)
		{
			return;
		}
		final Element outElement = outWidget.getElement();
		final Element inElement = inWidget.getElement();
		addCallbackHandler(outElement, new Callback()
		{
			@Override
			public void onTransitionCompleted()
			{
				clearTransitionProperties(outElement);
			}
		});
		addCallbackHandler(inElement, new Callback()
		{
			@Override
			public void onTransitionCompleted()
			{
				clearTransitionProperties(inElement);
				if (callback != null)
				{
					callback.onTransitionCompleted();
				}
			}
		});
		fadeOut(outElement, (duration/2.0));
		fadeIn(inElement, (duration/2.0), (duration/2.0));
	}
	
	@Override
	public void fadeOut(Widget outWidget, int duration, final Callback callback)
	{
		if(outWidget == null)
		{
			return;
		}
		final Element outElement = outWidget.getElement();
		addCallbackHandler(outElement, new Callback()
		{
			@Override
			public void onTransitionCompleted()
			{
				clearTransitionProperties(outElement);
				if (callback != null)
				{
					callback.onTransitionCompleted();
				}
			}
		});
		fadeOut(outElement, duration);
	}

	@Override
	public void fadeIn(Widget inWidget, int duration, final Callback callback)
	{
		if(inWidget == null)
		{
			return;
		}
		final Element inElement = inWidget.getElement();
		addCallbackHandler(inElement, new Callback()
		{
			@Override
			public void onTransitionCompleted()
			{
				clearTransitionProperties(inElement);
				if (callback != null)
				{
					callback.onTransitionCompleted();
				}
			}
		});
		fadeIn(inElement, duration, 0);
	}

	@Override
	public void clearFadeTransitions(Widget widget)
	{
		if(widget == null)
		{
			return;
		}
		widget.getElement().getStyle().setOpacity(1);
	}
	
	private native void fadeOut(Element el, double duration)/*-{
		el.style.MozTransitionProperty = 'opacity';
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
	
		el.style.opacity = 0;
	}-*/;

	private native void fadeIn(Element el, double duration, double delay)/*-{
		el.style.MozTransitionProperty = 'opacity';
		if (duration == 0)
		{
			el.style.MozTransitionDelay = '0';
			el.style.MozTransitionDuration = '';
			el.style.MozTransitionTimingFunction = '';
		}
		else
		{
			el.style.MozTransitionDelay = ''+delay;
			el.style.MozTransitionDuration = duration+'ms';
			el.style.MozTransitionTimingFunction = 'ease-out';
		}
	
		el.style.opacity = 1;
	}-*/;

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

	private native void clearTransitionProperties(Element el)/*-{
		el.style.MozTransitionProperty = 'all';
		el.style.MozTransitionDuration = '';
		el.style.MozTransitionTimingFunction = '';
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