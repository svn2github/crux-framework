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

import org.cruxframework.crux.core.client.db.AbstractObjectStore;
import org.cruxframework.crux.core.client.db.Cursor;
import org.cruxframework.crux.core.client.db.DBMessages;
import org.cruxframework.crux.core.client.db.DatabaseCursorCallback;
import org.cruxframework.crux.core.client.db.DatabaseRetrieveCallback;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore.IDBObjectCursorRequest;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore.IDBObjectRetrieveRequest;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBCursorEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBErrorEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBObjectRetrieveEvent;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContextExt;
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

	public ObjectStoreProxyCreator(GeneratorContextExt context, TreeLogger logger, JClassType targetObjectType, String objectStoreName, String[] keyPath)
	{
		super(context, logger, targetObjectType, objectStoreName, keyPath);
		this.abstractObjectStoreType = context.getTypeOracle().findType(AbstractObjectStore.class.getCanonicalName());
		this.idbObjectStoreVariable = "idbObjectStore";
	}
	
	@Override
	protected void generateProxyFields(SourcePrinter srcWriter) throws CruxGeneratorException
	{
	    super.generateProxyFields(srcWriter);
	    srcWriter.println("private "+DBMessages.class.getCanonicalName()+" messages = GWT.create("+DBMessages.class.getCanonicalName()+".class);");
	}

	@Override
	protected void generateProxyContructor(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("public "+getProxySimpleName()+"(IDBObjectStore idbObjectStore){");
		srcWriter.println("super(idbObjectStore);");
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
		srcWriter.println("public void delete("+getKeyTypeName()+" key){");
		if (hasCompositeKey())
		{
			srcWriter.println(idbObjectStoreVariable+".delete(getNativeKey(key));");
		}
		else
		{
			srcWriter.println(idbObjectStoreVariable+".delete(key);");
		}
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateDeleteRangeMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void delete(KeyRange<"+getKeyTypeName()+"> keyRange){");
		srcWriter.println(idbObjectStoreVariable+".delete(keyRange.getNativeKeyRange());");
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateAddMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void add("+getTargetObjectClassName()+" object){");
		srcWriter.println("if (object == null){");
		srcWriter.println("throw new NullPointerException();");
		srcWriter.println("}");
		if (isEmptyType())
		{
			srcWriter.println(idbObjectStoreVariable+".add(object);");
		}
		else
		{
			srcWriter.println(idbObjectStoreVariable+".add("+serializerVariable+".encode(object).isObject().getJavaScriptObject());");
		}
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generatePutMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void put("+getTargetObjectClassName()+" object){");
		srcWriter.println("if (object == null){");
		srcWriter.println("throw new NullPointerException();");
		srcWriter.println("}");
		if (isEmptyType())
		{
			srcWriter.println(idbObjectStoreVariable+".put(object);");
		}
		else
		{
			srcWriter.println(idbObjectStoreVariable+".put("+serializerVariable+".encode(object).isObject().getJavaScriptObject());");
		}
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
		srcWriter.println("retrieveRequest.onSuccess(new IDBObjectRetrieveEvent.Handler(){");
		srcWriter.println("public void onSuccess(IDBObjectRetrieveEvent event){");
		if (isEmptyType())
		{
			srcWriter.println("callback.onSuccess(event.getObject());");
		}
		else
		{
			srcWriter.println("callback.onSuccess("+serializerVariable+".decode(new JSONObject(event.getObject())));");
		}
		srcWriter.println("}");
		srcWriter.println("});");

		srcWriter.println("retrieveRequest.onError(new IDBErrorEvent.Handler(){");
		srcWriter.println("public void onError(IDBErrorEvent event){");
		srcWriter.println("callback.onFailed(messages.objectStoreGetError(event.getName()));");
		srcWriter.println("}");
		srcWriter.println("});");
				
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateOpenCursorMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void openCursor(final DatabaseCursorCallback<"+getKeyTypeName()+", "+getTargetObjectClassName()+"> callback){");
		srcWriter.println("IDBObjectCursorRequest cursorRequest = " + idbObjectStoreVariable+".openCursor();");
		srcWriter.println("cursorRequest.onSuccess(new IDBCursorEvent.Handler(){");
		srcWriter.println("public void onSuccess(IDBCursorEvent event){");
		String cursorClassName = new CursorProxyCreator(context, logger, targetObjectType, objectStoreName, keyPath).create();
		srcWriter.println("callback.onSuccess(new "+cursorClassName+"(event.getCursor()));");
		srcWriter.println("}");
		srcWriter.println("});");

		srcWriter.println("cursorRequest.onError(new IDBErrorEvent.Handler(){");
		srcWriter.println("public void onError(IDBErrorEvent event){");
		srcWriter.println("callback.onFailed(messages.objectStoreCursorError(event.getName()));");
		srcWriter.println("}");
		srcWriter.println("});");
		
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
				IDBObjectRetrieveEvent.class.getCanonicalName(), 
				IDBObjectRetrieveRequest.class.getCanonicalName(), 
				JSONObject.class.getCanonicalName(), 
				DatabaseCursorCallback.class.getCanonicalName(), 
				IDBObjectCursorRequest.class.getCanonicalName(), 
				IDBCursorEvent.class.getCanonicalName(),
				IDBErrorEvent.class.getCanonicalName(),
				Cursor.class.getCanonicalName(), 
				GWT.class.getCanonicalName()
		};
		return imports;
	}
}
