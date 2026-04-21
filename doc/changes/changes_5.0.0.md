# Oracle Virtual Schema 5.0.0, released 2026-04-??

Code name: Anonymous telemetry

## Summary

This release adds anonymous feature-usage telemetry via `telemetry-java`. See the [documentation](https://github.com/exasol/telemetry-java/blob/main/doc/app-user-guide.md) for details on collected data and opt-out behavior.

## Features

* #84: Add anonymous feature tracking

## Dependency Updates

### Virtual Schema for Oracle

#### Compile Dependency Updates

* Updated `com.exasol:virtual-schema-common-jdbc:13.0.1` to `14.0.0`

#### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:7.2.1` to `7.2.3`
* Updated `com.exasol:extension-manager-integration-test-java:0.5.15` to `0.5.19`
* Updated `com.exasol:test-db-builder-java:3.6.4` to `4.0.0`
* Updated `com.exasol:virtual-schema-common-jdbc:13.0.1` to `14.0.0`
* Updated `com.oracle.database.jdbc:ojdbc8:23.26.0.0.0` to `23.26.1.0.0`
* Removed `org.glassfish.jersey.core:jersey-client:2.47`
* Updated `org.junit.jupiter:junit-jupiter-api:5.14.1` to `5.14.3`
* Updated `org.junit.jupiter:junit-jupiter-params:5.14.1` to `5.14.3`
* Updated `org.mockito:mockito-junit-jupiter:5.20.0` to `5.23.0`
* Updated `org.testcontainers:testcontainers-junit-jupiter:2.0.2` to `2.0.5`
* Updated `org.testcontainers:testcontainers-oracle-xe:2.0.2` to `2.0.5`

### Extension

#### Compile Dependency Updates

* Updated `@exasol/extension-manager-interface:0.4.3` to `0.5.1`

#### Development Dependency Updates

* Updated `eslint:^9.39.4` to `^10.2.1`
* Updated `@eslint/js:^9.39.4` to `^10.0.1`
* Updated `ts-jest:^29.2.5` to `^29.4.9`
* Updated `@types/jest:^29.5.14` to `^30.0.0`
* Updated `typescript-eslint:^8.58.0` to `^8.59.0`
* Updated `typescript:^5.9.3` to `^6.0.3`
* Updated `@typescript-eslint/eslint-plugin:^8.58.0` to `^8.59.0`
* Updated `jest:29.7.0` to `30.3.0`
