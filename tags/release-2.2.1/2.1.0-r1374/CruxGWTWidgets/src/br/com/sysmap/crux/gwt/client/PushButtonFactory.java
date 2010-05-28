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
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.PushButton;


/**
 * Represents a Push ButtonFactory
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="pushButton", library="gwt")
public class PushButtonFactory extends CustomButtonFactory<PushButton> 
{
	@Override
	public PushButton instantiateWidget(Element element, String widgetId) 
	{
		return new PushButton();
	}
	
	@Override
	@TagChildren({
		@TagChild(FacesProcessor.class)
	})
	public void processChildren(WidgetFactoryContext<PushButton> context) throws InterfaceConfigException
	{
		super.processChildren(context);
	}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="6")
	public static class FacesProcessor extends ChoiceChildProcessor<PushButton> 
	{
		@Override
		@TagChildren({
			@TagChild(UpFaceProcessor.class),
			@TagChild(UpDisabledFaceProcessor.class),
			@TagChild(UpHoveringFaceProcessor.class),
			@TagChild(DownFaceProcessor.class),
			@TagChild(DownDisabledFaceProcessor.class),
			@TagChild(DownHoveringFaceProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<PushButton> context) throws InterfaceConfigException {}
	}
	
	public static class FaceChildrenProcessor extends ChoiceChildProcessor<PushButton>
	{
		@Override
		@TagChildren({
			@TagChild(TextFaceProcessor.class),
			@TagChild(HTMLFaceProcessor.class),
			@TagChild(ImageFaceProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<PushButton> context) throws InterfaceConfigException {}
	}

	public static class UpFaceProcessor extends AbstractUpFaceProcessor<PushButton>
	{
		@Override
		@TagChildren({
			@TagChild(FaceChildrenProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<PushButton> context) throws InterfaceConfigException 
		{
			super.processChildren(context);
		}
	}
	
	public static class UpDisabledFaceProcessor extends AbstractUpDisabledFaceProcessor<PushButton>
	{
		@Override
		@TagChildren({
			@TagChild(FaceChildrenProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<PushButton> context) throws InterfaceConfigException 
		{
			super.processChildren(context);
		}
	}

	public static class UpHoveringFaceProcessor extends AbstractUpHoveringFaceProcessor<PushButton>
	{
		@Override
		@TagChildren({
			@TagChild(FaceChildrenProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<PushButton> context) throws InterfaceConfigException 
		{
			super.processChildren(context);
		}
	}

	public static class DownFaceProcessor extends AbstractDownFaceProcessor<PushButton>
	{
		@Override
		@TagChildren({
			@TagChild(FaceChildrenProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<PushButton> context) throws InterfaceConfigException 
		{
			super.processChildren(context);
		}
	}

	public static class DownDisabledFaceProcessor extends AbstractDownDisabledFaceProcessor<PushButton>
	{
		@Override
		@TagChildren({
			@TagChild(FaceChildrenProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<PushButton> context) throws InterfaceConfigException 
		{
			super.processChildren(context);
		}
	}

	public static class DownHoveringFaceProcessor extends AbstractDownHoveringFaceProcessor<PushButton>
	{
		@Override
		@TagChildren({
			@TagChild(FaceChildrenProcessor.class)
		})
		public void processChildren(WidgetChildProcessorContext<PushButton> context) throws InterfaceConfigException 
		{
			super.processChildren(context);
		}
	}
	
	public static class TextFaceProcessor extends AbstractTextFaceProcessor<PushButton> {}
	public static class HTMLFaceProcessor extends AbstractHTMLFaceProcessor<PushButton> {}
	public static class ImageFaceProcessor extends AbstractImageFaceProcessor<PushButton> {}	
}
