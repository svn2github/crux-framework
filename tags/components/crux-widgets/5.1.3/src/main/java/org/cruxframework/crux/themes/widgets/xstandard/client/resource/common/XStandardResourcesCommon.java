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
package org.cruxframework.crux.themes.widgets.xstandard.client.resource.common;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ClientBundle.Source;

/**
 * @author Gesse Dafe
 */
public interface XStandardResourcesCommon extends ClientBundle
{
	@Source("svg-icon-file.svg")
	DataResource svgIconFile();
	
	@Source("svg-icon-close.svg")
	DataResource svgIconClose();
	
	@Source("svg-icon-danger.svg")
	DataResource svgIconDanger();
	
	@Source("svg-icon-warning.svg")
	DataResource svgIconWarning();
	
	@Source("svg-icon-success.svg")
	DataResource svgIconSuccess();
	
	@Source("svg-icon-paginator-first.svg")
	DataResource svgIconPaginatorFirst();
	
	@Source("svg-icon-paginator-last.svg")
	DataResource svgIconPaginatorLast();
	
	@Source("svg-icon-paginator-next.svg")
	DataResource svgIconPaginatorNext();
	
	@Source("svg-icon-paginator-prev.svg")
	DataResource svgIconPaginatorPrev();
}
