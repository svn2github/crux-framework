package org.cruxframework.crux.crossdevice.client;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.client.utils.StyleUtils;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

@Controller("storyboardSmallController")
public class StoryboardSmallController extends DeviceAdaptiveController implements Storyboard
{
	protected FlowPanel storyboard;
	protected String itemHeight;
	protected String itemWidth;

	@Override
	public Widget getWidget(int index)
	{
		return ((FocusPanel)storyboard.getWidget(index)).getWidget();
	}

	public void add(Widget widget)
	{
		storyboard.add(createFocusPanelForCell(widget));
	}

	@Override
	public int getWidgetCount()
	{
		return storyboard.getWidgetCount();
	}

	@Override
	public int getWidgetIndex(Widget child)
	{
		int count = getWidgetCount();
		for (int i=0; i< count; i++)
		{
			if (getWidget(i).equals(child))
			{
				return i;
			}
		}
		return -1;
	}

	@Override
	public boolean remove(int index)
	{
		return storyboard.remove(index);
	}

	@Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<Integer> handler)
    {
		 return storyboard.addHandler(handler, SelectionEvent.getType());
	}

	@Override
    public void fireEvent(GwtEvent<?> event)
    {
		storyboard.fireEvent(event);
    }

	protected FocusPanel createFocusPanelForCell(Widget widget)
	{
		final FocusPanel panel = new FocusPanel();
		panel.add(widget);
		panel.setStyleName("item");
		if (!StringUtils.isEmpty(itemHeight))
		{
			panel.setHeight(itemHeight);
		}
		
		if (!StringUtils.isEmpty(itemWidth))
		{
			panel.setWidth(itemWidth);
		}

		panel.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				int index = storyboard.getWidgetIndex(panel);
			    SelectionEvent.fire(StoryboardSmallController.this, index);
			}
		});
		return panel;
	}
		
	protected FlowPanel getStoryboard()
	{
		return (FlowPanel) getChildWidget("storyboard");
	}

	@Override
	protected void init()
    {
		storyboard = getStoryboard();
		this.itemHeight = "75px";
    }
	
	@Override
	protected void initWidgetDefaultStyleName()
	{
		setStyleName("xdev-Storyboard");
	}

	@Override
	protected void applyWidgetDependentStyleNames()
	{
		StyleUtils.addStyleDependentName(getElement(), DeviceAdaptive.Size.small.toString());
		StyleUtils.addStyleDependentName(getElement(), DeviceAdaptive.Feature.touch.toString());
	}

	@Override
    public String getLargeDeviceItemWidth()
    {
	    return null;
    }

	@Override
    public void setLargeDeviceItemWidth(String width)
    {
    }

	@Override
    public String getSmallDeviceItemHeight()
    {
	    return this.itemHeight;
    }

	@Override
    public void setSmallDeviceItemHeight(String height)
    {
		this.itemHeight = height;	    
    }

	@Override
    public String getLargeDeviceItemHeight()
    {
	    return null;
    }

	@Override
    public void setLargeDeviceItemHeight(String height)
    {
    }
}
