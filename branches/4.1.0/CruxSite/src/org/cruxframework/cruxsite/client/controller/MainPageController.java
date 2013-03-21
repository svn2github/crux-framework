package org.cruxframework.cruxsite.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.ioc.Inject;
import org.cruxframework.crux.core.client.screen.views.BindRootView;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.screen.views.ViewWrapper;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.cruxsite.client.widget.SiteFace;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;

@Controller("mainPageController")
public class MainPageController
{
	@Inject
	private MainScreen screen;

	@Expose
	public void onLoad()
	{
		String subsection = Window.Location.getHash();
		if(StringUtils.isEmpty(subsection))
		{
			subsection = "home";
		}
		else
		{
			subsection = getViewName(subsection);
			if(StringUtils.isEmpty(subsection))
			{
				subsection = "home";
			}
		}
		showView(subsection);
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
		String viewId = getViewName(event.getValue());
		if (!StringUtils.isEmpty(viewId))
		{
		    screen.site().showView(viewId, viewId, true);
		}
	}
	
	public void setScreen(MainScreen screen)
    {
    	this.screen = screen;
    }

	private void showView(String viewName)
    {
	    screen.site().showView(viewName, viewName, false);
	    View.addToHistory(viewName);
    }
	// TODO mover isso pra uma classe utilitaria do crux, com suporte a informar o nome do parametro que indica a view (abaixo seria "section")
	private String getViewName(String hash)
	{
		if (StringUtils.isEmpty(hash))
		{
			return null;
		}
		String viewName = hash.replaceAll("[#!]", "");
		if (viewName.indexOf('=') > 0 || viewName.indexOf('&') > 0)
		{
			for (String kvPair : viewName.split("&")) 
			{
				String[] kv = kvPair.split("=", 2);
				if (kv.length > 1 && StringUtils.unsafeEquals(kv[0], "section")) 
				{
					return URL.decodeQueryString(kv[1]);
				} 
			}
			return null;
		}
		
		return viewName;
	}
			
	@BindRootView
	public static interface MainScreen extends ViewWrapper
	{
		SiteFace site();
	}
}
