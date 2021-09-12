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
package org.cprover.coverage;

/**
 * <code>CoverageUtils</code> holds constants used in instrumentation.
 */
public final class CoverageUtils {

  /**
   * <code>SOURCE_LOCATION</code> is the JSON field name of the source loaction.
   */
  public static final String SOURCE_LOCATION = "sourceLocation";
  /**
   * <code>BYTECODE_INDEX</code> is the JSON field name of the bytecode index.
   */
  public static final String BYTECODE_INDEX = "bytecodeIndex";
  /**
   * <code>JAVA_NS_PREFIX</code> is the internal Java prefix.
   */
  public static final String JAVA_NS_PREFIX = "java::";
  /**
   * <code>JAVA_NS_PREFIX_LENGTH</code> is the length of the Java prefix.
   */
  public static final int JAVA_NS_PREFIX_LENGTH = JAVA_NS_PREFIX.length();

  /**
   * private constructor fo <code>CoverageUtils</code> to prevent instantiation.
   */
  private CoverageUtils() {
  }
}
