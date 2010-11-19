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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.ScriptReference;
import com.google.gwt.core.ext.linker.StylesheetReference;
import com.google.gwt.core.linker.CrossSiteIframeLinker;
import com.google.gwt.dev.About;
import com.google.gwt.util.tools.Utility;

/**
 * Finalizes the module manifest file with the selection script.
 */
public final class GadgetLinker extends CrossSiteIframeLinker
{
	  private static final String INSTALL_LOCATION_JS_PROPERTY = "com/google/gwt/core/ext/linker/impl/installLocationIframe.js";
	  private static final String WAIT_FOR_BODY_LOADED_JS = "br/com/sysmap/crux/gadget/linker/waitForBodyLoaded.js";
	  private static final String GADGET_PROCESS_METAS_JS = "br/com/sysmap/crux/gadget/linker/processMetas.js";
	  private static final String GADGET_COMPUTE_SCRIPT_BASE_JS = "br/com/sysmap/crux/gadget/linker/computeScriptBase.js";
	  
	  private EmittedArtifact manifestArtifact;

	/**
	 * @see com.google.gwt.core.linker.CrossSiteIframeLinker#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return "Crux Gadget";
	}

	/**
	 * @see com.google.gwt.core.ext.linker.impl.SelectionScriptLinker#link(com.google.gwt.core.ext.TreeLogger, com.google.gwt.core.ext.LinkerContext, com.google.gwt.core.ext.linker.ArtifactSet)
	 */
	@Override
	public ArtifactSet link(TreeLogger logger, LinkerContext context, ArtifactSet artifacts) throws UnableToCompleteException
	{
		ArtifactSet toLink = new ArtifactSet(artifacts);

		// Mask the stub manifest created by the generator
		for (EmittedArtifact res : toLink.find(EmittedArtifact.class))
		{
			if (res.getPartialPath().endsWith(".gadget.xml"))
			{
				manifestArtifact = res;
				toLink.remove(res);
				break;
			}
		}

		if (manifestArtifact == null)
		{
			if (artifacts.find(CompilationResult.class).isEmpty())
			{
				// Maybe hosted mode or junit, defer to XSLinker.
				return new CrossSiteIframeLinker().link(logger, context, toLink);
			}
			else
			{
				// When compiling for web mode, enforce that the manifest is
				// present.
				logger.log(TreeLogger.ERROR, "No gadget manifest found in ArtifactSet.");
				throw new UnableToCompleteException();
			}
		}
		return super.link(logger, context, toLink);
	}

