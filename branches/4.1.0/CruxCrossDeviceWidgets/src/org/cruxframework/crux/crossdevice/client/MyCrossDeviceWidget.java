package org.cruxframework.crux.crossdevice.client;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Template;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Templates;

@Templates({
	@Template(name="myCrossDeviceAll", device=Device.all),
	@Template(name="myCrossDeviceAndroids", device=Device.smallDisplayArrows)
})
public interface MyCrossDeviceWidget extends DeviceAdaptive
{
	String getTestAttribute();
	void setTestAttribute(String testAttribute);
}
