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

import br.com.sysmap.crux.core.i18n.DefaultServerMessage;


/**
 * Messages from generator.
 * @author Thiago
 *
 */
public interface GeneratorMessages 
{
	@DefaultServerMessage("[generator 001] - DeclarativeFactory not registered: ")
	String errorGeneratingRegisteredWidgetFactoryNotRegistered();

	@DefaultServerMessage("[generator 002] - Error for register client event handler. Controller: {0}. Error:{1}")
	String errorGeneratingRegisteredController(String controller, String errMesg);

	@DefaultServerMessage("[generator 003] - Error for register client formatter. Formatter: {0}. Error:{1}")
	String errorGeneratingRegisteredFormatter(String formatter, String errMesg);

	@DefaultServerMessage("[generator 004] - Error for generate client class {0}:")
	String errorGeneratingRegisteredElement(String errMsg);

	@DefaultServerMessage("[generator 005] - Error retrieving screen Identifier.")
	String errorGeneratingRegisteredElementInvalidScreenID();

	@DefaultServerMessage("[generator 006] - property {0} could not be created. This is not visible neither has a getter/setter method.")
	String registeredClientObjectPropertyNotFound(String name);

	@DefaultServerMessage("[generator 007] - Error generating class for declared message {0}.")
	String errorGeneratingDeclaredMessagesClassNotFound(String string);

	@DefaultServerMessage("[controller 001] - Method Not Found: ")
	String errorInvokingGeneratedMethod();

	@DefaultServerMessage("[generator 008] - Error for register CruxSerializable serializer. Serializer: {0}. Error:{1}")
	String errorGeneratingRegisteredCruxSerializable(String serializer, String localizedMessage);

	@DefaultServerMessage("[generator 009] - Error for generating screen wrapper: {0}.")
	String errorGeneratingScreenWrapper(String localizedMessage);

	@DefaultServerMessage("[generator 010] - Error for generating context wrapper: Invalid Method signature: {0}.")
	String errorContextWrapperInvalidSignature(String method);

	@DefaultServerMessage("[generator 011] - Error for generating context wrapper: Primitive Parameter not allowed: {0}.")
	String errorContextWrapperPrimitiveParamterNotAllowed(String method);

	@DefaultServerMessage("[generator 012] - Error for generating invoker wrapper: Invalid Method signature: {0}.")
	String errorInvokerWrapperInvalidSignature(String method);

	@DefaultServerMessage("[generator 013] - Error for invoking method. Serialization Error.")
	String errorInvokerWrapperSerializationError();

	@DefaultServerMessage("[generator 014] - Widget {0} not found.")
	String errorGeneratingRegisteredObjectWidgetNotFound(String name);

	@DefaultServerMessage("[generator 015] - Error for register client datasource. DataSource: {0}. Error:{1}")
	String errorGeneratingRegisteredDataSource(String dataSource, String message);

	@DefaultServerMessage("[generator 016] - DataSource class {0} must use annotation DataSourceColumn or implements BindableDataSource<T> interface and use annotation DataSourceBinding")
	String errorGeneratingRegisteredDataSourceNoMetaInformation(String name);

	@DefaultServerMessage("[generator 017] - DataSource class {0} can not use annotation DataSourceColumn if it implements BindableDataSource<T> interface.")
	String errorGeneratingRegisteredDataSourceConflictingMetaInformation(String name);

	@DefaultServerMessage("[generator 018] - DataSource class {0} has invalid annotatted information.")
	String errorGeneratingRegisteredDataSourceInvalidMetaInformation(String dataSourceClassName);

	@DefaultServerMessage("[generator 019] - DataSource not found: ")
	String errorGeneratingRegisteredDataSourceNotFound();
	
	@DefaultServerMessage("[generator 020] - Error generating invokable object: {0}")
	String errorGeneratingInvokableObject(String errMesg);

	@DefaultServerMessage("[generator 021] - Error for register client datasource. DataSource: {0}. Error: Can not realize the type for datasource generic declaration.")
	String errorGeneratingRegisteredDataSourceCanNotRealizeGenericType(String name);

