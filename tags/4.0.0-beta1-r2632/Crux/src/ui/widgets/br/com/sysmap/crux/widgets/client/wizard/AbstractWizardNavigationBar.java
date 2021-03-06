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
package br.com.sysmap.crux.widgets.client.wizard;

import java.io.Serializable;

import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;
import br.com.sysmap.crux.widgets.client.rollingpanel.RollingPanel;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
public abstract class AbstractWizardNavigationBar<T extends Serializable> extends Composite implements WizardStepListener<T>
{
	protected Wizard<T> wizard;
	protected RollingPanel rollingPanel;

	/**
	 * @param vertical
	 */
	public AbstractWizardNavigationBar(boolean vertical, String styleName)
	{
		this.rollingPanel = new RollingPanel(vertical);
		this.rollingPanel.setStyleName(styleName);
		initWidget(this.rollingPanel);
    }
	
	/**
	 * @return
	 */
	public String getNextButtonStyleName()
    {
    	return this.rollingPanel.getNextButtonStyleName();
    }

	/**
	 * @return
	 */
	public String getPreviousButtonStyleName()
    {
    	return this.rollingPanel.getPreviousButtonStyleName();
    }

	/**
	 * @return
	 */
	public int getSpacing()
	{
		return this.rollingPanel.getSpacing();
	}

	/**
	 * @return
	 */
	public Wizard<T> getWizard()
    {
    	return wizard;
    }

	/**
	 * @return
	 */
	public boolean isVertical()
	{
		return this.rollingPanel.isVertical();
	}

	/**
	 * @param nextButtonStyleName
	 */
	public void setNextButtonStyleName(String nextButtonStyleName)
    {
		this.rollingPanel.setNextButtonStyleName(nextButtonStyleName);
    }

	/**
	 * @param previousButtonStyleName
	 */
	public void setPreviousButtonStyleName(String previousButtonStyleName)
    {
		this.rollingPanel.setPreviousButtonStyleName(previousButtonStyleName);
    }
	
	/**
	 * @param spacing
	 */
	public void setSpacing(int spacing)
	{
		this.rollingPanel.setSpacing(spacing);
	}
	
	/**
	 * 
	 */
	protected void checkWizard()
	{
		if (this.wizard == null)
		{
			throw new NullPointerException(WidgetMsgFactory.getMessages().wizardControlBarOrphan());
		}
	}

	/**
	 * @param align
	 */
	protected void setCellHorizontalAlignment(HorizontalAlignmentConstant align)
    {
		this.rollingPanel.setHorizontalAlignment(align);
    }

	/**
	 * @param verticalAlign
	 */
	protected void setCellVerticalAlignment(VerticalAlignmentConstant verticalAlign)
    {
		this.rollingPanel.setVerticalAlignment(verticalAlign);
    }
	
	/**
	 * @param wizard
	 */
	protected void setWizard(Wizard<T> wizard)
    {
    	this.wizard = wizard;
    }
	
	/**
	 * @param originalScrollPosition
	 */
	protected void updateScrollPosition(int originalScrollPosition)
    {
	    if (rollingPanel.isVertical())
		{
			rollingPanel.setVerticalScrollPosition(originalScrollPosition);
		}
		else
		{
			rollingPanel.setHorizontalScrollPosition(originalScrollPosition);
		}
    }
}
