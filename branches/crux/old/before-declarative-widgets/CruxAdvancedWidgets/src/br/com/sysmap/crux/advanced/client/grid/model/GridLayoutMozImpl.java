package br.com.sysmap.crux.advanced.client.grid.model;

import com.google.gwt.user.client.ui.ScrollPanel;

public class GridLayoutMozImpl extends GridLayout
{
	@Override
	void adjustToBrowser(ScrollPanel scrollingArea, GridHtmlTable table)
	{
		table.getElement().getStyle().setProperty("tableLayout", "fixed");
	}
}
