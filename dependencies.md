<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                                 | License                                          |
| ------------------------------------------ | ------------------------------------------------ |
| [Virtual Schema Common JDBC][0]            | [MIT][1]                                         |
| [Exasol Database fundamentals for Java][2] | [MIT][1]                                         |
| [error-reporting-java][4]                  | [MIT][1]                                         |
| [ojdbc8][6]                                | [Oracle Free Use Terms and Conditions (FUTC)][7] |

## Test Dependencies

| Dependency                                      | License                           |
| ----------------------------------------------- | --------------------------------- |
| [Virtual Schema Common JDBC][0]                 | [MIT][1]                          |
| [Hamcrest][10]                                  | [BSD License 3][11]               |
| [JUnit Jupiter (Aggregator)][12]                | [Eclipse Public License v2.0][13] |
| [mockito-junit-jupiter][14]                     | [The MIT License][15]             |
| [EqualsVerifier | release normal jar][16]       | [Apache License, Version 2.0][17] |
| [Test containers for Exasol on Docker][18]      | [MIT][1]                          |
| [Testcontainers :: JUnit Jupiter Extension][20] | [MIT][21]                         |
| [Testcontainers :: JDBC :: Oracle XE][20]       | [MIT][21]                         |
| [Test Database Builder for Java][24]            | [MIT][1]                          |
| [udf-debugging-java][26]                        | [MIT][1]                          |
| [Matcher for SQL Result Sets][28]               | [MIT][1]                          |
| [virtual-schema-shared-integration-tests][30]   | [MIT][1]                          |
| [JaCoCo :: Agent][32]                           | [Eclipse Public License 2.0][33]  |

## Plugin Dependencies

| Dependency                                              | License                                        |
| ------------------------------------------------------- | ---------------------------------------------- |
| [SonarQube Scanner for Maven][34]                       | [GNU LGPL 3][35]                               |
| [Apache Maven Compiler Plugin][36]                      | [Apache License, Version 2.0][17]              |
| [Apache Maven Enforcer Plugin][38]                      | [Apache License, Version 2.0][17]              |
| [Maven Flatten Plugin][40]                              | [Apache Software Licenese][41]                 |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][42] | [ASL2][41]                                     |
| [Reproducible Build Maven Plugin][44]                   | [Apache 2.0][41]                               |
| [Maven Surefire Plugin][46]                             | [Apache License, Version 2.0][17]              |
| [Versions Maven Plugin][48]                             | [Apache License, Version 2.0][17]              |
| [Project keeper maven plugin][50]                       | [The MIT License][51]                          |
| [Apache Maven Assembly Plugin][52]                      | [Apache License, Version 2.0][17]              |
| [Apache Maven JAR Plugin][54]                           | [Apache License, Version 2.0][17]              |
| [Artifact reference checker and unifier][56]            | [MIT][1]                                       |
| [Apache Maven Dependency Plugin][58]                    | [Apache License, Version 2.0][17]              |
| [Maven Failsafe Plugin][60]                             | [Apache License, Version 2.0][17]              |
| [JaCoCo :: Maven Plugin][62]                            | [Eclipse Public License 2.0][33]               |
| [error-code-crawler-maven-plugin][64]                   | [MIT][1]                                       |
| [Maven Clean Plugin][66]                                | [The Apache Software License, Version 2.0][41] |
| [Maven Resources Plugin][68]                            | [The Apache Software License, Version 2.0][41] |
| [Maven Install Plugin][70]                              | [The Apache Software License, Version 2.0][41] |
| [Maven Deploy Plugin][72]                               | [The Apache Software License, Version 2.0][41] |
| [Maven Site Plugin 3][74]                               | [The Apache Software License, Version 2.0][41] |

[32]: https://www.eclemma.org/jacoco/index.html
[4]: https://github.com/exasol/error-reporting-java
[6]: https://www.oracle.com/database/technologies/maven-central-guide.html
[2]: https://github.com/exasol/db-fundamentals-java
[41]: http://www.apache.org/licenses/LICENSE-2.0.txt
[46]: https://maven.apache.org/surefire/maven-surefire-plugin/
[66]: http://maven.apache.org/plugins/maven-clean-plugin/
[7]: https://www.oracle.com/downloads/licenses/oracle-free-license.html
[1]: https://opensource.org/licenses/MIT
[14]: https://github.com/mockito/mockito
[60]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[24]: https://github.com/exasol/test-db-builder-java
[30]: https://github.com/exasol/virtual-schema-shared-integration-tests
[48]: http://www.mojohaus.org/versions-maven-plugin/
[50]: https://github.com/exasol/project-keeper/
[11]: http://opensource.org/licenses/BSD-3-Clause
[36]: https://maven.apache.org/plugins/maven-compiler-plugin/
[21]: http://opensource.org/licenses/MIT
[0]: https://github.com/exasol/virtual-schema-common-jdbc
[33]: https://www.eclipse.org/legal/epl-2.0/
[35]: http://www.gnu.org/licenses/lgpl.txt
[18]: https://github.com/exasol/exasol-testcontainers
[62]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[15]: https://github.com/mockito/mockito/blob/main/LICENSE
[28]: https://github.com/exasol/hamcrest-resultset-matcher
[44]: http://zlika.github.io/reproducible-build-maven-plugin
[51]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[58]: https://maven.apache.org/plugins/maven-dependency-plugin/
[17]: https://www.apache.org/licenses/LICENSE-2.0.txt
[34]: http://sonarsource.github.io/sonar-scanner-maven/
[16]: https://www.jqno.nl/equalsverifier
[38]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[13]: https://www.eclipse.org/legal/epl-v20.html
[70]: http://maven.apache.org/plugins/maven-install-plugin/
[12]: https://junit.org/junit5/
[42]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[20]: https://testcontainers.org
[40]: https://www.mojohaus.org/flatten-maven-plugin/flatten-maven-plugin
[26]: https://github.com/exasol/udf-debugging-java
[10]: http://hamcrest.org/JavaHamcrest/
[72]: http://maven.apache.org/plugins/maven-deploy-plugin/
[74]: http://maven.apache.org/plugins/maven-site-plugin/
[68]: http://maven.apache.org/plugins/maven-resources-plugin/
[56]: https://github.com/exasol/artifact-reference-checker-maven-plugin
[64]: https://github.com/exasol/error-code-crawler-maven-plugin
[54]: https://maven.apache.org/plugins/maven-jar-plugin/
[52]: https://maven.apache.org/plugins/maven-assembly-plugin/
