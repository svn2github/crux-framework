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
package br.com.sysmap.crux.core.client.screen;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.controller.crossdoc.CrossDocumentException;
import br.com.sysmap.crux.core.client.controller.crossdoc.Target;
import br.com.sysmap.crux.core.client.utils.StringUtils;


/**
 * This class is used to make calls in different documents.
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class ScreenAccessor
{
	/**
	 * Make a call to a cross document method.
	 * 
	 * @param serializedData the call itself. Contains the method and all parameters serialized.
	 * @param target Where the method resides
	 * @return
	 */
	protected String invokeCrossDocument(String serializedData, Target target, String frame, String siblingFrame, JSWindow jsWindow)
	{
		if (target != null)
		{
			switch (target)
	        {
		        case TOP: return callTopControllerAccessor(serializedData);
		        case ABSOLUTE_TOP: return callAbsoluteTopControllerAccessor(serializedData);
		        case PARENT: return callParentControllerAccessor(serializedData);
		        case OPENER: return callOpenerControllerAccessor(serializedData);
		        default: 
		        	throw new CrossDocumentException(Crux.getMessages().crossDocumentInvalidTarget());
	        }
			
		}
		else if (!StringUtils.isEmpty(frame))
    	{
    		return callFrameControllerAccessor(frame, serializedData);
    	}
    	else if (!StringUtils.isEmpty(siblingFrame))
    	{
    		return callSiblingFrameControllerAccessor(siblingFrame, serializedData);
    	}
    	else if (jsWindow != null)
    	{
    		return callWindowControllerAccessor(jsWindow, serializedData);
    	}
    	else
    	{
    		throw new CrossDocumentException(Crux.getMessages().crossDocumentInvalidTarget());
    	}
	}
	
	private native String callTopControllerAccessor(String serializedData)/*-{
		return $wnd.top._cruxCrossDocumentAccessor(serializedData);
	}-*/;
	
	private static native String callAbsoluteTopControllerAccessor(String serializedData)/*-{
		var who = $wnd.top;
		var op = $wnd.opener;
		while (op != null)
		{
			who = op.top;
			op = op.opener;
		}
		return who._cruxCrossDocumentAccessor(serializedData);
	}-*/;	
	
	private static native String callOpenerControllerAccessor(String serializedData)/*-{
		return $wnd.opener._cruxCrossDocumentAccessor(serializedData);
	}-*/;
	
	private static native String callParentControllerAccessor(String serializedData)/*-{
		return $wnd.parent._cruxCrossDocumentAccessor(serializedData);
	}-*/;
	
	private static native String callFrameControllerAccessor(String frame, String serializedData)/*-{
		return $wnd.frames[frame]._cruxCrossDocumentAccessor(serializedData);
	}-*/;		
	
	private static native String callSiblingFrameControllerAccessor(String frame, String serializedData)/*-{
		return $wnd.parent.frames[frame]._cruxCrossDocumentAccessor(serializedData);
	}-*/;		

	private static native String callWindowControllerAccessor(JSWindow jsWindow, String serializedData)/*-{
	return jsWindow._cruxCrossDocumentAccessor(serializedData);
}-*/;		
}
