/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.rebind;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.core.client.serializer.BooleanSerializer;
import br.com.sysmap.crux.core.client.serializer.ByteSerializer;
import br.com.sysmap.crux.core.client.serializer.CharacterSerializer;
import br.com.sysmap.crux.core.client.serializer.DateSerializer;
import br.com.sysmap.crux.core.client.serializer.DoubleSerializer;
import br.com.sysmap.crux.core.client.serializer.FloatSerializer;
import br.com.sysmap.crux.core.client.serializer.IntegerSerializer;
import br.com.sysmap.crux.core.client.serializer.LongSerializer;
import br.com.sysmap.crux.core.client.serializer.SQLDateSerializer;
import br.com.sysmap.crux.core.client.serializer.ShortSerializer;
import br.com.sysmap.crux.core.client.serializer.StringBufferSerializer;
import br.com.sysmap.crux.core.client.serializer.StringBuilderSerializer;
import br.com.sysmap.crux.core.client.serializer.StringSerializer;
import br.com.sysmap.crux.core.client.serializer.TimestampSerializer;
import br.com.sysmap.crux.core.rebind.screen.Screen;
import br.com.sysmap.crux.core.rebind.screen.serializable.Serializers;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class RegisteredCruxSerializablesGenerator extends AbstractRegisteredElementsGenerator
{
	@Override
	protected void generateClass(TreeLogger logger, GeneratorContext context,JClassType classType, List<Screen> screens) 
	{
		String packageName = classType.getPackage().getName();
		String className = classType.getSimpleSourceName();
		String implClassName = className + "Impl";

		PrintWriter printWriter = context.tryCreate(logger, packageName, implClassName);
		// if printWriter is null, source code has ALREADY been generated, return
		if (printWriter == null) return;

		ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, implClassName);
		composer.addImplementedInterface("br.com.sysmap.crux.core.client.screen.RegisteredCruxSerializables");
		SourceWriter sourceWriter = null;
		sourceWriter = composer.createSourceWriter(context, printWriter);

		generateConstructor(logger, sourceWriter, screens, implClassName);
		sourceWriter.println("private java.util.Map<String,CruxSerializable> serializers = new java.util.HashMap<String,CruxSerializable>();");

		sourceWriter.println("public CruxSerializable getCruxSerializable(String type){");
		sourceWriter.println("return serializers.get(type);");
		sourceWriter.println("}");

		sourceWriter.println("public void registerCruxSerializable(String type, CruxSerializable moduleShareable){");
		sourceWriter.println("serializers.put(type, moduleShareable);");
		sourceWriter.println("}");

		
		sourceWriter.outdent();
		sourceWriter.println("}");

		context.commit(logger, printWriter);
	}
	
	protected void generateConstructor(TreeLogger logger, SourceWriter sourceWriter, List<Screen> screens, String implClassName) 
	{
		sourceWriter.println("public "+implClassName+"(){ ");

		Map<String, Boolean> added = new HashMap<String, Boolean>();
		generateDefaultSerializersBlock(sourceWriter);
		
		for (Screen screen : screens)
		{
			Iterator<String> iterator = screen.iterateSerializers();
			while (iterator.hasNext())
			{
				String serializer = iterator.next();
				generateSerialisersBlock(logger, sourceWriter, serializer, added);
			}
		}
		sourceWriter.println("}");
	}

	private void generateDefaultSerializersBlock(SourceWriter sourceWriter)
	{
		sourceWriter.println("serializers.put(\"java.lang.Boolean\", new " + BooleanSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.lang.Byte\", new " + ByteSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.lang.Character\", new " + CharacterSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.util.Date\", new " + DateSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.lang.Double\", new " + DoubleSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.lang.Float\", new " + FloatSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.lang.Integer\", new " + IntegerSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.lang.Long\", new " + LongSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.lang.Short\", new " + ShortSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.sql.Date\", new " + SQLDateSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.lang.StringBuffer\", new " + StringBufferSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.lang.StringBuilder\", new " + StringBuilderSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.lang.String\", new " + StringSerializer.class.getName() + "());");
		sourceWriter.println("serializers.put(\"java.lang.Timestamp\", new " + TimestampSerializer.class.getName() + "());");
	} 
		
	protected void generateSerialisersBlock(TreeLogger logger, SourceWriter sourceWriter, String serializer, Map<String, Boolean> added)
	{
		try
		{
			if (!added.containsKey(serializer) && Serializers.getCruxSerializable(serializer)!= null)
			{
				Class<?> serializerClass = Serializers.getCruxSerializable(serializer);
				sourceWriter.println("serializers.put(\""+serializerClass.getName()+"\", new " + getClassSourceName(serializerClass) + "());");
				added.put(serializer, true);
			}
		}
		catch (Throwable e) 
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredCruxSerializable(serializer, e.getLocalizedMessage()), e);
		}
	}
}
