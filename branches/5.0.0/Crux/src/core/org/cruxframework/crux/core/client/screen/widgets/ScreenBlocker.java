package org.cruxframework.crux.core.client.screen.widgets;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ScreenBlocker extends Composite
{
	private ScreenBlockerImpl screenBlockerImpl;
	
	public static class ScreenBlockerImpl extends Composite implements TouchStartHandler, IsWidget
	{
		protected SimplePanel wrapper;
		
		public ScreenBlockerImpl() 
		{
			wrapper = new SimplePanel();
			initWidget(wrapper);
		}
		
		@Override
		public void onTouchStart(TouchStartEvent event) 
		{
			event.preventDefault();
		}
	}
	
	public ScreenBlocker(String blockingDivStyleName)
	{
		screenBlockerImpl = GWT.create(ScreenBlockerImpl.class);
		screenBlockerImpl.setStyleName(blockingDivStyleName);
		initWidget(screenBlockerImpl);
	}
	
	private static void expandElementToScreen(Widget widget)
	{
		Style style = widget.getElement().getStyle();
		style.setProperty("position", "absolute");
		style.setPropertyPx("top", 0);
		style.setPropertyPx("left", 0);
		style.setProperty("width", "100%");
		style.setProperty("height", "100%");
	}
	
	public static class ScreenBlockerOtherBrowsers extends ScreenBlockerImpl
	{
		protected ScreenBlockerOtherBrowsers()
		{
			SimplePanel blockingDiv = new SimplePanel();
			expandElementToScreen(blockingDiv);
			wrapper.add(blockingDiv);
		}
	}
	
	public static class ScreenBlockerIE6_7 extends ScreenBlockerImpl
	{
		protected ScreenBlockerIE6_7()
		{
			Frame blockingFrame = new Frame();
			expandElementToScreen(blockingFrame);
			blockingFrame.getElement().getStyle().setProperty("opacity", "0.0");
			blockingFrame.getElement().getStyle().setProperty("filter", "alpha(opacity=0)");
			wrapper.add(blockingFrame);
		}
	}
}
