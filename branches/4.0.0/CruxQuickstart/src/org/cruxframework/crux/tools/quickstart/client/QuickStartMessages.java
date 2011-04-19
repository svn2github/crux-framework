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
package org.cruxframework.crux.tools.quickstart.client;

import org.cruxframework.crux.core.client.i18n.MessageName;

import com.google.gwt.i18n.client.Messages;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
@MessageName("quickMsg")
public interface QuickStartMessages extends Messages 
{
	@DefaultMessage("Generate New Crux Application")
	String generateApp();

	@DefaultMessage("Run Crux Examples")
	String viewExamples();
	
	@DefaultMessage("View Crux User Manual")
	String viewUserManual();
	
	@DefaultMessage("View Crux API Docs")
	String viewJavadoc();
	
	@DefaultMessage("Crux QuickStart")
	String appTitle();
	
	@DefaultMessage("Project Information")
	String projectInfo();
	
	@DefaultMessage("Gadget Information")
	String gadgetInfo();

	@DefaultMessage("Project Name:")
	String projectName();

	@DefaultMessage("Startup URL:")
	String hostedModeStartupURL();

	@DefaultMessage("Startup Module:")
	String hostedModeStartupModule();

	@DefaultMessage("DevMode JVM Args:")
	String hostedModeVMArgs();

	@DefaultMessage("Project Layout:")
	String projectLayout();

	@DefaultMessage("Module Description:")
	String cruxModuleDescription();

	@DefaultMessage("Welcome")
	String welcomeQuickstartLabel();

	@DefaultMessage("Welcome to Crux Project Generator. Click ''next'' to continue.")
	String welcomeQuickstartMsg();

	@DefaultMessage("Summary")
	String summaryQuickstartLabel();

	@DefaultMessage("Your project is ready to be generated. Click ''finish'' to generate it now.")
	String summaryQuickstartMsg();

	@DefaultMessage("Project Generated.")
	String generateAppSuccessTitle();

	@DefaultMessage("Your project was successfuly generated.")
	String generateAppSuccessMessage();

	@DefaultMessage("Error generating project.")
	String generateAppFailureTitle();

	@DefaultMessage("Your project could not be generated. See the log on console for more information.")
	String generateAppFailureMessage();
	
	@DefaultMessage("Eclipse Information")
	String ideInfoLabel();
	
	@DefaultMessage("Select the output directory:")
	String workspaceDir();
	
	@DefaultMessage("Select a Crux Application:")
	String examplesMsg();
	
	@DefaultMessage("Fill in the basic information of your project:")
	String projectInfoTitle();
	
	@DefaultMessage("Fill in the basic information of your gadget project:")
	String gadgetInfoTitle();
	
	@DefaultMessage("The name of your project. This will be used to generate an Eclipse Project for you. Because some project''s resources will inherit this name, we recomend you to avoid special or blank characters.")
	String projectNameHelpText();
	
	@DefaultMessage("The name of the page you want to be used as the welcome page of your application. We recomend you to use ''.html'' extension. If you choose another file extension, you will have to confuigure the ''web.xml'' file of your generated project, in order to set the ''ModulesDeclarativeUIFilter'' to handle the URLs properly.")
	String startupURLHelpText();
	
	@DefaultMessage("The main GWT module of your application. The freshly generated project will have only a single module, but you are free to create others by yourself.")
	String startupModuleHelpText();
	
	@DefaultMessage("A set of arguments that will passed to the Java Virtual Machine for running the GWT development console (GWT DevMode).")
	String devModeJVMArgsHelpText();
	
	@DefaultMessage("The layout of your project. If you don''t know what this means, please consult this link:\n http://code.google.com/p/crux-framework/wiki/UserManual#6_Project_Layouts, or leave the default option selected.")
	String projectLayoutOptionHelpText();
	
	@DefaultMessage("A brief textual description of your application, which will be used for documentation purposes.")
	String moduleDescriptionHelpText();

	@DefaultMessage("version {0}")
	String cruxVersion(String result);
	
	@DefaultMessage("Generating your project. Please wait...")
	String waitGeneratingProject();
	
	@DefaultMessage("Back to Main Menu")
	String backToMainMenu();
	
