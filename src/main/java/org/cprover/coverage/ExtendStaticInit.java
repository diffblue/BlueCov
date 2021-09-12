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
import org.objectweb.asm.Opcodes;

/**
 * Describe class <code>ExtendStaticInit</code> here.
 */
public class ExtendStaticInit extends InstrumentByteCode {

  /**
   * Minimum stack size necessary for instrumented static initialisers.
   */
  private static final int MIN_STACK = 1;
  /**
   * <code>className</code> is the name of the class.
   */
  private String className;

  /**
   * Creates a new <code>ExtendStaticInit</code> instance.
   *
   * @param mv               a <code>MethodVisitor</code> value
   * @param name             name of class as <code>String</code> value
   * @param methodName       name of method as <code>String</code> value
   * @param offsetIdMap      map from offsets to UIDs as <code>HashMap</code>
   * @param instrumentedLocs the list of locations to instrument as
   *                         <code>List<Integer></code>
   */
  public ExtendStaticInit(
      final MethodVisitor mv,
      final String name,
      final String methodName,
      final HashMap<Integer, Integer> offsetIdMap,
      final List<Integer> instrumentedLocs) {
    super(mv, name, methodName, offsetIdMap, instrumentedLocs);
    this.className = name;
  }

  /**
   * <code>putLoggerInstance</code> adds the bytecode instructions for
   * instrumentation.
   *
   * @param mv        a <code>MethodVisitor</code> value
   * @param className name of the class as <code>String</code>
   */
  static void putLoggerInstance(
      final MethodVisitor mv,
      final String className) {
    mv.visitMethodInsn(Opcodes.INVOKESTATIC,
        "org/cprover/coverage/CoverageLog",
        "getInstance",
        "()Lorg/cprover/coverage/CoverageLog;",
        false);
    mv.visitFieldInsn(Opcodes.PUTSTATIC, className,
        "diffblue_coverage_reporter",
        "Lorg/cprover/coverage/CoverageLog;");
  }

  @Override
  /**
   * <code>visitCode</code> instruments &lt;clinit&gt; at its entry point to set
   * up the <code>CoverageLoc</code> instance to support &lt;clinit&gt;
   * coverage, too
   *
   */
  public final void visitCode() {
    putLoggerInstance(mv, this.className);
    super.visitCode();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.objectweb.asm.MethodVisitor#visitMaxs(int, int)
   */
  @Override
  public final void visitMaxs(final int maxStack, final int maxLocals) {
    super.visitMaxs(Math.max(MIN_STACK, maxStack), maxLocals);
  }
}
