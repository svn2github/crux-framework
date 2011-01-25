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
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagEvent;
import br.com.sysmap.crux.core.client.declarative.TagEvents;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.AttributeProcessor;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.HasAllMouseHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasClickHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasDoubleClickHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.event.LoadErrorEvtBind;
import br.com.sysmap.crux.core.rebind.widget.creator.event.LoadEvtBind;

import com.google.gwt.user.client.ui.Image;

/**
 * A Factory for Image widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="image", library="gwt")
public class ImageFactory extends WidgetCreator<WidgetCreatorContext> 
	   implements HasClickHandlersFactory<WidgetCreatorContext>, 
	   			  HasAllMouseHandlersFactory<WidgetCreatorContext>, 
	   			  HasDoubleClickHandlersFactory<WidgetCreatorContext>
{
	@Override
	@TagAttributes({
		@TagAttribute(value="url", required=true),
		@TagAttribute(value="altText"),
		@TagAttribute(value="visibleRect", processor=VisibleRectAttributeParser.class)
	})	
	public void processAttributes(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class VisibleRectAttributeParser extends AttributeProcessor<WidgetCreatorContext>
	{
		@Override
        public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
        {
			String widget = context.getWidget();
			String[] coord = attributeValue.split(",");
			
			if (coord != null && coord.length == 4)
			{
				out.println(widget+".setVisibleRect("+Integer.parseInt(coord[0].trim())+", "+Integer.parseInt(coord[1].trim())+","+ 
						Integer.parseInt(coord[2].trim())+", "+Integer.parseInt(coord[3].trim())+");");
			}
        }
	}
	
	@Override
	@TagEvents({
		@TagEvent(LoadEvtBind.class),
		@TagEvent(LoadErrorEvtBind.class)
	})
	public void processEvents(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		super.processEvents(out, context);
	}

	@Override
	public String instantiateWidget(SourcePrinter out, JSONObject metaElem, String widgetId)
	{
		String varName = ViewFactoryCreator.createVariableName("image");
		String className = Image.class.getCanonicalName();
		out.println(className + " " + varName+" = new "+className+"();");
		return varName;
	}	
}
