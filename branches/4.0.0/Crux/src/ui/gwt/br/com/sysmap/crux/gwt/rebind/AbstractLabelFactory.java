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

import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasAllMouseHandlersFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasAutoHorizontalAlignmentFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasClickHandlersFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasDirectionEstimatorFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasDirectionFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasDoubleClickHandlersFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasTextFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasWordWrapFactory;

/**
 * Represents a Label DeclarativeFactory
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractLabelFactory extends WidgetCreator<WidgetCreatorContext> 
       implements HasDirectionFactory<WidgetCreatorContext>, HasWordWrapFactory<WidgetCreatorContext>, 
       			  HasTextFactory<WidgetCreatorContext>, HasClickHandlersFactory<WidgetCreatorContext>, 
       			  HasDoubleClickHandlersFactory<WidgetCreatorContext>, HasAllMouseHandlersFactory<WidgetCreatorContext>, 
                  HasAutoHorizontalAlignmentFactory<WidgetCreatorContext>, HasDirectionEstimatorFactory<WidgetCreatorContext>
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
