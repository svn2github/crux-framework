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
package org.cruxframework.crux.core.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class JClassUtils
{

	public static JType buildGetValueExpression(StringBuilder out, JClassType dtoType, String colKey, 
			String recordObject, boolean finishCommand) 
					throws NoSuchFieldException
    {
        if (StringUtils.isEmpty(colKey))
        {
			throw new NoSuchFieldException(colKey);
        }
        String[] props;
        if (colKey.contains("."))
        {
        	props = colKey.split("\\.");
        }
        else
        {
        	props = new String[]{colKey};
        }
        
        if (props != null && props.length > 0)
        {
        	StringBuilder getExpression = new StringBuilder();
        	StringBuilder checkNullExpression = new StringBuilder();
        	
        	getExpression.append(recordObject);
        	JType baseType = dtoType;
        	JClassType baseClassType = baseType.isClassOrInterface();
        	for (int i=0; i < props.length; i++)
        	{
        		if (baseClassType == null && i < props.length-1)
        		{
        			throw new NoSuchFieldException(colKey);
        		}
        		String prop = props[i];
        		if (i>0)
        		{
        			if (i>1)
        			{
        				checkNullExpression.append(" || ");
        			}
        			checkNullExpression.append(getExpression.toString()+"==null ");
        		}
        		
        		String getterMethod = JClassUtils.getGetterMethod(prop, baseClassType);
        		if (getterMethod == null)
        		{
        			JMethod method = getMethod(baseClassType, prop, new JType[]{});
        			if (method == null)
        			{
        				throw new NoSuchFieldException(colKey);
        			}
        			else
        			{
        				getterMethod = prop;
        			}
        		}
        		getExpression.append("."+getterMethod+"()");
        		baseType = JClassUtils.getReturnTypeFromMethodClass(baseClassType, getterMethod, new JType[]{});
        		baseClassType = baseType.isClassOrInterface();
        	}
        	if (finishCommand)
        	{
        		getExpression.append(";");
        	}
        	
        	if (checkNullExpression.length() > 0)
        	{
        		out.append(checkNullExpression.toString()+"?null:");
        	}
        	out.append(getExpression.toString());
        	
        	return baseType;
        }
        else
        {
			throw new NoSuchFieldException(colKey);
        }
    }		

	/**
	 * 
	 * @param propertyName
	 * @param baseClass 
	 * @return
	 */
	public static String getGetterMethod(String propertyName, JClassType baseClass)
	{
		if (propertyName == null || propertyName.length() == 0)
		{
			return null;
		}
		String result = ""+Character.toUpperCase(propertyName.charAt(0)); 
		result += propertyName.substring(1);
		if (propertyName.length() > 1)
		{
			try
            {
	            baseClass.getMethod("get"+result, new JType[]{});
                result = "get"+result;
            }
            catch (Exception e)
            {
	            try
                {
	                baseClass.getMethod("is"+result, new JType[]{});
	                result = "is"+result;
                }
                catch (Exception e1)
                {
                	if (baseClass.getSuperclass() == null)
                	{
                		result = null;
                	}
                	else
                	{
                		result = getGetterMethod(propertyName, baseClass.getSuperclass());
                	}
                }
            }
			
		}
		return result;
	}

	/**
	 * @param methodName
	 * @return
	 */
	public static JType getReturnTypeFromMethodClass(JClassType clazz, String methodName, JType[] params)
    {
	    JMethod method = getMethod(clazz, methodName, params);
		
		if (method == null)
		{
			return null;
		}
		JType returnType = method.getReturnType();
		return returnType;
    }

	/**
	 * @param clazz
	 * @param methodName
	 * @param params
	 * @return
	 */
	public static JMethod getMethod(JClassType clazz, String methodName, JType[] params)
    {
	    JMethod method = null;
	    JClassType superClass = clazz;
	    while (method == null && superClass != null)
	    {
	    	method = superClass.findMethod(methodName, params);
	    	superClass = superClass.getSuperclass();
	    }
	    return method;
    }

	/**
	 * 
	 * @param clazz
	 * @param methodName
	 * @return
	 */
	public static JMethod[] findMethods(JClassType clazz, String methodName)
	{
		List<JMethod> result = new ArrayList<JMethod>();
		JMethod[] methods = null;
	    JClassType superClass = clazz;
	    while (superClass.getSuperclass() != null)
	    {
	    	methods = superClass.getMethods();
	    	if (methods != null)
	    	{
	    		for (JMethod method : methods)
                {
	                if (method.getName().equals(methodName))
	                {
	                	result.add(method);
	                }
                }
	    	}
	    	superClass = superClass.getSuperclass();
	    }
	    return result.toArray(new JMethod[result.size()]);
	}
	
	/**
	 * 
	 * @param clazz
	 * @param propertyName
	 * @return
	 */
	public static JMethod[] findSetterMethods(JClassType clazz, String propertyName)
	{
		String setterMethodName = "set"+Character.toUpperCase(propertyName.charAt(0)); 
		if (propertyName.length() > 1)
		{
			setterMethodName += propertyName.substring(1);
		}
		
		JMethod[] methods = findMethods(clazz, setterMethodName);
		List<JMethod> result = new ArrayList<JMethod>();
		for (JMethod method : methods)
        {
	        if (method.getParameters().length == 1)
	        {
	        	result.add(method);
	        }
        }
		return result.toArray(new JMethod[result.size()]);
	}
	

	
	
	/**
	 * 
	 * @param valueVariable
	 * @param expectedType
	 * @return
	 * @throws NotFoundException 
	 */
	public static String getParsingExpressionForSimpleType(String valueVariable, JType expectedType) throws NotFoundException
	{
		if (expectedType == JPrimitiveType.INT || Integer.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return "Integer.parseInt("+valueVariable+")";
		}
		else if (expectedType == JPrimitiveType.SHORT || Short.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return "Short.parseShort("+valueVariable+")";
		}
		else if (expectedType == JPrimitiveType.LONG || Long.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return "Long.parseLong("+valueVariable+")";
		}
		else if (expectedType == JPrimitiveType.BYTE || Byte.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return "Byte.parseByte("+valueVariable+")";
		}
		else if (expectedType == JPrimitiveType.FLOAT || Float.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return "Float.parseFloat("+valueVariable+")";
		}
		else if (expectedType == JPrimitiveType.DOUBLE || Double.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return "Double.parseDouble("+valueVariable+")";
		}
		else if (expectedType == JPrimitiveType.BOOLEAN || Boolean.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return "Boolean.parseBoolean("+valueVariable+")";
		}
		else if (expectedType == JPrimitiveType.CHAR || Character.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return valueVariable+".charAt(0)";
		}
		else if (Date.class.getCanonicalName().equals(expectedType.getQualifiedSourceName()))
		{
			return "new "+Date.class.getCanonicalName()+"(Long.parseLong("+valueVariable+"))";
		}
		else if (expectedType.isEnum() != null)
		{
			return expectedType.getQualifiedSourceName()+".valueOf("+valueVariable+")";
		}
		else
		{
			JClassType stringType = ((JClassType)expectedType).getOracle().getType(String.class.getName());
		    if (stringType.isAssignableFrom((JClassType)expectedType))
		    {
		    	return valueVariable;
		    }
		    
		    if (((JClassType)expectedType).findConstructor(new JType[]{stringType}) != null)
		    {
		    	return "new "+expectedType.getQualifiedSourceName()+"("+valueVariable+")";
		    }
		    JMethod valueOfMethod = ((JClassType)expectedType).findMethod("valueOf", new JType[]{stringType});
			if (valueOfMethod != null && valueOfMethod.isStatic())
		    {
		    	return expectedType.getQualifiedSourceName()+".valueOf("+valueVariable+")";
		    }
		    JMethod fromStringMethod = ((JClassType)expectedType).findMethod("fromString", new JType[]{stringType});
			if (fromStringMethod != null && fromStringMethod.isStatic())
		    {
		    	return expectedType.getQualifiedSourceName()+".fromString("+valueVariable+")";
		    }
		}

		return null;
	}

	/**
	 * @param clazz
	 * @param name
	 * @return
	 */
	public static JField getDeclaredField(JClassType clazz, String name) throws NoSuchFieldException
	{
		JField field = clazz.getField(name);
		if (field == null)
		{
			if (clazz.getSuperclass() == null)
			{
				throw new NoSuchFieldException(name);
			}
			field = getDeclaredField(clazz.getSuperclass(), name);
		}

		return field;
	}
	
	/**
	 * @param clazz
	 * @return
	 */
	public static JField[] getDeclaredFields(JClassType clazz)
	{
		if (clazz.getSuperclass() == null)
		{
			return new JField[0];
		}
		Set<JField> result = new HashSet<JField>();
		JField[] declaredFields = clazz.getFields();
		for (JField field : declaredFields)
		{
			result.add(field);
		}
		clazz = clazz.getSuperclass();
		while (clazz.getSuperclass() != null)
		{
			declaredFields = clazz.getFields();
			for (JField field : declaredFields)
			{
				if (!result.contains(field))
				{
					result.add(field);
				}
			}
			clazz = clazz.getSuperclass();
		}
		
		return result.toArray(new JField[result.size()]);
	}
	
	/**
	 * @param method
	 * @return
	 */
	public static String getMethodDescription(JMethod method)
	{
		StringBuilder str = new StringBuilder();
		
		str.append(method.getEnclosingType().getQualifiedSourceName());
		str.append(".");
		str.append(method.getName());
		str.append("(");
		boolean needsComma = false;
		
		for (JParameter parameter: method.getParameters())
		{
			if (needsComma)
			{
				str.append(",");
			}
			needsComma = true;
			str.append(parameter.getType().getParameterizedQualifiedSourceName());
		}
		str.append(")");
		
		return str.toString();
	}
	
	/**
	 * Returns a string to be used in generic code block, according with the given type 
	 * @param type
	 * @return
	 */
	public static String getGenericDeclForType(JType type)
	{
		if (type.isPrimitive() != null)
		{
			JPrimitiveType jPrimitiveType = type.isPrimitive();
			return jPrimitiveType.getQualifiedBoxedSourceName();
		}
		else
		{
			return type.getParameterizedQualifiedSourceName();
		}
	}
	
	/**
	 * @param type
	 * @return
	 */
	public static boolean isSimpleType(JType type)
	{
		if (type instanceof JPrimitiveType)
		{
			return true;
		}
		else
		{
			try
            {
	            JClassType classType = (JClassType)type;

	            JClassType charSequenceType = classType.getOracle().getType(CharSequence.class.getCanonicalName());
	            JClassType dateType = classType.getOracle().getType(Date.class.getCanonicalName());
	            JClassType numberType = classType.getOracle().getType(Number.class.getCanonicalName());
	            JClassType booleanType = classType.getOracle().getType(Boolean.class.getCanonicalName());
	            JClassType characterType = classType.getOracle().getType(Character.class.getCanonicalName());

	            return (classType.isPrimitive() != null) ||
	            (numberType.isAssignableFrom(classType)) ||
	            (booleanType.isAssignableFrom(classType)) ||
	            (characterType.isAssignableFrom(classType)) ||
	            (charSequenceType.isAssignableFrom(classType)) ||
	            (charSequenceType.isAssignableFrom(classType)) ||
	            (dateType.isAssignableFrom(classType)) ||
	            (classType.isEnum() != null);
            }
            catch (NotFoundException e)
            {
            	throw new CruxGeneratorException(e.getMessage(), e);
            }		
		}
	}
	
	/**
	 * Returns <code>true</code> is the given field has both a "get" and a "set" methods.
	 * @param clazz
	 * @param field
	 * @return
	 */
	public static boolean hasGetAndSetMethods(JField field, JClassType clazz)
	{
		return hasGetMethod(field, clazz) && hasSetMethod(field, clazz);
	}
	
	/**
	 * Returns <code>true</code> is the given field has an associated public "get" method.
	 * @param clazz
	 * @param field
	 * @return
	 */
	public static boolean hasGetMethod(JField field, JClassType clazz)
	{
		String getterMethodName = "get"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
		try
		{
			return (clazz.getMethod(getterMethodName, new JType[]{}) != null);
		}
		catch (Exception e)
		{
			try
			{
				getterMethodName = "is"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
				return (clazz.getMethod(getterMethodName, new JType[]{}) != null);
			}
			catch (Exception e1)
			{
				if (clazz.getSuperclass() == null)
				{
					return false;
				}
				else
				{
					return hasGetMethod(field, clazz.getSuperclass());
				}
			}
		}
	}	
	
	/**
	 * Returns <code>true</code> is the given field has an associated public "set" method.
	 * @param field
	 * @param clazz
	 * @return
	 */
	public static boolean hasSetMethod(JField field, JClassType clazz)
	{
		String setterMethodName = "set"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
		try
		{
			return (clazz.getMethod(setterMethodName, new JType[]{field.getType()}) != null);
		}
		catch (Exception e)
		{
			if (clazz.getSuperclass() == null)
			{
				return false;
			}
			else
			{
				return hasSetMethod(field, clazz.getSuperclass());
			}
		}
	}
	
	/**
	 * Verify if the given field is fully accessible.
	 * @param field
	 * @param clazz
	 * @return <code>true</code> if the field is public or has associated "get" and "set" methods.
	 */
	public static boolean isFullAccessibleField(JField field, JClassType clazz)
	{
		return field.isPublic() || hasGetAndSetMethods(field, clazz);
	}	
	
	/**
	 * Verify if the given field is a visible property
	 * @param voClass
	 * @param field
	 * @param allowProtected
	 * @return
	 */
	public static boolean isPropertyVisibleToRead(JClassType voClass, JField field, boolean allowProtected)
	{
		if (field.isPublic() || (allowProtected && field.isProtected()))
		{
			return true;
		}
		else
		{
			return hasGetMethod(field, voClass);
		}
	}
	
	/**
	 * Verify if the given field is a visible property
	 * @param voClass
	 * @param field
	 * @param allowProtected
	 * @return
	 */
	public static boolean isPropertyVisibleToWrite(JClassType voClass, JField field, boolean allowProtected)
	{
		if ((field.isPublic() || (allowProtected && field.isProtected())) && !field.isFinal())
		{
			return true;
		}
		else
		{
			return hasSetMethod(field, voClass);
		}
	}

	/**
	 * Generates a property set block. First try to set the field directly, then try to use a javabean setter method.
	 * 
	 * @param logger
	 * @param voClass
	 * @param field
	 * @param parentVariable
	 * @param valueVariable
	 * @param sourceWriter
	 */
	public static void generateFieldValueSet(JClassType voClass, JField field, String parentVariable,  
			                           String valueVariable, SourcePrinter sourceWriter, boolean allowProtected)
	{
		if (field.isPublic() || (allowProtected && field.isProtected()))
		{
			sourceWriter.println(parentVariable+"."+field.getName()+"="+valueVariable+";");
		}
		else
		{
			String setterMethodName = "set"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
			try
			{
				if (voClass.getMethod(setterMethodName, new JType[]{field.getType()}) != null)
				{
					sourceWriter.println(parentVariable+"."+setterMethodName+"("+valueVariable+");");
				}
			}
			catch (Exception e)
			{
				throw new CruxGeneratorException("Property ["+field.getName()+"] could not be created. This is not visible neither has a getter/setter method.");
			}
		}
	}
	
	/**
	 * Generates a property get block. First try to get the field directly, then try to use a javabean getter method.
	 * 
	 * @param voClass
	 * @param field
	 * @param parentVariable
	 * @param allowProtected
	 */
	public static String getFieldValueGet(JClassType voClass, JField field, String parentVariable, boolean allowProtected)
	{
		if (field.isPublic() || (allowProtected && field.isProtected()))
		{
			return parentVariable+"."+field.getName();
		}
		else
		{
			String getterMethodName = "get"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
			try
			{
				JMethod method = voClass.getMethod(getterMethodName, new JType[]{});
				if (method != null && (method.isPublic() || (allowProtected && method.isProtected())))
				{
					return (parentVariable+"."+getterMethodName+"()");
				}
				else
				{
					throw new CruxGeneratorException("Property ["+field.getName()+"] could not be created. This is not visible neither has a getter/setter method.");
				}
			}
			catch (Exception e)
			{
				try
				{
					getterMethodName = "is"+Character.toUpperCase(field.getName().charAt(0))+field.getName().substring(1);
					JMethod method = voClass.getMethod(getterMethodName, new JType[]{});
					if (method != null && (method.isPublic() || (allowProtected && method.isProtected())))
					{
						return (parentVariable+"."+getterMethodName+"()");
					}
					else
					{
						throw new CruxGeneratorException("Property ["+field.getName()+"] could not be created. This is not visible neither has a getter/setter method.");
					}
				}
				catch (Exception e1)
				{
					throw new CruxGeneratorException("Property ["+field.getName()+"] could not be created. This is not visible neither has a getter/setter method.");
				}
			}
		}
	}

	public static boolean isValidSetterMethod(JMethod method)
	{
        return (method.isPublic() && method.getName().startsWith("set") && method.getName().length() >3 && method.getParameters().length == 1);
	}
	
	public static String getPropertyForSetterMethod(JMethod method)
    {
		String name = method.getName().substring(3);
		name = Character.toLowerCase(name.charAt(0))+ name.substring(1);
		
		return name;
    }
	
	public static List<JMethod> getSetterMethods(JClassType objectType)
    {
		List<JMethod> result = new ArrayList<JMethod>();
	    JMethod[] methods = objectType.getOverridableMethods();
	    
	    for (JMethod jMethod : methods)
        {
	        if (isValidSetterMethod(jMethod))
	        {
	        	result.add(jMethod);
	        }
        }
	    
	    return result;
    }

	public static String getEmptyValueForType(JType objectType)
    {
		JPrimitiveType primitiveType = objectType.isPrimitive();
		if (primitiveType != null)
		{
			if ((primitiveType == JPrimitiveType.INT)
					||(primitiveType == JPrimitiveType.SHORT)
					||(primitiveType == JPrimitiveType.LONG)
					||(primitiveType == JPrimitiveType.BYTE)
					||(primitiveType == JPrimitiveType.FLOAT)
					||(primitiveType == JPrimitiveType.DOUBLE))
					{
				return "0";
					}
			else if (primitiveType == JPrimitiveType.BOOLEAN)
			{
				return "false";
			}
			else if (primitiveType == JPrimitiveType.CHAR)
			{
				return "' '";
			}
		}
		
		return "null";
    }
	
	public static JClassType getTypeArgForGenericType(JClassType type)
    {
	    JParameterizedType parameterized = type.isParameterized();
	    if (parameterized == null)
	    {
	    	return type.getOracle().findType("java.lang.Object");
	    }
	    JClassType jClassType = parameterized.getTypeArgs()[0];
	    return jClassType;
    }

}
