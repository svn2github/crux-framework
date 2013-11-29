package org.cruxframework.crux.widgets.client.listshuttle;

import java.util.List;

import org.cruxframework.crux.widgets.client.listshuttle.ListShuttle.BeanRenderer;

import com.google.gwt.user.client.ui.HasEnabled;

/**
 * @author Jair Elton
 * @author Samuel Almeida Cardoso
 */
public interface IListShuttle<T> extends HasEnabled
{
	public void setAvailableHeader(String availableHeader);
	public void setSelectedHeader(String selectedHeader);
	
	public void setSelectedItems(List<T> selectedItems);
	public List<T> getSelectedItems();
	public void setAvailableItems(List<T> availableItems);
	public List<T> getAvailableItems();
	
	public void setBeanRenderer(BeanRenderer<T> beanRenderer);
	
}
