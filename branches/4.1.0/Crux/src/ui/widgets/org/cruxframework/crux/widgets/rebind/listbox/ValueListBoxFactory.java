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
package org.cruxframework.crux.widgets.rebind.listbox;

import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.dto.DataObjects;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.FocusableFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAllFocusHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAllKeyHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAllMouseHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasChangeHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasClickHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasDirectionEstimatorFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasDoubleClickHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasEnabledFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasNameFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.utils.ProvidesKeyUtils;
import org.cruxframework.crux.core.utils.RendererUtils;
import org.cruxframework.crux.widgets.client.listbox.ValueListBox;

/**
 * Represents a Value List Box component
 *
 * @author daniel.martins - <code>daniel@cruxframework.org</code>
 *
 */
@DeclarativeFactory(id="valueListBox", library="widgets", targetWidget=ValueListBox.class)

@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="dataObject", required=true)

})
public class ValueListBoxFactory extends WidgetCreator<WidgetCreatorContext> implements
				HasChangeHandlersFactory<WidgetCreatorContext>, HasNameFactory<WidgetCreatorContext>, HasDirectionEstimatorFactory<WidgetCreatorContext>,
				HasClickHandlersFactory<WidgetCreatorContext>, HasDoubleClickHandlersFactory<WidgetCreatorContext>, FocusableFactory<WidgetCreatorContext>,
				HasEnabledFactory<WidgetCreatorContext>, HasAllFocusHandlersFactory<WidgetCreatorContext>, HasAllKeyHandlersFactory<WidgetCreatorContext>,
				HasAllMouseHandlersFactory<WidgetCreatorContext>
{
	@Override
	public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException {}

	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context)
	{
		String listBoxDataObject = context.readWidgetProperty("dataObject");
		String className = getWidgetClassName();
		String dataObject = DataObjects.getDataObject(listBoxDataObject);
		String rendererVarName = RendererUtils.getRenderer(out, context.getWidgetElement(), "dataObject", "renderer");
		String providesKeyVarName = ProvidesKeyUtils.getkeyProvider(out, context.getWidgetElement(), "dataObject", "providesKey" );

		out.println("final " + className + " <" + dataObject + "> " + context.getWidget()+" = new "+className+ " <" + dataObject + ">( " + rendererVarName + " , " + providesKeyVarName +");");
	}

	@Override
	public WidgetCreatorContext instantiateContext() {
		return new WidgetCreatorContext();
	}
}