package org.cruxframework.cruxsite.client.widget;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PromoBanner extends Composite 
{
	private VerticalPanel container = new VerticalPanel();
	private FlowPanel banners = new FlowPanel();
	private SlidingDeckPanel imagesPanel = new SlidingDeckPanel();
	private Label leftArrow = new Label();
	private Label rightArrow = new Label();
	private HorizontalPanel bullets = new HorizontalPanel();
	private int autoTransitionInterval = 5000;
	private AutoTransiteTimer autoTransiteTimer = new AutoTransiteTimer(this); 
	
	public PromoBanner() 
	{
		initWidget(container);
		
		container.setStyleName("crux-PromoBanner");
		container.add(banners);
		container.add(bullets);
		container.setCellHorizontalAlignment(bullets, HasHorizontalAlignment.ALIGN_CENTER);
		
		bullets.setStyleName("bulletsArea");
		
		banners.setStyleName("bannersArea");
		banners.add(imagesPanel);
		banners.getElement().getStyle().setPosition(Position.RELATIVE);
		
		banners.add(rightArrow);
		banners.add(leftArrow);
		
		rightArrow.addClickHandler(
			new ClickHandler() {
				public void onClick(ClickEvent event) 
				{
					showBanner(imagesPanel.getVisibleWidget() + 1, true);
				}
			}
		);
		
		leftArrow.addClickHandler(
			new ClickHandler() {
				public void onClick(ClickEvent event) 
				{
					showBanner(imagesPanel.getVisibleWidget() - 1, false);
				}
			}
		);
		
		imagesPanel.setHeight("100%");
		imagesPanel.setWidth("100%");
		
		Scheduler.get().scheduleDeferred(
			new ScheduledCommand() 
			{
				public void execute() 
				{
					adjustPositions();
				}
			}
		);
		
		autoTransiteTimer.reschedule();
	}
	
	protected void showBanner(int i, boolean slideToRight) 
	{
		autoTransiteTimer.reschedule();
		
		if(i > imagesPanel.getWidgetCount() - 1)
		{
			i = 0;
		}
		
		if(i < 0)
		{
			i = imagesPanel.getWidgetCount() - 1;
		}
		
		imagesPanel.showWidget(i, slideToRight);
		switchActiveBullet(i);
	}

	protected void showBanner(int i) 
	{
		autoTransiteTimer.reschedule();
		
		if(i > imagesPanel.getWidgetCount() - 1)
		{
			i = 0;
		}
		
		if(i < 0)
		{
			i = imagesPanel.getWidgetCount() - 1;
		}
		
		imagesPanel.showWidget(i);
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
	
	
	public void addBanner(String backgroundImageURL, String title, String text, String buttonLabel, ClickHandler onclick)
	{
		addBanner(backgroundImageURL, title, text, null, buttonLabel, onclick);
	}
	
	public void addBanner(String backgroundImageURL, String title, String text,  String styleName, String buttonLabel, ClickHandler onclick)
	{
		SimplePanel panel = new SimplePanel();
		
		if(styleName != null)
		{
			panel.setStyleName(styleName);
		}
		
		panel.getElement().getStyle().setBackgroundImage("url(" + backgroundImageURL + ")");
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
		imagesPanel.add(panel);
		
		Label bullet = new Label();
		final int targetIndex = imagesPanel.getWidgetCount() - 1;
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
		
		if(imagesPanel.getVisibleWidget() < 1)
		{
			showBanner(0);
		}
	}

	public void setBannersHeight(String height) 
	{
		banners.setHeight(height);
	}
	
	public void setTransitionDuration(int transitionDuration) 
	{
		this.imagesPanel.setTransitionDuration(transitionDuration);
	}

	public void setAutoTransitionInterval(int autoTransitionInterval) 
	{
		this.autoTransitionInterval = autoTransitionInterval;
		autoTransiteTimer.reschedule();
	}
	
	private static class AutoTransiteTimer extends Timer 
	{
		private PromoBanner promoBanner;

		public AutoTransiteTimer(PromoBanner promoBanner) 
		{
			this.promoBanner = promoBanner;
		}
		
		@Override
		public void run() 
		{
			promoBanner.showBanner(promoBanner.imagesPanel.getVisibleWidget() + 1, true);
		}
		
		public void reschedule() 
		{
			this.cancel();
			this.scheduleRepeating(promoBanner.autoTransitionInterval);
		}
	};
}
