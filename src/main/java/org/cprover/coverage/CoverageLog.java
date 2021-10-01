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
import java.util.Map;
import java.util.Set;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

/**
 * <code>CoverageLog</code> provides access to the persistent data structures
 * used for coverage analysis. This class can't be reliably considered to be a singleton. Multiple
 * instances can be created if there are multiple Threads, JVMs or classloaders used in the tests.
 */
public final class CoverageLog extends Thread {

  /**
   * <code>DB_ENV_VAR</code> is the name of the environment variable that
   * hold the path to the coverage database.
   */
  public static final String DB_ENV_VAR = "BLUECOV_DB";
  /**
   * MapDB has a bug in closing its file handles for which they provide a "hack" method {@link
   * DBMaker.Maker#cleanerHackEnable()}. This is only useful in environments where the VM doesn't
   * shutdown immediately after closing the database.
   */
  public static final String DB_USE_CLEANER_HACK = "DB_USE_CLEANER_HACK";
  /**
   * <code>CoverageLog</code> singleton <code>instance</code>.
   */
  private static CoverageLog instance;
  /**
   * <code>fileName</code> of the database file.
   */
  private static String fileName = "blueCov.db";
  /**
   * <code>locCountMap</code> maps locations to counts.
   */
  private String locCountMap = "locCountMap";
  /**
   * <code>locationMap</code> maps UID to locations.
   */
  private String locationMap = "locationMap";
  /**
   * <code>descriptionMap</code> maps description to UID.
   */
  private String descriptionMap = "descriptionMap";
  /**
   * <code>jbmcNameMap</code> maps UID to JBMC internal name.
   */
  private String jbmcNameMap = "nameMap";
  /**
   * Indicates that all {@link DB} objects should use {@link DBMaker.Maker#cleanerHackEnable()}.
   */
  private boolean useMapDbCleanerHack;
  /**
   * <code>lineNumberMap</code> map UID to integer array of line numbers.
   */
  private String lineNumberMap = "lineMap";
  /**
   * <code>db</code> object holding the persistent data structures.
   */
  private DB db;
  /**
   * <code>countMap</code> maps UID to counts.
   */
  private HTreeMap<Integer, Integer> countMap; // UID -> count
  /**
   * <code>locMap</code> maps UID to description.
   */
  private HTreeMap<Integer, String> locMap;    // UID -> description
  /**
   * <code>descMap</code> maps description back to UID.
   */
  private HTreeMap<String, Integer> descMap;   // description -> UID
  /**
   * <code>nameMap</code> maps UID to JBMC internal names.
   */
  private HTreeMap<Integer, String> nameMap;   // UID -> JBMC name
  /**
   * <code>lineMap</code> maps UID to array of lines.
   */
  private HTreeMap<Integer, int[]> lineMap;    // UID -> array of covered lines
  /**
   * <code>id</code> current UID counter.
   */
  private int id;
  /**
   * <code>shouldReport</code> describes whether report will be emitted at end
   * of run.
   */
  private boolean shouldReport = false;
  /**
   * <code>inMemoryMap</code> is the temporary in-memory mapping.
   */
  private HashMap<Integer, Integer> inMemoryMap;
  /**
   * <code>inMemory</code> signals whether DB should be written at the end and
   * be kept in memory while running.
   */
  private boolean inMemory = false;

  /**
   * private <code>CoverageLog</code> constructor.
   */
  private CoverageLog() {
    Map<String, String> env = System.getenv();
    if (env.containsKey(DB_ENV_VAR)) {
      fileName = env.get(DB_ENV_VAR);
    }
    if (env.containsKey(DB_USE_CLEANER_HACK)) {
      useMapDbCleanerHack = Boolean.parseBoolean(env.get(DB_USE_CLEANER_HACK));
    }
    db = makeDb();
    countMap = db.hashMap(locCountMap)
        .keySerializer(Serializer.INTEGER)
        .valueSerializer(Serializer.INTEGER)
        .createOrOpen();
    locMap = db.hashMap(locationMap)
        .keySerializer(Serializer.INTEGER)
        .valueSerializer(Serializer.STRING)
        .createOrOpen();
    descMap = db.hashMap(descriptionMap)
        .keySerializer(Serializer.STRING)
        .valueSerializer(Serializer.INTEGER)
        .createOrOpen();
    nameMap = db.hashMap(jbmcNameMap)
        .keySerializer(Serializer.INTEGER)
        .valueSerializer(Serializer.STRING)
        .createOrOpen();
    lineMap = db.hashMap(lineNumberMap)
        .keySerializer(Serializer.INTEGER)
        .valueSerializer(Serializer.INT_ARRAY)
        .createOrOpen();
    this.id = locMap.size();
    Runtime.getRuntime().addShutdownHook(this);
  }

