/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.gwt.client;

import org.cruxframework.crux.core.client.screen.HTMLContainer;
import org.cruxframework.crux.core.client.screen.ViewFactoryUtils;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CruxHTMLPanel extends HTMLPanel implements HTMLContainer
{
	/**
	 * Constructor
	 * @param element
	 */
	public CruxHTMLPanel(String wrapperElementId)
	{
		super("");
		Element panelElement = ViewFactoryUtils.getEnclosingPanelElement(wrapperElementId);
		assert Document.get().getBody().isOrHasChild(panelElement);
		panelElement.removeFromParent();
		getElement().appendChild(panelElement);
	}

	@Override
	public void onAttach()
	{
		super.onAttach();
	}
}
