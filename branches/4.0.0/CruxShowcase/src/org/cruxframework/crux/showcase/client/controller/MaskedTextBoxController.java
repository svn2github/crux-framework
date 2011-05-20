package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.formatter.Formatter;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.ScreenWrapper;
import org.cruxframework.crux.widgets.client.maskedtextbox.MaskedTextBox;

import com.google.gwt.user.client.ui.Label;

@Controller("maskedTextBoxController")
public class MaskedTextBoxController {
	
	private String currentFormatterName = "phone";
	
	@Create
	protected MaskedScreen screen;
	
	@Expose
	public void changeFormat(){
		
		String newFormatterName = "phone".equals(currentFormatterName) ? "date" : "phone";
		Formatter newFormatter = Screen.getFormatter(newFormatterName);
		currentFormatterName = newFormatterName;
		
		MaskedTextBox maskedTextBox = screen.getMaskedTextBox();
		maskedTextBox.setUnformattedValue(null);		
		maskedTextBox.setFormatter(newFormatter);		
		
		swapLabel();
	}	

	private void swapLabel()
	{
		String label = currentFormatterName.substring(0, 1).toUpperCase() + currentFormatterName.substring(1);
		screen.getMaskedLabel().setText(label + ":");
	}
	
	public static interface MaskedScreen extends ScreenWrapper{
		MaskedTextBox getMaskedTextBox();
		Label getMaskedLabel();
	}
}