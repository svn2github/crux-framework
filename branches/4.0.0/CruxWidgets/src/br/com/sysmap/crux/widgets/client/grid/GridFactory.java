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

import br.com.sysmap.crux.core.client.collection.Array;
import br.com.sysmap.crux.core.client.datasource.PagedDataSource;
import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.declarative.TagEvent;
import br.com.sysmap.crux.core.client.declarative.TagEvents;
import br.com.sysmap.crux.core.client.screen.AttributeProcessor;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.factory.align.AlignmentAttributeParser;
import br.com.sysmap.crux.core.client.screen.factory.align.HorizontalAlignment;
import br.com.sysmap.crux.core.client.screen.factory.align.VerticalAlignment;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;
import br.com.sysmap.crux.widgets.client.event.row.RowEventsBind.BeforeRowSelectEvtBind;
import br.com.sysmap.crux.widgets.client.event.row.RowEventsBind.RowClickEvtBind;
import br.com.sysmap.crux.widgets.client.event.row.RowEventsBind.RowDoubleClickEvtBind;
import br.com.sysmap.crux.widgets.client.event.row.RowEventsBind.RowRenderEvtBind;
import br.com.sysmap.crux.widgets.client.grid.Grid.SortingType;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

/**
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="grid", library="widgets")
public class GridFactory extends WidgetCreator<Grid, WidgetCreatorContext>
{
	/**
	 * @param cellSpacing 
	 * @param autoLoad 
	 * @see br.com.sysmap.crux.core.rebind.widget.WidgetCreator#instantiateWidget(com.google.gwt.dom.client.Element, java.lang.String)
	 */
	public Grid instantiateWidget(CruxMetaDataElement gridElem, String widgetId) throws InterfaceConfigException
	{
		Grid grid = new Grid(getColumnDefinitions(gridElem), getPageSize(gridElem), 
				                               getRowSelectionModel(gridElem), getCellSpacing(gridElem), 
				                               getAutoLoad(gridElem), getStretchColumns(gridElem), getHighlightRowOnMouseOver(gridElem),
				                               getEmptyDataFilling(gridElem), isFixedCellSize(gridElem), getSortingColumn(gridElem), getSortingType(gridElem));
		return grid;
	}
	
	private SortingType getSortingType(CruxMetaDataElement gridElem)
	{
		String sortingType = gridElem.getProperty("defaultSortingType");
		if(!StringUtils.isEmpty(sortingType))
		{
			SortingType sort = SortingType.valueOf(sortingType);
			return sort;
		}
		return null;
	}

	private String getSortingColumn(CruxMetaDataElement gridElem)
	{
		return gridElem.getProperty("defaultSortingColumn");
	}

	private boolean isFixedCellSize(CruxMetaDataElement gridElem)
	{
		String fixedCellSize = gridElem.getProperty("fixedCellSize");
		
		if(fixedCellSize != null && fixedCellSize.trim().length() > 0)
		{
			return Boolean.parseBoolean(fixedCellSize);
		}
		
		return false;
	}
	
	private String getEmptyDataFilling(CruxMetaDataElement gridElem)
	{
		String emptyDataFilling = gridElem.getProperty("emptyDataFilling");
		
		if(emptyDataFilling != null && emptyDataFilling.trim().length() > 0)
		{
			return emptyDataFilling;
		}
		
		return null;
	}
	
	private boolean getHighlightRowOnMouseOver(CruxMetaDataElement gridElem)
	{
		String highlight = gridElem.getProperty("highlightRowOnMouseOver");
		
		if(highlight != null && highlight.trim().length() > 0)
		{
			return Boolean.parseBoolean(highlight);
		}
		
		return false;
	}

	private boolean getAutoLoad(CruxMetaDataElement gridElem)
	{
		String autoLoad = gridElem.getProperty("autoLoadData");
		
		if(autoLoad != null && autoLoad.trim().length() > 0)
		{
			return Boolean.parseBoolean(autoLoad);
		}
		
		return false;
	}
	
	private boolean getStretchColumns(CruxMetaDataElement gridElem)
	{
		String stretchColumns = gridElem.getProperty("stretchColumns");
		
		if(stretchColumns != null && stretchColumns.trim().length() > 0)
		{
			return Boolean.parseBoolean(stretchColumns);
		}
		
		return false;
	}

	private int getCellSpacing(CruxMetaDataElement gridElem)
	{
		String spacing = gridElem.getProperty("cellSpacing");
		
		if(spacing != null && spacing.trim().length() > 0)
		{
			return Integer.parseInt(spacing);
		}
		
		return 1;
	}

	/**
	 * @author Gesse Dafe
	 */
	public static class DataSourceAttributeParser implements AttributeProcessor<WidgetCreatorContext>
	{
		/**
		 * @see br.com.sysmap.crux.core.client.screen.AttributeProcessor#processAttribute(br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext, java.lang.String)
		 */
		public void processAttribute(WidgetCreatorContext context, String propertyValue)
		{
			String dataSourceName = context.readWidgetProperty("dataSource");
			
			if(!StringUtils.isEmpty(dataSourceName))
			{
				PagedDataSource<?> dataSource = (PagedDataSource<?>) Screen.createDataSource(dataSourceName);
				Grid widget = context.getWidget();			
				widget.setDataSource(dataSource);
			}
		}		
	}

	/**
	 * @param gridElem
	 * @return
	 */
	private RowSelectionModel getRowSelectionModel(CruxMetaDataElement gridElem)
	{
		String rowSelection = gridElem.getProperty("rowSelection");
		
		if(rowSelection != null && rowSelection.length() > 0)
		{
			if("unselectable".equals(rowSelection))
			{
				return RowSelectionModel.unselectable;
			}
			else if("single".equals(rowSelection))
			{
				return RowSelectionModel.single;
			}
			else if("multiple".equals(rowSelection))
			{
				return RowSelectionModel.multiple;
			}
			else if("singleRadioButton".equals(rowSelection))
			{
				return RowSelectionModel.singleRadioButton;
			}
			else if("multipleCheckBox".equals(rowSelection))
			{
				return RowSelectionModel.multipleCheckBox;
			}
			else if("multipleCheckBoxSelectAll".equals(rowSelection))
			{
				return RowSelectionModel.multipleCheckBoxSelectAll;
			}
		}
		
		return RowSelectionModel.unselectable;
	}

	/**
	 * @param gridElem
	 * @return
	 */
	private int getPageSize(CruxMetaDataElement gridElem)
	{
		String pageSize = gridElem.getProperty("pageSize");
		
		if(pageSize != null && pageSize.length() > 0)
		{
			return Integer.parseInt(pageSize);
		}
		
		return Integer.MAX_VALUE;
	}

	/**
	 * @param gridElem
	 * @return
	 * @throws InterfaceConfigException
	 */
	private ColumnDefinitions getColumnDefinitions(CruxMetaDataElement gridElem) throws InterfaceConfigException
	{
		ColumnDefinitions defs = new ColumnDefinitions();

		Array<CruxMetaDataElement> colElems = ensureChildren(gridElem, false);
		int colsSize = colElems.size();
		if(colsSize > 0)
		{
			for (int i=0; i<colsSize; i++)
			{
				CruxMetaDataElement colElem = colElems.get(i);
				if (colElem != null)
				{
					String width = colElem.getProperty("width");
					String strVisible = colElem.getProperty("visible");
					String strSortable = colElem.getProperty("sortable");					
					String strWrapLine = colElem.getProperty("wrapLine");
					String label = colElem.getProperty("label");
					String key = colElem.getProperty("key");
					String strFormatter = colElem.getProperty("formatter");
					String hAlign = colElem.getProperty("horizontalAlignment");
					String vAlign = colElem.getProperty("verticalAlignment");

					boolean visible = (strVisible != null && strVisible.length() > 0) ? Boolean.parseBoolean(strVisible) : true;
					boolean sortable = (strSortable != null && strSortable.length() > 0) ? Boolean.parseBoolean(strSortable) : true;
					boolean wrapLine = (strWrapLine != null && strWrapLine.length() > 0) ? Boolean.parseBoolean(strWrapLine) : false;
					String formatter = (strFormatter != null && strFormatter.length() > 0) ? strFormatter : null;
					label = (label != null && label.length() > 0) ? ScreenFactory.getInstance().getDeclaredMessage(label) : "";

					ColumnDefinition def = null;

					String columnType = getChildName(colElem);
					if("dataColumn".equals(columnType))
					{
						def = new DataColumnDefinition(
								label, 
								width, 
								formatter, 
								visible,
								sortable, 
								wrapLine,
								AlignmentAttributeParser.getHorizontalAlignment(hAlign, HasHorizontalAlignment.ALIGN_CENTER),
								AlignmentAttributeParser.getVerticalAlignment(vAlign, HasVerticalAlignment.ALIGN_MIDDLE));
					}
					else if("widgetColumn".equals(columnType))
					{
						def = new WidgetColumnDefinition(
								label, 
								width, 
								ensureFirstChild(colElem, false),
								visible, 
								AlignmentAttributeParser.getHorizontalAlignment(hAlign, HasHorizontalAlignment.ALIGN_CENTER),
								AlignmentAttributeParser.getVerticalAlignment(vAlign, HasVerticalAlignment.ALIGN_MIDDLE));
					}

					defs.add(key, def);
				}
			}
		}
		else
		{
			throw new InterfaceConfigException(WidgetMsgFactory.getMessages().gridDoesNotHaveColumns(gridElem.getProperty("id")));
		}
				
		return defs;
	}
	
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="pageSize", type=Integer.class, defaultValue="8"),
		@TagAttributeDeclaration(value="rowSelection", type=RowSelectionModel.class, defaultValue="unselectable"),
		@TagAttributeDeclaration(value="cellSpacing", type=Integer.class, defaultValue="1"),
		@TagAttributeDeclaration(value="autoLoadData", type=Boolean.class, defaultValue="false"),
		@TagAttributeDeclaration(value="stretchColumns", type=Boolean.class, defaultValue="false"),
		@TagAttributeDeclaration(value="highlightRowOnMouseOver", type=Boolean.class, defaultValue="false"),
		@TagAttributeDeclaration(value="fixedCellSize", type=Boolean.class, defaultValue="false"),
		@TagAttributeDeclaration(value="emptyDataFilling", type=String.class, defaultValue=" "),
		@TagAttributeDeclaration(value="defaultSortingColumn", type=String.class),
		@TagAttributeDeclaration(value="defaultSortingType", type=SortingType.class, defaultValue="ascending")
	})
	@TagAttributes({
		@TagAttribute(value="dataSource", processor=DataSourceAttributeParser.class)
	})
	public void processAttributes(WidgetCreatorContext context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	@Override
	@TagEvents({
		@TagEvent(RowClickEvtBind.class),
		@TagEvent(RowDoubleClickEvtBind.class),
		@TagEvent(RowRenderEvtBind.class),
		@TagEvent(BeforeRowSelectEvtBind.class)
	})
	public void processEvents(WidgetCreatorContext context) throws InterfaceConfigException
	{
		super.processEvents(context);
	}
	
	@Override
	@TagChildren({
		@TagChild(value=ColumnProcessor.class, autoProcess=false)
	})
	public void processChildren(WidgetCreatorContext context) throws InterfaceConfigException {}
	
	
	@TagChildAttributes(maxOccurs="unbounded")
	public static class ColumnProcessor extends ChoiceChildProcessor<Grid, WidgetCreatorContext>
	{
		@Override
		@TagChildren({
			@TagChild(DataColumnProcessor.class),
			@TagChild(WidgetColumnProcessor.class)
		})
		public void processChildren(WidgetCreatorContext context) throws InterfaceConfigException {}
	}

	
	@TagChildAttributes(tagName="dataColumn", minOccurs="0", maxOccurs="unbounded")
	public static class DataColumnProcessor extends WidgetChildProcessor<Grid, WidgetCreatorContext>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration("width"),
			@TagAttributeDeclaration(value="visible", type=Boolean.class),
			@TagAttributeDeclaration(value="sortable", type=Boolean.class, defaultValue="true"),
			@TagAttributeDeclaration(value="wrapLine", type=Boolean.class, defaultValue="false"),
			@TagAttributeDeclaration("label"),
			@TagAttributeDeclaration(value="key", required=true),
			@TagAttributeDeclaration("formatter"),
			@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign"),
			@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
		})
		public void processChildren(WidgetCreatorContext context) throws InterfaceConfigException {}
	}

	@TagChildAttributes(tagName="widgetColumn", minOccurs="0", maxOccurs="unbounded")
	public static class WidgetColumnProcessor extends WidgetChildProcessor<Grid, WidgetCreatorContext>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration("width"),
			@TagAttributeDeclaration(value="visible", type=Boolean.class),
			@TagAttributeDeclaration("label"),
			@TagAttributeDeclaration(value="key", required=true),
			@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign"),
			@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
		})
		@TagChildren({
			@TagChild(WidgetProcessor.class)
		})
		public void processChildren(WidgetCreatorContext context) throws InterfaceConfigException {}
	}
	
	public static class WidgetProcessor extends AnyWidgetChildProcessor<Grid, WidgetCreatorContext>{}
}