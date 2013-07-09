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

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DialogViewContainer extends SingleViewContainer 
{
	public static final String DEFAULT_STYLE_NAME = "crux-DialogViewContainer";
	private DialogBox dialog;
	private FlowPanel contentPanel; 
	private View innerView;
	private boolean unloadViewOnClose;
	
	public static DialogViewContainer openDialog(String viewName)
	{
		return createDialog(viewName, viewName, true);
	}
	
	public static DialogViewContainer openDialog(String viewName, String viewId, boolean closeable)
	{
		return createDialog(viewName, viewId, closeable);
	}

	public static DialogViewContainer createDialog(String viewName, String viewId, boolean closeable)
	{
		return createDialog(viewName, viewName, true, true, null, null, -1, -1);
	}
	
	public static DialogViewContainer createDialog(String viewName, String viewId, boolean closeable, boolean modal, String width, String height, int left, int top)
	{
		return createDialog(viewName, viewId, closeable, modal, width, height, left, top, null);
	}
	public static DialogViewContainer createDialog(String viewName, String viewId, boolean closeable, boolean modal, String width, String height, int left, int top, Object parameter)
	{
		DialogViewContainer container = new DialogViewContainer(closeable, modal);
		container.showView(viewName, viewId, parameter);
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
		super(null, true);
		dialog = new DialogBox(false, modal);
		dialog.setStyleName(DEFAULT_STYLE_NAME);
		
		contentPanel = new FlowPanel();
		contentPanel.setWidth("100%");
		
		FlowPanel bodyPanel = new FlowPanel();
		bodyPanel.setWidth("100%");
		bodyPanel.setHeight("100%");
		
		if (closeable)
		{
			final Button closeBtn = new Button(" ");
			closeBtn.setStyleName("closeButton");
			closeBtn.getElement().getStyle().setFloat(Float.RIGHT);
			
			closeBtn.addClickHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					closeDialog();
				}
			});
			FlowPanel headerPanel = new FlowPanel();
			headerPanel.setWidth("100%");
			headerPanel.add(closeBtn);
						
			bodyPanel.add(headerPanel);
		}
		bodyPanel.add(contentPanel);
		dialog.add(bodyPanel);

		initWidget(new Label());
	}

	public boolean isUnloadViewOnClose()
    {
    	return unloadViewOnClose;
    }

	public void setUnloadViewOnClose(boolean unloadViewOnClose)
    {
    	this.unloadViewOnClose = unloadViewOnClose;
    }

	public void setModal(boolean modal)
	{
		dialog.setModal(modal);
	}
	
	public boolean isModal()
	{
		return dialog.isModal();
	}
	
	public void setSize(String width, String height)
	{
		dialog.setSize(width, height);
	}

	@Override
	public void setWidth(String width)
	{
		dialog.setWidth(width);
	}

	@Override
	public void setHeight(String height)
	{
		dialog.setHeight(height);
	}
	
	public void setPosition(int left, int top)
	{
		dialog.setPopupPosition(left, top);
	}
	
	
	@Override
	public void setStyleName(String style)
	{
		dialog.setStyleName(style);
	}

	@Override
	public void setStyleDependentName(String styleSuffix, boolean add)
	{
		dialog.setStyleDependentName(styleSuffix, add);
	}
	
	@Override
	public void setStyleName(String style, boolean add)
	{
		dialog.setStyleName(style, add);
	}

	@Override
	public void setStylePrimaryName(String style)
	{
		dialog.setStylePrimaryName(style);
	}

	public void center()
	{
		dialog.center();
	}
	
	public View getView()
	{
		return innerView;
	}
	
	public void openDialog()
	{
		assert(innerView != null):"There is no View loaded into this container.";
		bindToDOM();
		dialog.show();
	}
	
	public void closeDialog()
	{
		closeDialog(unloadViewOnClose);
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
		dialog.hide();
		unbindToDOM();
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
	    return contentPanel;
    }
	
	@Override
	protected void handleViewTitle(String title, Panel containerPanel, String viewId)
	{
		this.dialog.setText(title);
	}
}
