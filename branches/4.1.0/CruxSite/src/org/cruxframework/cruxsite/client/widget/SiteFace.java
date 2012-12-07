/*
 * Copyright 2011 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.cruxsite.client.widget;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Template;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Templates;
import org.cruxframework.crux.crossdevice.client.event.SelectHandler;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Templates({
	@Template(name="siteFaceLarge", device=Device.all),
	@Template(name="siteFaceSmall", device=Device.smallDisplayArrows),
	@Template(name="siteFaceSmall", device=Device.smallDisplayTouch),
})
public interface SiteFace extends DeviceAdaptive
{
	void showView(String viewName, String viewId);
	void addMenuEntry(String label, String crwalerUrl, String tooltip, SelectHandler selectHandler);
}
