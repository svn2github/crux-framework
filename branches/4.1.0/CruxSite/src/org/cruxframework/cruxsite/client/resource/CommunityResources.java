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
package org.cruxframework.cruxsite.client.resource;

import org.cruxframework.crux.core.client.resources.Resource;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Resource("communityResources")
public interface CommunityResources extends ClientBundle
{
	@Source("icon-blogger.png")
	ImageResource iconBlogger();

	@Source("icon-forum.png")
	ImageResource iconForum();

	@Source("icon-google.png")
	ImageResource iconGoogle();
}
