package org.cruxframework.crux.widgets.client.event.row;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasBeforeCancelRowEditionHandler extends HasHandlers
{
	HandlerRegistration addBeforeCancelRowEditionHandler(BeforeCancelRowEditionHandler handler);
}
