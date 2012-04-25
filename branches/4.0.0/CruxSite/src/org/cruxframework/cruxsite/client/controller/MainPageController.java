package org.cruxframework.cruxsite.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.cruxsite.client.SiteConstants;

import com.google.gwt.user.client.Window;

@Controller("mainPageController")
public class MainPageController extends CruxSiteController
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
	
	@Expose
	public void onClickGoIndex()
	{
		goToPage("index.html");
	}
	
	private native void openNewWindow(String url, String windowName) /*-{
		$wnd.open(url, 'target=_blank').focus();
	}-*/;
	
	
	@Expose
	public void onClickMenuDownload()
	{
		goToPage("download.html");
	}
	
	@Expose
	public void onClickMenuLearn()
	{
	}
	
	@Expose
	public void onClickMenuCompare()
	{
	}

	@Expose
	public void onClickMenuContribute()
	{
	}
	
	@Expose
	public void onClickMenuCommunity()
	{
	}
	
	@Expose
	public void onClickEnglish()
	{
		goToPage(getCurrentPageName(), "en");
	}
	
	@Expose
	public void onClickPortuguese()
	{
		goToPage(getCurrentPageName(), "pt");
	}
	
	private String getCurrentPageName()
	{
		String pageName = "index.html";
		String url = Window.Location.getHref();
		String pageExt = ".html";
		int indexHtml = url.lastIndexOf(pageExt);
		if(indexHtml > 0)
		{
			pageName = url.substring(url.lastIndexOf("/", indexHtml) + 1, indexHtml + pageExt.length());
			pageName = pageName.replace("-en" + pageExt, pageExt);
			pageName = pageName.replace("-pt" + pageExt, pageExt);
		}
		return pageName;
	}
}
