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

import br.com.sysmap.crux.core.client.screen.factory.HasAllMouseHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasAutoHorizontalAlignmentFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasClickHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasDirectionEstimatorFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasDirectionFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasDoubleClickHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasTextFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasWordWrapFactory;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;

import com.google.gwt.user.client.ui.Label;

/**
 * Represents a Label DeclarativeFactory
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractLabelFactory<T extends Label> extends WidgetCreator<T, WidgetCreatorContext> 
       implements HasDirectionFactory<T, WidgetCreatorContext>, HasWordWrapFactory<T, WidgetCreatorContext>, 
       			  HasTextFactory<T, WidgetCreatorContext>, HasClickHandlersFactory<T, WidgetCreatorContext>, 
       			  HasDoubleClickHandlersFactory<T, WidgetCreatorContext>, HasAllMouseHandlersFactory<T, WidgetCreatorContext>, 
                  HasAutoHorizontalAlignmentFactory<T, WidgetCreatorContext>, HasDirectionEstimatorFactory<T, WidgetCreatorContext>
{
}
