package org.cruxframework.crux.core.server.rest.spi;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.cruxframework.crux.core.server.rest.core.NewCookie;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class HttpResponse
{
	private HttpServletResponse response;
	private int status = 200;
	private HttpServletResponseHeaders outputHeaders;

	public HttpResponse(HttpServletResponse response)
	{
		this.response = response;
		outputHeaders = new HttpServletResponseHeaders(response);
	}

	public int getStatus()
	{
		return status;
	}

	public void setStatus(int status)
	{
		this.status = status;
		this.response.setStatus(status);
	}

	public HttpServletResponseHeaders getOutputHeaders()
	{
		return outputHeaders;
	}

	public OutputStream getOutputStream() throws IOException
	{
		return response.getOutputStream();
	}

	public void addNewCookie(NewCookie cookie)
	{
		Cookie cook = new Cookie(cookie.getName(), cookie.getValue());
		cook.setMaxAge(cookie.getMaxAge());
		cook.setVersion(cookie.getVersion());
		if (cookie.getDomain() != null)
			cook.setDomain(cookie.getDomain());
		if (cookie.getPath() != null)
			cook.setPath(cookie.getPath());
		cook.setSecure(cookie.isSecure());
		if (cookie.getComment() != null)
			cook.setComment(cookie.getComment());
		response.addCookie(cook);
	}

	public void sendError(int status) throws IOException
	{
		response.sendError(status);
	}

	public void sendError(int status, String message) throws IOException
	{
		response.sendError(status, message);
	}

	public boolean isCommitted()
	{
		return response.isCommitted();
	}

	public void reset()
	{
		response.reset();
		outputHeaders = new HttpServletResponseHeaders(response);
	}

	public void setContentLength(int length)
    {
		response.setContentLength(length);
    }

}