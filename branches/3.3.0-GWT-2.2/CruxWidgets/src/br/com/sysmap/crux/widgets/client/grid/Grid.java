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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import br.com.sysmap.crux.core.client.collection.FastList;
import br.com.sysmap.crux.core.client.datasource.DataSourceRecord;
import br.com.sysmap.crux.core.client.datasource.HasDataSource;
import br.com.sysmap.crux.core.client.datasource.LocalDataSource;
import br.com.sysmap.crux.core.client.datasource.LocalDataSourceCallback;
import br.com.sysmap.crux.core.client.datasource.MeasurableDataSource;
import br.com.sysmap.crux.core.client.datasource.MeasurablePagedDataSource;
import br.com.sysmap.crux.core.client.datasource.MeasurableRemoteDataSource;
import br.com.sysmap.crux.core.client.datasource.PagedDataSource;
import br.com.sysmap.crux.core.client.datasource.RemoteDataSource;
import br.com.sysmap.crux.core.client.datasource.RemoteDataSourceCallback;
import br.com.sysmap.crux.core.client.formatter.Formatter;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;
import br.com.sysmap.crux.widgets.client.event.row.BeforeRowSelectEvent;
import br.com.sysmap.crux.widgets.client.event.row.BeforeRowSelectHandler;
import br.com.sysmap.crux.widgets.client.event.row.BeforeShowDetailsEvent;
import br.com.sysmap.crux.widgets.client.event.row.HasBeforeRowSelectHandlers;
import br.com.sysmap.crux.widgets.client.event.row.LoadRowDetailsEvent;
import br.com.sysmap.crux.widgets.client.event.row.RowClickEvent;
import br.com.sysmap.crux.widgets.client.event.row.RowDoubleClickEvent;
import br.com.sysmap.crux.widgets.client.event.row.RowRenderEvent;
import br.com.sysmap.crux.widgets.client.event.row.ShowRowDetailsEvent;
import br.com.sysmap.crux.widgets.client.paging.Pageable;
import br.com.sysmap.crux.widgets.client.paging.Pager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * A paged sortable data grid
 * @author Gesse S. F. Dafe
 */
public class Grid extends AbstractGrid<DataRow> implements Pageable, HasDataSource<PagedDataSource<?>>, HasBeforeRowSelectHandlers
{	
	private int pageSize;
	private PagedDataSource<?> dataSource;
	private FastList<ColumnHeader> headers = new FastList<ColumnHeader>();
	private boolean autoLoadData;
	private boolean loaded;
	private String currentSortingColumn;
	private boolean ascendingSort;
	private Pager pager; 
	private RowSelectionModel rowSelectionModel;
	private long generatedWidgetId = 0;
	private String emptyDataFilling;
	private String defaultSortingColumn;
	private SortingType defaultSortingType;
	private RowDetailsManager rowDetailsManager;
	
	/**
	 * @param columnDefinitions the columns to be rendered
	 * @param pageSize the number of rows per page
	 * @param rowSelection the behavior of the grid about line selection
	 * @param cellSpacing the space between the cells
	 * @param autoLoadData if <code>true</code>, when a data source is set, its first page records are fetched and rendered.
	 * @param stretchColumns if <code>true</code>, the width of the columns are auto adjusted to fit the grid width. Prevents horizontal scrolling.   
	 * @param highlightRowOnMouseOver if <code>true</code>, rows change their styles when mouse passed over them
	 * @param emptyDataFilling an alternative text to be shown when there is no data for some data cell
	 * @param fixedCellSize equivalent of setting CSS attribute <code>table-layout</code> to <code>fixed</code>
	 * @param defaultSortingColumn the column to be used to automatically sort the grid's data when it is rendered for the first time 
	 * @param defaultSortingType tells the grid if <code>defaultSortingColumn</code> should be used ascending or descending
	 */
	public Grid(ColumnDefinitions columnDefinitions, int pageSize, RowSelectionModel rowSelection, int cellSpacing, boolean autoLoadData, boolean stretchColumns, boolean highlightRowOnMouseOver, String emptyDataFilling, boolean fixedCellSize, String defaultSortingColumn, SortingType defaultSortingType)
	{
		this(columnDefinitions, pageSize, rowSelection, cellSpacing, autoLoadData, stretchColumns, highlightRowOnMouseOver, emptyDataFilling, fixedCellSize, defaultSortingColumn, defaultSortingType, null, false);
	}
	
