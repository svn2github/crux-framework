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
package org.cruxframework.crux.core.rebind.offline;

import java.util.List;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.ConfigurationProperty;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.EmittedArtifact.Visibility;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;

/**
 * @author Thiago da Rosa de Bustamante
 * 
 */
@LinkerOrder(Order.POST)
public class AppCacheLinker extends AbstractLinker
{
	@Override
	public String getDescription()
	{
		return "Appcache manifest generator";
	}

	@Override
	public ArtifactSet link(TreeLogger logger, LinkerContext context, ArtifactSet artifacts) throws UnableToCompleteException
	{
		String moduleName = context.getModuleName();
		String initialPage = getInitialPage(context);
		
		ArtifactSet artifactset = new ArtifactSet(artifacts);

		StringBuilder builder = new StringBuilder("CACHE MANIFEST\n");
		builder.append("# Build Time ["+getCurrentTimeTruncatingMiliseconds()+"]\n");
		builder.append("CACHE:\n");

		if (initialPage != null)
		{
			builder.append(initialPage+"\n");
		}
		
		for (EmittedArtifact emitted : artifacts.find(EmittedArtifact.class))
		{
			if (emitted.getVisibility() == Visibility.Private)
			{
				continue;
			}
			String pathName = emitted.getPartialPath();
			if (pathName.endsWith("symbolMap") || pathName.endsWith(".xml.gz") || pathName.endsWith("rpc.log")
			   || pathName.endsWith("gwt.rpc") || pathName.endsWith("manifest.txt") || pathName.startsWith("rpcPolicyManifest"))
			{
				continue;
			}
			builder.append("/"+moduleName+"/"+pathName).append("\n");
		}
		builder.append("/"+moduleName+"/hosted.html\n");
		builder.append("/"+moduleName+"/"+moduleName+".nocache.js\n");
		builder.append("NETWORK:\n");
		builder.append("*\n");
		EmittedArtifact manifest = emitString(logger, builder.toString(), getManifestName(context));
		artifactset.add(manifest);
		return artifactset;
	}

	private long getCurrentTimeTruncatingMiliseconds()
    {
	    long currentTime = (System.currentTimeMillis()/1000) * 1000;
		return currentTime;
    }

	private String getInitialPage(LinkerContext context)
    {
	    for (ConfigurationProperty property : context.getConfigurationProperties())
        {
			if (property.getName().equals("crux.appcache.initialpage"))
			{
				List<String> values = property.getValues();
				return values.isEmpty() ? null : values.get(0);
			}
        }
	    return null;
    }

	private String getManifestName(LinkerContext context)
    {
		String name = "offline.appcache";
	    for (ConfigurationProperty property : context.getConfigurationProperties())
        {
			if (property.getName().equals("crux.appcache.manifestName"))
			{
				List<String> values = property.getValues();
				if (!values.isEmpty())
				{
					name = values.get(0);
					break;
				}
			}
        }
	    return name;
    }
}