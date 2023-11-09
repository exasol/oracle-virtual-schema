<!-- @formatter:off -->
# Dependencies

## Virtual Schema for Oracle

### Compile Dependencies

| Dependency                                 | License          |
| ------------------------------------------ | ---------------- |
| [Virtual Schema Common JDBC][0]            | [MIT License][1] |
| [Exasol Database fundamentals for Java][2] | [MIT License][3] |
| [error-reporting-java][4]                  | [MIT License][5] |

### Test Dependencies

| Dependency                                      | License                                           |
| ----------------------------------------------- | ------------------------------------------------- |
| [Virtual Schema Common JDBC][0]                 | [MIT License][1]                                  |
| [Hamcrest][6]                                   | [BSD License 3][7]                                |
| [JUnit Jupiter (Aggregator)][8]                 | [Eclipse Public License v2.0][9]                  |
| [mockito-junit-jupiter][10]                     | [MIT][11]                                         |
| [EqualsVerifier \| release normal jar][12]      | [Apache License, Version 2.0][13]                 |
| [SLF4J JDK14 Provider][14]                      | [MIT License][15]                                 |
| [Test containers for Exasol on Docker][16]      | [MIT License][17]                                 |
| [Testcontainers :: JUnit Jupiter Extension][18] | [MIT][19]                                         |
| [Testcontainers :: JDBC :: Oracle XE][18]       | [MIT][19]                                         |
| [ojdbc8][20]                                    | [Oracle Free Use Terms and Conditions (FUTC)][21] |
| [Test Database Builder for Java][22]            | [MIT License][23]                                 |
| [udf-debugging-java][24]                        | [MIT License][25]                                 |
| [Matcher for SQL Result Sets][26]               | [MIT License][27]                                 |
| [virtual-schema-shared-integration-tests][28]   | [MIT License][29]                                 |
| [Extension integration tests library][30]       | [MIT License][31]                                 |
| [JaCoCo :: Agent][32]                           | [Eclipse Public License 2.0][33]                  |

### Runtime Dependencies

| Dependency                    | License                                                                                                        |
| ----------------------------- | -------------------------------------------------------------------------------------------------------------- |
| [JSON-P Default Provider][34] | [Eclipse Public License 2.0][35]; [GNU General Public License, version 2 with the GNU Classpath Exception][36] |

### Plugin Dependencies

| Dependency                                              | License                           |
| ------------------------------------------------------- | --------------------------------- |
| [SonarQube Scanner for Maven][37]                       | [GNU LGPL 3][38]                  |
| [Apache Maven Compiler Plugin][39]                      | [Apache-2.0][13]                  |
| [Apache Maven Enforcer Plugin][40]                      | [Apache-2.0][13]                  |
| [Maven Flatten Plugin][41]                              | [Apache Software Licenese][13]    |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][42] | [ASL2][43]                        |
| [Maven Surefire Plugin][44]                             | [Apache-2.0][13]                  |
| [Versions Maven Plugin][45]                             | [Apache License, Version 2.0][13] |
| [duplicate-finder-maven-plugin Maven Mojo][46]          | [Apache License 2.0][47]          |
| [Project keeper maven plugin][48]                       | [The MIT License][49]             |
| [Apache Maven Assembly Plugin][50]                      | [Apache-2.0][13]                  |
| [Apache Maven JAR Plugin][51]                           | [Apache License, Version 2.0][13] |
| [Artifact reference checker and unifier][52]            | [MIT License][53]                 |
| [Apache Maven Dependency Plugin][54]                    | [Apache-2.0][13]                  |
| [Maven Failsafe Plugin][55]                             | [Apache-2.0][13]                  |
| [JaCoCo :: Maven Plugin][56]                            | [Eclipse Public License 2.0][33]  |
| [error-code-crawler-maven-plugin][57]                   | [MIT License][58]                 |
| [Reproducible Build Maven Plugin][59]                   | [Apache 2.0][43]                  |
| [Apache Maven Clean Plugin][60]                         | [Apache-2.0][13]                  |
| [Exec Maven Plugin][61]                                 | [Apache License 2][13]            |

## Extension

### Compile Dependencies

