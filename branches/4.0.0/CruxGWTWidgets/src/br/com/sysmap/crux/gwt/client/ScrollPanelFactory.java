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
import br.com.sysmap.crux.core.client.screen.AttributeProcessor;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenLoadHandler;
import br.com.sysmap.crux.core.client.screen.factory.HasScrollHandlersFactory;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Represents a ScrollPanelFactory
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="scrollPanel", library="gwt")
public class ScrollPanelFactory extends PanelFactory<ScrollPanel, WidgetCreatorContext> 
       implements HasScrollHandlersFactory<ScrollPanel, WidgetCreatorContext>
{
	public static enum VerticalScrollPosition{top,bottom};
	public static enum HorizontalScrollPosition{left,right};

	@Override
	public ScrollPanel instantiateWidget(CruxMetaDataElement element, String widgetId) 
	{
		return new ScrollPanel();
	}
	
	@Override
	@TagAttributes({
		@TagAttribute(value="alwaysShowScrollBars", type=Boolean.class),
		@TagAttribute(value="verticalScrollPosition", type=VerticalScrollPosition.class, processor=VerticalScrollPositionAttributeParser.class),
		@TagAttribute(value="horizontalScrollPosition", type=HorizontalScrollPosition.class, processor=HorizontalScrollPositionAttributeParser.class),
		@TagAttribute(value="ensureVisible", processor=EnsureVisibleAttributeParser.class)
	})
	public void processAttributes(WidgetCreatorContext context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class VerticalScrollPositionAttributeParser implements AttributeProcessor<WidgetCreatorContext>
	{
		public void processAttribute(WidgetCreatorContext context, String propertyValue) 
		{
			ScrollPanel widget = context.getWidget();
			if (StringUtils.unsafeEquals("top", propertyValue))
			{
				widget.scrollToTop();
			}
			else 
			{
				widget.scrollToBottom();
			}
		}
	}

	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class HorizontalScrollPositionAttributeParser implements AttributeProcessor<WidgetCreatorContext>
	{
		public void processAttribute(WidgetCreatorContext context, String propertyValue) 
		{
			ScrollPanel widget = context.getWidget();
			if (StringUtils.unsafeEquals("left", propertyValue))
			{
				widget.scrollToLeft();
			}
			else
			{
				widget.scrollToRight();
			}
		}
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class EnsureVisibleAttributeParser implements AttributeProcessor<WidgetCreatorContext>
	{
		protected GWTMessages messages = GWT.create(GWTMessages.class);
		public void processAttribute(final WidgetCreatorContext context, final String propertyValue) 
		{
			final ScrollPanel widget = context.getWidget();
			ScreenFactory.getInstance().addLoadHandler(new ScreenLoadHandler()
			{
				public void onLoad(ScreenLoadEvent event) 
				{
					Widget c = Screen.get(propertyValue);
					if (c == null)
					{
						String widgetId = context.getWidgetId();
						throw new NullPointerException(messages.scrollPanelWidgetNotFound(widgetId, propertyValue));
					}
					widget.ensureVisible(c);
				}
			});
		}
	}	
}
