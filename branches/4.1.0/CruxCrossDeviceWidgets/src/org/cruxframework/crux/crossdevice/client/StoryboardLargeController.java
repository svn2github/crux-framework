package org.cruxframework.crux.crossdevice.client;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.utils.StyleUtils;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

@Controller("storyboardLargeController")
public class StoryboardLargeController extends StoryboardSmallController
{
	@Override
	protected void init()
    {
		super.init();
		this.itemHeight = "200px";
		this.itemWidth = "200px";
    }

	@Override
	protected FocusPanel createFocusPanelForCell(Widget widget)
	{
	    final FocusPanel panel = super.createFocusPanelForCell(widget);
	    panel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
	    
//   Para o IE, testar
//	    display:-moz-inline-stack;
//	    display:inline-block;
//	    zoom:1;
//	    *display:inline;


		return panel;
	}

	@Override
	protected void applyWidgetDependentStyleNames()
	{
		StyleUtils.addStyleDependentName(getElement(), DeviceAdaptive.Size.large.toString());
	}	
	
	@Override
    public String getLargeDeviceItemWidth()
    {
	    return this.itemWidth;
    }

	@Override
    public void setLargeDeviceItemWidth(String width)
    {
		this.itemWidth = width;
    }

	@Override
    public String getSmallDeviceItemHeight()
    {
	    return null;
    }

	@Override
    public void setSmallDeviceItemHeight(String height)
    {
    }

	@Override
    public String getLargeDeviceItemHeight()
    {
	    return this.itemHeight;
    }

	@Override
    public void setLargeDeviceItemHeight(String height)
    {
		this.itemHeight = height;
    }	
}
