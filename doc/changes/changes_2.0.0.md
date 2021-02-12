# Oracle Virtual Schemas 2.0.0, released 2021-??-??

Code name:

## Summary

The `SQL_DIALECT` property used when executing a `CREATE VIRTUAL SCHEMA` from the Exasol database is obsolete from this version. Please, do not provide this property anymore.

## Features / Enhancements

* 3: Unified error messages with `error-reporting-java` 

## Plugin Dependencies

* Added `com.exasol:error-code-crawler-maven-plugin:0.1.1`
* Updated `com.exasol:error-reporting-java:0.2.0` to `0.2.2`
* Updated `com.exasol:virtual-schema-common-jdbc:8.0.0` to `9.0.1`