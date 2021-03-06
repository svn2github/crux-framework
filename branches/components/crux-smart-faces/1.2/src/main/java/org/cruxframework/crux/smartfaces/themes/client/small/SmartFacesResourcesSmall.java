/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.smartfaces.themes.client.small;

import org.cruxframework.crux.core.client.resources.Resource;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.smartfaces.themes.client.common.SmartFacesResourcesCommon;

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.DataResource;

/**
 * @author Thiago da Rosa de Bustamante
 * @author Claudio Holanda Junior
 */
@Resource(value="smartFacesResources", supportedDevices={Device.smallDisplayArrows, Device.smallDisplayTouch})
public interface SmartFacesResourcesSmall extends SmartFacesResourcesCommon
{
	@Source({"org/cruxframework/crux/smartfaces/themes/client/common/smartFacesCommon.css","smartFacesSmall.css"})
	CssResource css();
	
	@Source("org/cruxframework/crux/smartfaces/themes/client/small/svg-icon-menu.svg")
	DataResource svgIconMenu();
}
