package org.cruxframework.crux.widgets.client.animation;

import com.google.gwt.user.client.Element;

/**
 * Base class for animate swaping 
 * @author Gesse Dafe
 */
public abstract class SwapAnimation 
{
	private final Element entering;
	private final Element leaving;
	private int durationMillis;

	public SwapAnimation(Element entering, Element leaving)
	{
		this.entering = entering;
		this.leaving = leaving;
	}
	
	public void start(int durationMillis)
	{
		this.durationMillis = durationMillis;
		doStart();
	}

	public abstract void cancel();
	public abstract void finish();
	protected abstract void doStart();

	protected int getDurationMillis() 
	{
		return durationMillis;
	}

	protected Element getEntering() 
	{
		return entering;
	}
	
	protected Element getLeaving() 
	{
		return leaving;
	}
}
