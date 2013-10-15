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

import org.cruxframework.crux.core.client.db.Cursor.CursorDirection;
import org.cruxframework.crux.core.client.file.Blob;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class FileStore extends DBObject
{
	public static final String OBJECT_STORE_NAME = "_CRUX_FILE_STORE_";

	protected FileStore(AbstractDatabase db)
	{
		super(db);
	}

	public void add(Blob file, String fileName)
	{
		add(file, fileName, null);
	}

	public abstract void add(final Blob file, final String fileName, final DatabaseWriteCallback<String> callback);

	public abstract void put(final Blob file, final String fileName, final DatabaseWriteCallback<String> callback);

	public abstract void get(String key, DatabaseRetrieveCallback<Blob> callback);

	public abstract void delete(String key, DatabaseDeleteCallback callback);

	public void delete(KeyRange<String> keyRange)
	{
		delete(keyRange, null);
	}

	public abstract void delete(KeyRange<String> keyRange, DatabaseDeleteCallback callback);

	public abstract void clear();

	public abstract void openCursor(FileStoreCursorCallback callback);

	public abstract void openCursor(KeyRange<String> keyRange, FileStoreCursorCallback callback);

	public abstract void openCursor(KeyRange<String> keyRange, CursorDirection direction, FileStoreCursorCallback callback);

	public abstract void count(DatabaseCountCallback callback);

	public abstract void count(KeyRange<String> range, DatabaseCountCallback callback);

	public abstract KeyRangeFactory<String> getKeyRangeFactory();
}
