package org.cruxframework.cruxsite.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.ioc.Inject;
import org.cruxframework.crux.core.client.screen.views.BindRootView;
import org.cruxframework.crux.core.client.screen.views.ControllerWrapper;

@Controller("bannerController")
public class BannerController 
{
	@Inject 
	private MainScreen mainController;
	
	public void setMainController(MainScreen mainController) 
	{
		this.mainController = mainController;
	}

	@BindRootView
	public static interface MainScreen extends ControllerWrapper 
	{
		MainPageController mainPageController();
	}
	
	@Expose
	public void onClickBannerFast()
	{
		mainController.mainPageController().onClickMenuLearn();
	}
	
	@Expose
	public void onClickBannerSocial()
	{
		mainController.mainPageController().onClickMenuLearn();
	}
	
	@Expose
	public void onClickBannerJava()
	{
		mainController.mainPageController().onClickMenuLearn();
	}
	
	@Expose
	public void onClickBannerCrossDevice()
	{
		mainController.mainPageController().onClickMenuLearn();
	}
}
