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
package org.cprover.coverage.helper;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.cprover.coverage.CoverageReport;

/**
 * Converts generated {@link String JSON} to comparable values. This is necessary because {@link
 * CoverageReport} generates its output from hash maps, whose order is platform dependent.
 */
public final class PropertyHelper {

  private static final String GOAL_SEPARATOR = "\\[?[{]\"goal";

  private PropertyHelper() {
  }

  /**
   * Splits the input by goals and sorts these goals lexicographically, before rejoining them to a
   * {@link String}.
   *
   * @param json JSON-formatted {@link String coverage output}.
   * @return Sorted {@link String coverage output}.
   */
  public static String toSortedGoals(final String json) {
    final String oneLine = json.replaceAll("\\s*", "").replaceAll("]$", ",");
    final String goals[] = oneLine.split(GOAL_SEPARATOR);
    final Stream<String> sortedGoals = Arrays.stream(goals).sorted();
    return sortedGoals.collect(Collectors.joining(System.lineSeparator()));
  }

  /**
   * Provides {@link CoverageReport#getReport()} as a sorted, comparable {@link String}.
   *
   * @return A unit test-comparable coverage report.
   */
  public static String getCoverageReport() {
    return toSortedGoals(CoverageReport.getReport());
  }
}
