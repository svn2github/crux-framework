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
package br.com.sysmap.crux.widgets.client.event.paste;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Source for PasteEvents. All classes that implements this interface must explicitly invoke 
 * <code>br.com.sysmap.crux.widgets.client.event.paste.PasteRegisterFactory.getPasteRegister().addNativeHandlerForPaste(this, this.getElement());</code> 
 * once in its constructor. 
 * @author Gess� S. F. Daf� - <code>gessedafe@gmail.com</code>
 */
public interface HasPasteHandlers extends HasHandlers 
{
	HandlerRegistration addPasteHandler(PasteHandler handler);
}