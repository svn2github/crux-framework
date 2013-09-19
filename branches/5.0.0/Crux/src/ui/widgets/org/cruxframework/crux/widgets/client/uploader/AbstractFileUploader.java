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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cruxframework.crux.core.client.file.File;
import org.cruxframework.crux.core.client.file.FileList;
import org.cruxframework.crux.core.client.file.FileReader;
import org.cruxframework.crux.core.client.xhr.XMLHttpRequest2;
import org.cruxframework.crux.widgets.client.button.Button;
import org.cruxframework.crux.widgets.client.event.SelectEvent;
import org.cruxframework.crux.widgets.client.event.SelectHandler;
import org.cruxframework.crux.widgets.client.progressbar.ProgressBar;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.PartialSupport;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
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
abstract class AbstractFileUploader extends Composite
{
	static final int HTTP_STATUS_NON_HTTP = 0;
	static final int HTTP_STATUS_OK = 200;
	static final String HTTP_POST = "POST";

	protected FlowPanel mainPanel;
	protected FlowPanel filesPanel;
	protected FileButton fileInput;
	protected List<File> files = new ArrayList<File>();
	protected Map<String, FlowPanel> filePanelWidgets = new HashMap<String, FlowPanel>();
	protected String url;
	protected boolean autoUploadFiles = false;
	protected Button sendButton;
	
	private ClientProcessFileHandler clientProcessFileHandler;
	
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

	public Iterator<File> iterateFiles()
	{
		return files.iterator();
	}

	public void uploadFile(File file)
	{
		if(clientProcessFileHandler == null || clientProcessFileHandler.process(file))
		{
			uploadFile(file, url);
		}
	}
	
	public static interface ClientProcessFileHandler
	{
		boolean process(File file);
	}
	
	public void setClientProcessFileHandler(ClientProcessFileHandler clientProcessFileHandler) 
	{
		this.clientProcessFileHandler = clientProcessFileHandler;
	}
	
	public void uploadFile(final File file, String url)
	{
		XMLHttpRequest2 xhr = getXhr(file);
		xhr.open(HTTP_POST, url);
		xhr.send("file", file);
	}

	public void uploadAllFiles()
	{
		uploadAllFiles(url);
	}
	
	public void uploadAllFiles(String url)
	{
		for (File file : files)
		{
			uploadFile(file, url);
		}
	}

	public void removeFile(File file)
	{
		Widget filePanel = filePanelWidgets.get(file.getName());
		if (filePanel != null)
		{
			filePanelWidgets.remove(file.getName());
			filePanel.removeFromParent();
			files.remove(file);
		}
	}

	public void clear()
	{
		while (files.size() > 0)
        {
	        removeFile(files.get(0));
        }
	}
	
	public void addFile(File file)
	{
		processFile(file);
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
			@Override
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
			@Override
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
			boolean processed = processFile(file);
			if (processed && isAutoUploadFiles())
			{
				uploadFile(file);
			}
		}
	}

	protected boolean processFile(File file)
	{
		if (!filePanelWidgets.containsKey(file.getName()))
		{
			if (isMultiple() || files.size() < 1)
			{
				files.add(file);
				FlowPanel filePanel = createFilePanel(file);
				filePanelWidgets.put(file.getName(), filePanel);
				filesPanel.add(filePanel);
				return true;
			}
		}
		return false;
	}

	protected FlowPanel createFilePanel(final File file)
	{
		FlowPanel filePanel = new FlowPanel();
		filePanel.setStyleName("filePanel");
		filePanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		filePanel.setWidth("100%");
		filePanel.add(createDeleteButton(file));
		filePanel.add(createNameLabel(file));
		filePanel.add(createProgressBar(file));
		return filePanel;
	}

	protected Button createDeleteButton(final File file)
	{
		Button delete = new Button();
		delete.setStyleName("cancelUploadButton");
		delete.getElement().getStyle().setFloat(Float.LEFT);
		delete.addSelectHandler(new SelectHandler()
		{
			@Override
			public void onSelect(SelectEvent event)
			{
				removeFile(file);
			}
		});
		return delete;
	}

	protected Label createNameLabel(final File file)
	{
		Label label = new Label(file.getName());
		label.getElement().getStyle().setFloat(Float.LEFT);
		return label;
	}

	protected ProgressBar createProgressBar(File file)
	{
		ProgressBar progressBar = new ProgressBar();
		return progressBar;
	}

	protected ProgressBar getProgressBar(File file)
	{
		FlowPanel filePanel = filePanelWidgets.get(file.getName());
		ProgressBar progressBar = (ProgressBar) filePanel.getWidget(filePanel.getWidgetCount()-1);
		return progressBar;
	}
	
	protected Button getRemoveButton(File file)
	{
		FlowPanel filePanel = filePanelWidgets.get(file.getName());
		Button button = (Button) filePanel.getWidget(0);
		return button;
	}

	protected void updateProgressBar(File file, double loaded, double total)
	{
		ProgressBar progressBar = getProgressBar(file);
		int percentLoaded = (int) Math.round((loaded / total) * 100);
		progressBar.update(percentLoaded);
	}

	protected XMLHttpRequest2 getXhr(final File file)
	{
		XMLHttpRequest2 xhr = XMLHttpRequest2.create();
		xhr.setOnProgressHandler(new XMLHttpRequest2.ProgressHandler()
		{
			@Override
			public void onProgress(double loaded, double total)
			{
				updateProgressBar(file, loaded, total);
			}
		});
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler()
		{
			@Override
			public void onReadyStateChange(XMLHttpRequest xhr)
			{
				if (xhr.getReadyState() == XMLHttpRequest.DONE) 
				{
					xhr.clearOnReadyStateChange();
					if (getBrowserSpecificFailure(xhr) != null)
					{
						getProgressBar(file).setError(true);
					}
					else
					{
						int status = xhr.getStatus()-200;
						if (status >= 0 && status < 10)
						{
							concludeUpload(file);
						}
						else
						{
							getProgressBar(file).setError(true);
						}
					}
				}
			}
		});
		return xhr;
	}

	protected void concludeUpload(File file)
	{
		getProgressBar(file).conclude();
		getRemoveButton(file).getElement().getStyle().setVisibility(Visibility.HIDDEN);
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
