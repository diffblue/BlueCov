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

import java.util.HashMap;
import java.util.List;
import org.objectweb.asm.MethodVisitor;

/**
 * <code>InstrumentByteCodeVisitor</code> implements `visitCode` for
 * <code>InstrumentByteCode</code>.
 */
public final class InstrumentByteCodeVisitor extends InstrumentByteCode {

  /**
   * Creates a new <code>InstrumentByteCodeVisitor</code> instance.
   *
   * @param mv                    a <code>MethodVisitor</code> value
   * @param name                  <code>String</code> name of class
   * @param mName                 <code>String</code> name of method
   * @param offsetUIDMap          <code>HashMap</code> from offsets to UIDs
   * @param instrumentedLocations <code>List</code> of locations to instrument
   */
  public InstrumentByteCodeVisitor(
      final MethodVisitor mv,
      final String name,
      final String mName,
      final HashMap<Integer, Integer> offsetUIDMap,
      final List<Integer> instrumentedLocations) {
    super(mv, name, mName, offsetUIDMap, instrumentedLocations);
  }

  @Override
  public void visitCode() {
    debug("method " + getMethodName());
    super.visitCode();
  }
}
