package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.showcase.client.dto.Person;
import org.cruxframework.crux.widgets.client.wizard.EnterEvent;
import org.cruxframework.crux.widgets.client.wizard.WizardControlBar;


@Controller("wizardStep1Controller")
public class WizardStep1Controller {
	
	@Expose
	public void onEnter(EnterEvent<Person> event){
		event.getWizardAccessor().getControlBar().getCommand(WizardControlBar.CANCEL_COMMAND).setEnabled(false);
		event.getWizardAccessor().getControlBar().getCommand(WizardControlBar.FINISH_COMMAND).setEnabled(false);
	}
}