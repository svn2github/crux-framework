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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.cruxframework.crux.core.client.file.Blob;
import org.cruxframework.crux.core.client.file.File;
import org.cruxframework.crux.core.client.file.FileList;
import org.cruxframework.crux.core.client.file.FileReader;
import org.cruxframework.crux.core.client.file.FileReader.ReaderStringCallback;
import org.cruxframework.crux.core.client.xhr.XMLHttpRequest2;
import org.cruxframework.crux.widgets.client.button.Button;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;
import org.cruxframework.crux.widgets.client.progressbar.ProgressBar;
import org.cruxframework.crux.widgets.client.uploader.FileUploader.UploadHandler;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.PartialSupport;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

/**
 * An HTML5 based file uploader widget.
 * 
 * @author Thiago da Rosa de Bustamante
 */
@PartialSupport
abstract class AbstractFileUploader extends Composite implements HasEnabled 
{
	public static final String SUPPORTED_IMAGES_MIMETYPES = 
			  "image/jpg,"
			+ "image/jpeg,"
			+ "image/tif,"
			+ "image/gif,"
			+ "image/png,"
			+ "image/raw";
	
	static final int HTTP_STATUS_NON_HTTP = 0;
	static final int HTTP_STATUS_OK = 200;
	static final String HTTP_POST = "POST";

	protected FlowPanel mainPanel;
	protected FlowPanel filesPanel;
	protected FileButton fileInput;
	protected Map<String, Blob> files = new HashMap<String, Blob>();
	protected Map<String, FlowPanel> filePanelWidgets = new HashMap<String, FlowPanel>();
	protected String url;
	protected boolean autoUploadFiles = false;
	protected boolean showProgressBar = true;
	protected Button sendButton;
	
	//TODO: Implement this behavior
	protected boolean enabled = true;
	
	private UploadHandler uploadHandler = null;
	
	/**
	 * Protected Constructor. Use createIfSupported() to instantiate.
	 */
	protected AbstractFileUploader()
	{
		initializeWidgets();
		initWidget(mainPanel);
	}

	protected abstract void initializeWidgets();
	
	public String getUrl()
    {
    	return url;
    }

	public void setFileInputText(String text)
	{
		fileInput.setText(text);
	}
	
	public void setSendButtonText(String text)
	{
		sendButton.setText(text);
	}
	
	public void setUrl(String url)
    {
    	this.url = url;
    }

	public boolean isAutoUploadFiles()
    {
    	return autoUploadFiles;
    }

	public void setAutoUploadFiles(boolean autoUploadFiles)
    {
    	this.autoUploadFiles = autoUploadFiles;
		sendButton.setVisible(!autoUploadFiles);
    }

	public boolean isMultiple()
	{
		return fileInput.isMultiple();
	}

	public void setMultiple(boolean multiple)
	{
		fileInput.setMultiple(multiple);
	}

	public Iterator<Blob> iterateFiles()
	{
		return files.values().iterator();
	}

	public void uploadFile(String fileName)
	{
		uploadFile(fileName, url);
	}

	public void uploadFile(String fileName, String url)
	{
		uploadFile(files.get(fileName), fileName, url);
	}

	public void uploadFile(Blob file, String fileName)
	{
		uploadFile(file, fileName, url);
	}

	public void uploadFile(final Blob file, String fileName, String url)
	{
		if(uploadHandler == null || uploadHandler.onStart(file, fileName))
		{
			XMLHttpRequest2 xhr = getXhr(fileName);
			xhr.open(HTTP_POST, url);
			xhr.send("file", file);
		}
		else if (uploadHandler != null)
 		{
			uploadHandler.onCanceled(fileName);
		}
	}

	public void uploadAllFiles()
	{
		uploadAllFiles(url);
	}
	
	public void uploadAllFiles(String url)
	{
		for (String fileName : files.keySet())
		{
			uploadFile(fileName, url);
		}
	}

	public void removeFile(String fileName)
	{
		removeFile(fileName, true);
	}
	
	public void removeFile(String fileName, boolean fireEvents)
	{
		Widget filePanel = filePanelWidgets.get(fileName);
		if (filePanel != null)
		{
			filePanelWidgets.remove(fileName);
			filePanel.removeFromParent();
			files.remove(fileName);
			if (fireEvents)
			{
				uploadHandler.onFileRemoved(fileName);
			}
		}
	}

	public void clear()
	{
		Iterator<String> keys = files.keySet().iterator();
		while (keys.hasNext())
        {
			String key = keys.next();
			keys.remove();
			Widget filePanel = filePanelWidgets.get(key);
			if (filePanel != null)
			{
				filePanelWidgets.remove(key);
				filePanel.removeFromParent();
			}
        }
	}
	
