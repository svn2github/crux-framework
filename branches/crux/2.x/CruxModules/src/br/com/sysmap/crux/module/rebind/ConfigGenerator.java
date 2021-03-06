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
package br.com.sysmap.crux.module.rebind;

import br.com.sysmap.crux.core.config.ConfigurationFactory;
import br.com.sysmap.crux.core.rebind.CruxClientConfigGenerator;
import br.com.sysmap.crux.module.CruxModuleHandler;

import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ConfigGenerator extends CruxClientConfigGenerator
{
	@Override
	protected void generateEnableChildrenWindowsDebugMethod(SourceWriter sourceWriter)
	{
		sourceWriter.println("public boolean enableDebugForURL(String url){");
		if (!Boolean.parseBoolean(ConfigurationFactory.getConfigurations().enableChildrenWindowsDebug()))
		{
			sourceWriter.println("return false;");
		}
		else
		{
			String[] developmentModules = CruxModuleHandler.getDevelopmentModules();
			if (developmentModules != null)
			{
				sourceWriter.println("if (url == null){");
				sourceWriter.println("return false;");
				sourceWriter.println("}");

				sourceWriter.println("String urlWithoutParameters = url;");
				sourceWriter.println("int index = url.indexOf(\"?\");");
				sourceWriter.println("if (index  > 0){");
				sourceWriter.println("urlWithoutParameters = url.substring(0,index);");
				sourceWriter.println("}");
				for (String moduleName : developmentModules)
				{
					String[] pages = CruxModuleHandler.getCruxModule(moduleName).getPages();
					if (pages != null)
					{
						for (String page : pages)
						{
							sourceWriter.println("if (urlWithoutParameters.endsWith(\""+page+"\")){");
							sourceWriter.println("return true;");
							sourceWriter.println("}");
						}
					}
				}
			}
			sourceWriter.println("return false;");
		}
		sourceWriter.println("}");
	}
}
