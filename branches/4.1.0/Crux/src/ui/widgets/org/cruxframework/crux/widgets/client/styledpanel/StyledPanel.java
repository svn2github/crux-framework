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
package org.cruxframework.crux.widgets.client.styledpanel;

import org.cruxframework.crux.core.client.utils.StyleManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Gesse S. F. Dafe
 */
public class StyledPanel extends FlowPanel implements HasHorizontalAlignment, HasVerticalAlignment
{
	private static final String DEFAULT_STYLE_NAME = "cruxStyledPanel";
	private static final StyleManager styleManager = GWT.create(StyleManager.class);
	
	private VerticalAlignmentConstant verticalAlignment = HasVerticalAlignment.ALIGN_TOP;
	private HorizontalAlignmentConstant horizontalAlignment = HasHorizontalAlignment.ALIGN_LEFT;

	public StyledPanel()
	{
		styleManager.applyStyleName(this, DEFAULT_STYLE_NAME);
	}

	@Override
	public VerticalAlignmentConstant getVerticalAlignment()
	{
		return this.verticalAlignment;
	}

	@Override
	public void setVerticalAlignment(VerticalAlignmentConstant align)
	{
		String display = "";
		String verticalAlign = "";

		if (align != null)
		{
			this.verticalAlignment = align;
			
			if (this.verticalAlignment.equals(HasVerticalAlignment.ALIGN_MIDDLE))
			{
				display = "table-cell";
				verticalAlign = "middle";
			}
			else if (this.verticalAlignment.equals(HasVerticalAlignment.ALIGN_BOTTOM))
			{
				display = "table-cell";
				verticalAlign = "bottom";
			}
		}
		
		getElement().getStyle().setProperty("display", display);
		getElement().getStyle().setProperty("verticalAlign", verticalAlign);
	}

	@Override
	public HorizontalAlignmentConstant getHorizontalAlignment()
	{
		return this.horizontalAlignment;
	}

	@Override
	public void setHorizontalAlignment(HorizontalAlignmentConstant align)
	{
		setHorizontalAlignment(align, null);
	}
	
	/**
	 * Apply the horizontal alignment on a single child widget or on every children (when child == null).
	 * @param align
	 * @param childIndex
	 */
	public void setHorizontalAlignment(HorizontalAlignmentConstant align, Widget child)
	{
		String marginLeft = "";
		String marginRight = "";

		if (align != null)
		{
			this.horizontalAlignment = align;
			
			if (this.horizontalAlignment.equals(HasHorizontalAlignment.ALIGN_RIGHT))
			{
				marginRight = "auto";
			}
			if (this.verticalAlignment.equals(HasHorizontalAlignment.ALIGN_CENTER))
			{
				marginLeft = "auto";
				marginRight = "auto";
			}
		}
		
		if(child == null)
		{
			int widgetCount = getWidgetCount();
			for(int i = 0; i < widgetCount; i++)
			{
				Widget currentChild = getWidget(i);
				currentChild.getElement().getStyle().setProperty("marginLeft", marginLeft);
				currentChild.getElement().getStyle().setProperty("marginRight", marginRight);
			}
		}
		else
		{
			child.getElement().getStyle().setProperty("marginLeft", marginLeft);
			child.getElement().getStyle().setProperty("marginRight", marginRight);
		}
	}
	
	@Override
	public void add(Widget w)
	{
		super.add(w);
		setHorizontalAlignment(getHorizontalAlignment(), w);
	}

}