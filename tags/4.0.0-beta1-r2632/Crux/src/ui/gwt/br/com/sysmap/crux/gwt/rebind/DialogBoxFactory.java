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

import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasAnimationFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasCloseHandlersFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasHTMLFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagConstraints;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildren;

import com.google.gwt.user.client.ui.DialogBox;

/**
 * @author Thiago da Rosa de Bustamante
 * @author Gesse S. F. Dafe <code>gessedafe@gmail.com</code>
 */
@DeclarativeFactory(id="dialogBox", library="gwt", attachToDOM=false, targetWidget= DialogBox.class)
@TagAttributes({
	@TagAttribute(value="previewingAllNativeEvents", type=Boolean.class),
	@TagAttribute(value="autoHideOnHistoryEventsEnabled", type=Boolean.class),
	@TagAttribute("glassStyleName"),
	@TagAttribute(value="glassEnabled", type=Boolean.class),
	@TagAttribute(value="modal", type=Boolean.class),
	@TagAttribute(value="autoHide", type=Boolean.class, property="autoHideEnabled")
})
@TagChildren({
	@TagChild(DialogBoxFactory.WidgetContentProcessor.class)
})
public class DialogBoxFactory extends PanelFactory<WidgetCreatorContext>
       implements HasAnimationFactory<WidgetCreatorContext>, 
                  HasCloseHandlersFactory<WidgetCreatorContext>, 
                  HasHTMLFactory<WidgetCreatorContext>
{
    @Override
    public WidgetCreatorContext instantiateContext()
    {
        return new WidgetCreatorContext();
    }
    
    @TagConstraints(minOccurs="0", maxOccurs="1")
    public static class WidgetContentProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}		
}
