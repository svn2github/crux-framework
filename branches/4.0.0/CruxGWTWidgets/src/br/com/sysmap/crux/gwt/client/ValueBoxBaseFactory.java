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
import br.com.sysmap.crux.core.client.screen.AttributeProcessor;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.factory.HasChangeHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasDirectionEstimatorFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasDirectionFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasNameFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasTextFactory;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;

import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.ValueBoxBase;


/**
 * Base class for text box based widget factories
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class ValueBoxBaseFactory<V, T extends ValueBoxBase<V>> extends FocusWidgetFactory<T, WidgetCreatorContext>
                implements HasChangeHandlersFactory<T, WidgetCreatorContext>, HasNameFactory<T, WidgetCreatorContext>, 
                           HasTextFactory<T, WidgetCreatorContext>, HasDirectionEstimatorFactory<T, WidgetCreatorContext>, 
                           HasDirectionFactory<T, WidgetCreatorContext>
{	
	public static enum TextAlign{center, justify, left, right}
	
	@Override
	@TagAttributes({
		@TagAttribute(value="readOnly", type=Boolean.class),
		@TagAttribute(value="alignment", type=TextAlign.class, processor=TextAlignmentProcessor.class)
	})
	public void processAttributes(WidgetCreatorContext context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class TextAlignmentProcessor implements AttributeProcessor<WidgetCreatorContext>
	{
		public void processAttribute(WidgetCreatorContext context, String textAlignment)
		{
			TextBoxBase widget = (TextBoxBase) context.getWidget();
			
			TextAlign align = TextAlign.valueOf(textAlignment);
			switch (align) {
				case center: widget.setAlignment(ValueBoxBase.TextAlignment.CENTER);		
				break;
				case justify: widget.setAlignment(ValueBoxBase.TextAlignment.JUSTIFY);		
				break;
				case left: widget.setAlignment(ValueBoxBase.TextAlignment.LEFT);		
				break;
				case right: widget.setAlignment(ValueBoxBase.TextAlignment.RIGHT);		
				break;
			}
		}
	}	
}
