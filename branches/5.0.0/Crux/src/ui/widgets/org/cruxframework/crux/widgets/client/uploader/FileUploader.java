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

import org.cruxframework.crux.core.client.file.File;
import org.cruxframework.crux.core.client.file.FileList;
import org.cruxframework.crux.core.client.file.FileReader;

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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@PartialSupport
public class FileUploader extends Widget
{
	private FlowPanel mainPanel;
	private Label dropArea;
	private FileInput fileInput;
	
	protected FileUploader()
    {
		mainPanel = new FlowPanel();
		mainPanel.setStyleName("crux-FileUploader");
		fileInput = new FileInput();
		fileInput.addChangeHandler(new ChangeHandler()
		{
			@Override
			public void onChange(ChangeEvent event)
			{
				event.stopPropagation();
				event.preventDefault();
				dropArea.removeStyleDependentName("hover");
				processFiles(event.getNativeEvent());
			}
		});
		mainPanel.add(fileInput);
		if (DropEvent.isSupported())
		{
			mainPanel.add(initDropArea());
		}
    }

	public boolean isMultiple()
	{
		return fileInput.isMultiple();
	}
	
	public void setMultiple(boolean multiple)
	{
		fileInput.setMultiple(multiple);
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
			parseFile(files.get(i));
		}
	}

	protected void parseFile(File file)
    {
	    // TODO Auto-generated method stub
	    
    }

	protected void uploadFile(File file)
	{
		
	}
	
	
	private native JsArray<File> getFiles(NativeEvent event)/*-{
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
		return File.isSupported() && FileList.isSupported() && FileReader.isSupported();
	}
}
