package org.cruxframework.crux.widgets.client.animation;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;

/**
 * @author Gesse Dafe
 */
public abstract class SlidingSwapAnimation extends SwapAnimation
{
	private final int orientation;
	private final int delta;
	private int originalEnteringPos;
	private int originalLeavingPos;
	private int enteringFinalPos;
	private int leavingFinalPos;
	private Timer finishTimer;
	public static enum Direction {FORWARD, BACKWARDS}

	public SlidingSwapAnimation(Element entering, Element leaving, int delta, Direction direction) 
	{
		super(entering, leaving);
		this.orientation = direction.equals(Direction.FORWARD) ? -1 : 1;
		this.delta = delta * orientation;
	}

	@Override
	protected void doStart() 
	{
		if(this.finishTimer != null)
		{
			finishTimer.cancel();
		}
		
		int durationMillis = getDurationMillis();
		beforeStart(getEntering(), getLeaving());
		
		originalEnteringPos = getOriginalPosition(getEntering());
		originalLeavingPos = getOriginalPosition(getLeaving());
		enteringFinalPos = originalEnteringPos + delta;
		leavingFinalPos = originalLeavingPos + delta;
		
		setPosition(getEntering(), enteringFinalPos, durationMillis);
		setPosition(getLeaving(), leavingFinalPos, durationMillis);
		
		this.finishTimer = new Timer() 
		{
			public void run() 
			{
				finish();
			}
		};
		
		this.finishTimer.schedule(durationMillis);
	}

	@Override
	public void finish() 
	{
		setPosition(getEntering(), enteringFinalPos, 0);
		setPosition(getLeaving(), leavingFinalPos, 0);
	}
	
	@Override
	public void cancel() 
	{
		if(this.finishTimer != null)
		{
			finishTimer.cancel();
		}
		
		setPosition(getEntering(), originalEnteringPos, 0);
		setPosition(getLeaving(), originalLeavingPos, 0);
	}

	protected abstract void beforeStart(Element entering, Element leaving);
	protected abstract void setPosition(Element target, int finalPosition, int durationMillis);
	protected abstract int getOriginalPosition(Element element);
}