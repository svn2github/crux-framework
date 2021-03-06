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
package br.com.sysmap.crux.widgets.client.textarea;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.TextChildProcessor;
import br.com.sysmap.crux.core.client.screen.factory.HasDirectionFactory;
import br.com.sysmap.crux.gwt.client.TextBoxBaseFactory;

import com.google.gwt.dom.client.Element;

/**
 * Factory for TextArea widget
 * @author Gess� S. F. Daf�
 */
@DeclarativeFactory(id="textArea", library="widgets")
public class TextAreaFactory extends TextBoxBaseFactory<TextArea> 
       implements HasDirectionFactory<TextArea>
{	
	@Override
	@TagAttributes({
		@TagAttribute(value="characterWidth", type=Integer.class),
		@TagAttribute(value="visibleLines", type=Integer.class),
		@TagAttribute(value="maxLength", type=Integer.class)
	})
	public void processAttributes(WidgetFactoryContext<TextArea> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}

	@Override
	public TextArea instantiateWidget(Element element, String widgetId) 
	{
		return new TextArea();
	}
	
	@Override
	@TagChildren({
		@TagChild(InnerTextProcessor.class)
	})
	public void processChildren(WidgetFactoryContext<TextArea> context) throws InterfaceConfigException {}
	
	@TagChildAttributes(minOccurs="0", widgetProperty="value")
	public static class InnerTextProcessor extends TextChildProcessor<TextArea> {}	
}