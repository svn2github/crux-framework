package br.com.sysmap.crux.advanced.client.grid.model;

import br.com.sysmap.crux.advanced.client.util.StyleUtils;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;

public class Row
{
	private AbstractGrid<?, ?> grid;
	private int index;
	private Element elem;
	private boolean hasSelectionCell;
	private boolean selected;
	private boolean enabled;
		
	protected Row(int index, Element elem, AbstractGrid<?, ?> grid, boolean hasSelectionCell)
	{
		this.index = index;
		this.elem = elem;
		this.hasSelectionCell = hasSelectionCell;
		this.grid = grid;
	}
	
	protected void setCell(Cell cell, String column)
	{
		CellFormatter formatter = grid.getTable().getCellFormatter();
		
		ColumnDefinition def = grid.getColumnDefinitions().getDefinition(column);
		int colIndex = getColumnIndex(column);
		formatter.setAlignment(index, colIndex, def.getHorizontalAlign(), def.getVerticalAlign());
		
		if(def.getWidth() != null)
		{
			formatter.setWidth(index, colIndex, def.getWidth());
		}	
		
		setCell(cell, colIndex);	
	}

	void setCell(Cell cell, int column)
	{
		grid.getTable().setWidget(index, column, cell);
		cell.setRow(this);
		cell.setGrid(grid);
	}
	
	Cell getCell(int column)
	{
		return (Cell) grid.getTable().getWidget(index, column);
	}
	
	protected Cell getCell(String column)
	{
		int colIndex = getColumnIndex(column);
		return (Cell) grid.getTable().getWidget(index, colIndex);
	}
	
	/**
	 * Adds a style name to the row
	 * @param rowIndex
	 */
	public void addStyleDependentName(String styleSuffix)
	{
		StyleUtils.addStyleDependentName(elem, styleSuffix);
	}
	
	/**
	 * Adds a style name to the row
	 * @param rowIndex
	 */
	public void removeStyleDependentName(String styleSuffix)
	{
		StyleUtils.removeStyleDependentName(elem, styleSuffix);
	}
	
	/**
	 * Sets the style name of the row
	 * @param rowIndex
	 */
	void setStyle(String styleName)
	{
		elem.setClassName(styleName);
	}
	
	@SuppressWarnings("unchecked")
	public void setSelected(boolean selected)
	{
		if(hasSelectionCell)
		{
			HasValue<Boolean> selector = (HasValue<Boolean>) getCell(0).getCellWidget();
			selector.setValue(selected);
		}
		
		if(selected)
		{
			addStyleDependentName("selected");
		}
		else
		{
			removeStyleDependentName("selected");
		}
		
		this.selected = selected;
	}
	
	private int getColumnIndex(String column)
	{
		int colIndex = grid.getColumnDefinitions().getColumnIndex(column);
		
		if(hasSelectionCell)
		{
			colIndex++;
		}
		return colIndex;
	}

	/**
	 * @return the selected
	 */
	protected boolean isSelected()
	{
		return selected;
	}

	/**
	 * @return the grid
	 */
	protected AbstractGrid<?, ?> getGrid()
	{
		return grid;
	}
	
	public void setVisible(boolean visible)
	{
		elem.getStyle().setProperty("display", visible ? "" : "none");
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled)
	{
		if(hasSelectionCell)
		{
			CheckBox selector = (CheckBox) getCell(0).getCellWidget();
			selector.setEnabled(enabled);
		}		
		
		if(!enabled)
		{
			addStyleDependentName("disabled");
		}
		else
		{
			removeStyleDependentName("disabled");
		}
		
		this.enabled = enabled;
	}
}