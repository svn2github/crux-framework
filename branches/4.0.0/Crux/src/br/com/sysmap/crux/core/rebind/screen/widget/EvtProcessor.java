/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.rebind.screen.widget;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JGenericType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JType;

import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.GeneratorMessages;
import br.com.sysmap.crux.core.rebind.controller.ClientControllers;
import br.com.sysmap.crux.core.rebind.screen.Event;
import br.com.sysmap.crux.core.rebind.screen.EventFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.utils.ClassUtils;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class EvtProcessor extends AbstractProcessor
{
	/**
	 * @param widgetCreator
	 */
	public EvtProcessor(WidgetCreator<?> widgetCreator)
    {
	    super(widgetCreator);
    }

	private static GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);

	/**
	 * @param out
	 * @param context
	 * @param eventValue
	 */
    public void processEvent(SourcePrinter out, WidgetCreatorContext context, String eventValue)
    {
		processEvent(out, eventValue, context.getWidget(), context.getWidgetId());
    }	

    
    /**
     * @param out
     * @param eventValue
     * @param cruxEvent
     */
    public void printEvtCall(SourcePrinter out, String eventValue, String cruxEvent)
    {
    	printEvtCall(out, eventValue, getEventName(), getEventClass(), cruxEvent, getWidgetCreator());
    }
    
    /**
     * @param out
     * @param eventValue
     * @param eventName
     * @param eventClass
     * @param cruxEvent
     * @param creator
     */
    public static void printEvtCall(SourcePrinter out, String eventValue, String eventName, Class<?> eventClass, String cruxEvent, WidgetCreator<?> creator)
    {
    	printEvtCall(out, eventValue, eventName, eventClass, cruxEvent, creator.getContext());
    }

    /**
     * @param out
     * @param eventValue
     * @param eventName
     * @param eventClass
     * @param cruxEvent
     * @param context
     */
    public static void printEvtCall(SourcePrinter out, String eventValue, String eventName, Class<?> eventClass, String cruxEvent, GeneratorContext context)
    {
    	Event event = EventFactory.getEvent(eventName, eventValue);
    	
    	JClassType eventClassType = context.getTypeOracle().findType(eventClass.getCanonicalName());
    	
    	String controller = ClientControllers.getController(event.getController());
    	if (controller == null)
    	{
    		throw new CruxGeneratorException(messages.eventProcessorErrorControllerNotFound(controller));
    	}

    	boolean hasEventParameter = true;
    	JClassType controllerClass = context.getTypeOracle().findType(controller);
    	if (controllerClass == null)
    	{
    		throw new CruxGeneratorException(messages.eventProcessorErrorControllerNotFound(controller));
    	}
    	
    	if (getControllerMethodWithEvent(event.getMethod(), eventClassType, controllerClass) == null)
    	{
    		if (ClassUtils.getMethod(controllerClass, event.getMethod(), new JType[]{}) == null)
    		{
        		throw new CruxGeneratorException(messages.eventProcessorErrorControllerMethodNotFound(controller, event.getMethod()));
    		}
    		hasEventParameter = false;
    	}
    	
    	out.print("(("+controller+")ScreenFactory.getInstance().getRegisteredControllers().getController("
    			+EscapeUtils.quote(event.getController())+"))."+event.getMethod()+"(");
    	
    	if (hasEventParameter)
    	{
    		out.print(cruxEvent);
    	}
    	out.println(");");
    }

	/**
	 * @param methodName
	 * @param eventClassType
	 * @param controllerClass
	 * @return
	 */
	private static JMethod getControllerMethodWithEvent(String methodName, JClassType eventClassType, JClassType controllerClass)
    {
		JGenericType genericType = eventClassType.isGenericType();
		if (genericType == null)
		{
			return ClassUtils.getMethod(controllerClass, methodName, new JType[]{eventClassType});
		}
		else
		{
			eventClassType = genericType.getRawType();
			JClassType superClass = controllerClass;
			while (superClass.getSuperclass() != null)
			{
				JMethod[] methods = superClass.getMethods();
				if (methods != null)
				{
					for (JMethod method : methods)
					{
						JParameter[] parameters = method.getParameters();
						if (method.getName().equals(methodName) && parameters != null && parameters.length==1 && 
								parameters[0].getType().isClass() != null && parameters[0].getType().isClass().isAssignableTo(eventClassType))
						{
							return method;
						}
					}
				}
				superClass = superClass.getSuperclass();
			}
			return null;
		}
    }

    /**
     * @param eventValue
     * @param cruxEvent
     */
    public void printPostProcessingEvtCall(String eventValue, String cruxEvent)
    {
    	printPostProcessingEvtCall(eventValue, getEventName(), getEventClass(), cruxEvent, getWidgetCreator());
    }

    /**
     * @param eventValue
     * @param eventName
     * @param eventClass
     * @param cruxEvent
     * @param creator
     */
    public static void printPostProcessingEvtCall(String eventValue, String eventName, Class<?> eventClass, String cruxEvent, WidgetCreator<?> creator)
    {
    	Event event = EventFactory.getEvent(eventName, eventValue);
    	
    	JClassType eventClassType = creator.getContext().getTypeOracle().findType(eventClass.getCanonicalName());

    	String controller = ClientControllers.getController(event.getController());
    	if (controller == null)
    	{
    		throw new CruxGeneratorException(messages.eventProcessorErrorControllerNotFound(controller));
    	}

    	boolean hasEventParameter = true;
    	JClassType controllerClass = creator.getContext().getTypeOracle().findType(controller);
    	if (controllerClass == null)
    	{
    		throw new CruxGeneratorException(messages.eventProcessorErrorControllerNotFound(controller));
    	}
    	if (getControllerMethodWithEvent(event.getMethod(), eventClassType, controllerClass) == null)
    	{
    		if (ClassUtils.getMethod(controllerClass, event.getMethod(), new JType[]{}) == null)
    		{
        		throw new CruxGeneratorException(messages.eventProcessorErrorControllerMethodNotFound(controller, event.getMethod()));
    		}
    		hasEventParameter = false;
    	}
    	
        creator.printlnPostProcessing("(("+controller+")ScreenFactory.getInstance().getRegisteredControllers().getController("
    			+EscapeUtils.quote(event.getController())+"))."+event.getMethod()+"(");
    	
    	if (hasEventParameter)
    	{
    		creator.printlnPostProcessing(cruxEvent);
    	}
    	creator.printlnPostProcessing(");");
    }
    
    
    /**
     * @param out
     * @param eventValue
     * @param widget
     * @param widgetId
     */
    public void processEvent(SourcePrinter out, String eventValue, String widget, String widgetId)
    {
		out.println(widget+".add"+getEventHandlerClass().getSimpleName()+"(new "+getEventHandlerClass().getCanonicalName()+"(){");
		out.println("public void "+getEventName()+"("+getEventClass().getCanonicalName()+" event){");
		printEvtCall(out, eventValue, "event");
		out.println("}");
		out.println("});");
    }
	
	/**
	 * @return
	 */
	public abstract Class<?> getEventClass();
	
	/**
	 * @return
	 */
	public abstract Class<?> getEventHandlerClass();
	
	/**
	 * @return
	 */
	public abstract String getEventName();
}
