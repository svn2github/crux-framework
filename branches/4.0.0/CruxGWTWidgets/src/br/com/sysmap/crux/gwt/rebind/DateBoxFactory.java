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
package br.com.sysmap.crux.gwt.rebind;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.HasValueChangeHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.gwt.client.DateFormatUtil;

import com.google.gwt.user.datepicker.client.DateBox;

/**
 * Factory for TabPanel widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="dateBox", library="gwt")
public class DateBoxFactory extends CompositeFactory<WidgetCreatorContext> 
       implements HasValueChangeHandlersFactory<WidgetCreatorContext>
{
	@Override
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
	@TagEventsDeclaration({
		@TagEventDeclaration("onLoadFormat")
	})
	public void processEvents(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		super.processEvents(out, context);
	}
	
	
	@Override
	public String instantiateWidget(SourcePrinter out, JSONObject metaElem, String widgetId)
	{
		String varName = ViewFactoryCreator.createVariableName("dateBox");
		String className = DateBox.class.getCanonicalName();

		JSONArray children = ensureChildren(metaElem, true);
		
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
						picker = createChildWidget(out, childElement);
					}
				}
			}			
			out.println(className+" "+varName+" new "+className+"("+picker+", null, "+getFormat(metaElem, widgetId)+");");
			return varName;
		}
		else
		{
			out.println(className+" "+varName+" new "+className+"();");
			out.println(varName+".setFormat("+getFormat(metaElem, widgetId)+");");
			return varName;
		}		
	}
	
	@Override
	@TagChildren({
		@TagChild(value=DateBoxProcessor.class, autoProcess=false)
	})
	public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException {}
	
	@TagChildAttributes(tagName="datePicker", minOccurs="0", type=DatePickerFactory.class)
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
}
