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
package br.com.sysmap.crux.core.rebind.widget.creator;

import br.com.sysmap.crux.core.client.declarative.TagEvent;
import br.com.sysmap.crux.core.client.declarative.TagEvents;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.creator.event.MouseDownEvtBind;
import br.com.sysmap.crux.core.rebind.widget.creator.event.MouseMoveEvtBind;
import br.com.sysmap.crux.core.rebind.widget.creator.event.MouseOutEvtBind;
import br.com.sysmap.crux.core.rebind.widget.creator.event.MouseOverEvtBind;
import br.com.sysmap.crux.core.rebind.widget.creator.event.MouseUpEvtBind;
import br.com.sysmap.crux.core.rebind.widget.creator.event.MouseWheelEvtBind;

import com.google.gwt.event.dom.client.HasAllMouseHandlers;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface HasAllMouseHandlersFactory<T extends HasAllMouseHandlers, C extends WidgetCreatorContext>
{
	@TagEvents({
		@TagEvent(MouseDownEvtBind.class),
		@TagEvent(MouseUpEvtBind.class),
		@TagEvent(MouseOverEvtBind.class),
		@TagEvent(MouseOutEvtBind.class),
		@TagEvent(MouseMoveEvtBind.class),
		@TagEvent(MouseWheelEvtBind.class)
	})	
	void processEvents(SourcePrinter out, C context) throws InterfaceConfigException;
}
