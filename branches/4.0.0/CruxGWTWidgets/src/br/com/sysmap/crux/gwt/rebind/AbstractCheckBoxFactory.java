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
package br.com.sysmap.crux.gwt.rebind;

import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.HasHTMLFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasNameFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasValueChangeHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasWordWrapFactory;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributes;

/**
 * Factory for ChecBox widgets.
 * @author Thiago Bustamante
 *
 */
@TagAttributes({
	@TagAttribute(value="checked", type=Boolean.class, property="value"),
	@TagAttribute("formValue")	
})
public abstract class AbstractCheckBoxFactory extends FocusWidgetFactory<WidgetCreatorContext> 
       implements HasNameFactory<WidgetCreatorContext>, HasValueChangeHandlersFactory<WidgetCreatorContext>, 
       			  HasHTMLFactory<WidgetCreatorContext>, HasWordWrapFactory<WidgetCreatorContext>
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
	
}
