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

import com.google.common.io.CharStreams;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import org.cprover.coverage.build.MavenBuild;
import org.cprover.coverage.helper.PropertyHelper;
import org.junit.Assert;
import org.junit.Test;

public class DiffblueBenchmarksTest {

  @Test
  public void testJavaTest() throws Exception {
    try (final RepositoryEnvironment repo = new RepositoryEnvironment(
        "https://github.com/diffblue-benchmarks/java-test.git",
        new MavenBuild("main-module/pom.xml"))) {
      final Path classPath = repo.getWorkingDirectory()
          .resolve("main-module/target/classes/com/diffblue/javatest/");
      final String[] files = {"BubbleSort", "Calc", "Main", "Search", "TicTacToe", "Validation",
          "nestedobjects/User", "nestedobjects/subpackage/Item", "nestedobjects/subpackage/Order"};
      for (final String file : files) {
        final String fileName = file + Extensions.JSON_PROPERTIES;
        try (final InputStream is = DiffblueBenchmarksTest.class.getResourceAsStream(
            "/java-test/com/diffblue/javatest/" + fileName)) {
          Files.copy(is, classPath.resolve(fileName));
        }
      }
      repo.runBlueCovAndTests();
      try (final InputStream is = UnitTestEnvironment.class.getResourceAsStream(
          "/java-test/expected-coverage" + Extensions.RESULT_JSON)) {
        Assert.assertEquals(
            PropertyHelper.toSortedGoals(CharStreams.toString(new InputStreamReader(is))),
            PropertyHelper.getCoverageReport());
      }
    }
  }
}
