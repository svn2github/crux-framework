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
package br.com.sysmap.crux.core.client.screen.factory.align;

import com.google.gwt.user.client.ui.HasAutoHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.AutoHorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class AlignmentAttributeParser
{
	/**
	 * 
	 * @param align
	 * @param defaultAlignment
	 * @return
	 */
	public static HorizontalAlignmentConstant getHorizontalAlignment(String align, HorizontalAlignmentConstant defaultAlignment)
	{
		if (align != null && align.length() > 0)
		{
			HorizontalAlignment alignment = HorizontalAlignment.valueOf(align);
			
			switch (alignment) 
			{
				case center: return HasHorizontalAlignment.ALIGN_CENTER;
				case left: return HasHorizontalAlignment.ALIGN_LEFT;
				case right: return HasHorizontalAlignment.ALIGN_RIGHT;
				case justify: return HasHorizontalAlignment.ALIGN_JUSTIFY;
				case localeStart: return HasHorizontalAlignment.ALIGN_LOCALE_START;
				case localeEnd: return HasHorizontalAlignment.ALIGN_LOCALE_END;
				case defaultAlign: return HasHorizontalAlignment.ALIGN_DEFAULT;

				default: return defaultAlignment;
			}
		}
		else
		{
			return defaultAlignment;
		}
	}
	
	/**
	 * 
	 * @param align
	 * @param defaultAlignment
	 * @return
	 */
	public static AutoHorizontalAlignmentConstant getAutoHorizontalAlignment(String align, AutoHorizontalAlignmentConstant defaultAlignment)
	{
		if (align != null && align.length() > 0)
		{
			AutoHorizontalAlignment alignment = AutoHorizontalAlignment.valueOf(align);
			
			switch (alignment) 
			{
				case center: return HasAutoHorizontalAlignment.ALIGN_CENTER;
				case left: return HasAutoHorizontalAlignment.ALIGN_LEFT;
				case right: return HasAutoHorizontalAlignment.ALIGN_RIGHT;
				case justify: return HasAutoHorizontalAlignment.ALIGN_JUSTIFY;
				case localeStart: return HasAutoHorizontalAlignment.ALIGN_LOCALE_START;
				case localeEnd: return HasAutoHorizontalAlignment.ALIGN_LOCALE_END;
				case contentStart: return HasAutoHorizontalAlignment.ALIGN_CONTENT_START;
				case contentEnd: return HasAutoHorizontalAlignment.ALIGN_CONTENT_END;
				case defaultAlign: return HasAutoHorizontalAlignment.ALIGN_DEFAULT;

				default: return defaultAlignment;
			}
		}
		else
		{
			return defaultAlignment;
		}
	}
	
	
	
	/**
	 * 
	 * @param align
	 * @param defaultAlignment
	 * @return
	 */
	public static VerticalAlignmentConstant getVerticalAlignment(String align, VerticalAlignmentConstant defaultAlignment)
	{
		if (align != null && align.length() > 0)
		{
			VerticalAlignment alignment = VerticalAlignment.valueOf(align);

			if (alignment.equals(VerticalAlignment.bottom))
			{
				return HasVerticalAlignment.ALIGN_BOTTOM;
			}
			else if (alignment.equals(VerticalAlignment.middle))
			{
				return HasVerticalAlignment.ALIGN_MIDDLE;
			}
			else if (alignment.equals(VerticalAlignment.top))
			{
				return HasVerticalAlignment.ALIGN_TOP;
			}
			else
			{
				return defaultAlignment;
			}
		}
		else
		{
			return defaultAlignment;
		}
	}		
	
	/**
	 * 
	 * @param align
	 * @return
	 */
	public static VerticalAlignmentConstant getVerticalAlignment(String align)
	{
		return getVerticalAlignment(align, HasVerticalAlignment.ALIGN_MIDDLE);
	}
}
