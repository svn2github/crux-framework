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
package br.com.sysmap.crux.core.rebind.config;

import br.com.sysmap.crux.core.client.config.CruxClientConfig;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.config.ConfigurationFactory;
import br.com.sysmap.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;

import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CruxClientConfigProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	/**
	 * Constructor
	 * @param logger
	 * @param context
	 */
	public CruxClientConfigProxyCreator(TreeLogger logger, GeneratorContextExt context)
    {
	    super(logger, context, context.getTypeOracle().findType(CruxClientConfig.class.getCanonicalName()), true);
    }
	
	@Override
    protected String[] getImports()
    {
	    String[] imports = new String[] {
	    		Screen.class.getCanonicalName()
			};
		    return imports;    
    }

	@Override
    protected void generateProxyContructor(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }

	@Override
    protected void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }

	@Override
    protected void generateProxyMethods(SourceWriter sourceWriter) throws CruxGeneratorException
    {
		generateEnableChildrenWindowsDebugMethod(sourceWriter);
		generateEnableCrux2OldInterfacesCompatibility(sourceWriter);
    }

	@Override
    protected void generateSubTypes(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }	

	/**
	 * @param sourceWriter
	 */
	protected void generateEnableChildrenWindowsDebugMethod(SourceWriter sourceWriter)
	{
		sourceWriter.println("public boolean enableDebugForURL(String url){");
		sourceWriter.indent();
		sourceWriter.println("return " + ConfigurationFactory.getConfigurations().enableChildrenWindowsDebug() + ";");
		sourceWriter.outdent();
		sourceWriter.println("}");
	}
	
	/**
	 * @param sourceWriter
	 */
	protected void generateEnableCrux2OldInterfacesCompatibility(SourceWriter sourceWriter)
	{
		sourceWriter.println("public boolean enableCrux2OldInterfacesCompatibility(){");
		sourceWriter.indent();
		sourceWriter.println("return " + ConfigurationFactory.getConfigurations().enableCrux2OldInterfacesCompatibility() + ";");
		sourceWriter.outdent();
		sourceWriter.println("}");
	}
}
