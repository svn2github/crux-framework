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

import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagConstraints;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildren;

import com.google.gwt.user.client.ui.HorizontalSplitPanel;

/**
 * Represents a HorizontalSplitPanel
 * @author Thiago Bustamante
 */
@SuppressWarnings("deprecation")
@DeclarativeFactory(id="horizontalSplitPanel", library="gwt", targetWidget=HorizontalSplitPanel.class)
@TagChildren({
	@TagChild(HorizontalSplitPanelFactory.LeftProcessor.class),
	@TagChild(HorizontalSplitPanelFactory.RightProcessor.class)
})
public class HorizontalSplitPanelFactory extends PanelFactory<WidgetCreatorContext>
{
	@TagConstraints(tagName="left", minOccurs="0")
	@TagChildren({
		@TagChild(LeftWidgeProcessor.class)
	})
	public static class LeftProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagConstraints(tagName="right", minOccurs="0")
	@TagChildren({
		@TagChild(RightWidgeProcessor.class)
	})
	public static class RightProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(widgetProperty="leftWidget")
	public static class LeftWidgeProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagConstraints(widgetProperty="rightWidget")
	public static class RightWidgeProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
