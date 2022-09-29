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
| [Hamcrest][10]                                  | [BSD License 3][11]               |
| [JUnit Jupiter (Aggregator)][12]                | [Eclipse Public License v2.0][13] |
| [mockito-junit-jupiter][14]                     | [The MIT License][15]             |
| [EqualsVerifier | release normal jar][16]       | [Apache License, Version 2.0][17] |
| [Test containers for Exasol on Docker][18]      | [MIT License][19]                 |
| [Testcontainers :: JUnit Jupiter Extension][20] | [MIT][21]                         |
| [Testcontainers :: JDBC :: Oracle XE][20]       | [MIT][21]                         |
| [Test Database Builder for Java][24]            | [MIT License][25]                 |
| [udf-debugging-java][26]                        | [MIT][27]                         |
| [Matcher for SQL Result Sets][28]               | [MIT License][29]                 |
| [virtual-schema-shared-integration-tests][30]   | [MIT License][31]                 |
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
| [Artifact reference checker and unifier][59]            | [MIT][27]                                      |
| [Apache Maven Dependency Plugin][61]                    | [Apache License, Version 2.0][17]              |
| [Maven Failsafe Plugin][63]                             | [Apache License, Version 2.0][17]              |
| [JaCoCo :: Maven Plugin][65]                            | [Eclipse Public License 2.0][33]               |
| [error-code-crawler-maven-plugin][67]                   | [MIT][27]                                      |
| [Maven Clean Plugin][69]                                | [The Apache Software License, Version 2.0][44] |
| [Maven Resources Plugin][71]                            | [The Apache Software License, Version 2.0][44] |
| [Maven Install Plugin][73]                              | [The Apache Software License, Version 2.0][44] |
| [Maven Deploy Plugin][75]                               | [The Apache Software License, Version 2.0][44] |
| [Maven Site Plugin 3][77]                               | [The Apache Software License, Version 2.0][44] |

[32]: https://www.eclemma.org/jacoco/index.html
[44]: http://www.apache.org/licenses/LICENSE-2.0.txt
[49]: https://maven.apache.org/surefire/maven-surefire-plugin/
[69]: http://maven.apache.org/plugins/maven-clean-plugin/
[7]: https://www.oracle.com/downloads/licenses/oracle-free-license.html
[14]: https://github.com/mockito/mockito
[27]: https://opensource.org/licenses/MIT
[43]: https://www.mojohaus.org/flatten-maven-plugin/
[51]: http://www.mojohaus.org/versions-maven-plugin/
[53]: https://github.com/exasol/project-keeper/
[11]: http://opensource.org/licenses/BSD-3-Clause
[39]: https://maven.apache.org/plugins/maven-compiler-plugin/
[29]: https://github.com/exasol/hamcrest-resultset-matcher/blob/main/LICENSE
[25]: https://github.com/exasol/test-db-builder-java/blob/main/LICENSE
[4]: https://github.com/exasol/error-reporting-java/
[33]: https://www.eclipse.org/legal/epl-2.0/
[38]: http://www.gnu.org/licenses/lgpl.txt
[65]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[19]: https://github.com/exasol/exasol-testcontainers/blob/main/LICENSE
[15]: https://github.com/mockito/mockito/blob/main/LICENSE
[47]: http://zlika.github.io/reproducible-build-maven-plugin
[3]: https://github.com/exasol/db-fundamentals-java/blob/main/LICENSE
[31]: https://github.com/exasol/virtual-schema-shared-integration-tests/blob/main/LICENSE
[37]: http://sonarsource.github.io/sonar-scanner-maven/
[26]: https://github.com/exasol/udf-debugging-java/
[5]: https://github.com/exasol/error-reporting-java/blob/main/LICENSE
[12]: https://junit.org/junit5/
[0]: https://github.com/exasol/virtual-schema-common-jdbc/
[34]: https://github.com/eclipse-ee4j/jsonp
[10]: http://hamcrest.org/JavaHamcrest/
[36]: https://projects.eclipse.org/license/secondary-gpl-2.0-cp
[71]: http://maven.apache.org/plugins/maven-resources-plugin/
[59]: https://github.com/exasol/artifact-reference-checker-maven-plugin
[57]: https://maven.apache.org/plugins/maven-jar-plugin/
[30]: https://github.com/exasol/virtual-schema-shared-integration-tests/
[6]: https://www.oracle.com/database/technologies/maven-central-guide.html
[28]: https://github.com/exasol/hamcrest-resultset-matcher/
[24]: https://github.com/exasol/test-db-builder-java/
[63]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[21]: http://opensource.org/licenses/MIT
[2]: https://github.com/exasol/db-fundamentals-java/
[54]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[61]: https://maven.apache.org/plugins/maven-dependency-plugin/
[35]: https://projects.eclipse.org/license/epl-2.0
[17]: https://www.apache.org/licenses/LICENSE-2.0.txt
[16]: https://www.jqno.nl/equalsverifier
[41]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[13]: https://www.eclipse.org/legal/epl-v20.html
[1]: https://github.com/exasol/virtual-schema-common-jdbc/blob/main/LICENSE
[73]: http://maven.apache.org/plugins/maven-install-plugin/
[45]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[20]: https://testcontainers.org
[18]: https://github.com/exasol/exasol-testcontainers/
[75]: http://maven.apache.org/plugins/maven-deploy-plugin/
[77]: http://maven.apache.org/plugins/maven-site-plugin/
[67]: https://github.com/exasol/error-code-crawler-maven-plugin
[55]: https://maven.apache.org/plugins/maven-assembly-plugin/
