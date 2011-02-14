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

import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.controller.ClientControllers;
import br.com.sysmap.crux.core.rebind.screen.Event;
import br.com.sysmap.crux.core.rebind.screen.EventFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class EvtProcessor extends AbstractProcessor
{
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
    	printEvtCall(out, eventValue, getEventName(), getEventClass(), cruxEvent);
    }
    
    /**
     * @param out
     * @param eventValue
     * @param eventName
     * @param eventClass
     * @param cruxEvent
     */
    public static void printEvtCall(SourcePrinter out, String eventValue, String eventName, Class<?> eventClass, String cruxEvent)
    {
    	Event event = EventFactory.getEvent(eventName, eventValue);
    	
    	String controller = ClientControllers.getController(event.getController());
    	if (controller == null)
    	{
    		throw new CruxGeneratorException();//TODO message
    	}

    	boolean hasEventParameter = true;
    	try
        {
	        Class<?> controllerClass = Class.forName(controller);
	        controllerClass.getMethod(event.getMethod(), new Class<?>[]{eventClass});
        }
        catch (Exception e)
        {
        	try
            {
    	        Class<?> controllerClass = Class.forName(controller);
    	        controllerClass.getMethod(event.getMethod(), new Class<?>[]{});
    	        hasEventParameter = false;
            }
            catch (Exception e1)
            {
            	throw new CruxGeneratorException(); //TODO: message
            }
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
    public static void printPostProcessingEvtCall(String eventValue, String eventName,Class<?> eventClass, String cruxEvent, WidgetCreator<?> creator)
    {
    	Event event = EventFactory.getEvent(eventName, eventValue);
    	
    	String controller = ClientControllers.getController(event.getController());
    	if (controller == null)
    	{
    		throw new CruxGeneratorException();//TODO message
    	}

    	boolean hasEventParameter = true;
    	try
        {
	        Class<?> controllerClass = Class.forName(controller);
	        controllerClass.getMethod(event.getMethod(), new Class<?>[]{eventClass});
        }
        catch (Exception e)
        {
        	try
            {
    	        Class<?> controllerClass = Class.forName(controller);
    	        controllerClass.getMethod(event.getMethod(), new Class<?>[]{});
    	        hasEventParameter = false;
            }
            catch (Exception e1)
            {
            	throw new CruxGeneratorException(); //TODO: message
            }
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
