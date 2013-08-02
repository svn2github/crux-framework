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
package org.cruxframework.crux.core.client.db;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface Database
{
	/**
	 * Retrieve the database name. This information is extracted from {@code @}DatabaseMetadata annotation
	 * @return
	 */
	String getName(); 

	/**
	 * Retrieve the database version. This information is extracted from {@code @}DatabaseMetadata annotation
	 * @return
	 */
	int getVersion(); 

	/**
	 * Open the database. If it does not exists, create a new database.
	 * @param callback - called when operation is completed
	 */
	void open(final DatabaseCallback callback);

	/**
	 * Close the current database.
	 */
	void close();

	/**
	 * Remove the current database from client browser.
	 * @param callback - called when operation is completed
	 */
	void delete(final DatabaseCallback callback);
}