	public void addFile(Blob file, String fileName)
	{
		processFile(file, fileName);
	}


	public boolean isShowProgressBar() 
	{
		return showProgressBar;
	}

	public void setShowProgressBar(boolean showProgressBar) 
	{
		this.showProgressBar = showProgressBar;
	}
	
	public void setUploadHandler(UploadHandler uploadHandler) {
		this.uploadHandler = uploadHandler;
	}

	public boolean isEnabled() 
	{
		return enabled;
	}
	
	public void setEnabled(boolean enabled) 
	{
		this.enabled = enabled;
	}
	
	protected FlowPanel initFilesPanel()
	{
		FlowPanel filesPanel = new FlowPanel();
		filesPanel.setStyleName("filesArea");
		return filesPanel;
	}

	protected FlowPanel initButtonsPanel()
    {
	    FlowPanel buttonsPanel = new FlowPanel();
		fileInput = initFileInput();
		buttonsPanel.add(fileInput);
		sendButton = initSendButton();
		buttonsPanel.add(sendButton);
	    return buttonsPanel;
    }

	protected Button initSendButton()
    {
		Button sendButton = new Button();
		sendButton.setStyleName("sendButton");
		sendButton.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		sendButton.addSelectHandler(new SelectHandler()
		{
			public void onSelect(SelectEvent event)
			{
				uploadAllFiles();
			}
		});
		return sendButton;
    }

	protected FileButton initFileInput()
	{
		FileButton fileInput = new FileButton();
		fileInput.setStyleName("fileInputButton");
		fileInput.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		fileInput.addChangeHandler(new ChangeHandler()
		{
			public void onChange(ChangeEvent event)
			{
				onSelectFile(event);
			}

		});
		return fileInput;
	}

	protected void onSelectFile(ChangeEvent event)
	{
		event.stopPropagation();
		event.preventDefault();
		processFiles(event.getNativeEvent());
		resetFileInput();
	}

	protected void resetFileInput()
	{
		FlowPanel buttonsPanel = (FlowPanel) fileInput.getParent();
		int index = buttonsPanel.getWidgetIndex(fileInput);
		buttonsPanel.remove(index);
		
		String text = fileInput.getText();
		boolean multiple = fileInput.isMultiple();
		fileInput = initFileInput();
		fileInput.setText(text);
		fileInput.setMultiple(multiple);
		buttonsPanel.insert(fileInput, index);
	}

	protected void processFiles(NativeEvent event)
	{
		JsArray<File> files = getFiles(event);
		if (files.length() > 0 && !isMultiple())
		{
			clear();
		}
		for (int i=0; i< files.length(); i++)
		{
			File file = files.get(i);
			if(uploadHandler != null && !uploadHandler.onFileAdded(file))
			{
				//remove the file
				files.set(i, null);
				continue;
			}
			
			
			boolean processed = processFile(file, file.getName());
			if (processed && isAutoUploadFiles())
			{
				uploadFile(file, file.getName());
			}
		}
	}

	protected boolean processFile(Blob file, String fileName)
	{
		if (!filePanelWidgets.containsKey(fileName))
		{
			if (isMultiple() || files.size() < 1)
			{
				files.put(fileName, file);
				FlowPanel filePanel = createFilePanel(fileName, file);
				filePanelWidgets.put(fileName, filePanel);
				filesPanel.add(filePanel);
				return true;
			}
		}
		return false;
	}

	protected FlowPanel createFilePanel(String fileName, Blob file)
	{
		FlowPanel filePanel = new FlowPanel();
		filePanel.setStyleName("filePanel");
		filePanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		filePanel.add(createDeleteButton(fileName));
		createThumbnailIfSupported(file, filePanel);
		filePanel.add(createNameLabel(fileName));
		if(showProgressBar)
		{
			filePanel.add(createProgressBar());
		}
		return filePanel;
	}

	private void createThumbnailIfSupported(Blob file, final FlowPanel filePanel) {
		FileReader fileReader = FileReader.createIfSupported();
		//if is supported and is image create it, otherwise return.
		if(fileReader == null && !SUPPORTED_IMAGES_MIMETYPES.contains(file.getType()))
		{
			return;
		}
		
		fileReader.readAsDataURL(file, new ReaderStringCallback() {
			public void onComplete(String result) {
				Image image = new Image(result);
				image.setStyleName("thumbnailImage");
				image.getElement().getStyle().setFloat(Float.LEFT);
				filePanel.add(image);
			}
		});
	}