	/**
	 * Full constructor
	 * @param columnDefinitions the columns to be rendered
	 * @param pageSize the number of rows per page
	 * @param rowSelection the behavior of the grid about line selection
	 * @param cellSpacing the space between the cells
	 * @param autoLoadData if <code>true</code>, when a data source is set, its first page records are fetched and rendered.
	 * @param stretchColumns if <code>true</code>, the width of the columns are auto adjusted to fit the grid width. Prevents horizontal scrolling.   
	 * @param highlightRowOnMouseOver if <code>true</code>, rows change their styles when mouse passed over them
	 * @param emptyDataFilling an alternative text to be shown when there is no data for some data cell
	 * @param fixedCellSize equivalent of setting CSS attribute <code>table-layout</code> to <code>fixed</code>
	 * @param defaultSortingColumn the column to be used to automatically sort the grid's data when it is rendered for the first time 
	 * @param defaultSortingType tells the grid if <code>defaultSortingColumn</code> should be used ascending or descending
	 * @param rowDetailsDefinition the template HTML element used to create on-demand row details
	 * @param showRowDetailsIcon if <code>true</code>, the second column of the grid will contain icons for expanding or collapsing the row's details 
	 */
	public Grid(ColumnDefinitions columnDefinitions, int pageSize, RowSelectionModel rowSelection, int cellSpacing, boolean autoLoadData, boolean stretchColumns, boolean highlightRowOnMouseOver, String emptyDataFilling, boolean fixedCellSize, String defaultSortingColumn, SortingType defaultSortingType, RowDetailsDefinition rowDetailsDefinition, boolean showRowDetailsIcon)
	{
		super(columnDefinitions, rowSelection, cellSpacing, stretchColumns, highlightRowOnMouseOver, fixedCellSize, rowDetailsDefinition, showRowDetailsIcon);
		getColumnDefinitions().setGrid(this);
		this.emptyDataFilling = emptyDataFilling != null ? emptyDataFilling : " ";
		this.pageSize = pageSize;
		this.rowSelectionModel = rowSelection;
		this.autoLoadData = autoLoadData;
		this.defaultSortingColumn = defaultSortingColumn;
		this.defaultSortingType = defaultSortingType;
		if(hasRowDetails())
		{
			this.rowDetailsManager = new RowDetailsManager(rowDetailsDefinition);
		}
		super.render();
	}
	
	/**
	 * Sets the data source and re-renders the grid
	 * @param dataSource
	 */
	public void setDataSource(PagedDataSource<?> dataSource)
	{
		this.dataSource = dataSource;
		this.dataSource.setPageSize(this.pageSize);
		
		if(hasRowDetails())
		{
			this.rowDetailsManager.reset();
		}
		
		if(this.dataSource instanceof RemoteDataSource<?>)
		{
			RemoteDataSource<?> remote = (RemoteDataSource<?>) this.dataSource;
			
			remote.setCallback(new RemoteDataSourceCallback()
			{
				public void execute(int startRecord, int endRecord)
				{
					loaded = true;
					if(!autoSort())
					{
						render();
					}
				}
				
				public void cancelFetching()
				{
					render();			
				}
			});
			
			if(autoLoadData)
			{
				loadData();
			}
		}
		else if(this.dataSource instanceof LocalDataSource<?>)
		{
			LocalDataSource<?> local = (LocalDataSource<?>) this.dataSource;
			
			local.setCallback(new LocalDataSourceCallback()
			{
				public void execute()
				{
					loaded = true;
					if(!autoSort())
					{
						render();
					}
				}
			});
			
			if(autoLoadData)
			{
				loadData();
			}
		}
	}
	
	public void loadData()
	{
		if(!this.loaded)
		{
			if(this.dataSource instanceof RemoteDataSource)
			{
				if(this.dataSource instanceof MeasurableDataSource)
				{
					((MeasurableRemoteDataSource<?>) this.dataSource).load();
				}
				else
				{
					this.dataSource.nextPage();
				}
			}
			else if(this.dataSource instanceof LocalDataSource)
			{
				LocalDataSource<?> local = (LocalDataSource<?>) this.dataSource;
				local.load();
			}
		}
	}

	@Override
	protected DataRow createRow(int index, Element element)
	{	
		return new DataRow(index, element, this, hasSelectionColumn(), hasRowDetails(), hasRowDetailsIconColumn());
	}

