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
package br.com.sysmap.crux.basic.client;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Grid;

/**
 * Factory for Grid widget
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="grid", library="bas")
public class GridFactory extends HTMLTableFactory<Grid>
{

	@Override
	protected Grid instantiateWidget(Element element, String widgetId)
	{
		return new Grid();
	}

	@Override
	protected void prepareCell(Grid widget, int indexRow, int indexCol, String widgetId)
	{
		if (indexRow < 0 || indexCol < 0)
		{
			throw new IndexOutOfBoundsException(messages.gridInvalidRowColIndexes(widgetId));
		}
		if (widget.getRowCount() < indexRow+1)
		{
			widget.resizeRows(indexRow+1);
		}
		if (widget.getColumnCount() < indexCol+1)
		{
			widget.resizeColumns(indexCol + 1);
		}
	}
}
