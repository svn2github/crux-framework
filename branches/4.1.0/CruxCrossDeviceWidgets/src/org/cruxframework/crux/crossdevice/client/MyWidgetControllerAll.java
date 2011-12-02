package org.cruxframework.crux.crossdevice.client;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;

import com.google.gwt.user.client.ui.Button;

@Controller("myWidgetControllerAll")
public class MyWidgetControllerAll extends DeviceAdaptiveController implements MyCrossDeviceWidget
{
	@Override
    public String getTestAttribute()
    {
		Button button = (Button)getChildWidget("button");
		return button.getText();
    }

	@Override
    public void setTestAttribute(String testAttribute)
    {
		Button button = (Button)getChildWidget("button");
		button.setText(testAttribute);
    }
}
