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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Properties;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.MavenInvocationException;

public final class MavenHelper {

  private static final String DEPENDENCIES = "<dependencies>";
  private static final String REPOSITORIES = "<repositories>";
  private static final String PROJECT_END = "</project>";
  private static final String EXISTING_DEPENDENCIES_FORMAT = "<dependencies>{0}";
  private static final String EXISTING_REPOSITORIES_FORMAT = "<repositories>{0}";
  private static final String DEPENDENCIES_FORMAT = "<dependencies>{0}</dependencies></project>";
  private static final String REPOSITORIES_FORMAT = "<repositories>{0}</repositories></project>";
  private static final String DEPENDENCY = "<dependency><groupId>org.cprover.coverage</groupId><artifactId>bluecov</artifactId><version>0.1</version></dependency>";
  private static final String REPOSITORY_FORMAT = "<repository><id>java-coverage-measurement</id><url>file://{0}</url></repository>";
  private static final String THIS_PROJECT_POM_NAME = "pom.xml";
  private static final String COMPILE_GOAL = "compile";
  private static final String TEST_GOAL = "test";
  private static final String WINDOWS_PATH_COMPONENT_SEPARATOR = "\\";
  private static final String GENERIC_PATH_COMPONENT_SEPARATOR = "/";

  private MavenHelper() {
  }

  /**
   * @param pom
   * @throws MavenInvocationException
   */
  public static void compile(final Path pom) throws MavenInvocationException {
    invoke(pom, COMPILE_GOAL);
  }

  /**
   * @param pom
   * @throws MavenInvocationException
   */
  public static void test(final Path pom) throws MavenInvocationException {
    invoke(pom, TEST_GOAL);
  }

  /**
   * @param pom
   * @throws IOException
   */
  public static void addDepdendencyToThisProject(final Path pom)
      throws IOException, MavenInvocationException {
    String pomContent = new String(Files.readAllBytes(pom));
    if (pomContent.contains(DEPENDENCIES)) {
      pomContent = pomContent.replaceFirst(DEPENDENCIES,
          MessageFormat.format(EXISTING_DEPENDENCIES_FORMAT, DEPENDENCY));
    } else {
      pomContent = pomContent.replaceFirst(PROJECT_END,
          MessageFormat.format(DEPENDENCIES_FORMAT, DEPENDENCY));
    }
    final String tempRepositoryPath = installThisProjectIntoTempRepository(pom.getParent());
    final String repository = MessageFormat.format(REPOSITORY_FORMAT, tempRepositoryPath);
    if (pomContent.contains(REPOSITORIES)) {
      pomContent = pomContent.replaceFirst(REPOSITORIES,
          MessageFormat.format(EXISTING_REPOSITORIES_FORMAT, repository));
    } else {
      pomContent = pomContent.replaceFirst(PROJECT_END,
          MessageFormat.format(REPOSITORIES_FORMAT, repository));
    }
    Files.write(pom, pomContent.getBytes());
  }

  /**
   * @param parent
   * @return
   * @throws IOException
   * @throws MavenInvocationException
   */
  public static String installThisProjectIntoTempRepository(final Path parent)
      throws IOException, MavenInvocationException {
    final Path tempRepository = parent.resolve("temp-repository").toAbsolutePath();
    Files.createDirectory(tempRepository);
    final File testClassesPath = new File(
        MavenHelper.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    final Path thisProjectPom = testClassesPath.toPath().getParent().getParent()
        .resolve(THIS_PROJECT_POM_NAME).toAbsolutePath();
    final Properties properties = new Properties();
    final String tempRepositoryPath = tempRepository.toString();
    properties.setProperty("maven.repo.local", tempRepositoryPath);
    properties.setProperty("skipTests", Boolean.TRUE.toString());
    invoke(thisProjectPom, "install", properties);
    return tempRepositoryPath.replace(WINDOWS_PATH_COMPONENT_SEPARATOR,
        GENERIC_PATH_COMPONENT_SEPARATOR);
  }

  private static void invoke(final Path pom, final String goal) throws MavenInvocationException {
    invoke(pom, goal, null);
  }

  private static void invoke(final Path pom, final String goal, final Properties properties)
      throws MavenInvocationException {
    final InvocationRequest request = new DefaultInvocationRequest();
    request.setPomFile(pom.toFile());
    request.setGoals(Arrays.asList(goal));
    request.setProperties(properties);
    request.setBatchMode(true);
    new DefaultInvoker().execute(request);
  }
}
