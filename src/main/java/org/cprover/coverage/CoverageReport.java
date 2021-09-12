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

import java.util.HashMap;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

/**
 * <code>CoverageReport</code> reporter classs to generate reports from coverage
 * database.
 */
public final class CoverageReport {

  /**
   * Provides the currently configured {@link CoverageLog}'s contents as {@link String JSON}.
   *
   * @return The {@link String contents} of the currently configured {@link CoverageLog}.
   */
  public static String getReport() {
    CoverageLog logger = CoverageLog.getInstance(false);
    return getReport(logger);
  }

  public static String getReport(CoverageLog logger) {
    logger.setReport(false);
    HashMap<String, Integer> descCountMap = logger.getHitCounts();
    HashMap<String, int[]> descLineMap = logger.getLinesForBlock();

    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

    for (String description : descCountMap.keySet()) {
      JsonObjectBuilder entryBuilder = Json.createObjectBuilder();

      Integer hits = descCountMap.get(description);
      entryBuilder = entryBuilder
          .add("goalID", description)
          .add("hitCount", hits);

      JsonArrayBuilder lineNumbers = Json.createArrayBuilder();
      for (int line : descLineMap.get(description)) {
        JsonObjectBuilder lineEntry = Json.createObjectBuilder();
        lineNumbers
            .add(lineEntry
                .add("lineNumber", line));
      }
      entryBuilder.add("coveredLines", lineNumbers);
      arrayBuilder.add(entryBuilder);
    }
    return arrayBuilder.build().toString();
  }

  /**
   * <code>main</code> is entry point of coverage reporter.
   *
   * @param args a <code>String</code> value
   */
  public static void main(final String[] args) {
    new CoverageReport().doReport();
  }

  /**
   * <code>doReport</code> emits a report of coverage in database.
   */
  void doReport() {
    System.out.println(getReport());
  }
}
