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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.cprover.coverage.BlueCov;
import org.cprover.coverage.helper.ClassHelper;

/**
 * Helper class to set up a class path in which to run {@link BlueCov} for unit tests.
 */
public class UnitTestEnvironment implements AutoCloseable {

  private static final String TEST_DIRECTORY_SUFFIX = "org/cprover/coverage/benchmarks";
  private static final String JSON_RESOURCE_PREFIX = "/bluecov/";
  private static final String CLASS_RESOURCE_PREFIX = "/org/cprover/coverage/benchmarks/";
  private static final String BENCHMARKS_PACKAGE_PREFIX = "org.cprover.coverage.benchmarks.";
  private final BlueCovEnvironment blueCovEnvironment = new BlueCovEnvironment();
  private final String[] benchmarkClasses;
  private final String benchmarkName;
  private Path targetClassFilePath;

  /**
   * Use this constructor in a try-with-resources statement to set up a unit test class path for
   * {@link BlueCov}. Creates all resources in the user's temporary directory and deletes them on
   * {@link AutoCloseable#close()}.
   *
   * @param benchmarkClasses The first {@link String name} should be the main benchmark class to
   *                         load and run methods on. All additional {@link String elements} in the
   *                         array mark additional classes to copy into the test directory.
   */
  public UnitTestEnvironment(final String... benchmarkClasses)
      throws IOException, ReflectiveOperationException {
    this.benchmarkClasses = benchmarkClasses;
    benchmarkName = benchmarkClasses[0];
    try {
      copyBenchmarkClassPath();
      blueCovEnvironment.runBlueCov(Arrays.asList(targetClassFilePath.toAbsolutePath().toString()));
    } catch (final IOException | ReflectiveOperationException | RuntimeException e) {
      close();
      throw e;
    }
  }

  /**
   * Helper operation for unit tests which loads a JSON coverage report test resource.
   *
   * @param resultName The {@link String name} of the JSON result resource to load, without suffix.
   * @return The {@link String content} of the result resource.
   */
  public static String getExpectedResult(final String resultName) throws IOException {
    final String resource = JSON_RESOURCE_PREFIX + resultName + Extensions.RESULT_JSON;
    try (final InputStream is = UnitTestEnvironment.class.getResourceAsStream(resource)) {
      return CharStreams.toString(new InputStreamReader(is));
    }
  }

  private void copyBenchmarkClassPath() throws IOException {
    final Path targetPackagePath = blueCovEnvironment.getTempDirectory()
        .resolve(TEST_DIRECTORY_SUFFIX);
    Files.createDirectories(targetPackagePath);
    for (final String benchmarkClass : benchmarkClasses) {
      try (final InputStream is = BlueCovTest.class.getResource(
          CLASS_RESOURCE_PREFIX + benchmarkClass + Extensions.CLASS).openStream()) {
        Files.copy(is, targetPackagePath.resolve(benchmarkClass + Extensions.CLASS));
      }
    }
    try (final InputStream is = BlueCovTest.class.getResource(
        JSON_RESOURCE_PREFIX + benchmarkName + Extensions.JSON_PROPERTIES).openStream()) {
      Files.copy(is, targetPackagePath.resolve(benchmarkName + Extensions.JSON_PROPERTIES));
    }
    targetClassFilePath = targetPackagePath.resolve(benchmarkName + Extensions.CLASS);
  }

  /**
   * Provides the instrumented main class which can be used to run methods with specific inputs and
   * measure coverage changes in the report.
   *
   * @return A {@link Class} loaded from the class path created in the user's temporary directory.
   */
  public Class<?> getTargetClass() throws IOException {
    return ClassHelper.loadClass(BENCHMARKS_PACKAGE_PREFIX + benchmarkName, targetClassFilePath);
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() throws IOException, ReflectiveOperationException {
    blueCovEnvironment.close();
  }
}
