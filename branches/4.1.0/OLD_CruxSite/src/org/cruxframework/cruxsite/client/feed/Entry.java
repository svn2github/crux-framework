package org.cruxframework.cruxsite.client.feed;

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

public class Entry extends JavaScriptObject
{
	protected Entry() {}

	public final native JsArray<MediaGroup> getMediaGroups()/*-{
		return this.mediaGroups;
	}-*/;		

	public final native String getTitle()/*-{
		return this.title;
	}-*/;		

	public final native String getLink()/*-{
		return this.link;
	}-*/;		

	public final native String getContent()/*-{
		return this.content;
	}-*/;		

	public final native String getContentSnippet()/*-{
		return this.contentSnippet;
	}-*/;		

    @SuppressWarnings("deprecation")
    public final Date getPublishedDate()
	{
		String time = getPublishedDateString();
		return (time != null && time.length()>0?new Date(time):null);
	}
	
	public final native String getPublishedDateString()/*-{
		return this.publishedDate;
	}-*/;		

	public final native JsArrayString getCategories()/*-{
		return this.categories;
	}-*/;		
}
