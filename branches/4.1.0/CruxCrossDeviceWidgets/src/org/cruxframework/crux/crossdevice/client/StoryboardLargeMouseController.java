package org.cruxframework.crux.crossdevice.client;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.utils.StyleUtils;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

@Controller("storyboardLargeMouseController")
public class StoryboardLargeMouseController extends StoryboardLargeController
{
	@Override
	protected FocusPanel createFocusPanelForCell(Widget widget)
	{
	    final FocusPanel panel = super.createFocusPanelForCell(widget);
		panel.addKeyPressHandler(new KeyPressHandler()
		{
			@Override
			public void onKeyPress(KeyPressEvent event)
			{
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
				{
					int index = storyboard.getWidgetIndex(panel);
					SelectionEvent.fire(StoryboardLargeMouseController.this, index);
				}
			}
		});
	    
		return panel;
	}
	
	@Override
	protected void applyWidgetDependentStyleNames()
	{
		super.applyWidgetDependentStyleNames();
		StyleUtils.addStyleDependentName(getElement(), DeviceAdaptive.Feature.mouse.toString());
	}	
}