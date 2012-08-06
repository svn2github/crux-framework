package org.cruxframework.crux.crossdevice.client.promobanner;

import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;
import org.cruxframework.crux.crossdevice.client.slidingdeck.SlidingDeckPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author    Daniel Martins - <code>daniel@cruxframework.org</code>
 *
 */
public abstract class BasePromoBannerController extends DeviceAdaptiveController implements PromoBanner
{
	protected VerticalPanel promoBanner;
	protected FocusPanel focusPanel;
	protected FlowPanel banners;
	protected HorizontalPanel bullets;

	protected SlidingDeckPanel slidingDeckPanel = new SlidingDeckPanel();

	private int autoTransitionInterval = 5000;

	private AutoTransiteTimer autoTransiteTimer = new AutoTransiteTimer(this);

	@Override
	protected void init()
	{
		promoBanner = getChildWidget("promoBanner");
		focusPanel = getChildWidget("focusPanel");
		banners = getChildWidget("banners");
		bullets = getChildWidget("bullets");

		banners.add(slidingDeckPanel);

		slidingDeckPanel.setHeight("100%");
		slidingDeckPanel.setWidth("100%");

		autoTransiteTimer.reschedule();

		initWidgetDefaultStyleName();
	}


	@Override
	protected void initWidgetDefaultStyleName()
	{
		setStyleName("xdev-PromoBanner");
	}

	@Override
	public void setStyleName(String style)
	{
		this.promoBanner.setStyleName(style);
	}

	@Override
	public void setBannersHeight(String height)
	{
		banners.setHeight(height);
		slidingDeckPanel.setHeight(height);
	}

	@Override
	public String getBannersHeight()
	{
		return banners.getElement().getStyle().getHeight();
	}


	protected void showBanner(int i, boolean slideToRight)
	{
		autoTransiteTimer.reschedule();

		if(i > slidingDeckPanel.getWidgetCount() - 1)
		{
			i = 0;
		}

		if(i < 0)
		{
			i = slidingDeckPanel.getWidgetCount() - 1;
		}

		slidingDeckPanel.showWidget(i, slideToRight);
		switchActiveBullet(i);
	}


	protected void showBanner(int i)
	{
		autoTransiteTimer.reschedule();

		if(i > slidingDeckPanel.getWidgetCount() - 1)
		{
			i = 0;
		}

		if(i < 0)
		{
			i = slidingDeckPanel.getWidgetCount() - 1;
		}

		slidingDeckPanel.showWidget(i);
		switchActiveBullet(i);
	}


	private void switchActiveBullet(int i)
	{
		for(int b = 0; b < bullets.getWidgetCount(); b++)
		{
			Widget bullet = bullets.getWidget(b);
			if(b == i)
			{
				bullet.addStyleDependentName("active");
			}
			else
			{
				bullet.removeStyleDependentName("active");
			}
		}
	}

	public void addDefaultBanner(String imageURL, String title, String text, String buttonLabel, ClickHandler onclick)
	{
		addBanner(imageURL, title, text, null, buttonLabel, onclick);
	}

	@Override
	public void addDefaultBanner(String imageURL, String title, String text, String styleName, String buttonLabel, ClickHandler onclick)
	{
		addBanner(imageURL, title, text, styleName, buttonLabel, onclick);
	}

	protected void addBanner(String imageURL, String title, String text,  String styleName, String buttonLabel, ClickHandler onclick)
	{
		SimplePanel panel = new SimplePanel();

		if(styleName != null)
		{
			panel.setStyleName(styleName);
		}

		panel.getElement().getStyle().setBackgroundImage("url(" + imageURL + ")");
		panel.setHeight("100%");
		panel.setWidth("100%");

		VerticalPanel messagePanel = new VerticalPanel();
		messagePanel.setStyleName("messagePanel");

		Label titleLbl = new Label(title);
		titleLbl.setStyleName("title");
		messagePanel.add(titleLbl);

		Label textLbl = new Label(text);
		textLbl.setStyleName("text");
		messagePanel.add(textLbl);

		if(onclick != null)
		{
			Button btn = new Button();
			btn.setStyleName("button");
			btn.setText(buttonLabel);
			btn.addClickHandler(onclick);
			messagePanel.add(btn);
		}

		panel.add(messagePanel);
		slidingDeckPanel.add(panel);

		Label bullet = new Label();
		final int targetIndex = slidingDeckPanel.getWidgetCount() - 1;
		bullet.addClickHandler(
			new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					showBanner(targetIndex);
				}
			}
		);
		bullet.setStyleName("bullet");
		bullets.add(bullet);

		if(slidingDeckPanel.getVisibleWidget() < 1)
		{
			showBanner(0);
		}
	}

	@Override
	public void setTransitionDuration(int transitionDuration)
	{
		this.slidingDeckPanel.setTransitionDuration(transitionDuration);
	}

	@Override
	public int getTransitionDuration()
	{
		return slidingDeckPanel.getTransitionDuration();
	}

	@Override
	public void setAutoTransitionInterval(int autoTransitionInterval)
	{
		this.autoTransitionInterval = autoTransitionInterval;
		autoTransiteTimer.reschedule();
	}

	@Override
	public int getAutoTransitionInterval()
	{
		return autoTransitionInterval;
	}

	private static class AutoTransiteTimer extends Timer
	{
		private BasePromoBannerController promoBanner;

		public AutoTransiteTimer(BasePromoBannerController promoBanner)
		{
			this.promoBanner = promoBanner;
		}

		@Override
		public void run()
		{
			promoBanner.showBanner(promoBanner.slidingDeckPanel.getVisibleWidget() + 1, true);
		}

		public void reschedule()
		{
			this.cancel();
			this.scheduleRepeating(promoBanner.autoTransitionInterval);
		}
	};
}
