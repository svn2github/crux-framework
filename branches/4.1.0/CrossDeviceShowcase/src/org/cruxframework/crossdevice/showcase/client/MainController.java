package org.cruxframework.crossdevice.showcase.client;

import org.cruxframework.crossdevice.showcase.client.widget.MenuDisplay;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;

@Controller("mainController")
public class MainController 
{
	@Expose
	public void showMenu()
	{
		MenuDisplay menuDisplay = (MenuDisplay) Screen.get("menuDisplay");
		menuDisplay.showMenu();
	}
}
