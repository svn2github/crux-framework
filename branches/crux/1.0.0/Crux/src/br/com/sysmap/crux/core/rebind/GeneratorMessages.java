/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.sysmap.crux.core.rebind;

import br.com.sysmap.crux.core.i18n.DefaultMessage;

/**
 * Messages from generator.
 * @author Thiago
 *
 */
public interface GeneratorMessages 
{
	@DefaultMessage("[generator 001] - WidgetFactory not registered: ")
	String errorGeneratingRegisteredWidgetFactoryNotRegistered();

	@DefaultMessage("[generator 002] - Error for register client event handler. Controller: {0}. Error:{1}")
	String errorGeneratingRegisteredClientHandler(String widget, String errMesg);

	@DefaultMessage("[generator 003] - Error for register client formatter. Formatter: {0}. Error:{1}")
	String errorGeneratingRegisteredFormatter(String formatter, String errMesg);

	@DefaultMessage("[generator 004] - Error for register client class {0}:")
	String errorGeneratingRegisteredElement(String errMsg);

	@DefaultMessage("[generator 005] - Error retrieving screen Identifier.")
	String errorGeneratingRegisteredElementInvalidScreenID();

	@DefaultMessage("[generator 006] - property {0} can could not be created. This is not visible neither has a getter/setter method.")
	String registeredClientObjectPropertyNotFound(String name);

	@DefaultMessage("[generator 007] - Error generating class for declared message {0}.")
	String errorGeneratingDeclaredMessagesClassNotFound(String string);

	@DefaultMessage("[controller 001] - Method Not Found: ")
	String errorInvokingGeneratedMethod();

	@DefaultMessage("[generator 008] - Error for register CruxSerializable serializer. Serializer: {0}. Error:{1}")
	String errorGeneratingRegisteredCruxSerializable(String serializer, String localizedMessage);

	@DefaultMessage("[generator 009] - Error for generating screen wrapper: {0}.")
	String errorGeneratingScreenWrapper(String localizedMessage);

	@DefaultMessage("[generator 010] - Error for generating context wrapper: Invalid Method signature: {0}.")
	String errorContextWrapperInvalidSignature(String method);

	@DefaultMessage("[generator 011] - Error for generating context wrapper: Primitive Parameter not allowed: {0}.")
	String errorContextWrapperPrimitiveParamterNotAllowed(String method);

	@DefaultMessage("[generator 012] - Error for generating invoker wrapper: Invalid Method signature: {0}.")
	String errorInvokerWrapperInvalidSignature(String method);

	@DefaultMessage("[generator 013] - Error for invoking method. Serialization Error.")
	String errorInvokerWrapperSerializationError();

	@DefaultMessage("[generator 014] - Widget {0} not found.")
	String errorGeneratingRegisteredObjectWidgetNotFound(String name);

	@DefaultMessage("[generator 015] - Error for register client datasource. DataSource: {0}. Error:{1}")
	String errorGeneratingRegisteredDataSource(String dataSource, String message);

	@DefaultMessage("[generator 016] - DataSource class {0} must use annotation DataSourceColumn or implements Bindable<T> interface and use annotation DataSourceBinding")
	String errorGeneratingRegisteredDataSourceNoMetaInformation(String name);

	@DefaultMessage("[generator 017] - DataSource class {0} can not use annotation DataSourceColumn if it implements Bindable<T> interface.")
	String errorGeneratingRegisteredDataSourceConflictingMetaInformation(String name);

	@DefaultMessage("[generator 018] - DataSource class {0} has invalid annotatted information.")
	String errorGeneratingRegisteredDataSourceInvalidMetaInformation(String dataSourceClassName);

	@DefaultMessage("[generator 019] - DataSource not found: ")
	String errorGeneratingRegisteredDataSourceNotFound();
	
	@DefaultMessage("[screenFactory 001] - The id attribute is required for CRUX Widgets.")
	String screenFactoryWidgetIdRequired();

	@DefaultMessage("[screenFactory 002] - Can not create widget {0}. Verify the widget type.")
	String screenFactoryErrorCreateWidget(String widgetId);

	@DefaultMessage("[screenFactory 003] - Error creating widget. Duplicated identifier: {0}.")
	String screenFactoryErrorDuplicatedWidget(String widgetId);

	@DefaultMessage("[screenFactory 004] - Multiple modules in the same html page is not allowed in CRUX.")
	String screenFactoryErrorMultipleModulesOnPage();
	
	@DefaultMessage("[screenFactory 005] - Error retrieving screen {0}. Error: {1}.")
	String screenFactoryErrorRetrievingScreen(String screenId, String errMsg);

	@DefaultMessage("[screenFactory 006] - Error Creating widget {0}. Error: {1}.")
	String screenFactoryGenericErrorCreateWidget(String screenId, String errMsg);

	@DefaultMessage("[screenFactory 007] - Screen {0} not found!")
	String screenFactoryScreeResourceNotFound(String screenId);

	@DefaultMessage("[Screen 001] - Error setting property {0} for widget {1}.")
	String screenPropertyError(String property, String widgetId);
}
