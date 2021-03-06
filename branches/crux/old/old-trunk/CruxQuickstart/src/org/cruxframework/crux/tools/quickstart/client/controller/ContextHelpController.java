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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Controller for helpContext template.
 * @author Gesse S. F. Dafe - <code>gessedafe@gmail.com</code>
 */
@Controller("contextHelpController")
public class ContextHelpController
{
	@Expose
	public void showHelpText(ClickEvent event)
	{
		Image image = (Image) event.getSource();
		HorizontalPanel hPanel = (HorizontalPanel) image.getParent();
		Hidden hidden = (Hidden) hPanel.getWidget(2);
		String helpText = hidden.getValue();
		
		DialogBox dialog = new DialogBox(true);
		dialog.setWidth("300");
		dialog.setWidget(new Label(helpText));
		dialog.setStyleName("HelpDialog");
		dialog.showRelativeTo(image);
	}
}