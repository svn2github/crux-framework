package org.cruxframework.cruxsite.client.feed;

public class Resources
{
	public static String bootstrap()
	{
		return "<html>\n<head>\n"+
			   "<script type=\"text/javascript\" src=\"http://www.google.com/jsapiKEY\"></script>\n"+
			   "<script type=\"text/javascript\">\n"+
			   "try {"+
			   "google.load('feeds', '1', {'nocss' : true});"+
			   "window.parent.AjaxFeedLoad(google);"+
			   "} catch (ex) {"+
			   "window.parent.AjaxFeedError(ex);"+
			   "}\n"+
			   "</script>\n"+
			   "</head>\n<body>\n</body>\n</html>\n";
	}
}
