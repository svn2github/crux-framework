package org.cruxframework.crux.crossdevice.client.storyboard;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
	protected Widget createClickablePanelForCell(Widget widget)
	{
		final FocusPanel panel = new FocusPanel();
		panel.add(widget);
		panel.setStyleName("item");
		if (!StringUtils.isEmpty(itemHeight))
		{
			panel.setHeight(itemHeight);
		}
		
		if (!StringUtils.isEmpty(itemWidth))
		{
			panel.setWidth(itemWidth);
		}

		panel.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				int index = storyboard.getWidgetIndex(panel);
			    SelectionEvent.fire(StoryboardLargeMouseController.this, index);
			}
		});
	    panel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
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
}
