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
| [Netty/Common][34]                              | [Apache License, Version 2.0][35]                 |
| [JaCoCo :: Agent][36]                           | [EPL-2.0][37]                                     |

### Runtime Dependencies

| Dependency                    | License                                                                                                        |
| ----------------------------- | -------------------------------------------------------------------------------------------------------------- |
| [JSON-P Default Provider][38] | [Eclipse Public License 2.0][39]; [GNU General Public License, version 2 with the GNU Classpath Exception][40] |

### Plugin Dependencies

| Dependency                                              | License                           |
| ------------------------------------------------------- | --------------------------------- |
| [Apache Maven Clean Plugin][41]                         | [Apache-2.0][15]                  |
| [Apache Maven Install Plugin][42]                       | [Apache-2.0][15]                  |
| [Apache Maven Resources Plugin][43]                     | [Apache-2.0][15]                  |
| [Apache Maven Site Plugin][44]                          | [Apache License, Version 2.0][15] |
| [SonarQube Scanner for Maven][45]                       | [GNU LGPL 3][46]                  |
| [Apache Maven Toolchains Plugin][47]                    | [Apache-2.0][15]                  |
| [Apache Maven Compiler Plugin][48]                      | [Apache-2.0][15]                  |
| [Apache Maven Enforcer Plugin][49]                      | [Apache-2.0][15]                  |
| [Maven Flatten Plugin][50]                              | [Apache Software Licenese][15]    |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][51] | [ASL2][52]                        |
| [Maven Surefire Plugin][53]                             | [Apache-2.0][15]                  |
| [Versions Maven Plugin][54]                             | [Apache License, Version 2.0][15] |
| [duplicate-finder-maven-plugin Maven Mojo][55]          | [Apache License 2.0][56]          |
| [Project Keeper Maven plugin][57]                       | [The MIT License][58]             |
| [Apache Maven Assembly Plugin][59]                      | [Apache-2.0][15]                  |
| [Apache Maven JAR Plugin][60]                           | [Apache-2.0][15]                  |
| [Artifact reference checker and unifier][61]            | [MIT License][62]                 |
| [Apache Maven Dependency Plugin][63]                    | [Apache-2.0][15]                  |
| [Maven Failsafe Plugin][64]                             | [Apache-2.0][15]                  |
| [JaCoCo :: Maven Plugin][65]                            | [EPL-2.0][37]                     |
| [Quality Summarizer Maven Plugin][66]                   | [MIT License][67]                 |
| [error-code-crawler-maven-plugin][68]                   | [MIT License][69]                 |
| [Reproducible Build Maven Plugin][70]                   | [Apache 2.0][52]                  |
| [Exec Maven Plugin][71]                                 | [Apache License 2][15]            |

## Extension

### Compile Dependencies

| Dependency                                | License |
| ----------------------------------------- | ------- |
| [@exasol/extension-manager-interface][72] | MIT     |

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
[34]: https://netty.io/netty-common/
[35]: https://www.apache.org/licenses/LICENSE-2.0
[36]: https://www.eclemma.org/jacoco/index.html
[37]: https://www.eclipse.org/legal/epl-2.0/
[38]: https://github.com/eclipse-ee4j/jsonp
[39]: https://projects.eclipse.org/license/epl-2.0
[40]: https://projects.eclipse.org/license/secondary-gpl-2.0-cp
[41]: https://maven.apache.org/plugins/maven-clean-plugin/
[42]: https://maven.apache.org/plugins/maven-install-plugin/
[43]: https://maven.apache.org/plugins/maven-resources-plugin/
[44]: https://maven.apache.org/plugins/maven-site-plugin/
[45]: http://sonarsource.github.io/sonar-scanner-maven/
[46]: http://www.gnu.org/licenses/lgpl.txt
[47]: https://maven.apache.org/plugins/maven-toolchains-plugin/
[48]: https://maven.apache.org/plugins/maven-compiler-plugin/
[49]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[50]: https://www.mojohaus.org/flatten-maven-plugin/
[51]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[52]: http://www.apache.org/licenses/LICENSE-2.0.txt
[53]: https://maven.apache.org/surefire/maven-surefire-plugin/
[54]: https://www.mojohaus.org/versions/versions-maven-plugin/
[55]: https://basepom.github.io/duplicate-finder-maven-plugin
[56]: http://www.apache.org/licenses/LICENSE-2.0.html
[57]: https://github.com/exasol/project-keeper/
[58]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[59]: https://maven.apache.org/plugins/maven-assembly-plugin/
[60]: https://maven.apache.org/plugins/maven-jar-plugin/
[61]: https://github.com/exasol/artifact-reference-checker-maven-plugin/
[62]: https://github.com/exasol/artifact-reference-checker-maven-plugin/blob/main/LICENSE
[63]: https://maven.apache.org/plugins/maven-dependency-plugin/
[64]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[65]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[66]: https://github.com/exasol/quality-summarizer-maven-plugin/
[67]: https://github.com/exasol/quality-summarizer-maven-plugin/blob/main/LICENSE
[68]: https://github.com/exasol/error-code-crawler-maven-plugin/
[69]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[70]: http://zlika.github.io/reproducible-build-maven-plugin
[71]: https://www.mojohaus.org/exec-maven-plugin
[72]: https://registry.npmjs.org/@exasol/extension-manager-interface/-/extension-manager-interface-0.4.2.tgz
