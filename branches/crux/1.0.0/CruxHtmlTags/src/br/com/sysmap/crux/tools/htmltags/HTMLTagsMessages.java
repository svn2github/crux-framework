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
package br.com.sysmap.crux.tools.htmltags;

import br.com.sysmap.crux.core.i18n.DefaultMessage;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public interface HTMLTagsMessages
{
	@DefaultMessage("[htmlTags - 001] - Error initialising widgetTagConfigScanner. Message: {0}")
	String widgetTagConfigScannerInitializationError(String localizedMessage);
	
	@DefaultMessage("[htmlTags - 002] - Error creating xslt file from template. Template crux-ui.template.xslt not found")
	String transformerTemplateNotFound();
	
	@DefaultMessage("[htmlTags - 003] - Error creating xslt file from template. Error reading Template crux-ui.template.xslt: {0}")
	String transformerErrorReadingTemplate(String message);
	
	@DefaultMessage("[htmlTags - 004] - Error creating xsl transformer: {0}")
	String transformerErrorCreatingTransformer(String message);
	
	@DefaultMessage("[htmlTags - 005] - Error obtaining screen resource. Screen id: {0}. Message: {1}")
	String cruxHtmlTagsScreenResolverError(String screenId, String message);
}
