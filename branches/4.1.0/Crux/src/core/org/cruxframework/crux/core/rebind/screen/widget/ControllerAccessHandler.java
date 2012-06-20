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
package org.cruxframework.crux.core.rebind.screen.widget;

/**
 * Subclasses of this interface are used to inform Crux about how a controller class must be 
 * referenced by generated code
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface ControllerAccessHandler
{
	String getControllerExpression(String controller);
	String getControllerImplClassName(String controller);
	
	/**
	 * A ControllerAccessHandler that must be used when any reference to the controller printed
	 * during the current generation cycle must point to the same controller instance. It is used
	 * by LazyPanels discover how to access the controller.  
	 * @author Thiago da Rosa de Bustamante
	 */
	public static interface SingleControllerAccessHandler extends ControllerAccessHandler
	{
		String getSingleControllerImplClassName();
		String getSingleControllerVariable();
	}
}
