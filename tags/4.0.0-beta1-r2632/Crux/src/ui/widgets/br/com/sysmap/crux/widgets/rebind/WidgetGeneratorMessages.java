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
package br.com.sysmap.crux.widgets.rebind;

import br.com.sysmap.crux.core.i18n.DefaultServerMessage;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface WidgetGeneratorMessages
{
	@DefaultServerMessage("[wizardData 001] - Duplicated wizardData found: {0}")
	String wizardDataDuplicatedObject(String value);

	@DefaultServerMessage("[wizardData 002] - Error initializing wizardData objects: {0}")
	Object wizardDataInitializeError(String localizedMessage);

	@DefaultServerMessage("[wizardDataGenerator 002] - Error generating wizardData object: {0}. Error: {1}")
	String errorGeneratingRegisteredWizardData(String wizardData, String localizedMessage);

	@DefaultServerMessage("[tabInvoker 001] - Error for generating invoker wrapper: Invalid Method signature: {0}. A valid signature must have the form [methodName][OnTab][tabId]")
	String tabsControllerInvalidSignature(String signature);

	@DefaultServerMessage("[grid 001] - Grid {0} has no column.")
	String gridDoesNotHaveColumns(String gridId);
	
	@DefaultServerMessage("[grid 002] - Grid {0} has an invalid column (unexpected column type).")
	String gridErrorInvalidColumnType(String id);

	@DefaultServerMessage("[grid 003] - Grid <{0}> has an invalid column <colKey>.")
	String gridErrorInvalidColumn(String gridId, String colKey);

	@DefaultServerMessage("[maskedLabel 001] - The attribute formatter is required for MaskedLabel.")
	String maskedLabelFormatterRequired();	

	@DefaultServerMessage("[maskedLabel 002] - The formatter {0} was not found on this screen.")
	String maskedLabelFormatterNotFound(String formatter);

	@DefaultServerMessage("[maskedTextBox] - The attribute formatter is required for MaskedTextBox.")
	String maskedTextBoxFormatterRequired();

	@DefaultServerMessage("[pager 001] - No pageable widget set for this pager.")
	String pagerNoPageableSet();
}


