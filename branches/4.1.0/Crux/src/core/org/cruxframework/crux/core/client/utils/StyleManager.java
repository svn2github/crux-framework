package org.cruxframework.crux.core.client.utils;

import com.google.gwt.user.client.ui.Widget;

public class StyleManager
{
	public void applyStyleName(Widget widget, String styleName)
	{
		widget.setStyleName(styleName);
		widget.addStyleName(styleName + "-large");
	}
}
