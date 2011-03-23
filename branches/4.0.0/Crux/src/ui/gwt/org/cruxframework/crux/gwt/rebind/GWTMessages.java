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
package org.cruxframework.crux.gwt.rebind;

import org.cruxframework.crux.core.i18n.DefaultServerMessage;

/**
 * Messages for basic widgets
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface GWTMessages
{
	@DefaultServerMessage("[gwt-widgets - 001] - The DisclosurePanel ''{0}'' must contains a required child element to describe the widget and an optional first element to describe the header.")
	String disclosurePanelInvalidChildrenElements(String widgetId);
	@DefaultServerMessage("[gwt-widgets - 002] - The items of MenuBar ''{0}'' must contains the required attribute _itemType (with one of the values: ''text'', ''html'', ''separator'').")
	String menuBarItemTypeEmpty(String widgetId);
	@DefaultServerMessage("[gwt-widgets - 003] - The items of Tree ''{0}'' must contains the attribute _text or be a valid widget declaration.")
	String treeInvalidTreeItem(String id);
	@DefaultServerMessage("[gwt-widgets - 004] - The items of HorizontalSplitPanel ''{0}'' must contains the attribute _position (with one of the vales: ''left'', ''right''.")
	String horizontalSplitPanelInvalidPosition(String id);
	@DefaultServerMessage("[gwt-widgets - 005] - The items of VerticalSplitPanel ''{0}'' must contains the attribute _position (with one of the vales: ''top'', ''bottom''.")
	String verticalSplitPanelInvalidPosition(String id);
	@DefaultServerMessage("[gwt-widgets - 006] - Error preparing the cells for FlexTable ''{0}'': Row and Col indexes must be greater or equals 0.")
	String flexTableInvalidRowColIndexes(String widgetId);
	@DefaultServerMessage("[gwt-widgets - 007] - Error preparing the cells for Grid ''{0}'': Row and Col indexes must be greater or equals 0.")
	String gridInvalidRowColIndexes(String widgetId);
	@DefaultServerMessage("[gwt-widgets - 008] - Error in ScrollPanel ''{0}''.Error ensuring visibility for component ''{1}''.")
	String scrollPanelWidgetNotFound(String widgetId, String visibleWidget);
	@DefaultServerMessage("[gwt-widgets - 009] - Error adding widget ''{0}'' on parent Stackpanel ''{1}''.")
	String stackPanelIvalidChild(String idChild, String idParent);
	@DefaultServerMessage("[gwt-widgets - 009] - Error adding widget ''{0}'' on parent Dockpanel ''{1}''. Invalid direction.")
	String dockPanelInvalidDirection(String id, String id2);
	@DefaultServerMessage("[gwt-widgets - 010] - The menu item ''{0}'' has no child items and has no command. Ignoring item...")
	String menubarItemWithoutChildrenOrCommand(String itemCaption);
	@DefaultServerMessage("[gwt-widgets - 011] - The attribute size is required for cells not centered in DockLayoutPanel wiht id: ''{0}''.")
	String dockLayoutPanelRequiredSize(String rootWidgetId);
	@DefaultServerMessage("[gwt-widgets - 012] - DataProvider factory method not found: Controller[{0}], Method[{1}].")
	String cellTableDataProviderMethodNotFound(String controller, String method);
	@DefaultServerMessage("[gwt-widgets - 013] - Can not access property [{0}] on row object[{1}].")
	String cellTableCanNotAccessProperty(String property, String rowDataObject);
	@DefaultServerMessage("[gwt-widgets - 013] - Can not sort column for property [{0}] on row object[{1}]. Property must have a primitive or Comparable type.")
	String cellTableCanNotSortColumn(String property, String rowDataObject);

}
