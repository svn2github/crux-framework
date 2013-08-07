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
package org.cruxframework.crux.core.client.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the database metadada. All Database interfaces must be annotated with 
 * this annotation to inform Crux the database structure.
 * 
 * @author Thiago da Rosa de Bustamante
 */
public @interface DatabaseMetadata
{
	/**
	 * @return Database name.
	 */
	String name();
	/**
	 * @return Database version.
	 */
	int version();
	/**
	 * @return Object stores metadata.
	 */
	ObjectStoreMetadata[] objectStores();

	/**
	 * If this property is true, Crux will override any existent Object store or index when updating database.
	 * If false, only non existent database or index will be created on database updating. 
	 * @return
	 */
	boolean overrideDBElements() default false;
	
	/**
	 * Defines a new object store for current database. 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.ANNOTATION_TYPE)
	public @interface ObjectStoreMetadata
	{
		String name() default "";
		boolean autoIncrement() default false;
		String keyPath() default "";
		String[] compositeKeyPath() default {};
		Class<?> targetClass() default Empty.class;
	}
	
	public static class Empty{}
}