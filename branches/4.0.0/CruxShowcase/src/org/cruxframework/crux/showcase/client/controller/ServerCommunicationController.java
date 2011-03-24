package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.rpc.AsyncCallbackAdapter;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.showcase.client.remote.ServerServiceAsync;


import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

@Controller("serverCommunicationController")
public class ServerCommunicationController {
	
	@Create
	protected ServerServiceAsync service;
	
	@Expose
	public void callService() {

		final Label label = Screen.get("serverResponse", Label.class);
		String name = Screen.get("name", TextBox.class).getValue();
		service.sayHello(name, new AsyncCallbackAdapter<String>(this){
			public void onComplete(String result)
			{
				label.setText(result);
			}			
		});
	}
}