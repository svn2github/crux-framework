package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.dialog.Confirm;
import org.cruxframework.crux.widgets.client.event.CancelEvent;
import org.cruxframework.crux.widgets.client.event.CancelHandler;
import org.cruxframework.crux.widgets.client.event.OkEvent;
import org.cruxframework.crux.widgets.client.event.OkHandler;

import com.google.gwt.user.client.ui.Label;

@Controller("confirmController")
public class ConfirmController {
	
	@Expose
	public void showConfirm(){
		
		Confirm.show(
		
			"An Important Question", "Is the truth out there?", 
			
			new OkHandler(){
				public void onOk(OkEvent event)
				{
					Screen.get("message", Label.class).setText("Yes, it is!");
				}
			},
			
			new CancelHandler(){
				public void onCancel(CancelEvent event)
				{
					Screen.get("message", Label.class).setText("No, it is not!");
				}
			}
		);
	}
}