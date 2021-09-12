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

import java.io.IOException;
import java.nio.file.Path;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.cprover.coverage.helper.MavenHelper;

/**
 * Implements {@link IBuild} using {@link MavenHelper}.
 */
public class MavenBuild implements IBuild {

  private final String relativePomFilePath;
  private Path workingDirectory;

  /**
   * Configures a Maven build with the given POM path.
   *
   * @param relativePomFilePath The relative path of the POM within the {@link
   *                            IBuild#setWorkingDirectory(Path) working directory}.
   */
  public MavenBuild(final String relativePomFilePath) {
    this.relativePomFilePath = relativePomFilePath;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.cprover.coverage.build.IBuildEnvironment#compile()
   */
  @Override
  public void compile() throws BuildException {
    try {
      MavenHelper.compile(getPom());
    } catch (final MavenInvocationException e) {
      throw new BuildException(e);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.cprover.coverage.build.IBuildEnvironment#addDependencyToThisProject()
   */
  @Override
  public void addDependencyToThisProject() throws BuildException {
    try {
      MavenHelper.addDepdendencyToThisProject(getPom());
    } catch (final IOException | MavenInvocationException e) {
      throw new BuildException(e);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.cprover.coverage.build.IBuildEnvironment#runTests()
   */
  @Override
  public void runTests() throws BuildException {
    try {
      MavenHelper.test(getPom());
    } catch (final MavenInvocationException e) {
      throw new BuildException(e);
    }
  }

  private Path getPom() {
    return workingDirectory.resolve(relativePomFilePath);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.cprover.coverage.build.IBuildEnvironment#setWorkingDirectory(java.nio.
   * file.Path)
   */
  @Override
  public MavenBuild setWorkingDirectory(final Path workingDirectory) {
    this.workingDirectory = workingDirectory;
    return this;
  }
}
