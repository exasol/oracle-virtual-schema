# Virtual Schema for Oracle 2.4.0, released 2022-12-05

Code name: Dependency Upgrade

## Summary

Enabled to use Oracle database with characters not strictly ASCII by updating dependencies and using a new version of virtual-schema-common-jdbc.

Virtual-schema-common-jdbc version 10.0.0 introduced enhanced detection for data types of result sets.

Unfortunately with the new algorithm compatibility problems with the source database can happen under the following circumstances:

* data type `CHAR` or `VARCHAR`
* 8-bit character sets with encodings like `latin1` or `ISO-8859-1`
* characters being not strictly ASCII, e.g. German umlaut "Ü"

The current release therefore uses an updated version of `virtual-schema-common-jdbc` with an additional adapter property to configure the data type detection.

For details please see [adapter Properties for JDBC-Based Virtual Schemas](https://github.com/exasol/virtual-schema-common-jdbc/blob/main/README.md#adapter-properties-for-jdbc-based-virtual-schemas).

## Features

* #26: Enabled to use Oracle database with characters not strictly ASCII.

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:virtual-schema-common-jdbc:10.0.1` to `10.1.0`

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:6.2.0` to `6.4.0`
* Updated `com.exasol:test-db-builder-java:3.3.4` to `3.4.1`
* Updated `com.exasol:udf-debugging-java:0.6.4` to `0.6.5`
* Updated `com.exasol:virtual-schema-common-jdbc:10.0.1` to `10.1.0`
* Updated `nl.jqno.equalsverifier:equalsverifier:3.10.1` to `3.12.1`
* Updated `org.mockito:mockito-junit-jupiter:4.8.0` to `4.9.0`
* Updated `org.testcontainers:junit-jupiter:1.17.3` to `1.17.6`
* Updated `org.testcontainers:oracle-xe:1.17.3` to `1.17.6`
