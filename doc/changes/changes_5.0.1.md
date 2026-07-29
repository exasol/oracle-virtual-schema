# Oracle Virtual Schema 5.0.1, released 2026-07-28

Code name: Fixed vulnerabilities CVE-2026-59889, CVE-2026-59888, CVE-2026-54518, CVE-2026-54517, CVE-2026-54516, CVE-2026-54515, CVE-2026-54514, CVE-2026-54513, CVE-2026-54512, CVE-2026-9563

## Summary

This release fixes 10 vulnerabilities in test dependencies.

## Security

* #100: Fixed CVE-2026-59889 in `com.fasterxml.jackson.core:jackson-databind:jar:2.21.2:test`
* #99: Fixed CVE-2026-59888 in `com.fasterxml.jackson.core:jackson-databind:jar:2.21.2:test`
* #98: Fixed CVE-2026-54518 in `com.fasterxml.jackson.core:jackson-databind:jar:2.21.2:test`
* #97: Fixed CVE-2026-54517 in `com.fasterxml.jackson.core:jackson-databind:jar:2.21.2:test`
* #96: Fixed CVE-2026-54516 in `com.fasterxml.jackson.core:jackson-databind:jar:2.21.2:test`
* #95: Fixed CVE-2026-54515 in `com.fasterxml.jackson.core:jackson-databind:jar:2.21.2:test`
* #94: Fixed CVE-2026-54514 in `com.fasterxml.jackson.core:jackson-databind:jar:2.21.2:test`
* #93: Fixed CVE-2026-54513 in `com.fasterxml.jackson.core:jackson-databind:jar:2.21.2:test`
* #92: Fixed CVE-2026-54512 in `com.fasterxml.jackson.core:jackson-databind:jar:2.21.2:test`
* #91: Fixed CVE-2026-9563 in `org.eclipse.parsson:parsson:jar:1.1.7:test`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:virtual-schema-common-jdbc:14.0.2` to `14.0.4`

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:7.2.3` to `8.0.1`
* Removed `com.exasol:extension-manager-integration-test-java:0.5.19`
* Updated `com.exasol:hamcrest-resultset-matcher:1.7.2` to `1.7.3`
* Updated `com.exasol:test-db-builder-java:4.0.0` to `4.0.1`
* Updated `com.exasol:udf-debugging-java:0.6.18` to `0.6.20`
* Updated `com.exasol:virtual-schema-common-jdbc:14.0.2` to `14.0.4`
* Updated `com.oracle.database.jdbc:ojdbc8:23.26.1.0.0` to `23.26.3.0.0`
* Updated `org.jacoco:org.jacoco.agent:0.8.14` to `0.8.15`
* Updated `org.slf4j:slf4j-jdk14:2.0.17` to `2.0.18`

### Plugin Dependency Updates

* Updated `com.exasol:artifact-reference-checker-maven-plugin:0.4.4` to `1.0.1`
* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.7` to `2.1.0`
* Updated `com.exasol:project-keeper-maven-plugin:5.6.1` to `5.7.4`
* Removed `com.exasol:quality-summarizer-maven-plugin:0.2.1`
* Updated `org.apache.maven.plugins:maven-dependency-plugin:3.10.0` to `3.11.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.6.2` to `3.6.3`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.5` to `3.5.6`
* Updated `org.apache.maven.plugins:maven-site-plugin:3.21.0` to `3.22.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.5` to `3.5.6`
* Removed `org.codehaus.mojo:exec-maven-plugin:3.5.1`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.14` to `0.8.15`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:5.5.0.6356` to `5.7.0.6970`
* Added `org.spdx:spdx-maven-plugin:1.0.4`
