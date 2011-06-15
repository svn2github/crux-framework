package org.cruxframework.crux.core.client.executor;

import java.util.Date;

import com.google.gwt.user.client.Timer;


/**
 * Resize Handler que executa no maximo a cada meio segundo.
 * Proporciona um redimensionamento mais suave dos componentes 
 * graficos.
 */
public abstract class ThrottleExecutor
{
	private int ratio;
	private long lastExec = new Date().getTime();

	/**
	 * Creates a executor capable of controlling the 
	 * interval between the invocations to its 
	 * action.
	 *  
	 * @param ratio the interval between the invocations, in milliseconds
	 */
	public ThrottleExecutor(int ratio) 
	{
		this.ratio = ratio;
	}
	
	/**
	 * Controls the ratio
	 */
	private Timer timer = new Timer()
	{
		public void run() 
		{
			throttle();
		}
	};
	
	/**
	 * Throttles the invocations on the executor
	 */
	protected void throttle() 
	{
		long now = new Date().getTime();
		
		long delta = now - lastExec;
		
		if(delta < ratio)
		{
			if(timer != null)
			{
				timer.cancel();
			}
			
			timer.schedule(ratio);
		}
		else
		{
			lastExec = now;
			doAction();
		}
	}

	/**
	 * Executes the desired action. The invocation ratio of 
	 * this method is controlled by the <code>ratio</code>
	 * field of this instance.
	 */
	protected abstract void doAction();

}