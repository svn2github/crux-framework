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
package br.com.sysmap.crux.widgets.client.paging;

import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenLoadHandler;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;
import br.com.sysmap.crux.widgets.client.event.paging.PageEvtBind;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Gesse S. F. Dafe
 */
public abstract class AbstractPagerFactory<T extends AbstractPager> extends WidgetCreator<T, WidgetCreatorContext>
{
	/**
	 * @see br.com.sysmap.crux.core.rebind.widget.WidgetCreator#instantiateWidget(com.google.gwt.dom.client.Element, java.lang.String)
	 */
	public T instantiateWidget(CruxMetaDataElement elem, String widgetId) throws InterfaceConfigException
	{
		return createPagerInstance();
	}

	/**
	 * Creates a new instance of the Pager
	 * @return
	 */
	protected abstract T createPagerInstance();

	@SuppressWarnings("unchecked")
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("pageable"),
		@TagAttributeDeclaration(value="enabled", type=Boolean.class)
	})
	public void processAttributes(WidgetCreatorContext context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	
		final T widget = (T)context.getWidget();
		final String pageableId = context.readWidgetProperty("pageable");
		final String strEnabled = context.readWidgetProperty("enabled");
		
		addScreenLoadedHandler(
				
			new ScreenLoadHandler()
			{
				public void onLoad(ScreenLoadEvent screenLoadEvent)
				{					
					Widget pageable = null;
					if(pageableId != null)
					{
						pageable = Screen.get(pageableId);
					}
					
					if(pageable != null)
					{
						widget.setPageable((Pageable) pageable);
						if(strEnabled != null && strEnabled.length() > 0)
						{
							widget.setEnabled(Boolean.parseBoolean(strEnabled));
						}
					}
					else
					{
						throw new RuntimeException(WidgetMsgFactory.getMessages().pagerNoPageableSet()); 
					}							
				}				
			}		
		);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@TagEventsDeclaration({
		@TagEventDeclaration("onPage")
	})
	public void processEvents(WidgetCreatorContext context) throws InterfaceConfigException
	{
		CruxMetaDataElement element = context.getWidgetElement();
		T widget = (T)context.getWidget();
		PageEvtBind.bindEvent(element, widget);
		super.processEvents(context);
	}
}