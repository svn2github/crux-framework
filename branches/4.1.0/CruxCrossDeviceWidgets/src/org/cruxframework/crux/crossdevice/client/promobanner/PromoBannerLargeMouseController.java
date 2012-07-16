package org.cruxframework.crux.crossdevice.client.promobanner;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.utils.StyleUtils;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Label;


/**
 *
 * @author    Daniel Martins - <code>daniel@cruxframework.org</code>
 *
 */
@Controller("promoBannerLargeMouseController")
public class PromoBannerLargeMouseController extends BasePromoBannerController
{
	private Label leftArrow = new Label();
	private Label rightArrow = new Label();

	@Override
	protected void init()
	{
		super.init();

		focusPanel.addKeyDownHandler(new KeyDownHandler()
		{
			@Override
			public void onKeyDown(KeyDownEvent event)
			{
				if(event.isLeftArrow())
				{
					showBanner(slidingDeckPanel.getVisibleWidget() - 1, false);
				}
				else if(event.isRightArrow())
				{
					showBanner(slidingDeckPanel.getVisibleWidget() + 1, true);
				}
			}
		});

		leftArrow.addClickHandler( new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				showBanner(slidingDeckPanel.getVisibleWidget() - 1, false);
			}
		});

		rightArrow.addClickHandler( new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				showBanner(slidingDeckPanel.getVisibleWidget() + 1, true);
			}
		});


		banners.add(leftArrow);
		banners.add(rightArrow);

		Scheduler.get().scheduleDeferred(
			new ScheduledCommand()
			{
				public void execute()
				{
					adjustPositions();
				}
			}
		);
	}

	@Override
	protected void applyWidgetDependentStyleNames()
	{
		StyleUtils.addStyleDependentName(getElement(), DeviceAdaptive.Size.large.toString());
	}

	private void adjustPositions()
	{
		int containerHeight = banners.getElement().getClientHeight();

		rightArrow.setStyleName("rightArrow");
		rightArrow.getElement().getStyle().setPosition(Position.ABSOLUTE);
		rightArrow.getElement().getStyle().setRight(0, Unit.PX);
		rightArrow.getElement().getStyle().setTop((containerHeight - rightArrow.getElement().getClientHeight())/2, Unit.PX);

		leftArrow.setStyleName("leftArrow");
		leftArrow.getElement().getStyle().setPosition(Position.ABSOLUTE);
		leftArrow.getElement().getStyle().setLeft(0, Unit.PX);
		leftArrow.getElement().getStyle().setTop((containerHeight - leftArrow.getElement().getClientHeight())/2, Unit.PX);
	}

	@Override
	public void addSmallBanner(String imageURL, String title, String text, String styleName, String buttonLabel, ClickHandler onclick)
	{

	}

	public void addLargeBanner(String imageURL, String title, String text, String buttonLabel, ClickHandler onclick)
	{
		addLargeBanner(imageURL, title, text, null, buttonLabel, onclick);
	}

	@Override
	public void addLargeBanner(String imageURL, String title, String text, String styleName, String buttonLabel, ClickHandler onclick)
	{
		addBanner(imageURL, title, text, styleName, buttonLabel, onclick);
	}
}
