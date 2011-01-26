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

import com.google.gwt.user.client.ui.TabPanel;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.creator.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;

/**
 * Factory for TabPanel widgets
 * @author Thiago da Rosa de Bustamante
 */
@SuppressWarnings("deprecation")
@DeclarativeFactory(id="tabPanel", library="gwt", targetWidget=TabPanel.class)
public class TabPanelFactory extends AbstractTabPanelFactory 
{
	@Override
	@TagChildren({
		@TagChild(TabProcessor.class)
	})	
	public void processChildren(SourcePrinter out, TabPanelContext context) throws CruxGeneratorException 
	{
	}
	
	public static class TabProcessor extends AbstractTabProcessor
	{
		@TagChildren({
			@TagChild(TabTitleProcessor.class), 
			@TagChild(TabWidgetProcessor.class)
		})	
		public void processChildren(SourcePrinter out, TabPanelContext context) throws CruxGeneratorException
		{
			super.processChildren(out, context);
		}
		
	}
	
	@TagChildAttributes(minOccurs="0")
	public static class TabTitleProcessor extends ChoiceChildProcessor<TabPanelContext> 
	{
		@Override
		@TagChildren({
			@TagChild(TextTabProcessor.class),
			@TagChild(HTMLTabProcessor.class),
			@TagChild(WidgetTitleTabProcessor.class)
		})		
		public void processChildren(SourcePrinter out, TabPanelContext context) throws CruxGeneratorException {}
		
	}
	
	public static class TextTabProcessor extends AbstractTextTabProcessor {}
	
	public static class HTMLTabProcessor extends AbstractHTMLTabProcessor {}
	
	@TagChildAttributes(tagName="tabWidget")
	public static class WidgetTitleTabProcessor extends WidgetChildProcessor<TabPanelContext> 
	{
		@Override
		@TagChildren({
			@TagChild(WidgetTitleProcessor.class)
		})	
		public void processChildren(SourcePrinter out, TabPanelContext context) throws CruxGeneratorException {}
	}

	public static class WidgetTitleProcessor extends AbstractWidgetTitleProcessor {}
	
	@TagChildAttributes(tagName="panelContent")
	public static class TabWidgetProcessor extends WidgetChildProcessor<TabPanelContext> 
	{
		@Override
		@TagChildren({
			@TagChild(WidgetContentProcessor.class)
		})	
		public void processChildren(SourcePrinter out, TabPanelContext context) throws CruxGeneratorException {}
	}

	public static class WidgetContentProcessor extends AbstractWidgetContentProcessor {}	
}
