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

import java.util.LinkedList;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.screen.AttributeProcessor;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.HasPostProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.client.screen.factory.HasAllFocusHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasAllKeyHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasAllMouseHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasAnimationFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasCloseHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasOpenHandlersFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasSelectionHandlersFactory;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Tree.Resources;


class TreeContext extends WidgetCreatorContext
{
	LinkedList<TreeItem> itemStack;
}

/**
 * A factory for Tree widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="tree", library="gwt")
public class TreeFactory extends WidgetCreator<Tree, TreeContext> 
       implements HasAnimationFactory<Tree, TreeContext>, HasAllFocusHandlersFactory<Tree, TreeContext>,
                  HasOpenHandlersFactory<Tree, TreeContext>, HasCloseHandlersFactory<Tree, TreeContext>, 
                  HasAllMouseHandlersFactory<Tree, TreeContext>, HasAllKeyHandlersFactory<Tree, TreeContext>,
                  HasSelectionHandlersFactory<Tree, TreeContext>
{
	protected GWTMessages messages = GWT.create(GWTMessages.class);
	
	@Override
	public Tree instantiateWidget(CruxMetaDataElement element, String widgetId) 
	{
		Event eventLoadImage = EvtBind.getWidgetEvent(element, "onLoadImage");
		
		if (eventLoadImage != null)
		{
			LoadImagesEvent<Tree> loadEvent = new LoadImagesEvent<Tree>(widgetId);
			Resources treeImages = (Resources) Events.callEvent(eventLoadImage, loadEvent);

			String useLeafImagesStr = element.getProperty("useLeafImages");
			boolean useLeafImages = true;
			if (useLeafImagesStr != null && useLeafImagesStr.length() > 0)
			{
				useLeafImages = (Boolean.parseBoolean(useLeafImagesStr));
			}
			
			return new Tree(treeImages, useLeafImages);
		}
		
		return new Tree();
	}
	
	@Override
	@TagAttributes({
		@TagAttribute(value="tabIndex", type=Integer.class),
		@TagAttribute(value="accessKey", type=Character.class),
		@TagAttribute(value="openSelectedItem", type=Boolean.class, processor=OpenSelectedItemAttributeParser.class),
		@TagAttribute(value="focus", type=Boolean.class)
	})
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="useLeafImages", type=Boolean.class)
	})
	public void processAttributes(TreeContext context) throws InterfaceConfigException 
	{
		super.processAttributes(context);
	}

	/**
	 * @author Thiago da Rosa de Bustamante
	 */
	public static class OpenSelectedItemAttributeParser implements AttributeProcessor<TreeContext>
	{
		public void processAttribute(TreeContext context, String propertyValue) 
		{
			if(Boolean.parseBoolean(propertyValue))
			{
				Tree widget = context.getWidget();
				widget.ensureSelectedItemVisible();
			}
		}
	}
	
	@Override
	@TagEventsDeclaration({
		@TagEventDeclaration("onLoadImage")
	})
	public void processEvents(TreeContext context) throws InterfaceConfigException
	{
		super.processEvents(context);
	}
	
	@Override
	@TagChildren({
		@TagChild(TreeItemProcessor.class)
	})
	public void processChildren(TreeContext context) throws InterfaceConfigException {}
	
	@TagChildAttributes(tagName="item", minOccurs="0", maxOccurs="unbounded")
	public static class TreeItemProcessor extends WidgetChildProcessor<Tree, TreeContext> implements HasPostProcessor<Tree, TreeContext>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="state", type=Boolean.class),
			@TagAttributeDeclaration(value="selected", type=Boolean.class)
		})
		@TagChildren({
			@TagChild(TreeCaptionProcessor.class),
			@TagChild(TreeItemProcessor.class)
		})
		public void processChildren(TreeContext context) throws InterfaceConfigException 
		{
			context.itemStack = new LinkedList<TreeItem>();
		}
		
		public void postProcessChildren(TreeContext context) throws InterfaceConfigException 
		{
			context.itemStack.removeFirst();
		}
	}
	
	public static class TreeCaptionProcessor extends ChoiceChildProcessor<Tree, TreeContext>
	{
		@Override
		@TagChildren({
			@TagChild(TextCaptionProcessor.class),
			@TagChild(WidgetCaptionProcessor.class)
		})
		public void processChildren(TreeContext context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="textTitle", type=String.class)
	public static class TextCaptionProcessor extends WidgetChildProcessor<Tree, TreeContext>
	{
		@Override
		public void processChildren(TreeContext context) throws InterfaceConfigException 
		{
			String textCaption = ensureTextChild(context.getChildElement(), true);
			
			TreeItem parent = context.itemStack.peek();
			TreeItem currentItem;
			Tree rootWidget = context.getWidget();
			if (parent == null)
			{
				currentItem = rootWidget.addItem(textCaption);
			}
			else
			{
				currentItem = parent.addItem(textCaption);
			}
			context.itemStack.addFirst(currentItem);
		}
	}	

	@TagChildAttributes(tagName="widgetTitle")
	public static class WidgetCaptionProcessor extends WidgetChildProcessor<Tree, TreeContext>
	{
		@Override
		@TagChildren({
			@TagChild(WidgetCaptionWidgetProcessor.class)
		})
		public void processChildren(TreeContext context) throws InterfaceConfigException {}
	}	


	@TagChildAttributes(type=AnyWidget.class)
	public static class WidgetCaptionWidgetProcessor extends WidgetChildProcessor<Tree, TreeContext>
	{
		@Override
		public void processChildren(TreeContext context) throws InterfaceConfigException 
		{
			Widget child = createChildWidget(context.getChildElement());

			TreeItem parent = context.itemStack.peek();
			TreeItem currentItem;
			if (parent == null)
			{
				Tree rootWidget = context.getWidget();
				currentItem = rootWidget.addItem(child);
			}
			else
			{
				currentItem = parent.addItem(child);
			}
			context.itemStack.addFirst(currentItem);
		}
	}
}
