# Virtual Schema for Oracle 2.2.0, released 2022-04-11

Code name: Import from ora: explicit data types switch.

## Features

* #14: Explicitly state types when using IMPORT_FROM_ORA: Added GENERATE_JDBC_DATATYPE_MAPPING_FOR_OCI flag to virtual schema adapter for this functionality.
## Dependency Updates

### Test Dependency Updates

* Updated `com.exasol:virtual-schema-shared-integration-tests:2.1.1` to `2.2.0`
* Added `org.jacoco:org.jacoco.agent:0.8.5`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:0.7.1` to `1.1.0`
* Updated `com.exasol:project-keeper-maven-plugin:1.3.4` to `2.3.0`
* Updated `org.apache.maven.plugins:maven-dependency-plugin:2.8` to `3.2.0`
* Updated `org.apache.maven.plugins:maven-jar-plugin:3.2.2` to `3.2.0`
* Added `org.codehaus.mojo:flatten-maven-plugin:1.2.7`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.9.0` to `2.8.1`
* Added `org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184`
* Updated `org.sonatype.ossindex.maven:ossindex-maven-plugin:3.2.0` to `3.1.0`
