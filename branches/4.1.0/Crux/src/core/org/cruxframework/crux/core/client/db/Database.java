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

import org.cruxframework.crux.core.client.db.Transaction.TransactionCallback;
import org.cruxframework.crux.core.client.db.annotation.DatabaseMetadata;

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
	 * Return true if the current database is open.
	 * @return
	 */
	boolean isOpen();
	
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
	 * Create a new transaction targeting the objectStores associated with the given object types.
	 * @param objectTypes
	 * @param mode
	 * @return
	 */
	Transaction getTransaction(Class<?>[] objectTypes, Transaction.Mode mode);

	/**
	 * Create a new transaction targeting the given objectStores.
	 * @param storeNames
	 * @param mode
	 * @return
	 */
	Transaction getTransaction(String[] storeNames, Transaction.Mode mode);
	
	/**
	 * Create a new transaction targeting the objectStores associated with the given object types.
	 * @param objectTypes
	 * @param mode
	 * @param callback
	 * @return
	 */
	Transaction getTransaction(Class<?>[] objectTypes, Transaction.Mode mode, TransactionCallback callback);

	/**
	 * Create a new transaction targeting the given objectStores.
	 * @param storeNames
	 * @param mode
	 * @param callback
	 * @return
	 */
	Transaction getTransaction(String[] storeNames, Transaction.Mode mode, TransactionCallback callback);

	/**
	 * Insert the object into its associated objectStore. If no objectStore is associated with object type, a DatabaseException is threw
	 * @param <K>
	 * @param <V>
	 * @param object
	 * @param callback
	 */
	<K, V> void add(V object, DatabaseCallback callback);
	
	/**
	 * Insert all objects into its associated objectStore. If no objectStore is associated with object type, a DatabaseException is threw
	 * @param <K>
	 * @param <V>
	 * @param object
	 * @param callback
	 */
	<K, V> void add(V[] objects, Class<V> objectType, DatabaseCallback callback);
	
	/**
	 * Update object into its associated objectStore. If the object does not exists, create a new one.
	 * @param <K>
	 * @param <V>
	 * @param object
	 * @param callback
	 */
	<K, V> void put(V object, DatabaseCallback callback);

	/**
	 * Update all received objects into its associated objectStore. If one object does not exists, create a new one.
	 * If no objectStore is associated with object type, a DatabaseException is threw  
	 * @param <K>
	 * @param <V>
	 * @param object
	 * @param callback
	 */
	<K, V> void put(V[] objects, Class<V> objectType, DatabaseCallback callback);

    /**
     * Retrieve the object associated with the given key from its associated objectStore. 
	 * If no objectStore is associated with object type, a DatabaseException is threw  
	 * @param <K>
	 * @param <V>
     * @param key
     * @param objectType
     * @param callback
     */
	<K, V> void get(K key, Class<V> objectType, DatabaseRetrieveCallback<V> callback);
	
    /**
     * Remove the object associated with the given key from its associated objectStore. 
	 * If no objectStore is associated with object type, a DatabaseException is threw  
	 * @param <K>
	 * @param <V>
     * @param compositeKey
     * @param objectType
     * @param callback
     */
	<K, V> void delete(K key, Class<V> objectType, DatabaseCallback callback);

    /**
     * Remove all objects in the given range from its associated objectStore. 
	 * If no objectStore is associated with object type, a DatabaseException is threw  
	 * @param <K>
	 * @param <V>
     * @param compositeKey
     * @param objectType
     * @param callback
     */
	<K, V> void delete(KeyRange<K> keyRange, Class<V> objectType, DatabaseCallback callback);
}
