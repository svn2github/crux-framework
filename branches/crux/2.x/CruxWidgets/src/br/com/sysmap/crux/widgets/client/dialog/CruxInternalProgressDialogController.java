/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.widgets.client.dialog;

import java.util.ArrayList;
import java.util.List;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.ExposeOutOfModule;
import br.com.sysmap.crux.core.client.controller.Global;
import br.com.sysmap.crux.core.client.screen.InvokeControllerEvent;
import br.com.sysmap.crux.core.client.screen.ModuleComunicationException;
import br.com.sysmap.crux.core.client.screen.ModuleComunicationSerializer;
import br.com.sysmap.crux.core.client.screen.Screen;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;


/**
 * Multi-frame aware controller for showing the progress dialog
 * @author Gess� S. F. Daf�
 */
@Global
@Controller(value="__progressDialog", lazy=false)
public class CruxInternalProgressDialogController 
{
	private ModuleComunicationSerializer serializer;
	private DialogBox dialog = null;
	private Label messageLabel = null;
	
	private List<String> stack = new ArrayList<String>(); 
	
	/**
	 * Default constructor
	 */
	public CruxInternalProgressDialogController()
	{
		this.serializer = Screen.getCruxSerializer();
		this.serializer.registerCruxSerializable(ProgressDialogData.class.getName(), new ProgressDialogData());
	}

	/**
	 * Invoke showProgressDialogOnTop on top window. It is required to handle multi-frame pages.
	 * @param data
	 */
	public void showProgressDialog(ProgressDialogData data)
	{
		try
		{
			showProgressDialogOnTop(serializer.serialize(data));
		}
		catch (ModuleComunicationException e)
		{
			Crux.getErrorHandler().handleError(e);
		}	
	}
	
	/**
	 * Invoke hideProgressDialogOnTop on top window. It is required to handle multi-frame pages.
	 * @param data
	 */
	public void hideProgressDialog()
	{
		hideProgressDialogOnTop();
	}
	
	/**
	 * Handler method to be invoked on top. This method shows the progress dialog.
	 * @param controllerEvent
	 */
	@ExposeOutOfModule
	public void showProgressDialogHandler(InvokeControllerEvent controllerEvent)
	{
		try
		{
			final ProgressDialogData data = (ProgressDialogData) controllerEvent.getParameter();
			String message = data.getMessage();
			this.stack.add(message);
			
			if(this.stack.size() == 1)
			{
				Screen.blockToUser("crux-ProgressDialogScreenBlocker");
				
				final DialogBox dialogBox = new DialogBox(false, true);
				dialogBox.setStyleName(data.getStyleName());
				dialogBox.setAnimationEnabled(data.isAnimationEnabled());
				
				HorizontalPanel horizontalPanel = new HorizontalPanel();
				horizontalPanel.setSpacing(0);
	
				FocusPanel iconPanel = createIconPanel();
				horizontalPanel.add(iconPanel);
				this.messageLabel = createMessageLabel(message);
				horizontalPanel.add(this.messageLabel);
							
				dialogBox.add(horizontalPanel);
				
				dialog = dialogBox;
				
				dialogBox.center();
				dialogBox.show();
				
				// TODO - Gess� - find out a better solution to avoid focus on blocked screen widgets 
				iconPanel.setFocus(true);
				iconPanel.setFocus(false);
			}
			else
			{
				this.messageLabel.setText(message);
			}				
		}
		catch (Exception e)
		{
			Crux.getErrorHandler().handleError(e);
			Screen.unblockToUser();
		}
	}

	/**
	 * Handler method to be invoked on top. That method hides the progress dialog.
	 * @param controllerEvent
	 */
	@ExposeOutOfModule
	public void hideProgressDialogHandler(InvokeControllerEvent controllerEvent)
	{
		if(this.stack.size() <= 1)
		{			
			if(dialog != null)
			{
				Screen.unblockToUser();
				dialog.hide();
				dialog = null;
			}
			
			this.stack.clear();
		}
		else
		{
			this.stack.remove(this.stack.size() - 1);
			String msg = this.stack.get(this.stack.size() - 1);
			this.messageLabel.setText(msg);
		}
	}
	
	/**
	 * Creates a panel to display a icon for the message 
	 * @return a panel
	 */
	private FocusPanel createIconPanel()
	{
		FocusPanel iconPanel = new FocusPanel();
		iconPanel.setStyleName("icon");
		return iconPanel;
	}

	/**
	 * Creates a label to display the message 
	 * @param data
	 * @return a label
	 */
	private Label createMessageLabel(String message)
	{
		Label label = new Label(message);
		label.setStyleName("message");
		return label;
	}

	/**
	 * Calls the method <code>showProgressDialogHandler</code> in the top window
	 * @param serializedData
	 */
	private native void showProgressDialogOnTop(String serializedData)/*-{
		$wnd.top._cruxScreenControllerAccessor("__progressDialog.showProgressDialogHandler", serializedData);
	}-*/;
	
	/**
	 * Calls the method <code>hideProgressDialogHandler</code> in the top window
	 */
	private native void hideProgressDialogOnTop()/*-{
		$wnd.top._cruxScreenControllerAccessor("__progressDialog.hideProgressDialogHandler");
	}-*/;
}
