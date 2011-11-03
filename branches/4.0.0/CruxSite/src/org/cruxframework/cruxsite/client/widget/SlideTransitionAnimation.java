package org.cruxframework.cruxsite.client.widget;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;

class SlideTransitionAnimation extends TransitionAnimation
{
	private boolean rightToLeft;
	private int leavingElementWidth;
 
	public SlideTransitionAnimation(Element leavingElement, Element enteringElement, int transitionDuration, boolean rightToLeft)
	{
		super(leavingElement, enteringElement, transitionDuration);
		Element reference = leavingElement != null ? leavingElement : enteringElement;
		leavingElementWidth = reference.getParentElement().getClientWidth();
		this.rightToLeft = rightToLeft;
	}

	@Override
	protected void completeTransitionForEnteringElement(Element enteringElement, Style enteringElementStyle) 
	{
		enteringElementStyle.setLeft(0, Unit.PX);
		enteringElementStyle.setDisplay(Display.BLOCK);
	}

	@Override
	protected void completeTransitionForLeavingElement(Element leavingElement, Style leavingElementStyle) 
	{
		leavingElementStyle.setDisplay(Display.NONE);
		leavingElementStyle.setLeft(0, Unit.PX);
	}

	@Override
	protected void startTransitionForLeavingElement(Element leavingElement, Style leavingElementStyle) 
	{
		leavingElementStyle.setLeft(0, Unit.PX);
		leavingElementStyle.setDisplay(Display.BLOCK);
	}

	@Override
	protected void startTransitionForEnteringElement(Element enteringElement, Style enteringElementStyle) 
	{
		if(rightToLeft)
		{
			enteringElementStyle.setLeft(leavingElementWidth + 1, Unit.PX);
		}
		else
		{
			enteringElementStyle.setLeft(0 - leavingElementWidth - 1, Unit.PX);
		}
		
		enteringElementStyle.setDisplay(Display.BLOCK);
	}

	@Override
	protected void updateLeavingElement(Element leavingElement, Style leavingElementStyle, double progress) 
	{
		if(rightToLeft)
		{
			double leavingElemLeft = 0 - progress * leavingElementWidth;
			leavingElementStyle.setLeft(leavingElemLeft, Unit.PX);
		}
		else
		{
			double leavingElemLeft = progress * leavingElementWidth;
			leavingElementStyle.setLeft(leavingElemLeft, Unit.PX);
		}
	}

	@Override
	protected void updateEnteringElement(Element enteringElement, Style enteringElementStyle, double progress) 
	{
		if(rightToLeft)
		{
			double leavingElemLeft = 0 - progress * leavingElementWidth;
			double enteringElementLeft = leavingElementWidth + leavingElemLeft + 1;
			enteringElementStyle.setLeft(enteringElementLeft, Unit.PX);
		}
		else
		{
			double leavingElemLeft = progress * leavingElementWidth;
			double enteringElementLeft = leavingElemLeft - leavingElementWidth;
			enteringElementStyle.setLeft(enteringElementLeft, Unit.PX);
		}
	}
}