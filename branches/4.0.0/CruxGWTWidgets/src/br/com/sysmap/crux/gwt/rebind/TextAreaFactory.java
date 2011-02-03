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

import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.creator.children.TextChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildren;

import com.google.gwt.user.client.ui.TextArea;


/**
 * A Factory for TextArea widgets
 * 
 * @author Thiago Bustamante
 *
 */
@DeclarativeFactory(id="textArea", library="gwt", targetWidget=TextArea.class)
@TagAttributes({
	@TagAttribute(value="characterWidth", type=Integer.class),
	@TagAttribute(value="visibleLines", type=Integer.class)
})
@TagChildren({
	@TagChild(TextAreaFactory.InnerTextProcessor.class)
})
public class TextAreaFactory extends TextBoxBaseFactory
{	
	@Override
	public String instantiateWidget(SourcePrinter out, JSONObject metaElem, String widgetId)
	{
		String varName = ViewFactoryCreator.createVariableName("textArea");
		String className = TextArea.class.getCanonicalName();
		out.println(className + " " + varName+" = new "+className+"();");
		return varName;
	}
	
	@TagChildAttributes(minOccurs="0", widgetProperty="value")
	public static class InnerTextProcessor extends TextChildProcessor<WidgetCreatorContext> {}	
}
