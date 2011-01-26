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
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.creator.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildren;

import com.google.gwt.user.client.ui.VerticalSplitPanel;

/**
 * A Factory for VerticalSplitPanel widgets
 * @author Thiago Bustamante
 */
@SuppressWarnings("deprecation")
@DeclarativeFactory(id="verticalSplitPanel", library="gwt", targetWidget=VerticalSplitPanel.class)
public class VerticalSplitPanelFactory extends PanelFactory<WidgetCreatorContext>
{
	@Override
	public String instantiateWidget(SourcePrinter out, JSONObject metaElem, String widgetId)
	{
		String varName = ViewFactoryCreator.createVariableName("verticalSplitPanel");
		String className = VerticalSplitPanel.class.getCanonicalName();
		out.println(className + " " + varName+" = new "+className+"();");
		return varName;
	}
	
	@Override
	@TagChildren({
		@TagChild(TopProcessor.class),
		@TagChild(BottomProcessor.class)
	})
	public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException {}
	
	@TagChildAttributes(tagName="top", minOccurs="0")
	public static class TopProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		@Override
		@TagChildren({
			@TagChild(TopWidgeProcessor.class)
		})
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException {}
	}
	
	@TagChildAttributes(tagName="bottom", minOccurs="0")
	public static class BottomProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		@Override
		@TagChildren({
			@TagChild(BottomWidgeProcessor.class)
		})
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException {}
	}

	@TagChildAttributes(widgetProperty="topWidget")
	public static class TopWidgeProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagChildAttributes(widgetProperty="bottomWidget")
	public static class BottomWidgeProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}
}
