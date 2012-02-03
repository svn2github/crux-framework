package org.cruxframework.crux.widgets.client.eventadapters;

import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class TapEventAdapter extends Composite 
{
	private boolean touchHandled = false;
	private Widget child;

	public TapEventAdapter(Widget child)
	{
		// TODO - messages
		assert (child instanceof HasAllTouchHandlers) : "";
		assert (child instanceof HasClickHandlers) : "";
		initWidget(child);
		sinkEvents(Event.TOUCHEVENTS | Event.ONCLICK);
	}
	
	@Override
	public void onBrowserEvent(Event event) 
	{
		switch (DOM.eventGetType(event)) 
		{
			case Event.ONTOUCHSTART:
			{
				onTouchStart(event);
				break;
			}
			case Event.ONTOUCHEND:
			{
				onTouchEnd(event);
				break;
			}
			case Event.ONCLICK:
			{
				onClick(event);
				return;
			}
		}
		
		super.onBrowserEvent(event);
	}

	private void onClick(Event event) 
	{
		event.stopPropagation();
		event.preventDefault();
		
		if(touchHandled)
		{
			touchHandled = false;
			super.onBrowserEvent(event);
		}
	}

	private void onTouchEnd(Event event) 
	{
		event.stopPropagation();
		event.preventDefault();
		touchHandled = true;
		fireClick(this.getElement());
	}

	private void onTouchStart(Event event) 
	{
		event.stopPropagation();
		event.preventDefault();
	}

	/**
	 * @param executor
	 * @return
	 */
	private static native void fireClick(Element elem) /*-{
		elem.click();
	}-*/;
}
