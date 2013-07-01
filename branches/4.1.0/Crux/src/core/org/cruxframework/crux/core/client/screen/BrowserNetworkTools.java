package org.cruxframework.crux.core.client.screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Inspect network conditions and give requested feedback to user.
 * 
 * @author samuel.cardoso
 */
public class BrowserNetworkTools
{
	private static NetworkChecker isOnline = null;

	public interface NetworkChecker
	{
		public boolean isOnline();
	}

	/**
	 * Inspect network conditions.
	 * 
	 * @return
	 */
	public static boolean isOnline()
	{
		return getStaticInstance().isOnline();
	}

	private static NetworkChecker getStaticInstance()
	{
		if (isOnline == null)
		{
			isOnline = GWT.create(NetworkChecker.class);
		}
		return isOnline;
	}

	/**
	 * TODO: javadoc!!!
	 * 
	 * @return
	 */
	public static class NetworkCheckerDefault implements NetworkChecker
	{
		private static final int REPEATING_INTERVAL = 5000;
		private static final String TESTER_ID = "_network_tester_";
		private final String URL_IMG = "clear.cache.gif";
		private Boolean isOnline;
		private Panel testerPanel;

		public NetworkCheckerDefault()
		{
			testerPanel = createTesterPanel();
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand()
			{
				public boolean execute()
				{
					updateNetowrkStatus();
					return true;
				}
			}, REPEATING_INTERVAL);
		}

		@Override
		public boolean isOnline()
		{
			if (isOnline == null)
			{
				updateNetowrkStatus();
			}
			return isOnline;
		}

		private void updateNetowrkStatus()
		{
			Image img = new Image(URL_IMG+"?"+System.currentTimeMillis());
			img.getElement().getStyle().setPosition(Position.ABSOLUTE);
			img.getElement().getStyle().setLeft(-1000, Unit.PX);
			img.addLoadHandler(new LoadHandler()
			{
				@Override
				public void onLoad(LoadEvent event)
				{
					isOnline = true;
					((Image)event.getSource()).removeFromParent();
				}
			});

			img.addErrorHandler(new ErrorHandler()
			{
				@Override
				public void onError(ErrorEvent event)
				{
					isOnline = false;
					((Image)event.getSource()).removeFromParent();
				}
			});
			testerPanel.add(img);	
		}

		private Panel createTesterPanel()
        {
	        final Element div = Document.get().createDivElement();
			div.setId(TESTER_ID);
			Document.get().getBody().appendChild(div);
	        return RootPanel.get(TESTER_ID);
        }
	}

	/**
	 * TODO: javadoc!!!
	 * 
	 * @return
	 */
	public static class NetworkCheckerSafari implements NetworkChecker
	{
		@Override
		public native boolean isOnline() /*-{
			return $wnd.navigator.onLine();
		}-*/;
	}
}
