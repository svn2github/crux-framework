package org.cruxframework.crux.showcase.client.controller;

import java.util.Date;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.controller.ValueObject;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.titlepanel.TitlePanel;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

@Controller("valueBindController")
public class ValueBindController {
	
	@Create
	protected Person person;
	
	@Expose
	public void onClick()
	{
		VerticalPanel messageItens = new VerticalPanel();
		messageItens.setSpacing(8);
		messageItens.add(new Label(person.getName()));
		messageItens.add(new Label(person.getPhone()));
		messageItens.add(new Label("" + person.getDateOfBirth()));
		TitlePanel messagePanel = new TitlePanel("", "", "crux-Popup help-Popup");
		messagePanel.setContentWidget(messageItens);
		messagePanel.setTitleText("The values entered by the user are:");
		PopupPanel popupPanel = new PopupPanel(true);
		popupPanel.add(messagePanel);
		popupPanel.showRelativeTo(Screen.get("button"));
	}
	
	@ValueObject
	public static class Person
	{
		private String name;
		private String phone;
		private Date dateOfBirth;
		
		public String getName(){
			return name;
		}

		public void setName(String name){
			this.name = name;
		}

		public String getPhone(){
			return phone;
		}

		public void setPhone(String phone){
			this.phone = phone;
		}

		public Date getDateOfBirth(){
			return dateOfBirth;
		}

		public void setDateOfBirth(Date dateOfBirth){
			this.dateOfBirth = dateOfBirth;
		}
	}
}