package org.cruxframework.cruxsite.client.feed;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class Feed extends JavaScriptObject
{
	protected Feed() {}
	
	public final native String getFeedUrl()/*-{
		return this.feedUrl;
	}-*/;	

	public final native String getTitle()/*-{
		return this.title;
	}-*/;	

	public final native String getLink()/*-{
		return this.link;
	}-*/;	
	
	public final native String getDescription()/*-{
		return this.description;
	}-*/;	
	
	public final native String getAuthor()/*-{
		return this.author;
	}-*/;	

	public final native JsArray<Entry> getEntries()/*-{
		return this.entries;
	}-*/;	
}
