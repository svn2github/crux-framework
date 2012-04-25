package org.cruxframework.cruxsite.client.controller;

import org.cruxframework.crux.core.client.screen.Screen;

import com.google.gwt.user.client.Window;

public abstract class CruxSiteController
{
	/**
	 * Navigates to another page, preserving the Locale selected by the user. 
	 * @param pageName
	 */
	protected void goToPage(String pageName)
	{
		goToPage(pageName, null);
	}
	
	/**
	 * Navigates to another page, setting the new Locale 
	 * @param pageName
	 */
	protected void goToPage(String pageName, String locale)
	{
		if(locale == null)
		{
			locale = Screen.getLocale();
		}
		
		if(locale != null && locale.trim().length() > 0 && !locale.equals("default"))
		{
			String[] parts = pageName.split("\\.");
			pageName = parts[0] + "-" + locale + "." + parts[1];
			locale = "?locale=" + locale;
		}
		else
		{
			locale = "";
		}
		
		Window.Location.assign(Screen.rewriteUrl("../" + pageName + locale));
	}
}
