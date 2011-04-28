package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.dialog.MessageBox;
import org.cruxframework.crux.widgets.client.event.OkEvent;
import org.cruxframework.crux.widgets.client.event.OkHandler;

import com.google.gwt.user.client.ui.Label;

@Controller("messageBoxController")
public class MessageBoxController {
	
	@Expose
	public void showMessage(){
		
		MessageBox.show(
				
			"Message For You",
			
			"The truth is out there.", 
			
			new OkHandler(){
				public void onOk(OkEvent event)
				{
					Screen.get("message", Label.class).setText("You accepted the message.");
				}
			}
		);
	}
}