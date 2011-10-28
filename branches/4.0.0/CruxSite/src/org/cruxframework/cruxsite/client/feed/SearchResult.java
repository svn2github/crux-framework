package org.cruxframework.cruxsite.client.feed;

import com.google.gwt.core.client.JavaScriptObject;

public class SearchResult extends JavaScriptObject
{
	protected SearchResult() {}

	public final native String getTitle()/*-{
		return this.title;
	}-*/;		

	public final native String getLink()/*-{
		return this.link;
	}-*/;		

	public final native String getContentSnippet()/*-{
		return this.contentSnippet;
	}-*/;		

	public final native String getUrl()/*-{
		return this.url;
	}-*/;		
}
