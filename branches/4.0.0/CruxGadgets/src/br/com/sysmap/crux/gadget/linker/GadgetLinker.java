/*
 * Copyright 2010 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.gadget.linker;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.linker.CrossSiteIframeLinker;
import com.google.gwt.dev.About;

/**
 * Finalizes the module manifest file with the selection script.
 */
public final class GadgetLinker extends CrossSiteIframeLinker
{
	private static final String GADGET_LINKER_TEMPLATE_JS = "br/com/sysmap/crux/gadget/linker/GadgetTemplate.js";
	private static final String GADGET_COMPUTE_SCRIPT_BASE_JS = "br/com/sysmap/crux/gadget/linker/computeScriptBase.js";
	private static final String GADGET_INSTALL_SCRIPT_JS = "br/com/sysmap/crux/gadget/linker/installScriptEarlyDownload.js";
	private static final String GADGET_LOAD_EXTERNAL_STYLESHHETS_SCRIPT_JS = "br/com/sysmap/crux/gadget/linker/loadExternalStylesheets.js";
	private static final String GADGET_PROCESS_METAS_JS = "br/com/sysmap/crux/gadget/linker/processMetas.js";
	private static final String GADGET_WAIT_FOR_BODY_LOADED_JS = "br/com/sysmap/crux/gadget/linker/waitForBodyLoaded.js";
	private static final String GADGET_SET_LOCALE_JS = "br/com/sysmap/crux/gadget/linker/setGadgetLocale.js";

	private ArtifactSet toLink;

	/**
	 * @see com.google.gwt.core.linker.CrossSiteIframeLinker#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return "Crux Gadget";
	}

	
	/**
	 * @see com.google.gwt.core.ext.linker.impl.SelectionScriptLinker#link(com.google.gwt.core.ext.TreeLogger, com.google.gwt.core.ext.LinkerContext, com.google.gwt.core.ext.linker.ArtifactSet, boolean)
	 */
	@Override
	public ArtifactSet link(TreeLogger logger, LinkerContext context, ArtifactSet artifacts, boolean onePermutation) throws UnableToCompleteException
	{
		toLink = new ArtifactSet(artifacts);

		ArtifactSet result = super.link(logger, context, toLink, onePermutation);
		return result;
	}

	/**
	 * @see com.google.gwt.core.ext.Linker#relink(com.google.gwt.core.ext.TreeLogger, com.google.gwt.core.ext.LinkerContext, com.google.gwt.core.ext.linker.ArtifactSet)
	 */
	@Override
	public ArtifactSet relink(TreeLogger logger, LinkerContext context, ArtifactSet newArtifacts) throws UnableToCompleteException
	{
		permutationsUtil.setupPermutationsMap(toLink);
		ArtifactSet toReturn = new ArtifactSet(toLink);
		EmittedArtifact art = emitSelectionScript(logger, context, toLink);
		if (art != null) {
			toReturn.add(art);
		}
		maybeOutputPropertyMap(logger, context, toReturn);
		maybeAddHostedModeFile(logger, context, toReturn, null);
		return toReturn;
	}
	
	/**
	 * @see com.google.gwt.core.ext.linker.impl.SelectionScriptLinker#emitSelectionScript(com.google.gwt.core.ext.TreeLogger, com.google.gwt.core.ext.LinkerContext, com.google.gwt.core.ext.linker.ArtifactSet)
	 */
	@Override
	protected EmittedArtifact emitSelectionScript(TreeLogger logger, LinkerContext context, ArtifactSet artifacts) throws UnableToCompleteException
	{

		logger = logger.branch(TreeLogger.DEBUG, "Building gadget manifest", null);

		String bootstrap = "<script>" + generateSelectionScript(logger, context, artifacts) + 
		"</script>";
		StringBuffer manifest = new StringBuffer();
		
		GadgetManifestGenerator gadgetManifestGenerator = new GadgetManifestGenerator(logger, context.getModuleName());
		manifest.append(gadgetManifestGenerator.generateGadgetManifestFile());
		String manifestName = gadgetManifestGenerator.getManifestName();
		replaceAll(manifest, "__BOOTSTRAP__", bootstrap);

		return emitString(logger, manifest.toString(), manifestName);
	}

	/**
	 * @see com.google.gwt.core.linker.CrossSiteIframeLinker#fillSelectionScriptTemplate(java.lang.StringBuffer, com.google.gwt.core.ext.TreeLogger, com.google.gwt.core.ext.LinkerContext, com.google.gwt.core.ext.linker.ArtifactSet, com.google.gwt.core.ext.linker.CompilationResult)
	 */
	@Override
	protected String fillSelectionScriptTemplate(StringBuffer selectionScript, TreeLogger logger, 
			LinkerContext context, ArtifactSet artifacts, CompilationResult result) throws UnableToCompleteException
	{
		super.fillSelectionScriptTemplate(selectionScript, logger, context, artifacts, result);

	    includeJs(selectionScript, logger, getJsSetGadgetLocale(context), "__GADGET_SET_LOCALE__");
		
		// Add a substitution for the GWT major release number. e.g. "2.2"
		int gwtVersions[] = About.getGwtVersionArray();
		replaceAll(selectionScript, "__GWT_MAJOR_VERSION__", gwtVersions[0] + "." + gwtVersions[1]);
		return selectionScript.toString();
	}

	protected String getJsSetGadgetLocale(LinkerContext context)
	{
		return GADGET_SET_LOCALE_JS;
	}
	
	@Override
	protected String getJsComputeScriptBase(LinkerContext context)
	{
		return GADGET_COMPUTE_SCRIPT_BASE_JS;
	}

	@Override
	protected String getJsInstallScript(LinkerContext context)
	{
		return GADGET_INSTALL_SCRIPT_JS;
	}

	@Override
	protected String getJsLoadExternalStylesheets(LinkerContext context)
	{
		return GADGET_LOAD_EXTERNAL_STYLESHHETS_SCRIPT_JS;
	}

	@Override
	protected String getJsProcessMetas(LinkerContext context)
	{
		return GADGET_PROCESS_METAS_JS;
	}

	@Override
	protected String getJsWaitForBodyLoaded(LinkerContext context)
	{
		return GADGET_WAIT_FOR_BODY_LOADED_JS;
	}

	@Override
	protected String getSelectionScriptTemplate(TreeLogger logger, LinkerContext context)
	{
		return GADGET_LINKER_TEMPLATE_JS;
	}
}
