package org.cruxframework.cruxsite.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.cruxsite.client.SiteConstants;

@Controller("mainPageController")
public class MainPageController 
{
	@Create
	protected SiteConstants constants;
	
	@Expose
	public void onClickGoBlog()
	{
		openNewWindow(constants.blogUrl(), "blog");
	}
	
	@Expose
	public void onClickGoProject()
	{
		openNewWindow(constants.projectUrl(), "project");
	}
	
	private native void openNewWindow(String url, String windowName)
	/*-{
		$wnd.open(url, 'target=_blank').focus();
	}-*/;
}
