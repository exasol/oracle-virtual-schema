# Virtual Schema for Oracle 2.4.2, released 2023-10-02

Code name: Fix CVE-2023-42503 in test dependency

## Summary

This release fixes CVE-2023-42503 in test dependency `org.apache.commons:commons-compress`.

## Security

* #34: Fixed CVE-2023-42503 in test dependency `org.apache.commons:commons-compress`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:virtual-schema-common-jdbc:10.5.0` to `11.0.2`
* Updated `com.oracle.database.jdbc:ojdbc8:21.9.0.0` to `23.2.0.0`

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:6.5.1` to `6.6.2`
* Updated `com.exasol:hamcrest-resultset-matcher:1.5.2` to `1.6.1`
* Updated `com.exasol:test-db-builder-java:3.4.2` to `3.5.1`
* Updated `com.exasol:udf-debugging-java:0.6.8` to `0.6.11`
* Updated `com.exasol:virtual-schema-common-jdbc:10.5.0` to `11.0.2`
* Updated `com.exasol:virtual-schema-shared-integration-tests:2.2.3` to `2.2.5`
* Updated `nl.jqno.equalsverifier:equalsverifier:3.14` to `3.15.2`
* Updated `org.jacoco:org.jacoco.agent:0.8.8` to `0.8.10`
* Updated `org.junit.jupiter:junit-jupiter:5.9.2` to `5.10.0`
* Updated `org.mockito:mockito-junit-jupiter:5.2.0` to `5.5.0`
* Updated `org.slf4j:slf4j-jdk14:2.0.6` to `2.0.9`
* Updated `org.testcontainers:junit-jupiter:1.17.6` to `1.19.0`
* Updated `org.testcontainers:oracle-xe:1.17.6` to `1.19.0`

### Plugin Dependency Updates

* Updated `com.exasol:artifact-reference-checker-maven-plugin:0.4.0` to `0.4.2`
* Updated `com.exasol:error-code-crawler-maven-plugin:1.1.1` to `1.3.0`
* Updated `com.exasol:project-keeper-maven-plugin:2.4.6` to `2.9.12`
* Updated `io.github.zlika:reproducible-build-maven-plugin:0.15` to `0.16`
* Updated `org.apache.maven.plugins:maven-assembly-plugin:3.3.0` to `3.6.0`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.10.1` to `3.11.0`
* Updated `org.apache.maven.plugins:maven-dependency-plugin:3.3.0` to `3.6.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.0.0` to `3.4.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M5` to `3.1.2`
* Updated `org.apache.maven.plugins:maven-jar-plugin:3.2.2` to `3.3.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M5` to `3.1.2`
* Added `org.basepom.maven:duplicate-finder-maven-plugin:2.0.1`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.2.7` to `1.5.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.10.0` to `2.16.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.8` to `0.8.10`
