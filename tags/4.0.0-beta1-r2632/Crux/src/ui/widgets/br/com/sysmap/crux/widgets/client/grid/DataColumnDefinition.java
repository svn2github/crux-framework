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
package br.com.sysmap.crux.widgets.client.grid;

import br.com.sysmap.crux.core.client.formatter.Formatter;

import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

public class DataColumnDefinition extends ColumnDefinition
{
	private Formatter formatter;
	private boolean wrapLine;
	private boolean sortable;

	public DataColumnDefinition(String label, String width, Formatter formatter, boolean visible, boolean sortable, boolean wrapLine, HorizontalAlignmentConstant horizontalAlign, VerticalAlignmentConstant verticalAlign)
	{
		super(label, width, visible, horizontalAlign, verticalAlign);
		this.formatter = formatter;
		this.wrapLine = wrapLine;
		this.sortable = sortable;
	}

	/**
	 * @return the formatter
	 */
	public Formatter getFormatter()
	{
		return formatter;
	}

	/**
	 * @return the wrapLine
	 */
	public boolean isWrapLine()
	{
		return wrapLine;
	}

	public boolean isSortable()
    {
    	return sortable;
    }

	public void setSortable(boolean sortable)
    {
    	this.sortable = sortable;
    }
}