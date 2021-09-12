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
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * <code>InstrumentByteCode</code> adds <code>record</code> calls to
 * bytecode.
 */
public class InstrumentByteCode extends MethodVisitor {

  /**
   * Since we don't know where in the method our instrumentation is added, we need to add an offset
   * to the maximum stack size. Our instrumentation loads the CoverageLog singleton instance and the
   * location integer constant before invoking "record", adding 2 elements to the stack.
   */
  private static final int WORST_CASE_STACK_OFFSET = 2;
  /**
   * <code>debug</code> toggles debug output.
   */
  private final boolean debug = false;
  /**
   * <code>className</code> is the name of the class.
   */
  private String className;
  /**
   * <code>methodName</code> is the name of the method.
   */
  private String methodName;
  /**
   * <code>offetsetIdMap</code> map from offsets to UIDs.
   */
  private HashMap<Integer, Integer> offsetIdMap;

  /**
   * <code>instrumentedLocs</code> list of instrumented locations.
   */
  private List<Integer> instrumentedLocs;

  /**
   * Indicates whether the visited method was instrumented. This indicates that we need to adapt
   * calls to {@link MethodVisitor#visitMaxs(int, int)}.
   */
  private boolean lastMethodWasInstrumented;
  /**
   * <code>bcLine</code> is the bytecode offset.
   */
  private int bcLine = 0;

  /**
   * Creates a new <code>InstrumentByteCode</code> instance.
   *
   * @param mv                    a <code>MethodVisitor</code> value
   * @param name                  <code>String</code> name of class
   * @param mName                 <code>String</code> name of method
   * @param offsetUIDMap          <code>HashMap</code> from offsets to UIDs
   * @param instrumentedLocations <code>List</code> of locations to instrument
   */
  public InstrumentByteCode(
      final MethodVisitor mv,
      final String name,
      final String mName,
      final HashMap<Integer, Integer> offsetUIDMap,
      final List<Integer> instrumentedLocations) {
    super(Opcodes.ASM5, mv);
    this.className = name;
    this.methodName = mName;
    this.offsetIdMap = offsetUIDMap;
    this.instrumentedLocs = instrumentedLocations;
  }

  /**
   * <code>getMethodName</code> returns method name.
   *
   * @return a <code>String</code> value
   */
  protected final String getMethodName() {
    return methodName;
  }

  /**
   * <code>debug</code> output.
   *
   * @param msg a <code>String</code> value
   */
  final void debug(final String msg) {
    if (debug) {
      System.out.println(msg);
    }
  }

  @Override
  public final void visitInsn(final int opcode) {
    debug("ins @ " + bcLine);
    instrumentByteCode(bcLine);
    bcLine += 1;
    super.visitInsn(opcode);
  }

  @Override
  public final void visitIntInsn(final int opcode, final int operand) {
    debug("int @ " + bcLine);
    instrumentByteCode(bcLine);
    bcLine += 1;
    super.visitIntInsn(opcode, operand);
  }

  @Override
  public final void visitVarInsn(final int opcode, final int var) {
    debug("var @ " + bcLine);
    instrumentByteCode(bcLine);
    bcLine += 1;
    super.visitVarInsn(opcode, var);
  }

  @Override
  public final void visitTypeInsn(final int opcode, final String desc) {
    debug("type @ " + bcLine);
    instrumentByteCode(bcLine);
    bcLine += 1;
    super.visitTypeInsn(opcode, desc);
  }

  @Override
  public final void visitFieldInsn(
      final int opcode,
      final String owner,
      final String name,
      final String desc) {
    debug("field @ " + bcLine);
    instrumentByteCode(bcLine);
    bcLine += 1;
    super.visitFieldInsn(opcode, owner, name, desc);
  }

  @Override
  public final void visitMethodInsn(
      final int opcode,
      final String owner,
      final String name,
      final String desc,
      final boolean itf) {
    debug("invk @ " + bcLine);
    instrumentByteCode(bcLine);
    bcLine += 1;
    super.visitMethodInsn(opcode, owner, name, desc, itf);
  }

