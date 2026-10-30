# Virtual Schema for Oracle 6.0.0, released 2026-10-30

Code name: Secure Dependencies

## Summary

Fixed vulnerabilities in the test dependencies Objenesis and ASM.

## Breaking Changes

* This project now requires Java 17 for building and running and does not support Exasol v8 anymore.
* The virtual schema now useses Oracle JDBC driver `ojdbc17` instead of `ojdbc8`. Please upgrade the driver.

- Fixes #105
- Fixes #106
- Fixes #107
- Fixes #108
- Fixes #109
- Fixes #110
- Fixes #111
- Fixes #112


## Security

* Fixed CVE-2026-116486 (class loader type confusion) in dependency `org.objenesis:objenesis:3.4`
* #107: Fixed CVE-2026-110257 (uncontrolled memory allocation) in dependency `org.ow2.asm:asm:9.10.1`
* #108: Fixed CVE-2026-110258 (uncontrolled memory allocation) in dependency `org.ow2.asm:asm:9.10.1`
* #109: Fixed CVE-2026-110259 (uncontrolled recursion) in dependency `org.ow2.asm:asm:9.10.1`
* #110: Fixed CVE-2026-110260 (quadratic resource use) in dependency `org.ow2.asm:asm:9.10.1`
* #111: Fixed CVE-2026-110261 (algorithmic complexity) in dependency `org.ow2.asm:asm:9.10.1`
* #112: Fixed CVE-2026-110263 (uncontrolled recursion) in dependency `org.ow2.asm:asm:9.10.1`
* #105: Fixed CVE-2026-110262 (unbounded memory allocation) in dependency `org.ow2.asm:asm-commons:9.10.1`
* #106: Fixed CVE-2026-110270 (exponential memory allocation) in dependency `org.ow2.asm:asm-commons:9.10.1`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:virtual-schema-common-jdbc:14.0.5` to `14.1.1`

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:8.0.1` to `8.0.2`
* Updated `com.exasol:hamcrest-resultset-matcher:1.7.3` to `1.7.4`
* Updated `com.exasol:test-db-builder-java:4.0.2` to `4.0.3`
* Updated `com.exasol:udf-debugging-java:0.6.20` to `0.6.22`
* Updated `com.exasol:virtual-schema-common-jdbc:14.0.5` to `14.1.1`
* Updated `com.exasol:virtual-schema-shared-integration-tests:3.0.3` to `3.1.0`
* Added `com.oracle.database.jdbc:ojdbc17:23.26.4.0.0`
* Removed `com.oracle.database.jdbc:ojdbc8:23.26.3.0.0`
* Updated `nl.jqno.equalsverifier:equalsverifier:3.19.4` to `4.5.2`
* Updated `org.jacoco:org.jacoco.agent:0.8.15` to `0.8.16`
* Updated `org.junit.jupiter:junit-jupiter-api:5.14.3` to `6.1.3`
* Updated `org.junit.jupiter:junit-jupiter-params:5.14.3` to `6.1.3`
* Updated `org.mockito:mockito-junit-jupiter:5.23.0` to `5.24.0`
* Updated `org.slf4j:slf4j-jdk14:2.0.18` to `2.0.20`
* Added `org.testcontainers:testcontainers-oracle-free:2.0.5`
* Removed `org.testcontainers:testcontainers-oracle-xe:2.0.5`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.1.0` to `2.1.3`
* Updated `com.exasol:project-keeper-maven-plugin:5.7.4` to `5.8.2`
* Updated `io.github.git-commit-id:git-commit-id-maven-plugin:10.0.0` to `10.0.1`
* Updated `org.apache.maven.plugins:maven-artifact-plugin:3.6.1` to `3.7.0`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.15.0` to `3.16.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.6` to `3.6.1`
* Updated `org.apache.maven.plugins:maven-install-plugin:3.1.4` to `3.2.0`
* Updated `org.apache.maven.plugins:maven-jar-plugin:3.5.0` to `3.5.1`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.6` to `3.6.1`
* Updated `org.apache.maven.plugins:maven-toolchains-plugin:3.2.0` to `3.3.0`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.7.3` to `1.8.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.21.0` to `2.23.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.15` to `0.8.16`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:5.7.0.6970` to `5.8.0.7211`
