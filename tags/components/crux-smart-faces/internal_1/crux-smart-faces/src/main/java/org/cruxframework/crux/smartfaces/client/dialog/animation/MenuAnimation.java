/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.smartfaces.client.dialog.animation;

import org.cruxframework.crux.core.client.css.animation.Animation;
import org.cruxframework.crux.core.client.css.animation.StandardAnimation;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 */
public abstract class MenuAnimation
{
	private Animation<?> entrance = getEntranceAnimation();
	private Animation<?> exit = getExitAnimation();

	public void animateExit(Widget widget, Animation.Callback callback)
	{
		exit.animate(widget, callback);
	}
	
	public void animateEntrance(Widget widget, Animation.Callback callback)
	{
		entrance.animate(widget, callback);
	}
	
	protected abstract Animation<?> getExitAnimation();
	protected abstract Animation<?> getEntranceAnimation();
	
	public static MenuAnimation bounce = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.bounceIn);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOut);
		}
	};

	public static MenuAnimation bounceUpDown = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.bounceInDown);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutUp);
		}
	};

	public static MenuAnimation bounceLeft = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.bounceInLeft);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutLeft);
		}
	};

	public static MenuAnimation bounceRight = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.bounceInRight);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutRight);
		}
	};

	public static MenuAnimation bounceDownUp = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.bounceInUp);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutDown);
		}
	};

	public static MenuAnimation fade = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeIn);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOut);
		}
	};

	public static MenuAnimation fadeDownUp = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInDown);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutUp);
		}
	};

	public static MenuAnimation fadeUpDown = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInUp);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutDown);
		}
	};

	public static MenuAnimation fadeLeft = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInLeft);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutLeft);
		}
	};

	public static MenuAnimation fadeRight = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInRight);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutRight);
		}
	};
	
	public static MenuAnimation fadeDownUpBig = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInDownBig);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutUpBig);
		}
	};

	public static MenuAnimation fadeUpDownBig = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInUpBig);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutDownBig);
		}
	};

	public static MenuAnimation fadeLeftBig = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInLeftBig);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutLeftBig);
		}
	};

	public static MenuAnimation fadeRightBig = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInRightBig);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutRightBig);
		}
	};	

	public static MenuAnimation flipX = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.flipInX);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.flipOutX);
		}
	};	

	public static MenuAnimation flipY = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.flipInY);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.flipOutY);
		}
	};	

	public static MenuAnimation lightSpeed = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.lightSpeedIn);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.lightSpeedOut);
		}
	};	

	public static MenuAnimation rotate = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.rotateIn);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOut);
		}
	};	

	public static MenuAnimation rotateDownLeft = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.rotateInDownLeft);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOutUpLeft);
		}
	};	

	public static MenuAnimation rotateDownRight = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.rotateInDownRight);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOutUpRight);
		}
	};	

	public static MenuAnimation rotateUpLeft = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.rotateInUpLeft);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOutDownLeft);
		}
	};	

	public static MenuAnimation rotateUpRight = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.rotateInUpRight);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOutDownRight);
		}
	};	

	public static MenuAnimation slideDown = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.slideInDown);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.slideOutUp);
		}
	};	

	public static MenuAnimation slideLeft = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.slideInLeft);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.slideOutLeft);
		}
	};	

	public static MenuAnimation slideRight = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.slideInRight);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.slideOutRight);
		}
	};	

	public static MenuAnimation roll = new MenuAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.rollIn);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rollOut);
		}
	};	
}
