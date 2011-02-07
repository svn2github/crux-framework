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
package br.com.sysmap.crux.widgets.rebind.collapsepanel;

import org.json.JSONObject;

import br.com.sysmap.crux.core.client.screen.LazyPanel;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildLazyCondition;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildLazyConditions;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildren;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagEvent;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagEvents;
import br.com.sysmap.crux.gwt.rebind.CellPanelContext;
import br.com.sysmap.crux.widgets.client.collapsepanel.CollapsePanel;
import br.com.sysmap.crux.widgets.client.event.collapseexpand.BeforeExpandEvent;
import br.com.sysmap.crux.widgets.client.event.collapseexpand.BeforeExpandHandler;
import br.com.sysmap.crux.widgets.rebind.event.BeforeCollapseEvtBind;
import br.com.sysmap.crux.widgets.rebind.event.BeforeExpandEvtBind;
import br.com.sysmap.crux.widgets.rebind.titlepanel.AbstractTitlePanelFactory;

/**
 * Factory for Collapse Panel widget
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="collapsePanel", library="widgets", targetWidget=CollapsePanel.class)
@TagEvents({
	@TagEvent(BeforeCollapseEvtBind.class),
	@TagEvent(BeforeExpandEvtBind.class)
})
@TagAttributes({
	@TagAttribute(value="collapsed", type=Boolean.class),
	@TagAttribute(value="collapsible", type=Boolean.class)
})
@TagChildren({
	@TagChild(CollapsePanelFactory.TitleProcessor.class),
	@TagChild(CollapsePanelFactory.BodyProcessor.class)
})
public class CollapsePanelFactory extends AbstractTitlePanelFactory
{
	@Override
	public String instantiateWidget(SourcePrinter out, JSONObject metaElem, String widgetId) throws CruxGeneratorException
	{
		String varName = createVariableName("widget");
		String className = getWidgetClassName();
		out.println("final "+className + " " + varName+" = new "+className+"();");

		String collapsible = metaElem.optString("collapsible");
		String collapsed = metaElem.optString("collapsed");
		if ((collapsible == null || !StringUtils.unsafeEquals(collapsible, "false")) &&
			(collapsed != null && StringUtils.unsafeEquals(collapsed, "true")))
		{
			out.println(varName+".addBeforeExpandHandler(new "+BeforeExpandHandler.class.getCanonicalName()+"(){");
			out.println("private boolean loaded = false;");
			out.println("public void onBeforeExpand("+BeforeExpandEvent.class.getCanonicalName()+" event){");
			out.println("if (!loaded){");
			out.println(LazyPanel.class.getCanonicalName()+" widget = ("+LazyPanel.class.getCanonicalName()+")"+varName+".getContentWidget();");
			out.println("widget.ensureWidget();");
			out.println("loaded = true;");
			out.println("}");
			out.println("}");
			out.println("});");
		}		
		
		return varName;
	}	
	
	@TagChildAttributes(tagName="title", minOccurs="0")
	@TagChildren({
		@TagChild(TitleChildrenProcessor.class)
	})
	public static class TitleProcessor extends WidgetChildProcessor<CellPanelContext> {}

	@TagChildren({
		@TagChild(CollapsePanelHTMLChildProcessor.class),
		@TagChild(CollapsePanelTextChildProcessor.class),
		@TagChild(CollapsePanelWidgetProcessor.class)
	})
	public static class TitleChildrenProcessor extends ChoiceChildProcessor<CellPanelContext> {}
	
	@TagChildAttributes(tagName="widget")
	@TagChildren({
		@TagChild(TitleWidgetTitleProcessor.class)
	})
	public static class CollapsePanelWidgetProcessor extends WidgetChildProcessor<CellPanelContext> {}
	
	@TagChildAttributes(widgetProperty="titleWidget")
	public static class TitleWidgetTitleProcessor extends AnyWidgetChildProcessor<CellPanelContext> {}
	
	@TagChildAttributes(tagName="body", minOccurs="0")
	@TagChildren({
		@TagChild(BodyChildrenProcessor.class)
	})
	public static class BodyProcessor extends WidgetChildProcessor<CellPanelContext> {}

	@TagChildren({
		@TagChild(CollapsePanelBodyHTMLChildProcessor.class),
		@TagChild(CollapsePanelBodyTextChildProcessor.class),
		@TagChild(CollapsePanelBodyWidgetProcessor.class)
	})
	public static class BodyChildrenProcessor extends ChoiceChildProcessor<CellPanelContext> {}
	
	@TagChildAttributes(tagName="widget")
	@TagChildren({
		@TagChild(BodyWidgetContentProcessor.class)
	})
	public static class CollapsePanelBodyWidgetProcessor extends WidgetChildProcessor<CellPanelContext> {}
	
	@TagChildAttributes(widgetProperty="contentWidget")
	@TagChildLazyConditions(all={
		@TagChildLazyCondition(property="collapsible", notEquals="false"),
		@TagChildLazyCondition(property="collapsed", equals="true")
	})
	public static class BodyWidgetContentProcessor extends AnyWidgetChildProcessor<CellPanelContext> {}

	public static class CollapsePanelHTMLChildProcessor extends HTMLChildProcessor{}
	public static class CollapsePanelTextChildProcessor extends TextChildProcessor{}
	public static class CollapsePanelBodyHTMLChildProcessor extends BodyHTMLChildProcessor{}
	public static class CollapsePanelBodyTextChildProcessor extends BodyTextChildProcessor{}
	
}