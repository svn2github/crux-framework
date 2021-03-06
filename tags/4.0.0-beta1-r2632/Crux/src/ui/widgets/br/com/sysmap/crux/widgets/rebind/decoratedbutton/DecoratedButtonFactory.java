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
package br.com.sysmap.crux.widgets.rebind.decoratedbutton;

import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasTextFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.gwt.rebind.FocusWidgetFactory;
import br.com.sysmap.crux.widgets.client.decoratedbutton.DecoratedButton;

/**
 * Factory for Decorated Button widget
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="decoratedButton", library="widgets", targetWidget=DecoratedButton.class)
public class DecoratedButtonFactory extends FocusWidgetFactory<WidgetCreatorContext> implements HasTextFactory<WidgetCreatorContext>
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}