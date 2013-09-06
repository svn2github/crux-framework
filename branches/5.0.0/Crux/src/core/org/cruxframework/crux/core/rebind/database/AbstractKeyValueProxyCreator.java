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

import java.util.Date;

import org.cruxframework.crux.core.client.db.annotation.DatabaseDef.Empty;
import org.cruxframework.crux.core.client.db.indexeddb.IDBCursorWithValue;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.rest.JSonSerializerProxyCreator;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractKeyValueProxyCreator extends AbstractProxyCreator
{
	protected final JClassType targetObjectType;
	protected final JClassType integerType;
	protected final JClassType doubleType;
	protected final JClassType stringType;
	protected final JClassType dateType;
	protected final JClassType emptyType;
	protected final String objectStoreName;
	protected final String[] keyPath;
	protected final String serializerVariable;

	public AbstractKeyValueProxyCreator(GeneratorContext context, TreeLogger logger, JClassType targetObjectType, String objectStoreName, String[] keyPath)
	{
		super(logger, context, false);
		this.objectStoreName = objectStoreName;
		this.keyPath = keyPath;
		this.targetObjectType = targetObjectType;
		if (JClassUtils.isSimpleType(targetObjectType))
		{
			throw new CruxGeneratorException("Simple types are not allowed as row in Crux Database. Create an wrapper Object to your value. ObjectStoreName["+objectStoreName+"]");
		}
		this.stringType = context.getTypeOracle().findType(String.class.getCanonicalName());
		this.integerType = context.getTypeOracle().findType(Integer.class.getCanonicalName());
		this.doubleType = context.getTypeOracle().findType(Double.class.getCanonicalName());
		this.dateType = context.getTypeOracle().findType(Date.class.getCanonicalName());
		this.emptyType = context.getTypeOracle().findType(Empty.class.getCanonicalName());
		this.serializerVariable = "serializer";
	}
	
	@Override
	protected void generateProxyFields(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		if (!isEmptyType())
		{
			String serializerName = new JSonSerializerProxyCreator(context, logger, targetObjectType).create();;
			srcWriter.println("private "+serializerName+" "+serializerVariable+" = new "+serializerName+"();");
		}
	}

	protected boolean hasCompositeKey()
    {
	    return (keyPath.length > 1) && (!isEmptyType());
    }

	protected void generateFromNativeKeyMethod(SourcePrinter srcWriter)
    {
		generateFromNativeKeyMethod(srcWriter, keyPath);
    }
	
	protected void generateFromNativeValueMethod(SourcePrinter srcWriter, String[] keyPath)
    {
		generateFromNativeMethod(srcWriter, "fromNativeValue", keyPath);
    }
	
	protected void generateFromNativeKeyMethod(SourcePrinter srcWriter, String[] keyPath)
    {
		generateFromNativeMethod(srcWriter, "fromNativeKey", keyPath);
    }
	
	protected void generateFromNativeMethod(SourcePrinter srcWriter, String methodName, String[] keyPath)
    {
	    srcWriter.println("private Object[] "+methodName+"("+JsArrayMixed.class.getCanonicalName()+" key){");
	    srcWriter.println("Object[] result = new Object[key.length()];");
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
        	    srcWriter.println("result["+i+"] = key.getString("+i+");");
        	}
        	else if (jType.equals(integerType) || (jType.equals(JPrimitiveType.INT)))
        	{
        	    srcWriter.println("result["+i+"] = (int)key.getNumber("+i+");");
        	}
        	else if (jType.equals(doubleType) || (jType.equals(JPrimitiveType.DOUBLE)))
        	{
        	    srcWriter.println("result["+i+"] = key.getNumber("+i+");");
        	}
        	else if (jType.equals(dateType))
        	{
        	    srcWriter.println("result["+i+"] = new "+Date.class.getCanonicalName()+"((long)key.getNumber("+i+"));");
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
    }

	protected void generateCreateArrayMethod(SourcePrinter srcWriter)
    {
	    srcWriter.println("private native "+JsArrayMixed.class.getCanonicalName()+" createArray()/*-{");
	    srcWriter.println("return [];");
	    srcWriter.println("}-*/;");
	    srcWriter.println();
    }
	
	protected void generateGetNativeKeyMethod(SourcePrinter srcWriter)
    {
	    generateCreateArrayMethod(srcWriter);
	    
	    srcWriter.println("private "+JsArrayMixed.class.getCanonicalName()+" getNativeKey(Object[] key){");
	    srcWriter.println(JsArrayMixed.class.getCanonicalName()+ " result = createArray();");
	    
	    int i=0;
	    for (String key : keyPath)
        {
			JType jType = JClassUtils.getTypeForProperty(key, targetObjectType);
			if (jType == null)
			{
				throw new CruxGeneratorException("Invalid keyPath for objectStore ["+targetObjectType.getParameterizedQualifiedSourceName()+"]");
			}
        	if (jType.equals(stringType))
        	{
        	    srcWriter.println("result.push((String)key["+i+"]);");
        	}
        	else if (jType.equals(integerType) || (jType.equals(JPrimitiveType.INT)))
        	{
        	    srcWriter.println("result.push((int)key["+i+"]);");
        	}
        	else if (jType.equals(doubleType) || (jType.equals(JPrimitiveType.DOUBLE)))
        	{
        	    srcWriter.println("result.push((double)key["+i+"]);");
        	}
        	else if (jType.equals(dateType))
        	{
        	    srcWriter.println("result.push((double)(("+Date.class.getCanonicalName()+")key["+i+"]).getTime());");
        	}
        	else
        	{
        		throw new CruxGeneratorException("Invalid key type for objectStore ["+targetObjectType.getParameterizedQualifiedSourceName()+"]");
        	}
        	i++;
        }
	    srcWriter.println("return result;");
	    srcWriter.println("}");
    }

	protected String getTargetObjectClassName()
    {
		if (isEmptyType())
		{
			return JavaScriptObject.class.getCanonicalName();
		}
	    return targetObjectType.getParameterizedQualifiedSourceName();
    }
	
	protected String getKeyTypeName()
	{
		return getKeyTypeName(keyPath);
	}

	protected String getKeyTypeName(String[] keyPath)
	{
		if (isEmptyType())
		{
			return JsArrayMixed.class.getCanonicalName();
		}
		if (keyPath.length >= 2)
		{
			return "Object[]";
		}
		else if (keyPath.length == 1)
		{
			JType jType = JClassUtils.getTypeForProperty(keyPath[0], targetObjectType);
			if (jType == null)
			{
				throw new CruxGeneratorException("Invalid keyPath for objectStore ["+targetObjectType.getParameterizedQualifiedSourceName()+"]");
			}
        	if (jType.equals(stringType))
        	{
        		return "String";
        	}
        	else if (jType.equals(integerType) || (jType.equals(JPrimitiveType.INT)))
        	{
        		return "Integer";
        	}
        	else if (jType.equals(doubleType) || (jType.equals(JPrimitiveType.DOUBLE)))
        	{
        		return "Double";
        	}
        	else if (jType.equals(dateType))
        	{
        		return Date.class.getCanonicalName();
        		
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
	
	protected boolean isEmptyType()
    {
	    return targetObjectType.isAssignableTo(emptyType);
    }
	
	protected void generateGetKeyRangeFactoryMethod(SourcePrinter srcWriter, String parentName)
    {
		srcWriter.println("public KeyRangeFactory<"+getKeyTypeName()+"> getKeyRangeFactory(){");
		String keyRangeFatoryClassName = new KeyRangeFactoryProxyCreator(context, logger, targetObjectType, objectStoreName, keyPath, parentName).create();
		srcWriter.println("return (KeyRangeFactory<"+getKeyTypeName()+">) new "+keyRangeFatoryClassName+"();");
		srcWriter.println("}");
		srcWriter.println();
    }

	protected void generateGetCallbacks(SourcePrinter srcWriter, String callbackVar, String dbVariable, String retrieveRequestVar)
    {		
		srcWriter.println("if ("+callbackVar+" != null || "+dbVariable+".errorHandler != null){");
		srcWriter.println("if ("+callbackVar+" != null){");
		srcWriter.println(""+callbackVar+".setDb("+dbVariable+");");
		srcWriter.println("}");

		srcWriter.println(retrieveRequestVar+".onSuccess(new IDBObjectRetrieveEvent.Handler(){");
		srcWriter.println("public void onSuccess(IDBObjectRetrieveEvent event){");
		srcWriter.println("if ("+callbackVar+" != null){");
		if (isEmptyType())
		{
			srcWriter.println(""+callbackVar+".onSuccess(event.getObject());");
		}
		else
		{
			srcWriter.println("if (event.getObject() != null){");
			srcWriter.println(""+callbackVar+".onSuccess("+serializerVariable+".decode(new JSONObject(event.getObject())));");
			srcWriter.println("}else{");
			srcWriter.println(""+callbackVar+".onSuccess(null);");
			srcWriter.println("}");
		}
		srcWriter.println(""+callbackVar+".setDb(null);");
		srcWriter.println("}");
		srcWriter.println("}");
		srcWriter.println("});");

		srcWriter.println(retrieveRequestVar+".onError(new IDBErrorEvent.Handler(){");
		srcWriter.println("public void onError(IDBErrorEvent event){");
		srcWriter.println("if ("+callbackVar+" != null){");
		srcWriter.println(""+callbackVar+".onError("+dbVariable+".messages.objectStoreGetError(event.getName()));");
		srcWriter.println(""+callbackVar+".setDb(null);");
		srcWriter.println("} else if ("+dbVariable+".errorHandler != null){");
		srcWriter.println(dbVariable+".errorHandler.onError("+dbVariable+".messages.objectStoreGetError(event.getName()));");
		srcWriter.println("}");
		srcWriter.println("}");
		srcWriter.println("});");

		srcWriter.println("}");
    }

	protected void generateWriteCallbacks(SourcePrinter srcWriter, String callbackVar, String dbVariable, String writeRequestVar)
    {		
		srcWriter.println("if ("+callbackVar+" != null || "+dbVariable+".errorHandler != null){");
		srcWriter.println("if ("+callbackVar+" != null){");
		srcWriter.println(""+callbackVar+".setDb("+dbVariable+");");
		srcWriter.println("}");

		srcWriter.println(writeRequestVar+".onSuccess(new IDBObjectStoreEvent.Handler(){");
		srcWriter.println("public void onSuccess(IDBObjectStoreEvent event){");
		srcWriter.println("if ("+callbackVar+" != null){");

		String keyTypeName = getKeyTypeName();
		if (hasCompositeKey())
		{
			srcWriter.println(callbackVar+".onSuccess(fromNativeKey(event.getObjectKey()));");
		}
		else if (keyTypeName.equals("String"))
		{
			srcWriter.println(callbackVar+".onSuccess(event.getStringKey());");
		}
		else if (keyTypeName.equals("Integer"))
		{
			srcWriter.println(callbackVar+".onSuccess(event.getIntKey());");
		}
		else if (keyTypeName.equals("Double"))
		{
			srcWriter.println(callbackVar+".onSuccess(event.getDoubleKey());");
		}
		else if (keyTypeName.equals(Date.class.getCanonicalName()))
		{
			srcWriter.println(callbackVar+".onSuccess(event.getDateKey());");
		}
		else
		{
			srcWriter.println(callbackVar+".onSuccess(event.getObjectKey().cast());");
		}
		
		srcWriter.println(""+callbackVar+".setDb(null);");
		srcWriter.println("}");
		srcWriter.println("}");
		srcWriter.println("});");

		srcWriter.println(writeRequestVar+".onError(new IDBErrorEvent.Handler(){");
		srcWriter.println("public void onError(IDBErrorEvent event){");
		srcWriter.println("if ("+callbackVar+" != null){");
		srcWriter.println(""+callbackVar+".onError("+dbVariable+".messages.objectStoreWriteError(event.getName()));");
		srcWriter.println(""+callbackVar+".setDb(null);");
		srcWriter.println("} else if ("+dbVariable+".errorHandler != null){");
		srcWriter.println(dbVariable+".errorHandler.onError("+dbVariable+".messages.objectStoreWriteError(event.getName()));");
		srcWriter.println("}");
		srcWriter.println("}");
		srcWriter.println("});");

		srcWriter.println("}");
    }
	
	protected void generateCursorHandlers(SourcePrinter srcWriter, String callbackVar, String dbVariable, String cursorRequestVar, String cursorName)
	{
		srcWriter.println("if ("+callbackVar+" != null || "+dbVariable+".errorHandler != null){");
		srcWriter.println("if ("+callbackVar+" != null){");
		srcWriter.println(""+callbackVar+".setDb("+dbVariable+");");
		srcWriter.println("}");
		
		srcWriter.println(cursorRequestVar+".onSuccess(new IDBCursorEvent.Handler(){");
		srcWriter.println("public void onSuccess(IDBCursorEvent event){");
		String cursorClassName = new CursorProxyCreator(context, logger, targetObjectType, objectStoreName, keyPath, cursorName).create();
		srcWriter.println(IDBCursorWithValue.class.getCanonicalName()+" cursor = event.getCursor();");
		srcWriter.println("if ("+callbackVar+" != null){");
		srcWriter.println("if(cursor != null){");
		srcWriter.println(""+callbackVar+".onSuccess(new "+cursorClassName+"(cursor));");
		srcWriter.println("}else{");
		srcWriter.println(""+callbackVar+".onSuccess(null);");
		srcWriter.println("}");
		srcWriter.println(""+callbackVar+".setDb(null);");
		srcWriter.println("}");
		srcWriter.println("}");
		srcWriter.println("});");
		
		srcWriter.println(cursorRequestVar+".onError(new IDBErrorEvent.Handler(){");
		srcWriter.println("public void onError(IDBErrorEvent event){");
		srcWriter.println("if ("+callbackVar+" != null){");
		srcWriter.println(""+callbackVar+".onError("+dbVariable+".messages.objectStoreCursorError(event.getName()));");
		srcWriter.println(""+callbackVar+".setDb(null);");
		srcWriter.println("} else if ("+dbVariable+".errorHandler != null){");
		srcWriter.println(dbVariable+".errorHandler.onError("+dbVariable+".messages.objectStoreCursorError(event.getName()));");
		srcWriter.println("}");
		srcWriter.println("}");
		srcWriter.println("});");
		
		srcWriter.println("}");
	}
	
	protected void generateGetNativeArrayKeyMethod(SourcePrinter srcWriter, String idbCursorVariable)
    {
		if (keyPath.length <= 1)
		{
			srcWriter.println("private native "+JsArrayMixed.class.getCanonicalName()+" createKeyArray(IDBCursor cursor)/*-{");
			srcWriter.println("return [cursor.key];");
			srcWriter.println("}-*/;");
			srcWriter.println();
		}

	    srcWriter.println("public JsArrayMixed getNativeArrayKey(){");
		if (keyPath.length > 1)
		{
			srcWriter.println("return "+idbCursorVariable+".getObjectKey();");
		}
		else 
		{
			srcWriter.println("return createKeyArray("+idbCursorVariable+");");
		}
		srcWriter.println("}");
    }
}
