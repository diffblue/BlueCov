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

import java.nio.file.Path;

/**
 * Implementing classes implement build tasks for regression test projects.
 */
public interface IBuild {

  /**
   * Compiles a configured project.
   */
  public void compile() throws BuildException;

  /**
   * Adds this project, including the coverage report helper, to the built project's class path.
   */
  public void addDependencyToThisProject() throws BuildException;

  /**
   * Runs the regression test project's tests. This to generate coverage.
   */
  public void runTests() throws BuildException;

  /**
   * Sets the location where the regression test project is stored.
   *
   * @param workingDirectory {@link Path Location} where the regression test project is stored.
   * @return <code>this</code>
   */
  public IBuild setWorkingDirectory(final Path workingDirectory);
}
