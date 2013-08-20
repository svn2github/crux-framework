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
package org.cruxframework.cruxsite.client;

import org.cruxframework.crux.core.client.i18n.MessageName;

import com.google.gwt.i18n.client.Messages;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@MessageName("siteMessages")
public interface SiteMessages extends Messages
{
	@DefaultMessage("Crux Framework - Official Site")
	String siteTitle();
	
	@DefaultMessage("blog")
	String blogShortcut();
	
	@DefaultMessage("project")
	String projectShortcut();
	
	@DefaultMessage("Blog Activity")
	String blogFeedsTitle();
	
	@DefaultMessage("Project Activity")
	String projectFeedsTitle();
	
	@DefaultMessage("perfomance, maintainability, usability. simpler than ever.")
	String headerLabel();

	@DefaultMessage("Fast")
	String bannerFastTitle();
	
	@DefaultMessage("Web Applications running faster than ever. As well as your development team.")
	String bannerFastText();
	
	@DefaultMessage("Social")
	String bannerSocialTitle();
	
	@DefaultMessage("You can run your applications on social platforms and web portals! Crux is ready for Google+, Facebook, Twitter, Pinterest and many others.")
	String bannerSocialText();
	
	@DefaultMessage("CAFEBABE")
	String bannerJavaTitle();
	
	@DefaultMessage("Yes, it''s pure Java inside! With Crux you don''t ever need to write a single line of Javascript code.")
	String bannerJavaText();
	
	@DefaultMessage("Cross-Device")
	String bannerCrossDeviceTitle();
	
	@DefaultMessage("Write once, run everywhere. Smartphones, tablets, Smart TVs... Crux fits all.")
	String bannerCrossDeviceText();
	
	@DefaultMessage("Crux is")
	String bannerWhatIsCruxTitle();	
	
	@DefaultMessage("a Java rapid development framework which enables the creation of powerfull cutting-edge web apps and web sites.") 
	String bannerWhatIsCrux();
	
	@DefaultMessage("Triggo Labs")
	String bannerTriggoTitle();
	
	@DefaultMessage("The best webstore to take off your Crux app.")
	String bannerTriggoText();
	
	@DefaultMessage("Learn More")
	String bannerActionLearnMore();
	
	@DefaultMessage("Visit Now!")
	String bannerActionVisitNow();
}
