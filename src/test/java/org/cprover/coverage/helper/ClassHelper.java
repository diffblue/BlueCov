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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Helper for .class file related operations.
 */
public final class ClassHelper {

  private ClassHelper() {
  }

  /**
   * Loads a {@link Class} from a given {@link Path class file}, rather than from the class path
   * using the default {@link ClassLoader}.
   *
   * @param binaryName The {@link String binary name} of a {@link Class} is its name and package,
   *                   e.g.:
   *                   <code>"org.cprover.coverage.test.ClassHelper"</code>.
   * @param path       {@link Path} to a file from which to load the {@link Class}.
   * @return The {@link Class} contained in {@link Path path}.
   * @throws IOException if an I/O error occurs
   */
  public static Class<?> loadClass(final String binaryName, final Path path) throws IOException {
    return new ClassLoader() {
      public Class<?> loadClass(final Path path) throws IOException {
        final byte[] content = Files.readAllBytes(path);
        return defineClass(binaryName, content, 0, content.length);
      }
    }.loadClass(path);
  }
}
