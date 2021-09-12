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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import org.cprover.coverage.BlueCov;
import org.cprover.coverage.CoverageLog;
import org.cprover.coverage.helper.EnvironmentHelper;

/**
 * Environment for unit tests which need to run {@link BlueCov}.
 */
public class BlueCovEnvironment implements AutoCloseable {

  private static final String TEST_DIRECTORY_PREFIX = "bluecov-test";
  private static final String CLASS_LIST_FILE_NAME = "classes.txt";
  private static final String DB_NAME = "blueCov.db";
  private final Path tempDirectory;

  /**
   * Creates a temporary directory in which to run {@link BlueCov}.
   *
   * @throws IOException if creation of the temporary directory fails.
   */
  public BlueCovEnvironment() throws IOException {
    tempDirectory = Files.createTempDirectory(TEST_DIRECTORY_PREFIX);
  }

  /**
   * Instruments the given classes using {@link BlueCov}.
   *
   * @param classFiles The classes to instrument.
   * @throws IOException                  if any I/O error occurs creating {@link BlueCov}'s classes
   *                                      file.
   * @throws ReflectiveOperationException if any reflective operation for {@link BlueCov} (e.g
   *                                      changing {@link System#getenv()}) fails.
   */
  public void runBlueCov(final Iterable<String> classFiles)
      throws IOException, ReflectiveOperationException {
    final Path classListFile = tempDirectory.resolve(CLASS_LIST_FILE_NAME);
    Files.write(classListFile, classFiles);
    final Path coverageDb = tempDirectory.resolve(DB_NAME);
    EnvironmentHelper.put(CoverageLog.DB_ENV_VAR, coverageDb.toAbsolutePath().toString());
    EnvironmentHelper.put(CoverageLog.DB_USE_CLEANER_HACK, Boolean.TRUE.toString());
    BlueCov.main(new String[]{classListFile.toString()});
    EnvironmentHelper.cleanupCoverageLog();
  }

  /**
   * Provides the temporary directory in which {@link BlueCov} will be run.
   *
   * @return Directory in the user's temporary folder.
   */
  public Path getTempDirectory() {
    return tempDirectory;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() throws IOException, ReflectiveOperationException {
    EnvironmentHelper.cleanupCoverageLog();
    Files.walk(tempDirectory).sorted(Comparator.reverseOrder()).map(Path::toFile)
        .forEach(File::delete);
  }
}
