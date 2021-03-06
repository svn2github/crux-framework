/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.core.client.controller;

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.event.EventProcessor;



/**
 * Contains a wrapper class for every controller used on module.
 * 
 * @author Thiago da Rosa de Bustamante
 */
@Legacy(value=RegisteredControllers.class)
public interface RegisteredControllersLegacy 
{
	@Deprecated
	@Legacy
	String invokeCrossDocument(String serializedData); 
	@Deprecated
	@Legacy
	void invokeController(String controller, String method, boolean fromOutOfModule, Object sourceEvent, EventProcessor eventProcessor);
}
