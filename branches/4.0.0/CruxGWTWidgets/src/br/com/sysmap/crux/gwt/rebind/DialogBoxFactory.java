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

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.HasAnimationFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasCloseHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasHTMLFactory;

/**
 * @author Thiago da Rosa de Bustamante
 * @author Gesse S. F. Dafe <code>gessedafe@gmail.com</code>
 */
@DeclarativeFactory(id="dialogBox", library="gwt", attachToDOM=false)
public class DialogBoxFactory extends PanelFactory<WidgetCreatorContext>
       implements HasAnimationFactory<WidgetCreatorContext>, 
                  HasCloseHandlersFactory<WidgetCreatorContext>, 
                  HasHTMLFactory<WidgetCreatorContext>
{
	@Override
	@TagAttributes({
		@TagAttribute(value="previewingAllNativeEvents", type=Boolean.class),
		@TagAttribute(value="autoHideOnHistoryEventsEnabled", type=Boolean.class),
		@TagAttribute("glassStyleName"),
		@TagAttribute(value="glassEnabled", type=Boolean.class),
		@TagAttribute(value="modal", type=Boolean.class),
		@TagAttribute(value="autoHide", type=Boolean.class, property="autoHideEnabled")
	})
	public void processAttributes(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
	}
}
