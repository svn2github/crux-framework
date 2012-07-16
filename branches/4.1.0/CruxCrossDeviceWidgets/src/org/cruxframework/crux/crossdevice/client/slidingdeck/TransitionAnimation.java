package org.cruxframework.crux.crossdevice.client.slidingdeck;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;

abstract class TransitionAnimation extends Animation
{
	private Style leavingElementStyle;
	private Style enteringElementStyle;
	private Element leavingElement;
	private Element enteringElement;
	private int transitionDuration = 1000;
 
	public TransitionAnimation(Element leavingElement, Element enteringElement, int transitionDuration)
	{
		this.transitionDuration = transitionDuration;
		
		this.leavingElement = leavingElement;
		if(this.leavingElement != null)
		{
			this.leavingElementStyle = leavingElement.getStyle();
		}
		
		this.enteringElement = enteringElement;
		if(this.enteringElement != null)
		{
			this.enteringElementStyle = enteringElement.getStyle();
		}
	}
	
	public void transite()
	{
		if(this.leavingElement != null)
		{
			startTransitionForLeavingElement(leavingElement, leavingElementStyle);
		}
		
		if(this.enteringElement != null)
		{
			startTransitionForEnteringElement(enteringElement, enteringElementStyle);
		}
		
		run(transitionDuration);
	}
	
	protected void onUpdate(double progress) 
	{
		if(this.leavingElement != null)
		{
			updateLeavingElement(leavingElement, leavingElementStyle, progress);
		}
		
		if(this.enteringElement != null)
		{
			updateEnteringElement(enteringElement, enteringElementStyle, progress);
		}
	}
	
	@Override
	protected void onComplete() 
	{
		if(leavingElement != null)
		{
			completeTransitionForLeavingElement(leavingElement, leavingElementStyle);
		}
		
		if(this.enteringElement != null)
		{
			completeTransitionForEnteringElement(enteringElement, enteringElementStyle);
		}
	}

	protected abstract void completeTransitionForEnteringElement(Element enteringElement, Style enteringElementStyle);
	
	protected abstract void completeTransitionForLeavingElement(Element leavingElement, Style leavingElementStyle);
	
	protected abstract void startTransitionForEnteringElement(Element enteringElement, Style enteringElementStyle);

	protected abstract void startTransitionForLeavingElement(Element leavingElement, Style leavingElementStyle);

	protected abstract void updateEnteringElement(Element enteringElement, Style enteringElementStyle, double progress);

	protected abstract void updateLeavingElement(Element leavingElement, Style leavingElementStyle, double progress);

}