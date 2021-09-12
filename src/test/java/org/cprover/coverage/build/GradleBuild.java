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
package org.cprover.coverage.build;

import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.cprover.coverage.helper.MavenHelper;

/**
 * Implements {@link IBuild} for Gradle projects.
 */
public class GradleBuild implements IBuild {

  private static final String GRADLE_BUILD_FILE_NAME = "build.gradle";
  private static final String MAVEN_CENTRAL_REPO = "mavenCentral()";
  private static final String NEW_REPO_FORMAT = "mavenCentral()\n maven '{' \n url \"{0}\" \n '}'";
  private static final String DEPENDENCIES = "\n\ndependencies {";
  private static final String NEW_DEPENDENCIES = "\n\ndependencies {\n    compile group: 'org.cprover.test_gen', name: 'bluecov', version: '0.1'";
  private Path workingDirectory;

  /*
   * (non-Javadoc)
   *
   * @see IBuild#compile()
   */
  @Override
  public void compile() throws BuildException {
    exec("installApp");
  }

  private void exec(final String command) throws BuildException {
    final Process process;
    try {
      process = new ProcessBuilder(workingDirectory.resolve("gradlew.bat").toString(),
          command).directory(workingDirectory.toFile()).start();
    } catch (IOException e) {
      throw new BuildException(e);
    }
    try {
      if (0 != process.waitFor()) {
        throw new IOException();
      }
    } catch (final InterruptedException | IOException e) {
      try (final InputStream is = process.getInputStream(); final InputStream es = process.getErrorStream()) {
        final StringBuilder message = new StringBuilder();
        CharStreams.copy(new InputStreamReader(is), message);
        CharStreams.copy(new InputStreamReader(es), message);
        throw new IOException(message.toString(), e);
      } catch (final IOException e2) {
        throw new BuildException(e2);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see IBuild#addDependencyToThisProject()
   */
  @Override
  public void addDependencyToThisProject() throws BuildException {
    try {
      final Path gradleBuild = workingDirectory.resolve(GRADLE_BUILD_FILE_NAME);
      final String tempRepositoryPath = MavenHelper.installThisProjectIntoTempRepository(
          workingDirectory);
      final String newContent = new String(Files.readAllBytes(gradleBuild)).replace(
              MAVEN_CENTRAL_REPO, MessageFormat.format(NEW_REPO_FORMAT, tempRepositoryPath))
          .replace(DEPENDENCIES, NEW_DEPENDENCIES);
      Files.write(gradleBuild, newContent.getBytes());
    } catch (final IOException | MavenInvocationException e) {
      throw new BuildException(e);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see IBuild#runTests()
   */
  @Override
  public void runTests() throws BuildException {
    exec("test");
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * IBuild#setWorkingDirectory(java.nio.file.Path)
   */
  @Override
  public IBuild setWorkingDirectory(final Path workingDirectory) {
    this.workingDirectory = workingDirectory.toAbsolutePath();
    return this;
  }
}
