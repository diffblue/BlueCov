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
import org.junit.Assert;
import org.junit.rules.ExpectedException;

public class FieldAdapterTest {

  @org.junit.Rule
  public ExpectedException thrown = ExpectedException.none();

  @org.junit.Test
  public void isInstrumentedTest()
      throws Throwable {

    boolean retval;
    {
      /* Arrange */
      FieldAdapter fieldAdapter = (FieldAdapter) Reflector.getInstance(
          "org.cprover.coverage.FieldAdapter");
      Reflector.setField(fieldAdapter, "isInstrumented", false);
      Reflector.setField(fieldAdapter, "hasStaticInit", false);
      Reflector.setField(fieldAdapter, "cv", null);
      Reflector.setField(fieldAdapter, "instrumentedLocs", null);
      Reflector.setField(fieldAdapter, "fName", "");
      Reflector.setField(fieldAdapter, "fAcc", 0);
      Reflector.setField(fieldAdapter, "offsetIdMap", null);
      Reflector.setField(fieldAdapter, "className", "");
      Reflector.setField(fieldAdapter, "fDesc", "");

      /* Act */
      retval = fieldAdapter.isInstrumented();
    }
    {
      /* Assert result */
      Assert.assertEquals(false, retval);
    }
  }

  @org.junit.Test
  public void visitFieldTest2()
      throws Throwable {

    org.objectweb.asm.FieldVisitor retval;
    {
      /* Setup mocks */
      final java.util.ArrayList<org.objectweb.asm.FieldVisitor> org_objectweb_asm_ClassVisitor_visitField_int_String_String_String_Object_answer_list = new java.util.ArrayList<org.objectweb.asm.FieldVisitor>();
      final java.util.ArrayList<Object[]> org_objectweb_asm_ClassVisitor_visitField_int_String_String_String_Object_expectation_list = new java.util.ArrayList<Object[]>();
      final com.diffblue.deeptestutils.IterAnswer org_objectweb_asm_ClassVisitor_visitField_int_String_String_String_Object_answer_object = new com.diffblue.deeptestutils.IterAnswer<org.objectweb.asm.FieldVisitor>(
          "org.objectweb.asm.ClassVisitor", "visitField",
          org_objectweb_asm_ClassVisitor_visitField_int_String_String_String_Object_answer_list,
          org_objectweb_asm_ClassVisitor_visitField_int_String_String_String_Object_expectation_list);
      org_objectweb_asm_ClassVisitor_visitField_int_String_String_String_Object_answer_list.add(
          null);
      java.util.ArrayList<org.objectweb.asm.ClassVisitor> org_objectweb_asm_ClassVisitor_instances = new java.util.ArrayList<org.objectweb.asm.ClassVisitor>();

      /* Arrange */
      FieldAdapter fieldAdapter = (FieldAdapter) Reflector.getInstance(
          "org.cprover.coverage.FieldAdapter");
      Reflector.setField(fieldAdapter, "isInstrumented", false);
      Reflector.setField(fieldAdapter, "hasStaticInit", false);
      org.objectweb.asm.ClassVisitor classVisitor = (org.objectweb.asm.ClassVisitor) org.mockito.Mockito.mock(
          org.objectweb.asm.ClassVisitor.class);
      Reflector.setField(classVisitor, "cv", null);
      Reflector.setField(fieldAdapter, "cv", classVisitor);
      Reflector.setField(fieldAdapter, "instrumentedLocs", null);
      Reflector.setField(fieldAdapter, "fName", "?????????");
      Reflector.setField(fieldAdapter, "fAcc", 0);
      Reflector.setField(fieldAdapter, "offsetIdMap", null);
      Reflector.setField(fieldAdapter, "className", "");
      Reflector.setField(fieldAdapter, "fDesc", "");
      org_objectweb_asm_ClassVisitor_instances.add(classVisitor);
      int access = 0;
      String name = "?????????";
      String desc = "";
      String signature = "??????";
      Object value = (Object) Reflector.getInstance("java.lang.Object");

      for (org.objectweb.asm.ClassVisitor org_objectweb_asm_ClassVisitor_iter : org_objectweb_asm_ClassVisitor_instances) {
        org.mockito.Mockito.when(
                org_objectweb_asm_ClassVisitor_iter.visitField(org.mockito.Matchers.anyInt(),
                    org.mockito.Matchers.isA(String.class), org.mockito.Matchers.isA(String.class),
                    org.mockito.Matchers.isA(String.class), org.mockito.Matchers.isA(Object.class)))
            .thenAnswer(
                org_objectweb_asm_ClassVisitor_visitField_int_String_String_String_Object_answer_object);
      }

      /* Act */
      retval = fieldAdapter.visitField(access, name, desc, signature, value);

      /* Assert side effects */
      Assert.assertEquals(true, Reflector.getInstanceField(fieldAdapter, "isInstrumented"));
    }
    {
      /* Assert result */
      Assert.assertEquals(null, retval);
    }
  }

