package org.cruxframework.cruxsite.client.widget;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ImageBanner extends Composite 
{
	
	private FlowPanel container = new FlowPanel();
	private DeckPanel imagesPanel = new DeckPanel();
	private Label leftArrow = new Label();
	private Label rightArrow = new Label();
	private HorizontalPanel bullets = new HorizontalPanel();
	
	public ImageBanner() 
	{
		container.add(imagesPanel);
		initWidget(container);
		setStyleName("crux-ImageBanner");
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
	}
}
