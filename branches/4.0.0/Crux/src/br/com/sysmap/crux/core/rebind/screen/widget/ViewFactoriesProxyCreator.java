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
package br.com.sysmap.crux.core.rebind.screen.widget;

import java.util.List;

import br.com.sysmap.crux.core.client.collection.FastMap;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ViewFactory;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.Screen;
import br.com.sysmap.crux.core.server.Environment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewFactoriesProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	/**
	 * @param logger
	 * @param context
	 */
	public ViewFactoriesProxyCreator(TreeLogger logger, GeneratorContext context)
    {
	    super(logger, context, context.getTypeOracle().findType(ViewFactory.class.getCanonicalName()));
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
		sourceWriter.println("public void createView(String screenId) throws InterfaceConfigException{ ");
		sourceWriter.indent();

		if (Environment.isProduction())
		{
			generateViewCreationForAllScreens(sourceWriter);
		}
		else
		{
			generateViewCreationForCurrentScreen(sourceWriter);
		}
		
		sourceWriter.outdent();
		sourceWriter.println("}");
    }

	/**
	 * @param sourceWriter
	 */
	private void generateViewCreationForCurrentScreen(SourceWriter sourceWriter) 
	{
			Screen screen = getCurrentScreen();
			generateViewCreator(sourceWriter, screen);
	}

	/**
	 * @param sourceWriter
	 * @param screens
	 */
	private void generateViewCreationForAllScreens(SourceWriter sourceWriter) 
	{
		List<Screen> screens = getScreens();
		//TODO: Carregar as telas sob necessidade...com um fragment
		
		boolean first = true;
		for (Screen screen : screens)
        {
			if (!first)
			{
				sourceWriter.print("else ");
			}
			first = false;
			
			sourceWriter.println("if (StringUtils.unsafeEquals(screenId, "+EscapeUtils.quote(screen.getModule()+"/"+screen.getRelativeId())+")){");
			sourceWriter.indent();
			
			generateViewCreator(sourceWriter, screen);

			sourceWriter.outdent();
			sourceWriter.println("}");
        }
	}	
	
	/**
	 * @param sourceWriter
	 * @param screen
	 */
	private void generateViewCreator(SourceWriter sourceWriter, Screen screen)
    {
		ViewFactoryCreator factoryCreator = getViewFactoryCreator(screen);
		try
		{
			sourceWriter.println("new "+ factoryCreator.create()+"().create();");
		}
		finally
		{
			factoryCreator.prepare(null, null);
		}
    }

	/**
	 * @param screen
	 * @return
	 */
	private ViewFactoryCreator getViewFactoryCreator(Screen screen)
	{
		if (Environment.isProduction())
		{
			return new ViewFactoryCreator(context, logger, screen);
		}
		else
		{
			ViewFactoryCreator factory = screen.getFactory();
			if (factory == null)
			{
				factory = new ViewFactoryCreator(context, logger, screen);
				screen.setFactory(factory);
			}
			else
			{
				factory.prepare(context, logger);
			}
			return factory;
		}
	}
	
	@Override
    protected void generateSubTypes(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }

	@Override
	protected String[] getImports()
	{
		String[] imports = new String[] {
				GWT.class.getCanonicalName(),
				FastMap.class.getCanonicalName(),
				ViewFactory.class.getCanonicalName(),
				StringUtils.class.getCanonicalName(),
				com.google.gwt.user.client.ui.Widget.class.getCanonicalName(), 
				WidgetCreatorContext.class.getCanonicalName(), 
				InterfaceConfigException.class.getCanonicalName()
		};
		return imports;
	}
}
