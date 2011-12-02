package org.cruxframework.crux.crossdevice.client;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;

import com.google.gwt.user.client.ui.Anchor;

@Controller("myWidgetControllerAndroids")
public class MyWidgetControllerAndroids extends DeviceAdaptiveController implements MyCrossDeviceWidget
{
	@Override
    public String getTestAttribute()
    {
	    Anchor anchor = (Anchor)getChildWidget("anchor");
		return anchor.getHref();
    }

	@Override
    public void setTestAttribute(String testAttribute)
    {
		Anchor anchor = (Anchor)getChildWidget("anchor");
		anchor.setHref(testAttribute);
    }
	
}
