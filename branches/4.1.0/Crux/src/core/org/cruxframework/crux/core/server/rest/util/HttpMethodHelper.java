package org.cruxframework.crux.core.server.rest.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.cruxframework.crux.core.server.rest.annotation.GET;
import org.cruxframework.crux.core.server.rest.annotation.HttpMethod;
import org.cruxframework.crux.core.server.rest.core.dispatch.CacheInfo;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class HttpMethodHelper
{
	/**
	 * 
	 * @param method
	 * @return
	 */
	public static Set<String> getHttpMethods(Method method)
	{
		HashSet<String> methods = new HashSet<String>();
		for (Annotation annotation : method.getAnnotations())
		{
			HttpMethod http = annotation.annotationType().getAnnotation(HttpMethod.class);
			if (http != null)
			{
				methods.add(http.value());
			}
		}
		if (methods.size() == 0)
		{
			return null;
		}
		return methods;
	}
	
	/**
	 * 
	 * @param method
	 * @return
	 */
	public static CacheInfo getCacheInfoForGET(Method method)
	{
		GET get = method.getAnnotation(GET.class);
		if (get != null)
		{
			return CacheInfo.parseCacheInfo(get);
		}
		
		return null;
	}
}