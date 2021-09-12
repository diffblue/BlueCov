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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import org.cprover.coverage.build.BuildException;
import org.cprover.coverage.build.IBuild;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Environment to run GIT repository-based regression tests.
 */
public class RepositoryEnvironment implements AutoCloseable {

  private final BlueCovEnvironment blueCovEnvironment = new BlueCovEnvironment();
  private final IBuild build;

  /**
   * Creates a temporary directory, clones the given repository into it and compiles the project
   * with a dependency to our coverage log.
   *
   * @param repo  The {@link String repository} to clone.
   * @param build The {@link IBuild build tool} to use.
   * @throws BuildException               if any {@link IBuild} operation fails.
   * @throws GitAPIException              if any {@link Git} operation fails.
   * @throws IOException                  if creating the temporary directory fails.
   * @throws ReflectiveOperationException if cleaning up the BlueCov environment in case of a
   *                                      previous error fails.
   */
  public RepositoryEnvironment(final String repo, final IBuild build)
      throws BuildException, GitAPIException, IOException, ReflectiveOperationException {
    final Path workingDirectory = getWorkingDirectory();
    this.build = build.setWorkingDirectory(workingDirectory);
    try {
      Git.cloneRepository().setURI(repo).setDirectory(workingDirectory.toFile()).call().close();
      build.addDependencyToThisProject();
      build.compile();
    } catch (final GitAPIException | BuildException | RuntimeException e) {
      close();
      throw e;
    }
  }

  /**
   * Instruments the project's class files, then runs the project's tests to generate coverage.
   *
   * @throws IOException                  if the project's class files could not be identified.
   * @throws BuildException               if running the project's tests fails.
   * @throws ReflectiveOperationException if the BlueCov helper throws any {@link
   *                                      ReflectiveOperationException}.
   */
  public void runBlueCovAndTests()
      throws IOException, BuildException, ReflectiveOperationException {
    final Iterable<String> classesWithProperties = Files.walk(getWorkingDirectory())
        .map(Path::toAbsolutePath).map(Path::toString)
        .filter(p -> p.endsWith(Extensions.JSON_PROPERTIES))
        .map(s -> s.replace(Extensions.JSON_PROPERTIES, Extensions.CLASS))
        .collect(Collectors.toList());
    blueCovEnvironment.runBlueCov(classesWithProperties);
    build.runTests();
  }

  /**
   * Provides the {@link Path working directory} in which the repository will be loaded.
   *
   * @return Repository {@link Path working directory}.
   */
  public Path getWorkingDirectory() {
    return blueCovEnvironment.getTempDirectory();
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
