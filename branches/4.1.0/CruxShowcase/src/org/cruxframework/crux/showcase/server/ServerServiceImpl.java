package org.cruxframework.crux.showcase.server;

import org.cruxframework.crux.showcase.client.remote.ServerService;

public class ServerServiceImpl implements ServerService
{
	public String sayHello(String name)
	{
		return "Hello, "+name+"!";
	}
}
