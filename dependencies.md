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
| [Test Database Builder for Java][24]            | [MIT License][25]                 |
| [udf-debugging-java][26]                        | [MIT][1]                          |
| [Matcher for SQL Result Sets][28]               | [MIT][1]                          |
| [virtual-schema-shared-integration-tests][30]   | [MIT][1]                          |
| [JaCoCo :: Agent][32]                           | [Eclipse Public License 2.0][33]  |

## Runtime Dependencies

| Dependency                    | License                                                                                                        |
| ----------------------------- | -------------------------------------------------------------------------------------------------------------- |
| [JSON-P Default Provider][34] | [Eclipse Public License 2.0][35]; [GNU General Public License, version 2 with the GNU Classpath Exception][36] |

## Plugin Dependencies

| Dependency                                              | License                                        |
| ------------------------------------------------------- | ---------------------------------------------- |
| [SonarQube Scanner for Maven][37]                       | [GNU LGPL 3][38]                               |
| [Apache Maven Compiler Plugin][39]                      | [Apache License, Version 2.0][17]              |
| [Apache Maven Enforcer Plugin][41]                      | [Apache License, Version 2.0][17]              |
| [Maven Flatten Plugin][43]                              | [Apache Software Licenese][44]                 |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][45] | [ASL2][44]                                     |
| [Reproducible Build Maven Plugin][47]                   | [Apache 2.0][44]                               |
| [Maven Surefire Plugin][49]                             | [Apache License, Version 2.0][17]              |
| [Versions Maven Plugin][51]                             | [Apache License, Version 2.0][17]              |
| [Project keeper maven plugin][53]                       | [The MIT License][54]                          |
| [Apache Maven Assembly Plugin][55]                      | [Apache License, Version 2.0][17]              |
| [Apache Maven JAR Plugin][57]                           | [Apache License, Version 2.0][17]              |
| [Artifact reference checker and unifier][59]            | [MIT][1]                                       |
| [Apache Maven Dependency Plugin][61]                    | [Apache License, Version 2.0][17]              |
| [Maven Failsafe Plugin][63]                             | [Apache License, Version 2.0][17]              |
| [JaCoCo :: Maven Plugin][65]                            | [Eclipse Public License 2.0][33]               |
| [error-code-crawler-maven-plugin][67]                   | [MIT][1]                                       |
| [Maven Clean Plugin][69]                                | [The Apache Software License, Version 2.0][44] |
| [Maven Resources Plugin][71]                            | [The Apache Software License, Version 2.0][44] |
| [Maven Install Plugin][73]                              | [The Apache Software License, Version 2.0][44] |
| [Maven Deploy Plugin][75]                               | [The Apache Software License, Version 2.0][44] |
| [Maven Site Plugin 3][77]                               | [The Apache Software License, Version 2.0][44] |

[32]: https://www.eclemma.org/jacoco/index.html
[4]: https://github.com/exasol/error-reporting-java
[44]: http://www.apache.org/licenses/LICENSE-2.0.txt
[49]: https://maven.apache.org/surefire/maven-surefire-plugin/
[69]: http://maven.apache.org/plugins/maven-clean-plugin/
[7]: https://www.oracle.com/downloads/licenses/oracle-free-license.html
[1]: https://opensource.org/licenses/MIT
[14]: https://github.com/mockito/mockito
[43]: https://www.mojohaus.org/flatten-maven-plugin/
[51]: http://www.mojohaus.org/versions-maven-plugin/
[53]: https://github.com/exasol/project-keeper/
[11]: http://opensource.org/licenses/BSD-3-Clause
[39]: https://maven.apache.org/plugins/maven-compiler-plugin/
[25]: https://github.com/exasol/test-db-builder-java/blob/main/LICENSE
[33]: https://www.eclipse.org/legal/epl-2.0/
[38]: http://www.gnu.org/licenses/lgpl.txt
[65]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[15]: https://github.com/mockito/mockito/blob/main/LICENSE
[28]: https://github.com/exasol/hamcrest-resultset-matcher
[47]: http://zlika.github.io/reproducible-build-maven-plugin
[37]: http://sonarsource.github.io/sonar-scanner-maven/
[26]: https://github.com/exasol/udf-debugging-java/
[12]: https://junit.org/junit5/
[34]: https://github.com/eclipse-ee4j/jsonp
[10]: http://hamcrest.org/JavaHamcrest/
[36]: https://projects.eclipse.org/license/secondary-gpl-2.0-cp
[71]: http://maven.apache.org/plugins/maven-resources-plugin/
[59]: https://github.com/exasol/artifact-reference-checker-maven-plugin
[57]: https://maven.apache.org/plugins/maven-jar-plugin/
[6]: https://www.oracle.com/database/technologies/maven-central-guide.html
[2]: https://github.com/exasol/db-fundamentals-java
[24]: https://github.com/exasol/test-db-builder-java/
[63]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[30]: https://github.com/exasol/virtual-schema-shared-integration-tests
[21]: http://opensource.org/licenses/MIT
[0]: https://github.com/exasol/virtual-schema-common-jdbc
[18]: https://github.com/exasol/exasol-testcontainers
[54]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[61]: https://maven.apache.org/plugins/maven-dependency-plugin/
[35]: https://projects.eclipse.org/license/epl-2.0
[17]: https://www.apache.org/licenses/LICENSE-2.0.txt
[16]: https://www.jqno.nl/equalsverifier
[41]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[13]: https://www.eclipse.org/legal/epl-v20.html
[73]: http://maven.apache.org/plugins/maven-install-plugin/
[45]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[20]: https://testcontainers.org
[75]: http://maven.apache.org/plugins/maven-deploy-plugin/
[77]: http://maven.apache.org/plugins/maven-site-plugin/
[67]: https://github.com/exasol/error-code-crawler-maven-plugin
[55]: https://maven.apache.org/plugins/maven-assembly-plugin/
