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
package br.com.sysmap.crux.core.rebind.screen.widget.creator.children;

import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class WidgetChildProcessor<C extends WidgetCreatorContext>
{
	private WidgetCreator<?> widgetCreator;

	public void processChildren(SourcePrinter out, C context) throws CruxGeneratorException{}
	
	public static class AnyWidget{}
	public static class AnyTag{}
	public static class HTMLTag{}
	
	/**
	 * @param widgetCreator
	 */
	public void setWidgetCreator(WidgetCreator<?> widgetCreator)
    {
		this.widgetCreator = widgetCreator;
    }

	/**
	 * @return
	 */
	public WidgetCreator<?> getWidgetCreator()
    {
    	return widgetCreator;
    }
}
