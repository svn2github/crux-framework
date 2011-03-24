package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;

@Controller("simpleGridSourcesController")
public class SimpleGridSourcesController extends AbstractGridSourcesController {
	
	@Override
	protected String getDtoFilePath()
	{
		return "Contact.java";
	}

	@Override
	protected String getServiceImplFilePath()
	{
		return "SimpleGridServiceImpl.java";
	}

	@Override
	protected String getDSFilePath()
	{
		return "SimpleGridDataSource.java";
	}
}