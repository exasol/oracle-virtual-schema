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
| [SLF4J JDK14 Binding][18]                       | [MIT License][19]                 |
| [Test containers for Exasol on Docker][20]      | [MIT License][21]                 |
| [Testcontainers :: JUnit Jupiter Extension][22] | [MIT][23]                         |
| [Testcontainers :: JDBC :: Oracle XE][22]       | [MIT][23]                         |
| [Test Database Builder for Java][26]            | [MIT License][27]                 |
| [udf-debugging-java][28]                        | [MIT License][29]                 |
| [Matcher for SQL Result Sets][30]               | [MIT License][31]                 |
| [virtual-schema-shared-integration-tests][32]   | [MIT License][33]                 |
| [JaCoCo :: Agent][34]                           | [Eclipse Public License 2.0][35]  |

## Runtime Dependencies

| Dependency                    | License                                                                                                        |
| ----------------------------- | -------------------------------------------------------------------------------------------------------------- |
| [JSON-P Default Provider][36] | [Eclipse Public License 2.0][37]; [GNU General Public License, version 2 with the GNU Classpath Exception][38] |

## Plugin Dependencies

| Dependency                                              | License                                        |
| ------------------------------------------------------- | ---------------------------------------------- |
| [SonarQube Scanner for Maven][39]                       | [GNU LGPL 3][40]                               |
| [Apache Maven Compiler Plugin][41]                      | [Apache License, Version 2.0][17]              |
| [Apache Maven Enforcer Plugin][43]                      | [Apache License, Version 2.0][17]              |
| [Maven Flatten Plugin][45]                              | [Apache Software Licenese][46]                 |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][47] | [ASL2][46]                                     |
| [Reproducible Build Maven Plugin][49]                   | [Apache 2.0][46]                               |
| [Maven Surefire Plugin][51]                             | [Apache License, Version 2.0][17]              |
| [Versions Maven Plugin][53]                             | [Apache License, Version 2.0][17]              |
| [Project keeper maven plugin][55]                       | [The MIT License][56]                          |
| [Apache Maven Assembly Plugin][57]                      | [Apache License, Version 2.0][17]              |
| [Apache Maven JAR Plugin][59]                           | [Apache License, Version 2.0][17]              |
| [Artifact reference checker and unifier][61]            | [MIT][62]                                      |
| [Apache Maven Dependency Plugin][63]                    | [Apache License, Version 2.0][17]              |
| [Maven Failsafe Plugin][65]                             | [Apache License, Version 2.0][17]              |
| [JaCoCo :: Maven Plugin][67]                            | [Eclipse Public License 2.0][35]               |
| [error-code-crawler-maven-plugin][69]                   | [MIT][62]                                      |
| [Maven Clean Plugin][71]                                | [The Apache Software License, Version 2.0][46] |
| [Maven Resources Plugin][73]                            | [The Apache Software License, Version 2.0][46] |
| [Maven Install Plugin][75]                              | [The Apache Software License, Version 2.0][46] |
| [Maven Deploy Plugin][77]                               | [The Apache Software License, Version 2.0][46] |
| [Maven Site Plugin 3][79]                               | [The Apache Software License, Version 2.0][46] |

[34]: https://www.eclemma.org/jacoco/index.html
[46]: http://www.apache.org/licenses/LICENSE-2.0.txt
[51]: https://maven.apache.org/surefire/maven-surefire-plugin/
[71]: http://maven.apache.org/plugins/maven-clean-plugin/
[7]: https://www.oracle.com/downloads/licenses/oracle-free-license.html
[14]: https://github.com/mockito/mockito
[62]: https://opensource.org/licenses/MIT
[45]: https://www.mojohaus.org/flatten-maven-plugin/
[53]: http://www.mojohaus.org/versions-maven-plugin/
[55]: https://github.com/exasol/project-keeper/
[11]: http://opensource.org/licenses/BSD-3-Clause
[41]: https://maven.apache.org/plugins/maven-compiler-plugin/
[31]: https://github.com/exasol/hamcrest-resultset-matcher/blob/main/LICENSE
[27]: https://github.com/exasol/test-db-builder-java/blob/main/LICENSE
[4]: https://github.com/exasol/error-reporting-java/
[35]: https://www.eclipse.org/legal/epl-2.0/
[40]: http://www.gnu.org/licenses/lgpl.txt
[67]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[21]: https://github.com/exasol/exasol-testcontainers/blob/main/LICENSE
[15]: https://github.com/mockito/mockito/blob/main/LICENSE
[49]: http://zlika.github.io/reproducible-build-maven-plugin
[3]: https://github.com/exasol/db-fundamentals-java/blob/main/LICENSE
[19]: http://www.opensource.org/licenses/mit-license.php
[33]: https://github.com/exasol/virtual-schema-shared-integration-tests/blob/main/LICENSE
[39]: http://sonarsource.github.io/sonar-scanner-maven/
[28]: https://github.com/exasol/udf-debugging-java/
[5]: https://github.com/exasol/error-reporting-java/blob/main/LICENSE
[12]: https://junit.org/junit5/
[0]: https://github.com/exasol/virtual-schema-common-jdbc/
[36]: https://github.com/eclipse-ee4j/jsonp
[10]: http://hamcrest.org/JavaHamcrest/
[38]: https://projects.eclipse.org/license/secondary-gpl-2.0-cp
[18]: http://www.slf4j.org
[73]: http://maven.apache.org/plugins/maven-resources-plugin/
[61]: https://github.com/exasol/artifact-reference-checker-maven-plugin
[59]: https://maven.apache.org/plugins/maven-jar-plugin/
[32]: https://github.com/exasol/virtual-schema-shared-integration-tests/
[6]: https://www.oracle.com/database/technologies/maven-central-guide.html
[30]: https://github.com/exasol/hamcrest-resultset-matcher/
[26]: https://github.com/exasol/test-db-builder-java/
[65]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[23]: http://opensource.org/licenses/MIT
[2]: https://github.com/exasol/db-fundamentals-java/
[56]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[63]: https://maven.apache.org/plugins/maven-dependency-plugin/
[37]: https://projects.eclipse.org/license/epl-2.0
[17]: https://www.apache.org/licenses/LICENSE-2.0.txt
[16]: https://www.jqno.nl/equalsverifier
[43]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[13]: https://www.eclipse.org/legal/epl-v20.html
[1]: https://github.com/exasol/virtual-schema-common-jdbc/blob/main/LICENSE
[75]: http://maven.apache.org/plugins/maven-install-plugin/
[47]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[22]: https://testcontainers.org
[20]: https://github.com/exasol/exasol-testcontainers/
[77]: http://maven.apache.org/plugins/maven-deploy-plugin/
[79]: http://maven.apache.org/plugins/maven-site-plugin/
[69]: https://github.com/exasol/error-code-crawler-maven-plugin
[29]: https://github.com/exasol/udf-debugging-java/blob/main/LICENSE
[57]: https://maven.apache.org/plugins/maven-assembly-plugin/
