package org.cruxframework.crux.core.server.rest.spi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.cruxframework.crux.core.server.rest.core.HttpHeaders;
import org.cruxframework.crux.core.server.rest.core.MediaType;
import org.cruxframework.crux.core.server.rest.core.MultivaluedMap;
import org.cruxframework.crux.core.server.rest.core.MultivaluedMapImpl;
import org.cruxframework.crux.core.server.rest.util.Encode;

/**
 * Abstraction for an inbound http request on the server, or a response from a
 * server to a client
 * <p/>
 * We have this abstraction so that we can reuse marshalling objects in a client
 * framework and serverside framework
 * 
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpRequest
{
	protected HttpHeaders httpHeaders;
	protected HttpServletRequest request;
	protected UriInfo uri;
	protected String httpMethod;
	protected MultivaluedMap<String, String> formParameters;
	protected MultivaluedMap<String, String> decodedFormParameters;
	protected InputStream overridenStream;

	public HttpRequest(HttpServletRequest request, HttpHeaders httpHeaders, UriInfo uri, String httpMethod)
	{
		this.request = request;
		this.httpHeaders = httpHeaders;
		this.httpMethod = httpMethod;
		this.uri = uri;
	}

	public void setRequestUri(URI requestUri) throws IllegalStateException
	{
		uri = uri.relative(requestUri);
	}

	public void setRequestUri(URI baseUri, URI requestUri) throws IllegalStateException
	{
		uri = new UriInfo(baseUri, requestUri);
	}

	public MultivaluedMap<String, String> getPutFormParameters()
	{
		if (formParameters != null)
			return formParameters;
		if (MediaType.APPLICATION_FORM_URLENCODED_TYPE.isCompatible(getHttpHeaders().getMediaType()))
		{
			try
			{
				formParameters = parseForm(getInputStream());
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		else
		{
			throw new IllegalArgumentException("Request media type is not application/x-www-form-urlencoded");
		}
		return formParameters;
	}

	public MultivaluedMap<String, String> getPutDecodedFormParameters()
	{
		if (decodedFormParameters != null)
			return decodedFormParameters;
		decodedFormParameters = Encode.decode(getFormParameters());
		return decodedFormParameters;
	}

	public Object getAttribute(String attribute)
	{
		return request.getAttribute(attribute);
	}

	public void setAttribute(String name, Object value)
	{
		request.setAttribute(name, value);
	}

	public void removeAttribute(String name)
	{
		request.removeAttribute(name);
	}

	@SuppressWarnings("unchecked")
	public Enumeration<String> getAttributeNames()
	{
		return request.getAttributeNames();
	}

	public MultivaluedMap<String, String> getFormParameters()
	{
		if (formParameters != null)
			return formParameters;
		// Tomcat does not set getParameters() if it is a PUT request
		// so pull it out manually
		if (request.getMethod().equals("PUT") && (request.getParameterMap() == null || request.getParameterMap().isEmpty()))
		{
			return getPutFormParameters();
		}
		formParameters = Encode.encode(getDecodedFormParameters());
		return formParameters;
	}

	@SuppressWarnings("unchecked")
    public MultivaluedMap<String, String> getDecodedFormParameters()
	{
		if (decodedFormParameters != null)
			return decodedFormParameters;
		// Tomcat does not set getParameters() if it is a PUT request
		// so pull it out manually
		if (request.getMethod().equals("PUT") && (request.getParameterMap() == null || request.getParameterMap().isEmpty()))
		{
			return getPutDecodedFormParameters();
		}
		decodedFormParameters = new MultivaluedMapImpl<String, String>();
		Map<String, String[]> params = request.getParameterMap();
		for (Map.Entry<String, String[]> entry : params.entrySet())
		{
			String name = entry.getKey();
			String[] values = entry.getValue();
			MultivaluedMap<String, String> queryParams = uri.getQueryParameters();
			List<String> queryValues = queryParams.get(name);
			if (queryValues == null)
			{
				for (String val : values)
					decodedFormParameters.add(name, val);
			}
			else
			{
				for (String val : values)
				{
					if (!queryValues.contains(val))
					{
						decodedFormParameters.add(name, val);
					}
				}
			}
		}
		return decodedFormParameters;

	}

	public HttpHeaders getHttpHeaders()
	{
		return httpHeaders;
	}

	public InputStream getInputStream()
	{
		if (overridenStream != null)
			return overridenStream;
		try
		{
			return request.getInputStream();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void setInputStream(InputStream stream)
	{
		this.overridenStream = stream;
	}

	public UriInfo getUri()
	{
		return uri;
	}

	public String getHttpMethod()
	{
		return httpMethod;
	}

	public void setHttpMethod(String method)
	{
		this.httpMethod = method;
	}

	public boolean isInitial()
	{
		return true;
	}

	protected MultivaluedMap<String, String> parseForm(InputStream entityStream) throws IOException
	{
		char[] buffer = new char[100];
		StringBuffer buf = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(entityStream));

		int wasRead = 0;
		do
		{
			wasRead = reader.read(buffer, 0, 100);
			if (wasRead > 0)
				buf.append(buffer, 0, wasRead);
		}
		while (wasRead > -1);

		String form = buf.toString();

		MultivaluedMap<String, String> formData = new MultivaluedMapImpl<String, String>();
		String[] params = form.split("&");

		for (String param : params)
		{
			if (param.indexOf('=') >= 0)
			{
				String[] nv = param.split("=");
				String val = nv.length > 1 ? nv[1] : "";
				formData.add(nv[0], val);
			}
			else
			{
				formData.add(param, "");
			}
		}
		return formData;
	}

}