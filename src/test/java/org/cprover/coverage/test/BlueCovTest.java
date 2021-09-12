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

import java.lang.reflect.Method;
import org.cprover.coverage.helper.EnvironmentHelper;
import org.cprover.coverage.helper.PropertyHelper;
import org.junit.Assert;
import org.junit.Test;

public class BlueCovTest {

  @Test
  public void testSyntheticTestCase1Method2() throws Exception {
    try (final UnitTestEnvironment env = new UnitTestEnvironment("SyntheticTestCase1")) {
      final Method method = env.getTargetClass()
          .getDeclaredMethod("function", int.class, int.class, int.class, double.class,
              double.class);
      method.invoke(null, 1, 0, 0, 0.0, 0.0);
      EnvironmentHelper.cleanupCoverageLog();
      Assert.assertEquals(PropertyHelper.toSortedGoals(
              UnitTestEnvironment.getExpectedResult("SyntheticTestCase-method2")),
          PropertyHelper.getCoverageReport());
    }
  }

  @Test
  public void testSyntheticTestCase1Method2DefaultCase() throws Exception {
    try (final UnitTestEnvironment env = new UnitTestEnvironment("SyntheticTestCase1")) {
      final Method method = env.getTargetClass()
          .getDeclaredMethod("function", int.class, int.class, int.class, double.class,
              double.class);
      method.invoke(null, 1, 0, 0, 8.0, 3.0);
      EnvironmentHelper.cleanupCoverageLog();
      Assert.assertEquals(PropertyHelper.toSortedGoals(
              UnitTestEnvironment.getExpectedResult("SyntheticTestCase-method2-default-case")),
          PropertyHelper.getCoverageReport());
    }
  }

  @Test
  public void testSyntheticCommonSuperclass() throws Exception {
    try (final UnitTestEnvironment env = new UnitTestEnvironment(
        "SyntheticCommonSuperclassTestCase", "SyntheticCommonSuperclassTestCase$A",
        "SyntheticCommonSuperclassTestCase$B")) {
      final Method method = env.getTargetClass().getDeclaredMethod("f", boolean.class);
      method.invoke(null, false);
      EnvironmentHelper.cleanupCoverageLog();
      Assert.assertEquals(PropertyHelper.toSortedGoals(
              UnitTestEnvironment.getExpectedResult("SyntheticCommonSuperclassTestCase")),
          PropertyHelper.getCoverageReport());
    }
  }
}
