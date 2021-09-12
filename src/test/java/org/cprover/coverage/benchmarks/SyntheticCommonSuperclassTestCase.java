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

/**
 * This benchmark provokes a jump target which depends on a virtual method whose implementing type
 * is not known statically. This is enough to trigger the common super type calculation in ASM when
 * using COMPUTE_FRAMES.
 */
public class SyntheticCommonSuperclassTestCase {

  public static void f(boolean input) {
    A a;
    if (input) {
      a = new A();
    } else {
      a = new B();
    }

    int result = 0;
    if (a.f() > 0) {
      result += 10;
    } else {
      result += 5;
    }

    System.out.print(result);
  }

  public static class A {

    public int f() {
      return 0;
    }
  }

  public static class B extends A {

    public int f() {
      return 1;
    }
  }
}
