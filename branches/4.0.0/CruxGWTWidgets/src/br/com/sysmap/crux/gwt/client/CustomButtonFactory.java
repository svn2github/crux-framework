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

import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.HasHTMLFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;

import com.google.gwt.user.client.ui.CustomButton;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.CustomButton.Face;

class CustomButtonContext extends WidgetCreatorContext
{
	Face face;
}

/**
 * 
 * @author Thiago da Rosa de Bustamante
 */
public abstract class CustomButtonFactory<T extends CustomButton> extends FocusWidgetFactory<T, CustomButtonContext> 
			implements HasHTMLFactory<T, CustomButtonContext>
{
	@TagChildAttributes(tagName="up")
	abstract static class AbstractUpFaceProcessor<W extends CustomButton> extends WidgetChildProcessor<W, CustomButtonContext>
	{
		@SuppressWarnings("unchecked")
		@Override
		public void processChildren(CustomButtonContext context) throws InterfaceConfigException 
		{
			W widget = (W)context.getWidget();
			context.face = widget.getUpFace();
		}
	}
	
	@TagChildAttributes(tagName="upDisabled")
	abstract static class AbstractUpDisabledFaceProcessor<W extends CustomButton> extends WidgetChildProcessor<W, CustomButtonContext>
	{
		@SuppressWarnings("unchecked")
		@Override
		public void processChildren(CustomButtonContext context) throws InterfaceConfigException 
		{
			W widget = (W)context.getWidget();
			context.face = widget.getUpDisabledFace();
		}
	}

	@TagChildAttributes(tagName="upHovering")
	abstract static class AbstractUpHoveringFaceProcessor<W extends CustomButton> extends WidgetChildProcessor<W, CustomButtonContext>
	{
		@SuppressWarnings("unchecked")
		@Override
		public void processChildren(CustomButtonContext context) throws InterfaceConfigException 
		{
			W widget = (W)context.getWidget();
			context.face = widget.getUpHoveringFace();
		}
	}

	@TagChildAttributes(tagName="down")
	abstract static class AbstractDownFaceProcessor<W extends CustomButton> extends WidgetChildProcessor<W, CustomButtonContext>
	{
		@SuppressWarnings("unchecked")
		@Override
		public void processChildren(CustomButtonContext context) throws InterfaceConfigException 
		{
			W widget = (W)context.getWidget();
			context.face = widget.getDownFace();
		}
	}
	
	@TagChildAttributes(tagName="downDisabled")
	abstract static class AbstractDownDisabledFaceProcessor<W extends CustomButton> extends WidgetChildProcessor<W, CustomButtonContext>
	{
		@SuppressWarnings("unchecked")
		@Override
		public void processChildren(CustomButtonContext context) throws InterfaceConfigException 
		{
			W widget = (W)context.getWidget();
			context.face = widget.getDownDisabledFace();
		}
	}

	@TagChildAttributes(tagName="downHovering")
	abstract static class AbstractDownHoveringFaceProcessor<W extends CustomButton> extends WidgetChildProcessor<W, CustomButtonContext>
	{
		@SuppressWarnings("unchecked")
		@Override
		public void processChildren(CustomButtonContext context) throws InterfaceConfigException 
		{
			W widget = (W)context.getWidget();
			context.face = widget.getDownHoveringFace();
		}
	}
	
	@TagChildAttributes(tagName="textFace")
	abstract static class AbstractTextFaceProcessor<W extends CustomButton> extends WidgetChildProcessor<W, CustomButtonContext>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="value", required=true)
		})
		public void processChildren(CustomButtonContext context) throws InterfaceConfigException 
		{
			context.face.setText(context.readChildProperty("value"));
		}
	}
	
	@TagChildAttributes(tagName="htmlFace")
	abstract static class AbstractHTMLFaceProcessor<W extends CustomButton> extends WidgetChildProcessor<W, CustomButtonContext>
	{
		@Override
		public void processChildren(CustomButtonContext context) throws InterfaceConfigException 
		{
			context.face.setText(ensureTextChild(context.getChildElement(), true));
		}
	}
	
	@TagChildAttributes(tagName="imageFace")
	abstract static class AbstractImageFaceProcessor<W extends CustomButton> extends WidgetChildProcessor<W, CustomButtonContext>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="url", required=true),
			@TagAttributeDeclaration("visibleRect")
		})
		public void processChildren(CustomButtonContext context) throws InterfaceConfigException 
		{
			String visibleRect = context.readChildProperty("visibleRect");

			if (StringUtils.isEmpty(visibleRect))
			{
				context.face.setImage(new Image(context.readChildProperty("url")));
			}
			else
			{
				String[] coord = visibleRect.split(",");
				
				if (coord != null && coord.length == 4)
				{
					context.face.setImage(new Image(context.readChildProperty("url"),
							Integer.parseInt(coord[0].trim()),Integer.parseInt(coord[1].trim()), 
							Integer.parseInt(coord[2].trim()), Integer.parseInt(coord[3].trim())));
				}
			}
		}
	}
}
