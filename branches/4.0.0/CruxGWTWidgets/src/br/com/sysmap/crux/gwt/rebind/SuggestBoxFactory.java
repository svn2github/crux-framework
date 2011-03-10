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

import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasAllKeyHandlersFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasAnimationFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasSelectionHandlersFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasTextFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasValueChangeHandlersFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.gwt.client.LoadOracleEvent;

import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;

/**
 * Factory for SuggestBox widget
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="suggestBox", library="gwt", targetWidget=SuggestBox.class)
@TagAttributes({
	@TagAttribute(value="accessKey", type=Character.class),
	@TagAttribute(value="autoSelectEnabled", type=Boolean.class),
	@TagAttribute(value="focus", type=Boolean.class),
	@TagAttribute(value="limit", type=Integer.class),
	@TagAttribute("popupStyleName"),
	@TagAttribute(value="tabIndex", type=Integer.class),
	@TagAttribute("value")
})
@TagEventsDeclaration({
	@TagEventDeclaration("onLoadOracle")
})
public class SuggestBoxFactory extends CompositeFactory<WidgetCreatorContext> 
       implements HasAnimationFactory<WidgetCreatorContext>, HasTextFactory<WidgetCreatorContext>, 
                  HasValueChangeHandlersFactory<WidgetCreatorContext>, 
                  HasSelectionHandlersFactory<WidgetCreatorContext>,
                  HasAllKeyHandlersFactory<WidgetCreatorContext>
{
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context)
	{
		String className = SuggestBox.class.getCanonicalName();

		String event = ViewFactoryCreator.createVariableName("evt");
		String oracle = ViewFactoryCreator.createVariableName("oracle");

		String eventLoadOracle = context.readWidgetProperty("onLoadOracle");
		if (eventLoadOracle != null)
		{
			out.println("final Event "+event+" = Events.getEvent(\"onLoadOracle\", "+ EscapeUtils.quote(eventLoadOracle)+");");
			out.println(SuggestOracle.class.getCanonicalName()+" "+oracle+" = Events.callEvent("+event+", new "+LoadOracleEvent.class.getCanonicalName()+
					"<"+SuggestBox.class.getCanonicalName()+">("+EscapeUtils.quote(context.getWidgetId())+"));");
			out.println(className + " " + context.getWidget()+" = new "+className+"("+oracle+");");
		}
		else
		{
			out.println(className + " " + context.getWidget()+" = new "+className+"();");
		}
	}	
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
