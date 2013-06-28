package org.cruxframework.crux.widgets.client.storyboard;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;
import org.cruxframework.crux.widgets.client.selectablepanel.SelectablePanel;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
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
		return ((SimplePanel)storyboard.getWidget(index)).getWidget();
	}

	public void add(Widget widget)
	{
		storyboard.add(createClickablePanelForCell(widget));
	}
	
	@Override
    public void clear()
    {
		storyboard.clear();	    
    }

	@Override
    public Iterator<Widget> iterator()
    {
	    return new Iterator<Widget>()
		{
	    	private int index = -1;
	    	
			@Override
            public boolean hasNext()
            {
			      return index < (getWidgetCount() - 1);
            }

			@Override
            public Widget next()
			{
				if (index >= getWidgetCount()) 
				{
					throw new NoSuchElementException();
				}
				return getWidget(++index);
            }

			@Override
            public void remove()
            {
				if ((index < 0) || (index >= getWidgetCount())) 
				{
					throw new IllegalStateException();
				}
				StoryboardSmallController.this.remove(index--);
            }
		};
    }

	@Override
    public boolean remove(Widget w)
    {
	    int index = getWidgetIndex(w);
	    if (index >= 0)
	    {
	    	return remove(index);
	    }
	    return false;
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
		 return addHandler(handler, SelectionEvent.getType());
	}

	protected Widget createClickablePanelForCell(Widget widget)
	{
		final SelectablePanel panel = new SelectablePanel();
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

		panel.addSelectHandler(new SelectHandler()
		{
			@Override
			public void onSelect(SelectEvent event)
			{
				int index = storyboard.getWidgetIndex(panel);
			    SelectionEvent.fire(StoryboardSmallController.this, index);
			}
		});
		return panel;
	}
		
	@Override
	protected void init()
    {
		storyboard = getChildWidget("storyboard");
		this.itemHeight = "75px";
		this.itemHeight = "100%";
		setStyleName("crux-Storyboard");
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
