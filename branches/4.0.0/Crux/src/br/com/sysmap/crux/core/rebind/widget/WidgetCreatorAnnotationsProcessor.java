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

import java.util.List;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.GeneratorMessages;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;

import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class WidgetCreatorAnnotationsProcessor
{
	protected static GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);
	private List<AttributeCreator> attributes;
	private List<EventCreator> events;
	
	/**
	 * Constructor
	 * @param type
	 * @param widgetCreator
	 */
	WidgetCreatorAnnotationsProcessor(JClassType type, WidgetCreator<?> widgetCreator)
    {
		this.attributes = new AttributesAnnotationScanner(widgetCreator, type).scanAttributes();
		this.events = new EventsAnnotationScanner(type).scanEvents();
    }
	
	/**
	 * @param out
	 * @param context
	 */
	void processAttributes(SourcePrinter out, WidgetCreatorContext context)
	{
		for (AttributeCreator creator : attributes)
        {
	        creator.createAttribute(out, context);
        }
	}

	/**
	 * @param out
	 * @param context
	 */
	void processEvents(SourcePrinter out, WidgetCreatorContext context)
	{
		for (EventCreator creator : events)
        {
	        creator.createEvent(out, context);
        }
	}

	void processChildren(SourcePrinter out, WidgetCreatorContext context)
	{
		
	}

	static interface AttributeCreator
	{
		void createAttribute(SourcePrinter out, WidgetCreatorContext context);
	}

	static interface EventCreator
	{
		void createEvent(SourcePrinter out, WidgetCreatorContext context);
	}
}
