package org.cruxframework.crux.widgets.client.layout;

import org.cruxframework.crux.core.client.executor.ThrottleExecutor;
import org.cruxframework.crux.core.client.screen.Screen;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public abstract class AutoResizableComposite extends Composite   
{
	/**
	 * What should be done when the container element gets resized
	 * @param containerHeight
	 * @param containerWidth
	 */
	protected abstract void onResize(int containerHeight, int containerWidth);
	
	private ResizeHandler resizeHandler = new ResizeHandler(1000);
	
	/** 
	 * Handles the common actions of resizing a widget 
	 */
	private class ResizeHandler extends ThrottleExecutor 
	{
		public ResizeHandler(int ratio) 
		{
			super(ratio);
		}

		@Override
		protected void doAction() 
		{
			AutoResizableComposite resizable = AutoResizableComposite.this;
			Widget parent = resizable.getParent();
			if(resizable.isVisible())
			{
				resizable.setVisible(false);
				int containerHeight = parent.getElement().getClientHeight();
				int containerWidth = parent.getElement().getClientWidth();
				resizable.onResize(containerHeight, containerWidth);
				resizable.setVisible(true);
			}
		} 
	}
	
	@Override
	protected void initWidget(Widget widget) 
	{
		addAttachHandler(new Handler()
		{
			HandlerRegistration registration;
			@Override
			public void onAttachOrDetach(AttachEvent event)
			{
				if (event.isAttached())
				{
					registration = Screen.addResizeHandler(new com.google.gwt.event.logical.shared.ResizeHandler() 
					{
						public void onResize(ResizeEvent event) 
						{
							resizeHandler.throttle();
						}
					});
				}
				else if (registration != null)
				{
					registration.removeHandler();
					registration = null;
				}
			}
		});
		super.initWidget(widget);
	}	
}