  /**
   * <code>getDbFileName</code> returns the default name of the db file.
   *
   * @return a <code>String</code> value
   */
  public static String getDbFileName() {
    return fileName;
  }

  /**
   * <code>getInstance</code> returns the singleton instance of
   * <code>CoverageLog</code>.
   *
   * @return a <code>CoverageLog</code> value
   */
  public static CoverageLog getInstance() {
    return getInstance(true);
  }

  /**
   * <code>getInstance</code> returns the singleton instance of
   * <code>CoverageLog</code>.
   *
   * @param inMemory a <code>boolean</code> indicating whether DB should be closed after read,
   *                 in-memory store should be used and written to the DB at shutdown
   * @return the <code>CoverageLog</code> instance
   */
  public static CoverageLog getInstance(final boolean inMemory) {
    if (instance == null) {
      instance = new CoverageLog();
      if (inMemory) {
        instance.inMemory = inMemory;
        instance.inMemoryMap = new HashMap<>();
        @SuppressWarnings("unchecked")
        Set<Integer> keys = instance.countMap.keySet();
        for (Integer key : keys) {
          instance.inMemoryMap.put(key, 0);
        }
        instance.db.close();
      }
    }
    return instance;
  }

  /**
   * Creates a {@link DB} configured according to the {@link System#getenv() system environment}
   * configuration.
   *
   * @return A pre-configured {@link DB}.
   */
  private DB makeDb() {
    DBMaker.Maker maker = DBMaker
        .fileDB(fileName)
        .cleanerHackEnable()
        .fileMmapEnable()
        .fileMmapEnableIfSupported();
    if (useMapDbCleanerHack) {
      maker = maker.cleanerHackEnable();
    }
    // We need to wait in case other instances are using the DB.
    maker.fileLockWait();
    return maker.make();
  }

  /**
   * <code>record</code> is called to update the coverage of a basic block in
   * the DB.
   *
   * @param key UID of the basic block as <code>int</code>
   */
  public void record(final int key) {
    Integer i = inMemoryMap.get(key);
    if (i == null) {
      inMemoryMap.put(key, 1);
    } else {
      inMemoryMap.put(key, i + 1);
    }
  }

  /**
   * <code>register</code> basic block in database.
   *
   * @param key         UID <code>int</code> value
   * @param jbmcName    name of property used by JBMC <code>String</code>
   * @param className   name of class as <code>String</code>
   * @param methodName  name of method + signature as <code>String</code>
   * @param bcLoc       bytecode offset of basic block as <code>int</code>
   * @param lineNumbers lines covered by this property as <code>int[]</code>
   */
  public void register(
      final int key,
      final String jbmcName,
      final String className,
      final String methodName,
      final int bcLoc,
      final int[] lineNumbers) {
    String desc = getBasicBlockID(className, methodName, bcLoc);
    if (!locMap.containsKey(key)) {
      locMap.put(key, desc);
      descMap.put(desc, key);
      nameMap.put(key, jbmcName);
      lineMap.put(key, lineNumbers);
      id++;
    }
  }

  /**
   * <code>report</code> creates a short report about the coverage numbers of
   * the basic blocks stored in the DB.
   */
  public void report() {
    report(true);
  }

