# Oracle Virtual Schema 3.0.6, released 2025-07-08

Code name: Timestamp with precision support

## Summary

This release improves the support for TIMESTAMP columns types with fractional second precision (FSP). The specified FSP will be maintained in Exasol newer versions (>= 8.32.0)

We also fixed integration tests by improving conversion from Oracle numeric types to Exasol numeric or varchar types. The data type precision, size and decimal scale have been fixed for many conversion scenarios.

This release also contains a security update. We updated the dependencies of the project to fix transitive security issues.

We also added an exception for the OSSIndex for CVE-2024-55551, which is a false positive in Exasol's JDBC driver.
This issue has been fixed quite a while back now, but the OSSIndex unfortunately does not contain the fix version of 24.2.1 (2024-12-10) set.

## Features

* #48: TS(9) support in Oracle VS

## Security

* #129: Fix CVE-2024-55551 in `com.exasol:exasol-jdbc:jar:7.1.20:compile`

## Dependency Updates

### Virtual Schema for Oracle

#### Compile Dependency Updates

* Updated `com.exasol:virtual-schema-common-jdbc:12.0.0` to `13.0.0`

#### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:7.1.3` to `7.1.6`
* Updated `com.exasol:virtual-schema-common-jdbc:12.0.0` to `13.0.0`
* Updated `com.exasol:virtual-schema-shared-integration-tests:3.0.0` to `3.0.1`
* Updated `org.jacoco:org.jacoco.agent:0.8.12` to `0.8.13`
* Added `org.junit.jupiter:junit-jupiter-api:5.13.1`
* Added `org.junit.jupiter:junit-jupiter-params:5.13.1`
* Removed `org.junit.jupiter:junit-jupiter:5.11.4`
* Updated `org.mockito:mockito-junit-jupiter:5.15.2` to `5.18.0`
* Updated `org.testcontainers:junit-jupiter:1.20.4` to `1.21.1`
* Updated `org.testcontainers:oracle-xe:1.20.4` to `1.21.1`

#### Plugin Dependency Updates

* Updated `com.exasol:artifact-reference-checker-maven-plugin:0.4.2` to `0.4.3`
* Updated `com.exasol:project-keeper-maven-plugin:4.5.0` to `5.2.2`
* Added `io.github.git-commit-id:git-commit-id-maven-plugin:9.0.1`
* Removed `io.github.zlika:reproducible-build-maven-plugin:0.17`
* Added `org.apache.maven.plugins:maven-artifact-plugin:3.6.0`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.13.0` to `3.14.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.2` to `3.5.3`
* Updated `org.apache.maven.plugins:maven-install-plugin:3.1.3` to `3.1.4`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.2` to `3.5.3`
* Updated `org.codehaus.mojo:exec-maven-plugin:3.1.0` to `3.5.0`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.6.0` to `1.7.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.12` to `0.8.13`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:5.0.0.4389` to `5.1.0.4751`
