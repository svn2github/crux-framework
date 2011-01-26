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

import org.json.JSONObject;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildLazyCondition;
import br.com.sysmap.crux.core.client.declarative.TagChildLazyConditions;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.children.AnyWidgetChildProcessor;

/**
 * A Panel which content is only rendered when it becomes visible for the first time.
 * 
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="lazyPanel", library="gwt")
public class LazyPanelFactory extends PanelFactory<WidgetCreatorContext> 
{
	@Override
	public String instantiateWidget(SourcePrinter out, JSONObject metaElem, String widgetId) throws CruxGeneratorException
	{
		String varName = createVariableName("widget");
		String className = getWidgetClassName();
		out.println(className + " " + varName+" = new "+className+"(){");
		out.println("protected Widget createWidget(){");
		out.println("return null;");
		out.println("}");
		out.println("public void setVisible(boolean visible){");
		out.println("setVisible(getElement(), visible);");
		out.println("}");
		out.println("};");
		return varName;
	}
	
	@Override
	@TagChildren({
		@TagChild(WidgetContentProcessor.class)
	})
	public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException 
	{
		super.processChildren(out, context);
	}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="1")
	@TagChildLazyConditions(all={
		@TagChildLazyCondition(property="visible", notEquals="true")
	})	
	public static class WidgetContentProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}	
}
