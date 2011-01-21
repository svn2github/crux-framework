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

import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.AttributeProcessor;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.creator.HasChangeHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasDirectionEstimatorFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasDirectionFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasNameFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasTextFactory;

import com.google.gwt.user.client.ui.ValueBoxBase;


/**
 * Base class for text box based widget factories
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class ValueBoxBaseFactory<V, T extends ValueBoxBase<V>> extends FocusWidgetFactory<WidgetCreatorContext>
                implements HasChangeHandlersFactory<WidgetCreatorContext>, HasNameFactory<WidgetCreatorContext>, 
                           HasTextFactory<WidgetCreatorContext>, HasDirectionEstimatorFactory<WidgetCreatorContext>, 
                           HasDirectionFactory<WidgetCreatorContext>
{	
	public static enum TextAlign{center, justify, left, right}
	
	@Override
	@TagAttributes({
		@TagAttribute(value="readOnly", type=Boolean.class),
		@TagAttribute(value="alignment", type=TextAlign.class, processor=TextAlignmentProcessor.class)
	})
	public void processAttributes(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class TextAlignmentProcessor extends AttributeProcessor<WidgetCreatorContext>
	{
		@Override
		public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
		{
			TextAlign align = TextAlign.valueOf(attributeValue);
			String textAlignClassName = TextAlign.class.getCanonicalName();
			switch (align) {
				case center: out.println(context.getWidget() + ".setAlignment(" + textAlignClassName + ".CENTER);");
				break;
				case justify: out.println(context.getWidget() + ".setAlignment(" + textAlignClassName + ".JUSTIFY);");
				break;
				case left: out.println(context.getWidget() + ".setAlignment(" + textAlignClassName + ".LEFT);");
				break;
				case right: out.println(context.getWidget() + ".setAlignment(" + textAlignClassName + ".RIGHT);");
				break;
			}
		}
	}	
}
