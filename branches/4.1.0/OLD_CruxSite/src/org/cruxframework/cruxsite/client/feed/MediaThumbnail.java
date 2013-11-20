package org.cruxframework.cruxsite.client.feed;

import com.google.gwt.core.client.JavaScriptObject;

public class MediaThumbnail extends JavaScriptObject
{
	protected MediaThumbnail() {}

	/**
	 * Retrieve the url of the thumbnail. It is a required attribute.
	 * @return
	 */
	public final native String getUrl()/*-{
		return this.url;
	}-*/;
	
	/**
	 * Retrieve is the height of the thumbnail. It is an optional attribute.
	 * @return
	 */
	public final native int getHeight()/*-{
		return (this.height?this.height:0);
	}-*/;

	/**
	 * Retrieve the width of the thumbnail. It is an optional attribute.
	 * @return
	 */
	public final native int getWidth()/*-{
		return (this.width?this.width:0);
	}-*/;
	
}
