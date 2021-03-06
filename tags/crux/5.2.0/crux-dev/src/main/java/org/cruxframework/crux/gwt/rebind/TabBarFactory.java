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
package org.cruxframework.crux.gwt.rebind;

import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;

import com.google.gwt.user.client.ui.TabBar;

/**
 * Factory for TabBar widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="tabBar", library="gwt", targetWidget=TabBar.class)
@TagChildren({
	@TagChild(TabBarFactory.TabProcessor.class)
})
public class TabBarFactory extends AbstractTabBarFactory
{
	@TagChildren({
		@TagChild(TabItemProcessor.class)
	})	
	public static class TabProcessor extends AbstractTabProcessor {}
	
	@TagChildren({
		@TagChild(TextTabProcessor.class),
		@TagChild(HTMLTabProcessor.class),
		@TagChild(WidgetTabProcessor.class)
	})		
	public static class TabItemProcessor extends ChoiceChildProcessor<TabBarContext> {}
	
	@TagConstraints(tagName="widget")
	@TagChildren({
		@TagChild(WidgetProcessor.class)
	})	
	public static class WidgetTabProcessor extends WidgetChildProcessor<TabBarContext> {}
	
	public static class TextTabProcessor extends AbstractTextTabProcessor {}
	
	public static class HTMLTabProcessor extends AbstractHTMLTabProcessor {}
	
	public static class WidgetProcessor extends AbstractWidgetProcessor {}
}
