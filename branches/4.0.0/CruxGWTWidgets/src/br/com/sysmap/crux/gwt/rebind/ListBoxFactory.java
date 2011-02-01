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
import br.com.sysmap.crux.core.rebind.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildren;

import com.google.gwt.user.client.ui.ListBox;

/**
 * Represents a List Box component
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="listBox", library="gwt", targetWidget=ListBox.class)
public class ListBoxFactory extends AbstractListBoxFactory
{
	@Override
	@TagChildren({
		@TagChild(ListBoxItemsProcessor.class)
	})
	public void processChildren(SourcePrinter out, ListBoxContext context) throws CruxGeneratorException {}	
	
	public static class ListBoxItemsProcessor extends ItemsProcessor
	{		
	}
	
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="multiple", type=Boolean.class)
	})
	public void processAttributes(SourcePrinter out, ListBoxContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
	}

	@Override
	public String instantiateWidget(SourcePrinter out, JSONObject metaElem, String widgetId)
	{
		String varName = ViewFactoryCreator.createVariableName("label");
		String className = getWidgetClassName();
		String multiple = metaElem.optString("multiple");
		if (multiple != null && multiple.trim().length() > 0)
		{
			out.println(className + " " + varName+" = new "+className+"("+Boolean.parseBoolean(multiple)+");");

		}
		else
		{
			out.println(className + " " + varName+" = new "+className+"();");
		}
		return varName;
	}
}