  @org.junit.Test
  public void visitFieldTest3()
      throws Throwable {

    org.objectweb.asm.FieldVisitor retval;
    {
      /* Setup mocks */
      final java.util.ArrayList<org.objectweb.asm.FieldVisitor> org_objectweb_asm_ClassVisitor_visitField_int_String_String_String_Object_answer_list = new java.util.ArrayList<org.objectweb.asm.FieldVisitor>();
      final java.util.ArrayList<Object[]> org_objectweb_asm_ClassVisitor_visitField_int_String_String_String_Object_expectation_list = new java.util.ArrayList<Object[]>();
      final com.diffblue.deeptestutils.IterAnswer org_objectweb_asm_ClassVisitor_visitField_int_String_String_String_Object_answer_object = new com.diffblue.deeptestutils.IterAnswer<org.objectweb.asm.FieldVisitor>(
          "org.objectweb.asm.ClassVisitor", "visitField",
          org_objectweb_asm_ClassVisitor_visitField_int_String_String_String_Object_answer_list,
          org_objectweb_asm_ClassVisitor_visitField_int_String_String_String_Object_expectation_list);
      org_objectweb_asm_ClassVisitor_visitField_int_String_String_String_Object_answer_list.add(
          null);
      java.util.ArrayList<org.objectweb.asm.ClassVisitor> org_objectweb_asm_ClassVisitor_instances = new java.util.ArrayList<org.objectweb.asm.ClassVisitor>();

      /* Arrange */
      FieldAdapter fieldAdapter = (FieldAdapter) Reflector.getInstance(
          "org.cprover.coverage.FieldAdapter");
      Reflector.setField(fieldAdapter, "isInstrumented", true);
      Reflector.setField(fieldAdapter, "hasStaticInit", false);
      org.objectweb.asm.ClassVisitor classVisitor = (org.objectweb.asm.ClassVisitor) org.mockito.Mockito.mock(
          org.objectweb.asm.ClassVisitor.class);
      Reflector.setField(classVisitor, "cv", null);
      Reflector.setField(fieldAdapter, "cv", classVisitor);
      Reflector.setField(fieldAdapter, "instrumentedLocs", null);
      Reflector.setField(fieldAdapter, "fName", ",,,,,,");
      Reflector.setField(fieldAdapter, "fAcc", 0);
      Reflector.setField(fieldAdapter, "offsetIdMap", null);
      Reflector.setField(fieldAdapter, "className", "");
      Reflector.setField(fieldAdapter, "fDesc", "");
      org_objectweb_asm_ClassVisitor_instances.add(classVisitor);
      int access = 0;
      String name = "\\\\\\\\\\\\^";
      String desc = "";
      String signature = "!!!!!!!!";
      Object value = (Object) Reflector.getInstance("java.lang.Object");

      for (org.objectweb.asm.ClassVisitor org_objectweb_asm_ClassVisitor_iter : org_objectweb_asm_ClassVisitor_instances) {
        org.mockito.Mockito.when(
                org_objectweb_asm_ClassVisitor_iter.visitField(org.mockito.Matchers.anyInt(),
                    org.mockito.Matchers.isA(String.class), org.mockito.Matchers.isA(String.class),
                    org.mockito.Matchers.isA(String.class), org.mockito.Matchers.isA(Object.class)))
            .thenAnswer(
                org_objectweb_asm_ClassVisitor_visitField_int_String_String_String_Object_answer_object);
      }

      /* Act */
      retval = fieldAdapter.visitField(access, name, desc, signature, value);
    }
    {
      /* Assert result */
      Assert.assertEquals(null, retval);
    }
  }
}
