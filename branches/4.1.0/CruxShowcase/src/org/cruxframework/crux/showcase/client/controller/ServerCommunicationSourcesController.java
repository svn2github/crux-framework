package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;

@Controller("serverCommunicationSourcesController")
public class ServerCommunicationSourcesController extends SourcesController {
	
	private boolean serviceLoaded;
	private boolean serviceAsyncLoaded;
	private boolean serviceImplLoaded;

	@Expose
	public void loadServiceSource() {
		
		if(!serviceLoaded){
			serviceLoaded = true;
			
			loadFile("client/remote/ServerService.java", "serviceSource");
		}
	}

	@Expose
	public void loadServiceAsyncSource() {
		
		if(!serviceAsyncLoaded){
			serviceAsyncLoaded = true;
			
			loadFile("client/remote/ServerServiceAsync.java", "serviceAsyncSource");
		}
	}

	@Expose
	public void loadServiceImplSource() {
		
		if(!serviceImplLoaded){
			serviceImplLoaded = true;
			
			loadFile("server/ServerServiceImpl.java", "serviceImplSource");
		}
	}
}