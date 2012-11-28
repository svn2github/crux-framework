package org.cruxframework.cruxsite.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.ioc.Inject;
import org.cruxframework.crux.core.client.screen.views.BindRootView;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.screen.views.ViewWrapper;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.widgets.client.swapcontainer.SwapContainer;
import org.cruxframework.cruxsite.client.SiteConstants;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;

@Controller("mainPageController")
public class MainPageController
{
	@Inject
	private SiteConstants constants;
	
	@Inject
	private MainScreen screen;

	@Expose
	public void onLoad()
	{
		View.addToHistory("home");
	}
	
	@Expose
	public void onClickGoBlog()
	{
		Window.open(constants.blogUrl(), "blog", null);
	}
	
	@Expose
	public void onClickGoProject()
	{
		Window.open(constants.projectUrl(), "project", null);
	}
	
	@Expose
	public void onClickGoIndex()
	{
		showView("home");
	}

	@Expose
	public void onClickMenuDownload()
	{
		showView("download");
	}
	
	@Expose
	public void onClickMenuLearn()
	{
		showView("learn");
	}
	
	@Expose
	public void onClickMenuCompare()
	{
		showView("compare");
	}

	@Expose
	public void onClickMenuContribute()
	{
		showView("contribute");
	}
	
	@Expose
	public void onClickMenuCommunity()
	{
		showView("community");
	}
	
	@Expose
	public void onHistoryChanged(ValueChangeEvent<String> event)
	{
		String viewId = event.getValue();
		if (!StringUtils.isEmpty(viewId))
		{
		    screen.viewContainer().showView(viewId);
		}
	}
	
	public void setConstants(SiteConstants constants)
    {
    	this.constants = constants;
    }

	public void setScreen(MainScreen screen)
    {
    	this.screen = screen;
    }

	private void showView(String viewName)
    {
	    screen.viewContainer().showView(viewName);
	    View.addToHistory(viewName);
    }
	
	@BindRootView
	public static interface MainScreen extends ViewWrapper
	{
		SwapContainer viewContainer();
	}
}
