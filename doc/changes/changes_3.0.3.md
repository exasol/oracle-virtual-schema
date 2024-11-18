# Oracle Virtual Schema 3.0.3, released 2024-11-18

Code name: Fixed vulnerability CVE-2024-47535 in io.netty:netty-common:jar:4.1.104.Final:test

## Summary

This release fixes the following vulnerability:

### CVE-2024-47535 (CWE-400) in dependency `io.netty:netty-common:jar:4.1.104.Final:test`
Netty is an asynchronous event-driven network application framework for rapid development of maintainable high performance protocol servers & clients. An unsafe reading of environment file could potentially cause a denial of service in Netty. When loaded on an Windows application, Netty attempts to load a file that does not exist. If an attacker creates such a large file, the Netty application crashes. This vulnerability is fixed in 4.1.115.
#### References
* https://ossindex.sonatype.org/vulnerability/CVE-2024-47535?component-type=maven&component-name=io.netty%2Fnetty-common&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2024-47535
* https://github.com/advisories/GHSA-xq3w-v528-46rv

## Security

* #50: Fixed vulnerability CVE-2024-47535 in dependency `io.netty:netty-common:jar:4.1.104.Final:test`
## Dependency Updates

### Virtual Schema for Oracle

#### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:7.1.0` to `7.1.1`
* Updated `com.exasol:extension-manager-integration-test-java:0.5.11` to `0.5.12`
* Updated `com.exasol:hamcrest-resultset-matcher:1.6.5` to `1.7.0`
* Updated `com.exasol:test-db-builder-java:3.5.4` to `3.6.0`
* Updated `com.oracle.database.jdbc:ojdbc8:23.4.0.24.05` to `23.6.0.24.10`
* Updated `nl.jqno.equalsverifier:equalsverifier:3.16.1` to `3.17.3`
* Updated `org.hamcrest:hamcrest:2.2` to `3.0`
* Updated `org.jacoco:org.jacoco.agent:0.8.11` to `0.8.12`
* Updated `org.junit.jupiter:junit-jupiter:5.10.2` to `5.11.3`
* Updated `org.mockito:mockito-junit-jupiter:5.11.0` to `5.14.2`
* Updated `org.slf4j:slf4j-jdk14:2.0.13` to `2.0.16`
* Updated `org.testcontainers:junit-jupiter:1.19.7` to `1.20.3`
* Updated `org.testcontainers:oracle-xe:1.19.7` to `1.20.3`

#### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.2` to `2.0.3`
* Updated `com.exasol:project-keeper-maven-plugin:4.3.0` to `4.4.0`
* Added `com.exasol:quality-summarizer-maven-plugin:0.2.0`
* Updated `io.github.zlika:reproducible-build-maven-plugin:0.16` to `0.17`
* Updated `org.apache.maven.plugins:maven-dependency-plugin:3.6.1` to `3.8.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.4.1` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.2.5` to `3.5.1`
* Updated `org.apache.maven.plugins:maven-install-plugin:3.1.0` to `3.1.3`
* Updated `org.apache.maven.plugins:maven-jar-plugin:3.3.0` to `3.4.2`
* Updated `org.apache.maven.plugins:maven-resources-plugin:3.3.0` to `3.3.1`
* Updated `org.apache.maven.plugins:maven-site-plugin:3.12.1` to `3.9.1`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.2.5` to `3.5.1`
* Updated `org.apache.maven.plugins:maven-toolchains-plugin:3.1.0` to `3.2.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.16.2` to `2.17.1`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:3.11.0.3922` to `4.0.0.4121`
