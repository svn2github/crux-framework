package br.com.sysmap.crux.widgets.client.grid;

import java.util.HashMap;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple panel where the details of a grid's row will be attached.
 * @author Gesse Dafe
 */
public class RowDetailsPanel extends Composite
{
	private SimplePanel base = new SimplePanel();
	private Row row;
	private final HashMap<String, Widget> widgetsByOriginalId;

	RowDetailsPanel(Row row, HashMap<String, Widget> widgetsByOriginalId) 
	{
		this.row = row;
		this.widgetsByOriginalId = widgetsByOriginalId;
		initWidget(base);
	}

	Row getRow() 
	{
		return row;
	}
	
	/**
	 * @param <T>
	 * @param id
	 * @return The widget with the given id contained in this detail panel.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Widget> T getWidget(String id)
	{
		return (T) widgetsByOriginalId.get(id);
	}

	/**
	 * Adds a widget to this panel 
	 * @param w
	 */
	public void add(Widget w) 
	{
		base.add(w);
	}
	
	/**
	 * Removes all child widgets.
	 */
	public void clear()
	{
		base.clear();
	}
}
