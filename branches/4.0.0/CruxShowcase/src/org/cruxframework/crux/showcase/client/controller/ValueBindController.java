package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.widgets.client.decoratedbutton.DecoratedButton;
import org.cruxframework.crux.widgets.client.titlepanel.TitlePanel;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

@Controller("valueBindController")
public class ValueBindController {
	
	@Create
	protected Person person;
	
	@Expose
	public void onClick() {
		VerticalPanel formValues = new VerticalPanel();
		formValues.add(new Label(person.getName()));
		formValues.add(new Label(person.getPhone().toString()));
		formValues.add(new Label(person.getDateOfBirth().toString()));
		showDialog(formValues);
	}
	
	private void showDialog(VerticalPanel formValues) {
		
		final PopupPanel popupPanel = new PopupPanel(true);
		
		formValues.setSpacing(8);
		
		TitlePanel messagePanel = new TitlePanel("", "", "crux-Popup help-Popup");
		messagePanel.setContentWidget(formValues);
		
		DecoratedButton ok = new DecoratedButton();
		ok.setText("Ok");
		ok.setWidth("80px");
		ok.getElement().getStyle().setMargin(5, Unit.PX);
		ok.addClickHandler(
			new ClickHandler(){		
				public void onClick(ClickEvent event){
					popupPanel.hide();
				}
			}
		);
		
		formValues.add(ok);
		formValues.setCellHorizontalAlignment(ok, HasHorizontalAlignment.ALIGN_CENTER);
		messagePanel.setTitleText("The values entered by the user are:");
		popupPanel.add(messagePanel);
		popupPanel.showRelativeTo(Screen.get("button"));
	}
}