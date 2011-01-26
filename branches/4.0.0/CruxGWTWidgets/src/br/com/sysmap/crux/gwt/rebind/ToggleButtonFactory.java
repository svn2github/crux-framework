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

import com.google.gwt.user.client.ui.ToggleButton;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.creator.children.ChoiceChildProcessor;

/**
 * A Factory for ToggleButton widgets
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="toggleButton", library="gwt", targetWidget=ToggleButton.class)
public class ToggleButtonFactory extends CustomButtonFactory 
{
	
	/**
	 * Render component attributes
	 * @throws CruxGeneratorException 
	 */
	@Override
	@TagAttributes({
		@TagAttribute(value="down", type=Boolean.class)
	})
	public void processAttributes(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
	}

	@Override
	@TagChildren({
		@TagChild(FacesProcessor.class)
	})
	public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException
	{
		super.processChildren(out, context);
	}
	
	@TagChildAttributes(minOccurs="0", maxOccurs="6")
	public static class FacesProcessor extends ChoiceChildProcessor<CustomButtonContext> 
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
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException {}
	}
	
	public static class FaceChildrenProcessor extends ChoiceChildProcessor<CustomButtonContext>
	{
		@Override
		@TagChildren({
			@TagChild(TextFaceProcessor.class),
			@TagChild(HTMLFaceProcessor.class),
			@TagChild(ImageFaceProcessor.class)
		})
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException {}
	}

	public static class UpFaceProcessor extends AbstractUpFaceProcessor
	{
		@Override
		@TagChildren({
			@TagChild(FaceChildrenProcessor.class)
		})
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			super.processChildren(out, context);
		}
	}
	
	public static class UpDisabledFaceProcessor extends AbstractUpDisabledFaceProcessor
	{
		@Override
		@TagChildren({
			@TagChild(FaceChildrenProcessor.class)
		})
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			super.processChildren(out, context);
		}
	}

	public static class UpHoveringFaceProcessor extends AbstractUpHoveringFaceProcessor
	{
		@Override
		@TagChildren({
			@TagChild(FaceChildrenProcessor.class)
		})
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			super.processChildren(out, context);
		}
	}

	public static class DownFaceProcessor extends AbstractDownFaceProcessor
	{
		@Override
		@TagChildren({
			@TagChild(FaceChildrenProcessor.class)
		})
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			super.processChildren(out, context);
		}
	}

	public static class DownDisabledFaceProcessor extends AbstractDownDisabledFaceProcessor
	{
		@Override
		@TagChildren({
			@TagChild(FaceChildrenProcessor.class)
		})
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			super.processChildren(out, context);
		}
	}

	public static class DownHoveringFaceProcessor extends AbstractDownHoveringFaceProcessor
	{
		@Override
		@TagChildren({
			@TagChild(FaceChildrenProcessor.class)
		})
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			super.processChildren(out, context);
		}
	}
	
	public static class TextFaceProcessor extends AbstractTextFaceProcessor {}
	public static class HTMLFaceProcessor extends AbstractHTMLFaceProcessor {}
	public static class ImageFaceProcessor extends AbstractImageFaceProcessor {}	
}
