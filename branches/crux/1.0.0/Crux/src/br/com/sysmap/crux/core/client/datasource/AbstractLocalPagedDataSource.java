/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.client.datasource;



/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
abstract class AbstractLocalPagedDataSource<R extends DataSourceRecord, E> extends AbstractLocalScrollableDataSource<R,E> 
                                              implements PagedDataSource<R>
{
	protected int pageSize = 10;
	protected int currentPage = 0;
	
	public int getCurrentPage()
	{
		return currentPage;
	}

	public int getPageCount()
	{
		int numRecords = getRecordCount();
		int pageSize = getPageSize();
		
		return (numRecords / pageSize) + (numRecords%pageSize==0?0:1);
	}

	public int getPageSize()
	{
		return pageSize;
	}

	/**
	 * @see br.com.sysmap.crux.core.client.datasource.PagedDataSource#getCurrentPageSize()
	 */
	public int getCurrentPageSize()
	{
		return getPageEndRecord() - getPageStartRecord() + 1;
	}
	
	public boolean hasNextPage()
	{
		ensureLoaded();
		return (currentPage < getPageCount());
	}

	public boolean hasPreviousPage()
	{
		ensureLoaded();
		return (currentPage > 1 );
	}

	public boolean nextPage()
	{
		if (hasNextPage())
		{
			currentPage++;
			updateCurrentRecord();
			return true;
		}	
		return false;
	}

	public boolean previousPage()
	{
		if (hasPreviousPage())
		{
			currentPage--;
			updateCurrentRecord();
			return true;
		}
		return false;
	}

	public boolean setCurrentPage(int pageNumber)
	{
		ensureLoaded();
		if (pageNumber > 0 && pageNumber <= getPageCount())
		{
			currentPage = pageNumber;
			updateCurrentRecord();
			return true;
		}
		return false;
	}

	public void setPageSize(int pageSize)
	{
		if (pageSize < 1)
		{
			pageSize = 1;
		}
		this.pageSize = pageSize;
		if (this.loaded)
		{
			updateCurrentRecord();
		}
	}
	
	@Override
	public boolean hasNextRecord()
	{
		return isRecordOnPage(currentRecord+1);
	}
	
	@Override
	public boolean hasPreviousRecord()
	{
		return isRecordOnPage(currentRecord-1);
	}
	
	@Override
	public void reset()
	{
		super.reset();
		currentPage = 0;
	}	

	@Override
	public void firstRecord()
	{
		ensureLoaded();
		currentRecord = getPageStartRecord();
	}

	@Override
	public void lastRecord()
	{
		ensureLoaded();
		currentRecord = getPageEndRecord();
	}

	protected boolean isRecordOnPage(int record)
	{
		ensureLoaded();
		if (data == null)
		{
			return false;
		}
		int startPageRecord = getPageStartRecord();
		int endPageRescord = getPageEndRecord();
		if (endPageRescord >= data.length)
		{
			endPageRescord = data.length-1;
		}
		return (record >= startPageRecord && record <= endPageRescord);
	}

	protected int getPageEndRecord()
	{
		return (currentPage * pageSize) - 1;
	}

	protected int getPageStartRecord()
	{
		return (currentPage - 1) * pageSize;
	}
	
	protected void updateCurrentRecord()
	{
		currentRecord = getPageStartRecord(); 
	}
}
