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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Stream;

/**
 * Helper class to load resources in bulk from the class path.
 */
public final class ResourceHelper {

  private static final String FILE_SUFFIX_SEPARATOR = ".";
  private static final char RESOURCE_SEPARATOR = '/';

  private ResourceHelper() {
  }

  /**
   * Lists all file resources under the given {@link String path}.
   *
   * @param path Root {@link String path} to search for file resources.
   * @return All file resources beneath the {@link String root path}.
   */
  public static Stream<String> getChildResources(final String path) {
    final BufferedReader br = new BufferedReader(
        new InputStreamReader(ResourceHelper.class.getResourceAsStream(path)));
    return br.lines().flatMap(childPath -> {
      final String fullChildPath = path + RESOURCE_SEPARATOR + childPath;
      if (childPath.contains(FILE_SUFFIX_SEPARATOR)) {
        return Stream.of(fullChildPath);
      }
      return getChildResources(fullChildPath);
    });
  }
}
