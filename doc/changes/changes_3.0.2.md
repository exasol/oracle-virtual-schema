# Oracle Virtual Schema 3.0.2, released 2024-05-07

Code name: Improve error handling for extension

## Summary

This release improves error handling when creating a new Virtual Schema using the extension: the extension now checks if a schema with the same name exists and returns a helpful error message. This check is case-insensitive because Exasol's `CONNECTION` names are also case-insensitive.

## Bugfix

* #45: Added extension JS file to release assets

## Dependency Updates

### Virtual Schema for Oracle

#### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:7.0.1` to `7.1.0`
* Updated `com.exasol:extension-manager-integration-test-java:0.5.9` to `0.5.11`
* Updated `com.oracle.database.jdbc:ojdbc8:23.3.0.23.09` to `23.4.0.24.05`
* Updated `org.slf4j:slf4j-jdk14:2.0.12` to `2.0.13`

### Extension

#### Compile Dependency Updates

* Updated `@exasol/extension-manager-interface:0.4.1` to `0.4.2`

#### Development Dependency Updates

* Updated `eslint:^8.53.0` to `^8.54.0`
* Updated `@typescript-eslint/parser:^6.10.0` to `^7.8.0`
* Updated `ts-jest:^29.1.1` to `^29.1.2`
* Updated `@types/jest:^29.5.8` to `^29.5.12`
* Updated `typescript:^5.2.2` to `^5.4.5`
* Updated `@typescript-eslint/eslint-plugin:^6.10.0` to `^7.8.0`
* Updated `ts-node:^10.9.1` to `^10.9.2`
* Updated `esbuild:^0.19.5` to `^0.21.0`
