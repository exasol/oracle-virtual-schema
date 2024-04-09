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
| [Maven Project Version Getter][6]               | [MIT License][7]                                  |
| [Virtual Schema Common JDBC][0]                 | [MIT License][1]                                  |
| [Hamcrest][8]                                   | [BSD License 3][9]                                |
| [JUnit Jupiter (Aggregator)][10]                | [Eclipse Public License v2.0][11]                 |
| [mockito-junit-jupiter][12]                     | [MIT][13]                                         |
| [EqualsVerifier \| release normal jar][14]      | [Apache License, Version 2.0][15]                 |
| [SLF4J JDK14 Provider][16]                      | [MIT License][17]                                 |
| [Test containers for Exasol on Docker][18]      | [MIT License][19]                                 |
| [Testcontainers :: JUnit Jupiter Extension][20] | [MIT][21]                                         |
| [Testcontainers :: JDBC :: Oracle XE][20]       | [MIT][21]                                         |
| [ojdbc8][22]                                    | [Oracle Free Use Terms and Conditions (FUTC)][23] |
| [Test Database Builder for Java][24]            | [MIT License][25]                                 |
| [udf-debugging-java][26]                        | [MIT License][27]                                 |
| [Matcher for SQL Result Sets][28]               | [MIT License][29]                                 |
| [virtual-schema-shared-integration-tests][30]   | [MIT License][31]                                 |
| [Extension integration tests library][32]       | [MIT License][33]                                 |
| [JaCoCo :: Agent][34]                           | [Eclipse Public License 2.0][35]                  |

### Runtime Dependencies

| Dependency                    | License                                                                                                        |
| ----------------------------- | -------------------------------------------------------------------------------------------------------------- |
| [JSON-P Default Provider][36] | [Eclipse Public License 2.0][37]; [GNU General Public License, version 2 with the GNU Classpath Exception][38] |

### Plugin Dependencies

| Dependency                                              | License                           |
| ------------------------------------------------------- | --------------------------------- |
| [SonarQube Scanner for Maven][39]                       | [GNU LGPL 3][40]                  |
| [Apache Maven Toolchains Plugin][41]                    | [Apache License, Version 2.0][15] |
| [Apache Maven Compiler Plugin][42]                      | [Apache-2.0][15]                  |
| [Apache Maven Enforcer Plugin][43]                      | [Apache-2.0][15]                  |
| [Maven Flatten Plugin][44]                              | [Apache Software Licenese][15]    |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][45] | [ASL2][46]                        |
| [Maven Surefire Plugin][47]                             | [Apache-2.0][15]                  |
| [Versions Maven Plugin][48]                             | [Apache License, Version 2.0][15] |
| [duplicate-finder-maven-plugin Maven Mojo][49]          | [Apache License 2.0][50]          |
| [Project Keeper Maven plugin][51]                       | [The MIT License][52]             |
| [Apache Maven Assembly Plugin][53]                      | [Apache-2.0][15]                  |
| [Apache Maven JAR Plugin][54]                           | [Apache License, Version 2.0][15] |
| [Artifact reference checker and unifier][55]            | [MIT License][56]                 |
| [Apache Maven Dependency Plugin][57]                    | [Apache-2.0][15]                  |
| [Maven Failsafe Plugin][58]                             | [Apache-2.0][15]                  |
| [JaCoCo :: Maven Plugin][59]                            | [EPL-2.0][35]                     |
| [error-code-crawler-maven-plugin][60]                   | [MIT License][61]                 |
| [Reproducible Build Maven Plugin][62]                   | [Apache 2.0][46]                  |
| [Apache Maven Clean Plugin][63]                         | [Apache-2.0][15]                  |
| [Exec Maven Plugin][64]                                 | [Apache License 2][15]            |

## Extension

### Compile Dependencies

| Dependency                                | License |
| ----------------------------------------- | ------- |
| [@exasol/extension-manager-interface][65] | MIT     |

