package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;

@Controller("i18nSourcesController")
public class I18NSourcesController extends SourcesController{
	
	private boolean messagesLoaded;
	private boolean propertiesLoaded;

	@Expose
	public void loadMessagesSource() {
		
		if(!messagesLoaded){
			messagesLoaded = true;
			
			loadFile("client/controller/MyMessages.java", "messagesSource");
		}
	}

	@Expose
	public void loadPropertiesSource() {
		
		if(!propertiesLoaded){
			propertiesLoaded = true;
			
			loadFile("client/controller/MyMessages_pt_BR.properties", "propertiesSource");
		}
	}
}