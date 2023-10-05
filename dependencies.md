<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                                 | License                                          |
| ------------------------------------------ | ------------------------------------------------ |
| [Virtual Schema Common JDBC][0]            | [MIT License][1]                                 |
| [Exasol Database fundamentals for Java][2] | [MIT License][3]                                 |
| [error-reporting-java][4]                  | [MIT License][5]                                 |
| [ojdbc8][6]                                | [Oracle Free Use Terms and Conditions (FUTC)][7] |

## Test Dependencies

| Dependency                                      | License                           |
| ----------------------------------------------- | --------------------------------- |
| [Virtual Schema Common JDBC][0]                 | [MIT License][1]                  |
| [Hamcrest][8]                                   | [BSD License 3][9]                |
| [JUnit Jupiter (Aggregator)][10]                | [Eclipse Public License v2.0][11] |
| [mockito-junit-jupiter][12]                     | [The MIT License][13]             |
| [EqualsVerifier \| release normal jar][14]      | [Apache License, Version 2.0][15] |
| [SLF4J JDK14 Provider][16]                      | [MIT License][17]                 |
| [Test containers for Exasol on Docker][18]      | [MIT License][19]                 |
| [Testcontainers :: JUnit Jupiter Extension][20] | [MIT][21]                         |
| [Testcontainers :: JDBC :: Oracle XE][20]       | [MIT][21]                         |
| [Test Database Builder for Java][22]            | [MIT License][23]                 |
| [udf-debugging-java][24]                        | [MIT License][25]                 |
| [Matcher for SQL Result Sets][26]               | [MIT License][27]                 |
| [virtual-schema-shared-integration-tests][28]   | [MIT License][29]                 |
| [JaCoCo :: Agent][30]                           | [Eclipse Public License 2.0][31]  |

## Runtime Dependencies

| Dependency                    | License                                                                                                        |
| ----------------------------- | -------------------------------------------------------------------------------------------------------------- |
| [JSON-P Default Provider][32] | [Eclipse Public License 2.0][33]; [GNU General Public License, version 2 with the GNU Classpath Exception][34] |

## Plugin Dependencies

| Dependency                                              | License                                        |
| ------------------------------------------------------- | ---------------------------------------------- |
| [SonarQube Scanner for Maven][35]                       | [GNU LGPL 3][36]                               |
| [Apache Maven Compiler Plugin][37]                      | [Apache-2.0][15]                               |
| [Apache Maven Enforcer Plugin][38]                      | [Apache-2.0][15]                               |
| [Maven Flatten Plugin][39]                              | [Apache Software Licenese][15]                 |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][40] | [ASL2][41]                                     |
| [Maven Surefire Plugin][42]                             | [Apache-2.0][15]                               |
| [Versions Maven Plugin][43]                             | [Apache License, Version 2.0][15]              |
| [duplicate-finder-maven-plugin Maven Mojo][44]          | [Apache License 2.0][45]                       |
| [Project keeper maven plugin][46]                       | [The MIT License][47]                          |
| [Apache Maven Assembly Plugin][48]                      | [Apache-2.0][15]                               |
| [Apache Maven JAR Plugin][49]                           | [Apache License, Version 2.0][15]              |
| [Artifact reference checker and unifier][50]            | [MIT License][51]                              |
| [Apache Maven Dependency Plugin][52]                    | [Apache-2.0][15]                               |
| [Maven Failsafe Plugin][53]                             | [Apache-2.0][15]                               |
| [JaCoCo :: Maven Plugin][54]                            | [Eclipse Public License 2.0][31]               |
| [error-code-crawler-maven-plugin][55]                   | [MIT License][56]                              |
| [Reproducible Build Maven Plugin][57]                   | [Apache 2.0][41]                               |
| [Maven Clean Plugin][58]                                | [The Apache Software License, Version 2.0][41] |
| [Maven Resources Plugin][59]                            | [The Apache Software License, Version 2.0][41] |
| [Maven Install Plugin][60]                              | [The Apache Software License, Version 2.0][41] |
| [Maven Deploy Plugin][61]                               | [The Apache Software License, Version 2.0][41] |
| [Maven Site Plugin 3][62]                               | [The Apache Software License, Version 2.0][41] |