	@Override
	protected int getRowsToBeRendered()
	{
		if(isDataLoaded())
		{
			if(this.dataSource.getCurrentPage() == 0)
			{
				this.dataSource.nextPage();
			}
			
			return this.dataSource.getCurrentPageSize();
		}
		
		return 0;
	}
	
	/**
	 * Gets the current page row count
	 * @return
	 */
	public int getCurrentPageSize()
	{
		return getRowsToBeRendered();
	}

	@Override
	protected void onClear()
	{
		if(this.dataSource != null)
		{
			this.dataSource.reset();
		}
		
		this.currentSortingColumn = null;
		this.ascendingSort = false;
		this.loaded = false;
		
		if(this.pager != null)
		{
			this.pager.update(0, false);
		}
		
		if(hasRowDetails())
		{
			this.rowDetailsManager.reset();
		}
	}
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.grid.AbstractGrid#onShowDetails(boolean, br.com.sysmap.crux.widgets.client.grid.Row, boolean)
	 */
	protected void onShowDetails(boolean show, Row row, boolean fireEvents)
	{
		if (hasRowDetails()) 
		{
			boolean proceed = true;
			DataRow dataRow = (DataRow) row;

			if(show)
			{
				if(fireEvents)
				{
					BeforeShowDetailsEvent event = BeforeShowDetailsEvent.fire(this, dataRow);
					proceed = !event.isCanceled();
				}
			}
			
			if(proceed)
			{
				dataRow.showDetailsArea(show);
				
				if(show)
				{	
					boolean detailsPanelCreated = dataRow.getDetailsPanel() != null;
					
					if(!detailsPanelCreated)
					{
						DataSourceRecord<?> record = dataRow.getDataSourceRecord();
						boolean detailLoaded = this.rowDetailsManager.isDetailLoaded(record);
						createAndAttachDetails(dataRow, record);
	
						if(fireEvents)
						{
							if(detailLoaded)
							{
								ShowRowDetailsEvent.fire(this, dataRow);
							}
							else
							{
								LoadRowDetailsEvent.fire(this, dataRow);
							}
						}
					}
					else
					{
						if(fireEvents)
						{
							ShowRowDetailsEvent.fire(this, dataRow);
						}
					}
				}
			}
		}
	}

	/**
	 * Creates and attaches the details widget to the row
	 * @param dataRow
	 * @param record
	 */
	private void createAndAttachDetails(DataRow dataRow, DataSourceRecord<?> record) 
	{
		HashMap<String, Widget> widgetsByOriginalId = new HashMap<String, Widget>();
		Widget w = this.rowDetailsManager.createWidget(widgetsByOriginalId);
		RowDetailsPanel details = new RowDetailsPanel(dataRow, widgetsByOriginalId);
		details.add(w);
		dataRow.attachDetails(details);
		ensureVisible(dataRow.getDetailsPanel());
		this.rowDetailsManager.setDetailLoaded(record);
	}

	@Override
	protected boolean onSelectRow(boolean select, DataRow row, boolean fireEvents)
	{
		boolean proceed = true;
		
		if(fireEvents)
		{
			BeforeRowSelectEvent event = BeforeRowSelectEvent.fire(this, row);
			proceed = !event.isCanceled();
		}
		
		if(proceed)
		{
			if(select && (RowSelectionModel.single.equals(rowSelectionModel) || RowSelectionModel.singleRadioButton.equals(rowSelectionModel)))
			{
				DataSourceRecord<?>[] records = dataSource.getSelectedRecords();
				if(records != null)
				{
					for (int i = 0; i < records.length; i++)
					{
						DataSourceRecord<?> editableDataSourceRecord = records[i];
						editableDataSourceRecord.setSelected(false);
					}
				}
				
				Iterator<DataRow> it = getRowIterator();
				
				while(it.hasNext())
				{
					DataRow dataRow = it.next();
					dataRow.setSelected(false);
				}
			}
			
			row.setSelected(select);
		}
		
		return proceed;
	}

