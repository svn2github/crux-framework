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
package br.com.sysmap.crux.widgets.client.collapsepanel;

import org.json.JSONObject;

import br.com.sysmap.crux.core.client.screen.LazyPanel;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.creator.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildLazyCondition;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildLazyConditions;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildren;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagEvent;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagEvents;
import br.com.sysmap.crux.gwt.rebind.CellPanelContext;
import br.com.sysmap.crux.widgets.client.event.collapseexpand.BeforeCollapseEvtBind;
import br.com.sysmap.crux.widgets.client.event.collapseexpand.BeforeExpandEvent;
import br.com.sysmap.crux.widgets.client.event.collapseexpand.BeforeExpandEvtBind;
import br.com.sysmap.crux.widgets.client.event.collapseexpand.BeforeExpandHandler;
import br.com.sysmap.crux.widgets.client.titlepanel.AbstractTitlePanelFactory;

/**
 * Factory for Collapse Panel widget
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="collapsePanel", library="widgets", targetWidget=CollapsePanel.class)
public class CollapsePanelFactory extends AbstractTitlePanelFactory
{
	@Override
	public String instantiateWidget(SourcePrinter out, JSONObject metaElem, String widgetId) throws CruxGeneratorException
	{
		String varName = createVariableName("widget");
		String className = getWidgetClassName();
		out.println(className + " " + varName+" = new "+className+"();");

		String collapsible = metaElem.optString("collapsible");
		String collapsed = metaElem.optString("collapsed");
		if ((collapsible == null || !StringUtils.unsafeEquals(collapsible, "false")) &&
			(collapsed != null && StringUtils.unsafeEquals(collapsed, "true")))
		{
			out.println(varName+".addBeforeExpandHandler(new "+BeforeExpandHandler.class.getCanonicalName()+"(){");
			out.println("private boolean loaded = false;");
			out.println("public void onBeforeExpand("+BeforeExpandEvent.class.getCanonicalName()+" event){");
			out.println("if (!loaded){");
			out.println(LazyPanel.class.getCanonicalName()+"widget = ("+LazyPanel.class.getCanonicalName()+")ret.getContentWidget();");
			out.println("widget.ensureWidget();");
			out.println("loaded = true;");
			out.println("}");
			out.println("}");
			out.println("});");
		}		
		
		return varName;
	}	
	
	@Override
	@TagEvents({
		@TagEvent(BeforeCollapseEvtBind.class),
		@TagEvent(BeforeExpandEvtBind.class)
	})
	public void processEvents(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException
	{
		super.processEvents(out, context);
	}
	
	@Override
	@TagAttributes({
		@TagAttribute(value="collapsed", type=Boolean.class),
		@TagAttribute(value="collapsible", type=Boolean.class)
	})
	public void processAttributes(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
	}
	
	@Override
	@TagChildren({
		@TagChild(TitleProcessor.class),
		@TagChild(BodyProcessor.class)
	})
	public void processChildren(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException {}
	
	@TagChildAttributes(tagName="title", minOccurs="0")
	public static class TitleProcessor extends WidgetChildProcessor<CellPanelContext>
	{
		@Override
		@TagChildren({
			@TagChild(TitleChildrenProcessor.class)
		})
		public void processChildren(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException {}
	}

	public static class TitleChildrenProcessor extends ChoiceChildProcessor<CellPanelContext>
	{
		@Override
		@TagChildren({
			@TagChild(CollapsePanelHTMLChildProcessor.class),
			@TagChild(CollapsePanelTextChildProcessor.class),
			@TagChild(CollapsePanelWidgetProcessor.class)
		})
		public void processChildren(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException {}
	}
	
	@TagChildAttributes(tagName="widget")
	public static class CollapsePanelWidgetProcessor extends WidgetChildProcessor<CellPanelContext>
	{
		@Override
		@TagChildren({
			@TagChild(TitleWidgetTitleProcessor.class)
		})
		public void processChildren(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException {}
	}
	
	@TagChildAttributes(widgetProperty="titleWidget")
	public static class TitleWidgetTitleProcessor extends AnyWidgetChildProcessor<CellPanelContext> {}
	
	@TagChildAttributes(tagName="body", minOccurs="0")
	public static class BodyProcessor extends WidgetChildProcessor<CellPanelContext>
	{
		@Override
		@TagChildren({
			@TagChild(BodyChildrenProcessor.class)
		})
		public void processChildren(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException {}
	}

	public static class BodyChildrenProcessor extends ChoiceChildProcessor<CellPanelContext>
	{
		@Override
		@TagChildren({
			@TagChild(CollapsePanelBodyHTMLChildProcessor.class),
			@TagChild(CollapsePanelBodyTextChildProcessor.class),
			@TagChild(CollapsePanelBodyWidgetProcessor.class)
		})
		public void processChildren(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException {}
	}
	
	@TagChildAttributes(tagName="widget")
	public static class CollapsePanelBodyWidgetProcessor extends WidgetChildProcessor<CellPanelContext>
	{
		@Override
		@TagChildren({
			@TagChild(BodyWidgetContentProcessor.class)
		})
		public void processChildren(SourcePrinter out, CellPanelContext context) throws CruxGeneratorException {}
	}
	
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