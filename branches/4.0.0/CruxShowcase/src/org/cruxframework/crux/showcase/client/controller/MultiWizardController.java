package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.showcase.client.dto.Person;
import org.cruxframework.crux.widgets.client.dialog.MessageBox;
import org.cruxframework.crux.widgets.client.event.CancelEvent;
import org.cruxframework.crux.widgets.client.event.FinishEvent;
import org.cruxframework.crux.widgets.client.event.OkEvent;
import org.cruxframework.crux.widgets.client.event.OkHandler;
import org.cruxframework.crux.widgets.client.wizard.Wizard;


@Controller("multiWizardController")
public class MultiWizardController{
	
	@SuppressWarnings("unchecked")
    @Expose
	public void onCancel(final CancelEvent cancelEvt){
		final Wizard<Person> wizard = (Wizard<Person>) cancelEvt.getSource();
		MessageBox.show(
			"Info", "Operation canceled! Returning to first step...", new OkHandler()	{
				public void onOk(OkEvent ok) {
					wizard.first();
				}
			}
		);
	}

	@SuppressWarnings("unchecked")
    @Expose
	public void onFinish(final FinishEvent finishEvt){
		final Wizard<Person> wizard = (Wizard<Person>) finishEvt.getSource();
		MessageBox.show(
			"Info", "Congratulations! Operation completed. Returnig to first step...", new OkHandler()	{
				public void onOk(OkEvent ok) {
					wizard.first();
				}
			}
		);
	}
}