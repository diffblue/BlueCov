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

import static org.cprover.coverage.CoverageUtils.BYTECODE_INDEX;
import static org.cprover.coverage.CoverageUtils.JAVA_NS_PREFIX_LENGTH;
import static org.cprover.coverage.CoverageUtils.SOURCE_LOCATION;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

/**
 * Describe class <code>CoverageInstrument</code> here.
 */
public final class CoverageInstrument {

  /**
   * Entry point of <code>CoverageInstrument</code>.
   *
   * @param args takes input class file, output class file and properties JSON file as arguments
   * @throws IOException if an error occurs
   */
  public static void main(final String[] args) throws IOException {
    if (args.length > 2) {
      try (FileInputStream fs = new FileInputStream(args[2])) {
        Map<String, String> env = System.getenv();
        if (!env.containsKey(CoverageLog.DB_ENV_VAR)) {
          System.out.println("WARNING: " + CoverageLog.DB_ENV_VAR
              + " is not set "
              + " falling back to " + CoverageLog.getDbFileName()
              + " as database");
        }
        JsonReader jsonReader = Json.createReader(fs);
        JsonArray json = jsonReader.readArray();
        CoverageInstrument cov = new CoverageInstrument();
        cov.instrumentClassFile(args[0], args[1], json);
      }
    }
  }

  /**
   * <code>getBytesFromFile</code> reads class file into bye array.
   *
   * @param fileName as a <code>String</code> value
   * @return the <code>byte[]</code> containing the code
   * @throws IOException if an error occurs
   */
  byte[] getBytesFromFile(final String fileName) throws IOException {
    Path path = Paths.get(fileName);
    byte[] data = Files.readAllBytes(path);
    return data;
  }

  /**
   * <code>writeBytesToFile</code> writes byte array to class file.
   *
   * @param fileName as a <code>String</code> value
   * @param data     the <code>byte</code> array holding the code
   * @throws IOException if an error occurs
   */
  void writeBytesToFile(final String fileName, final byte[] data)
      throws IOException {
    String sep = FileSystems.getDefault().getSeparator();
    int directoryIndex = fileName.lastIndexOf(sep);
    if (directoryIndex != -1) {
      String dirName = fileName.substring(0, directoryIndex);
      System.out.println("create directory " + dirName);
      Files.createDirectories(Paths.get(dirName));
    }
    Path path = Paths.get(fileName);
    Files.write(path, data);
  }

