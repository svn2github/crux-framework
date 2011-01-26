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

import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.HasHTMLFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.HTMLTag;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildAttributes;

import com.google.gwt.user.client.ui.Image;

class CustomButtonContext extends WidgetCreatorContext
{
	String face;
}

/**
 * 
 * @author Thiago da Rosa de Bustamante
 */
public abstract class CustomButtonFactory extends FocusWidgetFactory<CustomButtonContext> 
			implements HasHTMLFactory<CustomButtonContext>
{
	@TagChildAttributes(tagName="up")
	abstract static class AbstractUpFaceProcessor extends WidgetChildProcessor<CustomButtonContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			context.face = "UpFace";
		}
	}
	
	@TagChildAttributes(tagName="upDisabled")
	abstract static class AbstractUpDisabledFaceProcessor extends WidgetChildProcessor<CustomButtonContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			context.face = "UpDisabledFace";
		}
	}

	@TagChildAttributes(tagName="upHovering")
	abstract static class AbstractUpHoveringFaceProcessor extends WidgetChildProcessor<CustomButtonContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			context.face = "UpHoveringFace";
		}
	}

	@TagChildAttributes(tagName="down")
	abstract static class AbstractDownFaceProcessor extends WidgetChildProcessor<CustomButtonContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			context.face = "DownFace";
		}
	}
	
	@TagChildAttributes(tagName="downDisabled")
	abstract static class AbstractDownDisabledFaceProcessor extends WidgetChildProcessor<CustomButtonContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			context.face = "DownDisabledFace";
		}
	}

	@TagChildAttributes(tagName="downHovering")
	abstract static class AbstractDownHoveringFaceProcessor extends WidgetChildProcessor<CustomButtonContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			context.face = "DownHoveringFace";
		}
	}
	
	@TagChildAttributes(tagName="textFace")
	abstract static class AbstractTextFaceProcessor extends WidgetChildProcessor<CustomButtonContext>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="value", required=true)
		})
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			out.println(context.getWidget()+"get"+context.face+"().setText("+EscapeUtils.quote(context.readChildProperty("value"))+");");
		}
	}
	
	@TagChildAttributes(tagName="htmlFace", type=HTMLTag.class)
	abstract static class AbstractHTMLFaceProcessor extends WidgetChildProcessor<CustomButtonContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			out.println(context.getWidget()+"get"+context.face+"().setHTML("+EscapeUtils.quote(ensureHtmlChild(context.getChildElement(), true))+");");
		}
	}
	
	@TagChildAttributes(tagName="imageFace")
	abstract static class AbstractImageFaceProcessor extends WidgetChildProcessor<CustomButtonContext>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="url", required=true),
			@TagAttributeDeclaration("visibleRect")
		})
		public void processChildren(SourcePrinter out, CustomButtonContext context) throws CruxGeneratorException 
		{
			String visibleRect = context.readChildProperty("visibleRect");

			if (StringUtils.isEmpty(visibleRect))
			{
				out.println(context.getWidget()+"get"+context.face+"().setImage(new "+Image.class.getCanonicalName()+
						"("+EscapeUtils.quote(context.readChildProperty("url"))+"));");
			}
			else
			{
				String[] coord = visibleRect.split(",");
				
				if (coord != null && coord.length == 4)
				{
					out.println(context.getWidget()+"get"+context.face+"().setImage(new "+Image.class.getCanonicalName()+
							"("+EscapeUtils.quote(context.readChildProperty("url"))+", "+
							Integer.parseInt(coord[0].trim())+","+Integer.parseInt(coord[1].trim())+","+ 
							Integer.parseInt(coord[2].trim())+","+Integer.parseInt(coord[3].trim())+"));");
				}
			}
		}
	}
}