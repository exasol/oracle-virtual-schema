<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                                 | License                                          |
| ------------------------------------------ | ------------------------------------------------ |
| [Virtual Schema Common JDBC][0]            | [MIT][1]                                         |
| [Exasol Database fundamentals for Java][2] | [MIT][1]                                         |
| [error-reporting-java][4]                  | [MIT][1]                                         |
| [ojdbc8][6]                                | [Oracle Free Use Terms and Conditions (FUTC)][7] |
| [Project Lombok][8]                        | [The MIT License][9]                             |

## Test Dependencies

| Dependency                                      | License                           |
| ----------------------------------------------- | --------------------------------- |
| [Virtual Schema Common JDBC][0]                 | [MIT][1]                          |
| [Hamcrest][12]                                  | [BSD License 3][13]               |
| [JUnit Jupiter (Aggregator)][14]                | [Eclipse Public License v2.0][15] |
| [mockito-junit-jupiter][16]                     | [The MIT License][17]             |
| [EqualsVerifier | release normal jar][18]       | [Apache License, Version 2.0][19] |
| [Test containers for Exasol on Docker][20]      | [MIT][1]                          |
| [Testcontainers :: JUnit Jupiter Extension][22] | [MIT][23]                         |
| [Testcontainers :: JDBC :: Oracle XE][22]       | [MIT][23]                         |
| [Test Database Builder for Java][26]            | [MIT][1]                          |
| [udf-debugging-java][28]                        | [MIT][1]                          |
| [Matcher for SQL Result Sets][30]               | [MIT][1]                          |
| [virtual-schema-shared-integration-tests][32]   | [MIT][1]                          |
| [JaCoCo :: Agent][34]                           | [Eclipse Public License 2.0][35]  |

## Plugin Dependencies

| Dependency                                              | License                                        |
| ------------------------------------------------------- | ---------------------------------------------- |
| [Apache Maven Enforcer Plugin][36]                      | [Apache License, Version 2.0][19]              |
| [Maven Flatten Plugin][38]                              | [Apache Software Licenese][39]                 |
| [Apache Maven Compiler Plugin][40]                      | [Apache License, Version 2.0][19]              |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][42] | [ASL2][39]                                     |
| [Reproducible Build Maven Plugin][44]                   | [Apache 2.0][39]                               |
| [Maven Surefire Plugin][46]                             | [Apache License, Version 2.0][19]              |
| [Versions Maven Plugin][48]                             | [Apache License, Version 2.0][19]              |
| [Project keeper maven plugin][50]                       | [MIT][1]                                       |
| [Apache Maven Assembly Plugin][52]                      | [Apache License, Version 2.0][19]              |
| [Apache Maven JAR Plugin][54]                           | [Apache License, Version 2.0][19]              |
| [Artifact reference checker and unifier][56]            | [MIT][1]                                       |
| [Apache Maven Dependency Plugin][58]                    | [Apache License, Version 2.0][19]              |
| [Lombok Maven Plugin][60]                               | [The MIT License][1]                           |
| [Maven Failsafe Plugin][62]                             | [Apache License, Version 2.0][19]              |
| [JaCoCo :: Maven Plugin][64]                            | [Eclipse Public License 2.0][35]               |
| [error-code-crawler-maven-plugin][66]                   | [MIT][1]                                       |
| [Maven Clean Plugin][68]                                | [The Apache Software License, Version 2.0][39] |
| [Maven Resources Plugin][70]                            | [The Apache Software License, Version 2.0][39] |
| [Maven Install Plugin][72]                              | [The Apache Software License, Version 2.0][39] |
| [Maven Deploy Plugin][74]                               | [The Apache Software License, Version 2.0][39] |
| [Maven Site Plugin 3][76]                               | [The Apache Software License, Version 2.0][39] |

[34]: https://www.eclemma.org/jacoco/index.html
[4]: https://github.com/exasol/error-reporting-java
[6]: https://www.oracle.com/database/technologies/maven-central-guide.html
[2]: https://github.com/exasol/db-fundamentals-java
[39]: http://www.apache.org/licenses/LICENSE-2.0.txt
[8]: https://projectlombok.org
[46]: https://maven.apache.org/surefire/maven-surefire-plugin/
[68]: http://maven.apache.org/plugins/maven-clean-plugin/
[7]: https://www.oracle.com/downloads/licenses/oracle-free-license.html
[1]: https://opensource.org/licenses/MIT
[16]: https://github.com/mockito/mockito
[62]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[26]: https://github.com/exasol/test-db-builder-java
[32]: https://github.com/exasol/virtual-schema-shared-integration-tests
[48]: http://www.mojohaus.org/versions-maven-plugin/
[13]: http://opensource.org/licenses/BSD-3-Clause
[40]: https://maven.apache.org/plugins/maven-compiler-plugin/
[23]: http://opensource.org/licenses/MIT
[0]: https://github.com/exasol/virtual-schema-common-jdbc
[35]: https://www.eclipse.org/legal/epl-2.0/
[20]: https://github.com/exasol/exasol-testcontainers
[64]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[17]: https://github.com/mockito/mockito/blob/main/LICENSE
[9]: https://projectlombok.org/LICENSE
[30]: https://github.com/exasol/hamcrest-resultset-matcher
[44]: http://zlika.github.io/reproducible-build-maven-plugin
[58]: https://maven.apache.org/plugins/maven-dependency-plugin/
[19]: https://www.apache.org/licenses/LICENSE-2.0.txt
[18]: https://www.jqno.nl/equalsverifier
[36]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[60]: https://awhitford.github.com/lombok.maven/lombok-maven-plugin/
[50]: https://github.com/exasol/project-keeper-maven-plugin/project-keeper-maven-plugin-generated-parent/project-keeper-maven-plugin
[15]: https://www.eclipse.org/legal/epl-v20.html
[72]: http://maven.apache.org/plugins/maven-install-plugin/
[14]: https://junit.org/junit5/
[42]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[22]: https://testcontainers.org
[38]: https://www.mojohaus.org/flatten-maven-plugin/flatten-maven-plugin
[28]: https://github.com/exasol/udf-debugging-java
[12]: http://hamcrest.org/JavaHamcrest/
[74]: http://maven.apache.org/plugins/maven-deploy-plugin/
[76]: http://maven.apache.org/plugins/maven-site-plugin/
[70]: http://maven.apache.org/plugins/maven-resources-plugin/
[56]: https://github.com/exasol/artifact-reference-checker-maven-plugin
[66]: https://github.com/exasol/error-code-crawler-maven-plugin
[54]: https://maven.apache.org/plugins/maven-jar-plugin/
[52]: https://maven.apache.org/plugins/maven-assembly-plugin/
