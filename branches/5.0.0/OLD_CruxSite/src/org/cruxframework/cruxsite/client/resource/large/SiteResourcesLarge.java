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
package org.cruxframework.cruxsite.client.resource.large;

import org.cruxframework.crux.core.client.resources.Resource;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author Gesse Dafe
 *
 */
@Resource(value="siteDeviceResources", supportedDevices={Device.all})
public interface SiteResourcesLarge extends ClientBundle
{
	@Source("cssSiteLarge.css")
	CssSiteLarge css();
	
	@Source("top-menu-disposal-header-logo.jpg")
	DataResource topMenuDisposalHeaderLogo();
	
	@Source("noise.png")
	DataResource noiseBg();
	
	@Source("banner-what-is-crux.jpg")
	ImageResource bannerWhatIsCrux();
	
	@Source("banner-cross-device.jpg")
	ImageResource bannerCrossDevice();
	
	@Source("banner-fast.jpg")
	ImageResource bannerFast();
	
	@Source("banner-social.jpg")
	ImageResource bannerSocial();
	
	@Source("banner-java.jpg")
	ImageResource bannerJava();
	
	@Source("banner-right-arrow.png")
	ImageResource bannerRightArrow();
	
	@Source("banner-left-arrow.png")
	ImageResource bannerLeftArrow();
	
	@Source("view-text-panel-bg.png")
	DataResource viewTextPanelBg();
	
	@Source("mountain-bg.png")
	DataResource mountainBg();
}
