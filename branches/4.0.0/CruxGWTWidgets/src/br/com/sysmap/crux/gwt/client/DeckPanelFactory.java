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
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.rebind.widget.AttributeProcessor;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.HasAnimationFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.children.AnyWidgetChildProcessor;

import com.google.gwt.user.client.ui.DeckPanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="deckPanel", library="gwt")
public class DeckPanelFactory extends ComplexPanelFactory<DeckPanel, WidgetCreatorContext>
					implements HasAnimationFactory<DeckPanel, WidgetCreatorContext>
{
	@Override
	public DeckPanel instantiateWidget(CruxMetaDataElement element, String widgetId) 
	{
		return new DeckPanel();
	}

	@Override
	@TagAttributes({
		@TagAttribute(value="visibleWidget", type=Integer.class, processor=VisibleWidgetAttributeParser.class)
	})
	public void processAttributes(WidgetCreatorContext context) throws InterfaceConfigException 
	{
		super.processAttributes(context);
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class VisibleWidgetAttributeParser implements AttributeProcessor<WidgetCreatorContext>
	{
		public void processAttribute(WidgetCreatorContext context, String propertyValue) 
		{
			DeckPanel widget = context.getWidget();
			widget.showWidget(Integer.parseInt(propertyValue));
		}
	}
	
	@Override
	@TagChildren({
		@TagChild(WidgetContentProcessor.class)
	})
	public void processChildren(WidgetCreatorContext context) throws InterfaceConfigException
	{
	}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded")
	public static class WidgetContentProcessor extends AnyWidgetChildProcessor<DeckPanel, WidgetCreatorContext> {}
		
}