package org.cruxframework.cruxsite.client.widget;

import com.google.gwt.user.client.Element;

class AnimationManager
{
	private SlideTransitionAnimation slideAnimation;
	private FadeTransitionAnimation fadeAnimation;
	private int transitionDuration = 1000;
	
	AnimationManager(int transitionDuration) 
	{
		this.transitionDuration = transitionDuration;
	}
	
	void slide(Element leavingElement, Element enteringElement, boolean rightToLeft) 
	{
		if(fadeAnimation != null)
		{
			fadeAnimation.cancel();
		}
		
		if(slideAnimation != null)
		{
			slideAnimation.cancel();
		}
		
		slideAnimation = new SlideTransitionAnimation(leavingElement, enteringElement, transitionDuration, rightToLeft);
		slideAnimation.transite();
	}
	
	void fade(Element leavingElement, Element enteringElement) 
	{
		if(slideAnimation != null)
		{
			slideAnimation.cancel();
		}
		
		if(fadeAnimation != null)
		{
			fadeAnimation.cancel();
		}

		fadeAnimation = new FadeTransitionAnimation(leavingElement, enteringElement, transitionDuration);
		fadeAnimation.transite();
	}

	void setTransitionDuration(int transitionDuration) 
	{
		this.transitionDuration = transitionDuration;
	}
}