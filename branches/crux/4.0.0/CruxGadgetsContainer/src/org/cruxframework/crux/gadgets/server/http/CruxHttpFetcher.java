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
package org.cruxframework.crux.gadgets.server.http;

import javax.annotation.Nullable;

import org.apache.shindig.gadgets.http.BasicHttpFetcher;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Singleton
public class CruxHttpFetcher extends BasicHttpFetcher
{
	  private static final int DEFAULT_CONNECT_TIMEOUT_MS = 5000;
	  private static final int DEFAULT_READ_TIMEOUT_MS = 60000;
	  private static final int DEFAULT_MAX_OBJECT_SIZE = 0;  // no limit

	/**
	 * 
	 * @param maxObjSize
	 * @param connectionTimeoutMs
	 * @param readTimeoutMs
	 * @param basicHttpFetcherProxy
	 */
	public CruxHttpFetcher(int maxObjSize, int connectionTimeoutMs, int readTimeoutMs, String basicHttpFetcherProxy)
    {
	    super(maxObjSize, connectionTimeoutMs, readTimeoutMs, basicHttpFetcherProxy);
    }

	/**
	 * 
	 * @param basicHttpFetcherProxy
	 */
	@Inject
	public CruxHttpFetcher(@Nullable @Named("org.apache.shindig.gadgets.http.basicHttpFetcherProxy")
						   String basicHttpFetcherProxy)
    {
	    this(DEFAULT_MAX_OBJECT_SIZE, DEFAULT_CONNECT_TIMEOUT_MS, DEFAULT_READ_TIMEOUT_MS, basicHttpFetcherProxy);
	}
}
