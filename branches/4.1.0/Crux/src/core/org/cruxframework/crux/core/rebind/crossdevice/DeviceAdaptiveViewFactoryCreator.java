/*
 * Copyright 2011 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.core.rebind.crossdevice;

import java.util.Map;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.controller.ClientControllers;
import org.cruxframework.crux.core.rebind.controller.RegisteredControllersProxyCreator;
import org.cruxframework.crux.core.rebind.datasource.RegisteredDataSourcesProxyCreator;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator;

import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DeviceAdaptiveViewFactoryCreator extends ViewFactoryCreator
{
	private final String controllerName;
	private String controllerClass;

	/**
	 * 
	 * @param context
	 * @param logger
	 * @param view
	 * @param device
	 * @param controllerClass 
	 */
	public DeviceAdaptiveViewFactoryCreator(GeneratorContextExt context, TreeLogger logger, View view, String device, final String controllerName, String module)
    {
	    super(context, logger, view, device, module);
		this.controllerName = controllerName;
	    controllerClass = ClientControllers.getController(controllerName);
    }

	@Override
	protected void generateProxyFields(SourcePrinter printer)
	{
	    super.generateProxyFields(printer);
		printer.println("private "+controllerClass+" _controller;");
	}
	
	/**
	 * Generate the View Constructor
	 */
	@Override
	protected void generateProxyContructor(SourcePrinter printer) throws CruxGeneratorException
	{
		String regsiteredControllersClass = new RegisteredControllersProxyCreator(logger, context, view, module, iocContainerClassName).create();
		String regsiteredDataSourcesClass = new RegisteredDataSourcesProxyCreator(logger, context, view, iocContainerClassName).create();

		printer.println("public "+getProxySimpleName()+"(String id, String title){");
		printer.println("super(id, title);");
		printer.println(iocContainerClassName +" iocContainer = new "+iocContainerClassName+"(this);");
		printer.println("this.registeredControllers = new "+regsiteredControllersClass+"(this, iocContainer);");
		printer.println("this.registeredDataSources = new "+regsiteredDataSourcesClass+"(this, iocContainer);");
		printer.println("this._controller = getRegisteredControllers().getController("+EscapeUtils.quote(controllerName)+");");
		printer.println("}");
	}
	
	
	@Override
	protected Map<String, String> getDeclaredMessages()
	{
	    return super.getDeclaredMessages();
	}
	
	@Override
	protected String getLoggerVariable()
	{
	    return super.getLoggerVariable();
	}
}
