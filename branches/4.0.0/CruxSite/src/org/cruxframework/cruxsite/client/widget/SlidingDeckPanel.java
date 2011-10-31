package org.cruxframework.cruxsite.client.widget;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.user.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class SlidingDeckPanel extends Composite
{
	private FlowPanel mainContainer;
	private int visibleWidget = -1;
	private WindowSlideAnimation animation;
	
	public SlidingDeckPanel() 
	{
		mainContainer = new FlowPanel();
		
		initWidget(mainContainer);
		
		getElement().getStyle().setOverflow(Overflow.HIDDEN);
		getElement().getStyle().setPosition(Position.RELATIVE);
	}


	public void add(Widget w) 
	{
		SimplePanel panel = new SimplePanel();
		panel.setVisible(false);
		mainContainer.add(panel);

		panel.setHeight("100%");
		panel.setWidth("100%");
		panel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		panel.getElement().getStyle().setPosition(Position.ABSOLUTE);
		panel.getElement().getStyle().setTop(0, Unit.PX);
		panel.getElement().getStyle().setLeft(0, Unit.PX);

		panel.add(w);
	}

	public Widget getWidget(int index) 
	{
		SimplePanel panel = (SimplePanel) mainContainer.getWidget(index);
		if(panel != null)
		{
			return panel.getWidget();
		}
		return null;
	}


	public int getWidgetCount() 
	{
		return mainContainer.getWidgetCount();
	}

	public int getWidgetIndex(Widget child) 
	{
		return mainContainer.getWidgetIndex(child.getParent());
	}

	public boolean remove(int index) 
	{
		return mainContainer.remove(index);
	}


	public int getVisibleWidget() 
	{
		return visibleWidget;
	}


	public void showWidget(int index, boolean slideToRight) 
	{
		if(getWidgetCount() >= 2 && visibleWidget >= 0)
		{
			if(index != visibleWidget)
			{
				if(animation != null)
				{
					animation.cancel();
				}
				
				animation = new WindowSlideAnimation(mainContainer.getWidget(visibleWidget).getElement(), mainContainer.getWidget(index).getElement());
				animation.move(slideToRight, 150);
			}
		}
		else
		{
			Widget newWidget = mainContainer.getWidget(index);
			newWidget.setVisible(true);
		}
		
		visibleWidget = index;
	}
	
	public void showWidget(int index) 
	{
		if(getWidgetCount() >= 2 && visibleWidget >= 0)
		{
			if(index != visibleWidget)
			{
				Widget newWidget = mainContainer.getWidget(index);
				newWidget.setVisible(true);
				
				Widget oldWidget = mainContainer.getWidget(visibleWidget);
				oldWidget.setVisible(false);
			}
		}
		else
		{
			Widget newWidget = mainContainer.getWidget(index);
			newWidget.setVisible(true);
		}

		visibleWidget = index;
	}
	
	public static class WindowSlideAnimation extends Animation
	{
		private final Style outElemStyle;
		private final Style inElemStyle;
		private boolean toLeft;
		private final Element outElem;
		private final Element inElem;
		private int outWidth;
	 
		public WindowSlideAnimation(Element outElem, Element inElem)
		{
			this.outElem = outElem;
			this.inElem = inElem;
			this.outElemStyle = outElem.getStyle();
			this.inElemStyle = inElem.getStyle();
			
		}
	 
		public void move(boolean toLeft, int milliseconds)
		{
			this.toLeft = toLeft;
			this.outWidth = outElem.getClientWidth();
			
			outElemStyle.setLeft(0, Unit.PX);
			
			if(toLeft)
			{
				inElemStyle.setLeft(outWidth + 1, Unit.PX);
			}
			else
			{
				inElemStyle.setLeft(0 - outWidth - 1, Unit.PX);
			}
			
			outElem.getStyle().setDisplay(Display.BLOCK);
			inElem.getStyle().setDisplay(Display.BLOCK);
			
			run(milliseconds);
		}
	 
		@Override
		protected void onUpdate(double progress)
		{
			if(toLeft)
			{
				double outLeft = 0 - progress * outWidth;
				outElemStyle.setLeft(outLeft, Unit.PX);

				double inLeft = outWidth + outLeft + 1;
				inElemStyle.setLeft(inLeft, Unit.PX);
			}
			else
			{
				double outLeft = progress * outWidth;
				outElemStyle.setLeft(outLeft, Unit.PX);
				
				double inLeft = outLeft - outWidth;
				inElemStyle.setLeft(inLeft, Unit.PX);
				
				//inElemStyle.setDisplay(Display.NONE);
			}
		}	
	 
		@Override
		protected void onComplete()
		{
			super.onComplete();
			outElem.getStyle().setDisplay(Display.NONE);
			inElem.getStyle().setLeft(0, Unit.PX);
		}
	}

}