  @Override
  public final void visitInvokeDynamicInsn(
      final String name,
      final String desc,
      final Handle bsm,
      final Object... bsmArgs) {
    debug("invkdyn @ " + bcLine);
    instrumentByteCode(bcLine);
    bcLine += 1;
    super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
  }

  @Override
  public final void visitJumpInsn(final int opcode, final Label label) {
    debug("jump @ " + bcLine);
    instrumentByteCode(bcLine);
    bcLine += 1;
    super.visitJumpInsn(opcode, label);
  }

  @Override
  public final void visitLdcInsn(final Object cst) {
    debug("ldc @ " + bcLine);
    instrumentByteCode(bcLine);
    bcLine += 1;
    super.visitLdcInsn(cst);
  }

  @Override
  public final void visitIincInsn(final int opcode, final int increment) {
    debug("iinc @ " + bcLine);
    instrumentByteCode(bcLine);
    bcLine += 1;
    super.visitIincInsn(opcode, increment);
  }

  @Override
  public final void visitTableSwitchInsn(
      final int min,
      final int max,
      final Label dflt,
      final Label... labels) {
    debug("table switch @ " + bcLine);
    instrumentByteCode(bcLine);
    bcLine += 1;
    super.visitTableSwitchInsn(min, max, dflt, labels);
  }

  @Override
  public final void visitLookupSwitchInsn(
      final Label dflt,
      final int[] keys,
      final Label[] labels) {
    debug("lookup switch @ " + bcLine);
    instrumentByteCode(bcLine);
    bcLine += 1;
    super.visitLookupSwitchInsn(dflt, keys, labels);
  }

  @Override
  public final void visitMultiANewArrayInsn(final String desc, final int dims) {
    debug("multianewarray @ " + bcLine);
    instrumentByteCode(bcLine);
    bcLine += 1;
    super.visitMultiANewArrayInsn(desc, dims);
  }


  /**
   * <code>shouldBeInstrumented</code> signals whether bytecode index should be
   * instrumented.
   *
   * @param bcIndex an <code>int</code> value
   * @return a <code>boolean</code> value
   */
  final boolean shouldBeInstrumented(final int bcIndex) {
    return offsetIdMap.containsKey(
        CoverageLog.getInstance(false)
            .getCoverageHash(className, methodName, bcIndex));
  }

  /**
   * <code>getUniqueIdentifier</code> returns UID of byecode index.
   *
   * @param bcIndex an <code>int</code> value
   * @return <code>int</code> UID value
   */
  final int getUniqueIdentifier(final int bcIndex) {
    return offsetIdMap.get(
        CoverageLog.getInstance(false)
            .getCoverageHash(className, methodName, bcIndex));
  }

  /**
   * <code>instrumentByteCode</code> adds call to <code>CoverageLog</code> in
   * bytecode.
   *
   * @param bcIndex an <code>int</code> value
   */
  final void instrumentByteCode(final int bcIndex) {
    if (shouldBeInstrumented(bcLine)) {
      lastMethodWasInstrumented = true;
      // get instance from static field
      // push value to record
      // call `record` on CoverageLog
      super.visitFieldInsn(Opcodes.GETSTATIC, this.className,
          "diffblue_coverage_reporter",
          "Lorg/cprover/coverage/CoverageLog;");
      super.visitLdcInsn(getUniqueIdentifier(bcLine));
      super.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
          "org/cprover/coverage/CoverageLog",
          "record",
          "(I)V",
          false);
      debug("added ID " + getUniqueIdentifier(bcLine));
      instrumentedLocs.add(getUniqueIdentifier(bcLine));
    }
  }

  /* (non-Javadoc)
   * @see org.objectweb.asm.MethodVisitor#visitMaxs(int, int)
   */
  @Override
  public void visitMaxs(final int maxStack, final int maxLocals) {
    if (lastMethodWasInstrumented) {
      super.visitMaxs(maxStack + WORST_CASE_STACK_OFFSET, maxLocals);
    } else {
      super.visitMaxs(maxStack, maxLocals);
    }
  }

  @Override
  public final void visitEnd() {
    debug("----------\n");
    lastMethodWasInstrumented = false;
    super.visitCode();
  }
}
