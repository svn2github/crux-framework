/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.core.client.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.gwt.core.client.JavaScriptObject;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface JsonEncoder<T>
{
	String encode(T object);
	JavaScriptObject toJavaScriptObject(T object);
	T decode(String jsonText);
	T fromJavaScriptObject(JavaScriptObject object);
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public static @interface JsonIgnore{}
	
	//We still need jackson annotations at server (des)serialization. Use Javassist to include them?
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	public static @interface JsonTypeInfo
	{
		String property() default "type";
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	public static @interface JsonSubTypes
	{
		Type[] value();
		
		@Retention(RetentionPolicy.RUNTIME)
		@Target({ElementType.TYPE})
		public static @interface Type
		{
			Class<?> value();
			String name();
		}		
	}
}
