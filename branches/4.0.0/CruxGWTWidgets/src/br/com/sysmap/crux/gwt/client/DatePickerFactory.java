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
package br.com.sysmap.crux.gwt.client;

import org.json.JSONObject;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.HasHighlightHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasShowRangeHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasValueChangeHandlersFactory;

import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * Factory for TabPanel widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="datePicker", library="gwt")
public class DatePickerFactory extends CompositeFactory<WidgetCreatorContext> 
       implements HasValueChangeHandlersFactory<WidgetCreatorContext>, 
                  HasShowRangeHandlersFactory<WidgetCreatorContext>, 
                  HasHighlightHandlersFactory<WidgetCreatorContext>
{
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="value", type=String.class),
		@TagAttributeDeclaration(value="currentMonth", type=String.class),
		@TagAttributeDeclaration(value="datePattern")
	})
	public void processAttributes(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
		
		String widget = context.getWidget();

		String datePattern = context.readWidgetProperty("datePattern");
		if (datePattern == null || datePattern.length() == 0)
		{
			datePattern = DateFormatUtil.MEDIUM_DATE_PATTERN;
		}
		
		String value = context.readWidgetProperty("value");
		if (value != null && value.length() > 0)
		{
			out.println(widget+".setValue("+
					DateFormatUtil.class.getCanonicalName()+".getDateTimeFormat("+
					EscapeUtils.quote(datePattern)+").parse("+EscapeUtils.quote(value)+"));");
		}		

		String currentMonth = context.readWidgetProperty("currentMonth");
		if (currentMonth != null && currentMonth.length() > 0)
		{
			out.println(widget+".setCurrentMonth("+
					DateFormatUtil.class.getCanonicalName()+".getDateTimeFormat("+
					EscapeUtils.quote(datePattern)+").parse("+EscapeUtils.quote(currentMonth)+"));");
		}		
	}
	
	@Override
	public String instantiateWidget(SourcePrinter out, JSONObject metaElem, String widgetId)
	{
		String varName = ViewFactoryCreator.createVariableName("datePicker");
		String className = DatePicker.class.getCanonicalName();
		out.println(className + " " + varName+" = new "+className+"();");
		return varName;
	}
}
