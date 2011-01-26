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
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;

/**
 * Represents a HorizontalSplitPanel
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="horizontalSplitPanel", library="gwt")
public class HorizontalSplitPanelFactory extends PanelFactory<WidgetCreatorContext>
{

	@Override
	@TagChildren({
		@TagChild(LeftProcessor.class),
		@TagChild(RightProcessor.class)
	})
	public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException {}
	
	@TagChildAttributes(tagName="left", minOccurs="0")
	public static class LeftProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		@Override
		@TagChildren({
			@TagChild(LeftWidgeProcessor.class)
		})
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException {}
	}
	
	@TagChildAttributes(tagName="right", minOccurs="0")
	public static class RightProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		@Override
		@TagChildren({
			@TagChild(RightWidgeProcessor.class)
		})
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException {}
	}

	@TagChildAttributes(widgetProperty="leftWidget")
	public static class LeftWidgeProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagChildAttributes(widgetProperty="rightWidget")
	public static class RightWidgeProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}
}
