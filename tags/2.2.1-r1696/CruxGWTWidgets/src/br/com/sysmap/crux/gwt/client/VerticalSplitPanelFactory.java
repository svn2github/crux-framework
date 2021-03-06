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

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.VerticalSplitPanel;

/**
 * Represents a VerticalSplitPanelFactory
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="verticalSplitPanel", library="gwt")
public class VerticalSplitPanelFactory extends PanelFactory<VerticalSplitPanel>
{
	@Override
	public VerticalSplitPanel instantiateWidget(Element element, String widgetId) 
	{
		return new VerticalSplitPanel();
	}
	
	@Override
	@TagEventsDeclaration({
		@TagEventDeclaration("onLoadImage")
	})
	public void processEvents(WidgetFactoryContext<VerticalSplitPanel> context) throws InterfaceConfigException
	{
		super.processEvents(context);
	}

	@Override
	@TagChildren({
		@TagChild(TopProcessor.class),
		@TagChild(BottomProcessor.class)
	})
	public void processChildren(WidgetFactoryContext<VerticalSplitPanel> context) throws InterfaceConfigException {}
	
	@TagChildAttributes(tagName="top", minOccurs="0")
	public static class TopProcessor extends WidgetChildProcessor<VerticalSplitPanel>
	{
		@Override
		@TagChildren({
			@TagChild(TopWidgeProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<VerticalSplitPanel> context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="bottom", minOccurs="0")
	public static class BottomProcessor extends WidgetChildProcessor<VerticalSplitPanel>
	{
		@Override
		@TagChildren({
			@TagChild(BottomWidgeProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<VerticalSplitPanel> context) throws InterfaceConfigException {}
	}

	@TagChildAttributes(widgetProperty="topWidget")
	public static class TopWidgeProcessor extends AnyWidgetChildProcessor<VerticalSplitPanel> {}
	
	@TagChildAttributes(widgetProperty="bottomWidget")
	public static class BottomWidgeProcessor extends AnyWidgetChildProcessor<VerticalSplitPanel> {}
}
