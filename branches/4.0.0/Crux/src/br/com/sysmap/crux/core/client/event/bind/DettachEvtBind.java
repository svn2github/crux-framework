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
package br.com.sysmap.crux.core.client.event.bind;

import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.rebind.widget.EvtProcessor;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;

/**
 * Helper Class for widget load events binding
 * @author Thiago Bustamante
 *
 */
public class DettachEvtBind extends EvtProcessor
{
	private static final String EVENT_NAME = "onDettach";

	public void processEvent(SourcePrinter out, WidgetCreatorContext context, String eventValue)
	{
		String event = ViewFactoryCreator.createVariableName("evt");

		out.println("Event "+event+" = Events.getEvent("+EscapeUtils.quote(getEventName())+", "+ EscapeUtils.quote(eventValue)+");");
		out.println(context.getWidget()+".addAttachHandler(new Handler(){");
		out.println("public void onAttachOrDetach(AttachEvent event){");
		out.println("if (!event.isAttached()){");
		out.println("Events.callEvent("+event+", event);");
		out.println("}");
		out.println("}");
		out.println("});");
	}

	public String getEventName()
	{
		return EVENT_NAME;
	}	
}
