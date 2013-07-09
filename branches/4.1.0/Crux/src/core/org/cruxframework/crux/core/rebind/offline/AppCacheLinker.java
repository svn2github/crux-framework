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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.screen.OfflineScreen;
import org.cruxframework.crux.core.rebind.screen.ScreenConfigException;
import org.cruxframework.crux.core.rebind.screen.ScreenFactory;
import org.cruxframework.crux.core.server.CruxBridge;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.Artifact;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.EmittedArtifact.Visibility;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.Shardable;
import com.google.gwt.core.ext.linker.impl.PermutationsUtil;
import com.google.gwt.util.tools.Utility;

/**
 * A GWT linker that produces an offline.appcache file describing what to cache
 * in the application cache. It produces one appcache file for each permutation.
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
	private PermutationsUtil permutationsUtil;

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
			analyzePermutationArtifacts(artifactset);
		}
		else
		{
			try
			{
				Set<OfflineScreen> offlinePages = OfflineScreens.getOfflinePages(context.getModuleName());
				if (offlinePages != null)
				{
					for (OfflineScreen offlineScreen : offlinePages)
					{
						emitOfflineArtifacts(logger, context, artifactset, offlineScreen);
					}
				}
			}
			catch (Exception e)
			{
				logger.log(TreeLogger.ERROR, "Unable to create offline files", e);
				throw new UnableToCompleteException();
			}
		}

		return artifactset;
	}

	private void emitOfflineArtifacts(TreeLogger logger, LinkerContext context, ArtifactSet artifacts, OfflineScreen offlineScreen) throws UnableToCompleteException
	{
		String screenID = getTargetScreenId(context, logger, offlineScreen.getRefScreen());
		emitMainAppCache(logger, context, artifacts, screenID);
		emitPermutationsAppCache(logger, context, artifacts, screenID);
		emitOfflinePage(logger, context, artifacts, offlineScreen);
	}

	private void emitOfflinePage(TreeLogger logger, LinkerContext context, ArtifactSet artifacts, OfflineScreen offlineScreen) throws UnableToCompleteException
	{
		permutationsUtil = new PermutationsUtil();
		permutationsUtil.setupPermutationsMap(artifacts);
		StringBuffer buffer = readFileToStringBuffer(getOfflinePageTemplate(logger, context), logger);
	    int startPos = buffer.indexOf("// __OFFLINE_SELECTION_END__");
	    if (startPos != -1)
	    {
	    	String ss = generateSelectionScript(logger, context, artifacts);
	    	buffer.insert(startPos, ss);
	    }
		replaceAll(buffer, "__MANIFEST_NAME__", getManifestName());
		artifacts.add(emitString(logger, buffer.toString(), offlineScreen.getId(), System.currentTimeMillis()));
	}

	private String generateSelectionScript(TreeLogger logger, LinkerContext context, ArtifactSet artifacts) throws UnableToCompleteException
	{
		String selectionScriptText;
		StringBuffer buffer = readFileToStringBuffer(getSelectionScriptTemplate(logger, context), logger);
		selectionScriptText = fillSelectionScriptTemplate(buffer, logger, context, artifacts, null);
		//selectionScriptText = context.optimizeJavaScript(logger, selectionScriptText);
		return selectionScriptText;
	}

	protected StringBuffer readFileToStringBuffer(String filename, TreeLogger logger) throws UnableToCompleteException
	{
		StringBuffer buffer;
		try
		{
			buffer = new StringBuffer(Utility.getFileFromClassPath(filename));
		}
		catch (IOException e)
		{
			logger.log(TreeLogger.ERROR, "Unable to read file: " + filename, e);
			throw new UnableToCompleteException();
		}
		return buffer;
	}

	protected static void replaceAll(StringBuffer buf, String search, String replace)
	{
		int len = search.length();
		for (int pos = buf.indexOf(search); pos >= 0; pos = buf.indexOf(search, pos + 1))
		{
			buf.replace(pos, pos + len, replace);
		}
	}

	private String fillSelectionScriptTemplate(StringBuffer selectionScript, TreeLogger logger, LinkerContext context, ArtifactSet artifacts, Object object) throws UnableToCompleteException
	{
		permutationsUtil.addPermutationsJs(selectionScript, logger, context);
		replaceAll(selectionScript, "__MODULE_FUNC__", context.getModuleFunctionName());
		replaceAll(selectionScript, "__MODULE_NAME__", context.getModuleName());
		return selectionScript.toString();
	}

	private String getSelectionScriptTemplate(TreeLogger logger, LinkerContext context)
	{
	    return "org/cruxframework/crux/core/rebind/offline/OfflineSelectionTemplate.js";
	}

	private String getOfflinePageTemplate(TreeLogger logger, LinkerContext context)
	{
	    return "org/cruxframework/crux/core/rebind/offline/OfflinePage.html";
	}

	private void analyzePermutationArtifacts(ArtifactSet artifacts)
	{
		String permutationName = getPermutationName(artifacts);

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
		generatedManifestResources.put(permutationName, hashSet);
	}

	private void emitPermutationsAppCache(TreeLogger logger, LinkerContext context, ArtifactSet artifacts, String startScreenId) throws UnableToCompleteException
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
		for (String permutationName : keySet)
		{
			Set<String> set = generatedManifestResources.get(permutationName);
			set.addAll(cachedArtifacts);
			artifacts.add(createCacheManifest(context, logger, set, permutationName));
			artifacts.add(createCacheManifestLoader(context, logger, permutationName, startScreenId));
		}
	}

	private void emitMainAppCache(TreeLogger logger, LinkerContext context, ArtifactSet artifacts, String startScreenId) throws UnableToCompleteException
	{
		String moduleName = context.getModuleName();

		StringBuilder builder = new StringBuilder("CACHE MANIFEST\n");
		builder.append("# Build Time [" + getCurrentTimeTruncatingMiliseconds() + "]\n");
		builder.append("\nCACHE:\n");

		if (startScreenId != null)
		{
			builder.append(startScreenId + "\n");
		}

		for (String fn : cachedArtifacts)
		{
			builder.append("/" + moduleName + "/" + fn + "\n");
		}
		
		Set<String> keySet = generatedManifestResources.keySet();
		for (String permutationName : keySet)
		{
			builder.append("/" + moduleName + "/" + getManifestLoaderName(permutationName) + "\n");
		}
		
		builder.append("\nNETWORK:\n");
		builder.append("*\n");
		EmittedArtifact manifest = emitString(logger, builder.toString(), getManifestName());
		artifacts.add(manifest);
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

	private Artifact<?> createCacheManifest(LinkerContext context, TreeLogger logger, Set<String> artifacts, String permutationName) throws UnableToCompleteException
	{
		String moduleName = context.getModuleName();

		StringBuilder builder = new StringBuilder("CACHE MANIFEST\n");
		builder.append("# Build Time [" + getCurrentTimeTruncatingMiliseconds() + "]\n");
		builder.append("\nCACHE:\n");

		for (String fn : artifacts)
		{
			builder.append("/" + moduleName + "/" + fn + "\n");
		}
		builder.append("\nNETWORK:\n");
		builder.append("*\n\n");

		return emitString(logger, builder.toString(), getManifestName(permutationName));
	}

	private Artifact<?> createCacheManifestLoader(LinkerContext context, TreeLogger logger, String permutationName, String startScreenId) throws UnableToCompleteException
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<html manifest=\"" + getManifestName(permutationName) + "\"><head><title></title><style>HTML,BODY{height: 100%;}</style></head>");
		builder.append("<body style=\"margin:0px;padding:0px;overflow:hidden\">");
		builder.append("<iframe src=\"" + startScreenId + "\" frameborder=\"0\" style=\"overflow:hidden;height:100%;width:100%\" height=\"100%\" width=\"100%\"></iframe>");
		builder.append("</body></html>");

		return emitString(logger, builder.toString(), getManifestLoaderName(permutationName));
	}

	private String getTargetScreenId(LinkerContext context, TreeLogger logger, String screenID) throws UnableToCompleteException
	{
		if (StringUtils.isEmpty(screenID))
		{
			screenID = CruxBridge.getInstance().getLastPageRequested();
			try
			{
				screenID = ScreenFactory.getInstance().getScreen(screenID, null).getRelativeId();
			}
			catch (ScreenConfigException e)
			{
				logger.log(TreeLogger.ERROR, e.getMessage(), e);
				throw new UnableToCompleteException();
			}
		}
		return screenID;
	}

	private long getCurrentTimeTruncatingMiliseconds()
	{
		long currentTime = (System.currentTimeMillis() / 1000) * 1000;
		return currentTime;
	}

	private String getManifestName()
	{
		return "offline.appcache";
	}

	private String getPermutationName(ArtifactSet artifacts)
	{
		for (CompilationResult result : artifacts.find(CompilationResult.class))
		{
			return result.getStrongName();
		}
		return null;
	}

	private String getManifestName(String permutationName)
	{
		return permutationName + ".appcache";
	}

	private String getManifestLoaderName(String permutationName)
	{
		return "offlineLoader_" + permutationName + ".cache.html";
	}

}