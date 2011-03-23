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
package org.cruxframework.crux.gwt.rebind;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasValueChangeHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventsDeclaration;
import org.cruxframework.crux.gwt.client.DateFormatUtil;
import org.json.JSONArray;
import org.json.JSONObject;


import com.google.gwt.user.datepicker.client.DateBox;

/**
 * Factory for TabPanel widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="dateBox", library="gwt", targetWidget=DateBox.class)
@TagAttributes({
	@TagAttribute(value="tabIndex", type=Integer.class),
	@TagAttribute(value="enabled", type=Boolean.class),
	@TagAttribute(value="accessKey", type=Character.class),
	@TagAttribute(value="focus", type=Boolean.class)
})
@TagAttributesDeclaration({
	@TagAttributeDeclaration("value"),
	@TagAttributeDeclaration("pattern"),
	@TagAttributeDeclaration(value="reportFormatError", type=Boolean.class)
})
@TagEventsDeclaration({
	@TagEventDeclaration("onLoadFormat")
})
@TagChildren({
	@TagChild(value=DateBoxFactory.DateBoxProcessor.class, autoProcess=false)
})

public class DateBoxFactory extends CompositeFactory<WidgetCreatorContext> 
       implements HasValueChangeHandlersFactory<WidgetCreatorContext>
{
	@Override
	public void processAttributes(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);

		String widget = context.getWidget();
		
		String value = context.readWidgetProperty("value");
		if (value != null && value.length() > 0)
		{
			boolean reportError = true;
			String reportFormatError = context.readWidgetProperty("reportFormatError");
			if (reportFormatError != null && reportFormatError.length() > 0)
			{
				reportError = Boolean.parseBoolean(reportFormatError);
			}
			
			out.println(widget+".setValue("+widget+".getFormat().parse("+widget+", "+EscapeUtils.quote(value)+", "+reportError+"));");
		}		
	}
	
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context)
	{
		String className = DateBox.class.getCanonicalName();

		JSONArray children = ensureChildren(context.getWidgetElement(), true);
		
		if (children != null && children.length() > 0)
		{
			int length = children.length();
			String picker = null;
			
			for (int i=0; i<length; i++)
			{
				JSONObject childElement = children.optJSONObject(i);
				if (childElement != null)
				{
					if (isWidget(childElement))
					{
						picker = createChildWidget(out, childElement, context);
					}
				}
			}			
			out.println(className+" "+context.getWidget()+" = new "+className+"("+picker+", null, "+getFormat(context.getWidgetElement(), context.getWidgetId())+");");
		}
		else
		{
			out.println(className+" "+context.getWidget()+" = new "+className+"();");
			out.println(context.getWidget()+".setFormat("+getFormat(context.getWidgetElement(), context.getWidgetId())+");");
		}		
	}
	
	@TagConstraints(tagName="datePicker", minOccurs="0", type=DatePickerFactory.class)
	public static class DateBoxProcessor extends WidgetChildProcessor<WidgetCreatorContext>{}
	
	/**
	 * @param element
	 * @param widgetId 
	 * @return
	 */
	public String getFormat(JSONObject element, String widgetId)
	{
		String format = null;
		String pattern = element.optString("pattern");
		
		if (!StringUtils.isEmpty(pattern))
		{
			format = "new "+DateBox.class.getCanonicalName()+".DefaultFormat("+DateFormatUtil.class.getCanonicalName()+".getDateTimeFormat("+EscapeUtils.quote(pattern)+"))";
		}
		else
		{

			String eventLoadFormat = element.optString("onLoadFormat");
			if (eventLoadFormat != null)
			{
			
				format = "(Format) Events.callEvent(Events.getEvent(\"onLoadFormat\", "+EscapeUtils.quote(eventLoadFormat)+
				         "), new LoadFormatEvent<"+DateBox.class.getCanonicalName()+">("+ EscapeUtils.quote(widgetId)+"))";
			}
			else 
			{
				format = "GWT.create("+DateBox.class.getCanonicalName()+".DefaultFormat.class)";
			}
		}
		
		return format;
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
