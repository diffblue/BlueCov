/**
 * Copyright 2016-2021 Diffblue Ltd and contributors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cprover.coverage.test;

/**
 * Literals class for file extensions.
 */
public final class Extensions {

  /**
   * Marks Java byte code class files.
   */
  public static final String CLASS = ".class";
  /**
   * Marks JBMC goal files.
   */
  public static final String JSON_PROPERTIES = ".class.json";
  /**
   * Marks BlueCov coverage result files.
   */
  public static final String RESULT_JSON = ".result.class.json";

  private Extensions() {
  }
}