  /**
   * <code>report</code> creates a short report about the coverage of the basic
   * blocks stored in the DB.
   *
   * @param reportUncovered a <code>boolean</code> indicating whether uncovered blocks should be
   *                        reported
   */
  public void report(final boolean reportUncovered) {
    Set<?> keys = locMap.keySet();
    for (Object key : keys) {
      if (countMap.containsKey(key)) {
        if (reportUncovered || countMap.get(key) > 0) {
          System.out.println(locMap.get(key) + " "
              + countMap.get(key).toString() + " times");
        }
      } else if (reportUncovered) {
        System.out.println(locMap.get(key) + " is uncovered");
      }
    }
  }

  /**
   * <code>getHitCounts</code> returns mapping of descriptions to hitcounts.
   *
   * @return hashmap
   */
  public HashMap<String, Integer> getHitCounts() {
    HashMap<String, Integer> descCountMap = new HashMap<>();
    Set<?> keys = locMap.keySet();
    for (Object key : keys) {
      if (countMap.containsKey(key)) {
        descCountMap.put(nameMap.get(key), countMap.get(key));
      } else {
        descCountMap.put(nameMap.get(key), 0);
      }
    }
    return descCountMap;
  }


  /**
   * <code>getLinesForblock</code> returns mapping of descriptions to line
   * numbers.
   *
   * @return hashmap
   */
  public HashMap<String, int[]> getLinesForBlock() {
    HashMap<String, int[]> descLineMap = new HashMap<>();
    Set<?> keys = locMap.keySet();
    for (Object key : keys) {
      descLineMap.put(nameMap.get(key), lineMap.get(key));
    }
    return descLineMap;
  }

  /**
   * <code>setReport</code> sets whether there should be a report at shutdown.
   *
   * @param toggleReport a <code>boolean</code> value
   */
  public void setReport(final boolean toggleReport) {
    this.shouldReport = toggleReport;
  }

  @Override
  /**
   * The purpose of <code>run</code> is to serve as shutdown hook to properly
   * close the DB.
   *
   */
  public void run() {
    if (inMemory) {
      db = makeDb();
      countMap = db.hashMap(locCountMap)
          .keySerializer(Serializer.INTEGER)
          .valueSerializer(Serializer.INTEGER)
          .createOrOpen();
      for (Integer key : inMemoryMap.keySet()) {
        Integer orig = countMap.get(key);
        if (orig == null) {
          orig = 0;
        }
        countMap.put(key, orig + inMemoryMap.get(key));
      }
    }
    if (shouldReport) {
      System.out.println("reporting");
      boolean reportUncovered = false;
      report(reportUncovered);
      System.out.println("closing DB");
    }
    db.close();
  }

  /**
   * <code>getBasicBlockID</code> creates an unique String identifying each
   * basic block.
   *
   * @param className  a <code>String</code> value
   * @param methodName a <code>String</code> value
   * @param bcLoc      an <code>int</code> value
   * @return a <code>String</code> representation of unique basic block name
   */
  public String getBasicBlockID(
      final String className,
      final String methodName,
      final int bcLoc) {
    return (className + "." + methodName + "@" + bcLoc);
  }

  /**
   * <code>getCoverageHash</code> creates a hashcode of a basic block.
   *
   * @param className  a <code>String</code> value
   * @param methodName a <code>String</code> value
   * @param bcLoc      an <code>int</code> value
   * @return hashcode of basic block as <code>int</code>
   */
  public int getCoverageHash(
      final String className,
      final String methodName,
      final int bcLoc) {
    return getBasicBlockID(className, methodName, bcLoc).hashCode();
  }

  /**
   * <code>getCoverageUID</code> provides the UID associated with the location
   * in <code>desc</code>.
   *
   * @param desc <code>String</code> value representing the intrumented basic
   *             block
   * @return an <code>int</code> UID for code instrumenting
   */
  public int getCoverageUID(final String desc) {
    if (descMap.containsKey(desc)) {
      return descMap.get(desc);
    } else {
      return id;
    }
  }

  /**
   * <code>resetCoverage</code> resets all counts in table to zero.
   */
  public void resetCoverage() {
    for (Integer key : inMemoryMap.keySet()) {
      inMemoryMap.put(key, 0);
    }
  }
}
