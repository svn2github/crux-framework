package br.com.sysmap.crux.showcase.client.controller;

import com.google.gwt.user.client.ui.CheckBox;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.widgets.client.decoratedbutton.DecoratedButton;

@Controller("decoratedButtonController")
public class DecoratedButtonController {
	
	private int clickTimes = 0;
	
	@Expose
	public void onClick(){
		clickTimes++;
		Screen.get("myButton", DecoratedButton.class).setText("You have clicked " + clickTimes + " times");
	}
	
	@Expose
	public void enableButton(){
		Boolean enable = Screen.get("enable", CheckBox.class).getValue();
		Screen.get("myButton", DecoratedButton.class).setEnabled(enable);
	}
}