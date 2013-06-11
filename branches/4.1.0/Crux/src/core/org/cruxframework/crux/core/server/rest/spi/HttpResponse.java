package org.cruxframework.crux.core.server.rest.spi;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.cruxframework.crux.core.server.rest.core.MediaType;
import org.cruxframework.crux.core.server.rest.util.HttpHeaderNames;

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

	void setStatus(int status)
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

	public void sendError(int status, String message) throws IOException
	{
		response.setStatus(status);
		if (message!= null)
		{
			byte[] responseBytes = message.getBytes("UTF-8");
			response.setContentLength(responseBytes.length);
			outputHeaders.putSingle(HttpHeaderNames.CONTENT_TYPE, new MediaType("text", "plain", "UTF-8"));
			response.getOutputStream().write(responseBytes);
			response.flushBuffer();
		}
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

	public void flushBuffer() throws IOException
	{
		response.flushBuffer();
	}

	void setContentLength(int length)
    {
		response.setContentLength(length);
    }
}