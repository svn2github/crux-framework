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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.cruxframework.crux.core.client.screen.Screen;
//TODO: terminar essa documentacao
/**
 * This annotation can be used to bind a Data Object to the {@link Screen}.
 * <p>
 * Objects tied to the screen are automatically populated with information from associated 
 * fields before any event is dispatched to your {@link Controller}.
 * <p>
 * After the event handler method finish, the screen is updated with any change made on the 
 * tied object.
 *<p>
 * For example, see the following controller:
 *
 * <pre>
 * {@code @Controller}("myController")
 * public class MyController
 * {
 *    {@code @}{@link Expose}
 *    public void myEventHandler()
 *    {
 *    	Window.alert("event dispatched!");
 *    }
 * }
 * </pre>
 * 
 * @see Controller  
 * @see Create  
 * @author Thiago da Rosa de Bustamante
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ValueObject
{
	boolean bindWidgetByFieldName() default true;
}
