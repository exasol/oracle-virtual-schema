# Oracle Virtual Schemas 2.0.1, released 2021-08-12

Code name: Dependency Updates

## Summary

In this release we updated the dependencies. By that we fixed transitive CVE-2021-36090.

## Documentation

* #7: Improved user guide.

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:db-fundamentals-java:0.1.1` to `0.1.2`
* Updated `com.exasol:error-reporting-java:0.2.2` to `0.4.0`
* Updated `com.exasol:virtual-schema-common-jdbc:9.0.1` to `9.0.3`

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:3.5.1` to `4.0.0`
* Updated `com.exasol:hamcrest-resultset-matcher:1.4.0` to `1.4.1`
* Updated `com.exasol:test-db-builder-java:3.1.0` to `3.2.1`
* Updated `com.exasol:virtual-schema-common-jdbc:9.0.1` to `9.0.3`
* Updated `nl.jqno.equalsverifier:equalsverifier:3.5.5` to `3.7`
* Updated `org.junit.jupiter:junit-jupiter:5.7.1` to `5.7.2`
* Updated `org.mockito:mockito-junit-jupiter:3.8.0` to `3.11.2`
* Updated `org.testcontainers:junit-jupiter:1.15.2` to `1.16.0`
* Updated `org.testcontainers:oracle-xe:1.15.2` to `1.16.0`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:0.1.1` to `0.4.0`
* Updated `com.exasol:project-keeper-maven-plugin:0.4.2` to `0.10.0`
* Added `io.github.zlika:reproducible-build-maven-plugin:0.13`
* Added `org.apache.maven.plugins:maven-dependency-plugin:2.8`
* Updated `org.apache.maven.plugins:maven-jar-plugin:2.4` to `3.2.0`