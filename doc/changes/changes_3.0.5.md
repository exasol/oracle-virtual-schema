# Oracle Virtual Schema 3.0.5, released 2025-01-13

Code name: Fixed CVE-2025-24970 and CVE-2025-25193

## Summary

This update fixes CVE-2025-24970 (crash on special network package) by updating `netty` to the latest version.
It also fixes CVE-2025-25193 (an issue under Windows). Note that this issue does not affect the virtual schema since it is not used under Windows anyway.

Also, please note that the vulnerable package was in the test code only, so a production update is not strictly necessary. 

## Features

* #55: Fixed CVE-2025-25193 by updating `netty` to 4.1.118
* #56: Fixed CVE-2025-24970 by updating `netty` to 4.1.118

## Dependency Updates

### Virtual Schema for Oracle

#### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:7.1.2` to `7.1.3`
* Updated `com.exasol:extension-manager-integration-test-java:0.5.13` to `0.5.15`
* Updated `com.exasol:udf-debugging-java:0.6.14` to `0.6.15`
* Updated `com.oracle.database.jdbc:ojdbc8:23.6.0.24.10` to `23.7.0.25.01`
* Updated `io.netty:netty-common:4.1.116.Final` to `4.1.118.Final`
* Updated `nl.jqno.equalsverifier:equalsverifier:3.18.1` to `3.19`
