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
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;

import com.google.gwt.user.client.ui.PasswordTextBox;

/**
 * Represents a PasswordTextBoxFactory component
 * @author Thiago Bustamante
 *
 */
@DeclarativeFactory(id="passwordTextBox", library="gwt")
public class PasswordTextBoxFactory extends TextBoxBaseFactory  
{
	@Override
	@TagAttributes({
		@TagAttribute(value="maxLength", type=Integer.class),
		@TagAttribute(value="visibleLength", type=Integer.class)
	})
	public void processAttributes(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
	}
	
	@Override
	public String instantiateWidget(SourcePrinter out, JSONObject metaElem, String widgetId)
	{
		String varName = ViewFactoryCreator.createVariableName("passwordTextBox");
		String className = PasswordTextBox.class.getCanonicalName();
		out.println(className + " " + varName+" = new "+className+"();");
		return varName;
	}	
}
