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

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagEvent;
import br.com.sysmap.crux.core.client.declarative.TagEvents;
import br.com.sysmap.crux.core.client.event.bind.LoadErrorEvtBind;
import br.com.sysmap.crux.core.client.event.bind.LoadEvtBind;
import br.com.sysmap.crux.core.client.screen.AttributeParser;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.WidgetFactoryContext;
import br.com.sysmap.crux.core.client.screen.factory.HasAllMouseHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasClickHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasDoubleClickHandlersFactory;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;

import com.google.gwt.user.client.ui.Image;

/**
 * A Factory for Image widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="image", library="gwt")
public class ImageFactory extends WidgetFactory<Image, WidgetFactoryContext> 
	   implements HasClickHandlersFactory<Image, WidgetFactoryContext>, 
	   			  HasAllMouseHandlersFactory<Image, WidgetFactoryContext>, 
	   			  HasDoubleClickHandlersFactory<Image, WidgetFactoryContext>
{
	@Override
	@TagAttributes({
		@TagAttribute(value="url", required=true),
		@TagAttribute(value="altText"),
		@TagAttribute(value="visibleRect", parser=VisibleRectAttributeParser.class)
	})	
	public void processAttributes(WidgetFactoryContext context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class VisibleRectAttributeParser implements AttributeParser<WidgetFactoryContext>
	{
		public void processAttribute(WidgetFactoryContext context, String propertyValue) 
		{
			Image widget = context.getWidget();
			String[] coord = propertyValue.split(",");
			
			if (coord != null && coord.length == 4)
			{
				widget.setVisibleRect(Integer.parseInt(coord[0].trim()),Integer.parseInt(coord[1].trim()), 
						Integer.parseInt(coord[2].trim()), Integer.parseInt(coord[3].trim()));
			}
		}
	}
	
	@Override
	@TagEvents({
		@TagEvent(LoadEvtBind.class),
		@TagEvent(LoadErrorEvtBind.class)
	})
	public void processEvents(WidgetFactoryContext context) throws InterfaceConfigException
	{
		super.processEvents(context);
	}

	@Override
	public Image instantiateWidget(CruxMetaDataElement element, String widgetId) 
	{
		return new Image();
	}
}
