package org.cruxframework.cruxsite.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.cruxsite.client.widget.PromoBanner;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.SimplePanel;

@Controller("bannerController")
public class BannerController 
{
	@Expose
	public void load()
	{
		PromoBanner banner = new PromoBanner();
		banner.setWidth("900px");
		banner.setBannerHeight("300px");
		
		SimplePanel canvas = (SimplePanel) Screen.get("canvas");
		canvas.add(banner);
		
		banner.addBanner("../img/banner-fast.jpg", "Fast", "Applications running faster than ever. As well as your development process.", "Learn More", 
			new ClickHandler() 
			{
				public void onClick(ClickEvent event) 
				{
					
				}
			}
		);
		
		banner.addBanner("../img/banner-social.jpg", "Social", "How about running your applications on social platforms and web portals? Crux is ready for Facebook, iGoogle, Orkut, Chrome Web Store and many others.", "Learn More", "longText", 
			new ClickHandler() 
			{
				public void onClick(ClickEvent event) 
				{
					
				}
			}
		);
		
		banner.addBanner("../img/banner-java.jpg", "CAFEBABE", "Yes, it's pure Java inside! With Crux you don't ever need to write a single line of Javascript code.", "Learn More", 
			new ClickHandler() 
			{
				public void onClick(ClickEvent event) 
				{
					
				}
			}
		);
		
		banner.addBanner("../img/banner-cross-device.jpg", "Cross-Device", "Write once, run everywhere. Smartphones, tablets, SmartTV... Crux fits all.", "Learn More", 
			new ClickHandler() 
			{
				public void onClick(ClickEvent event) 
				{
					
				}
			}
		);
		
		banner.addBanner("../img/banner-triggo.jpg", "Triggo Labs", "The best webstore to take off your Crux app.", "Visit Now!", 
			new ClickHandler() 
			{
				public void onClick(ClickEvent event) 
				{
					
				}
			}
		);
	}
}
