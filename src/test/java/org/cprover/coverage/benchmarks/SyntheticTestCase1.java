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
package org.cprover.coverage.benchmarks;

public class SyntheticTestCase1 {

  public static int STATIC_FIELD = 1;
  public double field = 2.0;

  public static int method1(int x, int y, int z) {
    return STATIC_FIELD + x + y + z;
  }

  public static void function(int i1, int i2, int i3, double x, double y) {
    SyntheticTestCase1 obj = new SyntheticTestCase1();
    if (i1 > 0) {
      obj.method2(x, y);
    } else {
      obj.method2(y, x);
    }
    if (i2 > 0) {
      method1(i1, i2, i3);
    }
  }

  public double method2(double x, double y) {
    field += x + y;
    if (field > 10.0) {
      return 0.0;
    }
    return field;
  }
}
