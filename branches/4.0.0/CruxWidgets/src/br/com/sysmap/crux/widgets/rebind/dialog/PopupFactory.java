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
package br.com.sysmap.crux.widgets.rebind.dialog;

import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.HasAnimationFactory;
import br.com.sysmap.crux.core.rebind.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributes;
import br.com.sysmap.crux.widgets.client.dialog.Popup;
import br.com.sysmap.crux.widgets.rebind.event.HasBeforeCloseHandlersFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="popup", library="widgets", targetWidget=Popup.class, attachToDOM=false)
public class PopupFactory extends WidgetCreator<WidgetCreatorContext> 
       implements HasAnimationFactory<WidgetCreatorContext>, HasBeforeCloseHandlersFactory<WidgetCreatorContext>
{
	@Override
	@TagAttributes({
		@TagAttribute(value="title", supportsI18N=true),
		@TagAttribute("url"),
		@TagAttribute(value="closeable", type=Boolean.class)
	})
	public void processAttributes(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
	}

	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
