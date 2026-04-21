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
| [Hamcrest][8]                                   | [BSD-3-Clause][9]                                 |
| [JUnit Jupiter API][10]                         | [Eclipse Public License v2.0][11]                 |
| [JUnit Jupiter Params][10]                      | [Eclipse Public License v2.0][11]                 |
| [mockito-junit-jupiter][12]                     | [MIT][13]                                         |
| [EqualsVerifier \| release normal jar][14]      | [Apache License, Version 2.0][15]                 |
| [SLF4J JDK14 Provider][16]                      | [MIT][17]                                         |
| [Test containers for Exasol on Docker][18]      | [MIT License][19]                                 |
| [Testcontainers :: JUnit Jupiter Extension][20] | [MIT][21]                                         |
| [Testcontainers :: JDBC :: Oracle XE][20]       | [MIT][21]                                         |
| [ojdbc8][22]                                    | [Oracle Free Use Terms and Conditions (FUTC)][23] |
| test-db-builder-java                            |                                                   |
| [udf-debugging-java][24]                        | [MIT License][25]                                 |
| [Matcher for SQL Result Sets][26]               | [MIT License][27]                                 |
| [virtual-schema-shared-integration-tests][28]   | [MIT License][29]                                 |
| [Extension integration tests library][30]       | [MIT License][31]                                 |
| [JaCoCo :: Agent][32]                           | [EPL-2.0][33]                                     |

### Runtime Dependencies

| Dependency                    | License                                                                                                        |
| ----------------------------- | -------------------------------------------------------------------------------------------------------------- |
| [JSON-P Default Provider][34] | [Eclipse Public License 2.0][35]; [GNU General Public License, version 2 with the GNU Classpath Exception][36] |

### Plugin Dependencies

| Dependency                                              | License                                     |
| ------------------------------------------------------- | ------------------------------------------- |
| [Apache Maven Clean Plugin][37]                         | [Apache-2.0][15]                            |
| [Apache Maven Install Plugin][38]                       | [Apache-2.0][15]                            |
| [Apache Maven Resources Plugin][39]                     | [Apache-2.0][15]                            |
| [Apache Maven Site Plugin][40]                          | [Apache-2.0][15]                            |
| [SonarQube Scanner for Maven][41]                       | [GNU LGPL 3][42]                            |
| [Apache Maven Toolchains Plugin][43]                    | [Apache-2.0][15]                            |
| [Apache Maven Compiler Plugin][44]                      | [Apache-2.0][15]                            |
| [Apache Maven Enforcer Plugin][45]                      | [Apache-2.0][15]                            |
| [Maven Flatten Plugin][46]                              | [Apache Software License][15]               |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][47] | [ASL2][48]                                  |
| [Maven Surefire Plugin][49]                             | [Apache-2.0][15]                            |
| [Versions Maven Plugin][50]                             | [Apache License, Version 2.0][15]           |
| [duplicate-finder-maven-plugin Maven Mojo][51]          | [Apache License 2.0][52]                    |
| [Apache Maven Artifact Plugin][53]                      | [Apache-2.0][15]                            |
| [Project Keeper Maven plugin][54]                       | [The MIT License][55]                       |
| [Apache Maven Assembly Plugin][56]                      | [Apache-2.0][15]                            |
| [Apache Maven JAR Plugin][57]                           | [Apache-2.0][15]                            |
| [Artifact reference checker and unifier][58]            | [MIT License][59]                           |
| [Apache Maven Dependency Plugin][60]                    | [Apache-2.0][15]                            |
| [Maven Failsafe Plugin][61]                             | [Apache-2.0][15]                            |
| [JaCoCo :: Maven Plugin][62]                            | [EPL-2.0][33]                               |
| [Quality Summarizer Maven Plugin][63]                   | [MIT License][64]                           |
| [error-code-crawler-maven-plugin][65]                   | [MIT License][66]                           |
| [Git Commit Id Maven Plugin][67]                        | [GNU Lesser General Public License 3.0][68] |
| [Exec Maven Plugin][69]                                 | [Apache License 2][15]                      |

## Extension

