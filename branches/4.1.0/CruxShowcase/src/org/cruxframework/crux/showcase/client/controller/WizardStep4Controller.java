package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.showcase.client.dto.Person;
import org.cruxframework.crux.widgets.client.wizard.EnterEvent;
import org.cruxframework.crux.widgets.client.wizard.LeaveEvent;
import org.cruxframework.crux.widgets.client.wizard.WizardControlBar;


@Controller("wizardStep4Controller")
public class WizardStep4Controller {
	
	@Create
	protected Person person;

	@Expose
	public void onEnter(EnterEvent<Person> event){
		Person personContext = event.getWizardAccessor().readData();
		if (personContext == null)
		{
			this.person = new Person();
		}
		else
		{
			this.person = personContext;
		}
		event.getWizardAccessor().getControlBar().getCommand(WizardControlBar.FINISH_COMMAND).setEnabled(true);
	}

	@Expose
	public void onLeave(LeaveEvent<Person> event){
		event.getWizardAccessor().getControlBar().getCommand(WizardControlBar.FINISH_COMMAND).setEnabled(false);
	}
}