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

import com.google.gwt.i18n.client.Constants;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface SiteConstants extends Constants
{
	@DefaultStringValue("UA-7689544-6")
	String analyticsCode();

	@DefaultStringValue("ABQIAAAArGIZjhmsan61DtT58_d6cRQNU4gAv_Jc96TUa1T-tg6v_fuASxRtwAMNaJHgnp12SaDI9Cs17oKAzw")
	String googleApiKey();

	@DefaultStringValue("http://feeds.feedburner.com/cruxframework/blog")
	String blogFeedUrl();

	@DefaultIntValue(4)
	int numFeedEntries();

	@DefaultStringValue("http://code.google.com/feeds/p/crux-framework/issueupdates/basic")
	String projectFeedUrl();
	
	@DefaultStringValue("http://blog.cruxframework.org")
	String blogUrl();
	
	@DefaultStringValue("http://code.google.com/p/crux-framework/")
	String projectUrl();
	
	@DefaultStringValue("http://crux-framework.googlecode.com/files/crux-4.1.0.zip")
	String downloadUrl();
}
