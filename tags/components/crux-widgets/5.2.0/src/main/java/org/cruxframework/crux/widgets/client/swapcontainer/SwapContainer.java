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
package org.cruxframework.crux.widgets.client.swapcontainer;

import org.cruxframework.crux.core.client.screen.views.SingleViewContainer;
import org.cruxframework.crux.core.client.screen.views.View;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
//TODO remover isso... criar um outro swap com outros tipod de animacao
public class SwapContainer extends SingleViewContainer
{
	public static final String DEFAULT_STYLE_NAME = "crux-SwapContainer";
	private SimplePanel containerPanel;
	private View innerView;

	public SwapContainer()
	{
		super(new SimplePanel(), true);
		containerPanel = getMainWidget();
		containerPanel.setStyleName(DEFAULT_STYLE_NAME);
	}

	public View getView()
	{
		return innerView;
	}
	
	@Override
    protected Panel getContainerPanel(View view)
    {
	    return containerPanel;
    }

	@Override
	protected void handleViewTitle(String title, Panel containerPanel, String viewId)
	{
		// Do nothing
	}
}
