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
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * <code>FieldAdapter</code> is the main entry point for the instrumentation
 * via the visitor pattern using by <code>ASM</code>. It extends
 * <code>ClassVisitor</code>, adds the required static fields and creates the
 * Method visitors to instrument the code.
 */
public class FieldAdapter extends ClassVisitor {

  /**
   * <code>fAcc</code> is field access flags.
   */
  private int fAcc;

  /**
   * <code>fName</code> is field name.
   */
  private String fName;

  /**
   * <code>fDesc</code> is field descriptor.
   */
  private String fDesc;

  /**
   * <code>isInstrumented</code> flag showing whether a class has already been
   * instrumented (seen via presence of <code>CoverageLog</code> field).
   */
  private boolean isInstrumented = false;

  /**
   * <code>hasStaticInit</code> signals whether class has static initialization.
   */
  private boolean hasStaticInit = false;

  /**
   * <code>className</code> name of class.
   */
  private String className;

  /**
   * <code>offsetIdMap</code> map from offsets to UIDs.
   */
  private HashMap<Integer, Integer> offsetIdMap;

  /**
   * <code>instrumentedLocs</code> list of locations to instrument.
   */
  private List<Integer> instrumentedLocs;

  /**
   * Creates a new <code>FieldAdapter</code> instance.
   *
   * @param cv                    a <code>ClassVisitor</code> value
   * @param fieldAcc              ACCESS bits as <code>int</code> value
   * @param fieldName             name of method as <code>String</code> value
   * @param fieldDesc             type of method as <code>String</code> value
   * @param name                  name of class as <code>String</code> value
   * @param offsetUIDMap          <code>HashMap</code> from offsets to UIDs
   * @param instrumentedLocations <code>List</code> of locationst to instrument
   */
  public FieldAdapter(
      final ClassVisitor cv,
      final int fieldAcc,
      final String fieldName,
      final String fieldDesc,
      final String name,
      final HashMap<Integer, Integer> offsetUIDMap,
      final List<Integer> instrumentedLocations) {
    super(Opcodes.ASM5, cv);
    this.fAcc = fieldAcc;
    this.fName = fieldName;
    this.fDesc = fieldDesc;
    this.className = name;
    this.offsetIdMap = offsetUIDMap;
    this.instrumentedLocs = instrumentedLocations;
  }

  @Override
  /**
   * <code>visitField</code> signals whether the selected field is already
   * present.
   *
   * @param access ACCESS bits as <code>int</code>
   * @param name name of field to add as <code>String</code> value
   * @param desc type of field as Java <code>String</code> encoding
   * @param signature type of field in Java <code>String</code> encoding if
   * required for generics
   * @param value value of the field to set
   * @return a <code>FieldVisitor</code> value
   */
  public final FieldVisitor visitField(
      final int access,
      final String name,
      final String desc,
      final String signature,
      final Object value) {
    if (name.equals(fName)) {
      isInstrumented = true;
    }
    return cv.visitField(access, name, desc, signature, value);
  }

  @Override
  /**
   * <code>visitEnd</code> crates &lt;clinit&gt; such that the static field for
   * <code>CoverageLog</code> gets initialized.
   *
   */
  public final void visitEnd() {
    if (!isInstrumented) {
      FieldVisitor fv = cv.visitField(fAcc, fName, fDesc, null, null);
      if (fv != null) {
        fv.visitEnd();
      }
      // create `<clinit>` if it does not exist to set up logger
      if (!hasStaticInit) {
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_STATIC, "<clinit>",
            "()V", null, null);
        mv.visitCode();
        ExtendStaticInit.putLoggerInstance(mv, this.className);
        mv.visitInsn(Opcodes.RETURN);

        // args should be ignored by recomputation
        mv.visitMaxs(1, 0);
        mv.visitEnd();
        System.out.println("did not find <clinit> in class, added one");
      }
    }
    cv.visitEnd();
  }

  @Override
  /**
   * <code>visitMethod</code> chooses which Method visitor to use, depending on
   * whether it is a normal method or &lt;clinit&gt; which must be extended with
   * the initialization of <code>CoverageLog</code> at its entry point
   *
   * @param access ACCESS bits of method as <code>int</code>
   * @param name method name as <code>String</code> value
   * @param desc method type as Java <code>String</code> encoding
   * @param signature generic method type as <code>String</code> encoding if
   * required
   * @param exception list of exception names that the method may throw
   * <code>String</code>
   * @return a <code>MethodVisitor</code> value
   */
  public final MethodVisitor visitMethod(
      final int access,
      final String name,
      final String desc,
      final String signature,
      final String[] exception) {
    MethodVisitor mv =
        super.visitMethod(access, name, desc, signature, exception);
    if (!isInstrumented) {
      if (name.equals("<clinit>")) {
        hasStaticInit = true;
        mv = new ExtendStaticInit(
            mv, this.className, name + ":" + desc,
            offsetIdMap, instrumentedLocs);
      } else if (mv != null) {
        mv = new InstrumentByteCodeVisitor(
            mv, this.className, name + ":" + desc,
            offsetIdMap, instrumentedLocs);
      }
    }
    return mv;
  }

  /**
   * <code>isInstrumented</code> signals whether method was already instrumented
   * before.
   *
   * @return a <code>boolean</code> value
   */
  public final boolean isInstrumented() {
    return isInstrumented;
  }
}
