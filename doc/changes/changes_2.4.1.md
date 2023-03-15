# Virtual Schema for Oracle 2.4.1, released 2023-03-16

Code name: Dependency Upgrade on Top of 2.4.0

## Summary

Updated dependencies to replace `com.exasol:exasol-script-api` by `udf-api-java/1.0.1` as `com.exasol:exasol-script-api` had been available on discontinued maven repository `maven.exasol.com`.

## Bugfixes

* #30: Updated dependencies

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-reporting-java:1.0.0` to `1.0.1`
* Updated `com.exasol:virtual-schema-common-jdbc:10.1.0` to `10.5.0`
* Updated `com.oracle.database.jdbc:ojdbc8:21.7.0.0` to `21.9.0.0`

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:6.4.0` to `6.5.1`
* Updated `com.exasol:test-db-builder-java:3.4.1` to `3.4.2`
* Updated `com.exasol:udf-debugging-java:0.6.5` to `0.6.8`
* Updated `com.exasol:virtual-schema-common-jdbc:10.1.0` to `10.5.0`
* Updated `com.exasol:virtual-schema-shared-integration-tests:2.2.2` to `2.2.3`
* Updated `nl.jqno.equalsverifier:equalsverifier:3.12.1` to `3.14`
* Updated `org.junit.jupiter:junit-jupiter:5.9.1` to `5.9.2`
* Updated `org.mockito:mockito-junit-jupiter:4.9.0` to `5.2.0`
* Added `org.slf4j:slf4j-jdk14:2.0.6`
