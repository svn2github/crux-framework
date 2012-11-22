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

import org.cruxframework.cruxsite.client.feed.Entry;
import org.cruxframework.cruxsite.client.feed.Feed;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class RssPanel extends Composite
{
	private VerticalPanel rssPanel;
	private HTML title;
	private Grid body;
	private DateTimeFormat dateTimeFormat;
	private int maxTitleSize;
	
	public RssPanel()
    {
	    this(100);
    }
	
	/**
	 * 
	 */
	public RssPanel(int maxTitleSize)
    {
		setMaxTitleSize(maxTitleSize);
		dateTimeFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM);

		rssPanel = new VerticalPanel();
		title = new HTML();
		title.setStyleName("rssPanelTitle");

		rssPanel.add(title);
		
		body = new Grid();
		body.resizeColumns(2);
		body.setStyleName("rssPanelBody");
		body.setCellSpacing(0);
		
		rssPanel.add(body);
		initWidget(rssPanel);
		
		setStyleName("rssPanel");
    }
	
	/**
	 * 
	 */
	public void clear()
	{
		body.clear();
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMaxTitleSize()
    {
    	return maxTitleSize;
    }

	/**
	 * 
	 * @param maxTitleSize
	 */
	public void setMaxTitleSize(int maxTitleSize)
    {
    	this.maxTitleSize = maxTitleSize;
    }

	/**
	 * 
	 * @param entry
	 */
	public void addEntry(final Entry entry)
	{
		int row = body.insertRow(body.getRowCount());
		HTML entryTitle = new HTML(getClipedTitle(entry.getTitle()));
		entryTitle.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				Window.open(entry.getLink(), "", null);
			}
		});
		entryTitle.setStyleName("entryTitle");
		entryTitle.setTitle(entry.getTitle());
		
		Label entryDate = new Label(dateTimeFormat.format(entry.getPublishedDate()));
		entryDate.setStyleName("entryDate");
		
		body.setWidget(row, 0, entryTitle);
		body.setWidget(row, 1, entryDate);
	}
	
	/**
	 * 
	 * @param feed
	 */
	public void setFeed(Feed feed)
	{
		clear();
		if (feed != null)
		{
			JsArray<Entry> entries = feed.getEntries();
			if (entries != null)
			{
				for (int i=0; i< entries.length(); i++)
				{
					addEntry(entries.get(i));
				}
			}
		}
	}
	
	/**
	 * 
	 */
	public String getTitle()
	{
		return title.getHTML();
	}
	
	/**
	 * 
	 */
	public void setTitle(String html)
	{
		title.setHTML(html);
	}
	
	/**
	 * 
	 * @param title
	 * @return
	 */
	private String getClipedTitle(String title)
	{
		if (title==null)
		{
			return null;
		}
		if (title.length() > maxTitleSize)
		{
			title = title.substring(0, maxTitleSize-4) + "...";
		}
		
		return title;
	}}