	@DefaultServerMessage("[generator 022] - Error generating invokable object. Can not realize the type for generic declaration.")
	String errorGeneratingInvokableObjectCanNotRealizeGenericType();

	@DefaultServerMessage("[generator 023] - Error generating widget factory: {0}.")
	String errorGeneratingWidgetFactory(String localizedMessage);

	@DefaultServerMessage("[generator 024] - Error generating widget factory. Can not realize the type for generic declaration.")
	String errorGeneratingWidgetFactoryCanNotRealizeGenericType(String name);

	@DefaultServerMessage("[generator 025] - Error generating widget factory. Invalid attribute name: {0}.")
	String errorGeneratingWidgetFactoryInvalidAttrName(String attrName);

	@DefaultServerMessage("[generator 026] - Error generating widget factory. Widget does not have a valid setter for attribute: {0}.")
	String errorGeneratingWidgetFactoryInvalidProperty(String attrName);

	@DefaultServerMessage("[generator 027] - Error generating widget factory. invalid validation method: {0}.")
	String errorGeneratingRegisteredControllerInvalidValidateMethod(String validateMethod);

	@DefaultServerMessage("[generator 028] - Error generating widget factory. An element can not contains text and other children.")
	String errorGeneratingWidgetFactoryMixedContentNotAllowed();

	@DefaultServerMessage("[generator 029] - Error creating modules scanner. Can not create builder object.")
	String modulesScannerErrorBuilderCanNotBeCreated();

	@DefaultServerMessage("[generator 030] - Searching for modules.")
	String modulesScannerSearchingModuleFiles();

	@DefaultServerMessage("[generator 031] - Error parsing module file: {0}.")
	String modulesScannerErrorParsingModuleFile(String fileName);

	@DefaultServerMessage("[generator 032] - Error initializing modulesScanner. ErrorMsg: {0}")
	String modulesScannerInitializationError(String localizedMessage);

	@DefaultServerMessage("[generator 033] - Can not find the web classes dir.")
	String modulesScannerErrorFindingClassesDir();

	@DefaultServerMessage("[generator 034] - Can not find the module {0}.")
	String errorGeneratingRegisteredElementModuleNotFound(String module);

	@DefaultServerMessage("[generator 035] - Parameter Object {0} has no valid field for binding.")
	String errorGeneratingRegisteredObjectParameterObjectHasNoValidField(String name);
	
	@DefaultServerMessage("[generator 036] - No method found on service interface that matches the async method: {0}.")
	String cruxProxyCreatorMethodNotFoundOnServiceInterface(String name);

	@DefaultServerMessage("[generator 037] - UseSynchronizer Token only can be used with void return type on Async interface.")
	String cruxProxyCreatorInvalidReturnType(String simpleSourceName);

	@DefaultServerMessage("[generator 038] - Unable to find source for type {0}")
	String generatorSourceNotFound(String typeName);

	@DefaultServerMessage("[generator 039] - {0} is not an interface.")
	String crossDocumentGeneratorTypeIsNotInterface(String qualifiedSourceName);
	
	@DefaultServerMessage("[generator 040] - Cross document interface {0} does not follow the name pattern for cross document objects.")
	String crossDocumentInvalidCrossDocInterfaceName(String crossDocInterfaceName);
	
	@DefaultServerMessage("[generator 041] - Could not find the cross document controller for the interface {0}.")
	String crossDocumentCanNotFindControllerForInterface(String crossDocInterfaceName);

	@DefaultServerMessage("[generator 042] - The controller found for the interface {0} does not have the annotation @Controller.")
	String crossDocumentInvalidController(String crossDocInterfaceName);

	@DefaultServerMessage("[parameter 001] - Required parameter {0} is missing.")
	String requiredParameterMissing(String name);

	@DefaultServerMessage("[parameter 002] - Error parsing parameter {0}.")
	String errorReadingParameter(String name);

	@DefaultServerMessage("[screenFactory 001] - The id attribute is required for CRUX Widgets.")
	String screenFactoryWidgetIdRequired();

