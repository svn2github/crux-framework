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
package br.com.sysmap.crux.widgets.rebind.transferlist;

import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagEvent;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagEvents;
import br.com.sysmap.crux.gwt.rebind.CompositeFactory;
import br.com.sysmap.crux.widgets.client.transferlist.TransferList;
import br.com.sysmap.crux.widgets.rebind.event.BeforeMoveItemsEvtBind;

/**
 * Factory for Transfer List widget
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="transferList", library="widgets", targetWidget=TransferList.class)
@TagAttributes({
	@TagAttribute(value="leftToRightButtonText", supportsI18N=true),
	@TagAttribute(value="rightToLeftButtonText", supportsI18N=true),
	@TagAttribute(value="leftListLabel", supportsI18N=true),
	@TagAttribute(value="rightListLabel", supportsI18N=true),
	@TagAttribute(value="visibleItemCount", type=Integer.class),
	@TagAttribute(value="multiTransferFromLeft", type=Boolean.class, defaultValue="true"),
	@TagAttribute(value="multiTransferFromRight", type=Boolean.class, defaultValue="true")
})
@TagEvents({
	@TagEvent(BeforeMoveItemsEvtBind.class)
})
public class TransferListFactory extends CompositeFactory<WidgetCreatorContext>
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}