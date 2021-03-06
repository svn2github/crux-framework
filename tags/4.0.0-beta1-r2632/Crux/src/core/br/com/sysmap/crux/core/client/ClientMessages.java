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
package br.com.sysmap.crux.core.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Contains all client messages used by Crux Framework
 * @author Thiago da Rosa de Bustamante -
 */
public interface ClientMessages extends Messages
{
	//  event
	@DefaultMessage("[event 001] - Client Controller Named ''{0}'' not found.")
	String eventProcessorClientControllerNotFound(String controller);
	@DefaultMessage("[event 002] - Error running client method ''{0}''. Message: ''{1}''")
	String eventProcessorClientError(String call, String errMsg);
	@DefaultMessage("[event 003] - Invalid declaration for handler method ''{0}''. Correct syntaxe is <controller.method>.")
	String eventFactoryInvalidHandlerMethodDeclaration(String evt);
	@DefaultMessage("[event 004] - Error loading screen fragment {0}.")
	String viewFactoryCanNotBeLoaded(String controller);
	@DefaultMessage("[event 005] - Service method is already being processed.")
	String methodIsAlreadyBeingProcessed();
	@DefaultMessage("[event 006] - Client Controller Named ''{0}'' does not implement CrossDocument interface and can not be called outside document.")
	String crossDocumentInvalidCrossDocumentController(String controller);
	@DefaultMessage("[event 007] - Error on cross document call. No responde received from method.")
	String crossDocumentInvocationError();
	@DefaultMessage("[event 008] - Error on cross document call. Invalid Target.")
	String crossDocumentInvalidTarget();
	@DefaultMessage("[event 009] - Error on cross document call: {0}.")
	String crossDocumentInvocationGeneralError(String errorMessage);
	@DefaultMessage("[event 010] - Can not identify the method to be called.")
	String crossDocumentCanNotIdentifyMethod();
	@DefaultMessage("[event 011] - Can not find the method to be called.")
	String crossDocumentMethodNotFound();
	@DefaultMessage("[event 012] - WriterStream is not open.")
	String crossDocumentSerializationErrorStreamClosed();
	
	//  screenFactory
	@DefaultMessage("[screenFactory 003] - The widget ''{0}'' is a layout panel that does not have its dimensions defined. Explicity define it, or append it directly on body element.")
	String screenFactoryLayoutPanelWithoutSize(String widgetId);
	
	// screen
	@DefaultMessage("[screen 001] - The id attribute is required for CRUX Screens.")
	String screenFactoryScreenIdRequired();
	
	@DefaultMessage("[screen 002] - Error creating view: ")
	String screenFactoryErrorCreatingView();

	//  widget
	@DefaultMessage("[widget 002] - The widget ''{0}'' does not implement HasWidgets and can not contains any other widget.")
	String screenFactoryInvalidWidgetParent(String widgetId);
	@DefaultMessage("[widget 005] - The element does not represents a widget.")
	String widgetFactoryEnsureWidgetFail();
	@DefaultMessage("[widget 006] - The element is not a span.")
	String widgetFactoryEnsureSpanFail();
	
	@DefaultMessage("[callback 001] - An Invalid controller was passed to AsyncCallbackAdapter.")
	String asyncCallbackInvalidHandlerError();

	@DefaultMessage("[screen 001] - An Invalid object was passed to update screen or DTOs.")
	String screenInvalidObjectError();
	@DefaultMessage("[screen 002] - Creating the view for screen {0}.")
	String screenFactoryCreatingView(String identifier);
	@DefaultMessage("[screen 003] - To use this feature you must enabled compatibility with Crux 2 old interfaces.")
	String screenFactoryCrux2OldInterfacesCompatibilityDisabled();
	@DefaultMessage("[screen 004] - Screen {0} created.")
	String screenFactoryViewCreated(String identifier);
	@DefaultMessage("This application contains components that are not fully supported by your brownser.")
	String screenFactoryUnsupportedWidget();
	
	
	@DefaultMessage("[moduleComunication 001] - Type ''{0}'' can not be shared between modules. Only primitives (and its wrappers), Strings, Dates, Arrays (not multidimesional) and classes implementing CruxSerializable can be used.")
	String moduleComunicationInvalidParamType(String name);

	@DefaultMessage("[datasource 001] - Error loading dataSource data: {0}")
	String localDataSourceErrorLoadingData(String message);
	@DefaultMessage("[datasource 002] - Error loading dataSource remote data: {0}")
	String remoteDataSourceErrorLoadingData(String message);
	@DefaultMessage("[datasource 003] - Error processing requested operation. DataSource is not loaded yet.")
	String dataSourceNotLoaded();
	@DefaultMessage("[datasource 004] - DataSource has changes on page. You must save or discard them before perform this operation.")
	String remoteDataSourcePageDirty();
	@DefaultMessage("[datasource 005] - The column {0} can not be sorted.")
	String dataSourceErrorColumnNotComparable(String columnName);
	
	@DefaultMessage("[stylesheets 001] - Found a null element reference when trying to modify it''s styleName property.")
	String nullElementAtSetStyleName();	
	@DefaultMessage("[stylesheets 002] - Empty strings can not be used as a styleName property value.")
	String emptyStringAsStyleNameValue();

	@DefaultMessage("[crux 001] - Crux Engine is already initialized.")
	String cruxAlreadyInitializedError();
	
	@DefaultMessage("[screenAccessor 001] - Calling a cross document method. Screen[{0}], Controller[{1}], Method[{2}], Target[{3}]")
	String screenAccessorCallingCrossDocument(String screenId, String controller, String method, String target);
	@DefaultMessage("[screenAccessor 001] - Cross document method executed. Screen[{0}], Controller[{1}], Method[{2}], Target[{3}]")
	String screenAccessorCrossDocumentExecuted(String screenId, String controller, String method, String target);
}
