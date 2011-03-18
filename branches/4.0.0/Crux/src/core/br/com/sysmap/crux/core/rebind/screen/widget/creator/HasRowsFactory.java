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
package br.com.sysmap.crux.core.rebind.screen.widget.creator;

import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.widget.AttributeProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.event.RangeChangeEvtBind;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.event.RowCountChangeEvtBind;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagEvent;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagEvents;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributes({
	@TagAttribute(value="rowCount", type=Integer.class),
	@TagAttribute(value="estimatedRowCount", type=Integer.class, processor=HasRowsFactory.EstimatedRowCountProcessor.class)
})
@TagEvents({
	@TagEvent(RangeChangeEvtBind.class), 
	@TagEvent(RowCountChangeEvtBind.class)
})	
public interface HasRowsFactory<C extends WidgetCreatorContext>
{
	class EstimatedRowCountProcessor extends AttributeProcessor<WidgetCreatorContext>
	{
		public EstimatedRowCountProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
        {
			try
			{
				Integer.parseInt(attributeValue);
			}
			catch (Exception e) 
			{
				throw new CruxGeneratorException(e);//TODO message
			}
			out.println(context.getWidget()+".setRowCount("+attributeValue+", false);");
        }
		
	}
	
}
