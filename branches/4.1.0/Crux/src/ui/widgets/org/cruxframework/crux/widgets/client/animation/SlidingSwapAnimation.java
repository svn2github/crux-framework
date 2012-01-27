package org.cruxframework.crux.widgets.client.animation;

import com.google.gwt.user.client.Element;

/**
 * @author Gesse Dafe
 */
public abstract class SlidingSwapAnimation extends SwapAnimation
{
	private int orientation;
	private int delta;
	private int originalEnteringPos;
	private int originalLeavingPos;
	private int enteringFinalPos;
	private int leavingFinalPos;
	public static enum Direction {FORWARD, BACKWARDS}

	public void setDelta(int delta, Direction direction)
	{
		this.orientation = direction.equals(Direction.FORWARD) ? -1 : 1;
		this.delta = delta * orientation;
	}
	
	@Override
	protected void doStart() 
	{
		int durationMillis = getDurationMillis();
		beforeStart(getEntering(), getLeaving());
		
		originalEnteringPos = getOriginalPosition(getEntering());
		originalLeavingPos = getOriginalPosition(getLeaving());
		enteringFinalPos = originalEnteringPos + delta;
		leavingFinalPos = originalLeavingPos + delta;
		
		setPosition(getEntering(), enteringFinalPos, durationMillis);
		setPosition(getLeaving(), leavingFinalPos, durationMillis);
	}

	@Override
	public void doFinish() 
	{
		setPosition(getEntering(), enteringFinalPos, 0);
		setPosition(getLeaving(), leavingFinalPos, 0);
	}
	
	@Override
	public void doCancel() 
	{
		setPosition(getEntering(), originalEnteringPos, 0);
		setPosition(getLeaving(), originalLeavingPos, 0);
	}

	protected abstract void beforeStart(Element entering, Element leaving);
	protected abstract void setPosition(Element target, int finalPosition, int durationMillis);
	protected abstract int getOriginalPosition(Element element);
}