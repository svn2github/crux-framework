package org.cruxframework.crux.widgets.client.animation;

import com.google.gwt.user.client.Element;

/**
 * @author Gesse Dafe
 */
public class WebkitHorizontalSlidingSwapAnimation extends HorizontalSlidingSwapAnimation
{
	public WebkitHorizontalSlidingSwapAnimation(Element entering, Element leaving, int delta, Direction orientation) 
	{
		super(entering, leaving, delta, orientation);
	}

	@Override
	protected native void beforeStart(Element entering, Element leaving) /*-{
		entering.style.webkitTransitionProperty = 'left';
		leaving.style.webkitTransitionProperty = 'left';
	}-*/;

	@Override
	protected native void setPosition(Element target, int finalPosition, int durationMillis) /*-{
		target.style.webkitTransitionDuration = durationMillis + 'ms';
		target.style.left = finalPosition + 'px';
	}-*/;
}