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
package org.cruxframework.crux.core.rebind.screen.resources;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Input;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Size;
import org.cruxframework.crux.core.rebind.screen.ScreenConfigException;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.rebind.screen.resources.Image.RepeatStyle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ResourcesFactory
{
	/**
	 * Parse resources element
	 * @param view
	 * @param elem
	 * @return
	 * @throws ScreenConfigException 
	 */
	public static Resources getResources(View view, JSONObject elem) throws ScreenConfigException 
	{
        try
        {
        	Resources resources = new Resources(elem.getString("id"), elem.optString("packageName"), elem.optBoolean("shared"), getResourceSize(elem), getResourceInput(elem));
        	
        	JSONArray children = elem.optJSONArray("_children");
        	if (children != null)
        	{
        		for (int i=0; i< children.length(); i++)
        		{
        			JSONObject child = children.getJSONObject(i);
        			String type = child.getString("_type");
        			if ("set".equals(type))
        			{
        				resources.addDefinition(new Resources.Definition(child.optString("packageName"), getResourceSize(child), getResourceInput(child)));
        			}
        			else if ("image".equals(type))
        			{
        				resources.addImage(getImageResourceElement(child));
        			}
        			else if ("css".equals(type))
        			{
        				resources.addCss(getCssResourceElement(child));
        			}
        			else if ("text".equals(type))
        			{
        				resources.addText(getTextResourceElement(child));
        			}
        			else if ("externalText".equals(type))
        			{
        				resources.addExternalText(getExternalTextResourceElement(child));
        			}
        			else if ("data".equals(type))
        			{
        				resources.addData(getDataResourceElement(child));
        			}
        		}
        	}
        	
        	return resources;
        }
        catch (JSONException e)
        {
	        throw new ScreenConfigException("Error parsing resources on view metaData. View ["+view.getId()+"].");
        }
	}

	/**
	 * 
	 * @param elem
	 * @param result
	 * @throws JSONException
	 */
	private static void processSimpleResourceChildren(JSONObject elem, SimpleResource result) throws JSONException
    {
	    JSONArray children = elem.optJSONArray("_children");
    	if (children != null)
    	{
    		for (int i=0; i< children.length(); i++)
    		{
    			JSONObject child = children.getJSONObject(i);
    			String type = child.getString("_type");
    			if ("set".equals(type))
    			{
    				result.addDefinition(new SimpleResource.Definition(child.getString("file"), getResourceSize(child), getResourceInput(child)));
    			}
    		}
    	}
    }
	
	/**
	 * 
	 * @param child
	 * @return
	 * @throws JSONException 
	 */
	private static Data getDataResourceElement(JSONObject elem) throws JSONException
    {
		Data result = new Data(elem.getString("id"), elem.optString("file"), getResourceSize(elem), getResourceInput(elem));
    	processSimpleResourceChildren(elem, result);
    	return result;
    }

	/**
	 * 
	 * @param child
	 * @return
	 * @throws JSONException 
	 */
	private static ExternalText getExternalTextResourceElement(JSONObject elem) throws JSONException
    {
		ExternalText result = new ExternalText(elem.getString("id"), elem.optString("file"), getResourceSize(elem), getResourceInput(elem));
    	processSimpleResourceChildren(elem, result);
    	return result;
    }

	/**
	 * 
	 * @param child
	 * @return
	 * @throws JSONException 
	 */
	private static Text getTextResourceElement(JSONObject elem) throws JSONException
    {
		Text result = new Text(elem.getString("id"), elem.optString("file"), getResourceSize(elem), getResourceInput(elem));
    	processSimpleResourceChildren(elem, result);
    	return result;
    }

	/**
	 * 
	 * @param child
	 * @return
	 * @throws JSONException 
	 */
	private static Css getCssResourceElement(JSONObject elem) throws JSONException
    {
		Css result = new Css(elem.getString("id"), elem.optString("file"), elem.optString("importPrefix"), getResourceSize(elem), getResourceInput(elem));
	    JSONArray children = elem.optJSONArray("_children");
    	if (children != null)
    	{
    		for (int i=0; i< children.length(); i++)
    		{
    			JSONObject child = children.getJSONObject(i);
    			String type = child.getString("_type");
    			if ("set".equals(type))
    			{
    				result.addDefinition(new Css.Definition(child.optString("file"), child.optString("importPrefix"), getResourceSize(child), getResourceInput(child)));
    			}
    			else if ("cssAlias".equals(type))
    			{
    				result.addAlias(getCssAliasElement(child));
    			}
    		}
    	}
	    return result;
    }

	/**
	 * 
	 * @param child
	 * @return
	 * @throws JSONException 
	 */
	private static Css.Alias getCssAliasElement(JSONObject elem) throws JSONException
    {
		Css.Alias result = new Css.Alias(elem.getString("name"), elem.optString("className"), getResourceSize(elem), getResourceInput(elem));
	    JSONArray children = elem.optJSONArray("_children");
    	if (children != null)
    	{
    		for (int i=0; i< children.length(); i++)
    		{
    			JSONObject child = children.getJSONObject(i);
    			String type = child.getString("_type");
    			if ("set".equals(type))
    			{
    				result.addDefinition(new Css.Alias.Definition(child.optString("className"), getResourceSize(child), getResourceInput(child)));
    			}
    		}
    	}
	    return result;
    }

	/**
	 * 
	 * @param child
	 * @return
	 * @throws JSONException 
	 */
	private static Image getImageResourceElement(JSONObject elem) throws JSONException
    {
		Image result = new Image(elem.getString("id"), elem.optString("file"), elem.optBoolean("flipRtl"), getRepeatStyle(elem), getResourceSize(elem), getResourceInput(elem));
	    JSONArray children = elem.optJSONArray("_children");
    	if (children != null)
    	{
    		for (int i=0; i< children.length(); i++)
    		{
    			JSONObject child = children.getJSONObject(i);
    			String type = child.getString("_type");
    			if ("set".equals(type))
    			{
    				result.addDefinition(new Image.Definition(child.optString("file"), child.optBoolean("flipRtl"), getRepeatStyle(child), getResourceSize(child), getResourceInput(child)));
    			}
    		}
    	}
	    return result;
    }

	private static RepeatStyle getRepeatStyle(JSONObject elem)
    {
    	String repeatStyle = elem.optString("repeatStyle");
		return (repeatStyle==null)?null:RepeatStyle.valueOf(repeatStyle);
    }

	/**
	 * 
	 * @param elem
	 * @return
	 */
	private static Size getResourceSize(JSONObject elem)
	{
    	String deviceSize = elem.optString("deviceSize");
		return (deviceSize==null)?null:Size.valueOf(deviceSize);
	}
	
	/**
	 * 
	 * @param elem
	 * @return
	 */
	private static Input getResourceInput(JSONObject elem)
	{
    	String deviceInput = elem.optString("deviceInput");
		return (deviceInput==null)?null:Input.valueOf(deviceInput);
	}
}
