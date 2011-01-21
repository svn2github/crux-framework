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

import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.HasHTMLFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasNameFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasValueChangeHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasWordWrapFactory;

import com.google.gwt.user.client.ui.CheckBox;

/**
 * CheckBoxFactory DeclarativeFactory.
 * @author Thiago Bustamante
 *
 */
public abstract class AbstractCheckBoxFactory<T extends CheckBox> extends FocusWidgetFactory<T, WidgetCreatorContext> 
       implements HasNameFactory<T, WidgetCreatorContext>, HasValueChangeHandlersFactory<T, WidgetCreatorContext>, 
       			  HasHTMLFactory<T, WidgetCreatorContext>, HasWordWrapFactory<T, WidgetCreatorContext>
{
	/**
	 * process widget attributes
	 * @throws InterfaceConfigException 
	 */
	@Override
	@TagAttributes({
		@TagAttribute(value="checked", type=Boolean.class, property="value"),
		@TagAttribute("formValue")	
	})
	public void processAttributes(WidgetCreatorContext context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
}
