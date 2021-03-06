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
package br.com.sysmap.crux.advanced.client.dialog;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.Widget;


/**
 * TODO - Gess� - Comment this
 * @author Gess� S. F. Daf� - <code>gessedafe@gmail.com</code>
 */
public class ProgressDialog extends Widget implements HasAnimation
{
	public static final String DEFAULT_STYLE_NAME = "crux-ProgressDialog" ;
	private static CruxInternalProgressDialogController progressDialogController = null;
	private String message;
	private String styleName;
	private boolean animationEnabled;
	protected static ProgressDialog progressDialog;
	
	/**
	 * 
	 */
	public ProgressDialog()
	{
		setElement(DOM.createSpan());
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getStyleName()
	{
		return styleName;
	}

	public void setStyleName(String styleName)
	{
		this.styleName = styleName;
	}
	
	public boolean isAnimationEnabled()
	{
		return animationEnabled;
	}

	public void setAnimationEnabled(boolean animationEnabled)
	{
		this.animationEnabled = animationEnabled;
	}	

	/**
	 * 
	 */
	public void show()
	{
		if (progressDialogController == null)
		{
			progressDialogController = new CruxInternalProgressDialogController(); 
		}
		progressDialog = this;
		progressDialogController.showProgressDialog(new ProgressDialogData(message, styleName != null ? styleName : DEFAULT_STYLE_NAME, animationEnabled));
	}
	
	/**
	 * 
	 */
	public static void hide()
	{
		if (progressDialogController == null)
		{
			progressDialogController = new CruxInternalProgressDialogController(); 
		}
		progressDialogController.hideProgressDialog();
	}
	
	/**
	 * 
	 * @param title
	 * @param message
	 * @param okCall
	 * @param cancelCall
	 */
	public static void show(String message)
	{
		show(message, DEFAULT_STYLE_NAME, false);
	}
	
	/**
	 * 
	 * @param title
	 * @param message
	 * @param okCall
	 * @param cancelCall
	 * @param styleName
	 */
	public static void show(String message, String styleName, boolean animationEnabled)
	{
		ProgressDialog progressDialog = new ProgressDialog(); 
		progressDialog.setMessage(message);
		progressDialog.setStyleName(styleName);
		progressDialog.setAnimationEnabled(animationEnabled);
		progressDialog.show();
	}
}
