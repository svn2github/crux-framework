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
package org.cruxframework.crux.core.rebind.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cruxframework.crux.core.client.service.RestProxy.Callback;
import org.cruxframework.crux.core.client.service.RestProxy.RestService;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.server.rest.annotation.Path;
import org.cruxframework.crux.core.server.rest.annotation.PathParam;
import org.cruxframework.crux.core.server.rest.annotation.QueryParam;
import org.cruxframework.crux.core.server.rest.core.registry.RestServiceScanner;
import org.cruxframework.crux.core.server.rest.util.Encode;
import org.cruxframework.crux.core.server.rest.util.HttpMethodHelper;
import org.cruxframework.crux.core.server.rest.util.InvalidRestMethod;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

/**
 * This class creates a client proxy for calling rest services
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class CruxRestProxyCreator extends AbstractWrapperProxyCreator
{
	private Class<?> restImplementationClass;
	private JClassType callbackType;
	private JClassType objectType;
	private JClassType stringType;
	private JClassType javascriptObjectType;
	private String serviceBasePath;
	private JClassType dateType;
	private static Set<String> jsonFriendlyTypes = new HashSet<String>();
	static
	{
		jsonFriendlyTypes.add(Integer.class.getCanonicalName());
		jsonFriendlyTypes.add(Short.class.getCanonicalName());
		jsonFriendlyTypes.add(Byte.class.getCanonicalName());
		jsonFriendlyTypes.add(Long.class.getCanonicalName());
		jsonFriendlyTypes.add(Double.class.getCanonicalName());
		jsonFriendlyTypes.add(Float.class.getCanonicalName());
		jsonFriendlyTypes.add(Boolean.class.getCanonicalName());
		jsonFriendlyTypes.add(Character.class.getCanonicalName());
		jsonFriendlyTypes.add(Integer.TYPE.getCanonicalName());
		jsonFriendlyTypes.add(Short.TYPE.getCanonicalName());
		jsonFriendlyTypes.add(Byte.TYPE.getCanonicalName());
		jsonFriendlyTypes.add(Long.TYPE.getCanonicalName());
		jsonFriendlyTypes.add(Double.TYPE.getCanonicalName());
		jsonFriendlyTypes.add(Float.TYPE.getCanonicalName());
		jsonFriendlyTypes.add(Boolean.TYPE.getCanonicalName());
		jsonFriendlyTypes.add(Character.TYPE.getCanonicalName());
		jsonFriendlyTypes.add(String.class.getCanonicalName());
		jsonFriendlyTypes.add(Date.class.getCanonicalName());
	}
	
	public CruxRestProxyCreator(TreeLogger logger, GeneratorContextExt context, JClassType baseIntf)
	{
		super(logger, context, baseIntf);
		callbackType = context.getTypeOracle().findType(Callback.class.getCanonicalName());
		stringType = context.getTypeOracle().findType(String.class.getCanonicalName());
		dateType = context.getTypeOracle().findType(Date.class.getCanonicalName());
		objectType = context.getTypeOracle().findType(Object.class.getCanonicalName());
		javascriptObjectType = context.getTypeOracle().findType(JavaScriptObject.class.getCanonicalName());
		restImplementationClass = getRestImplementationClass(baseIntf);
		String basePath;
		try
        {
	        basePath = context.getPropertyOracle().getConfigurationProperty("crux.rest.base.path").getValues().get(0);
	        if (basePath.endsWith("/"))
	        {
	        	basePath = basePath.substring(0, basePath.length()-1);
	        }
        }
        catch (Exception e)
        {
        	basePath = "/rest";
        }
		String value = restImplementationClass.getAnnotation(Path.class).value();
		if (value.startsWith("/"))
		{
			value = value.substring(1);
		}
		serviceBasePath = basePath+"/"+value;

	}

	private Class<?> getRestImplementationClass(JClassType baseIntf)
	{
		RestService restService = baseIntf.getAnnotation(RestService.class);
		if (restService == null)
		{
			throw new CruxGeneratorException("Can not create the rest proxy. Use @RestProxy.RestService annotation to inform the target of current proxy.");
		}
		String serviceClassName = RestServiceScanner.getInstance().getServiceClassName(restService.value());
		Class<?> restImplementationClass;
		try
		{
			restImplementationClass = Class.forName(serviceClassName);
		}
		catch (ClassNotFoundException e)
		{
			throw new CruxGeneratorException("Can not create the rest proxy. Can not found the implementationClass.");
		}
		return restImplementationClass;
	}

	@Override
	protected void generateWrapperMethod(JMethod method, SourcePrinter srcWriter)
	{
		try
		{
			Method implementationMethod = getImplementationMethod(method);
			String methodURI = getRestURI(method, implementationMethod);

			List<JParameter> parameters = generateProxyWrapperMethodDeclaration(srcWriter, method);
			String httpMethod = HttpMethodHelper.getHttpMethod(implementationMethod, false);
			JParameter callbackParameter = parameters.get(parameters.size()-1);
			String callbackResultTypeName = getCallbackResultTypeName(callbackParameter.getType());
			String callbackParameterName = callbackParameter.getName();
			
			srcWriter.println("String restURI = " + EscapeUtils.quote(methodURI) + ";");
			generateMethodParamToURICode(srcWriter, method, implementationMethod, "restURI");
			
			srcWriter.println("RequestBuilder builder = new RequestBuilder(RequestBuilder."+httpMethod+", restURI);");
			srcWriter.println("builder.setCallback(new RequestCallback(){");
			srcWriter.println("public void onResponseReceived(Request request, Response response){");
			srcWriter.println("int s = (response.getStatusCode()-200);");
			srcWriter.println("if (s >= 0 && s < 10){");
			if (!callbackResultTypeName.equalsIgnoreCase("void"))
			{
				JClassType callbackResultType = getTypeArgForGenericType(callbackParameter.getType());
				srcWriter.println("if (Response.SC_NO_CONTENT != response.getStatusCode()){");
				if (callbackResultType.isAssignableTo(stringType))
				{
					srcWriter.println(callbackResultTypeName+" result = response.getText();");
				}
//				else if (callbackResultType.isAssignableTo(dateType))
//				{
//					srcWriter.println(callbackResultTypeName+" result = new Date(Long.parseLong(response.getText());");
//				}
				else if (callbackResultType.findConstructor(new JType[]{stringType}) != null)
				{
					srcWriter.println(callbackResultTypeName+" result = new "+callbackResultTypeName+"(response.getText());");
				}
				else if (callbackResultType.findMethod("valueOf", new JType[]{stringType}) != null)
				{
					srcWriter.println(callbackResultTypeName+" result = "+callbackResultTypeName+".valueOf(response.getText());");
				}
				else if (callbackResultType.findMethod("fromString", new JType[]{stringType}) != null)
				{
					srcWriter.println(callbackResultTypeName+" result = "+callbackResultTypeName+".fromString(response.getText());");
				}
				else
				{
					srcWriter.println(callbackResultTypeName+" result = JsonUtils.unsafeEval(response.getText());");
				}
				srcWriter.println(callbackParameterName+".onSuccess(result);");
				srcWriter.println("}else {");
				srcWriter.println(callbackParameterName+".onSuccess(null);");
				srcWriter.println("}");
			}
			else
			{
				srcWriter.println(callbackParameterName+".onSuccess(null);");
			}
			srcWriter.println("}else{ ");
			srcWriter.println(callbackParameterName+".onError(response.getStatusCode(), response.getText());");
			srcWriter.println("}");
			srcWriter.println("}");
			srcWriter.println("public void onError(Request request, Throwable exception){");
			srcWriter.println(callbackParameterName+".onError(-1, exception.getMessage());");//TODO i18n
			srcWriter.println("}");
			srcWriter.println("});");
			
			srcWriter.println("try{");
			srcWriter.println("builder.send();");
			srcWriter.println("}catch (Exception e){");
			srcWriter.println(callbackParameterName+".onError(-1, e.getMessage());");//TODO i18n
			srcWriter.println("}");
			srcWriter.println("}");
		}
		catch (InvalidRestMethod e)
		{
			throw new CruxGeneratorException("Invalid Method: " + method.getEnclosingType().getName() + "." + method.getName() + "().", e);
		}
	}

	protected void generateMethodParamToURICode(SourcePrinter srcWriter, JMethod method, Method implementationMethod, String uriVariable)
    {
		Annotation[][] parameterAnnotations = implementationMethod.getParameterAnnotations();
		JParameter[] parameters = method.getParameters();

		for (int i = 0; i< parameterAnnotations.length; i++)
		{
			Annotation[] annotations = parameterAnnotations[i];
			for (Annotation annotation : annotations)
			{
				if ((annotation instanceof QueryParam) || (annotation instanceof PathParam))
				{
					srcWriter.println(uriVariable+"="+uriVariable+".replace(\"{"+parameters[i].getName()+"}\", "+parameters[i].getName()+");");
				}
			}
		}
    }

	protected String getCallbackResultTypeName(JType callbackParameter)
    {
	    JClassType jClassType = getTypeArgForGenericType(callbackParameter);
	    if (jClassType.isPrimitive() != null)
	    {
	    	return jClassType.isPrimitive().getQualifiedBoxedSourceName();
	    }
		return jClassType.getParameterizedQualifiedSourceName();
    }
	
	protected JClassType getTypeArgForGenericType(JType type)
    {
	    JParameterizedType parameterizedCallback = type.isParameterized();
	    if (parameterizedCallback == null)
	    {
	    	return objectType;
	    }
	    JClassType jClassType = parameterizedCallback.getTypeArgs()[0];
	    return jClassType;
    }
	

	protected String getRestURI(JMethod method, Method implementationMethod)
	{
		String methodPath = paths(serviceBasePath);
		Path path = implementationMethod.getAnnotation(Path.class);
		if (path != null)
		{
			methodPath = paths(methodPath, path.value());
		}
		String queryString = getQueryString(method, implementationMethod);
		if (queryString.length() > 0)
		{
			return methodPath+"?"+queryString;
		}
		return methodPath;
	}

	protected String getQueryString(JMethod method, Method implementationMethod)
	{
		StringBuilder str = new StringBuilder();
		boolean first = true;
		Annotation[][] parameterAnnotations = implementationMethod.getParameterAnnotations();
		JParameter[] parameters = method.getParameters();

		for (int i = 0; i< parameterAnnotations.length; i++)
		{
			Annotation[] annotations = parameterAnnotations[i];
			for (Annotation annotation : annotations)
			{
				if (annotation instanceof QueryParam)
				{
					if (!first)
					{
						str.append("&");
					}
					first = false;
					str.append(((QueryParam)annotation).value()+"={"+parameters[i].getName()+"}");
				}
			}
		}

		return str.toString();
	}

	protected String paths(String basePath, String... segments)
	{
		String path = basePath;
		if (path == null)
		{
			path = "";
		}
		for (String segment : segments)
		{
			if ("".equals(segment))
			{
				continue;
			}
			if (path.endsWith("/"))
			{
				if (segment.startsWith("/"))
				{
					segment = segment.substring(1);
					if ("".equals(segment))
					{
						continue;
					}
				}
				segment = Encode.encodePath(segment);
				path += segment;
			}
			else
			{
				segment = Encode.encodePath(segment);
				if ("".equals(path))
				{
					path = segment;
				}
				else if (segment.startsWith("/"))
				{
					path += segment;
				}
				else
				{
					path += "/" + segment;
				}
			}

		}
		return path;
	}

	/**
	 * @param srcWriter
	 * @param method
	 * @param returnType
	 * @return
	 */
	protected List<JParameter> generateProxyWrapperMethodDeclaration(SourcePrinter srcWriter, JMethod method)
	{
		srcWriter.println();
		srcWriter.print("public void ");
		srcWriter.print(method.getName() + "(");

		boolean needsComma = false;
		List<JParameter> parameters = new ArrayList<JParameter>();
		JParameter[] params = method.getParameters();
		for (int i = 0; i < params.length; ++i)
		{
			JParameter param = params[i];

			if (needsComma)
			{
				srcWriter.print(", ");
			}
			else
			{
				needsComma = true;
			}

			JType paramType = param.getType();
			if (i == (params.length - 1))
			{
				srcWriter.print("final ");
			}
			srcWriter.print(paramType.getParameterizedQualifiedSourceName());
			srcWriter.print(" ");

			String paramName = param.getName();
			parameters.add(param);
			srcWriter.print(paramName);
		}

		srcWriter.println(") {");
		return parameters;
	}

	@Override
	protected String[] getImports()
	{
		return new String[] { 
				RequestBuilder.class.getCanonicalName(), 
				RequestCallback.class.getCanonicalName(),
				Request.class.getCanonicalName(), 
				Response.class.getCanonicalName(), 
				JsonUtils.class.getCanonicalName()};
	}
	
	protected Method getImplementationMethod(JMethod method)
	{
		validateProxyMethod(method);
		Method implementationMethod = null;

		Method[] allMethods = restImplementationClass.getMethods();
		for (Method m: allMethods)
		{
			if (m.getName().equals(method.getName()))
			{
				implementationMethod = m;
				break;
			}
		}					
		
		validateImplementationMethod(method, implementationMethod);
		return implementationMethod;
	}

	private void validateProxyMethod(JMethod method)
    {
	    if (method.getReturnType() != JPrimitiveType.VOID) 
		{
			throw new CruxGeneratorException("Invalid signature for rest proxy method. Any method must be void");
		}
		JType[] parameterTypes = method.getParameterTypes();
		if (parameterTypes == null || parameterTypes.length < 1)
		{
			throw new CruxGeneratorException("Invalid signature for rest proxy method. Any method must have a last parameter of type RestProxy.Callback");
		}
		JClassType lastParameterType = parameterTypes[parameterTypes.length - 1].isClassOrInterface();
		if (lastParameterType == null || !callbackType.isAssignableFrom(lastParameterType))
		{
			throw new CruxGeneratorException("Invalid signature for rest proxy method. Any method must have a last parameter of type RestProxy.Callback");
		}
    }

	private void validateImplementationMethod(JMethod method, Method implementationMethod)
    {
		if (implementationMethod == null)
		{
			throw new CruxGeneratorException("Invalid signature for rest proxy method. Can not found the implementation method: "+
					method.getName()+", on class: "+restImplementationClass.getCanonicalName());
		}

		Class<?>[] implTypes = implementationMethod.getParameterTypes();
		JType[] proxyTypes = method.getParameterTypes();

		if ((proxyTypes.length -1)!= implTypes.length)
		{
			throw new CruxGeneratorException("Invalid signature for rest proxy method. The implementation method: "+
					method.getName()+", on class: "+restImplementationClass.getCanonicalName() + "does not match the parameters list.");
		}
		for (int i=0; i<implTypes.length; i++)
		{
			if (!isTypesCompatiblesForSerialization(implTypes[i], proxyTypes[i]))
			{
				throw new CruxGeneratorException("Invalid signature for rest proxy method. Incompatible parameters on method["+method.getReadableDeclaration()+"]");
			}
		}
		
		JClassType lastParameterType = proxyTypes[proxyTypes.length - 1].isClassOrInterface();
		if (!isTypesCompatiblesForSerialization(implementationMethod.getReturnType(), getTypeArgForGenericType(lastParameterType)))
		{
			throw new CruxGeneratorException("Invalid signature for rest proxy method. Return type of implementation method is not compatible with Callback's type. Method["+method.getReadableDeclaration()+"]");
		}
    }

	private boolean isTypesCompatiblesForSerialization(Class<?> class1, JType jType)
    {
	    if (jsonFriendlyTypes.contains(jType.getQualifiedSourceName()))
	    {
	    	return (getAllowedType(jType).contains(class1));
	    }
	    else
	    { 
	    	JClassType classOrInterface = jType.isClassOrInterface();
	    	if (classOrInterface != null)
	    	{
				if (javascriptObjectType.isAssignableFrom(classOrInterface))
		    	{
					if (classOrInterface.getQualifiedSourceName().equals(JsArray.class.getCanonicalName()))
					{
						boolean validArray = false;
						if (class1.isArray())
						{
							Class<?> componentType = class1.getComponentType();
							validArray = isTypesCompatiblesForSerialization(componentType, getTypeArgForGenericType(jType));
						}
						return validArray || (List.class.isAssignableFrom(class1)) || (Set.class.isAssignableFrom(class1));
					}
					else
					{
						return true;
					}
		    	}
	    	}
	    }
	    return false;
    }

	private List<Class<?>> getAllowedType(JType jType)
	{
		List<Class<?>> result = new ArrayList<Class<?>>();
		JPrimitiveType primitiveType = jType.isPrimitive();
		if (primitiveType == JPrimitiveType.INT)
		{
			result.add(Integer.TYPE);
			result.add(Integer.class);
		}
		else if (primitiveType == JPrimitiveType.SHORT)
		{
			result.add(Short.TYPE);
			result.add(Short.class);
		}
		else if (primitiveType == JPrimitiveType.LONG)
		{
			result.add(Long.TYPE);
			result.add(Long.class);
		}
		else if (primitiveType == JPrimitiveType.BYTE)
		{
			result.add(Byte.TYPE);
			result.add(Byte.class);
		}
		else if (primitiveType == JPrimitiveType.FLOAT)
		{
			result.add(Float.TYPE);
			result.add(Float.class);
		}
		else if (primitiveType == JPrimitiveType.DOUBLE)
		{
			result.add(Double.TYPE);
			result.add(Double.class);
		}
		else if (primitiveType == JPrimitiveType.BOOLEAN)
		{
			result.add(Boolean.TYPE);
			result.add(Boolean.class);
		}
		else if (primitiveType == JPrimitiveType.CHAR)
		{
			result.add(Character.TYPE);
			result.add(Character.class);
		}
		else if (jType.getQualifiedSourceName().equals(String.class.getCanonicalName()))
		{
			result.add(String.class);
		}
		else if (jType.getQualifiedSourceName().equals(Date.class.getCanonicalName()))
		{
			result.add(Date.class);
		}
		return result;
	}
}
