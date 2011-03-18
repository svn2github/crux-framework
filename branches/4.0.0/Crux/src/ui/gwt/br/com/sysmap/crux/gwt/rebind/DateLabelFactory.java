/*
 * Copyright 2011 Sysmap Solutions Software e Consultoria Ltda.
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

import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasAutoHorizontalAlignmentFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasDirectionEstimatorFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasWordWrapFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.gwt.client.DateFormatUtil;

import com.google.gwt.user.client.ui.DateLabel;

/**
 * A Factory for DateLabel widgets
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="dateLabel", library="gwt", targetWidget=DateLabel.class)
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="value", type=String.class),
	@TagAttributeDeclaration(value="datePattern")
})public class DateLabelFactory extends WidgetCreator<WidgetCreatorContext> 
		implements HasWordWrapFactory<WidgetCreatorContext>, 
				   HasAutoHorizontalAlignmentFactory<WidgetCreatorContext>, 
				   HasDirectionEstimatorFactory<WidgetCreatorContext>
{
	@Override
	public WidgetCreatorContext instantiateContext()
	{
		return new WidgetCreatorContext();
	}
	
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		String datePattern = context.readWidgetProperty("datePattern");
		if (datePattern == null || datePattern.length() == 0)
		{
			datePattern = DateFormatUtil.MEDIUM_DATE_PATTERN;
		}
		out.println("final "+className + " " + context.getWidget()+" = new "+className+"("+
						DateFormatUtil.class.getCanonicalName()+".getDateTimeFormat("+
						EscapeUtils.quote(datePattern)+");");
	}
	
	@Override
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
	}
	
}