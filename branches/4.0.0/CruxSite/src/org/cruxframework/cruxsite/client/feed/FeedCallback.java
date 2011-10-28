package org.cruxframework.cruxsite.client.feed;


public interface FeedCallback
{
  void onLoad(Feed feed);
  void onError(Error error);
}
