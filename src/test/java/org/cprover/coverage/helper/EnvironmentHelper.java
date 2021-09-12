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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import org.cprover.coverage.CoverageLog;

/**
 * Manipulates components of the coverage instrumenter's runtime environment, which is useful for
 * unit test configuration. These components include {@link System#getenv() system environment
 * variables}, {@link Runtime#addShutdownHook(Thread) shutdown hooks} and the {@link CoverageLog}
 * singleton.
 */
public final class EnvironmentHelper {

  private EnvironmentHelper() {
  }

  private static Object processEnvironmentValueOf(final String className, final String value)
      throws ReflectiveOperationException {
    final Method valueOf = Class.forName(className).getDeclaredMethod("valueOf", String.class);
    valueOf.setAccessible(true);
    return valueOf.invoke(null, value);
  }

  /**
   * Changes an environment variable.
   *
   * @param key   {@link String name} of the variable.
   * @param value Desired {@link String value}.
   */
  public static void put(final String key, final String value) throws ReflectiveOperationException {
    try {
      final Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
      final Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
      theEnvironmentField.setAccessible(true);
      @SuppressWarnings("unchecked") final Map<Object, Object> env = (Map<Object, Object>) theEnvironmentField.get(
          null);
      final Object explicitKey = processEnvironmentValueOf("java.lang.ProcessEnvironment$Variable",
          key);
      final Object explicitValue = processEnvironmentValueOf("java.lang.ProcessEnvironment$Value",
          value);
      env.put(explicitKey, explicitValue);
    } catch (final ClassNotFoundException e) {
      final Class<?>[] classes = Collections.class.getDeclaredClasses();
      final Map<String, String> env = System.getenv();
      for (final Class<?> cl : classes) {
        if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
          final Field field = cl.getDeclaredField("m");
          field.setAccessible(true);
          final Object obj = field.get(env);
          @SuppressWarnings("unchecked") final Map<String, String> map = (Map<String, String>) obj;
          map.put(key, value);
        }
      }
    }
  }

  private static void resetCoverageLogInstance() throws ReflectiveOperationException {
    final Field instance = CoverageLog.class.getDeclaredField("instance");
    instance.setAccessible(true);
    instance.set(null, null);
  }

  /**
   * Executes and removes all {@link Runtime#addShutdownHook(Thread) shutdown hooks} and resets the
   * singleton {@link CoverageLog} instance to
   * <code>null</code>
   *
   * @throws ReflectiveOperationException if any error occurs accessing in the java.lang.reflect
   *                                      package.
   */
  public static void cleanupCoverageLog() throws ReflectiveOperationException {
    final Field field = Class.forName("java.lang.ApplicationShutdownHooks")
        .getDeclaredField("hooks");
    field.setAccessible(true);
    final Map<?, ?> hooks = (Map<?, ?>) field.get(null);
    for (Object hook : hooks.keySet()) {
      ((Runnable) hook).run();
    }
    hooks.clear();
    resetCoverageLogInstance();
  }
}
