package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.widgets.client.dynatabs.DynaTabs;
import br.com.sysmap.crux.widgets.client.stackmenu.StackMenuItem;

import com.google.gwt.event.logical.shared.SelectionEvent;

@Controller("mainController")
public class MainController {
	
	@Expose
	public void openExample(SelectionEvent<StackMenuItem> evt) {
		StackMenuItem item = evt.getSelectedItem();
		Screen.get("tabs", DynaTabs.class).openTab(item.getKey(), item.getLabel(), item.getKey() + ".html", true, false);
	}

	
}