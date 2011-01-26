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

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.HTMLTag;

import com.google.gwt.user.client.ui.RadioButton;


/**
 * Represents a RadioButtonFactory component
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="radioButton", library="gwt", targetWidget=RadioButton.class)
public class RadioButtonFactory extends AbstractCheckBoxFactory
{
	@Override
	public String instantiateWidget(SourcePrinter out, JSONObject metaElem, String widgetId)
	{
		String varName = ViewFactoryCreator.createVariableName("radioButton");
		String className = RadioButton.class.getCanonicalName();
		out.println(className + " " + varName+" = new "+className+"("+metaElem.optString("name")+");");
		return varName;
	}	

	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="name", required=true)
	})
	@Override
	public void processAttributes(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
	    super.processAttributes(out, context);
	}
	
	
	@Override
	@TagChildren({
		@TagChild(value=ContentProcessor.class, autoProcess=false)
	})
	public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
	}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded", type=HTMLTag.class)
	public static class ContentProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
}
