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
 * Describe class <code>CoverageReset</code> here.
 */
public final class CoverageReset {

  /**
   * Describe <code>main</code> method here.
   *
   * @param args a <code>String</code> value
   */
  public static void main(final String[] args) {
    new CoverageReset().doReset();
  }

  /**
   * <code>doReset</code> resets a coverage database.
   */
  void doReset() {
    CoverageLog logger = CoverageLog.getInstance(false);
    logger.setReport(false);
    logger.resetCoverage();
  }
}
