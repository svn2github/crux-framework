/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.cruxsite.rebind;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventsDeclaration;
import org.cruxframework.cruxsite.client.widget.PromoBanner;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * Factory for PromoBanner widgets
 * @author Gesse Dafe
 */
@DeclarativeFactory(id="promoBanner", library="site", targetWidget=PromoBanner.class)
@TagAttributes({
	@TagAttribute(value="bannersHeight", required=true),
	@TagAttribute(value="transitionDuration", type=Integer.class, defaultValue="150"),
	@TagAttribute(value="autoTransitionInterval", type=Integer.class, defaultValue="5000")
	
})
@TagChildren({
	@TagChild(PromoBannerFactory.BannerProcessor.class)
})
public class PromoBannerFactory extends WidgetCreator<WidgetCreatorContext>
{
	@TagConstraints(minOccurs="0",maxOccurs="unbounded",tagName="banner")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="backgroundImageURL", required=true),
		@TagAttributeDeclaration(value="title", required=true),
		@TagAttributeDeclaration(value="text", required=true),
		@TagAttributeDeclaration("styleName"),
		@TagAttributeDeclaration(value="buttonLabel", required=true)
	})
	@TagEventsDeclaration({
		@TagEventDeclaration("onClick")
	})
	public static class BannerProcessor extends WidgetChildProcessor<WidgetCreatorContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException 
		{
			String handler = getWidgetCreator().createVariableName("clickHandler");
			processEvent(out, context.readChildProperty("onClick"), getWidgetCreator(), handler);
			
			String styleName = context.readChildProperty("styleName");
			styleName = StringUtils.isEmpty(styleName) ? null : EscapeUtils.quote(styleName);
			
			out.println(context.getWidget() + ".addBanner(" 
				+ EscapeUtils.quote(context.readChildProperty("backgroundImageURL"))
				+ ", " + getWidgetCreator().getDeclaredMessage(context.readChildProperty("title"))
				+ ", " + getWidgetCreator().getDeclaredMessage(context.readChildProperty("text"))
				+ ", " + styleName
				+ ", " + getWidgetCreator().getDeclaredMessage(context.readChildProperty("buttonLabel"))
				+ ", " + handler
			+ ");");
		}
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
	
	/**
	 * Creates the declaration of a clickHandler
	 * @param out
	 * @param clickEventAttribute
	 * @param creator
	 * @param handlerVarName
	 */
	private static void processEvent(SourcePrinter out, String clickEventAttribute, WidgetCreator<?> creator, String handlerVarName)
    {
		if(!StringUtils.isEmpty(clickEventAttribute))
		{
			out.println(ClickHandler.class.getCanonicalName() + " " + handlerVarName + " = new " + ClickHandler.class.getCanonicalName()+"(){");
			out.println("public void onClick("+ClickEvent.class.getCanonicalName()+" event){");
			EvtProcessor.printEvtCall(out, clickEventAttribute, "onClick", ClickEvent.class, "event", creator);
			out.println("}");
			out.println("};");
		}
		else
		{
			out.println(ClickHandler.class.getCanonicalName() + " " + handlerVarName + " = null;");
		}
    }
}
