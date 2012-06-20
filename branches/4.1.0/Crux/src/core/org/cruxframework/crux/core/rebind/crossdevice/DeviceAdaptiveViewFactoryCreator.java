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
import org.cruxframework.crux.core.rebind.controller.ClientControllers;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.rebind.screen.widget.ControllerAccessHandler.SingleControllerAccessHandler;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator;
import org.json.JSONObject;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;

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
	 * @param view
	 * @param device
	 * @param controllerClass 
	 */
	public DeviceAdaptiveViewFactoryCreator(GeneratorContextExt context, TreeLogger logger, View view, String device, final String controllerName, String module)
    {
	    super(context, logger, view, device, module);
	    final String controllerClass = ClientControllers.getController(controllerName);
		this.widgetConsumer = new WidgetConsumer()
		{
			@Override
			public void consume(SourcePrinter out, String widgetId, String widgetVariableName)
			{
				out.println("this._controller.addWidget("+EscapeUtils.quote(widgetId)+","+widgetVariableName+");");
				//TODO: tratar o renderWidgetsWithIDs para este caso.
			}
		};
	    controllerAccessHandler = new SingleControllerAccessHandler()
		{
			public String getControllerExpression(String controller)
			{
				if (!controllerName.equals(controller))
				{
					throw new CruxGeneratorException("Controller ["+controller+"] can not be used on the deviceAdaptive template. Only the bound controller ["+controllerName+"] can be refered.");
				}
				return getSingleControllerVariable();
			}

			public String getControllerImplClassName(String controller)
            {
				if (!controllerName.equals(controller))
				{
					throw new CruxGeneratorException("Controller ["+controller+"] can not be used on the deviceAdaptive template. Only the bound controller ["+controllerName+"] can be refered.");
				}
	            return controllerClass;
            }

			public String getSingleControllerImplClassName()
			{
				return controllerClass;
			}

			public String getSingleControllerVariable()
            {
				return "_controller";
            }
		};
    }

	/**
	 * 
	 * @param sourceWriter
	 * @param metaData
	 */
	public String generateWidgetCreation(SourcePrinter printer, JSONObject metaElement)
	{
		String widget = null;
		
	    createPostProcessingScope();

	    if (!metaElement.has("_type"))
	    {
	    	throw new CruxGeneratorException("Crux Meta Data contains an invalid meta element (without type attribute).");
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
	    		throw new CruxGeneratorException("Error Creating widget. See Log for more detail.", e);
	    	}
	    }

	    if (widget != null && widget.length() > 0)
	    {
	    	printer.println(widget+".addAttachHandler(new "+Handler.class.getCanonicalName()+"(){");
	    	printer.println("public void onAttachOrDetach("+AttachEvent.class.getCanonicalName()+" event){");
	    	commitPostProcessing(printer);
	    	printer.println("}");
	    	printer.println("});");
	    }
		
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
			throw new CruxGeneratorException("The id attribute is required for CRUX Widgets. " +
					"On page ["+getView().getId()+"], there is an widget of type ["+widgetType+"] without id.");
		}
		String widget;

		String widgetId = metaElem.optString("id");
		if (widgetId == null || widgetId.length() == 0)
		{
			throw new CruxGeneratorException("The id attribute is required for CRUX Widgets. " +
					"On page ["+getView().getId()+"], there is an widget of type ["+widgetType+"] without id.");
		}

		widget = newWidget(printer, metaElem, widgetId, widgetType, this.widgetConsumer, true);
		return widget;
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
