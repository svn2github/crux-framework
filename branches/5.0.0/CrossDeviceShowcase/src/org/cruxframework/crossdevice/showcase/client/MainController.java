package org.cruxframework.crossdevice.showcase.client;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.disposal.menutabsdisposal.MenuTabsDisposal;

@Controller("mainController")
public class MainController 
{
	@Expose
	public void showMenu()
	{
		MenuTabsDisposal menuDisposal = (MenuTabsDisposal) Screen.get("menuDisposal");
		menuDisposal.showMenu();
	}
}