[0]: https://github.com/exasol/virtual-schema-common-jdbc/
[1]: https://github.com/exasol/virtual-schema-common-jdbc/blob/main/LICENSE
[2]: https://github.com/exasol/db-fundamentals-java/
[3]: https://github.com/exasol/db-fundamentals-java/blob/main/LICENSE
[4]: https://github.com/exasol/error-reporting-java/
[5]: https://github.com/exasol/error-reporting-java/blob/main/LICENSE
[6]: https://github.com/exasol/maven-project-version-getter/
[7]: https://github.com/exasol/maven-project-version-getter/blob/main/LICENSE
[8]: http://hamcrest.org/JavaHamcrest/
[9]: http://opensource.org/licenses/BSD-3-Clause
[10]: https://junit.org/junit5/
[11]: https://www.eclipse.org/legal/epl-v20.html
[12]: https://github.com/mockito/mockito
[13]: https://opensource.org/licenses/MIT
[14]: https://www.jqno.nl/equalsverifier
[15]: https://www.apache.org/licenses/LICENSE-2.0.txt
[16]: http://www.slf4j.org
[17]: http://www.opensource.org/licenses/mit-license.php
[18]: https://github.com/exasol/exasol-testcontainers/
[19]: https://github.com/exasol/exasol-testcontainers/blob/main/LICENSE
[20]: https://java.testcontainers.org
[21]: http://opensource.org/licenses/MIT
[22]: https://www.oracle.com/database/technologies/maven-central-guide.html
[23]: https://www.oracle.com/downloads/licenses/oracle-free-license.html
[24]: https://github.com/exasol/test-db-builder-java/
[25]: https://github.com/exasol/test-db-builder-java/blob/main/LICENSE
[26]: https://github.com/exasol/udf-debugging-java/
[27]: https://github.com/exasol/udf-debugging-java/blob/main/LICENSE
[28]: https://github.com/exasol/hamcrest-resultset-matcher/
[29]: https://github.com/exasol/hamcrest-resultset-matcher/blob/main/LICENSE
[30]: https://github.com/exasol/virtual-schema-shared-integration-tests/
[31]: https://github.com/exasol/virtual-schema-shared-integration-tests/blob/main/LICENSE
[32]: https://github.com/exasol/extension-manager/
[33]: https://github.com/exasol/extension-manager/blob/main/LICENSE
[34]: https://www.eclemma.org/jacoco/index.html
[35]: https://www.eclipse.org/legal/epl-2.0/
[36]: https://github.com/eclipse-ee4j/jsonp
[37]: https://projects.eclipse.org/license/epl-2.0
[38]: https://projects.eclipse.org/license/secondary-gpl-2.0-cp
[39]: http://sonarsource.github.io/sonar-scanner-maven/
[40]: http://www.gnu.org/licenses/lgpl.txt
[41]: https://maven.apache.org/plugins/maven-toolchains-plugin/
[42]: https://maven.apache.org/plugins/maven-compiler-plugin/
[43]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[44]: https://www.mojohaus.org/flatten-maven-plugin/
[45]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[46]: http://www.apache.org/licenses/LICENSE-2.0.txt
[47]: https://maven.apache.org/surefire/maven-surefire-plugin/
[48]: https://www.mojohaus.org/versions/versions-maven-plugin/
[49]: https://basepom.github.io/duplicate-finder-maven-plugin
[50]: http://www.apache.org/licenses/LICENSE-2.0.html
[51]: https://github.com/exasol/project-keeper/
[52]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[53]: https://maven.apache.org/plugins/maven-assembly-plugin/
[54]: https://maven.apache.org/plugins/maven-jar-plugin/
[55]: https://github.com/exasol/artifact-reference-checker-maven-plugin/
[56]: https://github.com/exasol/artifact-reference-checker-maven-plugin/blob/main/LICENSE
[57]: https://maven.apache.org/plugins/maven-dependency-plugin/
[58]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[59]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[60]: https://github.com/exasol/error-code-crawler-maven-plugin/
[61]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[62]: http://zlika.github.io/reproducible-build-maven-plugin
[63]: https://maven.apache.org/plugins/maven-clean-plugin/
[64]: https://www.mojohaus.org/exec-maven-plugin
[65]: https://registry.npmjs.org/@exasol/extension-manager-interface/-/extension-manager-interface-0.4.1.tgz
