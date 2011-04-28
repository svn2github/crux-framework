package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;

@Controller("streamingGridSourcesController")
public class StreamingGridSourcesController extends AbstractGridSourcesController {
	
	@Override
	protected String getDtoFilePath()
	{
		return "Contact.java";
	}

	@Override
	protected String getServiceImplFilePath()
	{
		return "StreamingGridServiceImpl.java";
	}
	
	@Override
	protected String getDSFilePath()
	{
		return "StreamingGridDataSource.java";
	}	
}