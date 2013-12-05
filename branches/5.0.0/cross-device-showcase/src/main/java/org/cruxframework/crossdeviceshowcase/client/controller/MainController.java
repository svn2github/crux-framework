package org.cruxframework.crossdeviceshowcase.client.controller;

import org.cruxframework.crossdeviceshowcase.client.remote.SVNServiceAsync;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.ioc.Inject;
import org.cruxframework.crux.core.client.rpc.AsyncCallbackAdapter;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.disposal.menutabsdisposal.MenuTabsDisposal;

import com.google.gwt.user.client.Window;

@Controller("mainController")
public class MainController 
{
	@Inject
	public SVNServiceAsync service;
	
	@Expose
	public void showMenu()
	{
		MenuTabsDisposal menuDisposal = (MenuTabsDisposal) Screen.get("menuDisposal");
		menuDisposal.showMenu();
	}
	
	@Expose
	public void exibirCodigoFonte()
	{
		MenuTabsDisposal menuDisposal = (MenuTabsDisposal) Screen.get("menuDisposal");
		String viewId = menuDisposal.getCurrentView();
		service.getXmlFile(viewId + ".view.xml", true, new AsyncCallbackAdapter<String>() 
		{
			@Override
			public void onComplete(String result) 
			{
				Window.alert(result);
			}
		});
	}
}
