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
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
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
	
	/**
	 * 
	 */
	public RssPanel()
    {
		dateTimeFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);

		rssPanel = new VerticalPanel();
		title = new HTML();
		rssPanel.add(title);
		
		body = new Grid();
		body.resizeColumns(2);
		
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
	 * @param entry
	 */
	public void addEntry(Entry entry)
	{
		int row = body.insertRow(body.getRowCount());
		body.setWidget(row, 0, new HTML(entry.getTitle()));
		body.setWidget(row, 1, new Label(dateTimeFormat.format(entry.getPublishedDate())));
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
	 * @param html
	 */
	public void setTitle(SafeHtml html)
	{
		title.setHTML(html);
	}
}