	@Override
	protected void renderRow(DataRow row)
	{
		row.setDataSourceRecord(dataSource.getRecord());
		
		ColumnDefinitions defs = getColumnDefinitions();
		Iterator<ColumnDefinition> it = defs.getIterator();
		while (it.hasNext())
		{
			ColumnDefinition column = it.next();
			
			if(column.isVisible())
			{
				Widget widget = null;
				String key = column.getKey();
				boolean wrapLine = true;
				boolean truncate = true;
				
				if(column instanceof DataColumnDefinition)
				{
					wrapLine = true;
					truncate = false;
					widget = createDataLabel((DataColumnDefinition) column, key);					
				}
				else if(column instanceof WidgetColumnDefinition)
				{
					wrapLine = false;
					truncate = true;
					widget = createWidgetForColumn((WidgetColumnDefinition) column);
				}
				
				row.setCell(createCell(widget, wrapLine, truncate), key);
			}
		}
		
		row.setSelected(row.getDataSourceRecord().isSelected());
		row.setEnabled(!row.getDataSourceRecord().isReadOnly());
		
		if(dataSource.hasNextRecord())
		{
			dataSource.nextRecord();
		}
	}

	/**
	 * Creates the widget to be hosted by a WidgetColumn
	 * @param column
	 * @return
	 */
	private Widget createWidgetForColumn(WidgetColumnDefinition column)
	{
		try
		{
			return createWidget(column.getWidgetTemplate());
		}
		catch (InterfaceConfigException e)
		{
			GWT.log(e.getMessage(), e);
			throw new RuntimeException(WidgetMsgFactory.getMessages().errorCreatingWidgetForColumn(column.getKey()));
		}
	}
	
	/**
	 * Creates a widget
	 * @param column
	 * @return
	 * @throws InterfaceConfigException 
	 */
	private Widget createWidget(Element template) throws InterfaceConfigException
	{
		Element clone = (Element) template.cloneNode(true);
		setRandomId(clone);
		ScreenFactory factory = ScreenFactory.getInstance();
		return factory.newWidget(clone, clone.getId(), factory.getMetaElementType(clone), false);
	}

