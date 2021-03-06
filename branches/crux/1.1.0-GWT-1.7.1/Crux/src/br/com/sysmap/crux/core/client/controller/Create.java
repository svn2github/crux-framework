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
package br.com.sysmap.crux.core.client.controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to inform generators to instantiate automatically the field.
 * This annotation has two major proposes:
 * 1) It is used to makes the rpc mechanism painless. If an interface that extends
 * RemoteService (not annotated with @RemoteServiceRelativePath) is the type
 * for the field, the serviceEntryPoint is mapped to rpc (Crux default servlet)
 * 
 * 2)It is used to create DTOs that binds screen information and populate them automatically.
 * In that case, they can use the annotation @ScreenBind to inform the name of the widget (or element) 
 * that will provide the value for each object field. 
 * @author Thiago Bustamante
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Create 
{
}
