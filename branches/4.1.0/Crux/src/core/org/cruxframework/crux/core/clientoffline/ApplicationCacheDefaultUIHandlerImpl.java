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
package org.cruxframework.crux.core.clientoffline;

import org.cruxframework.crux.core.client.screen.Screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ApplicationCacheDefaultUIHandlerImpl implements ApplicationCacheUIHandler
{
    private OfflineMessages messages = GWT.create(OfflineMessages.class);
	private DialogBox progress;
	private static Label contentLabel;

    /**
     * Shows the progress dialog.
     */
	@Override
    public void showProgress(String message) 
    {
    	if (progress == null)
    	{
    		progress = createAlertBox(message);
    	}
    	contentLabel.setText(message);
    }

	/**
     * Hides the progress dialog.
     */
	@Override
    public void hideProgress() 
    {
//        if (progress != null)
//        {
//        	progress.hide();
//        	progress = null;
//			contentLabel = null;
//        }
    }

	@Override
    public void confirmReloadPage()
    {
    	if (Window.confirm(messages.requestUpdate()))
    	{
    		Screen.reload();
    	}
    }
	
	private static DialogBox createAlertBox(final String content) 
	{
        final DialogBox box = new DialogBox();
        final VerticalPanel panel = new VerticalPanel();
        contentLabel = new Label(content);
        box.add(contentLabel);
        final Button buttonClose = new Button();
        buttonClose.setText("Ok");
        buttonClose.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				box.hide();
			}
		});
        buttonClose.setWidth("90px");
        panel.add(buttonClose);
        panel.setCellHorizontalAlignment(buttonClose, HasAlignment.ALIGN_RIGHT);
        box.add(panel);
        box.center();
        return box;
    }
}
