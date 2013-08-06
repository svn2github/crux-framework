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
 * A Crux client database. Uses IndexedDB to store objects on application's client side.
 * To declare a new database, create a new interface extending Database and use {@link DatabaseMetadata} 
 * annotation on it to specify database structure.
 * 
 * @author Thiago da Rosa de Bustamante
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

	/**
	 * Insert a new object into its associated objectStore. If no objectStore is associated with object type, a DatabaseException is threw
	 * @param object
	 * @param callback
	 */
	void add(Object object, DatabaseCallback callback);
	
	/**
	 * Insert all object into its associated objectStore. If no objectStore is associated with object type, a DatabaseException is threw
	 * @param object
	 * @param callback
	 */
    void add(Object[] objects, Class<?> objectType, final DatabaseCallback callback);

	/**
	 * Update the current object into its associated objectStore. If the object does not exists, create a new one.
	 * If no objectStore is associated with object type, a DatabaseException is threw  
	 * @param object
	 * @param callback
	 */
	void put(Object object, DatabaseCallback callback);
	
	/**
	 * Update all received objects into its associated objectStore. If one object does not exists, create a new one.
	 * If no objectStore is associated with object type, a DatabaseException is threw  
	 * @param object
	 * @param callback
	 */
    void put(Object[] objects, Class<?> objectType, final DatabaseCallback callback);

    /**
     * Retrieve the object associated with the given key from its associated objectStore. 
	 * If no objectStore is associated with object type, a DatabaseException is threw  
     * @param <T>
     * @param key
     * @param objectType
     * @param callback
     */
    <T> void get(int key, Class<T> objectType, DatabaseRetrieveCallback<T> callback);

    /**
     * Retrieve the object associated with the given key from its associated objectStore. 
	 * If no objectStore is associated with object type, a DatabaseException is threw  
     * @param <T>
     * @param key
     * @param objectType
     * @param callback
     */
    <T> void get(String key, Class<T> objectType, DatabaseRetrieveCallback<T> callback);

    /**
     * Retrieve the object associated with the given key from its associated objectStore. 
	 * If no objectStore is associated with object type, a DatabaseException is threw  
     * @param <T>
     * @param key a composite key
     * @param objectType
     * @param callback
     */
    <T> void get(String[] compositeKey, Class<T> objectType, DatabaseRetrieveCallback<T> callback);
    
    /**
     * Remove the object associated with the given key from its associated objectStore. 
	 * If no objectStore is associated with object type, a DatabaseException is threw  
     * @param compositeKey
     * @param objectType
     * @param callback
     */
    void delete(Object[] compositeKey, Class<?> objectType, DatabaseCallback callback);

    /**
     * Remove the object associated with the given key from its associated objectStore. 
	 * If no objectStore is associated with object type, a DatabaseException is threw  
     * @param key
     * @param objectType
     * @param callback
     */
    void delete(int key, Class<?> objectType, DatabaseCallback callback);

    /**
     * Remove the object associated with the given key from its associated objectStore. 
	 * If no objectStore is associated with object type, a DatabaseException is threw  
     * @param key
     * @param objectType
     * @param callback
     */
    void delete(String key, Class<?> objectType, DatabaseCallback callback);
}
