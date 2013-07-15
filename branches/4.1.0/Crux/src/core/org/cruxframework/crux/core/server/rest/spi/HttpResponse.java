package org.cruxframework.crux.core.server.rest.spi;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletResponse;

import org.cruxframework.crux.core.server.rest.core.MediaType;
import org.cruxframework.crux.core.server.rest.util.HttpHeaderNames;
import org.cruxframework.crux.core.server.rest.util.JsonUtil;

import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class HttpResponse
{
	private static final Lock lock = new ReentrantLock();

	private HttpServletResponse response;
	private int status = 200;
	private HttpServletResponseHeaders outputHeaders;
	private static ObjectWriter exceptionDataWriter;

	public HttpResponse(HttpServletResponse response)
	{
		this.response = response;
		outputHeaders = new HttpServletResponseHeaders(response);
		initializeExceptionWriter();
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
	
	public void sendException(int status, String message) throws IOException
	{
		ExceptionData exceptionData = new ExceptionData(message);
		sendError(status, exceptionDataWriter.writeValueAsString(exceptionData));
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
	
	static class ExceptionData
	{
		private String message;

		public ExceptionData(String message)
		{
			this.message = message;
		}

		public String getMessage()
        {
        	return message;
        }
	}

	private void initializeExceptionWriter()
    {
	    if (exceptionDataWriter == null)
		{
			lock.lock();
			try
			{
				if (exceptionDataWriter == null)
				{
					exceptionDataWriter = JsonUtil.createWriter(ExceptionData.class);
				}
			}
			finally
			{
				lock.unlock();
			}
		}
    }
}