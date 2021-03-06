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
package org.cruxframework.crux.core.client.datasource;

import java.util.ArrayList;
import java.util.List;

import org.cruxframework.crux.core.client.datasource.DataSourceRecord.DataSourceRecordState;



/**
 * @author Thiago da Rosa de Bustamante
 *
 * 
 */

class DataSourceOperations<E>
{
	protected List<DataSourceRecord<E>> newRecords = new ArrayList<DataSourceRecord<E>>();
	protected List<DataSourceRecord<E>> removedRecords = new ArrayList<DataSourceRecord<E>>();
	protected List<DataSourceRecord<E>> changedRecords = new ArrayList<DataSourceRecord<E>>();
	protected List<DataSourceRecord<E>> selectedRecords = new ArrayList<DataSourceRecord<E>>();	

	protected AbstractScrollableDataSource<E> dataSource;
	
	public DataSourceOperations(AbstractScrollableDataSource<E> DataSource)
	{
		this.dataSource = DataSource;
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#insertRecord(int)
	 */
    public DataSourceRecord<E> insertRecord(int index)
	{
		this.dataSource.ensureLoaded();
		checkRange(index);
		DataSourceRecord<E> record = new DataSourceRecord<E>((DataSource<E>)this.dataSource, 
																		"_newRecord"+newRecords.size());
		record.setCreated(true);
		insertDataRecord(index, record);
		newRecords.add(record);
		return record;
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#removeRecord(int)
	 */
	public DataSourceRecord<E> removeRecord(int index)
	{
		this.dataSource.ensureLoaded();
		checkRange(index);
		DataSourceRecord<E> record = this.dataSource.data[index];
		DataSourceRecordState previousState = record.getCurrentState();
		record.setRemoved(true);
		removeDataRecord(index);
		updateState(record, previousState);
		return record;
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#updateState(org.cruxframework.crux.core.client.datasource.DataSourceRecord, org.cruxframework.crux.core.client.datasource.DataSourceRecord.DataSourceRecordState)
	 */
	public void updateState(DataSourceRecord<E> record, DataSourceRecordState previousState)
	{
		this.dataSource.ensureLoaded();
		if (record.isCreated())
		{
			if (record.isRemoved())
			{
				newRecords.remove(record);
			}
		}
		else if (record.isRemoved())
		{
			if (!previousState.isRemoved())
			{
				removedRecords.add(record);
				if (previousState.isDirty())
				{
					changedRecords.remove(record);
				}
			}
		}
		else if (record.isDirty() && !previousState.isDirty())
		{
			changedRecords.add(record);
		}
		
		if (record.isSelected() && !previousState.isSelected())
		{
			selectedRecords.add(record);
		}
		else if (!record.isSelected() && previousState.isSelected())
		{
			selectedRecords.remove(record);
		}
	}
	
	/**
	 * @param index
	 * @param record
	 */
	protected void insertDataRecord(int index, DataSourceRecord<E> record)
	{
		int size = this.dataSource.data.length;
		this.dataSource.data = copyOf(this.dataSource.data, size+1);
		System.arraycopy(this.dataSource.data, index, this.dataSource.data, index + 1, size - index);
		this.dataSource.data[index] = record;
	}
	
	/**
	 * @param index
	 */
	protected void removeDataRecord(int index)
	{
		int size = this.dataSource.data.length;
		int numMoved = size - index - 1;
		if (numMoved > 0)
		    System.arraycopy(this.dataSource.data, index+1, this.dataSource.data, index, numMoved);
		this.dataSource.data = copyOf(this.dataSource.data, size-1);
	}

	/**
	 * 
	 * @param original
	 * @param newLength
	 * @return
	 */
	@SuppressWarnings("unchecked")
    protected DataSourceRecord<E>[] copyOf(DataSourceRecord<E>[] original, int newLength)
	{
		DataSourceRecord<E>[] copy = new DataSourceRecord[newLength];
		System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
		return copy;
	}
	
	/**
	 * @param index
	 */
	protected void checkRange(int index)
	{
		if (index < 0 || index > this.dataSource.data.length)
		{
			throw new IndexOutOfBoundsException();
		}
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getNewRecords()
	 */
	@SuppressWarnings("unchecked")
    public DataSourceRecord<E>[] getNewRecords()
	{
		return newRecords.toArray(new DataSourceRecord[0]);
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getRemovedRecords()
	 */
	@SuppressWarnings("unchecked")
    public DataSourceRecord<E>[] getRemovedRecords()
	{
		return removedRecords.toArray(new DataSourceRecord[0]);
	}
	
	/**
	 * @return
	 */
	public int getNewRecordsCount()
	{
		return newRecords.size();
	}

	/**
	 * @return
	 */
	public int getRemovedRecordsCount()
	{
		return removedRecords.size();
	}

	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getUpdatedRecords()
	 */
	@SuppressWarnings("unchecked")
    public DataSourceRecord<E>[] getUpdatedRecords()
	{
		return changedRecords.toArray(new DataSourceRecord[0]);
	}
	
	/**
	 * @see org.cruxframework.crux.core.client.datasource.DataSource#getSelectedRecords()
	 */
	@SuppressWarnings("unchecked")
    public DataSourceRecord<E>[] getSelectedRecords()
	{
		return selectedRecords.toArray(new DataSourceRecord[0]);
	}
	
	public int getRecordIndex(E boundObject)
	{
		for(int i = 0; i < this.dataSource.data.length; i++)
		{
			if(this.dataSource.data[i].recordObject.equals(boundObject))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public void selectRecord(int index, boolean selected)
	{
		checkRange(index);
		this.dataSource.data[index].setSelected(selected);
	}
	
	public void reset()
	{
		newRecords.clear();
		removedRecords.clear();
		changedRecords.clear();
		selectedRecords.clear();
	}
	
	public boolean isDirty()
	{
		return (newRecords.size() > 0) || (removedRecords.size() > 0) || (changedRecords.size() > 0); 
	}
}