[0]: https://github.com/exasol/virtual-schema-common-jdbc/
[1]: https://github.com/exasol/virtual-schema-common-jdbc/blob/main/LICENSE
[2]: https://github.com/exasol/db-fundamentals-java/
[3]: https://github.com/exasol/db-fundamentals-java/blob/main/LICENSE
[4]: https://github.com/exasol/error-reporting-java/
[5]: https://github.com/exasol/error-reporting-java/blob/main/LICENSE
[6]: https://www.oracle.com/database/technologies/maven-central-guide.html
[7]: https://www.oracle.com/downloads/licenses/oracle-free-license.html
[8]: http://hamcrest.org/JavaHamcrest/
[9]: http://opensource.org/licenses/BSD-3-Clause
[10]: https://junit.org/junit5/
[11]: https://www.eclipse.org/legal/epl-v20.html
[12]: https://github.com/mockito/mockito
[13]: https://github.com/mockito/mockito/blob/main/LICENSE
[14]: https://www.jqno.nl/equalsverifier
[15]: https://www.apache.org/licenses/LICENSE-2.0.txt
[16]: http://www.slf4j.org
[17]: http://www.opensource.org/licenses/mit-license.php
[18]: https://github.com/exasol/exasol-testcontainers/
[19]: https://github.com/exasol/exasol-testcontainers/blob/main/LICENSE
[20]: https://java.testcontainers.org
[21]: http://opensource.org/licenses/MIT
[22]: https://github.com/exasol/test-db-builder-java/
[23]: https://github.com/exasol/test-db-builder-java/blob/main/LICENSE
[24]: https://github.com/exasol/udf-debugging-java/
[25]: https://github.com/exasol/udf-debugging-java/blob/main/LICENSE
[26]: https://github.com/exasol/hamcrest-resultset-matcher/
[27]: https://github.com/exasol/hamcrest-resultset-matcher/blob/main/LICENSE
[28]: https://github.com/exasol/virtual-schema-shared-integration-tests/
[29]: https://github.com/exasol/virtual-schema-shared-integration-tests/blob/main/LICENSE
[30]: https://www.eclemma.org/jacoco/index.html
[31]: https://www.eclipse.org/legal/epl-2.0/
[32]: https://github.com/eclipse-ee4j/jsonp
[33]: https://projects.eclipse.org/license/epl-2.0
[34]: https://projects.eclipse.org/license/secondary-gpl-2.0-cp
[35]: http://sonarsource.github.io/sonar-scanner-maven/
[36]: http://www.gnu.org/licenses/lgpl.txt
[37]: https://maven.apache.org/plugins/maven-compiler-plugin/
[38]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[39]: https://www.mojohaus.org/flatten-maven-plugin/
[40]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[41]: http://www.apache.org/licenses/LICENSE-2.0.txt
[42]: https://maven.apache.org/surefire/maven-surefire-plugin/
[43]: https://www.mojohaus.org/versions/versions-maven-plugin/
[44]: https://basepom.github.io/duplicate-finder-maven-plugin
[45]: http://www.apache.org/licenses/LICENSE-2.0.html
[46]: https://github.com/exasol/project-keeper/
[47]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[48]: https://maven.apache.org/plugins/maven-assembly-plugin/
[49]: https://maven.apache.org/plugins/maven-jar-plugin/
[50]: https://github.com/exasol/artifact-reference-checker-maven-plugin/
[51]: https://github.com/exasol/artifact-reference-checker-maven-plugin/blob/main/LICENSE
[52]: https://maven.apache.org/plugins/maven-dependency-plugin/
[53]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[54]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[55]: https://github.com/exasol/error-code-crawler-maven-plugin/
[56]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[57]: http://zlika.github.io/reproducible-build-maven-plugin
[58]: http://maven.apache.org/plugins/maven-clean-plugin/
[59]: http://maven.apache.org/plugins/maven-resources-plugin/
[60]: http://maven.apache.org/plugins/maven-install-plugin/
[61]: http://maven.apache.org/plugins/maven-deploy-plugin/
[62]: http://maven.apache.org/plugins/maven-site-plugin/
