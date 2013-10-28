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
package org.cruxframework.crux.core.rebind.database.sql;

import java.io.PrintWriter;
import java.util.HashSet;

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.db.WSQLAbstractObjectStore.EncodeCallback;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;

import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SQLKeyCursorProxyCreator extends SQLCursorProxyCreator
{
	public SQLKeyCursorProxyCreator(GeneratorContext context, TreeLogger logger, JClassType targetObjectType, 
								String objectStoreName, boolean autoIncrement, String[] keyPath, String[] objectStoreKeyPath, String indexName)
	{
		super(context, logger, targetObjectType, objectStoreName, autoIncrement, new HashSet<String>(), keyPath, objectStoreKeyPath, indexName);
	}

	@Override
	protected void generateProxyFields(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("private "+Array.class.getCanonicalName()+"<String> keyPath;");
		srcWriter.println("private "+Array.class.getCanonicalName()+"<String> indexColumnNames;");
	}
	
	@Override
	protected void generateProxyContructor(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("public "+getProxySimpleName()+"(WSQLAbstractDatabase db, WSQLKeyRange<"+getKeyTypeName()+"> range, CursorDirection direction, WSQLTransaction transaction){");
		srcWriter.println("super(db, range, "+EscapeUtils.quote(objectStoreName)+", direction, transaction);");
		populateKeyPathVariable(srcWriter);
		srcWriter.println("}");
	}

	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		generateGetNativeKeyMethod(srcWriter);
		generateGetKeyMethod(srcWriter);
		generateGetPrimaryKeyMethod(srcWriter);
		if (hasCompositeKey())
		{
			generateFromNativeValueMethod(srcWriter, keyPath);
		}
		generateGetIndexedColumnNamesMethod(srcWriter);
		generateGetKeyPathMethod(srcWriter);
		generateAddKeyRangeToQueryMethod(srcWriter);
		generateAddKeyToQueryMethod(srcWriter);
		generateAddPrimaryKeyToQueryMethod(srcWriter);
		generateDecodeObjectMethod(srcWriter);
		generateEncodeObjectMethod(srcWriter);
		generateUpdateMethod(srcWriter);
	}
	
	protected void generateUpdateMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public void update("+getKeyTypeName(objectStoreKeyPath)+" value){");
		srcWriter.println("}");
    }
	
	protected void generateGetValue(SourcePrinter srcWriter)
    {
		srcWriter.println("public void getValue("+getKeyTypeName(objectStoreKeyPath)+" value){");
		srcWriter.println("return getPrimaryKey();");
		srcWriter.println("}");
    }

	@Override
	protected void generateEncodeObjectMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("protected void encodeObject("+getKeyTypeName(objectStoreKeyPath)+" object, "+EncodeCallback.class.getCanonicalName()+" callback){");
		srcWriter.println("}");
	}

	@Override
	protected void generateDecodeObjectMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("protected "+getKeyTypeName(objectStoreKeyPath)+" decodeObject(String encodedObject){");
		srcWriter.println("return null;");
		srcWriter.println("}");
	}
	
	@Override
	public String getProxyQualifiedName()
	{
		return cursorType.getPackage().getName()+"."+getProxySimpleName();
	}

	@Override
	public String getProxySimpleName()
	{
		return cursorName.replaceAll("\\W", "_")+"_SQL_KeyCursor";
	}

	@Override
	protected SourcePrinter getSourcePrinter()
	{
		String packageName = cursorType.getPackage().getName();
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
		composerFactory.setSuperclass("WSQLCursor<"+getKeyTypeName()+","+getKeyTypeName(objectStoreKeyPath)+","+getKeyTypeName(objectStoreKeyPath)+">");

		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);
	}
	
	protected String[] getImports()
	{
		String[] imports = new String[] {
				Array.class.getCanonicalName(),
				JSONObject.class.getCanonicalName(),
				JsArrayMixed.class.getCanonicalName()
		};
		return imports;
	}
}
