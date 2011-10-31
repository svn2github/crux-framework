package org.cruxframework.cruxsite.client.widget;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ImageBanner extends Composite 
{
	
	private FlowPanel container = new FlowPanel();
	private SlidingDeckPanel imagesPanel = new SlidingDeckPanel();
	private Label leftArrow = new Label();
	private Label rightArrow = new Label();
	private HorizontalPanel bullets = new HorizontalPanel();
	
	public ImageBanner() 
	{
		initWidget(container);
		
		container.add(imagesPanel);
		container.setStyleName("crux-ImageBanner");
		container.getElement().getStyle().setPosition(Position.RELATIVE);
		
		container.add(rightArrow);
		container.add(leftArrow);
		
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
		
	}
	
	protected void showBanner(int i, boolean slideToRight) 
	{
		if(i > imagesPanel.getWidgetCount() - 1)
		{
			i = 0;
		}
		
		if(i < 0)
		{
			i = imagesPanel.getWidgetCount() - 1;
		}
		
		imagesPanel.showWidget(i, slideToRight);
	}

	private void adjustPositions()
	{
		int containerHeight = container.getElement().getClientHeight();
		
		rightArrow.setStyleName("rightArrow");
		rightArrow.getElement().getStyle().setPosition(Position.ABSOLUTE);
		rightArrow.getElement().getStyle().setRight(0, Unit.PX);
		rightArrow.getElement().getStyle().setTop((containerHeight - rightArrow.getElement().getClientHeight())/2, Unit.PX);
		
		leftArrow.setStyleName("leftArrow");
		leftArrow.getElement().getStyle().setPosition(Position.ABSOLUTE);
		leftArrow.getElement().getStyle().setLeft(0, Unit.PX);
		leftArrow.getElement().getStyle().setTop((containerHeight - leftArrow.getElement().getClientHeight())/2, Unit.PX);
	}
	
	
	public void addImage(String URL, String title, String text, String buttonLabel, ClickHandler onclick)
	{
		SimplePanel panel = new SimplePanel();
		panel.getElement().getStyle().setBackgroundImage("url(" + URL + ")");
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
		
		int index = imagesPanel.getVisibleWidget();
		if(index < 1)
		{
			imagesPanel.showWidget(0);
		}
	}
}
