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

import org.cprover.coverage.helper.EnvironmentHelper;
import org.cprover.coverage.helper.PropertyHelper;
import org.junit.Assert;
import org.junit.Test;

public class TextSearchEngineTest {

  @Test
  public void testTextSearchEngine() throws Exception {
    try (final UnitTestEnvironment env = new UnitTestEnvironment("ExprToken", "ExprToken$Type")) {
      final Class<?> targetClass = env.getTargetClass();
      final Class<?> typeClass = targetClass.getDeclaredClasses()[0];
      @SuppressWarnings({"unchecked", "rawtypes"}) final Object type = Enum.valueOf(
          (Class<Enum>) typeClass, "Bracket");
      final Object expr = targetClass.getDeclaredConstructors()[0].newInstance(type, '{');
      targetClass.getDeclaredMethod("GetBracket").invoke(expr);
      EnvironmentHelper.cleanupCoverageLog();
      final String expected = UnitTestEnvironment.getExpectedResult("ExprToken");
      final String comparable = PropertyHelper.toSortedGoals(expected);
      Assert.assertEquals(comparable, PropertyHelper.getCoverageReport());
    }
  }
}
