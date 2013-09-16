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

import org.cruxframework.crux.core.client.db.FileStore.FileInfo;
import org.cruxframework.crux.core.client.db.indexeddb.IDBCursorWithValue;
import org.cruxframework.crux.core.client.file.File;
import org.cruxframework.crux.core.client.file.FileReader;
import org.cruxframework.crux.core.client.file.FileReader.ReaderStringCallback;
import org.cruxframework.crux.core.client.utils.FileUtils;

import com.google.gwt.core.client.JsArrayMixed;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class FileCursor extends Cursor<String, File>
{
	private boolean usesWebSQL;

	protected FileCursor(IDBCursorWithValue idbCursor)
    {
	    super(idbCursor);
		usesWebSQL = NativeDBHandler.usesWebSQL();
    }

	@Override
    public JsArrayMixed getNativeArrayKey()
    {
	    JsArrayMixed key = JsArrayMixed.createArray().cast();
	    key.push(getKey());
	    return key;
    }

	@Override
    public void update(final File file)
    {
		if (usesWebSQL)
		{
			FileReader fileReader = FileReader.createIfSupported();
			assert(fileReader != null):"Unsupported browser";
			fileReader.readAsDataURL(file, new ReaderStringCallback()
			{
				@Override
				public void onComplete(String result)
				{
					FileInfo fileInfo = FileInfo.createObject().cast();
					fileInfo.setFileData(result);
					fileInfo.setName(file.getName());
					idbCursor.update(file);
				}
			});
		}
		else
		{
			idbCursor.update(file);
		}
    }

	@Override
    public String getKey()
    {
	    return idbCursor.getStringKey();
    }

	@Override
    public File getValue()
    {
		if (usesWebSQL)
		{
		    FileInfo fileInfo = idbCursor.getValue().cast();
			return FileUtils.fromDataURI(fileInfo.getFileData(), fileInfo.getName());
		}
	    return idbCursor.getValue().cast();
    }

	@Override
    public void continueCursor(String key)
    {
		idbCursor.continueCursor(key);
    }
}
