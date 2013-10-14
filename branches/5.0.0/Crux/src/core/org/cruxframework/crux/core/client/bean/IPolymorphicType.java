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
package org.cruxframework.crux.core.client.bean;

/**
 * Interface that indicates that a object has a JSON Polymorphic type.
 * @author samuel.cardoso
 */
public interface IPolymorphicType {
	/**
	 * @return the type of this object. It should be implemented as:
	 * return this.getClass().getName();
	 */
	public String getType();

	/**
	 * @param type the type to be set.
	 * To indicate that type property doesn't have any business value, annotate it with:
	 * @JsonIgnore
	 */
	public void setType(String type); 
}
