package org.cruxframework.crux.core.client.screen.eventadapter;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implementation of Google FastButton {@link http://code.google.com/mobile/articles/fast_buttons.html}
 * @author Thiago da Rosa de Bustamante
 * @author Gesse Dafe
 *
 */
public class TapEventAdapter extends Composite 
{
	private boolean touchHandled = false;
	private int startY;
	private int startX;
//	private static boolean ghostEventsHandlerCreated = false;
//	private static FastList<Coordinate> ghostEventsCoordinates = new FastList<TapEventAdapter.Coordinate>();
//	
//	private static class Coordinate
//	{
//		final int x;
//		final int y;
//
//		Coordinate(int x, int y)
//        {
//			this.x = x;
//			this.y = y;
//        }
//	}
	
	public TapEventAdapter(Widget child)
	{
		// TODO - messages
		assert (child instanceof HasAllTouchHandlers) : "";
		assert (child instanceof HasClickHandlers) : "";
		initWidget(child);
		sinkEvents(Event.TOUCHEVENTS | Event.ONCLICK);
//		if (!ghostEventsHandlerCreated)
//		{
//			createGhostEventsHandler();
//		}
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
		
		if(touchHandled)
		{
			Window.alert("click via touch: "+ this.toString());
			touchHandled = false;
			super.onBrowserEvent(event);
		}
		else Window.alert("click nativo: "+ this.toString());
	}

	private void onTouchEnd(Event event) 
	{
		event.stopPropagation();
		touchHandled = true;
		fireClick();
//		preventGhostClick(startX, startY);
	}

	private void onTouchStart(Event event) 
	{
		event.stopPropagation();

		this.startX = event.getTouches().get(0).getClientX();
		this.startY = event.getTouches().get(0).getClientY();		
	}

	/**
	 * @param executor
	 * @return
	 */
	private void fireClick() 
	{
		NativeEvent evt = Document.get().createClickEvent(1, 0, 0, 0, 0, false,
				false, false, false);
		getElement().dispatchEvent(evt);
	}
	
//	private void createGhostEventsHandler()
//    {
//		ceateGhostClickEventsHandlerFunction();
//	    ghostEventsHandlerCreated = true;
//    }
//	
//	private native void ceateGhostClickEventsHandlerFunction()/*-{
//		var f = function(event) {
//			@org.cruxframework.crux.core.client.screen.eventadapter.TapEventAdapter::handleGhostEvents(Lcom/google/gwt/dom/client/NativeEvent;)(event);
//		}
//		$doc.addEventListener('click', f, true);
//	}-*/;
//	
//	private void handleGhostEvents(NativeEvent event)
//	{
//		int size = ghostEventsCoordinates.size();
//		for (int i= 0; i< size; i++)
//		{
//			Coordinate coordinate = ghostEventsCoordinates.get(i);
//		    if (Math.abs(event.getClientX() - coordinate.x) < 25 && Math.abs(event.getClientY() - coordinate.y) < 25) 
//		    {
//		        event.stopPropagation();
//		        event.preventDefault();
//		    }			
//		}
//	}
//	
//	private void preventGhostClick(int x, int y)
//	{
//		ghostEventsCoordinates.add(new Coordinate(x, y));
//		new Timer()
//		{
//			@Override
//			public void run()
//			{
//				popCoordinate();
//			}
//		}.schedule(2500);
//	}
//	
//	private void popCoordinate()
//	{
//		int size = ghostEventsCoordinates.size();
//		if (size>0)
//		{
//			ghostEventsCoordinates.remove(size-1);
//		}
//	}
}
