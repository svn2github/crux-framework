/*
 * Copyright 2010 Google Inc.
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
package org.cruxframework.crux.gadget.client.features.osapi;

/**
 * An enum used to define an order of sorting objects in a collection.
 */
public enum SortOrder {
  ASCENDING("ascending"), DESCENDING("descending");

  private String order;

  private SortOrder(String order) {
    this.order = order;
  }

  /**
   * Returns the {@link String} representing the order of sorting.
   *
   * @return The {@link String} representing the order of sorting.
   */
  String getOrder() {
    return order;
  }
}