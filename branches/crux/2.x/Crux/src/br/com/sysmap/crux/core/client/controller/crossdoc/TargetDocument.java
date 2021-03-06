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
package br.com.sysmap.crux.core.client.controller.crossdoc;

import br.com.sysmap.crux.core.client.screen.JSWindow;

/**
 * All cross document objects created by Crux implements that interface. It can be
 * used to allow the caller to define where the method will be invoked.
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface TargetDocument extends CrossDocument
{
	/**
	 * Sets the target for the cross document call.
	 * 
	 * @param target - the target
	 */
	void setTarget(Target target);
	
	/**
	 * Sets the target frame for the cross document call.
	 * 
	 * @param frame - the name of the frame
	 */
	void setTargetFrame(String frame);
	
	/**
	 * Sets the target sibling frame for the cross document call.
	 * 
	 * @param frame - the name of the sibling frame
	 */
	void setTargetSiblingFrame(String frame);
	
	/**
	 * Sets the target window for the cross document call.
	 * 
	 * @param jsWindow - the window where the call will occur
	 */
	void setTargetWindow(JSWindow jsWindow);
}
