package org.cruxframework.cruxshowcase.crossdevice.client;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.swapcontainer.HorizontalSwapContainer;

@Controller("homeController")
public class HomeController 
{
	@Expose
	public void onLoad()
	{
		HorizontalSwapContainer views = (HorizontalSwapContainer) Screen.get("views");
		views.showView("input");
	}
	
	@Expose
	public void sayHello()
	{
		HorizontalSwapContainer views = (HorizontalSwapContainer) Screen.get("views");
		views.showView("hello");
	}
	
}
