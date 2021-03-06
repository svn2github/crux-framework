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

import org.json.JSONArray;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagConstraints;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildren;

import com.google.gwt.user.client.ui.Grid;

class GridFactoryContext extends HTMLTableFactoryContext
{
	boolean cellsInitialized = false;
}

/**
 * Factory for Grid widget
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="grid", library="gwt", targetWidget=Grid.class)
@TagChildren({
	@TagChild(GridFactory.GridRowProcessor.class)
})
public class GridFactory extends HTMLTableFactory<GridFactoryContext>
{
	/**
	 * Populate the panel with declared items
	 * @param element
	 * @throws CruxGeneratorException 
	 */
	@Override
	public void processChildren(SourcePrinter out, GridFactoryContext context) throws CruxGeneratorException	
	{
		JSONArray children = ensureChildren(context.getWidgetElement(), true);
		
		int count = getNonNullChildrenCount(children);
		
		String widget = context.getWidget();
		out.println(widget+".resizeRows("+count+");");
	}

	/**
	 * @param children
	 * @return
	 */
	private static int getNonNullChildrenCount(JSONArray children)
    {
	    int count = 0;
		int size = children.length();
		for (int i=0; i<size; i++)
		{
			if (children.opt(i) != null)
			{
				count++;
			}
		}
	    return count;
    }
	
	@TagConstraints(tagName="row", minOccurs="0", maxOccurs="unbounded")
	@TagChildren({
		@TagChild(GridCellProcessor.class)
	})
	public static class GridRowProcessor extends TableRowProcessor<GridFactoryContext>
	{
		@Override
		public void processChildren(SourcePrinter out, GridFactoryContext context) throws CruxGeneratorException
		{
			if (!context.cellsInitialized)
			{
				JSONArray children = ensureChildren(context.getChildElement(), true);
				String rootWidget = context.getWidget();
				out.println(rootWidget+".resizeColumns("+getNonNullChildrenCount(children)+");");
				context.cellsInitialized = true;
			}
			
			super.processChildren(out, context);
		}
	}

	@TagConstraints(minOccurs="0", maxOccurs="unbounded", tagName="cell")
	@TagChildren({
		@TagChild(GridChildrenProcessor.class)
	})
	public static class GridCellProcessor extends TableCellProcessor<GridFactoryContext>
	{
		@Override
		public void processChildren(SourcePrinter out, GridFactoryContext context) throws CruxGeneratorException
		{
			
			super.processChildren(out, context);
		}
	}
	
	@TagConstraints(minOccurs="0")
	@TagChildren({
		@TagChild(GridCellTextProcessor.class),
		@TagChild(GridCellHTMLProcessor.class),
		@TagChild(GridCellWidgetProcessor.class)
	})
	public static class GridChildrenProcessor extends ChoiceChildProcessor<GridFactoryContext> 
	{
		protected GWTMessages messages = MessagesFactory.getMessages(GWTMessages.class);
		
		@Override
		public void processChildren(SourcePrinter out, GridFactoryContext context) throws CruxGeneratorException {}
	}
	
	public static class GridCellTextProcessor extends CellTextProcessor<GridFactoryContext>{}
	public static class GridCellHTMLProcessor extends CellHTMLProcessor<GridFactoryContext>{}

	@TagChildren({
		@TagChild(GridWidgetProcessor.class)
	})	
	public static class GridCellWidgetProcessor extends CellWidgetProcessor<GridFactoryContext>
	{
		@Override
		public void processChildren(SourcePrinter out, GridFactoryContext context) throws CruxGeneratorException {}
		
	}
	public static class GridWidgetProcessor extends WidgetProcessor<GridFactoryContext>{} 
	
	@Override
    public GridFactoryContext instantiateContext()
    {
	    return new GridFactoryContext();
    }
}
