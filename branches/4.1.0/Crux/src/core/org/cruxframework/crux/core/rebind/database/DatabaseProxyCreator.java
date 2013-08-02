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
package org.cruxframework.crux.core.rebind.database;

import java.io.PrintWriter;

import org.cruxframework.crux.core.client.db.AbstractDatabase;
import org.cruxframework.crux.core.client.db.DatabaseMetadata;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;

import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * This class creates a client proxy for access a database
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class DatabaseProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	private DatabaseMetadata databaseMetadata;

	public DatabaseProxyCreator(TreeLogger logger, GeneratorContextExt context, JClassType baseIntf)
	{
		super(logger, context, baseIntf, true);
		databaseMetadata = baseIntf.getAnnotation(DatabaseMetadata.class);
	}

	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter)
	{
		srcWriter.println("public String getName(){");
		srcWriter.println("return "+EscapeUtils.quote(databaseMetadata.name())+";");
		srcWriter.println("}");
		srcWriter.println();
		srcWriter.println("public int getVersion(){");
		srcWriter.println("return "+databaseMetadata.version()+";");
		srcWriter.println("}");
		srcWriter.println();
		srcWriter.println("protected void updateDatabaseStructure(){");
		srcWriter.println("}");
		srcWriter.println();
	}

	@Override
	protected String[] getImports()
	{
		return new String[]{
			AbstractDatabase.class.getCanonicalName() 
		};
	}

	/**
	 * @return a sourceWriter for the proxy class
	 */
	@Override
	protected SourcePrinter getSourcePrinter()
	{
		JPackage pkg = baseIntf.getPackage();
		String packageName = pkg == null ? "" : pkg.getName();
		PrintWriter printWriter = context.tryCreate(logger, packageName, getProxySimpleName());

		if (printWriter == null)
		{
			return null;
		}

		ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, getProxySimpleName());

		String[] imports = getImports();
		for (String imp : imports)
		{
			composerFactory.addImport(imp);
		}
		composerFactory.setSuperclass(AbstractDatabase.class.getCanonicalName());
		composerFactory.addImplementedInterface(baseIntf.getQualifiedSourceName());

		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);
	}
}