	/**
	 * Generates and sets a random ID on the given element and its children.
	 * @param template
	 */
	private void setRandomId(Element template)
	{
		template.setId(template.getId() + "_" + generateWidgetIdSufix());
		NodeList<Node> children = template.getChildNodes();
		if(children != null)
		{
			int length = children.getLength();
			for(int i = 0; i < length; i++)
			{
				Node childNode = children.getItem(i);
				if(Element.is(childNode))
				{
					Element childElement = Element.as(childNode);
					setRandomId(childElement);
				}
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	private long generateWidgetIdSufix()
	{
		if(generatedWidgetId == 0)
		{
			generatedWidgetId = new Date().getTime();
		}
		
		return ++generatedWidgetId;
	}

	private Widget createDataLabel(DataColumnDefinition dataColumn, String key)
	{
		String formatterName = dataColumn.getFormatter();
		Object value = dataSource.getValue(key);
		String str = emptyDataFilling;
		boolean useEmptyDataStyle = true;
		
		if(value != null)
		{
			if(formatterName != null && formatterName.length() > 0)
			{
				Formatter formatter = Screen.getFormatter(formatterName);
				str = formatter.format(value);
				useEmptyDataStyle = false;
			}
			else
			{
				String strValue = value.toString(); 
				if(strValue.length() > 0)
				{
					str = strValue;
					useEmptyDataStyle = false;
				}
			}
		}
		
		Label label = new Label(str);
		
		if(useEmptyDataStyle)
		{
			label.addStyleName("emptyData");
		}
		
		if(!dataColumn.isWrapLine())
		{
			label.getElement().getStyle().setProperty("whiteSpace", "nowrap");
		}		 
		else
		{
			label.getElement().getStyle().setProperty("whiteSpace", "normal");
		}
		
		return label;
	}
	
	@Override
	protected Cell createColumnHeaderCell(final ColumnDefinition columnDefinition)
	{
		ColumnHeader header = new ColumnHeader(columnDefinition, this);		
		headers.add(header);
		Cell cell = createHeaderCell(header);
		return cell;
	}

	@Override
	protected void onBeforeRenderRows()
	{
		if(isDataLoaded())
		{
			if(this.dataSource.getCurrentPage() > 0 && this.dataSource.getCurrentPageSize() == 0)
			{
				this.previousPage();
				return;
			}
			
			updatePager();
	
			for (int i=0; i<headers.size(); i++)
			{
				ColumnHeader header = headers.get(i);
				header.applySortingLayout();
			}
			
			dataSource.firstRecord();
		}
	}
	
	private void updatePager()
	{
		if(isDataLoaded() && this.pager != null)
		{
			if(this.pager != null)
			{
				this.pager.update(this.dataSource.getCurrentPage(),  !this.dataSource.hasNextPage());
			}
		}
	}

	public int getPageCount()
	{
		if(this.dataSource instanceof MeasurablePagedDataSource<?>)
		{
			MeasurablePagedDataSource<?> ds = (MeasurablePagedDataSource<?>) this.dataSource;
			return ds.getPageCount();
		}
		else
		{
			return -1;
		}
	}

	public void nextPage()
	{
		if(isDataLoaded())
		{
			this.dataSource.nextPage();
			
			if(!(this.dataSource instanceof RemoteDataSource<?>))
			{
				render();
			}
		}
	}

	public void previousPage()
	{
		if(isDataLoaded())
		{
			this.dataSource.previousPage();
			
			if(!(this.dataSource instanceof RemoteDataSource<?>))
			{
				render();
			}
		}
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.paging.Pageable#setPager(br.com.sysmap.crux.widgets.client.paging.Pager)
	 */
	public void setPager(Pager pager)
	{
		this.pager = pager;
		updatePager();
	}
	
	@Override
	protected void onClearRendering()
	{
		this.headers = new FastList<ColumnHeader>();
		if(hasRowDetails())
		{
			this.rowDetailsManager.clearRendering();
		}
	}
	
	/**
	 * @return the dataSource
	 */
	public PagedDataSource<?> getDataSource()
	{
		return dataSource;
	}

	/**
	 * Sorts the grid's data by the given column
	 * @param columnKey
	 */
	public void sort(String columnKey, boolean ascending)
	{
		if(this.isDataLoaded())
		{
			this.dataSource.sort(columnKey, ascending);
			this.ascendingSort = ascending;
			this.currentSortingColumn = columnKey;
			this.render();
		}		
	}

	@Override
	public List<DataRow> getSelectedRows()
	{
		List<DataRow> result = new ArrayList<DataRow>();
		
		Iterator<DataRow> rows = getRowIterator();
		
		while(rows.hasNext())
		{
			DataRow row = rows.next();
			
			if(row.getDataSourceRecord().isSelected())
			{
				result.add(row);
			}
		}
		
		return result;
	}
	
	@Override
	public List<DataRow> getCurrentPageRows()
	{
		List<DataRow> result = new ArrayList<DataRow>();
		
		Iterator<DataRow> rows = getRowIterator();
		
		while(rows.hasNext())
		{
			DataRow row = rows.next();
			result.add(row);
		}
		
		return result;
	}
	
	/**
	 * @return
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	public Object[] getSelectedDataRows()
	{
		if(this.dataSource != null)
		{
			DataSourceRecord[] selectedRecords = this.dataSource.getSelectedRecords();
			
			if(selectedRecords != null)
			{
				Object[] selectedObjs = new Object[selectedRecords.length]; 

				for (int i = 0; i < selectedRecords.length; i++)
				{
					Object o = this.dataSource.getBoundObject(selectedRecords[i]);
					selectedObjs[i] = o;					
				}

				return selectedObjs;
			}			
		}		
		
		return new Object[0];
	}
	
	/**
	 * @see br.com.sysmap.crux.widgets.client.event.row.HasBeforeRowSelectHandlers#addBeforeRowSelectHandler(br.com.sysmap.crux.widgets.client.event.row.BeforeRowSelectHandler)
	 */
	public HandlerRegistration addBeforeRowSelectHandler(BeforeRowSelectHandler handler)
	{
		return addHandler(handler, BeforeRowSelectEvent.getType());
	}

	@Override
	protected void fireRowRenderEvent(DataRow row)
	{
		RowRenderEvent.fire(this, row);
	}

	@Override
	protected void fireRowClickEvent(DataRow row)
	{
		RowClickEvent.fire(this, row);
	}

	@Override
	protected void fireRowDoubleClickEvent(DataRow row)
	{
		RowDoubleClickEvent.fire(this, row);
	}

	public boolean isLoaded()
	{
		return loaded;
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.paging.Pageable#goToPage(int)
	 */
	public void goToPage(int page)
	{
		if(isDataLoaded())
		{
			if(this.dataSource instanceof MeasurablePagedDataSource<?>)
			{
				((MeasurablePagedDataSource<?>) this.dataSource).setCurrentPage(page);

				if(!(this.dataSource instanceof RemoteDataSource<?>))
				{
					render();
				}
			}
			else
			{
				throw new UnsupportedOperationException(WidgetMsgFactory.getMessages().gridRandomPagingNotSupported());
			}
		}
		
	}

	/**
	 * @see br.com.sysmap.crux.widgets.client.paging.Pageable#isDataLoaded()
	 */
	public boolean isDataLoaded()
	{
		return this.dataSource != null && loaded;
	}
	
	/**
	 * Sorts the grid's data when it is loaded for the first time
	 */
	private boolean autoSort()
	{
		boolean sort = isAutoSortEnabled();
		
		if(sort)
		{
			sort(this.defaultSortingColumn, !this.defaultSortingType.equals(SortingType.descending));
		}
		
		return sort;
	}
	
	@Override
	public DataRow getRow(Widget w)
	{
		DataRow row = super.getRow(w);
		
		if (row == null && hasRowDetails()) 
		{
			while(!(w instanceof RowDetailsPanel))
			{
				w = w.getParent();
			}
			
			if(w instanceof RowDetailsPanel)
			{
				row = (DataRow) ((RowDetailsPanel) w).getRow();
			}
		}
		
		return row;
	}
	
	/**
	 * Checks if auto sorting is enabled
	 * @return
	 */
	private boolean isAutoSortEnabled()
	{
		if(!StringUtils.isEmpty(this.defaultSortingColumn))
		{
			ColumnDefinition column = getColumnDefinition(this.defaultSortingColumn);
			
			if(column != null && column instanceof DataColumnDefinition)
			{
				return true;
			}
			else
			{
				throw new IllegalArgumentException(WidgetMsgFactory.getMessages().errorGridNoDataColumnFound(this.defaultSortingColumn));
			}		
		}
		
		return false;
	}
	
	/**
	 * Grid default sorting type
	 * @author Gesse S. F. Dafe
	 */
	public enum SortingType
	{
		ascending,
		descending
	}
	
	/**
	 * @author Gesse S. F. Dafe
	 */
	protected static class ColumnHeader extends Composite
	{
		private FocusPanel clickable;
		private Label columnLabelArrow;
		
		private Grid grid;
		private ColumnDefinition columnDefinition;
		
		public ColumnHeader(ColumnDefinition columnDefinition, Grid grid)
		{
			this.grid = grid;
			this.columnDefinition = columnDefinition;
			
			clickable = new FocusPanel();
			
			HorizontalPanel panel = new HorizontalPanel();
			panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			
			Label columnLabel = new Label(columnDefinition.getLabel());
			columnLabel.setStyleName("label");
			
			columnLabelArrow = new Label(" ");
			columnLabelArrow.setStyleName("arrow");
			
			panel.add(columnLabel);
			panel.add(columnLabelArrow);
			
			clickable.add(panel);
			if (isDataColumnSortable(columnDefinition))
			{
				clickable.addClickHandler(createClickHandler());
			}
			
			initWidget(clickable);
			
			setStyleName("columnSorter");
		}
		
		/**
		 * @param columnDefinition2
		 * @return
		 */
		private boolean isDataColumnSortable(ColumnDefinition columnDefinition)
		{
			if(columnDefinition instanceof DataColumnDefinition)
			{
				return grid.isDataLoaded() 
				&& ((DataColumnDefinition) columnDefinition).isSortable()
				&& this.grid.getDataSource().getMetadata().getColumn(columnDefinition.getKey()).isSortable();
			}
			return false;
					
		}

		/**
		 * @return
		 */
		private ClickHandler createClickHandler()
		{
			return new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					String columnKey = columnDefinition.getKey();
					String previousSorting = grid.currentSortingColumn;
					grid.currentSortingColumn = columnKey;
										
					boolean resorting = columnKey.equals(previousSorting);
					boolean descending = resorting && grid.ascendingSort;
										
					grid.sort(columnKey, !descending);									
				}			
			};
		}
		
		void applySortingLayout()
		{
			if(this.columnDefinition.getKey().equals(grid.currentSortingColumn))
			{
				if(grid.ascendingSort)
				{
					addStyleDependentName("asc");
				}
				else
				{
					addStyleDependentName("desc");
				}
			}
			else
			{
				removeStyleDependentName("asc");
				removeStyleDependentName("desc");
			}
		}
	}
}