| Dependency                                | License |
| ----------------------------------------- | ------- |
| [@exasol/extension-manager-interface][62] | MIT     |

[0]: https://github.com/exasol/virtual-schema-common-jdbc/
[1]: https://github.com/exasol/virtual-schema-common-jdbc/blob/main/LICENSE
[2]: https://github.com/exasol/db-fundamentals-java/
[3]: https://github.com/exasol/db-fundamentals-java/blob/main/LICENSE
[4]: https://github.com/exasol/error-reporting-java/
[5]: https://github.com/exasol/error-reporting-java/blob/main/LICENSE
[6]: http://hamcrest.org/JavaHamcrest/
[7]: http://opensource.org/licenses/BSD-3-Clause
[8]: https://junit.org/junit5/
[9]: https://www.eclipse.org/legal/epl-v20.html
[10]: https://github.com/mockito/mockito
[11]: https://opensource.org/licenses/MIT
[12]: https://www.jqno.nl/equalsverifier
[13]: https://www.apache.org/licenses/LICENSE-2.0.txt
[14]: http://www.slf4j.org
[15]: http://www.opensource.org/licenses/mit-license.php
[16]: https://github.com/exasol/exasol-testcontainers/
[17]: https://github.com/exasol/exasol-testcontainers/blob/main/LICENSE
[18]: https://java.testcontainers.org
[19]: http://opensource.org/licenses/MIT
[20]: https://www.oracle.com/database/technologies/maven-central-guide.html
[21]: https://www.oracle.com/downloads/licenses/oracle-free-license.html
[22]: https://github.com/exasol/test-db-builder-java/
[23]: https://github.com/exasol/test-db-builder-java/blob/main/LICENSE
[24]: https://github.com/exasol/udf-debugging-java/
[25]: https://github.com/exasol/udf-debugging-java/blob/main/LICENSE
[26]: https://github.com/exasol/hamcrest-resultset-matcher/
[27]: https://github.com/exasol/hamcrest-resultset-matcher/blob/main/LICENSE
[28]: https://github.com/exasol/virtual-schema-shared-integration-tests/
[29]: https://github.com/exasol/virtual-schema-shared-integration-tests/blob/main/LICENSE
[30]: https://github.com/exasol/extension-manager/
[31]: https://github.com/exasol/extension-manager/blob/main/LICENSE
[32]: https://www.eclemma.org/jacoco/index.html
[33]: https://www.eclipse.org/legal/epl-2.0/
[34]: https://github.com/eclipse-ee4j/jsonp
[35]: https://projects.eclipse.org/license/epl-2.0
[36]: https://projects.eclipse.org/license/secondary-gpl-2.0-cp
[37]: http://sonarsource.github.io/sonar-scanner-maven/
[38]: http://www.gnu.org/licenses/lgpl.txt
[39]: https://maven.apache.org/plugins/maven-compiler-plugin/
[40]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[41]: https://www.mojohaus.org/flatten-maven-plugin/
[42]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[43]: http://www.apache.org/licenses/LICENSE-2.0.txt
[44]: https://maven.apache.org/surefire/maven-surefire-plugin/
[45]: https://www.mojohaus.org/versions/versions-maven-plugin/
[46]: https://basepom.github.io/duplicate-finder-maven-plugin
[47]: http://www.apache.org/licenses/LICENSE-2.0.html
[48]: https://github.com/exasol/project-keeper/
[49]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[50]: https://maven.apache.org/plugins/maven-assembly-plugin/
[51]: https://maven.apache.org/plugins/maven-jar-plugin/
[52]: https://github.com/exasol/artifact-reference-checker-maven-plugin/
[53]: https://github.com/exasol/artifact-reference-checker-maven-plugin/blob/main/LICENSE
[54]: https://maven.apache.org/plugins/maven-dependency-plugin/
[55]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[56]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[57]: https://github.com/exasol/error-code-crawler-maven-plugin/
[58]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[59]: http://zlika.github.io/reproducible-build-maven-plugin
[60]: https://maven.apache.org/plugins/maven-clean-plugin/
[61]: https://www.mojohaus.org/exec-maven-plugin
[62]: https://registry.npmjs.org/@exasol/extension-manager-interface/-/extension-manager-interface-0.4.0.tgz
