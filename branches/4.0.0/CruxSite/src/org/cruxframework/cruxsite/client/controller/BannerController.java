package org.cruxframework.cruxsite.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;

import com.google.gwt.user.client.Window;

@Controller("bannerController")
public class BannerController 
{
	@Expose
	public void onClickBannerFast()
	{
		Window.alert("fast");
	}
	
	@Expose
	public void onClickBannerSocial()
	{
		Window.alert("social");
	}
	
	@Expose
	public void onClickBannerJava()
	{
		Window.alert("java");
	}
	
	@Expose
	public void onClickBannerCrossDevice()
	{
		Window.alert("cross-device");
	}
	
	@Expose
	public void onClickBannerTriggo()
	{
		Window.alert("triggo");
	}
}
