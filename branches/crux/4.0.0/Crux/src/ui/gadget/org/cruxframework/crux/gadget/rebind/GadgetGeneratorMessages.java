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
package org.cruxframework.crux.gadget.rebind;

import org.cruxframework.crux.core.i18n.DefaultServerMessage;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface GadgetGeneratorMessages
{
	@DefaultServerMessage("[gadget-gen 001] - Can not find the GadgetInformation meta class {0}.")
	String gadgetInformationMetaClassNotFound(String module);

	@DefaultServerMessage("[gadget-gen 002] - Error generating gadget descriptor. You must declare a interface (only one) that implements the interface GadgetInfo.")
	String gadgetManifestGeneratorDescriptorInterfaceNotFound();

	@DefaultServerMessage("[gadget-gen 003] - Gadget descriptor not found.")
	String gadgetManifestGeneratorDescriptorInterfaceNotLoaded();

	@DefaultServerMessage("[gadget-gen 004] - Gadget projects must use GadgetScreenResolver or a subClass of it.")
	String gadgetScreenResolverCastError();
	
	@DefaultServerMessage("[gadget-gen 005] - Could not retrieve screen ids.")
	String gadgetManifestGeneratorErrorReadingScreenIds();

	@DefaultServerMessage("[gadget-gen 006] - Could not create manifest document.")
	String gadgetManifestGeneratorCanNotCreateDocument();
	
	@DefaultServerMessage("[gadget-gen 007] - Gadget Descriptor must be an interface.")
	String gadgetManifestGeneratorDescriptorMustBeInterface();
	
	@DefaultServerMessage("[gadget-gen 008] - Error generating gadget proxy.")
	String gadgetProxyCreatorError();
}
