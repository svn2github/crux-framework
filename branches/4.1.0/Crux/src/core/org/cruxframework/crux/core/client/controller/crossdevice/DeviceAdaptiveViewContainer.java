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
package org.cruxframework.crux.core.client.controller.crossdevice;

import org.cruxframework.crux.core.client.screen.views.SingleViewContainer;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DeviceAdaptiveViewContainer extends SingleViewContainer implements IsWidget
{
	private SimplePanel containerPanel;

	public DeviceAdaptiveViewContainer()
    {
		containerPanel = new SimplePanel();
		containerPanel.addAttachHandler(new Handler()
		{
			@Override
			public void onAttachOrDetach(AttachEvent event)
			{
				if (event.isAttached())
				{
					bindToDOM();
				}
				else
				{
					unbindToDOM();
				}
			}
		});
    }
	
	@Override
    protected Panel getContainerPanel()
    {
	    return containerPanel;
    }

	@Override
    protected void handleViewTitle(String title, Panel containerPanel)
    {
    }

	@Override
    public Widget asWidget()
    {
	    return getContainerPanel();
    }
}
