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
package br.com.sysmap.crux.widgets.client.dynatabs;

import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Modified version of GWT TabPanel that uses a RollingPanel wrapping its TabBar.
 * 
 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
 *
 */
class InternalTabPanel extends Composite implements HasAnimation, HasBeforeSelectionHandlers<Integer>
{
	private TabbedDeckPanel deck = null;
	private InternalTabBar tabBar = null;

	/**
	 * Creates an empty tab panel.
	 */
	public InternalTabPanel()
	{
		tabBar = new InternalTabBar();
		deck = new TabbedDeckPanel(tabBar);

		VerticalPanel panel = new VerticalPanel();
		panel.add(tabBar);
		panel.add(deck);

		panel.setCellHeight(deck, "100%");
		tabBar.setWidth("100%");

		initWidget(panel);

		tabBar.addSelectionHandler(new SelectionHandler<Integer>()
		{
			public void onSelection(SelectionEvent<Integer> event)
			{
				deck.showWidget(event.getSelectedItem());
			}
		});

		setStyleName("gwt-TabPanel");
		deck.setStyleName("gwt-TabPanelBottom");
		// Add a11y role "TabPanel"
		Accessibility.setRole(deck.getElement(), Accessibility.ROLE_TABPANEL);
	}

	/**
	 * Adds a widget to the tab panel. If the Widget is already attached to the
	 * InternalTabPanel, it will be moved to the right-most index.
	 * 
	 * @param w
	 *            the widget to be added
	 * @param tabWidget
	 *            the widget to be shown in the tab
	 */
	public void add(Widget w, Widget tabWidget)
	{
		insert(w, tabWidget, getWidgetCount());
	}

	/**
	 * @see com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers#addBeforeSelectionHandler(com.google.gwt.event.logical.shared.BeforeSelectionHandler)
	 */
	public HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<Integer> handler)
	{
		return addHandler(handler, BeforeSelectionEvent.getType());
	}

	/**
	 * 
	 */
	public void clear()
	{
		while (getWidgetCount() > 0)
		{
			remove(getWidget(0));
		}
	}

	public Widget getWidget(int index)
	{
		return deck.getWidget(index);
	}

	public int getWidgetCount()
	{
		return deck.getWidgetCount();
	}

	public int getWidgetIndex(Widget widget)
	{
		return deck.getWidgetIndex(widget);
	}

	/**
	 * Inserts a widget into the tab panel. If the Widget is already attached to
	 * the InternalTabPanel, it will be moved to the requested index.
	 * 
	 * @param widget
	 *            the widget to be inserted.
	 * @param tabWidget
	 *            the widget to be shown on its tab.
	 * @param beforeIndex
	 *            the index before which it will be inserted.
	 */
	public void insert(Widget widget, Widget tabWidget, int beforeIndex)
	{
		// Delegate updates to the TabBar to our DeckPanel implementation
		deck.insertProtected(widget, tabWidget, beforeIndex);
	}

	public boolean isAnimationEnabled()
	{
		return deck.isAnimationEnabled();
	}

	public boolean remove(int index)
	{
		// Delegate updates to the TabBar to our DeckPanel implementation
		return deck.remove(index);
	}

	/**
	 * Removes the given widget, and its associated tab.
	 * 
	 * @param widget
	 *            the widget to be removed
	 */
	public boolean remove(Widget widget)
	{
		// Delegate updates to the TabBar to our DeckPanel implementation
		return deck.remove(widget);
	}

	/**
	 * Programmatically selects the specified tab.
	 * 
	 * @param index
	 *            the index of the tab to be selected
	 */
	public void selectTab(int index)
	{
		tabBar.selectTab(index);
	}

	public void setAnimationEnabled(boolean enable)
	{
		deck.setAnimationEnabled(enable);
	}

	/**
	 * <b>Affected Elements:</b>
	 * <ul>
	 * <li>-bar = The tab bar.</li>
	 * <li>-bar-tab# = The element containing the content of the tab itself.</li>
	 * <li>-bar-tab-wrapper# = The cell containing the tab at the index.</li>
	 * <li>-bottom = The panel beneath the tab bar.</li>
	 * </ul>
	 * 
	 * @see UIObject#onEnsureDebugId(String)
	 */
	@Override
	protected void onEnsureDebugId(String baseID)
	{
		super.onEnsureDebugId(baseID);
		tabBar.ensureDebugId(baseID + "-bar");
		deck.ensureDebugId(baseID + "-bottom");
	}

	/**
	 * @return
	 */
	int getSelectedTab()
    {
	    return tabBar.getSelectedTab();
    }

	/**
	 * We ensure that the DeckPanel cannot become of of sync with its associated
	 * TabBar by delegating all mutations to the TabBar to this implementation
	 * of DeckPanel.
	 * </p>
	 */
	private static class TabbedDeckPanel extends DeckPanel
	{
		private InternalTabBar tabBar;

		public TabbedDeckPanel(InternalTabBar tabBar)
		{
			this.tabBar = tabBar;
		}

		@Override
		public boolean remove(Widget w)
		{
			// Removal of items from the TabBar is delegated to the DeckPanel
			// to ensure consistency
			int idx = getWidgetIndex(w);
			if (idx != -1)
			{
				tabBar.removeTab(idx);
				return super.remove(w);
			}

			return false;
		}

		protected void insertProtected(Widget w, Widget tabWidget,
		        int beforeIndex)
		{

			// Check to see if the InternalTabPanel already contains the Widget.
			// If so,
			// remove it and see if we need to shift the position to the left.
			int idx = getWidgetIndex(w);
			if (idx != -1)
			{
				remove(w);
				if (idx < beforeIndex)
				{
					beforeIndex--;
				}
			}

			tabBar.insertTab(tabWidget, beforeIndex);
			super.insert(w, beforeIndex);
		}
	}
}
