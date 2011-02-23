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

import org.json.JSONObject;

import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.HTMLTag;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildren;

import com.google.gwt.user.cellview.client.CellTable;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="cellTable", library="gwt", targetWidget=CellTable.class)
@TagChildren({
	@TagChild(value=CellTableFactory.ColumnsProcessor.class)
})
public class CellTableFactory extends AbstractHasDataFactory
{
	@Override
	public String instantiateWidget(SourcePrinter out, JSONObject metaElem, String widgetId) throws CruxGeneratorException
	{
		String varName = createVariableName("widget");
		String className = getWidgetClassName()+"<"+getDataObject(metaElem)+">";
		String keyProvider = getkeyProvider(out, metaElem);
		out.println("final "+className + " " + varName+" = new "+className+"("+keyProvider+");");
		return varName;
	}
	
	@TagChildAttributes(tagName="column", minOccurs="1", maxOccurs="unbounded")
	@TagChildren({
		@TagChild(ColumnHeaderProcessor.class),
		@TagChild(ColumnFooterProcessor.class),
		@TagChild(ColumnCellProcessor.class)
	})
	public static class ColumnsProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		
	}
	
	@TagChildAttributes(tagName="header", type=String.class)
	@TagChildren({
		@TagChild(ColumnHeaderChoiceProcessor.class)
	})
	public static class ColumnHeaderProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		
	}

	@TagChildren({
		@TagChild(TextColumnHeaderProcessor.class),
		@TagChild(HTMLColumnHeaderProcessor.class)
	})
	public static class ColumnHeaderChoiceProcessor extends ChoiceChildProcessor<WidgetCreatorContext> {}
	
	@TagChildAttributes(tagName="text", type=String.class)
	public static class TextColumnHeaderProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		
	}

	@TagChildAttributes(tagName="html", type=HTMLTag.class)
	public static class HTMLColumnHeaderProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		
	}

	@TagChildAttributes(tagName="footer", minOccurs="0", maxOccurs="1")
	@TagChildren({
		@TagChild(ColumnFooterChoiceProcessor.class)
	})
	public static class ColumnFooterProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}	

	@TagChildren({
		@TagChild(TextColumnFooterProcessor.class),
		@TagChild(HTMLColumnFooterProcessor.class)
	})
	public static class ColumnFooterChoiceProcessor extends ChoiceChildProcessor<WidgetCreatorContext>
	{
		
	}
	
	@TagChildAttributes(tagName="text", type=String.class)
	public static class TextColumnFooterProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		
	}

	@TagChildAttributes(tagName="html", type=HTMLTag.class)
	public static class HTMLColumnFooterProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		
	}

	@TagChildAttributes(tagName="cell")
	public static class ColumnCellProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		
	}
	
}

