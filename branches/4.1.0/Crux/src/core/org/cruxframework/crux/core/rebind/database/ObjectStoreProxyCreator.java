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
import org.cruxframework.crux.core.client.db.DatabaseRetrieveCallback;
import org.cruxframework.crux.core.client.db.annotation.DatabaseMetadata.Empty;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore.IDBObjectRetrieveRequest;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBObjectRetrieveEvent;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.rest.JSonSerializerProxyCreator;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ObjectStoreProxyCreator extends AbstractProxyCreator
{
	private final JClassType targetObjectType;
	private JClassType integerType;
	private JClassType stringType;
	private JClassType abstractObjectStoreType;
	private JClassType emptyType;
	private final String objectStoreName;
	private final String[] keyPath;
	private String serializerVariable;
	private String idbObjectStoreVariable;

	public ObjectStoreProxyCreator(GeneratorContextExt context, TreeLogger logger, JClassType targetObjectType, String objectStoreName, String[] keyPath)
	{
		super(logger, context);
		this.objectStoreName = objectStoreName;
		this.keyPath = keyPath;
		this.abstractObjectStoreType = context.getTypeOracle().findType(AbstractObjectStore.class.getCanonicalName());
		this.targetObjectType = targetObjectType;
		if (JClassUtils.isSimpleType(targetObjectType))
		{
			throw new CruxGeneratorException("Simple types are not allowed as row in Crux Database. Create an wrapper Object to your value. ObjectStoreName["+objectStoreName+"]");
		}
		this.stringType = context.getTypeOracle().findType(String.class.getCanonicalName());
		this.integerType = context.getTypeOracle().findType(Integer.class.getCanonicalName());
		this.emptyType = context.getTypeOracle().findType(Empty.class.getCanonicalName());
		this.serializerVariable = "serializer";
		this.idbObjectStoreVariable = "idbObjectStore";
	}
	
	@Override
	protected void generateProxyFields(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		String serializerName = new JSonSerializerProxyCreator(context, logger, targetObjectType).create();;
		srcWriter.println("private "+serializerName+" "+serializerVariable+" = new "+serializerName+"();");
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
		if (hasCompositeKey())
		{
			generateGetNativeKeyMethod(srcWriter);
		}
	}

	private boolean hasCompositeKey()
    {
	    return (keyPath.length > 1) && (!targetObjectType.isAssignableTo(emptyType));
    }

	protected void generateGetNativeKeyMethod(SourcePrinter srcWriter)
    {
	    srcWriter.println("private "+JsArrayMixed.class.getCanonicalName()+" getNativeKey(Object[] key){");
	    srcWriter.println(JsArrayMixed.class.getCanonicalName()+ " result = createArray();");
	    
	    int i=0;
	    for (String key : keyPath)
        {
	        String getterMethod = JClassUtils.getGetterMethod(key, targetObjectType);
			if (StringUtils.isEmpty(getterMethod))
			{
				throw new CruxGeneratorException("Invalid keyPath for objectStore ["+targetObjectType.getParameterizedQualifiedSourceName()+"]");
			}
			JType jType = JClassUtils.getReturnTypeFromMethodClass(targetObjectType, getterMethod, new JType[]{});
        	if (jType.equals(stringType))
        	{
        	    srcWriter.println("result.push((String)key["+i+"]);");
        	}
        	else if (jType.equals(integerType) || (jType.equals(JPrimitiveType.INT)))
        	{
        	    srcWriter.println("result.push((int)key["+i+"]);");
        	}
        	else
        	{
        		throw new CruxGeneratorException("Invalid key type for objectStore ["+targetObjectType.getParameterizedQualifiedSourceName()+"]");
        	}
        	i++;
        }
	    srcWriter.println("return result;");
	    srcWriter.println("}");
	    
	    srcWriter.println();
	    srcWriter.println("private native "+JsArrayMixed.class.getCanonicalName()+" createArray()/*-{");
	    srcWriter.println(JsArrayMixed.class.getCanonicalName()+ " result = createArray();");
	    srcWriter.println("return [];");
	    srcWriter.println("}-*/;");
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
		srcWriter.println(idbObjectStoreVariable+".add("+serializerVariable+".encode(object).isObject().getJavaScriptObject());");
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generatePutMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void put("+getTargetObjectClassName()+" object){");
		srcWriter.println("if (object == null){");
		srcWriter.println("throw new NullPointerException();");
		srcWriter.println("}");
		srcWriter.println(idbObjectStoreVariable+".put("+serializerVariable+".encode(object).isObject().getJavaScriptObject());");
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
		srcWriter.println("callback.onSuccess("+serializerVariable+".decode(new JSONObject(event.getObject())));");
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

	private String getTargetObjectClassName()
    {
		if (targetObjectType.isAssignableTo(emptyType))
		{
			return JavaScriptObject.class.getCanonicalName();
		}
	    return targetObjectType.getParameterizedQualifiedSourceName();
    }
	
	private String getKeyTypeName()
	{
		if (targetObjectType.isAssignableTo(emptyType))
		{
			return JavaScriptObject.class.getCanonicalName();
		}
		if (keyPath.length >= 2)
		{
			return "Object[]";
		}
		else if (keyPath.length == 1)
		{
			String getterMethod = JClassUtils.getGetterMethod(keyPath[0], targetObjectType);
			if (StringUtils.isEmpty(getterMethod))
			{
				throw new CruxGeneratorException("Invalid keyPath for objectStore ["+targetObjectType.getParameterizedQualifiedSourceName()+"]");
			}
			JType jType = JClassUtils.getReturnTypeFromMethodClass(targetObjectType, getterMethod, new JType[]{});
        	if (jType.equals(stringType))
        	{
        		return "String";
        	}
        	else if (jType.equals(integerType) || (jType.equals(JPrimitiveType.INT)))
        	{
        		return "Integer";
        		
        	}
        	else
        	{
        		throw new CruxGeneratorException("Invalid key type for objectStore ["+targetObjectType.getParameterizedQualifiedSourceName()+"]");
        	}
		}
		else
		{
			throw new CruxGeneratorException("can not create an objectStore without a key definition. ObjectStore["+objectStoreName+"].");
		}
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
				JSONObject.class.getCanonicalName()
		};
		return imports;
	}
}
