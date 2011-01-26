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

import org.json.JSONObject;

import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.creator.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildren;

import com.google.gwt.user.client.ui.StackPanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="stackPanel", library="gwt", targetWidget=StackPanel.class)
public class StackPanelFactory extends AbstractStackPanelFactory
{
	@Override
	public String instantiateWidget(SourcePrinter out, JSONObject metaElem, String widgetId)
	{
		String varName = ViewFactoryCreator.createVariableName("stackPanel");
		String className = StackPanel.class.getCanonicalName();
		out.println(className + " " + varName+" = new "+className+"();");
		return varName;
	}	

	@Override
	@TagChildren({
		@TagChild(StackItemProcessor.class)
	})	
	public void processChildren(SourcePrinter out, AbstractStackPanelFactoryContext context) throws CruxGeneratorException{}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", tagName="stackItem")
	public static class StackItemProcessor extends WidgetChildProcessor<AbstractStackPanelFactoryContext>
	{
		@Override
		@TagChildren({
			@TagChild(TitleProcessor.class),
			@TagChild(ContentProcessor.class)
		})	
		public void processChildren(SourcePrinter out, AbstractStackPanelFactoryContext context) throws CruxGeneratorException {}
	}
	
	@TagChildAttributes(minOccurs="0")
	public static class TitleProcessor extends ChoiceChildProcessor<AbstractStackPanelFactoryContext>
	{
		@Override
		@TagChildren({
			@TagChild(TitleTextProcessor.class),
			@TagChild(TitleHTMLProcessor.class)
		})	
		public void processChildren(SourcePrinter out, AbstractStackPanelFactoryContext context) throws CruxGeneratorException {}
	}	
	
	@TagChildAttributes(minOccurs="0", tagName="widget")
	public static class ContentProcessor extends WidgetChildProcessor<AbstractStackPanelFactoryContext> 
	{
		@Override
		@TagChildren({
			@TagChild(ContentWidgetProcessor.class)
		})	
		public void processChildren(SourcePrinter out, AbstractStackPanelFactoryContext context) throws CruxGeneratorException {}
	}
	
	public static class ContentWidgetProcessor extends AbstractContentWidgetProcessor {}
	
	public static class TitleTextProcessor extends AbstractTitleTextProcessor {}
	
	public static class TitleHTMLProcessor extends AbstractTitleHTMLProcessor {}
}
