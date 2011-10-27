package org.cruxframework.cruxsite.server;

import org.cruxframework.cruxsite.client.remote.GreetingService;

public class GreetingServiceImpl implements GreetingService
{
	public String getHelloMessage(String name)
	{
		return "Server says: Hello, " + name + "!";
	}
}
