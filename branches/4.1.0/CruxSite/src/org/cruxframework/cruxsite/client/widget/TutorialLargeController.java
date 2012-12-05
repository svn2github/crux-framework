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
package org.cruxframework.cruxsite.client.widget;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Controller("tutorialLargeController")
public class TutorialLargeController extends DeviceAdaptiveController implements Tutorial
{
	private String tutorialUrl;
	private Image tutorialImage;
//	private HorizontalPanel controlsPanel;
	private Label time;
	private Label learnDescription;
	private String estimatedTime;
	private HTML title;
	private HTML subtitle;

	@Expose
	public void openTutorialClick()
	{
		Window.open(tutorialUrl, "tutorial", null);
	}
	
	@Override
    public String getEstimatedTime()
    {
	    return estimatedTime;
    }

	@Override
    public void setEstimatedTime(String time)
    {
		estimatedTime = time;
		this.time.setText("Aproch. Time: "+time);
    }

	@Override
	public String getLearnDescription()
	{
		return learnDescription.getText();
	}
	
	@Override
	public void setLearnDescription(String description)
	{
		learnDescription.setText(description);
	}
	
	@Override
	protected void init()
	{
		tutorialImage = getChildWidget("tutorialImage");
//		controlsPanel = getChildWidget("controlsPanel");
		time = getChildWidget("time");
		learnDescription = getChildWidget("learnDescription");
		title = getChildWidget("title");
		subtitle = getChildWidget("subtitle");;
	}

	@Override
	protected void initWidgetDefaultStyleName()
	{
		setStyleName("site-Tutorial");
	}

	@Override
    public void setLargeImage(String url)
    {
	    tutorialImage.setUrl(url);
    }	    

	@Override
    public void setSmallImage(String url)
    {
    }

	@Override
    public void setTutorialSubtitle(String subtitle)
    {
	   this.subtitle.setHTML("<p>"+subtitle+"</p>");
    }

	@Override
    public void setTutorialTitle(String title)
    {
		   this.title.setHTML("<h1>"+title+"</h1>");
    }

	@Override
    public void setTutorialUrl(String url)
    {
		tutorialUrl = url;
    }

	@Override
    public String getTutorialUrl()
    {
	    return tutorialUrl;
    }
}
