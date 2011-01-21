/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.gwt.client;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.HasChangeHandlersFactory;
import br.com.sysmap.crux.core.rebind.widget.creator.HasNameFactory;

import com.google.gwt.user.client.ui.FileUpload;

/**
 * Represents a FileUploadFactory component.
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="fileUpload", library="gwt")
public class FileUploadFactory extends WidgetCreator<FileUpload, WidgetCreatorContext> 
	   implements HasChangeHandlersFactory<FileUpload, WidgetCreatorContext>, 
	   			  HasNameFactory<FileUpload, WidgetCreatorContext>
{//TODO should implement HasEnabledFactory, but GWT FileUploadWidget does not implement HasEnabled. GWT Issue 5677

	@Override
	public FileUpload instantiateWidget(CruxMetaDataElement element, String widgetId) 
	{
		return new FileUpload();
	}
	
	@Override
	@TagAttributes({
		@TagAttribute(value="enabled", type=Boolean.class)
	})
	public void processAttributes(WidgetCreatorContext context) throws InterfaceConfigException 
	{
		super.processAttributes(context);
	}
}
