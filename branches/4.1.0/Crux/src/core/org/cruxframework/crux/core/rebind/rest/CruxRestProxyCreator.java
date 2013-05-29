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

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.service.RestProxy.Callback;
import org.cruxframework.crux.core.client.service.RestProxy.RestService;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.server.rest.annotation.CookieParam;
import org.cruxframework.crux.core.server.rest.annotation.FormParam;
import org.cruxframework.crux.core.server.rest.annotation.GET;
import org.cruxframework.crux.core.server.rest.annotation.HeaderParam;
import org.cruxframework.crux.core.server.rest.annotation.Path;
import org.cruxframework.crux.core.server.rest.annotation.PathParam;
import org.cruxframework.crux.core.server.rest.annotation.QueryParam;
import org.cruxframework.crux.core.server.rest.annotation.StateValidationModel;
import org.cruxframework.crux.core.server.rest.core.registry.RestServiceScanner;
import org.cruxframework.crux.core.server.rest.util.Encode;
import org.cruxframework.crux.core.server.rest.util.HttpHeaderNames;
import org.cruxframework.crux.core.server.rest.util.HttpMethodHelper;
import org.cruxframework.crux.core.server.rest.util.InvalidRestMethod;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Cookies;

/**
 * This class creates a client proxy for calling rest services
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class CruxRestProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	private Class<?> restImplementationClass;
	private JClassType callbackType;
	private JClassType javascriptObjectType;
	private String serviceBasePath;
	private Map<String, Method> readMethods = new HashMap<String, Method>();
	private Map<String, Method> updateMethods = new HashMap<String, Method>();
	private Set<RestMethodInfo> restMethods = new HashSet<RestMethodInfo>();
	private boolean mustGenerateStateControlMethods;
	
	
	public CruxRestProxyCreator(TreeLogger logger, GeneratorContextExt context, JClassType baseIntf)
	{
		super(logger, context, baseIntf, true);
		callbackType = context.getTypeOracle().findType(Callback.class.getCanonicalName());
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
		initializeRestMethods();
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
	protected void generateProxyFields(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		if (mustGenerateStateControlMethods)
		{
			srcWriter.println(FastMap.class.getCanonicalName()+"<String> __currentEtags = new "+FastMap.class.getCanonicalName()+"<String>();");
		}
	}
	
	@Override
    protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
    {
		if (mustGenerateStateControlMethods)
		{
			generateStateControlMethods(srcWriter);
		}
		for (RestMethodInfo methodInfo : restMethods)
        {
    		generateWrapperMethod(methodInfo, srcWriter);
        }
    }

	protected void generateStateControlMethods(SourcePrinter srcWriter)
	{
		srcWriter.println("public boolean __readCurrentEtag(String uri, RequestBuilder builder, boolean required){");
		srcWriter.println("String etag = __currentEtags.get(uri);");
		srcWriter.println("if (required && etag == null){");
		srcWriter.println("return false;");
		srcWriter.println("}");
		srcWriter.println("if (etag != null){");
		srcWriter.println("builder.setHeader("+EscapeUtils.quote(HttpHeaderNames.IF_MATCH)+", etag);");
		srcWriter.println("}");
		srcWriter.println("return true;");
		srcWriter.println("}");
		srcWriter.println("public void __saveCurrentEtag(String uri, Response response){");
		srcWriter.println("String etag = response.getHeader("+EscapeUtils.quote(HttpHeaderNames.ETAG)+");");
		srcWriter.println("__currentEtags.put(uri, etag);");
		srcWriter.println("}");
	}

	private void initializeRestMethods()
    {
    	JMethod[] methods = baseIntf.getOverridableMethods();
    	for (JMethod method : methods)
    	{
			Method implementationMethod = getImplementationMethod(method);
			String methodURI = getRestURI(method, implementationMethod);
			StateValidationModel validationModel = HttpMethodHelper.getStateValidationModel(implementationMethod);
			if (validationModel != null && !validationModel.equals(StateValidationModel.NO_VALIDATE))
			{
				updateMethods.put(methodURI, implementationMethod);
			}
			else if (implementationMethod.getAnnotation(GET.class) != null)
			{
				readMethods.put(methodURI, implementationMethod);
			}
			restMethods.add(new RestMethodInfo(method, implementationMethod, methodURI));
    	}
		
	    mustGenerateStateControlMethods = false;
		for (Entry<String, Method> entry : updateMethods.entrySet())
		{
			Method method = entry.getValue();
			Method readMethod = readMethods.get(entry.getKey());
			if (readMethod == null)
			{
				throw new CruxGeneratorException("Can not create the rest proxy. Can not found the " +
						"GET method for state dependent write method ["+method.toString()+"].");
			}
			mustGenerateStateControlMethods = true; 
		}
    }

	protected void generateWrapperMethod(RestMethodInfo methodInfo, SourcePrinter srcWriter)
	{
		try
		{
			List<JParameter> parameters = generateProxyWrapperMethodDeclaration(srcWriter, methodInfo.method);
			String httpMethod = HttpMethodHelper.getHttpMethod(methodInfo.implementationMethod, false);
			JParameter callbackParameter = parameters.get(parameters.size()-1);
			String callbackResultTypeName = getCallbackResultTypeName(callbackParameter.getType().isClassOrInterface());
			String callbackParameterName = callbackParameter.getName();

			srcWriter.println("String baseURIPath = " + EscapeUtils.quote(methodInfo.methodURI) + ";");
			generateMethodParamToURICode(srcWriter, methodInfo, "baseURIPath");
			srcWriter.println("final String restURI = baseURIPath;");

			srcWriter.println("RequestBuilder builder = new RequestBuilder(RequestBuilder."+httpMethod+", restURI);");
			srcWriter.println("builder.setCallback(new RequestCallback(){");
			srcWriter.println("public void onResponseReceived(Request request, Response response){");
			srcWriter.println("int s = (response.getStatusCode()-200);");
			srcWriter.println("if (s >= 0 && s < 10){");
			if (!callbackResultTypeName.equalsIgnoreCase("void"))
			{
				JClassType callbackResultType = JClassUtils.getTypeArgForGenericType(callbackParameter.getType().isClassOrInterface());
				srcWriter.println("if (Response.SC_NO_CONTENT != response.getStatusCode()){");
				srcWriter.println("try{");

				if (callbackResultType != null && callbackResultType.isAssignableTo(javascriptObjectType))
				{
					srcWriter.println(callbackResultTypeName+" result = "+JsonUtils.class.getCanonicalName()+".safeEval(jsonText);");
				}
				else
				{
					srcWriter.println("JSONValue jsonValue = JSONParser.parseStrict(response.getText());");
					String serializerName = new JSonSerializerProxyCreator(context, logger, callbackResultType).create();
					srcWriter.println(callbackResultTypeName+" result = new "+serializerName+"().decode(jsonValue);");
				}
				generateSalveStateBlock(srcWriter, methodInfo.implementationMethod, "response", "restURI", methodInfo.methodURI);
				srcWriter.println(callbackParameterName+".onSuccess(result);");
				srcWriter.println("}catch (Exception e){");
				srcWriter.println(callbackParameterName+".onError(-1, Crux.getMessages().restServiceUnexpectedError(e.getMessage()));");
				srcWriter.println("}");
				srcWriter.println("}else {");
				generateSalveStateBlock(srcWriter, methodInfo.implementationMethod, "response", "restURI", methodInfo.methodURI);
				srcWriter.println(callbackParameterName+".onSuccess(null);");
				srcWriter.println("}");
			}
			else
			{
				generateSalveStateBlock(srcWriter, methodInfo.implementationMethod, "response", "restURI", methodInfo.methodURI);
				srcWriter.println(callbackParameterName+".onSuccess(null);");
			}
			srcWriter.println("}else{ ");
			srcWriter.println(callbackParameterName+".onError(response.getStatusCode(), response.getText());");
			srcWriter.println("}");
			srcWriter.println("}");
			srcWriter.println("public void onError(Request request, Throwable exception){");
			srcWriter.println(callbackParameterName+".onError(-1, Crux.getMessages().restServiceUnexpectedError(exception.getMessage()));");
			srcWriter.println("}");
			srcWriter.println("});");

			srcWriter.println("try{");
			generateMethodParamToBodyCode(srcWriter, methodInfo, "builder", httpMethod);
			generateValidateStateBlock(srcWriter, methodInfo.implementationMethod, "builder", "restURI", methodInfo.methodURI, callbackParameterName);
			srcWriter.println("builder.send();");
			srcWriter.println("}catch (Exception e){");
			srcWriter.println(callbackParameterName+".onError(-1, Crux.getMessages().restServiceUnexpectedError(e.getMessage()));");
			srcWriter.println("}");
			srcWriter.println("}");
		}
		catch (InvalidRestMethod e)
		{
			throw new CruxGeneratorException("Invalid Method: " + methodInfo.method.getEnclosingType().getName() + "." + methodInfo.method.getName() + "().", e);
		}
	}

	private void generateSalveStateBlock(SourcePrinter srcWriter, Method method, String responseVar, String uriVar, String uri)
    {
		if (readMethods.containsKey(uri) && updateMethods.containsKey(uri))
		{
			GET get = method.getAnnotation(GET.class);
			if (get != null)
			{
				srcWriter.println("__saveCurrentEtag("+uriVar+", "+responseVar+");");
			}
		}
    }

	private void generateValidateStateBlock(SourcePrinter srcWriter, Method method, String builderVar, String uriVar, String uri, String callbackParameterName)
    {
		if (readMethods.containsKey(uri) && updateMethods.containsKey(uri))
		{
			StateValidationModel validationModel = HttpMethodHelper.getStateValidationModel(method);
			if (validationModel != null)
			{
				srcWriter.println("if (!__readCurrentEtag("+uriVar+", "+builderVar+","+validationModel.equals(StateValidationModel.ENSURE_STATE_MATCHES)+")){");
				srcWriter.println(callbackParameterName+".onError(-1, Crux.getMessages().restServiceMissingStateEtag("+uriVar+"));");
				srcWriter.println("return;");
				srcWriter.println("}");
			}
		}
    }

	protected void generateMethodParamToBodyCode(SourcePrinter srcWriter, RestMethodInfo methodInfo, String builder, String httpMethod)
	{
		Annotation[][] parameterAnnotations = methodInfo.implementationMethod.getParameterAnnotations();
		JParameter[] parameters = methodInfo.method.getParameters();
		boolean formEncoded = false;
		boolean hasBodyObject = false;

		String formString = getFormString(methodInfo); 
		if (!StringUtils.isEmpty(formString))
		{
			srcWriter.println("String requestData = "+EscapeUtils.quote(formString)+";");
		}
		for (int i = 0; i< parameterAnnotations.length; i++)
		{
			Annotation[] annotations = parameterAnnotations[i];
			if (annotations == null || annotations.length == 0)
			{ // JSON on body
				if(hasBodyObject)
				{
					throw new CruxGeneratorException("Invalid Method: " + methodInfo.method.getEnclosingType().getName() + "." + methodInfo.method.getName() + "(). " +
					"Request body can not contain more than one body parameter (JSON serialized object).");
				}

				hasBodyObject = true;
				String serializerName = new JSonSerializerProxyCreator(context, logger, parameters[i].getType()).create();
				srcWriter.println(builder+".setHeader(\"Content-type\", \"application/json\");");
				srcWriter.println("JSONValue serialized = new "+serializerName+"().encode("+parameters[i].getName()+");");
				srcWriter.println("String requestData = (serialized==null||serialized.isNull()!=null)?null:serialized.toString();");
			}
			else
			{
				for (Annotation annotation : annotations)
				{
					if (annotation instanceof FormParam)
					{
						if (!formEncoded)
						{
							srcWriter.println(builder+".setHeader(\"Content-type\", \"application/x-www-form-urlencoded\");");
							formEncoded = true;
						}
						srcWriter.println("requestData = requestData.replace(\"{"+parameters[i].getName()+"}\", URL.encode("+"("+parameters[i].getName()+"!=null?"+parameters[i].getName()+":\"\")));");
					}
					if (annotation instanceof HeaderParam)
					{
						srcWriter.println(builder+".setHeader("+EscapeUtils.quote(((HeaderParam) annotation).value())+", URL.encode("+"("+parameters[i].getName()+"!=null?"+parameters[i].getName()+":\"\")));");
					}
					if (annotation instanceof CookieParam)
					{
						srcWriter.println(Cookies.class.getCanonicalName()+".setCookie("+EscapeUtils.quote(((CookieParam) annotation).value()) + 
								", URL.encode("+"("+parameters[i].getName()+"!=null?"+parameters[i].getName()+":\"\")), new "+Date.class.getCanonicalName()+"(2240532000000L), null, \"/\", false);");
					}
				}
			}
		}
		if (hasBodyObject && formEncoded)
		{
			throw new CruxGeneratorException("Invalid Method: " + methodInfo.method.getEnclosingType().getName() + "." + methodInfo.method.getName() + "(). " +
			"Request body can not contain form parameters and a JSON serialized object.");
		}
		if (hasBodyObject || formEncoded)
		{
			if (httpMethod.equals("GET"))
			{
				throw new CruxGeneratorException("Invalid Method: " + methodInfo.method.getEnclosingType().getName() + "." + methodInfo.method.getName() + "(). " +
				"Can not use request body parameters on a GET operation.");
			}
			srcWriter.println(builder+".setRequestData(requestData);");
		}
	}

	protected void generateMethodParamToURICode(SourcePrinter srcWriter, RestMethodInfo methodInfo, String uriVariable)
	{
		Annotation[][] parameterAnnotations = methodInfo.implementationMethod.getParameterAnnotations();
		JParameter[] parameters = methodInfo.method.getParameters();

		for (int i = 0; i< parameterAnnotations.length; i++)
		{
			Annotation[] annotations = parameterAnnotations[i];
			for (Annotation annotation : annotations)
			{
				if ((annotation instanceof QueryParam) || (annotation instanceof PathParam))
				{
					srcWriter.println(uriVariable+"="+uriVariable+".replace(\"{"+parameters[i].getName()+"}\", "+
							"("+parameters[i].getName()+"!=null?"+parameters[i].getName()+":\"\"));");
				}
			}
		}
	}

	protected String getCallbackResultTypeName(JClassType callbackParameter)
	{
		JClassType jClassType = JClassUtils.getTypeArgForGenericType(callbackParameter);
		if (jClassType.isPrimitive() != null)
		{
			return jClassType.isPrimitive().getQualifiedBoxedSourceName();
		}
		return jClassType.getParameterizedQualifiedSourceName();
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

	protected String getFormString(RestMethodInfo methodInfo)
	{
		StringBuilder str = new StringBuilder();
		boolean first = true;
		Annotation[][] parameterAnnotations = methodInfo.implementationMethod.getParameterAnnotations();
		JParameter[] parameters = methodInfo.method.getParameters();

		try
		{
			for (int i = 0; i< parameterAnnotations.length; i++)
			{
				Annotation[] annotations = parameterAnnotations[i];
				for (Annotation annotation : annotations)
				{
					if (annotation instanceof FormParam)
					{
						if (!first)
						{
							str.append("&");
						}
						first = false;
						str.append(URLEncoder.encode(((FormParam)annotation).value(), "UTF-8")+"={"+parameters[i].getName()+"}");
					}
				}
			}
		}
		catch (UnsupportedEncodingException e)
		{
			throw new CruxGeneratorException("Unsupported encoding for parameter name on method ["+methodInfo.implementationMethod.toString()+"]");
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

	@Override
	protected String[] getImports()
	{
		return new String[] { 
				RequestBuilder.class.getCanonicalName(), 
				RequestCallback.class.getCanonicalName(),
				Request.class.getCanonicalName(), 
				Response.class.getCanonicalName(), 
				JsonUtils.class.getCanonicalName(), 
				JSONValue.class.getCanonicalName(),
				JSONParser.class.getCanonicalName(), 
				URL.class.getCanonicalName(), 
				Crux.class.getCanonicalName()};
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
		if (!isTypesCompatiblesForSerialization(implementationMethod.getReturnType(), JClassUtils.getTypeArgForGenericType(lastParameterType)))
		{
			throw new CruxGeneratorException("Invalid signature for rest proxy method. Return type of implementation method is not compatible with Callback's type. Method["+method.getReadableDeclaration()+"]");
		}
	}

	private boolean isTypesCompatiblesForSerialization(Class<?> class1, JType jType)
	{
		if (JSonSerializerProxyCreator.isJsonFriendly(jType))
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
							JClassType jClassType = jType.isClassOrInterface();
							validArray = jClassType != null && isTypesCompatiblesForSerialization(componentType, JClassUtils.getTypeArgForGenericType(jClassType));
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
		//Use a jsonEncorer implicitly
		return true;
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
		else if (jType.getQualifiedSourceName().equals(BigInteger.class.getCanonicalName()))
		{
			result.add(BigInteger.class);
		}
		else if (jType.getQualifiedSourceName().equals(BigDecimal.class.getCanonicalName()))
		{
			result.add(BigDecimal.class);
		}
		return result;
	}
	
	private static class RestMethodInfo
	{
		private JMethod method;
		private Method implementationMethod;
		private String methodURI;
		
		public RestMethodInfo(JMethod method, Method implementationMethod, String methodURI)
        {
			this.method = method;
			this.implementationMethod = implementationMethod;
			this.methodURI = methodURI;
        }
	}
}
