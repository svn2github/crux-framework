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
package org.cruxframework.crux.widgets.client.dialogcontainer;

import org.cruxframework.crux.core.client.screen.views.SingleViewContainer;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.widgets.client.dialog.CustomDialogBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DialogViewContainer extends SingleViewContainer 
{
	public static final String DEFAULT_STYLE_NAME = "crux-DialogViewContainer";
	private CustomDialogBox containerPanel;
	private View innerView;
	
	public static DialogViewContainer openDialog(String viewName)
	{
		return openDialog(viewName, viewName, true);
	}
	
	public static DialogViewContainer openDialog(String viewName, String viewId, boolean closeable)
	{
		return openDialog(viewName, viewName, true, true, null, null, -1, -1);
	}
	
	public static DialogViewContainer openDialog(String viewName, String viewId, boolean closeable, boolean modal, String width, String height, int left, int top)
	{
		DialogViewContainer container = new DialogViewContainer(closeable, modal);
		container.loadView(viewName, viewId, true);
		if (!StringUtils.isEmpty(width) && !StringUtils.isEmpty(height))
		{
			container.setSize(width, height);
		}
		if (left >= 0 && top >= 0)
		{
			container.setPosition(left, top);
		}
		return container;
	}
	
	public DialogViewContainer()
	{
		this(true, true);
	}
	
	public DialogViewContainer(boolean closeable, boolean modal)
	{
		super(true);
		containerPanel = new CustomDialogBox(false, true, modal);
		containerPanel.setStyleName(DEFAULT_STYLE_NAME);
		
		if (closeable)
		{
			final Button focusPanel = new Button(" ");
			focusPanel.setStyleName("closeButton");
			
			focusPanel.addClickHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					closeDialog();
				}
			});
						
			containerPanel.setTopRightWidget(focusPanel);
		}
		
		initWidget(new Label());
	}

	public void setModal(boolean modal)
	{
		containerPanel.setModal(modal);
	}
	
	public boolean isModal()
	{
		return containerPanel.isModal();
	}
	
	public void setSize(String width, String height)
	{
		containerPanel.setSize(width, height);
	}

	@Override
	public void setWidth(String width)
	{
		containerPanel.setWidth(width);
	}

	@Override
	public void setHeight(String height)
	{
		containerPanel.setHeight(height);
	}
	
	public void setPosition(int left, int top)
	{
		containerPanel.setPopupPosition(left, top);
	}
	
	
	@Override
	public void setStyleName(String style)
	{
		containerPanel.setStyleName(style);
	}

	@Override
	public void setStyleDependentName(String styleSuffix, boolean add)
	{
		containerPanel.setStyleDependentName(styleSuffix, add);
	}
	
	@Override
	public void setStyleName(String style, boolean add)
	{
		containerPanel.setStyleName(style, add);
	}

	@Override
	public void setStylePrimaryName(String style)
	{
		containerPanel.setStylePrimaryName(style);
	}

	public void center()
	{
		containerPanel.center();
	}
	
	public View getView()
	{
		return innerView;
	}
	
	public void openDialog()
	{
		assert(innerView != null):"There is no View loaded into this container.";
		containerPanel.show();
	}
	
	public void closeDialog()
	{
		closeDialog(false);
	}
	
	public boolean closeDialog(boolean unloadView)
	{
		if (unloadView)
		{
			if (!remove(innerView))
			{
				return false;
			}
		}
		containerPanel.hide();
		return true;
	}
	
	@Override
	protected boolean doAdd(View view, boolean lazy)
	{
	    assert(views.isEmpty()):"DialogViewContainer can not contain more then one view";
	    innerView = view;
	    boolean added = super.doAdd(view, lazy);
	    if (!added)
	    {//During view creation, a widget can make a reference to Screen static methods... So, it is better to 
	     // set rootView reference before widgets creation...	
	    	innerView = null;
	    }
		return added;
	}
	
	@Override
	protected boolean doRemove(View view, boolean skipEvents)
	{
	    boolean removed = super.doRemove(view, skipEvents);
	    if (removed)
	    {
	    	innerView = null;
	    }
		return removed;
	}
	
	@Override
    protected Panel getContainerPanel(View view)
    {
	    return getContainerPanel();
    }

    protected Panel getContainerPanel()
    {
	    return containerPanel;
    }
	
	@Override
	protected void handleViewTitle(String title, Panel containerPanel, String viewId)
	{
		this.containerPanel.setText(title);
	}
}
