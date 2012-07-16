package org.cruxframework.crux.crossdevice.client.slidingdeck;

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
	private AnimationManager animationManager =  new AnimationManager(400);

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
		panel.getElement().getStyle().setOpacity(0);
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
				animationManager.slide(mainContainer.getWidget(visibleWidget).getElement(), mainContainer.getWidget(index).getElement(), slideToRight);
			}
		}
		else
		{
			animationManager.fade(null, mainContainer.getWidget(index).getElement());
		}

		visibleWidget = index;
	}

	public void showWidget(int index)
	{
		Widget newWidget = mainContainer.getWidget(index);

		if(getWidgetCount() >= 2 && visibleWidget >= 0)
		{
			if(index != visibleWidget)
			{
				Widget oldWidget = mainContainer.getWidget(visibleWidget);
				animationManager.fade(oldWidget.getElement(), newWidget.getElement());
			}
		}
		else
		{
			animationManager.fade(null, newWidget.getElement());
		}

		visibleWidget = index;
	}

	public void setTransitionDuration(int transitionDuration)
	{
		animationManager.setTransitionDuration(transitionDuration);
	}

	public int getTransitionDuration()
	{
		return animationManager.getTransitionDuration();
	}
}