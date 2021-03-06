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
package br.com.sysmap.crux.core.config;

import br.com.sysmap.crux.core.i18n.DefaultServerMessage;

public interface Crux 
{
	@DefaultServerMessage("br.com.sysmap.crux.core.server.dispatch.ServiceFactoryImpl")
	String serviceFactory();
	
	@DefaultServerMessage("br.com.sysmap.crux.core.i18n.LocaleResolverImpl")
	String localeResolver();

	@DefaultServerMessage("br.com.sysmap.crux.core.declarativeui.DeclarativeUIScreenResolver")
	String screenResourceResolver();

	@DefaultServerMessage("br.com.sysmap.crux.core.server.classpath.ClassPathResolverImpl")
	String classPathResolver();
	
	@DefaultServerMessage("true")
	String wrapSiblingWidgets();

	@DefaultServerMessage("true")
	String enableChildrenWindowsDebug();

	@DefaultServerMessage("true")
	String enableWebRootScannerCache();

	@DefaultServerMessage("true")
	String enableHotDeploymentForWebDirs();

	@DefaultServerMessage("true")
	String enableHotDeploymentForWidgetFactories();
	
	@DefaultServerMessage("true")
	String renderWidgetsWithIDs();

	@DefaultServerMessage("false")
	String enableClientFactoryTracing();
	
	@DefaultServerMessage("false")
	String useCompileTimeClassScanning();

	void setEnableWebRootScannerCache(Boolean value);
	void setEnableChildrenWindowsDebug(Boolean value);
	void setWrapSiblingWidgets(Boolean value);
	void setEnableHotDeploymentForWebDirs(Boolean value);
	void setEnableHotDeploymentForWidgetFactories(Boolean value);
	void setRenderWidgetsWithIDs(Boolean value);
	void setEnableClientFactoryTracing(Boolean value);
	void setUseCompileTimeClassScanning(Boolean value);
	void setClassPathResolver(String value);
	void setScreenResourceResolver(String value);
	void setLocaleResolver(String value);
	void setServiceFactory(String value);
	//TODO atualizar wiki com as opções correntes



}
