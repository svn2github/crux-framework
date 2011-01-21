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
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.HasAllFocusHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasAllKeyHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasAllMouseHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasClickHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasDoubleClickHandlersFactory;

import com.google.gwt.user.client.ui.FocusPanel;

/**
 * Represents a FocusPanelFactory
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="focusPanel", library="gwt")
public class FocusPanelFactory extends PanelFactory<FocusPanel, WidgetCreatorContext>
	   implements HasAllMouseHandlersFactory<FocusPanel, WidgetCreatorContext>, 
	   			  HasClickHandlersFactory<FocusPanel, WidgetCreatorContext>, 
	   			  HasAllFocusHandlersFactory<FocusPanel, WidgetCreatorContext>, 
	   			  HasDoubleClickHandlersFactory<FocusPanel, WidgetCreatorContext>,
	   			  HasAllKeyHandlersFactory<FocusPanel, WidgetCreatorContext>
	   			
{
	@Override
	@TagAttributes({
		@TagAttribute(value="tabIndex", type=Integer.class),
		@TagAttribute(value="accessKey", type=Character.class),
		@TagAttribute(value="focus", type=Boolean.class)
	})
	public void processAttributes(WidgetCreatorContext context) throws InterfaceConfigException 
	{
		super.processAttributes(context);
	}
	
	@Override
	public FocusPanel instantiateWidget(CruxMetaDataElement element, String widgetId) 
	{
		return new FocusPanel();
	}
}