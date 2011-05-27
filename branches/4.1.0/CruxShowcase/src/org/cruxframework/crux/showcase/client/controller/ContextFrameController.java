package org.cruxframework.crux.showcase.client.controller;

import java.util.Date;

import org.cruxframework.crux.core.client.context.ContextManager;
import org.cruxframework.crux.core.client.context.HasContextChangeHandlers;
import org.cruxframework.crux.core.client.context.HasContextChangeHandlers.ContextEvent;
import org.cruxframework.crux.core.client.context.HasContextChangeHandlers.Handler;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.showcase.client.controller.ContextInitializerController.SharedContext;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;

@Controller("contextFrameController")
public class ContextFrameController {
	
	@Create
	protected SharedContext context;
	
	@Expose
	public void onLoad(){
		
		((HasContextChangeHandlers)ContextManager.getContextHandler()).addContextChangeHandler(new Handler()
		{
			public void onContextChange(ContextEvent event)
			{
				Window.alert("Screen: "+Screen.getId()+", Key:"+event.getKey()+", newValue:"+event.getNewValue()+", changed by: "+event.getUrl());
			}
		});
	}	
	
	
	
	@Expose
	public void readContext(){
		
		String message = context.getMessage();
		Date msgDate = context.getDate();
		
		if(message != null)
		{
			long messageAge = (new Date().getTime() - msgDate.getTime()) / 1000;
			
			Screen.get("label", Label.class).setText(
				"Message read from context: '" + message + "'.\nMessage age: " + messageAge + " seconds."
			);
		}
		else
		{
			Window.alert("Type a message and press 'Save in Context', before clicking here.");
		}
	}
}