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
	
	@DefaultMessage("Demo")
	String demoMenuItem();
	
	@DefaultMessage("Click to see a live example of powers of Crux.")
	String demoMenuItemTooltip();
	
	@DefaultMessage("Download")
	String downloadMenuItem();
	
	@DefaultMessage("Click to get the newest version of Crux SDK.")
	String downloadMenuItemTooltip();
	
	@DefaultMessage("Learn")
	String learnMenuItem();
	
	@DefaultMessage("Here you find tutorials, videos and other technical documentation about Crux.")
	String learnMenuItemTooltip();

	@DefaultMessage("Compare")
	String whyCruxMenuItem();
	
	@DefaultMessage("Compare Crux with other frameworks and discover why you should use it.")
	String whyCruxMenuItemTooltip();
	
	@DefaultMessage("Contribute")
	String contributeMenuItem();
	
	@DefaultMessage("Discover how you can join the Crux team and colaborate with this exciting project.")
	String contributeMenuItemTooltip();
	
	@DefaultMessage("Add-Ons")
	String addOnsMenuItem();
	
	@DefaultMessage("Enhance your Crux application with aditional 3rd party features. (coming soon)")
	String addOnsMenuItemTooltip();

	@DefaultMessage("Fast")
	String bannerFastTitle();
	
	@DefaultMessage("Applications running faster than ever. As well as your development process.")
	String bannerFastText();
	
	@DefaultMessage("Social")
	String bannerSocialTitle();
	
	@DefaultMessage("How about running your applications on social platforms and web portals? Crux is ready for Facebook, iGoogle, Orkut, Chrome Web Store and many others.")
	String bannerSocialText();
	
	@DefaultMessage("CAFEBABE")
	String bannerJavaTitle();
	
	@DefaultMessage("Yes, it's pure Java inside! With Crux you don't ever need to write a single line of Javascript code.")
	String bannerJavaText();
	
	@DefaultMessage("Cross-Device")
	String bannerCrossDeviceTitle();
	
	@DefaultMessage("Write once, run everywhere. Smartphones, tablets, Smart TVs... Crux fits all.")
	String bannerCrossDeviceText();
	
	@DefaultMessage("Triggo Labs")
	String bannerTriggoTitle();
	
	@DefaultMessage("The best webstore to take off your Crux app.")
	String bannerTriggoText();
	
	@DefaultMessage("Learn More")
	String bannerActionLearnMore();
	
	@DefaultMessage("Visit Now!")
	String bannerActionVisitNow();
	
	@DefaultMessage("Crux Framework - Download")
	String downloadPageTitle();
	
	@DefaultMessage("Dowload the lastest version of Crux")
	String downloadPageSecondaryTitle();
	
	@DefaultMessage("Click the icon below and download the lastest version of Crux Framework. The distribution brings with sample projects, API JavaDocs, visual installer and project generator. Give it a try!")
	String downloadPageSecondaryTitleMessage();	
	
	@DefaultMessage("Crux Framework 4")
	String downloadPageVersionName();
	
	@DefaultMessage("Version 4.0.1, All platforms - ZIP File, 12.5MB")
	String downloadPageVersionDetails();	
	
	@DefaultMessage("Main features in this version:")
	String releaseFeatures();
	
}
