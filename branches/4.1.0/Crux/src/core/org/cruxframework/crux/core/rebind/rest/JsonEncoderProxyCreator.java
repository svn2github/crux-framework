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
package org.cruxframework.crux.core.rebind.rest;

import org.cruxframework.crux.core.client.service.JsonEncoder;
import org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * This class creates a client proxy for encode and decode objects to/from json
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class JsonEncoderProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	private JClassType targetObjectType;
	private JClassType javascriptObjectType;

	public JsonEncoderProxyCreator(TreeLogger logger, GeneratorContextExt context, JClassType baseIntf)
	{
		super(logger, context, baseIntf, true);
		JClassType jsonEncoderType = context.getTypeOracle().findType(JsonEncoder.class.getCanonicalName());
		targetObjectType = JClassUtils.getActualParameterTypes(baseIntf, jsonEncoderType)[0];
		javascriptObjectType = context.getTypeOracle().findType(JavaScriptObject.class.getCanonicalName());
	}

	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter)
	{
		String serializerName = new JSonSerializerProxyCreator(context, logger, targetObjectType).create();;
		srcWriter.println("public String encode(" + targetObjectType.getParameterizedQualifiedSourceName() + " object){");
		srcWriter.println("JSONValue result = "+serializerName+".encode(object);");
		srcWriter.println("if (result == null || result.isString() == null){");
		srcWriter.println("return null;");
		srcWriter.println("}");
		srcWriter.println("return result.stringValue();");
		srcWriter.println("}");
		srcWriter.println();

		srcWriter.println("public " + targetObjectType.getParameterizedQualifiedSourceName() + " decode(String jsonText){");
		JClassType objectClassType = targetObjectType.isClassOrInterface();
		if (objectClassType != null && objectClassType.isAssignableTo(javascriptObjectType))
		{
			srcWriter.println(targetObjectType.getParameterizedQualifiedSourceName()+" result = "+JsonUtils.class.getCanonicalName()+".safeEval(jsonText);");
			srcWriter.println("return result;");
		}
		srcWriter.println("JSONValue jsonValue = JSONParser.parseStrict(jsonText);");
		srcWriter.println("return "+serializerName+".decode(jsonValue);");
		srcWriter.println("}");
		srcWriter.println();
	}

	@Override
	protected String[] getImports()
	{
		return new String[]{
				JsonUtils.class.getCanonicalName(), 
				JSONValue.class.getCanonicalName(), 
				JSONParser.class.getCanonicalName()
		};
	}

}
