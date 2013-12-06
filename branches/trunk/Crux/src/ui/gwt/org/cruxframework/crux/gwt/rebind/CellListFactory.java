/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.gwt.rebind;

import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;

import com.google.gwt.user.cellview.client.CellList;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="cellList", library="gwt", targetWidget=CellList.class)
@TagChildren({
	@TagChild(value=CellListFactory.CellListChildProcessor.class, autoProcess=false)
})
public class CellListFactory extends AbstractHasDataFactory<WidgetCreatorContext>
{
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName()+"<"+getDataObject(context.getWidgetElement())+">";
		String cell = getCell(out, context.getWidgetElement(), context.getWidgetId());
		String keyProvider = getkeyProvider(out, context.getWidgetElement());
		out.println("final "+className + " " + context.getWidget()+" = new "+className+"("+cell+", "+keyProvider+");");
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }	
}

