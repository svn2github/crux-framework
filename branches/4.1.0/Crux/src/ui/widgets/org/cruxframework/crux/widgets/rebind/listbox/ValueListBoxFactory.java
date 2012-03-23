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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.dto.DataObjectLabel;
import org.cruxframework.crux.core.i18n.MessagesFactory;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.GeneratorMessages;
import org.cruxframework.crux.core.rebind.dto.DataObjects;
import org.cruxframework.crux.core.rebind.dto.DataObjects.Label;
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
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.utils.ClassUtils;
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
@TagChildren({
	@TagChild(ValueListBoxFactory.ValueListBoxItemsProcessor.class)
})
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="dataObject", required=true)

})
public class ValueListBoxFactory extends WidgetCreator<WidgetCreatorContext> implements
				HasChangeHandlersFactory<WidgetCreatorContext>, HasNameFactory<WidgetCreatorContext>, HasDirectionEstimatorFactory<WidgetCreatorContext>,
				HasClickHandlersFactory<WidgetCreatorContext>, HasDoubleClickHandlersFactory<WidgetCreatorContext>, FocusableFactory<WidgetCreatorContext>,
				HasEnabledFactory<WidgetCreatorContext>, HasAllFocusHandlersFactory<WidgetCreatorContext>, HasAllKeyHandlersFactory<WidgetCreatorContext>,
				HasAllMouseHandlersFactory<WidgetCreatorContext>
{

	@TagConstraints(tagName="item", minOccurs="0", maxOccurs="unbounded")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("value"),
		@TagAttributeDeclaration(value="label", supportsI18N=true),
		@TagAttributeDeclaration(value="selected", type=Boolean.class)
	})
	public static class ValueListBoxItemsProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		private static final Log logger = LogFactory.getLog(ValueListBoxItemsProcessor.class);
		protected static GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);

		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{
			try
			{
				String dataObject = context.readWidgetProperty("dataObject");
				String dataObjectClass = DataObjects.getDataObject(dataObject);
				Class<?> dataClass = Class.forName(dataObjectClass);
				List<Label> labels = extractValueItem(dataClass);

				String label = context.readChildProperty("label");
				String value = context.readChildProperty("value");

				if (value == null || value.length() == 0)
				{
					value = label;
				}

				String variableName = getWidgetCreator().createVariableName("object");
				out.println(dataObjectClass + " " + variableName + " = new " + dataObjectClass +"();");

				int beginIndex = 0;
				int endIndex = 0;

				for (Label labelObject : labels)
		        {
					if(!"".equals(labelObject.getSuffix()) &&  value.indexOf(labelObject.getSuffix()) != -1)
					{
						endIndex = value.indexOf(labelObject.getSuffix());
					}
					else
					{
						endIndex = value.length();
					}

					out.println(variableName + "." +labelObject.getLabelField() + "(\"" + value.substring(beginIndex, endIndex)  + "\");");
					beginIndex = endIndex;
					endIndex = value.indexOf(labelObject.getSuffix(), beginIndex);
		        }

				out.println(context.getWidget()+".setValue(" + variableName +");");
			}
			catch (ClassNotFoundException e)
			{
				logger.error(messages.dataObjectsInitializeError(e.getLocalizedMessage()),e);
			}

		}


		private List<Label> extractValueItem(Class<?> dataClass)
		{
			List<Label> labels = new ArrayList<Label>();

			while(dataClass.getSuperclass() != null)
			{
				Field[] fields = dataClass.getDeclaredFields();

				for (Field field : fields)
				{
					DataObjectLabel annotation = field.getAnnotation(DataObjectLabel.class);

					if (annotation != null)
					{
						if (Modifier.isPublic(field.getModifiers()))
						{
							Label label = new Label();
							label.setLabelField(field.getName());
							label.setSuffix(annotation.separator());

							labels.add(label);
						}
						else
						{
							Label label = new Label();
							label.setLabelField(ClassUtils.getSetterMethod(field.getName()));
							label.setSuffix(annotation.separator());

							labels.add(label);
						}
					}
				}
				dataClass = dataClass.getSuperclass();
			}

			return labels;
		}

	}

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