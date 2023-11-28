# Virtual Schema for Oracle 2.4.3, released 2023-??-??

Code name: Test with Exasol 8

## Summary

This release adds integration tests using Exasol 8 and the extension code making VSORA available in the [Exasol Extension Manager](https://github.com/exasol/extension-manager). Please note that the VSORA extension expects the Oracle JDBC driver `ojdbc8.jar` in BucketFS. If this file is missing, the extension won't be available in Extension Manager.

**Note:** This release removes the Oracle JDBC driver from the adapter JAR file that was accidentally included in version 2.1.0. This means that you will need to define the `ADAPTER SCRIPT` specifying both the adapter JAR and the JDBC driver JAR as described in the [user guide](../user_guide/oracle_user_guide.md#installing-the-adapter-script).

**Known issues:** After adding integration tests for the extension, this release contains the following vulnerabilities in third party test dependencies:

* CVE-2023-4586 in `io.netty:netty-handler:jar:4.1.94.Final:test`
* CVE-2020-36641 in `fr.turri:aXMLRPC:jar:1.13.0:test`

## Feature

* #33: Added tests with Exasol 8
* #38: Added extension for Extension Manager

## Dependency Updates

### Virtual Schema for Oracle

#### Compile Dependency Updates

* Removed `com.oracle.database.jdbc:ojdbc8:23.3.0.23.09`

#### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:6.6.2` to `6.6.3`
* Added `com.exasol:extension-manager-integration-test-java:0.5.6`
* Updated `com.exasol:hamcrest-resultset-matcher:1.6.1` to `1.6.2`
* Updated `com.exasol:test-db-builder-java:3.5.1` to `3.5.2`
* Added `com.oracle.database.jdbc:ojdbc8:23.3.0.23.09`
* Updated `nl.jqno.equalsverifier:equalsverifier:3.15.2` to `3.15.3`
* Updated `org.jacoco:org.jacoco.agent:0.8.10` to `0.8.11`
* Updated `org.junit.jupiter:junit-jupiter:5.10.0` to `5.10.1`
* Updated `org.mockito:mockito-junit-jupiter:5.5.0` to `5.7.0`

#### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.3.0` to `1.3.1`
* Updated `com.exasol:project-keeper-maven-plugin:2.9.12` to `2.9.16`
* Updated `org.apache.maven.plugins:maven-clean-plugin:2.5` to `3.3.2`
* Updated `org.apache.maven.plugins:maven-dependency-plugin:3.6.0` to `3.6.1`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.4.0` to `3.4.1`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.1.2` to `3.2.2`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.1.2` to `3.2.2`
* Added `org.codehaus.mojo:exec-maven-plugin:3.1.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.16.0` to `2.16.1`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.10` to `0.8.11`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184` to `3.10.0.2594`

### Extension

#### Compile Dependency Updates

* Added `@exasol/extension-manager-interface:0.4.1`

#### Development Dependency Updates

* Added `eslint:^8.53.0`
* Added `@typescript-eslint/parser:^6.10.0`
* Added `ts-jest:^29.1.1`
* Added `@types/jest:^29.5.8`
* Added `typescript:^5.2.2`
* Added `@typescript-eslint/eslint-plugin:^6.10.0`
* Added `jest:29.7.0`
* Added `ts-node:^10.9.1`
* Added `esbuild:^0.19.5`
