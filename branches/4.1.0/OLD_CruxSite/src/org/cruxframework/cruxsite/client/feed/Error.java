package org.cruxframework.cruxsite.client.feed;

import com.google.gwt.core.client.JavaScriptObject;

public class Error extends JavaScriptObject
{
	protected Error() {}
	
	public final native String getMessage()/*-{
		return this.message;
	}-*/;

	public final native String getCode()/*-{
	return this.code;
}-*/;
}
