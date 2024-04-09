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
| extension-manager-integration-test-java         |                                                   |
| [JaCoCo :: Agent][32]                           | [Eclipse Public License 2.0][33]                  |

### Runtime Dependencies

| Dependency                    | License                                                                                                        |
| ----------------------------- | -------------------------------------------------------------------------------------------------------------- |
| [JSON-P Default Provider][34] | [Eclipse Public License 2.0][35]; [GNU General Public License, version 2 with the GNU Classpath Exception][36] |

### Plugin Dependencies

| Dependency                                              | License                           |
| ------------------------------------------------------- | --------------------------------- |
| [SonarQube Scanner for Maven][37]                       | [GNU LGPL 3][38]                  |
| [Apache Maven Toolchains Plugin][39]                    | [Apache License, Version 2.0][15] |
| [Apache Maven Compiler Plugin][40]                      | [Apache-2.0][15]                  |
| [Apache Maven Enforcer Plugin][41]                      | [Apache-2.0][15]                  |
| [Maven Flatten Plugin][42]                              | [Apache Software Licenese][15]    |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][43] | [ASL2][44]                        |
| [Maven Surefire Plugin][45]                             | [Apache-2.0][15]                  |
| [Versions Maven Plugin][46]                             | [Apache License, Version 2.0][15] |
| [duplicate-finder-maven-plugin Maven Mojo][47]          | [Apache License 2.0][48]          |
| [Project Keeper Maven plugin][49]                       | [The MIT License][50]             |
| [Apache Maven Assembly Plugin][51]                      | [Apache-2.0][15]                  |
| [Apache Maven JAR Plugin][52]                           | [Apache License, Version 2.0][15] |
| [Artifact reference checker and unifier][53]            | [MIT License][54]                 |
| [Apache Maven Dependency Plugin][55]                    | [Apache-2.0][15]                  |
| [Maven Failsafe Plugin][56]                             | [Apache-2.0][15]                  |
| [JaCoCo :: Maven Plugin][57]                            | [EPL-2.0][33]                     |
| [error-code-crawler-maven-plugin][58]                   | [MIT License][59]                 |
| [Reproducible Build Maven Plugin][60]                   | [Apache 2.0][44]                  |
| [Apache Maven Clean Plugin][61]                         | [Apache-2.0][15]                  |
| [Exec Maven Plugin][62]                                 | [Apache License 2][15]            |

## Extension

### Compile Dependencies

| Dependency                                | License |
| ----------------------------------------- | ------- |
| [@exasol/extension-manager-interface][63] | MIT     |

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
[32]: https://www.eclemma.org/jacoco/index.html
[33]: https://www.eclipse.org/legal/epl-2.0/
[34]: https://github.com/eclipse-ee4j/jsonp
[35]: https://projects.eclipse.org/license/epl-2.0
[36]: https://projects.eclipse.org/license/secondary-gpl-2.0-cp
[37]: http://sonarsource.github.io/sonar-scanner-maven/
[38]: http://www.gnu.org/licenses/lgpl.txt
[39]: https://maven.apache.org/plugins/maven-toolchains-plugin/
[40]: https://maven.apache.org/plugins/maven-compiler-plugin/
[41]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[42]: https://www.mojohaus.org/flatten-maven-plugin/
[43]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[44]: http://www.apache.org/licenses/LICENSE-2.0.txt
[45]: https://maven.apache.org/surefire/maven-surefire-plugin/
[46]: https://www.mojohaus.org/versions/versions-maven-plugin/
[47]: https://basepom.github.io/duplicate-finder-maven-plugin
[48]: http://www.apache.org/licenses/LICENSE-2.0.html
[49]: https://github.com/exasol/project-keeper/
[50]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[51]: https://maven.apache.org/plugins/maven-assembly-plugin/
[52]: https://maven.apache.org/plugins/maven-jar-plugin/
[53]: https://github.com/exasol/artifact-reference-checker-maven-plugin/
[54]: https://github.com/exasol/artifact-reference-checker-maven-plugin/blob/main/LICENSE
[55]: https://maven.apache.org/plugins/maven-dependency-plugin/
[56]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[57]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[58]: https://github.com/exasol/error-code-crawler-maven-plugin/
[59]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[60]: http://zlika.github.io/reproducible-build-maven-plugin
[61]: https://maven.apache.org/plugins/maven-clean-plugin/
[62]: https://www.mojohaus.org/exec-maven-plugin
[63]: https://registry.npmjs.org/@exasol/extension-manager-interface/-/extension-manager-interface-0.4.1.tgz
