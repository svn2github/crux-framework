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
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.Screen;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator;
import org.json.JSONObject;

import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DeviceAdaptiveViewFactoryCreator extends ViewFactoryCreator
{
	/**
	 * 
	 * @param context
	 * @param logger
	 * @param screen
	 * @param device
	 */
	public DeviceAdaptiveViewFactoryCreator(GeneratorContextExt context, TreeLogger logger, Screen screen, String device)
    {
	    super(context, logger, screen, device);
    }

	/**
	 * 
	 * @param sourceWriter
	 * @param metaData
	 */
	public String generateWidgetsCreation(SourceWriter sourceWriter, JSONObject metaElement)
	{
		SourcePrinter printer = new SourcePrinter(sourceWriter, getLogger());
		String widget = null;
		
	    createPostProcessingScope();

	    printer.println("final Screen "+getScreenVariable()+" = Screen.get();");

	    if (!metaElement.has("_type"))
	    {
	    	throw new CruxGeneratorException(messages.viewFactoryMetaElementDoesNotContainsType());
	    }
	    String type = getMetaElementType(metaElement);
	    if (!StringUtils.unsafeEquals("screen",type))
	    {
	    	try 
	    	{
	    		widget = createWidgetForDevice(printer, metaElement, type);
	    	}
	    	catch (Throwable e) 
	    	{
	    		throw new CruxGeneratorException(messages.viewFactoryGenericErrorCreateWidget(e.getLocalizedMessage()), e);
	    	}
	    }

	    commitPostProcessing(printer);
		
		return widget;
	}
	
	/**
	 * Generate the code for a widget creation, based on its metadata.
	 * 
	 * @param printer 
	 * @param metaElem
	 * @param widgetType
	 * @return
	 */
	private String createWidgetForDevice(SourcePrinter printer, JSONObject metaElem, String widgetType) 
	{
		if (!metaElem.has("id"))
		{
			throw new CruxGeneratorException(messages.screenFactoryWidgetIdRequired(getScreen().getId(), widgetType));
		}
		String widget;

		String widgetId = metaElem.optString("id");
		if (widgetId == null || widgetId.length() == 0)
		{
			throw new CruxGeneratorException(messages.screenFactoryWidgetIdRequired(getScreen().getId(), widgetType));
		}

		widget = newWidget(printer, metaElem, widgetId, widgetType, false);
		return widget;
	}
	
	/**
	 * Creates a new widget based on its meta-data element.
	 *  
	 * @param printer 
	 * @param metaElem
	 * @param widgetId
	 * @param addToScreen
	 * @return
	 * @throws CruxGeneratorException
	 */
	@Override
	protected String newWidget(SourcePrinter printer, JSONObject metaElem, String widgetId, String widgetType, boolean addToScreen) 
				throws CruxGeneratorException
	{
		String widget = super.newWidget(printer, metaElem, widgetId, widgetType, addToScreen);
		addToScreen(printer, widgetId, widget);
		return widget;
	}

	/**
	 * 
	 * @param printer
	 * @param widget
	 */
	protected void addToScreen(SourcePrinter printer, String widgetId, String widget)
    {
	   printer.println("this._controller.addWidget("+EscapeUtils.quote(widgetId)+","+widget+");");
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
