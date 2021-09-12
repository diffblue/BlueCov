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
package org.cprover.coverage;

import com.diffblue.deeptestutils.Reflector;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.rules.ExpectedException;

public class InstrumentByteCodeTest {

  @org.junit.Rule
  public ExpectedException thrown = ExpectedException.none();

  @org.junit.Test
  public void getMethodNameTest()
      throws Throwable {

    String retval;
    {
      /* Arrange */
      InstrumentByteCode instrumentByteCode = (InstrumentByteCode) Reflector.getInstance(
          "org.cprover.coverage.InstrumentByteCode");
      Reflector.setField(instrumentByteCode, "mv", null);
      Reflector.setField(instrumentByteCode, "debug", false);
      Reflector.setField(instrumentByteCode, "instrumentedLocs", null);
      Reflector.setField(instrumentByteCode, "lastMethodWasInstrumented", false);
      Reflector.setField(instrumentByteCode, "methodName", null);
      Reflector.setField(instrumentByteCode, "bcLine", 0);
      Reflector.setField(instrumentByteCode, "className", "");
      Reflector.setField(instrumentByteCode, "offsetIdMap", null);

      /* Act */
      Class<?> c = Reflector.forName("org.cprover.coverage.InstrumentByteCode");
      Method m = c.getDeclaredMethod("getMethodName");
      m.setAccessible(true);
      retval = (String) m.invoke(instrumentByteCode);
    }
    {
      /* Assert result */
      Assert.assertEquals(null, retval);
    }
  }
}
