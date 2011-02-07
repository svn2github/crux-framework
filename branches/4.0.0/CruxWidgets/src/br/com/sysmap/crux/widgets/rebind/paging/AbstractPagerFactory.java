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
package br.com.sysmap.crux.widgets.rebind.paging;

import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagEvent;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagEvents;
import br.com.sysmap.crux.widgets.client.paging.Pageable;
import br.com.sysmap.crux.widgets.rebind.WidgetGeneratorMessages;
import br.com.sysmap.crux.widgets.rebind.event.PageEvtBind;

/**
 * @author Gesse S. F. Dafe
 */
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="pageable", required=true),
	@TagAttributeDeclaration(value="enabled", type=Boolean.class)
})
@TagEvents({
	@TagEvent(PageEvtBind.class)
})
public abstract class AbstractPagerFactory extends WidgetCreator<WidgetCreatorContext>
{
	protected static WidgetGeneratorMessages widgetMessages = (WidgetGeneratorMessages)MessagesFactory.getMessages(WidgetGeneratorMessages.class);

	@Override
	public void processAttributes(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
	
		String widget = context.getWidget();
		String pageableId = context.readWidgetProperty("pageable");
		String strEnabled = context.readWidgetProperty("enabled");

		if(pageableId != null && containsWidget(pageableId))
		{
			printlnPostProcessing(widget+".setPageable(("+Pageable.class.getCanonicalName()+") Screen.get("+EscapeUtils.quote(pageableId)+"));");
			if(strEnabled != null && strEnabled.length() > 0)
			{
				printlnPostProcessing(widget+".setEnabled("+Boolean.parseBoolean(strEnabled)+");");
			}
		}
		else
		{
			throw new CruxGeneratorException(widgetMessages.pagerNoPageableSet()); 
		}							
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}