  /**
   * <code>instrumentClassFile</code> adds the bytecode instrumentation required
   * for coverage analysis to a class file.
   *
   * @param inFileName  a <code>String</code> value
   * @param outFileName a <code>String</code> value
   * @param json        a <code>JsonArray</code> value representing the output of
   *                    <code>JBMC</code> called with <code>--json-ui --show-properties</code>
   * @throws IOException if an error occurs in reading or writing the class files
   */
  public void instrumentClassFile(
      final String inFileName,
      final String outFileName,
      final JsonArray json) throws IOException {
    byte[] classData = getBytesFromFile(inFileName);
    ClassReader cr = new ClassReader(classData);

    String className = cr.getClassName();
    HashMap<Integer, Integer> offsetIdMap = new HashMap<>();
    CoverageLog logger = CoverageLog.getInstance(false);
    logger.setReport(false);

    JsonArray properties = null;
    for (int i = 0; i < json.size(); i++) {
      JsonObject object = json.getJsonObject(i);
      if (object.containsKey("properties")) {
        properties = object.getJsonArray("properties");
        break;
      }
    }

    if (properties == null) {
      System.out.println("ERROR: no properties found for " + className);
      return;
    }

    // do not instrument interfaces, just copy class file
    int accessFlags = cr.getAccess();
    if ((accessFlags & Opcodes.ACC_INTERFACE) != 0) {
      writeBytesToFile(outFileName, classData);
      return;
    }

    for (int i = 0; i < properties.size(); i++) {
      JsonObject entry = properties.getJsonObject(i);
      String cbmcName = entry.getString("name");

      if (entry.containsKey(SOURCE_LOCATION)) {
        JsonObject jSourceLoc = entry.getJsonObject(SOURCE_LOCATION);
        if (!(jSourceLoc.containsKey("file")
            && jSourceLoc.containsKey("line")
            && jSourceLoc.containsKey("function"))) {
          System.out.println(
              "WARNING: does not contain full source location info\n"
                  + entry + "\n------------------");
        }
        ArrayList<Integer> lineNumbers = new ArrayList<>();
        if (entry.containsKey("coveredLines")) {
          String[] coveredLines = entry.getString("coveredLines").split(",");
          for (int j = 0; j < coveredLines.length; j++) {
            String[] range = coveredLines[j].split("-");
            if (range.length == 2) { // found range
              addRange(
                  Integer.parseInt(range[0]),
                  Integer.parseInt(range[1]),
                  lineNumbers);
            } else {
              lineNumbers.add(Integer.parseInt(range[0]));
            }
          }
        } else {
          System.out.println(
              "WARNING: does not contain line coverage information\n" + entry);
          lineNumbers.add(0);
        }
        if (jSourceLoc.containsKey(BYTECODE_INDEX)
            && jSourceLoc.containsKey("function")) {
          // method+signature to uniquely identify java function
          int bcLine = Integer.parseInt(jSourceLoc.getString(BYTECODE_INDEX));

          String method = jSourceLoc.getString("function");
          // remove prefix consisting of "java::" FQN+'.'
          method = method.substring(JAVA_NS_PREFIX_LENGTH);
          if (!method.startsWith(className.replace("/", "."))) {
            continue;
          }
          method = method.substring(className.length() + 1);
          // check for subclass
          if (method.contains(".")) {
            continue;
          }
          int hashCode = logger.getCoverageHash(className, method, bcLine);
          if (!offsetIdMap.containsKey(hashCode)) {
            int uid = logger.getCoverageUID(
                logger.getBasicBlockID(className, method, bcLine));
            offsetIdMap.put(hashCode, uid);
            int[] lineNums = new int[lineNumbers.size()];
            if (lineNumbers.size() == 0) {
              System.out.println("ERROR: no line numbers for " + cbmcName);
            }
            for (int k = 0; k < lineNums.length; k++) {
              lineNums[k] = (Integer) lineNumbers.get(k);
            }
            logger
                .register(uid, cbmcName, className, method, bcLine, lineNums);
            System.out
                .println("register ID " + uid + " "
                    + logger.getBasicBlockID(className, method, bcLine));
          }
        } else {
          System.out.println(
              "WARNING: bytecode index or function information is missing in\n"
                  + entry);
        }
      }
    }

    // record which locations have been instrumented
    ArrayList<Integer> instrumentedIDs = new ArrayList<>();

    // Let classwriter compute local var and operand stack sizes.
    // We provide an implementation for calculating the changes
    // introduced by our instrumentation, which is faster but more
    // conservative than ClassWriter.COMPUTE_MAXS.
    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    ClassVisitor cv = new ClassVisitor(Opcodes.ASM5, cw) {
    };

    // FieldAdapter is a ClassVisitor that adds the logger as a static field to
    // the class
    // It then creates a MethodVisitor InstrumentByteCode that uses either
    //   * InstrumentByteCode to add coverage instrumentation, or
    //   * ExtendStaticInit to also add CoverageLog creation in clinit
    FieldAdapter fa =
        new FieldAdapter(cv, Opcodes.ACC_STATIC, "diffblue_coverage_reporter",
            "Lorg/cprover/coverage/CoverageLog;",
            cr.getClassName(), offsetIdMap, instrumentedIDs);
    cr.accept(fa, 0);

    byte[] filteredClassData = cw.toByteArray();
    writeBytesToFile(outFileName, filteredClassData);

    if (!fa.isInstrumented()) {
      Collection<Integer> ids = offsetIdMap.values();
      for (Integer id : instrumentedIDs) {
        ids.remove(id);
      }
      for (Integer id : ids) {
        System.out.println("ERROR: didn't instrument ID " + id);
      }
    } else {
      System.out.println("WARNING: file "
          + inFileName + " was already instrumented");
    }
  }

  /**
   * <code>addRange</code> adds number range to lineNumbers <code>int[]</code>.
   *
   * @param min         an <code>int</code> value
   * @param max         an <code>int</code> value
   * @param lineNumbers an ArrayList of <code>Integer</code> line numbers
   */
  private void addRange(
      final int min,
      final int max,
      final ArrayList<Integer> lineNumbers) {
    for (int k = min; k <= max; k++) {
      lineNumbers.add(k);
    }
  }
}
