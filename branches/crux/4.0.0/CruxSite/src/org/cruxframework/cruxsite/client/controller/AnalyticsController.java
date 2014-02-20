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
package org.cruxframework.cruxsite.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.cruxsite.client.SiteConstants;

import com.google.gwt.core.client.GWT;

/**
 * Loads then Google Analytics engine
 * 
 * @author Gesse Dafe
 */
@Controller("analyticsController")
public class AnalyticsController
{
	@Create
	protected SiteConstants consts;
	
	@Expose
	public void load()
	{
		if(GWT.isScript())
		{
			loadAnalytics(consts.analyticsCode());
		}
	}
	
	@Expose
	public native void loadAnalytics(String analyticsCode) /*-{			
		$wnd._gaq = $wnd._gaq || [];
		$wnd._gaq.push(['_setAccount', analyticsCode]);
		$wnd._gaq.push(['_trackPageview']);
		
		(function() {
			var ga = $doc.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
			ga.src = ('https:' == $doc.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
			var s = $doc.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
		})();
	}-*/;
}
