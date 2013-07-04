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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.Artifact;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.core.ext.linker.ConfigurationProperty;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.EmittedArtifact.Visibility;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.Shardable;

/**
 * A GWT linker that produces an offline.appcache file describing what to cache in
 * the application cache. It produces one appcache file for each permutation.
 * 
 * @author Thiago da Rosa de Bustamante
 */
@LinkerOrder(LinkerOrder.Order.POST)
@Shardable
public class AppCacheLinker extends AbstractLinker
{
	private final HashSet<String> cachedArtifacts = new HashSet<String>();
	private static Set<String> allArtifacts = Collections.synchronizedSet(new HashSet<String>());
	private static Map<String, Set<String>> generatedManifestResources = Collections.synchronizedMap(new HashMap<String, Set<String>>());
	private List<String> acceptedFileExtensions = Arrays.asList(".html", ".js", ".css", ".png", ".jpg", ".gif", ".ico");

	@Override
	public String getDescription()
	{
		return "HTML5 appcache manifest generator";
	}

	@Override
	public ArtifactSet link(TreeLogger logger, LinkerContext context, ArtifactSet artifacts, boolean onePermutation) throws UnableToCompleteException
	{
		ArtifactSet artifactset = new ArtifactSet(artifacts);

		if (onePermutation)
		{
			analyzePermutationArtifacts(artifacts);
		}
		else
		{
			emitPermutationsAppCache(logger, context, artifacts, artifactset);
			emitMainAppCache(logger, context, artifacts, artifactset);
		}
		
		return artifactset;
	}

	private void analyzePermutationArtifacts(ArtifactSet artifacts)
    {
	    String permutationId = getPermutationId(artifacts);

	    SortedSet<String> hashSet = new TreeSet<String>();
		for (EmittedArtifact emitted : artifacts.find(EmittedArtifact.class))
		{
			if (emitted.getVisibility() == Visibility.Private)
			{
				continue;
			}
    		String pathName = emitted.getPartialPath();
    		if (acceptCachedResource(pathName))
    		{
    			hashSet.add(pathName);
    			allArtifacts.add(pathName);
    		}
	    }
	    generatedManifestResources.put(permutationId, hashSet);
    }

    private void emitPermutationsAppCache(TreeLogger logger, LinkerContext context, ArtifactSet artifacts, ArtifactSet artifactset) throws UnableToCompleteException
    {
		for (EmittedArtifact emitted : artifacts.find(EmittedArtifact.class))
		{
			if (emitted.getVisibility() == Visibility.Private)
			{
				continue;
			}
			String pathName = emitted.getPartialPath();
			if (acceptCachedResource(pathName))
			{
				if (!allArtifacts.contains(pathName))
				{
					// common stuff like clear.cache.gif, *.nocache.js, etc
					cachedArtifacts.add(pathName);
				}
			}
	    }

	    Set<String> keySet = generatedManifestResources.keySet();
	    for (String permutationId : keySet)
	    {
	    	Set<String> set = generatedManifestResources.get(permutationId);
	    	set.addAll(cachedArtifacts);
	    	artifactset.add(createCacheManifest(context, logger, set, permutationId));
	    }
    }

	private void emitMainAppCache(TreeLogger logger, LinkerContext context, ArtifactSet artifacts, ArtifactSet artifactset) throws UnableToCompleteException
    {
	    String moduleName = context.getModuleName();
		String initialPage = getInitialPage(context);
		

		StringBuilder builder = new StringBuilder("CACHE MANIFEST\n");
		builder.append("# Build Time ["+getCurrentTimeTruncatingMiliseconds()+"]\n");
		builder.append("\nCACHE:\n");

		if (initialPage != null)
		{
			builder.append(initialPage+"\n");
		}
		
		for (String fn : cachedArtifacts)
		{
			builder.append("/"+moduleName+"/"+fn+"\n");
		}
		builder.append("\nNETWORK:\n");
		builder.append("*\n");
		EmittedArtifact manifest = emitString(logger, builder.toString(), getManifestName(context));
		artifactset.add(manifest);
    }

	private boolean acceptCachedResource(String filename)
	{
		if (filename.startsWith("compile-report/"))
		{
			return false;
		}
		for (String acceptedExtension : acceptedFileExtensions)
		{
			if (filename.endsWith(acceptedExtension))
			{
				return true;
			}
		}
		return false;
	}

	private Artifact<?> createCacheManifest(LinkerContext context, TreeLogger logger, Set<String> artifacts, String permutationId) throws UnableToCompleteException
	{
		String moduleName = context.getModuleName();

		StringBuilder builder = new StringBuilder("CACHE MANIFEST\n");
		builder.append("# Build Time ["+getCurrentTimeTruncatingMiliseconds()+"]\n");
		builder.append("\nCACHE:\n");

		for (String fn : artifacts)
		{
			builder.append("/"+moduleName+"/"+fn+"\n");
		}
		builder.append("\nNETWORK:\n");
		builder.append("*\n\n");

		return emitString(logger, builder.toString(), getManifestName(permutationId));
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

	static String getPermutationId(ArtifactSet artifacts)
	{
		for (CompilationResult result : artifacts.find(CompilationResult.class))
		{
			return Integer.toString(result.getPermutationId());
		}
		return null;
	}
	
	static String getManifestName(String permutationId)
    {
	    return permutationId+".appcache";
    }
}