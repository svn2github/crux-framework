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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.db.AbstractDatabase;
import org.cruxframework.crux.core.client.db.DatabaseErrorHandler;
import org.cruxframework.crux.core.client.db.ObjectStore;
import org.cruxframework.crux.core.client.db.annotation.DatabaseDef;
import org.cruxframework.crux.core.client.db.annotation.DatabaseDef.Empty;
import org.cruxframework.crux.core.client.db.annotation.DatabaseDef.IndexDef;
import org.cruxframework.crux.core.client.db.annotation.DatabaseDef.ObjectStoreDef;
import org.cruxframework.crux.core.client.db.annotation.Store;
import org.cruxframework.crux.core.client.db.annotation.Store.Indexed;
import org.cruxframework.crux.core.client.db.annotation.Store.Key;
import org.cruxframework.crux.core.client.db.indexeddb.IDBDatabaseOptionalParameters;
import org.cruxframework.crux.core.client.db.indexeddb.IDBIndexParameters;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore;
import org.cruxframework.crux.core.client.db.indexeddb.IDBOpenDBRequest;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * This class creates a client proxy for access a database
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class DatabaseProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	private DatabaseDef databaseMetadata;
	private JClassType integerType;
	private JClassType doubleType;
	private JClassType stringType;
	private JClassType emptyType;
	private JClassType dateType;

	public DatabaseProxyCreator(TreeLogger logger, GeneratorContextExt context, JClassType baseIntf)
	{
		super(logger, context, baseIntf, true);
		databaseMetadata = baseIntf.getAnnotation(DatabaseDef.class);
		integerType = context.getTypeOracle().findType(Integer.class.getCanonicalName());
		doubleType = context.getTypeOracle().findType(Double.class.getCanonicalName());
		dateType = context.getTypeOracle().findType(Date.class.getCanonicalName());
		stringType = context.getTypeOracle().findType(String.class.getCanonicalName());
		emptyType = context.getTypeOracle().findType(Empty.class.getCanonicalName());
	}

	@Override
	protected void generateProxyContructor(SourcePrinter srcWriter) throws CruxGeneratorException
	{
	    ObjectStoreDef[] objectStores = databaseMetadata.objectStores();
	    
	    Set<String> added = new HashSet<String>();
	    
		srcWriter.println("public "+getProxySimpleName()+"(){");
		srcWriter.println("this.name = "+EscapeUtils.quote(databaseMetadata.name())+";");
		srcWriter.println("this.version =  "+databaseMetadata.version()+";");

		if (!DatabaseDef.NoErrorHandler.class.isAssignableFrom(databaseMetadata.defaultErrorHandler()))
		{
			srcWriter.println("this.setDefaultErrorHandler((DatabaseErrorHandler)GWT.create("+databaseMetadata.defaultErrorHandler().getCanonicalName()+".class));");
		}
		
		for (ObjectStoreDef objectStoreMetadata : objectStores)
        {
			JClassType objectStoreTarget = getObjectStoreTarget(objectStoreMetadata);
			if (!objectStoreTarget.isAssignableTo(emptyType))
			{
				String objectStoreName = getObjectStoreName(objectStoreMetadata, objectStoreTarget);
				srcWriter.println("storeNames.put("+EscapeUtils.quote(objectStoreTarget.getQualifiedSourceName())+", "+EscapeUtils.quote(objectStoreName)+");");
				if (added.contains(objectStoreTarget.getQualifiedSourceName()))
				{
					throw new CruxGeneratorException("The same type is configured for different ObjectStores on Database["+databaseMetadata.name()+"]");
				}
				added.add(objectStoreTarget.getQualifiedSourceName());
			}
        }
		srcWriter.println("}");
	}
	
	@Override
	protected void generateProxyFields(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println(FastMap.class.getCanonicalName()+"<String> storeNames = new "+FastMap.class.getCanonicalName()+"<String>();");
	}
	
	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter)
	{
	    srcWriter.println("protected String getObjectStoreName(Class<?> objectType){");
	    srcWriter.println("return storeNames.get(objectType.getName().replace('$','.'));");
	    srcWriter.println("}");
		srcWriter.println();
		generateUpdateDatabaseStructureMethod(srcWriter);
		generateGetObjectStoreMethod(srcWriter);
		generateGetObjectStoreByNameMethod(srcWriter);
	}

	protected void generateUpdateDatabaseStructureMethod(SourcePrinter srcWriter)
    {
	    srcWriter.println("protected void updateDatabaseStructure(IDBOpenDBRequest openDBRequest){");

	    generateObjectStoresCreation(srcWriter, "openDBRequest");
	    
	    srcWriter.println("}");
		srcWriter.println();
    }
	
	protected void generateGetObjectStoreMethod(SourcePrinter srcWriter)
    {
	    srcWriter.println("protected <K, V> ObjectStore<K, V> getObjectStore(Class<V> objectType, IDBObjectStore idbObjectStore){");
	    
	    boolean first = true;
	    ObjectStoreDef[] objectStores = databaseMetadata.objectStores();
	    
		srcWriter.println("String className = objectType.getName().replace('$','.');");
		for (ObjectStoreDef objectStoreMetadata : objectStores)
        {
			JClassType objectStoreTarget = getObjectStoreTarget(objectStoreMetadata);
			if (!objectStoreTarget.isAssignableTo(emptyType))
			{
				String objectStoreName = getObjectStoreName(objectStoreMetadata, objectStoreTarget);
				if (!first)
				{
					srcWriter.print("else ");
				}
				first = false;
				Set<IndexData> indexes = getIndexes(objectStoreMetadata.indexes(), objectStoreTarget, objectStoreName);
				srcWriter.println("if (StringUtils.unsafeEquals(className, "+EscapeUtils.quote(objectStoreTarget.getQualifiedSourceName())+")){");
				String[] keyPath = getKeyPath(objectStoreMetadata, objectStoreTarget);
				String objectStore = new ObjectStoreProxyCreator(context, logger, objectStoreTarget, objectStoreName, keyPath, indexes).create();
			    srcWriter.println("return (ObjectStore<K, V>) new "+objectStore+"(this, idbObjectStore);");
				srcWriter.println("}");
			}
        }
	    
	    srcWriter.println("return null;");
	    srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateGetObjectStoreByNameMethod(SourcePrinter srcWriter)
    {
	    srcWriter.println("protected <K, V> ObjectStore<K, V> getObjectStore(String storeName, IDBObjectStore idbObjectStore){");
	    
	    boolean first = true;
	    ObjectStoreDef[] objectStores = databaseMetadata.objectStores();
	    
		for (ObjectStoreDef objectStoreMetadata : objectStores)
        {
			JClassType objectStoreTarget = getObjectStoreTarget(objectStoreMetadata);
			String objectStoreName = getObjectStoreName(objectStoreMetadata, objectStoreTarget);
			if (!first)
			{
				srcWriter.print("else ");
			}
			first = false;
			Set<IndexData> indexes = getIndexes(objectStoreMetadata.indexes(), objectStoreTarget, objectStoreName);
			srcWriter.println("if (StringUtils.unsafeEquals(storeName, "+EscapeUtils.quote(objectStoreName)+")){");
			String[] keyPath = getKeyPath(objectStoreMetadata, objectStoreTarget);
			String objectStore = new ObjectStoreProxyCreator(context, logger, objectStoreTarget, objectStoreName, keyPath, indexes).create();
			srcWriter.println("return (ObjectStore<K, V>) new "+objectStore+"(this, idbObjectStore);");
			srcWriter.println("}");
        }
	    
	    srcWriter.println("return null;");
	    srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateObjectStoresCreation(SourcePrinter srcWriter, String requestVar)
    {
	    ObjectStoreDef[] objectStores = databaseMetadata.objectStores();
	    
	    Set<String> addedObjectStores = new HashSet<String>();
		String objectStoreVar = "objectStore";
		String indexNamesVar = "indexNames";
		srcWriter.println("IDBObjectStore "+objectStoreVar+";");
		srcWriter.println(FastList.class.getCanonicalName() +"<String> storeNames = db.listObjectStoreNames();");
		if (databaseMetadata.overrideDBElements())
		{
			srcWriter.println("for(int i=0; i< storeNames.size(); i++){");
			srcWriter.println("String storeName = storeNames.get(i);");
			srcWriter.println("try{");
			srcWriter.println("db.deleteObjectStore(storeName);");
			srcWriter.println("}catch (Exception e){/* Chrome BUG. When an object store is created, but have no data, chrome raises a NotFoundException when removing these store. So ignore any delete failed attempt.*/}");
			srcWriter.println("}");
		}
		else
		{
			srcWriter.println(FastList.class.getCanonicalName() +"<String> "+indexNamesVar+";");
		}
		for (ObjectStoreDef objectStoreMetadata : objectStores)
        {
			JClassType objectStoreTarget = getObjectStoreTarget(objectStoreMetadata);
			String objectStoreName = getObjectStoreName(objectStoreMetadata, objectStoreTarget);
			if (addedObjectStores.contains(objectStoreName))
			{
				throw new CruxGeneratorException("Duplicated objectstore declared on Datasource ["+databaseMetadata.name()+"]");
			}
			addedObjectStores.add(objectStoreName);
			if (!databaseMetadata.overrideDBElements())
			{
				srcWriter.println("if (!storeNames.contains("+EscapeUtils.quote(objectStoreName)+")){");
			}
			
			if (objectStoreMetadata.keyPath().length == 1)
			{
				generateObjectStoreCreation(srcWriter, objectStoreMetadata.keyPath()[0], objectStoreMetadata.autoIncrement(), objectStoreName, objectStoreVar);
			}
			else if (objectStoreMetadata.keyPath().length > 1)
			{
				generateObjectStoreCreation(srcWriter, objectStoreMetadata.keyPath(), objectStoreMetadata.autoIncrement(), objectStoreName, objectStoreVar);
			}
			else if (objectStoreTarget != null && !objectStoreTarget.isAssignableTo(emptyType))
			{
				String[] keyPath = getKeyPath(objectStoreTarget);
				if (keyPath == null || keyPath.length == 0)
				{
					throw new CruxGeneratorException("can not create an objectStore without a key definition. ObjectStore["+objectStoreName+"].");
				}
				else if (keyPath.length == 1)
				{
					generateObjectStoreCreation(srcWriter, keyPath[0], isAutoIncrement(objectStoreTarget), objectStoreName, objectStoreVar);
				}
				else
				{
					generateObjectStoreCreation(srcWriter, keyPath, isAutoIncrement(objectStoreTarget), objectStoreName, objectStoreVar);
				}
			}
			else
			{
				throw new CruxGeneratorException("can not create an objectStore without a key definition. ObjectStore["+objectStoreName+"].");
			}
			if (!databaseMetadata.overrideDBElements())
			{
				srcWriter.println("} else {");
				srcWriter.println(objectStoreVar+" = "+requestVar+".getTransaction().getObjectStore("+EscapeUtils.quote(objectStoreName)+");");
				srcWriter.println("}");
			}
			generateIndexesCreation(srcWriter, objectStoreMetadata.indexes(), objectStoreTarget, objectStoreVar, objectStoreName, indexNamesVar);
        }
    }
	
	protected Set<IndexData> getIndexes(IndexDef[] indexMetadata, JClassType objectStoreTarget, String objectStoreName)
	{
		Set<IndexData> indexesCreated = new HashSet<IndexData>();
		getIndexesFromMetadata(indexMetadata, indexesCreated, objectStoreName);
		getIndexesFromObject(objectStoreTarget, indexesCreated, objectStoreName);
		return indexesCreated;
	}
	
	protected void getIndexesFromObject(JClassType objectStoreTarget, Set<IndexData> indexesCreated, String objectStoreName)
    {
	    if (objectStoreTarget != null)
		{
	    	Store store = objectStoreTarget.getAnnotation(Store.class);
	    	if (store != null)
	    	{
	    		getIndexesFromMetadata(store.indexes(), indexesCreated, objectStoreName);

	    		List<IndexData> indexes = getIndexFromAnnotations(objectStoreTarget, "");
	    		for (IndexData index: indexes)
	    		{
	    			if (indexesCreated.contains(index.keyPath[0]))
	    			{
	    				throw new CruxGeneratorException("Duplicated index declared on ObjectSore ["+objectStoreName+"] Index ["+index.keyPath[0]+"] Datasource ["+databaseMetadata.name()+"]");
	    			}
	    			indexesCreated.add(index);
	    		}
	    	}
		}
    }
	
	protected void getIndexesFromMetadata(IndexDef[] indexMetadata,  Set<IndexData> indexesCreated, String objectStoreName)
	{
		for (IndexDef index : indexMetadata)
		{
			String indexName = getIndexName(index, objectStoreName);
			if (indexesCreated.contains(indexName))
			{
				throw new CruxGeneratorException("Duplicated index declared on ObjectSore ["+objectStoreName+"] Index ["+indexName+"] Datasource ["+databaseMetadata.name()+"]");
			}
			if (index.keyPath() == null || index.keyPath().length == 0)
			{
				throw new CruxGeneratorException("Can not create an index without a key definition. Index ["+indexName+"] ObjectStore["+objectStoreName+"] Datasource ["+databaseMetadata.name()+"].");
			}
			indexesCreated.add(new IndexData(index.keyPath(), index.unique(), index.multiEntry(), indexName));
		}
	}
	
	
	protected void generateIndexesCreation(SourcePrinter srcWriter, IndexDef[] indexMetadata, 
										   JClassType objectStoreTarget, String objectStoreVar, 
										   String objectStoreName, String indexNamesVar)
    {
		if (!databaseMetadata.overrideDBElements())
		{
			srcWriter.println(indexNamesVar+" = "+objectStoreVar+".listIndexNames();");
		}
		Set<IndexData> indexesCreated = getIndexes(indexMetadata, objectStoreTarget, objectStoreName);
		for (IndexData index : indexesCreated)
        {
			if (index.keyPath.length == 1)
			{//TODO ensureValidIdentifier... remove ',' etc
				generateIndexCreation(srcWriter, index.keyPath[0], index.unique, index.multiEntry, index.indexName, objectStoreVar, indexNamesVar);
			}
			else
			{
				generateIndexCreation(srcWriter, index.keyPath, index.unique, index.multiEntry, index.indexName, objectStoreVar, indexNamesVar);
			}
        }
    }

	protected List<IndexData> getIndexFromAnnotations(JClassType objectStoreTarget, String prefix)
    {
		List<IndexData> result = new ArrayList<IndexData>();
		List<JMethod> getterMethods = JClassUtils.getGetterMethods(objectStoreTarget);
		for (JMethod method : getterMethods)
        {
	        if (JClassUtils.isSimpleType(method.getReturnType()))
	        {
	        	Indexed indexed = method.getAnnotation(Indexed.class);
	        	if (indexed != null)
	        	{
	        		String property = JClassUtils.getPropertyForGetterOrSetterMethod(method);
	        		result.add(new IndexData(new String[]{prefix+property}, indexed.unique(), false, prefix+property));
	        	}
	        }
	        else
	        {
        		String property = JClassUtils.getPropertyForGetterOrSetterMethod(method);
	        	result.addAll(getIndexFromAnnotations(method.getReturnType().isClassOrInterface(), prefix+property+"."));
	        }
        }
		
	    return result;
    }

	protected String getIndexName(IndexDef indexDef, String objectStoreName)
	{
		if (!StringUtils.isEmpty(indexDef.name()))
		{
			return indexDef.name();
		}
		StringBuilder str = new StringBuilder();
		boolean first = true;
		for (String key : indexDef.keyPath())
        {
			if (!first)
			{
				str.append('_');
			}
			first = false;
	        str.append(key);
        }
		
		if (str.length() == 0)
		{
			throw new CruxGeneratorException("Invalid index declared on ObjectSore ["+objectStoreName+"] Datasource ["+databaseMetadata.name()+"]");
		}
		
		return str.toString();
		
	}

	protected void generateIndexCreation(SourcePrinter srcWriter, String keyPath, boolean unique, boolean multiEntry, 
										 String name, String objectStoreVar, String indexNamesVar)
    {
		if (!databaseMetadata.overrideDBElements())
		{
			srcWriter.println("if (!"+indexNamesVar+".contains("+EscapeUtils.quote(name)+")){");
		}
	    srcWriter.println(objectStoreVar+".createIndex("+EscapeUtils.quote(name)+", "+ EscapeUtils.quote(keyPath) + ", " +
	    		IDBIndexParameters.class.getCanonicalName()+".create("+unique+", "+multiEntry+"));");
		if (!databaseMetadata.overrideDBElements())
		{
			srcWriter.println("}");
		}
    }

	protected void generateIndexCreation(SourcePrinter srcWriter, String[] keyPaths, boolean unique, boolean multiEntry, 
										String name, String objectStoreVar, String indexNamesVar)
	{
		if (!databaseMetadata.overrideDBElements())
		{
			srcWriter.println("if (!"+indexNamesVar+".contains("+EscapeUtils.quote(name)+")){");
		}
	    srcWriter.println(objectStoreVar+".createIndex("+EscapeUtils.quote(name)+", new String[]{");
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
	    srcWriter.println("}, "  + IDBIndexParameters.class.getCanonicalName()+".create("+unique+", "+multiEntry+"));");
		if (!databaseMetadata.overrideDBElements())
		{
			srcWriter.println("}");
		}
	}
	
	protected void generateObjectStoreCreation(SourcePrinter srcWriter, String keyPath, boolean autoIncrement, String objectStoreName, String objectStoreVar)
	{
		srcWriter.println(objectStoreVar+" = db.createObjectStore("+EscapeUtils.quote(objectStoreName)+", "+
				IDBDatabaseOptionalParameters.class.getCanonicalName()+".create("+EscapeUtils.quote(keyPath)+", "+autoIncrement+"));");
	}
	
	protected void generateObjectStoreCreation(SourcePrinter srcWriter, String[] keyPaths, boolean autoIncrement, String objectStoreName, String objectStoreVar)
    {
	    srcWriter.println(objectStoreVar+" = db.createObjectStore("+EscapeUtils.quote(objectStoreName)+", "+
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
				if (key.autoIncrement() && !method.getReturnType().getQualifiedSourceName().equals("int") && 
					!method.getReturnType().getQualifiedSourceName().equals(Integer.class.getCanonicalName()))
				{
					throw new CruxGeneratorException("Invalid key declaration on objectStore ["+targetObject.getQualifiedSourceName()+"]. Can not use autoIncrement on a non int key.");
				}
	        	result = key.autoIncrement();
	        }
        }
		return result;
	}
	
	protected String[] getKeyPath(ObjectStoreDef objectStoreMetadata, JClassType targetObject)
	{
		if (objectStoreMetadata.keyPath().length > 0)
		{
			return objectStoreMetadata.keyPath(); 
		}
		else if (targetObject != null)
		{
			return getKeyPath(targetObject);
		}
		return new String[]{};
	}
	
	protected String[] getKeyPath(JClassType targetObject)
	{
		List<String> keyPath = new ArrayList<String>();
		List<Key> keys = new ArrayList<Key>();

		List<JMethod> getterMethods = JClassUtils.getGetterMethods(targetObject);
		
		for (JMethod method : getterMethods)
        {
	        Key key = method.getAnnotation(Key.class);
			if (key != null)
	        {
	        	if (!isValidTypeForKey(method.getReturnType()))
	        	{
	        		throw new CruxGeneratorException("Crux databases only support Strings or int as key components");
	        	}
	        	keys.add(key);
        		keyPath.add(JClassUtils.getPropertyForGetterOrSetterMethod(method));
	        }
        }
		for (int i=0; i< keys.size(); i++)
		{
			int orderI = keys.get(i).order();
			int pos = i;
			for (int j=i+1; j < keys.size(); j++)
			{
				int orderJ = keys.get(j).order();
				if (orderJ < orderI)
				{
					orderI = orderJ;
					pos = j;
				}
			}
			if (pos != i)
			{
				Key key = keys.remove(pos);
				keys.add(i, key);
				String path = keyPath.remove(pos);
				keyPath.add(i, path);
			}
		}
		
		return keyPath.toArray(new String[keyPath.size()]);
	}

	protected boolean isValidTypeForKey(JType jType)
    {
	    return jType.equals(stringType) || 
	    	jType.equals(integerType) || 
	    	jType.equals(JPrimitiveType.INT) ||
	    	jType.equals(doubleType) || 
	    	jType.equals(JPrimitiveType.DOUBLE) ||
	    	jType.equals(dateType);
    }
	
	protected JClassType getObjectStoreTarget(ObjectStoreDef objectStoreMetadata)
	{
		return context.getTypeOracle().findType(objectStoreMetadata.targetClass().getCanonicalName());
	}
	
	protected String getObjectStoreName(ObjectStoreDef objectStoreMetadata, JClassType objectStoreTarget)
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
	            throw new CruxGeneratorException("Invalid Database Store. You must inform a name on @ObjectStoreDef, or point to a target Class annotated with @Store.");
            }
		}
		return name;
	}
	
	@Override
	protected String[] getImports()
	{
		return new String[]{
			AbstractDatabase.class.getCanonicalName(), 
			IDBObjectStore.class.getCanonicalName(),
			IDBOpenDBRequest.class.getCanonicalName(),
			DatabaseErrorHandler.class.getCanonicalName(),
			ObjectStore.class.getCanonicalName(), 
			StringUtils.class.getCanonicalName(), 
			GWT.class.getCanonicalName()
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
	

	static class IndexData
	{
		boolean unique;
		boolean multiEntry;
		String[] keyPath;
		final String indexName;
		IndexData(String[] keyPath, boolean unique, boolean multiEntry, String indexName)
		{
			this.keyPath = keyPath;
			this.unique = unique;
			this.multiEntry = multiEntry;
			this.indexName = indexName;
		}
	}
}
