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
package br.com.sysmap.crux.widgets.rebind.dynatabs;

import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildren;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.widgets.client.dynatabs.DynaTabs;
import br.com.sysmap.crux.widgets.client.dynatabs.Tab;
import br.com.sysmap.crux.widgets.rebind.event.BeforeBlurEvtBind;
import br.com.sysmap.crux.widgets.rebind.event.BeforeCloseEvtBind;
import br.com.sysmap.crux.widgets.rebind.event.BeforeFocusEvtBind;

/**
 * Factory for Decorated Button widget
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="dynaTabs", library="widgets", targetWidget=DynaTabs.class)
public class DynaTabsFactory extends WidgetCreator<WidgetCreatorContext>
{
	@Override
	@TagChildren({
		@TagChild(DynaTabProcessor.class)
	})
	public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException {}
	
	@TagChildAttributes(tagName="tab", minOccurs="0", maxOccurs="unbounded")
	public static class DynaTabProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		protected BeforeFocusEvtBind beforeFocusEvtBind = new BeforeFocusEvtBind();
		protected BeforeBlurEvtBind beforeBlurEvtBind = new BeforeBlurEvtBind();
		protected BeforeCloseEvtBind beforeCloseEvtBind = new BeforeCloseEvtBind();

		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="id", required=true),
			@TagAttributeDeclaration(value="url", required=true),
			@TagAttributeDeclaration("label"),
			@TagAttributeDeclaration(value="closeable", type=Boolean.class)
		})
		@TagEventsDeclaration({
			@TagEventDeclaration("onBeforeFocus"),
			@TagEventDeclaration("onBeforeBlur"),
			@TagEventDeclaration("onBeforeClose")
		})
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{
			String id = context.readChildProperty("id");
			String label = context.readChildProperty("label");
			label = (label != null && label.length() > 0) ? getWidgetCreator().getDeclaredMessage(label) : "";
			String url = context.readChildProperty("url");
						
			boolean closeable = true;
			String strCloseable = context.readChildProperty("closeable");
			if(strCloseable != null && strCloseable.trim().length() > 0)
			{
				closeable = Boolean.parseBoolean(strCloseable);
			}
			
			String rootWidget = context.getWidget();
			String tab = getWidgetCreator().createVariableName("tab");
			out.println(Tab.class.getCanonicalName()+" "+tab+" = "+rootWidget+".openTab("+EscapeUtils.quote(id)+", "+
					EscapeUtils.quote(label)+", "+EscapeUtils.quote(url)+", "+closeable+", false);");
			
			String beforeFocusEvt = context.readChildProperty(beforeFocusEvtBind.getEventName());
			if (!StringUtils.isEmpty(beforeFocusEvt))
			{
				beforeFocusEvtBind.processEvent(out, beforeFocusEvt, tab, null);
			}
			String beforeBlurEvt = context.readChildProperty(beforeBlurEvtBind.getEventName());
			if (!StringUtils.isEmpty(beforeBlurEvt))
			{
				beforeBlurEvtBind.processEvent(out, beforeBlurEvt, tab, null);
			}
			String beforeCloseEvt = context.readChildProperty(beforeCloseEvtBind.getEventName());
			if (!StringUtils.isEmpty(beforeCloseEvt))
			{
				beforeCloseEvtBind.processEvent(out, beforeCloseEvt, tab, null);
			}
		}
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}