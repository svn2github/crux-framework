/*
 * Copyright 2011 Sysmap Solutions Software e Consultoria Ltda.
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

import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.FocusableFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasDataFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasKeyboardPagingPolicyFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributes;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributes({
	@TagAttribute(value="pageSize", type = Integer.class),
	@TagAttribute(value="pageStart", type = Integer.class),
	@TagAttribute(value="rowCount", type = Integer.class)
})
public abstract class AbstractHasDataFactory<C extends WidgetCreatorContext> extends AbstractCellFactory<C> 
	   implements FocusableFactory<C>, HasKeyboardPagingPolicyFactory<C>, 
	              HasDataFactory<C>
{
	@Override
	public void instantiateWidget(SourcePrinter out, C context) throws CruxGeneratorException
	{
		String className = getWidgetClassName()+"<"+getDataObject(context.getWidgetElement())+">";
		String keyProvider = getkeyProvider(out, context.getWidgetElement());
		out.println("final "+className + " " + context.getWidget()+" = new "+className+"("+keyProvider+");");
	}
}
