package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.advanced.client.decoratedbutton.DecoratedButton;
import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;

@Controller("decoratedButtonController")
public class DecoratedButtonController {
	
	private int clickTimes = 0;
	
	@Expose
	public void onClick(){
		clickTimes++;
		Screen.get("enabledButton", DecoratedButton.class).setText("You have clicked " + clickTimes + " times");
	}
}