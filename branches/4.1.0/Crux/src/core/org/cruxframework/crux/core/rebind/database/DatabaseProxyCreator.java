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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cruxframework.crux.core.client.db.AbstractDatabase;
import org.cruxframework.crux.core.client.db.DatabaseMetadata;
import org.cruxframework.crux.core.client.db.DatabaseMetadata.ObjectStoreMetadata;
import org.cruxframework.crux.core.client.db.Store;
import org.cruxframework.crux.core.client.db.Store.Key;
import org.cruxframework.crux.core.client.db.indexeddb.IDBDatabaseOptionalParameters;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
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
		generateUpdateDatabaseStructureMethod(srcWriter);
	}

	protected void generateUpdateDatabaseStructureMethod(SourcePrinter srcWriter)
    {
	    srcWriter.println("protected void updateDatabaseStructure(){");

	    generateObjectStoresCreation(srcWriter);
	    
	    srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateObjectStoresCreation(SourcePrinter srcWriter)
    {
	    ObjectStoreMetadata[] objectStores = databaseMetadata.objectStores();
	    
	    Set<String> addedObjectStores = new HashSet<String>();
		for (ObjectStoreMetadata objectStoreMetadata : objectStores)
        {
			JClassType objectStoreTarget = getObjectStoreTarget(objectStoreMetadata);
			String objectStoreName = getObjectStoreName(objectStoreMetadata, objectStoreTarget);
			if (addedObjectStores.contains(objectStoreName))
			{
				throw new CruxGeneratorException("Duplicated objectstore declared on Datasource ["+databaseMetadata.name()+"]");
			}
			addedObjectStores.add(objectStoreName);
			if (databaseMetadata.overrideDBElements())
			{
				srcWriter.println("db.deleteObjectStore("+EscapeUtils.quote(objectStoreName)+");");
			}
			else
			{
				srcWriter.println("if (!db.getObjectStoreNames().contains("+EscapeUtils.quote(objectStoreName)+")){");
			}
			
			if (!StringUtils.isEmpty(objectStoreMetadata.keyPath()) && objectStoreMetadata.compositeKeyPath().length > 0)
			{
				throw new CruxGeneratorException("Error generating database for class ["+baseIntf.getQualifiedSourceName()+"]. You can declare just one of keyPath or CompositeKeyPath attribute for objectStore ["+objectStoreMetadata.name()+"].");
			}
			else if (!StringUtils.isEmpty(objectStoreMetadata.keyPath()))
			{
				generateObjectStoreCreation(srcWriter, objectStoreMetadata.keyPath(), objectStoreMetadata.autoIncrement(), objectStoreName);
			}
			else if (objectStoreMetadata.compositeKeyPath().length != 0)
			{
				generateObjectStoreCreation(srcWriter, objectStoreMetadata.compositeKeyPath(), objectStoreMetadata.autoIncrement(), objectStoreName);
			}
			else if (objectStoreTarget != null)
			{
				String[] keyPath = getKeyPath(objectStoreTarget);
				if (keyPath == null || keyPath.length == 0)
				{
					srcWriter.println("db.createObjectStore("+EscapeUtils.quote(objectStoreName)+");");
				}
				else if (keyPath.length == 1)
				{
					generateObjectStoreCreation(srcWriter, keyPath[0], isAutoIncrement(objectStoreTarget), objectStoreName);
				}
				else
				{
					generateObjectStoreCreation(srcWriter, keyPath, isAutoIncrement(objectStoreTarget), objectStoreName);
				}
			}
			else
			{
				srcWriter.println("db.createObjectStore("+EscapeUtils.quote(objectStoreName)+");");
			}
			if (!databaseMetadata.overrideDBElements())
			{
				srcWriter.println("}");
			}
        }
    }

	protected void generateObjectStoreCreation(SourcePrinter srcWriter, String keyPath, boolean autoIncrement, String objectStoreName)
    {
	    srcWriter.println("db.createObjectStore("+EscapeUtils.quote(objectStoreName)+", "+
	    		IDBDatabaseOptionalParameters.class.getCanonicalName()+".create("+EscapeUtils.quote(keyPath)+", "+autoIncrement+"));");
    }

	protected void generateObjectStoreCreation(SourcePrinter srcWriter, String[] keyPaths, boolean autoIncrement, String objectStoreName)
    {
	    srcWriter.print("db.createObjectStore("+EscapeUtils.quote(objectStoreName)+", "+
	    		IDBDatabaseOptionalParameters.class.getCanonicalName()+".create(new String[]{");
	    boolean first = true;
	    for (String keyPath : keyPaths)
	    {
	    	if (!first)
	    	{
	    		srcWriter.print(", ");
	    	}
	    	first = false;
	    	srcWriter.print(EscapeUtils.quote(keyPath));
	    }		
	    srcWriter.println("}, "+autoIncrement+"));");
    }

	protected boolean isAutoIncrement(JClassType targetObject)
	{
		boolean result = false;
		
		List<JMethod> getterMethods = JClassUtils.getGetterMethods(targetObject);
		for (JMethod method : getterMethods)
        {
	        Key key = method.getAnnotation(Key.class);
			if (key != null)
	        {
				if (result && !key.autoIncrement())
				{
					throw new CruxGeneratorException("Invalid composite key declaration on objectStore ["+targetObject.getQualifiedSourceName()+"]. Can not use autoIncrement only on subset on keyPath set.");
				}
	        	result = key.autoIncrement();
	        }
        }
		return result;
	}
	
	protected String[] getKeyPath(JClassType targetObject)
	{
		List<String> result = new ArrayList<String>();
		List<JMethod> getterMethods = JClassUtils.getGetterMethods(targetObject);
		for (JMethod method : getterMethods)
        {
	        if (method.getAnnotation(Key.class) != null)
	        {
	        	result.add(JClassUtils.getPropertyForGetterOrSetterMethod(method));
	        }
        }
		return result.toArray(new String[result.size()]);
	}
	
	protected JClassType getObjectStoreTarget(ObjectStoreMetadata objectStoreMetadata)
	{
		return context.getTypeOracle().findType(objectStoreMetadata.targetClass().getCanonicalName());
	}
	
	protected String getObjectStoreName(ObjectStoreMetadata objectStoreMetadata, JClassType objectStoreTarget)
	{
		String name = objectStoreMetadata.name();
		if (StringUtils.isEmpty(name))
		{
			try
            {
	            name = objectStoreTarget.getAnnotation(Store.class).value();
            }
            catch (Exception e)
            {
	            throw new CruxGeneratorException("Invalid Database Store. You must inform a name on @ObjectStoreMetadata, or point to a target Class annotated with @Store.");
            }
		}
		return name;
	}

	protected void checkKeyPath(String... keyPath)
	{
		
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
