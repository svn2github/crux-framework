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

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
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

	public JsonEncoderProxyCreator(TreeLogger logger, GeneratorContextExt context, JClassType baseIntf)
	{
		super(logger, context, baseIntf, true);
		JClassType jsonEncoderType = context.getTypeOracle().findType(JsonEncoder.class.getCanonicalName());
		JClassType[] interfaces = baseIntf.getImplementedInterfaces();
		for (JClassType intf : interfaces)
		{
			if (intf.equals(jsonEncoderType))
			{
				targetObjectType = intf.isParameterized().getTypeArgs()[0];
				break;
			}
		}
	}

	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter)
	{
		String serializerName = new JSonSerializerProxyCreator(context, logger, targetObjectType).create();;
		srcWriter.println("public String encode(" + targetObjectType.getParameterizedQualifiedSourceName() + " object){");
		srcWriter.println("return "+serializerName+".encode(object);");
		srcWriter.println("}");
		srcWriter.println();

		srcWriter.println("public " + targetObjectType.getParameterizedQualifiedSourceName() + " decode(String jsonText){");
		srcWriter.println("return "+serializerName+".decode(jsonText);");
		srcWriter.println("}");
		srcWriter.println();

		srcWriter.println("public JSONValue encodeToJSON(" + targetObjectType.getParameterizedQualifiedSourceName() + " object){");
		srcWriter.println("return "+serializerName+".encodeToJSON(object);");
		srcWriter.println("}");
		srcWriter.println();

		srcWriter.println("public " + targetObjectType.getParameterizedQualifiedSourceName() + " decodeFromJSON(JSONValue json){");
		srcWriter.println("return "+serializerName+".decodeFromJSON(json);");
		srcWriter.println("}");
	}

	@Override
	protected String[] getImports()
	{
		return new String[]{JsonUtils.class.getCanonicalName(), JSONValue.class.getCanonicalName()};
	}

}
