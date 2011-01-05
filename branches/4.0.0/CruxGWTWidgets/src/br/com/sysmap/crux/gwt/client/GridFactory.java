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

import br.com.sysmap.crux.core.client.collection.Array;
import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Grid;

class GridFactoryContext extends HTMLTableFactoryContext
{
	boolean cellsInitialized = false;
}

/**
 * Factory for Grid widget
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="grid", library="gwt")
public class GridFactory extends HTMLTableFactory<Grid, GridFactoryContext>
{

	@Override
	public Grid instantiateWidget(CruxMetaDataElement element, String widgetId)
	{
		return new Grid();
	}

	/**
	 * Populate the panel with declared items
	 * @param element
	 * @throws InterfaceConfigException 
	 */
	@Override
	@TagChildren({
		@TagChild(GridRowProcessor.class)
	})
	public void processChildren(GridFactoryContext context) throws InterfaceConfigException	
	{
		Array<CruxMetaDataElement> children = ensureChildren(context.getWidgetElement(), true);
		
		int count = getNonNullChildrenCount(children);
		
		Grid widget = context.getWidget();
		widget.resizeRows(count);
	}

	/**
	 * @param children
	 * @return
	 */
	private static int getNonNullChildrenCount(Array<CruxMetaDataElement> children)
    {
	    int count = 0;
		int size = children.size();
		for (int i=0; i<size; i++)
		{
			if (children.get(i) != null)
			{
				count++;
			}
		}
	    return count;
    }
	
	@TagChildAttributes(tagName="row", minOccurs="0", maxOccurs="unbounded")
	public static class GridRowProcessor extends TableRowProcessor<Grid, GridFactoryContext>
	{
		@Override
		@TagChildren({
			@TagChild(GridCellProcessor.class)
		})
		public void processChildren(GridFactoryContext context) throws InterfaceConfigException
		{
			if (!context.cellsInitialized)
			{
				Array<CruxMetaDataElement> children = ensureChildren(context.getChildElement(), true);
				Grid rootWidget = context.getWidget();
				rootWidget.resizeColumns(getNonNullChildrenCount(children));
				context.cellsInitialized = true;
			}
			
			super.processChildren(context);
		}
	}

	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", tagName="cell")
	public static class GridCellProcessor extends TableCellProcessor<Grid, GridFactoryContext>
	{
		@Override
		@TagChildren({
			@TagChild(GridChildrenProcessor.class)
		})
		public void processChildren(GridFactoryContext context) throws InterfaceConfigException
		{
			
			super.processChildren(context);
		}
	}
	
	@TagChildAttributes(minOccurs="0")
	public static class GridChildrenProcessor extends ChoiceChildProcessor<Grid, GridFactoryContext> 
	{
		protected GWTMessages messages = GWT.create(GWTMessages.class);
		
		@Override
		@TagChildren({
			@TagChild(GridCellTextProcessor.class),
			@TagChild(GridCellHTMLProcessor.class),
			@TagChild(GridCellWidgetProcessor.class)
		})
		public void processChildren(GridFactoryContext context) throws InterfaceConfigException {}
	}
	
	public static class GridCellTextProcessor extends CellTextProcessor<Grid, GridFactoryContext>{}
	public static class GridCellHTMLProcessor extends CellHTMLProcessor<Grid, GridFactoryContext>{}
	public static class GridCellWidgetProcessor extends CellWidgetProcessor<Grid, GridFactoryContext>
	{
		@Override
		@TagChildren({
			@TagChild(GridWidgetProcessor.class)
		})	
		public void processChildren(GridFactoryContext context) throws InterfaceConfigException {}
		
	}
	public static class GridWidgetProcessor extends WidgetProcessor<Grid, GridFactoryContext>{} 
	
}