### Compile Dependencies

| Dependency                                | License |
| ----------------------------------------- | ------- |
| [@exasol/extension-manager-interface][70] | MIT     |

[0]: https://github.com/exasol/virtual-schema-common-jdbc/
[1]: https://github.com/exasol/virtual-schema-common-jdbc/blob/main/LICENSE
[2]: https://github.com/exasol/db-fundamentals-java/
[3]: https://github.com/exasol/db-fundamentals-java/blob/main/LICENSE
[4]: https://github.com/exasol/error-reporting-java/
[5]: https://github.com/exasol/error-reporting-java/blob/main/LICENSE
[6]: https://github.com/exasol/maven-project-version-getter/
[7]: https://github.com/exasol/maven-project-version-getter/blob/main/LICENSE
[8]: http://hamcrest.org/JavaHamcrest/
[9]: https://raw.githubusercontent.com/hamcrest/JavaHamcrest/master/LICENSE
[10]: https://junit.org/
[11]: https://www.eclipse.org/legal/epl-v20.html
[12]: https://github.com/mockito/mockito
[13]: https://opensource.org/licenses/MIT
[14]: https://www.jqno.nl/equalsverifier
[15]: https://www.apache.org/licenses/LICENSE-2.0.txt
[16]: http://www.slf4j.org
[17]: https://opensource.org/license/mit
[18]: https://github.com/exasol/exasol-testcontainers/
[19]: https://github.com/exasol/exasol-testcontainers/blob/main/LICENSE
[20]: https://java.testcontainers.org
[21]: http://opensource.org/licenses/MIT
[22]: https://www.oracle.com/database/technologies/maven-central-guide.html
[23]: https://www.oracle.com/downloads/licenses/oracle-free-license.html
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
[37]: https://maven.apache.org/plugins/maven-clean-plugin/
[38]: https://maven.apache.org/plugins/maven-install-plugin/
[39]: https://maven.apache.org/plugins/maven-resources-plugin/
[40]: https://maven.apache.org/plugins/maven-site-plugin/
[41]: https://docs.sonarsource.com/sonarqube-server/latest/extension-guide/developing-a-plugin/plugin-basics/sonar-scanner-maven/sonar-maven-plugin/
[42]: http://www.gnu.org/licenses/lgpl.txt
[43]: https://maven.apache.org/plugins/maven-toolchains-plugin/
[44]: https://maven.apache.org/plugins/maven-compiler-plugin/
[45]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[46]: https://www.mojohaus.org/flatten-maven-plugin/
[47]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[48]: http://www.apache.org/licenses/LICENSE-2.0.txt
[49]: https://maven.apache.org/surefire/maven-surefire-plugin/
[50]: https://www.mojohaus.org/versions/versions-maven-plugin/
[51]: https://basepom.github.io/duplicate-finder-maven-plugin
[52]: http://www.apache.org/licenses/LICENSE-2.0.html
[53]: https://maven.apache.org/plugins/maven-artifact-plugin/
[54]: https://github.com/exasol/project-keeper/
[55]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[56]: https://maven.apache.org/plugins/maven-assembly-plugin/
[57]: https://maven.apache.org/plugins/maven-jar-plugin/
[58]: https://github.com/exasol/artifact-reference-checker-maven-plugin/
[59]: https://github.com/exasol/artifact-reference-checker-maven-plugin/blob/main/LICENSE
[60]: https://maven.apache.org/plugins/maven-dependency-plugin/
[61]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[62]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[63]: https://github.com/exasol/quality-summarizer-maven-plugin/
[64]: https://github.com/exasol/quality-summarizer-maven-plugin/blob/main/LICENSE
[65]: https://github.com/exasol/error-code-crawler-maven-plugin/
[66]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[67]: https://github.com/git-commit-id/git-commit-id-maven-plugin
[68]: http://www.gnu.org/licenses/lgpl-3.0.txt
[69]: https://www.mojohaus.org/exec-maven-plugin
[70]: https://registry.npmjs.org/@exasol/extension-manager-interface/-/extension-manager-interface-0.4.3.tgz
