package org.cruxframework.cruxsite.client.feed;

import com.google.gwt.core.client.JavaScriptObject;

public class FeedApi extends JavaScriptObject 
{
	protected FeedApi() {}
	
	public static native FeedApi create(String feedUrl)/*-{
	   var feed = new $wnd.google_feed.feeds.Feed(feedUrl);
	   feed.setResultFormat($wnd.google_feed.feeds.Feed.JSON_FORMAT);
	   return feed;
    }-*/;
  
    public final native void load(FeedCallback callback)/*-{
  	   this.load(function(result){
  	      if (result.error){
  	         callback.@org.cruxframework.cruxsite.client.feed.FeedCallback::onError(Lorg/cruxframework/cruxsite/client/feed/Error;)(result.error);
  	      }else{
  	         callback.@org.cruxframework.cruxsite.client.feed.FeedCallback::onLoad(Lorg/cruxframework/cruxsite/client/feed/Feed;)(result.feed);
  	      }
  	   });
    }-*/;
  
    public final native void setNumEntries(int entries)/*-{
       this.setNumEntries(entries);
    }-*/;
  
    public final native void includeHistoricalEntries()/*-{
	   this.includeHistoricalEntries();
    }-*/; 
    
    public final native void findFeeds(String query, SearchFeedCallback callback)/*-{
  	   $wnd.google_feed.feeds.findFeeds(query, function(result){
  	      if (result.error){
  	         callback.@org.cruxframework.cruxsite.client.feed.SearchFeedCallback::onError(Lorg/cruxframework/cruxsite/client/feed/Error;)(result.error);
  	      }else{
  	         callback.@org.cruxframework.cruxsite.client.feed.SearchFeedCallback::onSearchComplete(Lcom/google/gwt/core/client/JsArray;)(result.entries);
  	      }
  	   });
    }-*/;
}
