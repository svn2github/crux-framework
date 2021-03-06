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
package br.com.sysmap.crux.gadget.rebind.gadget;

import java.util.HashSet;
import java.util.Set;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.gadget.client.meta.GadgetFeature.ContainerFeature;
import br.com.sysmap.crux.gadget.client.meta.GadgetFeature.Feature;
import br.com.sysmap.crux.gadget.client.meta.GadgetFeature.NeedsFeatures;
import br.com.sysmap.crux.gadget.client.widget.GadgetView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.gadgets.client.UserPreferences;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class GadgetInfoProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	
	/**
	 * Constructor
	 * @param logger
	 * @param context
	 */
	public GadgetInfoProxyCreator(TreeLogger logger, GeneratorContextExt context, JClassType baseIntf)
    {
	    super(logger, context, baseIntf, false);
    }
	

	/**
	 * @param srcWriter
	 */
	protected void generateFeatureMethods(SourceWriter srcWriter)
	{
		generateFeaturesMethods(srcWriter, baseIntf, new HashSet<String>());
	}

	/**
	 * @param srcWriter
	 * @param moduleMetaClass
	 */
	protected void generateFeaturesMethods(SourceWriter srcWriter, JClassType moduleMetaClass, Set<String> added)
	{
		NeedsFeatures needsFeatures = moduleMetaClass.getAnnotation(NeedsFeatures.class);
		if (needsFeatures != null)
		{
			Feature[] features = needsFeatures.value();
			for (Feature feature : features)
			{
				if (!added.contains(feature.value().getFeatureName()))
				{
					generateFeatureMethod(srcWriter, feature.value());
					added.add(feature.value().getFeatureName());
				}
			}
		}
		
		JClassType[] interfaces = moduleMetaClass.getImplementedInterfaces();
		if (interfaces != null)
		{
			for (JClassType interfaceType : interfaces)
            {
				generateFeaturesMethods(srcWriter, interfaceType, added);
            }
		}
		
	}

	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateProxyContructor(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateProxyContructor(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }

	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateProxyFields(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }

	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateProxyMethods(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
	protected void generateProxyMethods(SourceWriter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("public " + UserPreferences.class.getCanonicalName() + " getUserPreferences(){");
		srcWriter.indent();
		srcWriter.println("return GadgetView.getGadget().getUserPreferences();");
		srcWriter.outdent();
		srcWriter.println("}");
		generateFeatureMethods(srcWriter);
	}

	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateSubTypes(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateSubTypes(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }

	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractInterfaceWrapperProxyCreator#getImports()
	 */
	@Override
    protected String[] getImports()
    {
		String[] imports = new String[] {
				UserPreferences.class.getCanonicalName(),
				GadgetView.class.getCanonicalName(),
				GWT.class.getCanonicalName(),
				Crux.class.getCanonicalName(),
				Window.class.getCanonicalName()
		};
		return imports;       
    }

	/**
	 * @param srcWriter
	 * @param feature
	 */
	private void generateFeatureMethod(SourceWriter srcWriter, ContainerFeature feature)
	{
		Class<?> featureClass = feature.getFeatureClass();
		srcWriter.println("public " + featureClass.getCanonicalName() + " get"+featureClass.getSimpleName()+"(){");
		srcWriter.indent();
		srcWriter.println("return GadgetView.getGadget().get"+featureClass.getSimpleName()+"();");
		srcWriter.outdent();
		srcWriter.println("}");
	}
}
