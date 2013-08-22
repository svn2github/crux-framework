/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.progressbar;

import com.google.gwt.dom.client.Style.Clear;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ProgressBar extends Composite
{
	private SimplePanel progressBar;
	private Label percentBar;
	private boolean loading = false;
	private boolean hideOnComplete = false;
	
	public ProgressBar()
    {
        progressBar = new SimplePanel();
        progressBar.getElement().getStyle().setClear(Clear.BOTH);
//        -moz-transition: opacity 1s linear;
//        -o-transition: opacity 1s linear;
//        -webkit-transition: opacity 1s linear;
//        margin: 10px 0;
//        padding: 3px;
//        border: 1px solid #000;
//        font-size: 14px;

        percentBar = new Label("0%");
        percentBar.getElement().getStyle().setProperty("height", "auto");
        percentBar.getElement().getStyle().setWidth(0, Unit.PX);
//        background-color: #99ccff;
        percentBar.setStyleName("percentBar");

        progressBar.add(percentBar);
        initWidget(progressBar);
        setStyleName("crux-ProgressBar");
    }
	
	public void update(int percent)
	{
		if (percent >= 0 && percent <= 100)
		{
			if (!loading)
			{
				loading = true;
				if (hideOnComplete)
				{
					progressBar.getElement().getStyle().setOpacity(1);
				}
			}
			percentBar.getElement().getStyle().setWidth(percent, Unit.PCT);
			percentBar.setText(percent + "%");				
		}
	}
	
	public void conclude()
	{
		update(100);
		loading = false;
		if (hideOnComplete)
		{
			new Timer()
			{
				@Override
				public void run()
				{
					progressBar.getElement().getStyle().setOpacity(0);
				}
			}.schedule(1500);
		}
	}

	public boolean isHideOnComplete()
    {
    	return hideOnComplete;
    }

	public void setHideOnComplete(boolean hideOnComplete)
    {
    	this.hideOnComplete = hideOnComplete;
        if (hideOnComplete && !loading)
        {
        	progressBar.getElement().getStyle().setOpacity(0);
        }
    	
    }
	
	public void setError(boolean error)
	{
		if (error)
		{
			progressBar.addStyleDependentName("error");
		}
		else
		{
			progressBar.removeStyleDependentName("error");
		}
	}
}