	@DefaultServerMessage("[screenFactory 002] - Can not create widget {0}. Verify the widget type.")
	String screenFactoryErrorCreateWidget(String widgetId);

	@DefaultServerMessage("[screenFactory 003] - Error creating widget. Duplicated identifier: {0}.")
	String screenFactoryErrorDuplicatedWidget(String widgetId);

	@DefaultServerMessage("[screenFactory 004] - Multiple modules in the same html page is not allowed in CRUX.")
	String screenFactoryErrorMultipleModulesOnPage();
	
	@DefaultServerMessage("[screenFactory 005] - Error retrieving screen {0}. Error: {1}.")
	String screenFactoryErrorRetrievingScreen(String screenId, String errMsg);

	@DefaultServerMessage("[screenFactory 006] - Error creating widget {0}. Error: {1}.")
	String screenFactoryGenericErrorCreateWidget(String screenId, String errMsg);

	@DefaultServerMessage("[screenFactory 007] - Screen {0} not found!")
	String screenFactoryScreeResourceNotFound(String screenId);
	
	@DefaultServerMessage("[screenFactory 008] - Error parsing screen {0}. Details: {1}.")
	String screenFactoryErrorParsingScreen(String screenId, String errMsg);

	@DefaultServerMessage("[Screen 001] - Error setting property {0} for screen {1}.")
	String screenPropertyError(String property, String screenId);
	
	@DefaultServerMessage("[generator 043] - Error for register client event handler. Error:{1}")
	String errorGeneratingRegisteredController(String errMesg);

	@DefaultServerMessage("[screenFactory 008] - Datasource {0}, declared on screen {1}, not found!")
	String screenFactoryInvalidDataSource(String datasource, String screenId);

	@DefaultServerMessage("[screenFactory 009] - Formatter {0}, declared on screen {1}, not found!")
	String screenFactoryInvalidFormatter(String formatter, String id);

	@DefaultServerMessage("[screenFactory 010] - Serializable {0}, declared on screen {1}, not found!")
	String screenFactoryInvalidSerializable(String serializer, String id);

	@DefaultServerMessage("[screenFactory 011] - Controller {0}, declared on screen {1}, not found!")
	String screenFactoryInvalidController(String handler, String id);

	@DefaultServerMessage("[generator 044] - Error generating invoker. Error:{0}")
	String errorGeneratingInvokerWrapper(String localizedMessage);

	@DefaultServerMessage("[generator 045] - Error generating invoker. Controller {0} not found.")
	String errorGeneratingInvokerControllerNotFound(String name);

	@DefaultServerMessage("[generator 046] - The Controller {0} is a CrossDocument controller. However, it does not implements the required interface {0}CrossDoc")
	String crossDocumentCanNotFindControllerCrossDocInterface(String canonicalName);

	@DefaultServerMessage("[generator 047] - Error generating invoker. Controller not found for interface {0}.")
	String errorGeneratingInvokerControllerNotFoundForWrapper(String canonicalName);

	@DefaultServerMessage("[generator 048] - Error Generating registered element. Can not retrieve module's list of screens.")
	String errorGeneratingRegisteredElementCanNotFoundScreens();

	@DefaultServerMessage("[generator 049] - Error Generating DataSource {0}. Can not retrieve identifier field {1}.")
	String errorGeneratingRegisteredDataSourceCanNotFindIdentifier(String name, String field);

	@DefaultServerMessage("[generator 050] - Error Generating DataSource. Column {1} from type {0} is invalid.")
	String errorGeneratingRegisteredDataSourceInvalidColumn(String name, String columnName);

	@DefaultServerMessage("[generator 051] - Error Generating DataSource {0}. Invalid Bound object. Primitive is not allowed")
	String errorGeneratingRegisteredDataSourceInvalidBoundObject(String name);

	@DefaultServerMessage("[generator 052] - Error searching for module pages. Module Name = {0}")
	String modulesErrorSearchingModulepages(String moduleName);

}
