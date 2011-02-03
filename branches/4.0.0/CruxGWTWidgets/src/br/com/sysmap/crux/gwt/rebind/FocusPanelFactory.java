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
import br.com.sysmap.crux.core.rebind.widget.creator.HasAllFocusHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasAllKeyHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasAllMouseHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasClickHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasDoubleClickHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildren;

import com.google.gwt.user.client.ui.FocusPanel;

/**
 * A Factory for FocusPanel widgets
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="focusPanel", library="gwt", targetWidget=FocusPanel.class)
@TagAttributes({
	@TagAttribute(value="tabIndex", type=Integer.class),
	@TagAttribute(value="accessKey", type=Character.class),
	@TagAttribute(value="focus", type=Boolean.class)
})
@TagChildren({
	@TagChild(FocusPanelFactory.WidgetContentProcessor.class)
})
public class FocusPanelFactory extends PanelFactory<WidgetCreatorContext>
	   implements HasAllMouseHandlersFactory<WidgetCreatorContext>, 
	   			  HasClickHandlersFactory<WidgetCreatorContext>, 
	   			  HasAllFocusHandlersFactory<WidgetCreatorContext>, 
	   			  HasDoubleClickHandlersFactory<WidgetCreatorContext>,
	   			  HasAllKeyHandlersFactory<WidgetCreatorContext>
	   			
{
    
    @TagChildAttributes(minOccurs="0", maxOccurs="1")
    public static class WidgetContentProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}		
    
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}