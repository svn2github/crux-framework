package org.cruxframework.crux.core.client.utils;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class StyleManager
{
	protected String getSufix()
	{
		return "-large";
	}

	public final void applyStyleName(Widget widget, String styleName)
	{
		widget.setStyleName(styleName);
		widget.addStyleName(styleName + getSufix());
	}
	
	public final void applyStyleName(IsWidget widget, String styleName)
	{
		applyStyleName(widget.asWidget(), styleName);
	}
}