	/**
	 * @see com.google.gwt.core.ext.linker.impl.SelectionScriptLinker#emitSelectionScript(com.google.gwt.core.ext.TreeLogger, com.google.gwt.core.ext.LinkerContext, com.google.gwt.core.ext.linker.ArtifactSet)
	 */
	@Override
	protected EmittedArtifact emitSelectionScript(TreeLogger logger, LinkerContext context, ArtifactSet artifacts) throws UnableToCompleteException
	{

		logger = logger.branch(TreeLogger.DEBUG, "Building gadget manifest", null);

		String bootstrap = "<script>" + context.optimizeJavaScript(logger, generateSelectionScript(logger, context, artifacts)) + 
		                   "</script>\n" + "<div id=\"__gwt_gadget_content_div\"></div>";

		// Read the content
		StringBuffer manifest = new StringBuffer();

		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(manifestArtifact.getContents(logger)));
			for (String line = in.readLine(); line != null; line = in.readLine())
			{
				manifest.append(line).append("\n");
			}
			in.close();
		}
		catch (IOException e)
		{
			logger.log(TreeLogger.ERROR, "Unable to read manifest stub", e);
			throw new UnableToCompleteException();
		}

		replaceAll(manifest, "__BOOTSTRAP__", bootstrap);

		return emitString(logger, manifest.toString(), manifestArtifact.getPartialPath());
	}

	/**
	 * @see com.google.gwt.core.ext.linker.impl.SelectionScriptLinker#generateScriptInjector(java.lang.String)
	 */
	@Override
	protected String generateScriptInjector(String scriptUrl)
	{
		if (isRelativeURL(scriptUrl))
		{
			return "  if (!__gwt_scriptsLoaded['" + scriptUrl + "']) {\n" + 
			       "    __gwt_scriptsLoaded['" + scriptUrl + "'] = true;\n" + 
			       "    document.write('<script language=\\\"javascript\\\" src=\\\"'+gadgets.io.getProxyUrl(base+'" + 
			       scriptUrl + "') + '\\\"></script>');\n" + "  }\n";
		}
		else
		{
			return "  if (!__gwt_scriptsLoaded['" + scriptUrl + "']) {\n" + 
			       "    __gwt_scriptsLoaded['" + scriptUrl + "'] = true;\n" + 
			       "    document.write('<script language=\\\"javascript\\\" src=\\\"'+gadgets.io.getProxyUrl('" + 
			       scriptUrl + "') + '\\\"></script>');\n" + "  }\n";
		}
	}

	/**
	 * @see com.google.gwt.core.linker.CrossSiteIframeLinker#generateSelectionScript(com.google.gwt.core.ext.TreeLogger, com.google.gwt.core.ext.LinkerContext, com.google.gwt.core.ext.linker.ArtifactSet)
	 */
	@Override
	protected String generateSelectionScript(TreeLogger logger, LinkerContext context, ArtifactSet artifacts) throws UnableToCompleteException
	{
		StringBuffer selectionScript = getSelectionScriptStringBuffer(logger, context);

		String waitForBodyLoadedJs;
		String installLocationJs;
		String computeScriptBase;
		String processMetas;

		try
		{
			waitForBodyLoadedJs = Utility.getFileFromClassPath(WAIT_FOR_BODY_LOADED_JS);
			installLocationJs = Utility.getFileFromClassPath(INSTALL_LOCATION_JS_PROPERTY);
			processMetas = Utility.getFileFromClassPath(GADGET_PROCESS_METAS_JS);
			computeScriptBase = Utility.getFileFromClassPath(GADGET_COMPUTE_SCRIPT_BASE_JS);
		}
		catch (IOException e)
		{
			logger.log(TreeLogger.ERROR, "Unable to read selection script template", e);
			throw new UnableToCompleteException();
		}

		replaceAll(selectionScript, "__INSTALL_LOCATION__", installLocationJs);
		replaceAll(selectionScript, "__WAIT_FOR_BODY_LOADED__", waitForBodyLoadedJs);
		replaceAll(selectionScript, "__START_DOWNLOAD_IMMEDIATELY__", "true");
		replaceAll(selectionScript, "__PROCESS_METAS__", processMetas);
		replaceAll(selectionScript, "__COMPUTE_SCRIPT_BASE__", computeScriptBase);

	    // Add external dependencies
	    int startPos = selectionScript.indexOf("// __MODULE_STYLES_END__");
	    if (startPos != -1) {
	      for (StylesheetReference resource : artifacts.find(StylesheetReference.class)) {
	        String text = generateStylesheetInjector(resource.getSrc());
	        selectionScript.insert(startPos, text);
	        startPos += text.length();
	      }
	    }
		
	    startPos = selectionScript.indexOf("// __MODULE_SCRIPTS_END__");
	    if (startPos != -1) {
	      for (ScriptReference resource : artifacts.find(ScriptReference.class)) {
	        String text = generateScriptInjector(resource.getSrc());
	        selectionScript.insert(startPos, text);
	        startPos += text.length();
	      }
	    }

		// This method needs to be called after all of the .js files have been
		// swapped into the selectionScript since it will fill in
		// __MODULE_NAME__
		// and many of the .js files contain that template variable
		selectionScript = processSelectionScriptCommon(selectionScript, logger, context);

		// Add a substitution for the GWT major release number. e.g. "2.1"
		int gwtVersions[] = About.getGwtVersionArray();
		replaceAll(selectionScript, "__GWT_MAJOR_VERSION__", gwtVersions[0] + "." + gwtVersions[1]);
		return selectionScript.toString();
	}
	
	/**
	 * @see com.google.gwt.core.ext.linker.impl.SelectionScriptLinker#generateStylesheetInjector(java.lang.String)
	 */
	@Override
	protected String generateStylesheetInjector(String stylesheetUrl)
	{
		String hrefExpr = "'" + stylesheetUrl + "'";
		if (isRelativeURL(stylesheetUrl))
		{
			hrefExpr = "base + " + hrefExpr;
		}
		hrefExpr = "gadgets.io.getProxyUrl(" + hrefExpr + ")";

		return "if (!__gwt_stylesLoaded['" + stylesheetUrl + "']) {\n           " +
		       "  var l = $doc.createElement('link');\n                          " + 
		       "  __gwt_stylesLoaded['" + stylesheetUrl + "'] = l;\n             " + 
		       "  l.setAttribute('rel', 'stylesheet');\n                         " + 
		       "  l.setAttribute('href', " + hrefExpr + ");\n                    " + 
		       "  $doc.getElementsByTagName('head')[0].appendChild(l);\n         " + 
		       "}\n";
	}
    
	/**
	 * @see com.google.gwt.core.linker.CrossSiteIframeLinker#getSelectionScriptTemplate(com.google.gwt.core.ext.TreeLogger, com.google.gwt.core.ext.LinkerContext)
	 */
	@Override
	protected String getSelectionScriptTemplate(TreeLogger logger, LinkerContext context)
	{
		return "br/com/sysmap/crux/gadget/linker/GadgetTemplate.js";
	}
}