	protected Button createDeleteButton(final String fileName)
	{
		Button delete = new Button();
		delete.setStyleName("cancelUploadButton");
		delete.getElement().getStyle().setFloat(Float.LEFT);
		delete.addSelectHandler(new SelectHandler()
		{
			public void onSelect(SelectEvent event)
			{
				removeFile(fileName);
			}
		});
		return delete;
	}

	protected Label createNameLabel(String fileName)
	{
		Label label = new Label(fileName);
		label.getElement().getStyle().setFloat(Float.LEFT);
		return label;
	}

	protected ProgressBar createProgressBar()
	{
		ProgressBar progressBar = new ProgressBar();
		return progressBar;
	}

	protected ProgressBar getProgressBar(String fileName)
	{
		FlowPanel filePanel = filePanelWidgets.get(fileName);
		ProgressBar progressBar = (ProgressBar) filePanel.getWidget(filePanel.getWidgetCount()-1);
		return progressBar;
	}
	
	protected Button getRemoveButton(String fileName)
	{
		FlowPanel filePanel = filePanelWidgets.get(fileName);
		Button button = (Button) filePanel.getWidget(0);
		return button;
	}

	protected void updateProgressBar(String fileName, double loaded, double total)
	{
		ProgressBar progressBar = getProgressBar(fileName);
		int percentLoaded = (int) Math.round((loaded / total) * 100);
		progressBar.update(percentLoaded);
	}

	protected XMLHttpRequest2 getXhr(final String fileName)
	{
		XMLHttpRequest2 xhr = XMLHttpRequest2.create();
		xhr.setOnProgressHandler(new XMLHttpRequest2.ProgressHandler()
		{
			public void onProgress(double loaded, double total)
			{
				updateProgressBar(fileName, loaded, total);
			}
		});
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler()
		{
			public void onReadyStateChange(XMLHttpRequest xhr)
			{
				if (xhr.getReadyState() == XMLHttpRequest.DONE) 
				{
					xhr.clearOnReadyStateChange();
					if (getBrowserSpecificFailure(xhr) != null)
					{
						uploadError(fileName);
					}
					else
					{
						int status = xhr.getStatus()-200;
						if (status >= 0 && status < 10)
						{
							concludeUpload(fileName);
						}
						else
						{
							uploadError(fileName);
						}
					}
				}
			}

		});
		return xhr;
	}

	protected void uploadError(final String fileName)
	{
		getProgressBar(fileName).setError(true);
		if (uploadHandler != null)
		{
			uploadHandler.onError(fileName);
		}
	}

	protected void concludeUpload(String fileName)
	{
		getProgressBar(fileName).conclude();
		getRemoveButton(fileName).getElement().getStyle().setVisibility(Visibility.HIDDEN);
		if (uploadHandler != null)
 		{
			uploadHandler.onComplete(fileName);
		}
	}

	protected native JsArray<File> getFiles(NativeEvent event)/*-{
		return event.target.files || event.dataTransfer.files;
	}-*/;

	/**
	 * Tests if the JavaScript <code>XmlHttpRequest.status</code> property is
	 * readable. This can return failure in two different known scenarios:
	 * 
	 * <ol>
	 * <li>On Mozilla, after a network error, attempting to read the status code
	 * results in an exception being thrown. See <a
	 * href="https://bugzilla.mozilla.org/show_bug.cgi?id=238559"
	 * >https://bugzilla.mozilla.org/show_bug.cgi?id=238559</a>.</li>
	 * <li>On Safari, if the HTTP response does not include any response text. See
	 * <a
	 * href="http://bugs.webkit.org/show_bug.cgi?id=3810">http://bugs.webkit.org
	 * /show_bug.cgi?id=3810</a>.</li>
	 * </ol>
	 * 
	 * @param xhr the JavaScript <code>XmlHttpRequest</code> object to test
	 * @return a String message containing an error message if the
	 *         <code>XmlHttpRequest.status</code> code is unreadable or null if
	 *         the status code could be successfully read.
	 */
	private native String getBrowserSpecificFailure(XMLHttpRequest xhr) /*-{
	    try {
	      if (xhr.status === undefined) {
	        return "XmlHttpRequest.status == undefined, please see Safari bug " +
	               "http://bugs.webkit.org/show_bug.cgi?id=3810 for more details";
	      }
	      return null;
	    } catch (e) {
	      return "Unable to read XmlHttpRequest.status; likely causes are a " +
	             "networking error or bad cross-domain request. Please see " +
	             "https://bugzilla.mozilla.org/show_bug.cgi?id=238559 for more " +
	             "details";
	    }
	}-*/;

	static boolean isSupported()
	{
		return File.isSupported() && FileList.isSupported() && FileReader.isSupported() && XMLHttpRequest2.isSupported();
	}
}
