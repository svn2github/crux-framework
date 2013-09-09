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
import java.util.Set;

import org.cruxframework.crux.core.client.db.AbstractObjectStore;
import org.cruxframework.crux.core.client.db.Cursor;
import org.cruxframework.crux.core.client.db.Cursor.CursorDirection;
import org.cruxframework.crux.core.client.db.DatabaseCursorCallback;
import org.cruxframework.crux.core.client.db.DatabaseRetrieveCallback;
import org.cruxframework.crux.core.client.db.DatabaseWriteCallback;
import org.cruxframework.crux.core.client.db.KeyRangeFactory;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore.IDBObjectCursorRequest;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore.IDBObjectRetrieveRequest;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore.IDBObjectStoreRequest;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBCursorEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBErrorEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBObjectRetrieveEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBObjectStoreEvent;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.database.DatabaseProxyCreator.IndexData;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ObjectStoreProxyCreator extends AbstractKeyValueProxyCreator
{
	private JClassType abstractObjectStoreType;
	private String idbObjectStoreVariable;
	private String dbVariable;
	private final Set<IndexData> indexes;

	public ObjectStoreProxyCreator(GeneratorContext context, TreeLogger logger, JClassType targetObjectType, String objectStoreName, String[] keyPath, Set<IndexData> indexes)
	{
		super(context, logger, targetObjectType, objectStoreName, keyPath);
		this.indexes = indexes;
		this.abstractObjectStoreType = context.getTypeOracle().findType(AbstractObjectStore.class.getCanonicalName());
		this.idbObjectStoreVariable = "idbObjectStore";
		this.dbVariable = "db";
	}
	
	@Override
	protected void generateProxyContructor(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("public "+getProxySimpleName()+"(AbstractDatabase db, IDBObjectStore idbObjectStore){");
		srcWriter.println("super(db, idbObjectStore);");
		srcWriter.println("}");
	}
	
	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		generateGetObjectStoreNameMethod(srcWriter);
		generateDeleteMethod(srcWriter);
		generateDeleteRangeMethod(srcWriter);
		generateAddMethod(srcWriter);
		generatePutMethod(srcWriter);
		generateGetMethod(srcWriter);
		generateOpenCursorMethod(srcWriter);
		generateOpenCursorKeyMethod(srcWriter);
		generateOpenCursorKeyDirectionMethod(srcWriter);
		generateGetIndexMethod(srcWriter);
		generateGetKeyRangeFactoryMethod(srcWriter, objectStoreName);
		if (hasCompositeKey())
		{
			generateGetNativeKeyMethod(srcWriter);
		}
	}

	protected void generateGetObjectStoreNameMethod(SourcePrinter srcWriter)
    {
	    srcWriter.println("public String getObjectStoreName(){");
		srcWriter.println("return "+EscapeUtils.quote(objectStoreName)+";");
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateDeleteMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void delete("+getKeyTypeName()+" key, final DatabaseWriteCallback<"+getKeyTypeName()+"> callback){");
		if (hasCompositeKey())
		{
			srcWriter.println("IDBObjectStoreRequest deleteRequest = " + idbObjectStoreVariable+".delete(getNativeKey(key));");
		}
		else
		{
			srcWriter.println("IDBObjectStoreRequest deleteRequest = " + idbObjectStoreVariable+".delete(key);");
		}
		generateDeleteCallbacks(srcWriter, "callback", dbVariable, "deleteRequest");
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateDeleteRangeMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void delete(KeyRange<"+getKeyTypeName()+"> keyRange, final DatabaseWriteCallback<"+getKeyTypeName()+"> callback){");
		srcWriter.println("IDBObjectStoreRequest deleteRequest = " + idbObjectStoreVariable+".delete(keyRange.getNativeKeyRange());");
		generateDeleteCallbacks(srcWriter, "callback", dbVariable, "deleteRequest");
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateAddMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void add("+getTargetObjectClassName()+" object){");
		srcWriter.println("put(object, null);");
		srcWriter.println("}");
		srcWriter.println();
		
		srcWriter.println("public void add("+getTargetObjectClassName()+" object, final DatabaseWriteCallback<"+getKeyTypeName()+"> callback){");
		srcWriter.println("if (object == null){");
		srcWriter.println("throw new NullPointerException();");
		srcWriter.println("}");
		if (isEmptyType())
		{
			srcWriter.println("IDBObjectStoreRequest storeRequest = " + idbObjectStoreVariable+".add(object);");
		}
		else
		{
			srcWriter.println("IDBObjectStoreRequest storeRequest = " + idbObjectStoreVariable+".add("+serializerVariable+".encode(object).isObject().getJavaScriptObject());");
		}
		generateWriteCallbacks(srcWriter, "callback", dbVariable, "storeRequest");
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generatePutMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void put("+getTargetObjectClassName()+" object){");
		srcWriter.println("put(object, null);");
		srcWriter.println("}");
		srcWriter.println();
		
		srcWriter.println("public void put("+getTargetObjectClassName()+" object, final DatabaseWriteCallback<"+getKeyTypeName()+"> callback){");
		srcWriter.println("if (object == null){");
		srcWriter.println("throw new NullPointerException();");
		srcWriter.println("}");
		if (isEmptyType())
		{
			srcWriter.println("IDBObjectStoreRequest storeRequest = " + idbObjectStoreVariable+".put(object);");
		}
		else
		{
			srcWriter.println("IDBObjectStoreRequest storeRequest = " + idbObjectStoreVariable+".put("+serializerVariable+".encode(object).isObject().getJavaScriptObject());");
		}
		generateWriteCallbacks(srcWriter, "callback", dbVariable, "storeRequest");
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateGetMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void get("+getKeyTypeName()+" key, final DatabaseRetrieveCallback<"+getTargetObjectClassName()+"> callback){");
		if (hasCompositeKey())
		{
			srcWriter.println("IDBObjectRetrieveRequest retrieveRequest = "+idbObjectStoreVariable+".get(getNativeKey(key));");
		}
		else
		{
			srcWriter.println("IDBObjectRetrieveRequest retrieveRequest = "+idbObjectStoreVariable+".get(key);");
		}
		generateGetCallbacks(srcWriter, "callback", dbVariable, "retrieveRequest");
				
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateOpenCursorMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void openCursor(final DatabaseCursorCallback<"+getKeyTypeName()+", "+getTargetObjectClassName()+"> callback){");
		srcWriter.println("IDBObjectCursorRequest cursorRequest = " + idbObjectStoreVariable+".openCursor();");
		generateCursorHandlers(srcWriter, "callback", dbVariable, "cursorRequest", "ObjectStore_"+getTargetObjectClassName());
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateOpenCursorKeyMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void openCursor(KeyRange<"+getKeyTypeName()+"> keyRange, final DatabaseCursorCallback<"+getKeyTypeName()+", "+getTargetObjectClassName()+"> callback){");
		srcWriter.println("IDBObjectCursorRequest cursorRequest = " + idbObjectStoreVariable+".openCursor(keyRange.getNativeKeyRange());");
		generateCursorHandlers(srcWriter, "callback", dbVariable, "cursorRequest", "ObjectStore_"+getTargetObjectClassName());
		srcWriter.println("}");
		srcWriter.println();
    }
	
	protected void generateOpenCursorKeyDirectionMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void openCursor(KeyRange<"+getKeyTypeName()+"> keyRange, CursorDirection direction, final DatabaseCursorCallback<"+getKeyTypeName()+", "+getTargetObjectClassName()+"> callback){");
		srcWriter.println("IDBObjectCursorRequest cursorRequest = " + idbObjectStoreVariable+".openCursor(keyRange.getNativeKeyRange(), direction.getNativeCursorDirection());");
		generateCursorHandlers(srcWriter, "callback", dbVariable, "cursorRequest", "ObjectStore_"+getTargetObjectClassName());
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateGetIndexMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("public <I> Index<"+getKeyTypeName()+", I, "+getTargetObjectClassName()+"> getIndex(String name){");
		for(IndexData index: indexes)
		{
			srcWriter.println("if (StringUtils.unsafeEquals(name, "+EscapeUtils.quote(index.indexName)+")){");
			String indexClassName = new IndexProxyCreator(context, logger, targetObjectType, objectStoreName, index.keyPath, index.indexName, keyPath).create();
			srcWriter.println("return (Index<"+getKeyTypeName()+", I, "+getTargetObjectClassName()+">) new " + indexClassName + "("+dbVariable+", "+idbObjectStoreVariable+".getIndex(name));");
			srcWriter.println("}");
		}
		
		srcWriter.println("return null;");
		srcWriter.println("}");
		srcWriter.println();
	}
	
	@Override
	public String getProxyQualifiedName()
	{
		return abstractObjectStoreType.getPackage().getName()+"."+getProxySimpleName();
	}

	@Override
	public String getProxySimpleName()
	{
		String typeName = objectStoreName.replaceAll("\\W", "_");
		return typeName+"_ObjectStore";
	}

	@Override
	protected SourcePrinter getSourcePrinter()
	{
		String packageName = abstractObjectStoreType.getPackage().getName();
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
		composerFactory.setSuperclass("AbstractObjectStore<"+getKeyTypeName()+","+getTargetObjectClassName()+">");

		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);
	}

	/**
	 * @return
	 */
	protected String[] getImports()
	{
		String[] imports = new String[] {
				AbstractObjectStore.class.getCanonicalName(), 
				IDBObjectStore.class.getCanonicalName(),
				DatabaseRetrieveCallback.class.getCanonicalName(),
				DatabaseWriteCallback.class.getCanonicalName(),
				IDBObjectRetrieveEvent.class.getCanonicalName(), 
				IDBObjectRetrieveRequest.class.getCanonicalName(),
				IDBObjectStoreRequest.class.getCanonicalName(),
				IDBObjectStoreEvent.class.getCanonicalName(),
				JSONObject.class.getCanonicalName(), 
				DatabaseCursorCallback.class.getCanonicalName(), 
				IDBObjectCursorRequest.class.getCanonicalName(), 
				IDBCursorEvent.class.getCanonicalName(),
				IDBErrorEvent.class.getCanonicalName(),
				KeyRangeFactory.class.getCanonicalName(),
				Cursor.class.getCanonicalName(), 
				CursorDirection.class.getCanonicalName(), 
				StringUtils.class.getCanonicalName()
		};
		return imports;
	}
}
