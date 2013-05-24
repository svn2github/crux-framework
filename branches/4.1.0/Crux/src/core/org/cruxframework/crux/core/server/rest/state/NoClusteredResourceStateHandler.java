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
package org.cruxframework.crux.core.server.rest.state;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;



/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class NoClusteredResourceStateHandler implements ResourceStateHandler
{
	public static class LRUMap<K, V> extends LinkedHashMap<K, V>
	{
        private static final long serialVersionUID = -8939312812258005339L;
		private final int maxEntries;

        public LRUMap(int maxEntries)
        {
        	super(maxEntries, 0.75f, true);
			this.maxEntries = maxEntries;
        }
        
		@Override
		protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest)
		{
		    return super.size() > maxEntries;
		}
	}
	
	public static class CacheEntry implements ResourceState
	{
		private final long dateModifiedMilis;
		private final long expires;
		private final String etag;

		private CacheEntry(long dateModifiedMilis, long expires, String etag)
		{
			this.dateModifiedMilis = dateModifiedMilis;
			this.expires = expires;
			this.etag = etag;
		}
		
		@Override
        public long getDateModified()
        {
	        return dateModifiedMilis;
        }

		@Override
        public String getEtag()
        {
	        return etag;
        }

		@Override
        public boolean isExpired()
        {
	        return System.currentTimeMillis() - dateModifiedMilis >= expires;
        }
	}
	
	private Map<String, CacheEntry> cache;
	
	/**
	 * 
	 */
	public NoClusteredResourceStateHandler(int maxCacheItems)
    {
		cache = Collections.synchronizedMap(new LRUMap<String, CacheEntry>(maxCacheItems));
    }
	
	@Override
    public ResourceState add(String uri, long dateModified, long expires, String etag)
    {
		CacheEntry cacheEntry = new CacheEntry(dateModified, expires, etag);
		cache.put(uri, cacheEntry);
	    return cacheEntry;
    }

	@Override
    public ResourceState get(String uri)
    {
	    return cache.get(uri);
    }

	@Override
    public void remove(String uri)
    {
		cache.remove(uri);
    }

	@Override
    public void clear()
    {
		cache.clear();
    }
}

//
//
//
//package org.jboss.resteasy.plugins.cache.server;
//
//
//import javax.ws.rs.core.CacheControl;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.MultivaluedMap;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * An HTTP cache that behaves somewhat the same way as a proxy (like Squid)
// *
// * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
// * @version $Revision: 1 $
// */
//@Deprecated
//public class NoClusteredResourceStateHandler implements ResourceStateHandler
//{
//   public static class CacheEntry implements Entry
//   {
//      private final byte[] cached;
//      private final int expires;
//      private final long timestamp = System.currentTimeMillis();
//      private final MultivaluedMap<String, Object> headers;
//      private String etag;
//
//      private CacheEntry(MultivaluedMap<String, Object> headers, byte[] cached, int expires, String etag)
//      {
//         this.cached = cached;
//         this.expires = expires;
//         this.headers = headers;
//         this.etag = etag;
//      }
//
//      public int getExpirationInSeconds()
//      {
//         return expires - (int) ((System.currentTimeMillis() - timestamp) / 1000);
//      }
//
//      public boolean isExpired()
//      {
//         return System.currentTimeMillis() - timestamp >= expires * 1000;
//      }
//
//      public String getEtag()
//      {
//         return etag;
//      }
//
//      public MultivaluedMap<String, Object> getHeaders()
//      {
//         return headers;
//      }
//
//      public byte[] getCached()
//      {
//         return cached;
//      }
//
//   }
//
//
//   private Map<String, Map<MediaType, CacheEntry>> cache = new ConcurrentHashMap<String, Map<MediaType, CacheEntry>>();
//
//   public Entry get(String uri, MediaType accept)
//   {
//      Map<MediaType, CacheEntry> entry = cache.get(uri);
//      if (entry == null || entry.isEmpty()) return null;
//      for (Map.Entry<MediaType, CacheEntry> produce : entry.entrySet())
//      {
//         if (accept.isCompatible(produce.getKey())) return produce.getValue();
//      }
//      return null;
//   }
//
//   public Entry add(String uri, MediaType mediaType, CacheControl cc, MultivaluedMap<String, Object> headers, byte[] entity, String etag)
//   {
//      CacheEntry cacheEntry = new CacheEntry(headers, entity, cc.getMaxAge(), etag);
//      Map<MediaType, CacheEntry> entry = cache.get(uri);
//      if (entry == null)
//      {
//         entry = new ConcurrentHashMap<MediaType, CacheEntry>();
//         cache.put(uri, entry);
//      }
//      entry.put(mediaType, cacheEntry);
//      return cacheEntry;
//   }
//
//   public void remove(String key)
//   {
//      cache.remove(key);
//   }
//
//   public void clear()
//   {
//      cache.clear();
//   }
//
//}