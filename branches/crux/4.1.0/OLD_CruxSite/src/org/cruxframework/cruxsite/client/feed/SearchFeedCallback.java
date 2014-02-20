package org.cruxframework.cruxsite.client.feed;

import com.google.gwt.core.client.JsArray;


public interface SearchFeedCallback
{
  void onSearchComplete(JsArray<SearchResult> entries);
  void onError(Error error);
}
