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
package org.cruxframework.crux.tools.quickstart.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.ioc.Inject;
import org.cruxframework.crux.core.client.rpc.AsyncCallbackAdapter;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.tools.quickstart.client.QuickStartMessages;
import org.cruxframework.crux.tools.quickstart.client.remote.WelcomeServiceAsync;
import org.cruxframework.crux.tools.quickstart.client.screen.OverviewScreen;

import com.google.gwt.user.client.Window;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
@Controller("overviewController")
public class OverviewController
{
	@Inject
	protected OverviewScreen screen; 
	
	@Inject
	protected QuickStartMessages messages; 

	@Inject
	protected WelcomeServiceAsync service;
	
	
	public void setScreen(OverviewScreen screen)
    {
    	this.screen = screen;
    }

	public void setMessages(QuickStartMessages messages)
    {
    	this.messages = messages;
    }

	public void setService(WelcomeServiceAsync service)
    {
    	this.service = service;
    }

	@Expose
	public void onLoad()
	{
		service.getCruxVersion(new AsyncCallbackAdapter<String>(this)
		{
			@Override
			public void onComplete(String result)
			{
				screen.getVersionLabel().setText(messages.cruxVersion(result));
			}
		});
	}

	@Expose
	public void generateApp()
	{
		Window.Location.assign(Screen.rewriteUrl("appWizard.html"));
	}
	
	@Expose
	public void viewExamples()
	{
		Window.Location.assign(Screen.rewriteUrl("examples.html"));
	}
	
	@Expose
	public void viewJavadoc()
	{
		Window.Location.assign(Screen.rewriteUrl("/docs/index.html"));
	}
	
	@Expose
	public void viewUserManual()
	{
		Window.Location.assign("http://code.google.com/p/crux-framework/wiki/UserManual");
	}
}
