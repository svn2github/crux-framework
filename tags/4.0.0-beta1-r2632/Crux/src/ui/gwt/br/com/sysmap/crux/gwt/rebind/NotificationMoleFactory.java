/*
 * Copyright 2011 Sysmap Solutions Software e Consultoria Ltda.
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

import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;

import com.google.gwt.user.client.ui.NotificationMole;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="notificationMole", library="gwt", targetWidget=NotificationMole.class)
@TagAttributes({
	@TagAttribute(value="animationDuration", type=Integer.class),
	@TagAttribute(value="message", required=true, supportsI18N=true),
})
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="show", type=Boolean.class)
})
public class NotificationMoleFactory extends WidgetCreator<WidgetCreatorContext>  
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
	
	@Override
	public void processAttributes(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String show = context.readChildProperty("show");
		if (!StringUtils.isEmpty(show) && Boolean.parseBoolean(show))
		{
			printlnPostProcessing(context.getWidget()+".show();");
		}
	}
}

