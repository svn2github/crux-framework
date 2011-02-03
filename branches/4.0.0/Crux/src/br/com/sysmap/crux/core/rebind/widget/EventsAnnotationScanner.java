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
package br.com.sysmap.crux.core.rebind.widget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.GeneratorMessages;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorAnnotationsProcessor.EventCreator;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagEvent;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagEvents;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class EventsAnnotationScanner
{
	protected static GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);
	
	private WidgetCreatorHelper factoryHelper;

	private final WidgetCreator<?> widgetCreator;

	EventsAnnotationScanner(WidgetCreator<?> widgetCreator, Class<?> type)
    {
		this.widgetCreator = widgetCreator;
		this.factoryHelper = new WidgetCreatorHelper(type);
    }
	
	/**
	 * @param factoryClass
	 * @throws CruxGeneratorException
	 */
	List<EventCreator> scanEvents() throws CruxGeneratorException
	{
		ArrayList<EventCreator> events = new ArrayList<EventCreator>();
		scanEvents(factoryHelper.getFactoryClass(), events, new HashSet<String>());
		return events;
	}
	
	/**
	 * @param factoryClass
	 * @param events
	 * @param added
	 * @throws CruxGeneratorException
	 */
	private void scanEvents(Class<?> factoryClass, List<EventCreator> events, Set<String> added) throws CruxGeneratorException
	{
		try
        {
			TagEvents tagEvents = factoryClass.getAnnotation(TagEvents.class);
			if (tagEvents != null)
			{
				for (TagEvent evt : tagEvents.value())
				{
					String evtBinderClassName = evt.value().getCanonicalName();
					if (!added.contains(evtBinderClassName))
					{
						added.add(evtBinderClassName);
						events.add(createEventProcessor(evt));
					}
				}
			}
	        Class<?> superclass = factoryClass.getSuperclass();
	        if (superclass!= null && !superclass.equals(Object.class))
	        {
	        	scanEvents(superclass, events, added);
	        }
	        Class<?>[] interfaces = factoryClass.getInterfaces();
	        for (Class<?> interfaceClass : interfaces)
	        {
	        	scanEvents(interfaceClass, events, added);
	        }
        }
        catch (Exception e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	}	

	/**
	 * @param evt
	 * @return
	 */
	private EventCreator createEventProcessor(TagEvent evt)
    {
		final EvtProcessor evtBinder;
		try
        {
	        evtBinder = evt.value().newInstance();
	        evtBinder.setWidgetCreator(widgetCreator);
        }
        catch (Exception e)
        {
        	throw new CruxGeneratorException(messages.widgetCreatorErrorCreatingEvtBinder());
        }
		
		return new EventCreator()
		{
			public void createEvent(SourcePrinter out, WidgetCreatorContext context)
			{
				String eventValue = context.readWidgetProperty(evtBinder.getEventName());
				if (!StringUtils.isEmpty(eventValue))
				{
					evtBinder.processEvent(out, context, eventValue);
				}
			}
		};
    }
}
