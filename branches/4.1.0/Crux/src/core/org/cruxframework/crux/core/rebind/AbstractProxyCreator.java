/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.core.rebind;

import java.util.List;

import org.cruxframework.crux.core.config.ConfigurationFactory;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.ConfigurationProperty;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractProxyCreator
{
	protected GeneratorContextExt context;
	protected TreeLogger logger;

	/**
	 * @param logger
	 * @param context
	 * @param crossDocumentIntf
	 */
	public AbstractProxyCreator(TreeLogger logger, GeneratorContextExt context)
    {
		this.logger = logger;
		this.context = context;
    }
	
	/**
	 * Creates the proxy.
	 * 
	 * @return a proxy class name .
	 * @throws CruxGeneratorException 
	 */
	public String create() throws CruxGeneratorException
	{
		SourcePrinter printer = getSourcePrinter();
		if (printer == null)
		{
			return getProxyQualifiedName();
		}

		generateSubTypes(printer);
		generateProxyContructor(printer);
		generateProxyMethods(printer);
		generateProxyFields(printer);

		printer.commit();
		return getProxyQualifiedName();
	}

	/**
	 * Generate the proxy constructor and delegate to the superclass constructor
	 * using the default address for the
	 * {@link com.google.gwt.user.client.rpc.RemoteService RemoteService}.
	 */
	protected void generateProxyContructor(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		
	}
	
	/**
	 * Generate any fields required by the proxy.
	 * @throws CruxGeneratorException 
	 */
	protected void generateProxyFields(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		
	}

	/**
	 * @param srcWriter
	 * @param serializableTypeOracle
	 * @throws CruxGeneratorException 
	 */
	protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		
	}
	
	/**
	 * Override this method to generate any nested type required by the proxy
	 * @param srcWriter
	 * @throws CruxGeneratorException 
	 */
	protected void generateSubTypes(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		
	}
	
	/**
	 * @param method
	 * @return
	 */
	protected String getJsniSimpleSignature(JMethod method)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(method.getName());
		sb.append("(");
		for (JParameter param : method.getParameters())
		{
			sb.append(param.getType().getJNISignature());
		}
		sb.append(")");
		return sb.toString();
	}
	
	/**
	 * @return the full qualified name of the proxy object.
	 */
	public abstract String getProxyQualifiedName();
	
	
	/**
	 * @return the simple name of the proxy object.
	 */
	public abstract String getProxySimpleName();
	
	/**
	 * @return a sourceWriter for the proxy class
	 */
	protected abstract SourcePrinter getSourcePrinter();
	
	/**
	 * @return
	 */
	protected boolean isCrux2OldInterfacesCompatibilityEnabled()
    {
		String value;
		try
        {
	        ConfigurationProperty property = context.getPropertyOracle().getConfigurationProperty("enableCrux2OldInterfacesCompatibility");
	        List<String> values = property.getValues();
	        if (values != null && values.size() > 0)
	        {
	        	value = values.get(0);
	        }
	        else
	        {
	            value = ConfigurationFactory.getConfigurations().enableCrux2OldInterfacesCompatibility();
	        }
        }
        catch (BadPropertyValueException e)
        {
            value = ConfigurationFactory.getConfigurations().enableCrux2OldInterfacesCompatibility();
        }
        return Boolean.parseBoolean(value);
    }
	
	/**
	 * @return
	 */
	protected boolean isCacheable()
	{
		return false;
	}
	
    /**
     * Printer for screen creation codes.
     * 
     * @author Thiago da Rosa de Bustamante
     */
    public static class SourcePrinter
    {
    	private final TreeLogger logger;
		private final SourceWriter srcWriter;

    	/**
    	 * Constructor
    	 * @param srcWriter
    	 * @param logger
    	 */
    	public SourcePrinter(SourceWriter srcWriter, TreeLogger logger)
        {
			this.srcWriter = srcWriter;
			this.logger = logger;
        }
    	
    	
    	/**
    	 * Flushes the printed code into a real output (file).
    	 */
    	public void commit()
    	{
    		srcWriter.commit(logger);
    	}
    	
    	/**
    	 * Indents the next line to be printed
    	 */
    	public void indent()
    	{
    		srcWriter.indent();
    	}
    	
    	/**
    	 * Outdents the next line to be printed
    	 */
    	public void outdent()
    	{
    		srcWriter.outdent();
    	}
    	
    	/**
    	 * Prints an in-line code.
    	 * @param s
    	 */
    	public void print(String s)
    	{
    		srcWriter.print(s);
    	}
    	
    	/**
    	 * Prints a line of code into the output. 
    	 * <li>If the line ends with <code>"{"</code>, indents the next line.</li>
    	 * <li>If the line ends with <code>"}"</code>, <code>"};"</code> or <code>"});"</code>, outdents the next line.</li>
    	 * @param s
    	 */
    	public void println(String s)
    	{
    		String line = s.trim();
    		
			if (line.endsWith("}") || line.endsWith("});") || line.endsWith("};") || line.endsWith("}-*/;") || line.startsWith("}"))
    		{
    			outdent();
    		}
			
    		srcWriter.println(s);
    		
    		if (line.endsWith("{"))
    		{
    			indent();
    		}
    	}


		public void println()
        {
			srcWriter.println();
        }
    }
}
