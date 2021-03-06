package br.com.sysmap.crux.showcase.client.controller;

import java.util.Date;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.controller.Expose;
import br.com.sysmap.crux.core.client.controller.ExposeOutOfModule;
import br.com.sysmap.crux.core.client.controller.Invoker;
import br.com.sysmap.crux.core.client.screen.InvokeControllerEvent;
import br.com.sysmap.crux.core.client.screen.Screen;

import com.google.gwt.user.client.ui.Label;

@Controller("screenCommunicationController")
public class ScreenCommunicationController {
	
	@Create
	protected FrameControllerInvoker frameControllerInvoker;
	
	@Expose
	public void changeFrame()
	{
		frameControllerInvoker.setMyLabelOnFrameMyFrame("Modified at " + new Date().toString());
	}
	
	public static interface FrameControllerInvoker extends Invoker
	{
		void setMyLabelOnFrameMyFrame(String text);
	}
	
	@Controller("frameController")
	public static class FrameController {
		
		@ExposeOutOfModule
		public void setMyLabel(InvokeControllerEvent event)
		{
			String text = event.getParameter(0, String.class);
			Screen.get("label", Label.class).setText(text);
		}
	}
}