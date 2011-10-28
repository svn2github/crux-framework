package org.cruxframework.cruxsite.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.cruxsite.client.widget.ImageBanner;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.SimplePanel;

@Controller("bannerController")
public class BannerController 
{
	@Expose
	public void load()
	{
		ImageBanner banner = new ImageBanner();
		banner.setHeight("300px");
		banner.setWidth("900px");
		
		SimplePanel canvas = (SimplePanel) Screen.get("canvas");
		canvas.add(banner);
		
		banner.addImage("../img/banner-fast.jpg", "Fast", "Applications running faster than ever. As well as your development process.", "Learn More!", 
			new ClickHandler() 
			{
				public void onClick(ClickEvent event) 
				{
					
				}
			}
		);
	}
}
