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
package br.com.sysmap.crux.basic.client;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 * @author Gess� S. F. Daf� <code>gessedafe@gmail.com</code>
 */
@DeclarativeFactory(id="dialogBox", library="bas")
public class DialogBoxFactory extends DecoratedPopupPanelFactory 
{
	@Override
	protected DialogBox instantiateWidget(Element element, String widgetId) 
	{
		String autoHideStr = element.getAttribute("_autoHide");
		boolean autoHide = false;
		if (autoHideStr != null && autoHideStr.length() >0)
		{
			autoHide = Boolean.parseBoolean(autoHideStr);
		}
		String modalStr = element.getAttribute("_modal");
		boolean modal = false;
		if (modalStr != null && modalStr.length() >0)
		{
			modal = Boolean.parseBoolean(modalStr);
		}

		return new DialogBox(autoHide, modal);
	}
	
	@Override
	protected void processAttributes(SimplePanel widget, Element element, String widgetId) throws InterfaceConfigException
	{
		super.processAttributes(widget, element, widgetId);

		String innerHtml = element.getInnerHTML();
		String text = element.getAttribute("_text");
		if ((text == null || text.length() ==0) && innerHtml != null && innerHtml.length() > 0)
		{
			((HasHTML)widget).setHTML(innerHtml);
		}
	}
}
