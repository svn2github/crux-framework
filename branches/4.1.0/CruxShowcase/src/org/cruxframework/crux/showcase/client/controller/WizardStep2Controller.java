package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.controller.Validate;
import org.cruxframework.crux.core.client.event.ValidateException;
import org.cruxframework.crux.core.client.screen.ScreenWrapper;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.showcase.client.dto.Person;
import org.cruxframework.crux.widgets.client.maskedtextbox.MaskedTextBox;
import org.cruxframework.crux.widgets.client.wizard.EnterEvent;
import org.cruxframework.crux.widgets.client.wizard.LeaveEvent;
import org.cruxframework.crux.widgets.client.wizard.WizardControlBar;


import com.google.gwt.user.client.ui.TextBox;

@Controller("wizardStep2Controller")
public class WizardStep2Controller {
	@Create
	protected Person person;
	
	@Create
	protected StepScreen screen;
	
	@Expose
	public void onEnter(EnterEvent<Person> event){
		
		event.getWizardAccessor().getControlBar().getCommand(WizardControlBar.CANCEL_COMMAND).setEnabled(true);
		
		Person personContext = event.getWizardAccessor().readData();
		if (personContext == null) {
			clearFields();
		}
		else {
			this.person = personContext;
		}
	}

	@Expose
	@Validate
	public void onLeave(LeaveEvent<Person> event){
		event.getWizardAccessor().updateData(this.person);
	}
	
	public void validateOnLeave(LeaveEvent<Person> event) throws ValidateException{
		if (!"step1".equals(event.getNextStep()))
		{
			if (StringUtils.isEmpty(this.person.getName())) {
				screen.getName().setFocus(true);
				event.cancel();
				throw new ValidateException("Field person is required!");
			}
			if (StringUtils.isEmpty(this.person.getPhone())) {
				screen.getPhone().setFocus(true);
				event.cancel();
				throw new ValidateException("Field phone is required!");
			}
			if (this.person.getDateOfBirth() == null) {
				screen.getDateOfBirth().setFocus(true);
				event.cancel();
				throw new ValidateException("Field Date of Birth is required!");
			}
		}
	}

	@Expose
	public void clearFields(){
		this.person = new Person();
	}
	
	public static interface StepScreen extends ScreenWrapper{
		TextBox getName();
		MaskedTextBox getPhone();
		MaskedTextBox getDateOfBirth();
	}
	
}