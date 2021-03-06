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
package org.cruxframework.crux.core.rebind.screen.widget.creator.align;

import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class HorizontalAlignmentAttributeParser<C extends WidgetCreatorContext> extends AttributeProcessor<C>
{
	public HorizontalAlignmentAttributeParser(WidgetCreator<?> widgetCreator)
    {
	    super(widgetCreator);
    }

	@Override
    public void processAttribute(SourcePrinter out, C context, String attributeValue)
    {
		out.println(context.getWidget()+".setHorizontalAlignment("+
				AlignmentAttributeParser.getHorizontalAlignment(attributeValue, HasHorizontalAlignment.class.getCanonicalName()+".ALIGN_DEFAULT")+");");
    }
}
