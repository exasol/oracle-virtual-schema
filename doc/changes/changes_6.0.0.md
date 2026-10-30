# Virtual Schema for Oracle 6.0.0, released 2026-10-30

Code name: Secure Dependencies

## Summary

Fixed vulnerabilities in the test dependencies Objenesis and ASM.

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

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:8.0.1` to `8.0.2`
* Updated `org.slf4j:slf4j-jdk14:2.0.18` to `2.0.19`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.1.0` to `2.1.1`
* Updated `com.exasol:project-keeper-maven-plugin:5.7.4` to `5.8.2`
* Updated `io.github.git-commit-id:git-commit-id-maven-plugin:10.0.0` to `10.0.1`
* Updated `org.apache.maven.plugins:maven-jar-plugin:3.5.0` to `3.5.1`
* Updated `org.apache.maven.plugins:maven-toolchains-plugin:3.2.0` to `3.3.0`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.7.3` to `1.8.0`
