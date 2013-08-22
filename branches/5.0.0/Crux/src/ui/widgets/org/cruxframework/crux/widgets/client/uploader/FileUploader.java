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
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@PartialSupport
public class FileUploader extends Composite
{
	static final int HTTP_STATUS_NON_HTTP = 0;
	static final int HTTP_STATUS_OK = 200;
	static final String HTTP_POST = "POST";

	private FlowPanel mainPanel;
	private FlowPanel filesPanel;
	private Label dropArea;
	private FileInput fileInput;
	private List<File> files = new ArrayList<File>();
	private Map<String, FlowPanel> filePanelWidgets = new HashMap<String, FlowPanel>();

	protected FileUploader()
	{
		mainPanel = new FlowPanel();
		mainPanel.setStyleName("crux-FileUploader");
		filesPanel = initFilesPanel();
		mainPanel.add(filesPanel);
		if (DropEvent.isSupported())
		{
			mainPanel.add(initDropArea());
		}
		fileInput = initFileInput();
		mainPanel.add(fileInput);
		initWidget(mainPanel);
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

	public void uploadFile(final File file, String url)
	{
		XMLHttpRequest2 xhr = getXhr(file);
		xhr.open(HTTP_POST, url);
		xhr.send(file);
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

	protected FileInput initFileInput()
	{
		FileInput fileInput = new FileInput();
		fileInput.addChangeHandler(new ChangeHandler()
		{
			@Override
			public void onChange(ChangeEvent event)
			{
				event.stopPropagation();
				event.preventDefault();
				dropArea.removeStyleDependentName("hover");
				processFiles(event.getNativeEvent());
				resetFileInput();
			}
		});
		return fileInput;
	}

	protected void resetFileInput()
	{
		fileInput.removeFromParent();
		fileInput = initFileInput();
		mainPanel.add(fileInput);
	}

	protected Widget initDropArea()
	{
		dropArea = new Label();
		dropArea.setStyleName("dropArea");
		dropArea.addDragOverHandler(new DragOverHandler()
		{
			@Override
			public void onDragOver(DragOverEvent event)
			{
				event.stopPropagation();
				event.preventDefault();
				dropArea.addStyleDependentName("hover");
			}
		});
		dropArea.addDragLeaveHandler(new DragLeaveHandler()
		{
			@Override
			public void onDragLeave(DragLeaveEvent event)
			{
				event.stopPropagation();
				event.preventDefault();
				dropArea.removeStyleDependentName("hover");
			}
		});
		dropArea.addDropHandler(new DropHandler()
		{
			@Override
			public void onDrop(DropEvent event)
			{
				event.stopPropagation();
				event.preventDefault();
				dropArea.removeStyleDependentName("hover");
				processFiles(event.getNativeEvent());
			}
		});
		dropArea.setWidth("100%");
		return dropArea;
	}

	protected void processFiles(NativeEvent event)
	{
		JsArray<File> files = getFiles(event);
		for (int i=0; i< files.length(); i++)
		{
			processFile(files.get(i));
		}
	}

	protected void processFile(File file)
	{
		if (!filePanelWidgets.containsKey(file.getName()))
		{
			files.add(file);
			FlowPanel filePanel = createFilePanel(file);
			filePanelWidgets.put(file.getName(), filePanel);
			filesPanel.add(filePanel);
		}
	}

	protected FlowPanel createFilePanel(final File file)
	{
		FlowPanel filePanel = new FlowPanel();
		filePanel.setStyleName("filePanel");
		filePanel.add(createDeleteButton(file));
		filePanel.add(createNameLabel(file));
		filePanel.add(createProgressBar(file));
		return filePanel;
	}

	protected Button createDeleteButton(final File file)
	{
		Button delete = new Button();
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
		return new Label(file.getName());
	}

	protected ProgressBar createProgressBar(File file)
	{
		ProgressBar progressBar = new ProgressBar();
		progressBar.setHideOnComplete(true);
		return progressBar;
	}

	protected ProgressBar getProgressBar(File file)
	{
		FlowPanel filePanel = filePanelWidgets.get(file.getName());
		ProgressBar progressBar = (ProgressBar) filePanel.getWidget(filePanel.getWidgetCount()-1);
		return progressBar;
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
					if ((xhr.getStatus() == HTTP_STATUS_OK || xhr.getStatus() == HTTP_STATUS_NON_HTTP) 
							&& xhr.getResponseText() != null && xhr.getResponseText().length() != 0)
					{
						getProgressBar(file).conclude();
					}
					else
					{
						getProgressBar(file).setError(true);
					}
				}
			}
		});
		return xhr;
	}

	protected native JsArray<File> getFiles(NativeEvent event)/*-{
		return e.target.files || e.dataTransfer.files;
	}-*/;

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
		return File.isSupported() && FileList.isSupported() && FileReader.isSupported() && XMLHttpRequest2.isSupported();
	}
	
}
