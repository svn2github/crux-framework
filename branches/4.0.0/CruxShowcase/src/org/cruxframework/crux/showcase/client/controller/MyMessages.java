package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.i18n.MessageName;

import com.google.gwt.i18n.client.Messages;

@MessageName("myMessages")
public interface MyMessages extends Messages
{
	@DefaultMessage("I18N on page Declaratively")
	String message1();

	@DefaultMessage("Change Locale")
	String myButton();

	@DefaultMessage("Current Locale: ")
	String currentLocaleLabel();

	@DefaultMessage("teste Hot Deploy")
	String testeHotDeploy();
}

