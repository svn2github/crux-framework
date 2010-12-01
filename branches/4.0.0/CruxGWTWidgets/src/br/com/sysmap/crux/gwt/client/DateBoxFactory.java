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

import java.util.Date;

import br.com.sysmap.crux.core.client.collection.Array;
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
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.WidgetFactoryContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.factory.HasValueChangeHandlersFactory;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import com.google.gwt.user.datepicker.client.DateBox.Format;

/**
 * Factory for TabPanel widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="dateBox", library="gwt")
public class DateBoxFactory extends CompositeFactory<DateBox, WidgetFactoryContext> 
       implements HasValueChangeHandlersFactory<DateBox, WidgetFactoryContext>
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
	public void processAttributes(final WidgetFactoryContext context) throws InterfaceConfigException
	{
		super.processAttributes(context);

		DateBox widget = context.getWidget();
		
		String value = context.readWidgetProperty("value");
		if (value != null && value.length() > 0)
		{
			boolean reportError = true;
			String reportFormatError = context.readWidgetProperty("reportFormatError");
			if (reportFormatError != null && reportFormatError.length() > 0)
			{
				reportError = Boolean.parseBoolean(reportFormatError);
			}
			
			Date date = widget.getFormat().parse(widget, value, reportError);
			widget.setValue(date);
		}		
	}
	
	@Override
	@TagEventsDeclaration({
		@TagEventDeclaration("onLoadFormat")
	})
	public void processEvents(WidgetFactoryContext context) throws InterfaceConfigException
	{
		super.processEvents(context);
	}
	
	@Override
	public DateBox instantiateWidget(CruxMetaDataElement element, String widgetId) throws InterfaceConfigException 
	{
		Array<CruxMetaDataElement> children = ensureChildren(element, true);
		
		if (children != null && children.size() > 0)
		{
			int length = children.size();
			DatePicker picker = null;
			
			for (int i=0; i<length; i++)
			{
				CruxMetaDataElement childElement = children.get(i);
				if (childElement != null)
				{
					if (isWidget(childElement))
					{
						picker = (DatePicker) createChildWidget(childElement);
					}
				}
			}			
			
			return new DateBox(picker, null, getFormat(element, widgetId));
		}
		else
		{
			DateBox dateBox = new DateBox();
			dateBox.setFormat(getFormat(element, widgetId));
			return dateBox;
		}		
	}
	
	@Override
	@TagChildren({
		@TagChild(value=DateBoxProcessor.class, autoProcess=false)
	})
	public void processChildren(WidgetFactoryContext context) throws InterfaceConfigException {}
	
	@TagChildAttributes(tagName="datePicker", minOccurs="0", type=DatePickerFactory.class)
	public static class DateBoxProcessor extends WidgetChildProcessor<DateBox, WidgetFactoryContext>{}
	
	/**
	 * @param element
	 * @param widgetId 
	 * @return
	 */
	public Format getFormat(CruxMetaDataElement element, String widgetId)
	{
		Format format = null;
		String pattern = element.getProperty("pattern");
		
		if (!StringUtils.isEmpty(pattern))
		{
			format = new DateBox.DefaultFormat(DateFormatUtil.getDateTimeFormat(pattern));
		}
		else
		{
			Event eventLoadFormat = EvtBind.getWidgetEvent(element, "onLoadFormat");
			
			if (eventLoadFormat != null)
			{
				LoadFormatEvent<DateBox> loadFormatEvent = new LoadFormatEvent<DateBox>(widgetId);
				format = (Format) Events.callEvent(eventLoadFormat, loadFormatEvent);
			}
			else 
			{
				format = GWT.create(DefaultFormat.class);
			}
		}
		
		return format;
	}
}
