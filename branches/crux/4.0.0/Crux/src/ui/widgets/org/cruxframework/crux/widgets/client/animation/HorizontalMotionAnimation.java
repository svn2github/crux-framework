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

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
 
/**
 * Simple horizontal motion animation.
 * 
 * @author Gesse Dafe
 */
public class HorizontalMotionAnimation extends Animation
{
	private final Style style;
	private int startLeft;
	private int finalLeft;
 
	public HorizontalMotionAnimation(Element element)
	{
		this.style = element.getStyle();
	}
 
	public void move(int startLeft, int finalLeft, int milliseconds)
	{
		this.startLeft = startLeft;
		this.finalLeft = finalLeft;
		run(milliseconds);
	}
 
	@Override
	protected void onUpdate(double progress)
	{
		double left = startLeft + (progress * (finalLeft - startLeft));
 		this.style.setLeft(left, Unit.PX);
	}	
 
	@Override
	protected void onComplete()
	{
		super.onComplete();
 		this.style.setLeft(finalLeft, Unit.PX);
	}
}