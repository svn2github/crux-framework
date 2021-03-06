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
 * @author Thiago da Rosa de Bustamante
 * @deprecated Use RemotePagedDataSource
 */
@Deprecated
public abstract class RemoteBindablePagedDataSource<T> extends RemotePagedDataSource<T>
{
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#getBoundObject()
	 */
	public T getBoundObject()
	{
		return super.getBoundObject(getRecord());
	}
	
	/**
	 * @see br.com.sysmap.crux.core.client.datasource.DataSource#getBoundObject(br.com.sysmap.crux.core.client.datasource.DataSourceRecord)
	 */
	public T getBoundObject(DataSourceRecord<T> record)
	{
		return super.getBoundObject(record);
	}
}
