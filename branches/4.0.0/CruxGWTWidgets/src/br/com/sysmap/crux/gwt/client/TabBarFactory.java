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

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.creator.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;

/**
 * Factory for TabBar widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="tabBar", library="gwt")
public class TabBarFactory extends AbstractTabBarFactory
{
	@Override
	@TagChildren({
		@TagChild(TabProcessor.class)
	})
	public void processChildren(SourcePrinter out, TabBarContext context) throws CruxGeneratorException {}		

	public static class TabProcessor extends AbstractTabProcessor
	{
		@Override
		@TagChildren({
			@TagChild(TabItemProcessor.class)
		})	
		public void processChildren(SourcePrinter out, TabBarContext context) throws CruxGeneratorException
		{
			super.processChildren(out, context);
		}
	}
	
	public static class TabItemProcessor extends ChoiceChildProcessor<TabBarContext> 
	{
		@Override
		@TagChildren({
			@TagChild(TextTabProcessor.class),
			@TagChild(HTMLTabProcessor.class),
			@TagChild(WidgetTabProcessor.class)
		})		
		public void processChildren(SourcePrinter out, TabBarContext context) throws CruxGeneratorException {}
	}
	
	@TagChildAttributes(tagName="widget")
	public static class WidgetTabProcessor extends WidgetChildProcessor<TabBarContext> 
	{
		@Override
		@TagChildren({
			@TagChild(WidgetProcessor.class)
		})	
		public void processChildren(SourcePrinter out, TabBarContext context) throws CruxGeneratorException {}
	}
	
	public static class TextTabProcessor extends AbstractTextTabProcessor {}
	
	public static class HTMLTabProcessor extends AbstractHTMLTabProcessor {}
	
	public static class WidgetProcessor extends AbstractWidgetProcessor {}
}
