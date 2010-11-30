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
package br.com.sysmap.crux.core.client.screen;

import com.google.gwt.user.client.ui.Widget;

/**
 * A Class that implements this interface does not create its children. It delegates the children 
 * creation to the {@code ScreenFactory} parser.  
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface HasWidgetsFactory <T extends Widget, C extends WidgetFactoryContext>
{
	/**
	 * @param parent
	 * @param parentId
	 * @param widget
	 * @param widgetId
	 */
	void add(T parent, String parentId, Widget widget, String widgetId);

}
