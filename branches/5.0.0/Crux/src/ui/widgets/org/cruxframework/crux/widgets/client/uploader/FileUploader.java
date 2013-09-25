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

import org.cruxframework.crux.core.client.file.Blob;

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
	public static interface UploadHandler
	{
		/**
		 * Action to be executed before the file upload start.
		 * @param file the file submitted
		 * @param fileName the fileName submitted
		 * @return true if client wants to upload the file to server and false otherwise.
		 */
		boolean onStart(Blob file, String fileName);
		
		/**
		 * Action to be executed after the client has processed the file.
		 * @param fileName the fileName submitted
		 */
		void onComplete(String fileName);
		
		/**
		 * Action to be executed after the client has removed the file.
		 * @param fileName the fileName submitted
		 */
		void onFileRemoved(String fileName);
		
		/**
		 * Action to be executed when the file upload is aborted (by onStart method).
		 * @param fileName the fileName submitted
		 */
		void onCanceled(String fileName);

		/**
		 * Action to be executed when an error occurs during the file upload.
		 * @param fileName the fileName submitted
		 */
		void onError(String fileName);
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

	public Iterator<Blob> iterateFiles()
	{
		return impl.iterateFiles();
	}

	public void uploadFile(String fileName)
	{
		impl.uploadFile(fileName);
	}

	public void uploadFile(String fileName, String url)
	{
		impl.uploadFile(fileName, url);
	}

	public void uploadFile(Blob file, String fileName)
	{
		impl.uploadFile(file, fileName);
	}

	public void uploadFile(final Blob file, String fileName, String url)
	{
		impl.uploadFile(file, fileName, url);
	}

	public void uploadAllFiles()
	{
		impl.uploadAllFiles();
	}
	
	public void uploadAllFiles(String url)
	{
		impl.uploadAllFiles(url);
	}

	public void removeFile(String fileName)
	{
		impl.removeFile(fileName);
	}

	public void clear()
	{
		impl.clear();
	}
	
	public void addFile(Blob file, String fileName)
	{
		impl.addFile(file, fileName);
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
	
	public void setUploadHandler(UploadHandler uploadHandler) 
	{
		impl.setUploadHandler(uploadHandler);
	}
	
	public boolean isShowProgressBar() 
	{
		return impl.isShowProgressBar();
	}

	public void setShowProgressBar(boolean showProgressBar) 
	{
		impl.setShowProgressBar(showProgressBar);
	}
}
//TODO tratar resubimssao.... marcar arquivos como enviados...
//TODO por a url como required na factory