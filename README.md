# BlueCov

This tool provides Java bytecode instrumentation based on the property information provided by JBMC.
In particular, every property that has a complete source location information is instrumented in
such a way that execution of the instrumented code is detected and the number of executions of the
corresponding code is stored in a persistent database.

## Building BlueCov

### Compiling

The BlueCov coverage measurement tool is a maven project, it can be built via

```bash
mvn compile
```

You will also need a classpath in a file `cp.txt` to run, this is done via:

```bash
mvn dependency:build-classpath -Dmdep.outputFile=cp.txt
```

### Running the tests

Before running the tests, you must set `M2_HOME` environment variable to the Maven installation
directory. This is because the tests use Maven to compile some example projects.

```bash
export M2_HOME=<path-to-maven-installation>
```

E.g. for Ubuntu:

```bash
export M2_HOME=/usr/share/maven
```

You can find where your Maven install directory is by running:

```bash
mvn -version | grep 'Maven home'
```

Tests are then run in the normal way:

```bash
mvn test
```

It is also possible to specify the `${home.maven}` system property
via `mvn -Dhome.maven=$PATH_TO_MAVEN` as an alternative to setting the environment variable.

## Using BlueCov

### Preparation

The instrumentation is based on information provided by JBMC via the `--show-properties` command.
For example:

```bash
jbmc --show-properties --json-ui org/cprover/A.class > org/cprover/A.class.json
```

will create a file `A.class.json` in `JSON` format inside org/cprover that contains the necessary
information.

You will need a file that contains a list of classes:

```
org.cprovoer.A.class
```

It will assume there exists A.class.json in the same location as the class file.

The following instructions assume this is in `classes.txt`.

### Run Tool

```bash
java -cp $(cat cp.txt):bluecov-0.1-jar-with-dependencies.jar org.cprover.coverage.BlueCov classes.txt
```

Note: ``(cat cp.txt)`` is pulling in the class path you made in compiling. The jar needs to the be
the one you've built. This needs to be run from the root of the compiled project (i.e. from within
target/classes for maven projects). That is, `java org.cprover.A` should be the correct path.

_Important notice_
It is mandatory to have all classes of the project to instrument in the classpath. The calculation
of the `StackMapTable` requires knowledge of common superclasses which can only be ensured that way.

If everything has gone correctly, you will see output of the form:

```bash
WARNING: BLUECOV_DB is not set  falling back to blueCov.db as database
register ID 0 A.<init>:()V@1
register ID 1 A.testFunction:(I)Z@1
register ID 2 A.testFunction:(I)Z@3
register ID 3 A.testFunction:(I)Z@5
register ID 4 A.testInstanceFunction:(I)Z@1
register ID 5 A.testInstanceFunction:(I)Z@3
register ID 6 A.testInstanceFunction:(I)Z@5
did not find <clinit> in class, added one
```

_Note_
One can use the environment variable `BLUECOV_DB` to change this default file name, this must then
be set when instrumenting _and_ when executing the instrumented code.

### Execution of instrumented Java

The bytecode can be executed just normally (with the full classpath). For example, providing A has
an entry point:

```bash
java -cp $(cat cp.txt):.:bluecov-0.1-jar-with-dependencies.jar A
```

Then the database will be updated with hit counts.

### Viewing the coverage results

To see the results (i.e. the hit count for each of the goals) run:

```bash
java -cp $(cat cp.txt):bluecov-0.1-jar-with-dependencies.jar `org.cprover.coverage.CoverageReport
```

This will produce a JSON report of coverage goals:

```json
[
  {
    "goalID": "java::TestClass.testInstanceFunction:(I)Z.coverage.2",
    "hitCount": 0,
    "coveredLines": [
      {
        "lineNumber": 12
      }
    ]
  },
  {
    "goalID": "java::TestClass.testFunction:(I)Z.coverage.3",
    "hitCount": 1,
    "coveredLines": [
      {
        "lineNumber": 6
      }
    ]
  }
]
```

### Integrating run into Maven

The tool can be integrated into a Maven project for the `mvn test` command by adding it as a plugin.
It will execute the instrumented code and update the database when `mvn test` is called. _Note: this
does not prepare or instrument the files._

The steps required to integrate the above into the `mvn test` command are:

- Prepare the class files in the project as described in "Using BlueCov".
- Copy the resulting `blueCov.db` file to the base directory of the Maven project.
- Update the `pom.xml` to include the resources necessary, in particular
    - dependencies

      ```xml
      <dependencies>
        <dependency>
          <groupId>junit</groupId>
          <artifactId>junit</artifactId>
          <version>4.8.2</version>
          <scope>test</scope>
        </dependency>
        <dependency>
          <groupId>org.mapdb</groupId>
          <artifactId>mapdb</artifactId>
          <version>3.0.2</version>
        </dependency>
        <dependency>
          <groupId>org.glassfish</groupId>
          <artifactId>javax.json</artifactId>
          <version>1.0.4</version>
        </dependency>
      ```

    - plugins

      ```xml
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-surefire-plugin</artifactId>
          <!-- <version>2.19</version> -->
          <configuration>
            <additionalClasspathElements>
              <additionalClasspathElement>$PATH_TO_COVERAGE_TOOL/JAR</additionalClasspathElement>
            </additionalClasspathElements>
          </configuration>
        </plugin>
      ```

- Now when `mvn test` is called, it will execute the instrumented Java and update the database.
- See "Viewing the coverage results" to see the results of this.

### Using BlueCov in another local project

If you want to write code using BlueCov as part of another project, import it from your m2
repository:

```xml

<dependency>
  <groupId>org.cprover.coverage</groupId>
  <artifactId>bluecov</artifactId>
  <version>0.1</version>
</dependency>
```

or from a file:

```xml

<dependency>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <version>2.22.0</version>
  <scope>test</scope>
  <systemPath>
    $PATH_TO_JAR
  </systemPath>
</dependency>
```

Note that to use the BlueCov jar in your local Maven repository, you must first run `mvn install` on
the BlueCov project.

## Incompatible Plugins

- `animal-sniffer` checks API compliance and complains about instrumented code, must be either
  removed or our classes have to be integrated into its classpath (unkonwn how to do that)

## Developing BlueCov

The basic flow of the program is:

1. BlueCov (entry point)
2. CoverageInstrument:

- reads each class file in turn
- creates entries in the database
- instruments each one

The instrumentations are:

- Static field for Logger (`FieldAdapter`)
- Extend <clinit> of the class to configure the field (`ExtendStaticInit`)
- Add calls in bytecode to call CoverageLog (`InstrumentByteCode`)

The `CoverageLog` is the class that is used in the execution of the instrumented Java program and is
responsible for the interface between the Java program and the database.
