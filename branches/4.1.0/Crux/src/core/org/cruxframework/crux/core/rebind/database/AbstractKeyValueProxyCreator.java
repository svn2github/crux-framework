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

import org.cruxframework.crux.core.client.db.annotation.DatabaseMetadata.Empty;
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

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractKeyValueProxyCreator extends AbstractProxyCreator
{
	protected final JClassType targetObjectType;
	protected final JClassType integerType;
	protected final JClassType stringType;
	protected final JClassType emptyType;
	protected final String objectStoreName;
	protected final String[] keyPath;
	protected final String serializerVariable;

	public AbstractKeyValueProxyCreator(GeneratorContextExt context, TreeLogger logger, JClassType targetObjectType, String objectStoreName, String[] keyPath)
	{
		super(logger, context);
		this.objectStoreName = objectStoreName;
		this.keyPath = keyPath;
		this.targetObjectType = targetObjectType;
		if (JClassUtils.isSimpleType(targetObjectType))
		{
			throw new CruxGeneratorException("Simple types are not allowed as row in Crux Database. Create an wrapper Object to your value. ObjectStoreName["+objectStoreName+"]");
		}
		this.stringType = context.getTypeOracle().findType(String.class.getCanonicalName());
		this.integerType = context.getTypeOracle().findType(Integer.class.getCanonicalName());
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
	    srcWriter.println("private Object[] fromNativeKey("+JsArrayMixed.class.getCanonicalName()+" key){");
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

	protected boolean isEmptyType()
    {
	    return targetObjectType.isAssignableTo(emptyType);
    }
}