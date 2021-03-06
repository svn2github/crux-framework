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
package org.cruxframework.crux.core.rebind.screen.widget;

import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.i18n.MessagesFactory;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.GeneratorMessages;
import org.cruxframework.crux.core.rebind.controller.ClientControllers;
import org.cruxframework.crux.core.rebind.controller.ControllerProxyCreator;
import org.cruxframework.crux.core.rebind.screen.Event;
import org.cruxframework.crux.core.rebind.screen.EventFactory;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JGenericType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JType;



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
    	printEvtCall(out, eventValue, eventName,  eventClass!= null? eventClass.getCanonicalName():null, cruxEvent, creator);
    }

    /**
     * @param out
     * @param eventValue
     * @param eventName
     * @param eventClassName
     * @param cruxEvent
     * @param creator
     */
    public static void printEvtCall(SourcePrinter out, String eventValue, String eventName, String eventClassName, String cruxEvent, WidgetCreator<?> creator)
    {
    	printEvtCall(out, eventValue, eventName, eventClassName, cruxEvent, creator.getContext(), creator.getScreen().getId());
    }

    /**
     * @param out
     * @param eventValue
     * @param eventName
     * @param eventClass
     * @param cruxEvent
     * @param context
     */
    public static void printEvtCall(SourcePrinter out, String eventValue, String eventName, Class<?> eventClass, String cruxEvent, GeneratorContext context, String screenId)
    {
    	printEvtCall(out, eventValue, eventName, eventClass!= null? eventClass.getCanonicalName():null, cruxEvent, context, screenId);
    }
    
    /**
     * @param out
     * @param eventValue
     * @param eventName
     * @param eventClassName
     * @param cruxEvent
     * @param context
     */
    public static void printEvtCall(SourcePrinter out, String eventValue, String eventName,String eventClassName, String cruxEvent, GeneratorContext context, String screenId)
    {
    	Event event = EventFactory.getEvent(eventName, eventValue);
    	
    	JClassType eventClassType = eventClassName==null?null:context.getTypeOracle().findType(eventClassName);
    	
    	String controller = ClientControllers.getController(event.getController());
    	if (controller == null)
    	{
    		throw new CruxGeneratorException(messages.eventProcessorErrorControllerNotFound(event.getController(), screenId));
    	}

    	boolean hasEventParameter = true;
    	JClassType controllerClass = context.getTypeOracle().findType(controller);
    	if (controllerClass == null)
    	{
    		throw new CruxGeneratorException(messages.eventProcessorErrorControllerNotFound(controller, screenId));
    	}
    	
    	JMethod exposedMethod = getControllerMethodWithEvent(event.getMethod(), eventClassType, controllerClass); 
    	if (exposedMethod == null)
    	{
    		exposedMethod = JClassUtils.getMethod(controllerClass, event.getMethod(), new JType[]{}); 
    		if (exposedMethod == null)
    		{
        		throw new CruxGeneratorException(messages.eventProcessorErrorControllerMethodNotFound(screenId, controller, event.getMethod()));
    		}
    		hasEventParameter = false;
    	}
    	
    	checkExposedMethod(event, controller, exposedMethod, context);

    	out.print("(("+controller+ControllerProxyCreator.CONTROLLER_PROXY_SUFFIX+
    			")ScreenFactory.getInstance().getRegisteredControllers().getController("
    			+EscapeUtils.quote(event.getController())+"))."+event.getMethod()+ControllerProxyCreator.EXPOSED_METHOD_SUFFIX+"(");
    	
    	if (hasEventParameter)
    	{
    		out.print(cruxEvent);
    	}
    	out.println(");");
    }

	/**
	 * @param event
	 * @param controller
	 * @param exposedMethod
	 * @param context
	 */
	private static void checkExposedMethod(Event event, String controller, JMethod exposedMethod, GeneratorContext context) 
	{
		if (exposedMethod.getAnnotation(Expose.class) == null)
    	{
    		throw new CruxGeneratorException(messages.eventProcessorErrorControllerMethodNotExposed(controller, event.getMethod()));
    	}
    	
		JClassType runtimeExceptionType = context.getTypeOracle().findType(RuntimeException.class.getCanonicalName());
		
    	JClassType[] methodThrows = exposedMethod.getThrows();
    	if (methodThrows != null)
    	{
    		for (JClassType exception : methodThrows) 
    		{
    			if (!exception.isAssignableTo(runtimeExceptionType))
    			{
    				throw new CruxGeneratorException(messages.eventProcessorErrorControllerMethodTrhowsException(controller, event.getMethod()));
    			}
			}
    	}
	}

	/**
	 * @param methodName
	 * @param eventClassType
	 * @param controllerClass
	 * @return
	 */
	static JMethod getControllerMethodWithEvent(String methodName, JClassType eventClassType, JClassType controllerClass)
    {
		if (eventClassType == null)
		{
			return null;
		}
		JGenericType genericType = eventClassType.isGenericType();
		if (genericType == null)
		{
			return JClassUtils.getMethod(controllerClass, methodName, new JType[]{eventClassType});
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
    		throw new CruxGeneratorException(messages.eventProcessorErrorControllerNotFound(event.getController(), creator.getScreen().getId()));
    	}

    	boolean hasEventParameter = true;
    	JClassType controllerClass = creator.getContext().getTypeOracle().findType(controller);
    	if (controllerClass == null)
    	{
    		throw new CruxGeneratorException(messages.eventProcessorErrorControllerNotFound(controller, creator.getScreen().getId()));
    	}
    	
    	JMethod exposedMethod = getControllerMethodWithEvent(event.getMethod(), eventClassType, controllerClass);
		if (exposedMethod == null)
    	{
			exposedMethod = JClassUtils.getMethod(controllerClass, event.getMethod(), new JType[]{}); 
    		if (exposedMethod == null)
    		{
        		throw new CruxGeneratorException(messages.eventProcessorErrorControllerMethodNotFound(creator.getScreen().getId(), controller, event.getMethod()));
    		}
    		hasEventParameter = false;
    	}
    	
    	checkExposedMethod(event, controller, exposedMethod, creator.getContext());
    	
        creator.printlnPostProcessing("(("+controller+ControllerProxyCreator.CONTROLLER_PROXY_SUFFIX+
        		")ScreenFactory.getInstance().getRegisteredControllers().getController("
    			+EscapeUtils.quote(event.getController())+"))."+event.getMethod()+ControllerProxyCreator.EXPOSED_METHOD_SUFFIX+"(");
    	
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
