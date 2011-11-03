package org.cruxframework.cruxsite.client.widget;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;

class FadeTransitionAnimation extends TransitionAnimation
{
	public FadeTransitionAnimation(Element leavingElement, Element enteringElement, int transitionDuration)
	{
		super(leavingElement, enteringElement, transitionDuration);
	}
 
	@Override
	protected void startTransitionForLeavingElement(Element leavingElement, Style leavingElementStyle) 
	{
		leavingElement.getStyle().setOpacity(1);
		leavingElementStyle.setLeft(0, Unit.PX);
	}

	@Override
	protected void startTransitionForEnteringElement(Element enteringElement, Style enteringElementStyle) 
	{
		enteringElementStyle.setOpacity(0);
		enteringElementStyle.setLeft(0, Unit.PX);
	}

	@Override
	protected void updateLeavingElement(Element leavingElement, Style leavingElementStyle, double progress) 
	{
		if(progress <= .5)
		{
			leavingElementStyle.setOpacity(1 - (2 * progress));
		}
	}

	@Override
	protected void updateEnteringElement(Element enteringElement, Style enteringElementStyle, double progress) 
	{
		if(progress > .5)
		{
			enteringElementStyle.setOpacity(2 * progress - 1);
		}
	}

	@Override
	protected void completeTransitionForEnteringElement(Element enteringElement, Style enteringElementStyle) 
	{
		enteringElementStyle.setOpacity(1);
		enteringElementStyle.setLeft(0, Unit.PX);
	}

	@Override
	protected void completeTransitionForLeavingElement(Element leavingElement, Style leavingElementStyle) 
	{
		leavingElementStyle.setOpacity(0);
		leavingElementStyle.setLeft(0, Unit.PX);
	}
}