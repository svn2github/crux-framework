package br.com.sysmap.crux.scannotation.archiveiterator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import br.com.sysmap.crux.classpath.URLResourceHandler;
import br.com.sysmap.crux.classpath.URLResourceHandlersRegistry;


/**
 * @author Thiago da Rosa de Bustamante - <code>thiago@sysmap.com.br</code>
 *
 */
public class ZIPProtocolIterator implements URLIterator
{
	protected ZipInputStream zipStream;
	protected ZipEntry next;
	protected Filter filter;
	protected String pathInZip;
	protected boolean initial = true;
	protected boolean closed = false;
	protected int pahtJarLength = 0;
	protected URL zip;

	public ZIPProtocolIterator(URL zip, Filter filter, String pathInZip) throws IOException, URISyntaxException
	{
		if (zip.toString().startsWith("file:"))
		{
			this.zip = toZipURL(zip);
			this.zipStream = new ZipInputStream(new FileInputStream(new File(zip.toURI())));
		}
		else
		{
			this.zip = zip;
			this.zipStream = new ZipInputStream(zip.openStream());
		}
		this.filter = filter;
		this.pathInZip = (pathInZip==null?"":pathInZip);
		if (this.pathInZip.startsWith("/"))
		{
			this.pathInZip = this.pathInZip.substring(1);
		}
		this.pahtJarLength = this.pathInZip.length();

	}

	protected ZIPProtocolIterator()
	{
		
	}
	
	private void setNext()
	{
		initial = true;
		try
		{
			if (next != null) zipStream.closeEntry();
			next = null;
			do
			{
				next = zipStream.getNextEntry();
			} while (next != null && (next.isDirectory() || filter == null || !next.getName().startsWith(pathInZip) || !filter.accepts(next.getName().substring(pahtJarLength))));
			if (next == null)
			{
				close();
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException("failed to browse jar", e);
		}
	}

	public URL next()
	{
		if (closed || (next == null && !initial)) return null;
		setNext();
		if (next == null) return null;
		URLResourceHandler handler = URLResourceHandlersRegistry.getURLResourceHandler(getProtocol());
		return handler.getChildResource(zip, next.getName());
	}

	protected String getProtocol()
	{
		return "zip";
	}

	public void close()
	{
		try
		{
			closed = true;
			zipStream.close();
		}
		catch (IOException ignored)
		{

		}

	}

	private URL toZipURL(URL fileURL) throws MalformedURLException
	{
		return new URL("zip:"+fileURL.toString().substring(5)+"!/");
	}	
}
