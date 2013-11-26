/**
 *
 */
package org.cruxframework.crux.widgets.client.listshuttle;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * ===========================================================================
 * Copyright (c) 2013 Sysmap Solutions Software e Consultoria Ltda
 * Todos os direitos reservados.
 * ===========================================================================
 *
 * Project:     simco-core
 * File:        ShuttleCell.java
 * Description:
 *
 * @author     Jair Batista - <code>jair.batista@sysmap.com.br</code>
 * @created    25/11/2013
 * @version    1.0
 * ===========================================================================
 */
public class ListShuttle<T> extends Composite {
	private static ShuttleCellWidgetUiBinder uiBinder = GWT.create(ShuttleCellWidgetUiBinder.class);

	private String id;
	private String availableHeader;
	private String selectedHeader;
	private List<T> availableItems;
	private List<T> selectedItems;
	private BeanRenderer<T> beanRenderer;

	@UiField
	protected DivElement containerDiv;

	@UiField
	protected Label availableLabel;

	@UiField
	protected Label selectedLabel;

	@UiField(provided = true)
	protected CellList<T> availableCellList;

	@UiField(provided = true)
	protected CellList<T> selectedCellList;

	@UiField
	protected Anchor addSelectedAnchor;

	@UiField
	protected Anchor addAllAnchor;

	@UiField
	protected Anchor removeSelectedAnchor;

	@UiField
	protected Anchor removeAllAnchor;

	@SuppressWarnings("rawtypes")
	interface ShuttleCellWidgetUiBinder extends UiBinder<Widget, ListShuttle> {
	}

	public ListShuttle() {
		this.availableCellList = new CellList<T>(new BeanCell());

		this.availableCellList.setSelectionModel(new SingleSelectionModel<T>());

		this.selectedCellList = new CellList<T>(new BeanCell());

		this.selectedCellList.setSelectionModel(new SingleSelectionModel<T>());

		setAvailableItems(new ArrayList<T>());
		setSelectedItems(new ArrayList<T>());

		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected void onLoad() {
		super.onLoad();

		this.availableLabel.setText(availableHeader);
		this.selectedLabel.setText(selectedHeader);

		bindHandlers();
	}

	private void bindHandlers() {
		this.addSelectedAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent events) {
				handleAddSelected();
			}
		});

		this.addAllAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				handleAddAll();
			}
		});

		this.removeSelectedAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				handleRemoveSelected();
			}
		});

		this.removeAllAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				handleRemoveAll();
			}
		});
	}

	private void handleAddSelected() {
		@SuppressWarnings("unchecked")
		SingleSelectionModel<T> availableSelectionModel = (SingleSelectionModel<T>) this.availableCellList.getSelectionModel();

		T selectedObject = availableSelectionModel.getSelectedObject();

		if (!getSelectedItems().contains(selectedObject)) {
			getSelectedItems().add(selectedObject);
			updateSelectedList();
		}

		if (getAvailableItems().remove(selectedObject)) {
			updateAvailableList();
		}
	}

	private void handleRemoveSelected() {
		@SuppressWarnings("unchecked")
		SingleSelectionModel<T> removeSelectionModel = (SingleSelectionModel<T>) this.selectedCellList.getSelectionModel();

		T selectedObject = removeSelectionModel.getSelectedObject();

		if (!getAvailableItems().contains(selectedObject)) {
			getAvailableItems().add(selectedObject);
			updateAvailableList();
		}

		if (getSelectedItems().remove(selectedObject)) {
			updateSelectedList();
		}
	}

	private void handleRemoveAll() {
		for (T item : getSelectedItems()) {
			if (!getAvailableItems().contains(item)) {
				getAvailableItems().add(item);
			}
		}

		getSelectedItems().clear();

		updateAvailableList();
		updateSelectedList();
	}

	private void handleAddAll() {
		for (T item : getAvailableItems()) {
			if (!getSelectedItems().contains(item)) {
				getSelectedItems().add(item);
			}
		}

		getAvailableItems().removeAll(getSelectedItems());

		updateAvailableList();
		updateSelectedList();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setContainerDiv(DivElement containerDiv) {
		this.containerDiv = containerDiv;
	}

	public void setAvailableLabel(Label availableLabel) {
		this.availableLabel = availableLabel;
	}

	public void setSelectedLabel(Label selectedLabel) {
		this.selectedLabel = selectedLabel;
	}

	public void setAvailableHeader(String availableHeader) {
		this.availableHeader = availableHeader;
	}

	public void setSelectedHeader(String selectedHeader) {
		this.selectedHeader = selectedHeader;
	}

	public void setBeanRenderer(BeanRenderer<T> beanRenderer) {
		this.beanRenderer = beanRenderer;
	}

	public void setAddSelectedAnchor(Anchor addSelectedAnchor) {
		this.addSelectedAnchor = addSelectedAnchor;
	}

	public void setAvailableItems(List<T> availableItems) {
		this.availableItems = availableItems;

		if (getSelectedItems() != null && !getSelectedItems().isEmpty()) {
			getAvailableItems().remove(getSelectedItems());
		}

		updateAvailableList();
	}

	public List<T> getAvailableItems() {
		if (availableItems == null) {
			availableItems = new ArrayList<T>();
		}
		return availableItems;
	}

	public void setSelectedItems(List<T> selectedItems) {
		this.selectedItems = selectedItems;
		updateSelectedList();

		if (getAvailableItems() != null && !getAvailableItems().isEmpty()) {
			getAvailableItems().removeAll(getSelectedItems());
			updateAvailableList();
		}
	}

	public List<T> getSelectedItems() {
		if (selectedItems == null) {
			selectedItems = new ArrayList<T>();
		}
		return selectedItems;
	}

	private void updateAvailableList() {
		this.availableCellList.setRowCount(getAvailableItems().size());
		this.availableCellList.setRowData(0, getAvailableItems());
	}

	private void updateSelectedList() {
		this.selectedCellList.setRowCount(getSelectedItems().size());
		this.selectedCellList.setRowData(0, getSelectedItems());
	}

	private class BeanCell extends AbstractCell<T> {
		@Override
		public void render(Context context, T data, SafeHtmlBuilder sb) {
			if (data != null) {
				if (beanRenderer == null) {
					setBeanRenderer(new ToStringBeanRenderer<T>());
				}

				String value = beanRenderer.render(data);

				sb.append(SafeHtmlUtils.fromString(value));
			}
		}
	}

	public static interface BeanRenderer<B> {
		public String render(B bean);
	}

	private class ToStringBeanRenderer<B> implements BeanRenderer<B> {
		@Override
		public String render(B bean) {
			return bean == null ? "null" : bean.toString();
		}
	}
}
