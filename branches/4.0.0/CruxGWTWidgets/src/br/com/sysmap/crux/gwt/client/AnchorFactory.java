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
import br.com.sysmap.crux.core.client.screen.factory.HasDirectionFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasHTMLFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasHorizontalAlignmentFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasNameFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasWordWrapFactory;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;

import com.google.gwt.user.client.ui.Anchor;

/**
 * Represents an AnchorFactory component
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="anchor", library="gwt")
public class AnchorFactory extends FocusWidgetFactory<Anchor, WidgetCreatorContext> 
	   implements HasHTMLFactory<Anchor, WidgetCreatorContext>, HasNameFactory<Anchor, WidgetCreatorContext>, 
	              HasWordWrapFactory<Anchor, WidgetCreatorContext>, HasDirectionFactory<Anchor, WidgetCreatorContext>, 
	              HasHorizontalAlignmentFactory<Anchor, WidgetCreatorContext>
{
	@Override
	@TagAttributes({
		@TagAttribute("href"),
		@TagAttribute("target")
	})
	public void processAttributes(WidgetCreatorContext context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}

	@Override
	public Anchor instantiateWidget(CruxMetaDataElement element, String widgetId) 
	{
		return new Anchor();
	}	
}
