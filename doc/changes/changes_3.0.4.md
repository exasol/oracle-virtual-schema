# Oracle Virtual Schema 3.0.4, released 2025-01-14

Code name: Fixed CVE-2024-4068

## Summary

We updated the JavaScript Library "braces" to 3.0.3 to fix CVE-2024-4068. This fix only affects test in the project since the library is not used in production code.

## Features

* #52: Fixed CVE-2024-4068 by updating "braces" JS library

## Dependency Updates

### Virtual Schema for Oracle

#### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:7.1.1` to `7.1.2`
* Updated `com.exasol:extension-manager-integration-test-java:0.5.12` to `0.5.13`
* Updated `com.exasol:maven-project-version-getter:1.2.0` to `1.2.1`
* Updated `com.exasol:udf-debugging-java:0.6.13` to `0.6.14`
* Updated `io.netty:netty-common:4.1.115.Final` to `4.1.116.Final`
* Updated `nl.jqno.equalsverifier:equalsverifier:3.17.3` to `3.18.1`
* Updated `org.junit.jupiter:junit-jupiter:5.11.3` to `5.11.4`
* Updated `org.mockito:mockito-junit-jupiter:5.14.2` to `5.15.2`
* Updated `org.testcontainers:junit-jupiter:1.20.3` to `1.20.4`
* Updated `org.testcontainers:oracle-xe:1.20.3` to `1.20.4`

#### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:4.4.0` to `4.5.0`
* Updated `org.apache.maven.plugins:maven-dependency-plugin:3.8.0` to `3.8.1`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.1` to `3.5.2`
* Updated `org.apache.maven.plugins:maven-site-plugin:3.9.1` to `3.21.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.1` to `3.5.2`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.17.1` to `2.18.0`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:4.0.0.4121` to `5.0.0.4389`