	@DefaultMessage("Crux Showcase")
	String openShowcase();
	
	@DefaultMessage("Hello World")
	String openHelloWorld();
	
	@DefaultMessage("Use Long Manifest Name:")
	String useLongManifestName();
	
	@DefaultMessage("Enabing this will make Crux use manifests with long names. The name of the manifest file will be generated using the module package name as suffix.")
	String useLongManifestNameHelpText();

	@DefaultMessage("Author:")
	String author();
	
	@DefaultMessage("The Author property of this gadget.")
	String authorHelpText();

	@DefaultMessage("About Me:")
	String authorAboutMe();
	
	@DefaultMessage("The Author About-Me property of this gadget.")
	String authorAboutMeHelpText();
	
	@DefaultMessage("Affiliation:")
	String authorAffiliation();
	
	@DefaultMessage("The Author Affiliation property of this gadget.")
	String authorAffiliationHelpText();
	
	@DefaultMessage("Email:")
	String authorEmail();
	
	@DefaultMessage("The Author Email property of this gadget.")
	String authorEmailHelpText();
	
	@DefaultMessage("Link:")
	String authorLink();
	
	@DefaultMessage("The Author Link property of this gadget.")
	String authorLinkHelpText();

	@DefaultMessage("Location:")
	String authorLocation();
	
	@DefaultMessage("The Author Location property of this gadget.")
	String authorLocationHelpText();
	
	@DefaultMessage("Photo:")
	String authorPhoto();
	
	@DefaultMessage("The Author Photo property of this gadget.")
	String authorPhotoHelpText();
	
	@DefaultMessage("Quote:")
	String authorQuote();
	
	@DefaultMessage("The Author Quote property of this gadget.")
	String authorQuoteHelpText();
	
	@DefaultMessage("Description:")
	String description();
	
	@DefaultMessage("The Description property of this gadget.")
	String descriptionHelpText();
	
	@DefaultMessage("Directory Title:")
	String directoryTitle();
	
	@DefaultMessage("The Directory Title property of this gadget.")
	String directoryTitleHelpText();

	@DefaultMessage("Height:")
	String height();
	
	@DefaultMessage("The Height property of this gadget.")
	String heightHelpText();

	@DefaultMessage("Width:")
	String width();
	
	@DefaultMessage("The Width property of this gadget.")
	String widthHelpText();
	
	@DefaultMessage("Screenshot:")
	String screenshot();
	
	@DefaultMessage("The Screenshot property of this gadget.")
	String screenshotHelpText();

	@DefaultMessage("Thumbnail:")
	String thumbnail();
	
	@DefaultMessage("The Thumbnail property of this gadget.")
	String thumbnailHelpText();

	@DefaultMessage("Title:")
	String title();
	
	@DefaultMessage("The Title property of this gadget.")
	String titleHelpText();

	@DefaultMessage("Title URL:")
	String titleUrl();
	
	@DefaultMessage("The Title URL property of this gadget.")
	String titleUrlHelpText();

	@DefaultMessage("Scrolling:")
	String scrolling();
	
	@DefaultMessage("The Scrolling property of this gadget.")
	String scrollingHelpText();

	@DefaultMessage("Singleton:")
	String singleton();
	
	@DefaultMessage("The Singleton property of this gadget.")
	String singletonHelpText();

	@DefaultMessage("Scaling:")
	String scaling();
	
	@DefaultMessage("The Scaling property of this gadget.")
	String scalingHelpText();
	
	@DefaultMessage("Locales:")
	String locales();
	
	@DefaultMessage("The Locales property of this gadget. The locales must be inserted separeted by commas. Eg: pt_BR,en_US")
	String localesHelpText();
	
	@DefaultMessage("Features:")
	String features();
	
	@DefaultMessage("The Features needed by this gadget.")
	String featuresHelpText();
		
	@DefaultMessage("Available Features")
	String availableFeatures();
	
	@DefaultMessage("Choosen Features")
	String usedFeatures();
	
	@DefaultMessage("User Preferences:")
	String userPreferences();
	
	@DefaultMessage("The userPreferences class used by Gadget to store gadget configuration parameters.")
	String userPreferencesHelpText();
}