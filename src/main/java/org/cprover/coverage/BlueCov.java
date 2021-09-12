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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;

/**
 * <code>BlueCov</code> is the main class for bytecode instrumentation.
 */
public final class BlueCov {

  /**
   * <code>main</code> method for command line usage.
   *
   * @param args a <code>String</code> value holding the file name of the list of class files
   */
  public static void main(final String[] args) {
    if (args.length >= 1) {
      Map<String, String> env = System.getenv();
      if (!env.containsKey(CoverageLog.DB_ENV_VAR)) {
        System.out.println("WARNING: " + CoverageLog.DB_ENV_VAR + " is not set "
            + " falling back to " + CoverageLog.getDbFileName()
            + " as database");
      }
      new BlueCov().doIt(args[0]);
    } else {
      System.out.println("BlueCov $LIST_OF_CLASS_FILES");
      System.out.println("        for each .class, we assume an existing"
          + ".class.json that contains the output of");
      System.out.println("        JBMC --show-properties --json-u"
          + "i $OTHER_OPTIONS ...  $CLASS_FILE");
    }
  }

  /**
   * <code>doIt</code> is the main entry point for bytecode instrumentation.
   *
   * @param classFileList the name of the file that holds the list of class files to instrument
   *                      <code>String</code>
   */
  void doIt(final String classFileList) {
    CoverageInstrument cov = new CoverageInstrument();
    try (InputStream f = new FileInputStream(classFileList);) {
      InputStreamReader ir = new InputStreamReader(f, Charset.defaultCharset());
      BufferedReader br = new BufferedReader(ir);

      String classFileName;

      while ((classFileName = br.readLine()) != null) {
        try (InputStream fs = new FileInputStream(classFileName + ".json")) {
          JsonReader jsonReader = Json.createReader(fs);
          JsonArray json = jsonReader.readArray();

          cov.instrumentClassFile(classFileName, classFileName, json);
        } catch (FileNotFoundException fnfe) {
          System.err.println(
              "ERROR: file '" + classFileName + ".json' not found");
        } catch (IOException ioe) {
          System.err.println("ERROR: " + ioe.getMessage());
        }
      }
    } catch (FileNotFoundException fnfe) {
      System.err.println("ERROR: could not find " + classFileList);
    } catch (IOException ioe) {
      System.err.println(
          "ERROR: could not read " + classFileList + "\n" + ioe.getMessage());
    }
  }
}
