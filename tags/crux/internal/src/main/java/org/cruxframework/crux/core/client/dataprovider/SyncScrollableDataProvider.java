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
package org.cruxframework.crux.core.client.dataprovider;

import org.cruxframework.crux.core.client.dataprovider.DataProviderRecord.DataProviderRecordState;

/**
 * @author Thiago da Rosa de Bustamante
 */
public abstract class SyncScrollableDataProvider<T> extends AbstractScrollableDataProvider<T> implements SyncDataProvider<T>
{
	protected SyncDataProviderCallback synchronousCallback = null;
	protected DataProviderOperations<T> operations = new DataProviderOperations<T>(this);

	@Override
	public void clearChanges()
	{
		this.operations.reset();
	}
	
	@Override
	public DataProviderRecord<T>[] getNewRecords()
	{
		return operations.getNewRecords();
	}
	
	@Override
	public DataProviderRecord<T>[] getRemovedRecords()
	{
		return operations.getRemovedRecords();
	}	
	
	@Override
	public DataProviderRecord<T>[] getSelectedRecords()
	{
		return operations.getSelectedRecords();
	}

	@Override
	public DataProviderRecord<T>[] getUpdatedRecords()
	{
		return operations.getUpdatedRecords();
	}

	@Override
	public DataProviderRecord<T> insertRecord(int index)
	{
		return operations.insertRecord(index);
	}

	@Override
	public DataProviderRecord<T> removeRecord(int index)
	{
		return operations.removeRecord(index);
	}

	@Override
	public void reset()
	{
		super.reset();
		operations.reset();
	}

	@Override
	public void setCallback(SyncDataProviderCallback callback)
	{
		this.synchronousCallback = callback;
	}

	@Override
	public void selectRecord(int index, boolean selected)
	{
		operations.selectRecord(index, selected);
	}
	
	@Override
	public int getIndex(T boundObject)
	{
		return operations.getRecordIndex(boundObject);
	}
	
	@Override
	public void updateState(DataProviderRecord<T> record, DataProviderRecordState previousState)
	{
		operations.updateState(record, previousState);
	}
	
	@Override
	protected void update(DataProviderRecord<T>[] records)
	{
		loaded = true;
		this.data = records;
		if (this.synchronousCallback != null)
		{
			this.synchronousCallback.onLoaded();
		}
	}
	
	protected SynchronousDataProviderEvent<T> createSynchronousDataProviderEvent()
	{
		return new SynchronousDataProviderEvent<T>(this);
	}
}