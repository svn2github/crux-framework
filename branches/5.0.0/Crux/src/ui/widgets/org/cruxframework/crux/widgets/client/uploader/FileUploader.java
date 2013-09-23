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
package org.cruxframework.crux.widgets.client.uploader;

import java.util.Iterator;

import org.cruxframework.crux.core.client.file.File;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.PartialSupport;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;

/**
 * An HTML5 based file uploader widget.
 * 
 * @author Thiago da Rosa de Bustamante
 */
@PartialSupport
public class FileUploader extends Composite implements HasEnabled 
{
	private AbstractFileUploader impl;
	
	/**
	 * @author samuel.cardoso
	 * If client wants to process any kind of information before file upload.
	 */
	public static interface ClientSendFileHandler
	{
		/**
		 * @param file the file submitted
		 * @return true if client wants to upload the file to server and false otherwise.
		 */
		boolean onStart(File file);
		
		/**
		 * Action to be executed after the client has processed the file.
		 */
		void onComplete();
	}
	
	/**
	 * Protected Constructor. Use createIfSupported() to instantiate.
	 */
	protected FileUploader()
	{
		impl = GWT.create(AbstractFileUploader.class);
		initWidget(impl);
	}

	public String getUrl()
    {
    	return impl.getUrl();
    }

	public void setUrl(String url)
	{
		impl.setUrl(url);
	}
	
	public void setFileInputText(String text)
	{
		impl.setFileInputText(text);
	}
	
	public void setSendButtonText(String text)
	{
		impl.setSendButtonText(text);
	}
	
	public boolean isAutoUploadFiles()
    {
    	return impl.isAutoUploadFiles();
    }

	public void setAutoUploadFiles(boolean autoUploadFiles)
    {
		impl.setAutoUploadFiles(autoUploadFiles);
    }

	public boolean isMultiple()
	{
		return impl.isMultiple();
	}

	public void setMultiple(boolean multiple)
	{
		impl.setMultiple(multiple);
	}

	public Iterator<File> iterateFiles()
	{
		return impl.iterateFiles();
	}

	public void uploadFile(File file)
	{
		impl.uploadFile(file);
	}

	public void uploadFile(final File file, String url)
	{
		impl.uploadFile(file, url);
	}

	public void uploadAllFiles()
	{
		impl.uploadAllFiles();
	}
	
	public void uploadAllFiles(String url)
	{
		impl.uploadAllFiles(url);
	}

	public void removeFile(File file)
	{
		impl.removeFile(file);
	}

	public void clear()
	{
		impl.clear();
	}
	
	public void addFile(File file)
	{
		impl.addFile(file);
	}

	public static FileUploader createIfSupported()
	{
		if (isSupported())
		{
			return new FileUploader();
		}
		return null;
	}

	public static boolean isSupported()
	{
		return AbstractFileUploader.isSupported();
	}
	
	public boolean isEnabled() 
	{
		return impl.isEnabled();
	}

	public void setEnabled(boolean enabled) 
	{
		impl.setEnabled(enabled);
	}
	
	public void setClientSendFileHandler(ClientSendFileHandler clientSendFileHandler) {
		impl.setClientSendFileHandler(clientSendFileHandler);
	}
}
//TODO tratar resubimssao.... marcar arquivos como enviados...
//TODO por a url como required na factory