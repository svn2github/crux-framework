/*
 * Copyright 2013 cruxframework.org
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
package org.cruxframework.crux.core.server.rest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated method responds to HTTP GET requests
 * @see HttpMethod
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod(HttpMethod.GET)
public @interface GET 
{
	/**
	 * the number of seconds, starting from current time, that the response can be maintained in client caches
	 * @return
	 */
	int cacheTime() default NEVER;

	/**
	 * the type of cache that can be used to store the response. Private means that information must be used only 
	 * for the client that received it (only one client). Public means that the information can be shared with other 
	 * users (and stored by intermediary cache systems) 
	 * @return
	 */
	CacheControl cacheControl() default CacheControl.PUBLIC;
	
	public static enum CacheControl{ PUBLIC, PRIVATE , NOCACHE}
	
	public static final int NEVER = -1;
	public static final int ONE_MINUTE = 60;
	public static final int ONE_HOUR = ONE_MINUTE * 60;
	public static final int ONE_DAY = ONE_HOUR * 24;
	public static final int ONE_MONTH = ONE_DAY * 30;
	public static final int ONE_YEAR = ONE_DAY * 365;

	//TODO construir um esquema para que o usuario informe a data de modificacao de um recurso, para habilitar gets condicionais 
}