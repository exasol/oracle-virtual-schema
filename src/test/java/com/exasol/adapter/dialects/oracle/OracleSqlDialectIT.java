package com.exasol.adapter.dialects.oracle;

import static com.exasol.adapter.dialects.oracle.IntegrationTestConstants.TABLE_JOIN_1;
import static com.exasol.adapter.dialects.oracle.IntegrationTestConstants.TABLE_JOIN_2;
import static com.exasol.matcher.ResultSetMatcher.matchesResultSet;
import static com.exasol.matcher.ResultSetStructureMatcher.table;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeFalse;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("integration")
@Testcontainers
class OracleSqlDialectIT extends CommonOracleIntegrationTestSetup {

    @Test
    void testCountAll() throws SQLException {
        try (Connection connection = getExasolConnection();
             Statement statementExasol = connection.createStatement()) {
            final String qualifiedTableName = VIRTUAL_SCHEMA_JDBC + "." + TABLE_ORACLE_NUMBER_HANDLING;
            final String query = "SELECT COUNT(*) FROM " + qualifiedTableName;
            assertThat(getActualResultSet(statementExasol, query), table("BIGINT").row(1L).matches());
        }
    }

    @Nested
    @DisplayName("Number handling test")
    class NumberHandlingTest {
        @ParameterizedTest
        @ValueSource(strings = { VIRTUAL_SCHEMA_JDBC_NUMBER_TO_DECIMAL, VIRTUAL_SCHEMA_ORACLE_NUMBER_TO_DECIMAL_JDBC_MAPPING })
        void testNumberToDecimalThrowsException(final String virtualSchemaName) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableName = virtualSchemaName + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT c5 FROM " + qualifiedTableName;
                final SQLException exception = assertThrows(SQLException.class, () -> statementExasol.execute(query));
                assertThat(exception.getMessage(),
                        containsString("value larger than specified precision allowed for this column"));
            }
        }

        @ParameterizedTest
        @ValueSource(strings = { VIRTUAL_SCHEMA_JDBC_NUMBER_TO_DECIMAL, VIRTUAL_SCHEMA_ORACLE_NUMBER_TO_DECIMAL_JDBC_MAPPING })
        void testNumber36ToDecimal(final String virtualSchemaName) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableName = virtualSchemaName + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT c_number36 FROM " + qualifiedTableName;
                assertAll(
                        () -> assertExpressionExecutionBigDecimalResult(statementExasol, query,
                                new BigDecimal("123456789012345678901234567890123456")),
                        () -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, "C_NUMBER36"),
                                equalTo("DECIMAL(36,0)")));
            }
        }

        @ParameterizedTest
        @ValueSource(strings = { VIRTUAL_SCHEMA_JDBC_NUMBER_TO_DECIMAL, VIRTUAL_SCHEMA_ORACLE_NUMBER_TO_DECIMAL_JDBC_MAPPING })
        void testNumber38ToDecimalThrowsException(final String virtualSchemaName) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableName = virtualSchemaName + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT c6 FROM " + qualifiedTableName;
                final SQLException exception = assertThrows(SQLException.class, () -> statementExasol.execute(query));
                assertThat(exception.getMessage(),
                        containsString("value larger than specified precision allowed for this column"));
            }
        }

        @ParameterizedTest
        @ValueSource(strings = { VIRTUAL_SCHEMA_JDBC_NUMBER_TO_DECIMAL, VIRTUAL_SCHEMA_ORACLE_NUMBER_TO_DECIMAL_JDBC_MAPPING })
        void testNumber10S5ToDecimal(final String virtualSchemaName) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableName = virtualSchemaName + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT C7 FROM " + qualifiedTableName;
                assertAll(() -> assertExpressionExecutionBigDecimalResult(statementExasol, query, new BigDecimal("12345.12345")),
                        () -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, "C7"), equalTo("DECIMAL(10,5)")));
            }
        }

        @Test
        void testSelectAllColsNumberFromJDBC() throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableNameActual = VIRTUAL_SCHEMA_JDBC_NUMBER_TO_DECIMAL + "."
                        + TABLE_ORACLE_NUMBER_HANDLING;
                final ResultSet expected = getExpectedResultSet(statementExasol, "(A DECIMAL(36,1), B DECIMAL(36,1), C DECIMAL(36,2))",
                        "(1234567890123456789012345678901234.6, 1234567890123456789012345678.9, 1234567890123456789012345678901234.56)");
                assertThat(statementExasol.executeQuery("SELECT * FROM " + qualifiedTableNameActual), //
                        matchesResultSet(expected));
            }
        }

        @Test
        void testSelectAllColsNumberFromOra() throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableNameActual = VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING + "."
                        + TABLE_ORACLE_NUMBER_HANDLING;
                final ResultSet expected = getExpectedResultSet(statementExasol, "(A VARCHAR(100), B VARCHAR(100), C DECIMAL(36,2))",
                        "('1234567890123456789012345678901234.56', '1234567890123456789012345678.9012345678', '1234567890123456789012345678901234.56')");
                assertThat(statementExasol.executeQuery("SELECT * FROM " + qualifiedTableNameActual), //
                        matchesResultSet(expected));
            }
        }

        @Test
        void testSelectAllColsNumberFromOraWithJDBCTypemapping() throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableNameActual = VIRTUAL_SCHEMA_ORACLE_NUMBER_TO_DECIMAL_JDBC_MAPPING + "."
                        + TABLE_ORACLE_NUMBER_HANDLING;
                final ResultSet expected = getExpectedResultSet(statementExasol, "(A DECIMAL(36,1), B DECIMAL(36,1), C DECIMAL(36,2))",
                        "('12.3456789012345678901234567890123460E32', '12.3456789012345678901234567890E26', '12.3456789012345678901234567890123456E32')");
                    assertThat(statementExasol.executeQuery("SELECT * FROM " + qualifiedTableNameActual), //
                            matchesResultSet(expected));
            }
        }

        @ParameterizedTest
        @ValueSource(strings = { VIRTUAL_SCHEMA_JDBC_NUMBER_TO_DECIMAL, VIRTUAL_SCHEMA_ORACLE_NUMBER_TO_DECIMAL })
        void testNumberDataTypes(final String virtualSchemaName) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableName = virtualSchemaName + "." + TABLE_ORACLE_NUMBER_HANDLING;
                assertAll(() -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, "A"), equalTo("DECIMAL(36,1)")),
                        () -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, "B"), equalTo("DECIMAL(36,1)")),
                        () -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, "C"), equalTo("DECIMAL(36,2)")));
            }
        }

        @ParameterizedTest
        @ValueSource(strings = { VIRTUAL_SCHEMA_JDBC_NUMBER_TO_DECIMAL, VIRTUAL_SCHEMA_ORACLE_NUMBER_TO_DECIMAL_JDBC_MAPPING })
        void testSelectOneNumberColumn(final String virtualSchemaName) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableName = virtualSchemaName + "." + TABLE_ORACLE_NUMBER_HANDLING;
                assertAll(
                        () -> assertExpressionExecutionBigDecimalResult(statementExasol, "SELECT A FROM " + qualifiedTableName,
                                new BigDecimal("1234567890123456789012345678901234.6")),
                        () -> assertExpressionExecutionBigDecimalResult(statementExasol, "SELECT B FROM " + qualifiedTableName,
                                new BigDecimal("1234567890123456789012345678.9")),
                        () -> assertExpressionExecutionBigDecimalResult(statementExasol, "SELECT C FROM " + qualifiedTableName,
                                new BigDecimal("1234567890123456789012345678901234.56")));
            }
        }
    }

    private String getColumnTypesOfTable(Statement statementExasol, final String tableName, final String columnName) throws SQLException {
        final ResultSet result = statementExasol.executeQuery("DESCRIBE " + tableName);
        while (result.next()) {
            if (result.getString("COLUMN_NAME").toUpperCase().equals(columnName)) {
                return result.getString("SQL_TYPE").toUpperCase();
            }
        }
        throw new IllegalArgumentException("Type for column " + columnName + " not found");
    }

    private void assertExpressionExecutionBigDecimalResult(Statement statementExasol, final String query, final BigDecimal expectedValue)
            throws SQLException {
        final ResultSet result = statementExasol.executeQuery(query);
        result.next();
        final BigDecimal actualResult = result.getBigDecimal(1);
        assertThat(actualResult.stripTrailingZeros(), equalTo(expectedValue));
    }

    private void assertExplainVirtual(Statement statementExasol, final String query, final String expected) throws SQLException {
        final ResultSet explainVirtual = statementExasol.executeQuery("EXPLAIN VIRTUAL " + query);
        explainVirtual.next();
        final String explainVirtualStringActual = explainVirtual.getString("PUSHDOWN_SQL");
        assertThat(explainVirtualStringActual, containsString(expected));
    }

    @Nested
    @DisplayName("Join test")
    class JoinTest {
        @ParameterizedTest
        @ValueSource(strings = { VIRTUAL_SCHEMA_JDBC, VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING })
        void testInnerJoin(final String virtualSchema) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String query = "SELECT * FROM " + virtualSchema + "." + TABLE_JOIN_1 + " a INNER JOIN  "
                        + virtualSchema + "." + TABLE_JOIN_2 + " b ON a.x=b.x";
                final ResultSet expected = getExpectedResultSet(statementExasol,
                        List.of("x VARCHAR(100)", "y VARCHAR(100)", "a VARCHAR(100)", "b VARCHAR(100)"), //
                        List.of("'2','bbb', '2','bbb'"));
                assertThat(getActualResultSet(statementExasol, query), matchesResultSet(expected));
            }
        }

        @ParameterizedTest
        @ValueSource(strings = { VIRTUAL_SCHEMA_JDBC, VIRTUAL_SCHEMA_ORACLE })
        void testInnerJoinWithProjection(final String virtualSchemaName) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedJoinTableName1 = virtualSchemaName + "." + TABLE_JOIN_1;
                final String qualifiedJoinTableName2 = virtualSchemaName + "." + TABLE_JOIN_2;
                final String query = "SELECT b.y || " + qualifiedJoinTableName1 + ".y FROM " + qualifiedJoinTableName1
                        + " INNER JOIN  " + qualifiedJoinTableName2 + " b ON " + qualifiedJoinTableName1 + ".x=b.x";
                final ResultSet expected = getExpectedResultSet(statementExasol, List.of("y VARCHAR(100)"), //
                        List.of("'bbbbbb'"));
                assertThat(getActualResultSet(statementExasol, query), matchesResultSet(expected));
            }
        }

        @ParameterizedTest
        @ValueSource(strings = { VIRTUAL_SCHEMA_JDBC, VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING })
        void testLeftJoin(final String virtualSchemaName) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedJoinTableName1 = virtualSchemaName + "." + TABLE_JOIN_1;
                final String qualifiedJoinTableName2 = virtualSchemaName + "." + TABLE_JOIN_2;
                final String query = "SELECT * FROM " + qualifiedJoinTableName1 + " a LEFT OUTER JOIN  "
                        + qualifiedJoinTableName2 + " b ON a.x=b.x ORDER BY a.x";
                final ResultSet expected = getExpectedResultSet(statementExasol,
                        List.of("x VARCHAR(100)", "y VARCHAR(100)", "a VARCHAR(100)", "b VARCHAR(100)"), //
                        List.of("'1', 'aaa', null, null", //
                                "'2', 'bbb', '2', 'bbb'"));
                assertThat(getActualResultSet(statementExasol, query), matchesResultSet(expected));
            }
        }

        @ParameterizedTest
        @ValueSource(strings = { VIRTUAL_SCHEMA_JDBC, VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING })
        void testRightJoin(final String virtualSchemaName) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedJoinTableName1 = virtualSchemaName + "." + TABLE_JOIN_1;
                final String qualifiedJoinTableName2 = virtualSchemaName + "." + TABLE_JOIN_2;
                final String query = "SELECT * FROM " + qualifiedJoinTableName1 + " a RIGHT OUTER JOIN  "
                        + qualifiedJoinTableName2 + " b ON a.x=b.x ORDER BY a.x";
                final ResultSet expected = getExpectedResultSet(statementExasol,
                        List.of("x VARCHAR(100)", "y VARCHAR(100)", "a VARCHAR(100)", "b VARCHAR(100)"), //
                        List.of("'2', 'bbb', '2', 'bbb'", //
                                "null, null, '3', 'ccc'"));
                assertThat(getActualResultSet(statementExasol, query), matchesResultSet(expected));
            }
        }

        @ParameterizedTest
        @ValueSource(strings = { VIRTUAL_SCHEMA_JDBC, VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING })
        void testFullOuterJoin(final String virtualSchemaName) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedJoinTableName1 = virtualSchemaName + "." + TABLE_JOIN_1;
                final String qualifiedJoinTableName2 = virtualSchemaName + "." + TABLE_JOIN_2;
                final String query = "SELECT * FROM " + qualifiedJoinTableName1 + " a FULL OUTER JOIN  "
                        + qualifiedJoinTableName2 + " b ON a.x=b.x ORDER BY a.x";
                final ResultSet expected = getExpectedResultSet(statementExasol,
                        List.of("x VARCHAR(100)", "y VARCHAR(100)", "a VARCHAR(100)", "b VARCHAR(100)"), //
                        List.of("1, 'aaa', null, null", //
                                "'2', 'bbb', '2', 'bbb'", //
                                "null, null, '3', 'ccc'"));
                assertThat(getActualResultSet(statementExasol, query), matchesResultSet(expected));
            }
        }

        @ParameterizedTest
        @ValueSource(strings = { VIRTUAL_SCHEMA_JDBC, VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING })
        void testRightJoinWithComplexCondition(final String virtualSchemaName) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedJoinTableName1 = virtualSchemaName + "." + TABLE_JOIN_1;
                final String qualifiedJoinTableName2 = virtualSchemaName + "." + TABLE_JOIN_2;
                final String query = "SELECT * FROM " + qualifiedJoinTableName1 + " a RIGHT OUTER JOIN  "
                        + qualifiedJoinTableName2 + " b ON a.x||a.y=b.x||b.y ORDER BY a.x";
                final ResultSet expected = getExpectedResultSet(statementExasol,
                        List.of("x VARCHAR(100)", "y VARCHAR(100)", "a VARCHAR(100)", "b VARCHAR(100)"), //
                        List.of("'2', 'bbb', '2', 'bbb'", //
                                "null, null, '3', 'ccc'"));
                assertThat(getActualResultSet(statementExasol, query), matchesResultSet(expected));
            }
        }

        @ParameterizedTest
        @ValueSource(strings = { VIRTUAL_SCHEMA_JDBC, VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING })
        void testFullOuterJoinWithComplexCondition(final String virtualSchemaName) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedJoinTableName1 = virtualSchemaName + "." + TABLE_JOIN_1;
                final String qualifiedJoinTableName2 = virtualSchemaName + "." + TABLE_JOIN_2;
                final String query = "SELECT * FROM " + qualifiedJoinTableName1 + " a FULL OUTER JOIN  "
                        + qualifiedJoinTableName2 + " b ON a.x-b.x=0 ORDER BY a.x";
                final ResultSet expected = getExpectedResultSet(statementExasol,
                        List.of("x VARCHAR(100)", "y VARCHAR(100)", "a VARCHAR(100)", "b VARCHAR(100)"), //
                        List.of("1, 'aaa', null, null", //
                                "'2', 'bbb', '2', 'bbb'", //
                                "null, null, '3', 'ccc'"));
                assertThat(getActualResultSet(statementExasol, query), matchesResultSet(expected));
            }
        }
    }

    @Nested
    @DisplayName("Datatype tests")
    class DatatypeTest {
        @ParameterizedTest
        @CsvSource(value = { "VIRTUAL_SCHEMA_JDBC, 12346.12345", //
                "VIRTUAL_SCHEMA_ORACLE_NUMBER_TO_DECIMAL_JDBC_MAPPING, 12346.12345" })
        void testSelectExpression(final String virtualSchemaName, final String expectedColumnValue) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableNameActual = virtualSchemaName + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT C7 + 1 FROM " + qualifiedTableNameActual + " ORDER BY 1";
                final String expectedExplainVirtual = "SELECT (\"" + TABLE_ORACLE_ALL_DATA_TYPES
                        + "\".\"C7\" + 1) FROM \"" + SCHEMA_ORACLE + "\".\"" + TABLE_ORACLE_ALL_DATA_TYPES
                        + "\" ORDER BY (\"" + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"C7\" + 1)";
                assertAll(() -> assertExpressionExecutionStringResult(statementExasol, query, expectedColumnValue),
                        () -> assertExplainVirtual(statementExasol, query, expectedExplainVirtual));
            }
        }

        private void assertExpressionExecutionStringResult(Statement statementExasol, final String query, final String expected)
                throws SQLException {
            final ResultSet result = statementExasol.executeQuery(query);
            result.next();
            final String actual = result.getString(1);
            MatcherAssert.assertThat(actual, containsString(expected));
        }

        private void assertExpressionExecutionStringResults(Statement statementExasol, final String query, final String expected1, String expected2)
                throws SQLException {
            final ResultSet result = statementExasol.executeQuery(query);
            result.next();
            final String actual = result.getString(1);
            MatcherAssert.assertThat(actual, actual.contains(expected1) || actual.contains(expected2));

            result.next();
            final String actual2 = result.getString(1);
            MatcherAssert.assertThat(actual2, actual2.contains(expected1) || actual2.contains(expected2));
        }

        @ParameterizedTest
        @CsvSource(value = { "VIRTUAL_SCHEMA_JDBC, 12355.12345", //
                "VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING, 12355.12345", "VIRTUAL_SCHEMA_ORACLE_NUMBER_TO_DECIMAL_JDBC_MAPPING, 12355.12345" })
        void testFilterExpression(final String virtualSchemaName, final String expectedColumnValue) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableNameActual = virtualSchemaName + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT C7 FROM " + qualifiedTableNameActual + " WHERE C7 > 12346";
                final String expectedExplainVirtual = "SELECT \"" + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"C7\" FROM \""
                        + SCHEMA_ORACLE + "\".\"" + TABLE_ORACLE_ALL_DATA_TYPES + "\" WHERE 12346 < \""
                        + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"C7\"";
                assertAll(() -> assertExpressionExecutionStringResult(statementExasol, query, expectedColumnValue),
                        () -> assertExplainVirtual(statementExasol, query, expectedExplainVirtual));
            }
        }

        @ParameterizedTest
        @CsvSource(value = { "VIRTUAL_SCHEMA_JDBC, 12345.12345", //
                "VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING, 12345.12345" })
        void testAggregateSingleGroup(final String virtualSchemaName, final String expectedColumnValue) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableNameActual = virtualSchemaName + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT min(C7) FROM " + qualifiedTableNameActual;
                final String expectedExplainVirtual = "SELECT MIN(\"" + TABLE_ORACLE_ALL_DATA_TYPES
                        + "\".\"C7\") FROM \"" + SCHEMA_ORACLE + "\".\"" + TABLE_ORACLE_ALL_DATA_TYPES + "\"";
                assertAll(() -> assertExpressionExecutionStringResult(statementExasol, query, expectedColumnValue),
                        () -> assertExplainVirtual(statementExasol, query, expectedExplainVirtual));
            }
        }

        @Test
        void testAggregateGroupByColumnJdbc() throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedActualTableName = VIRTUAL_SCHEMA_JDBC + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT C5, min(C7) FROM " + qualifiedActualTableName + " GROUP BY C5 ORDER BY 1 DESC";
                final String expectedExplainVirtual = "SELECT CAST(TO_CHAR(\"" + TABLE_ORACLE_ALL_DATA_TYPES
                        + "\".\"C5\") AS VARCHAR(4000)), MIN(\"" + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"C7\") FROM \""
                        + SCHEMA_ORACLE + "\".\"" + TABLE_ORACLE_ALL_DATA_TYPES + "\" GROUP BY CAST(TO_CHAR(\""
                        + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"C5\") AS VARCHAR(4000)) ORDER BY CAST(TO_CHAR(\"" + TABLE_ORACLE_ALL_DATA_TYPES
                        + "\".\"C5\") AS VARCHAR(4000)) DESC";
                final ResultSet actual = statementExasol.executeQuery(query);
                assertAll(
                        () -> assertThat(actual,
                                table("VARCHAR", "DECIMAL")
                                        .row("123456789012345678901234567890123456", new BigDecimal("12345.12345"))
                                        .row("1234567890.123456789", new BigDecimal("12355.12345")).matches()),
                        () -> assertExplainVirtual(statementExasol, query, expectedExplainVirtual));
            }
        }

        @Test
        void testAggregateGroupByExpressionOra() throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedActualTableName = VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT C5 + 1, min(C7) FROM " + qualifiedActualTableName
                        + " GROUP BY C5 + 1 ORDER BY 1 DESC";
                final ResultSet expected = getExpectedResultSet(statementExasol, "(A DOUBLE, B DECIMAL(10,5))",
                        "(1.2345678901234568E35, 12345.12345),"
                                + "(1.234567891123457E9, 12355.12345)");
                final ResultSet actual = statementExasol.executeQuery(query);
                final String expectedExplainVirtual = "SELECT (\"" + TABLE_ORACLE_ALL_DATA_TYPES
                        + "\".\"C5\" + 1), MIN(\"" + TABLE_ORACLE_ALL_DATA_TYPES
                        + "\".\"C7\") FROM \"" + SCHEMA_ORACLE + "\".\"" + TABLE_ORACLE_ALL_DATA_TYPES
                        + "\" GROUP BY (\"" + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"C5\" + 1) ORDER BY (\""
                        + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"C5\" + 1) DESC";
                assertAll(() -> assertThat(actual, matchesResultSet(expected)),
                        () -> assertExplainVirtual(statementExasol, query, expectedExplainVirtual));
            }
        }

        @Test
        void testAggregateGroupByTuple() throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedActualTableName = VIRTUAL_SCHEMA_JDBC + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT C_NUMBER36, C5, min(C7) FROM " + qualifiedActualTableName
                        + " GROUP BY C_NUMBER36, C5 ORDER BY C5 DESC";
                final ResultSet actual = statementExasol.executeQuery(query);
                final String expectedExplainVirtual = "SELECT \""
                        + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"C_NUMBER36\", CAST(TO_CHAR(\"" + TABLE_ORACLE_ALL_DATA_TYPES
                        + "\".\"C5\") AS VARCHAR(4000)), MIN(\"" + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"C7\") FROM \""
                        + SCHEMA_ORACLE + "\".\"" + TABLE_ORACLE_ALL_DATA_TYPES + "\" GROUP BY CAST(TO_CHAR(\"" + TABLE_ORACLE_ALL_DATA_TYPES
                        + "\".\"C5\") AS VARCHAR(4000)), \"" + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"C_NUMBER36\" ORDER BY CAST(TO_CHAR(\"" + TABLE_ORACLE_ALL_DATA_TYPES
                        + "\".\"C5\") AS VARCHAR(4000)) DESC'";
                assertAll(
                        () -> assertThat(actual,
                                table("DECIMAL", "VARCHAR", "DECIMAL")
                                        .row(new BigDecimal("123456789012345678901234567890123456"),
                                                "123456789012345678901234567890123456", new BigDecimal("12345.12345"))
                                        .row(null, "1234567890.123456789", new BigDecimal("12355.12345")).matches()),
                        () -> assertExplainVirtual(statementExasol, query, expectedExplainVirtual));
            }

        }

        @Test
        void testAggregateHaving() throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedActualTableName = VIRTUAL_SCHEMA_JDBC + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT C5, min(C7) FROM " + qualifiedActualTableName
                        + " GROUP BY C5 HAVING MIN(C7) > 12350";
                final ResultSet actual = statementExasol.executeQuery(query);
                final String expectedExplainVirtual = "SELECT CAST(TO_CHAR(\"" + TABLE_ORACLE_ALL_DATA_TYPES
                        + "\".\"C5\") AS VARCHAR(4000)), MIN(\"" + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"C7\") FROM \""
                        + SCHEMA_ORACLE + "\".\"" + TABLE_ORACLE_ALL_DATA_TYPES + "\" GROUP BY CAST(TO_CHAR(\""
                        + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"C5\") AS VARCHAR(4000)) HAVING 12350 < MIN(\"" + TABLE_ORACLE_ALL_DATA_TYPES
                        + "\".\"C7\")";
                assertAll(
                        () -> assertThat(actual, table("VARCHAR", "DECIMAL")
                                .row("1234567890.123456789", new BigDecimal("12355.12345")).matches()),
                        () -> assertExplainVirtual(statementExasol, query, expectedExplainVirtual));
            }
        }

        @ParameterizedTest
        @ValueSource(strings = { VIRTUAL_SCHEMA_JDBC, VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING })
        void testOrderByColumn(final String virtualSchemaName) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableNameActual = virtualSchemaName + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT C1 FROM " + qualifiedTableNameActual + " ORDER BY C1 DESC NULLS LAST";
                final String expectedExplainVirtual = "SELECT \"" + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"C1\" FROM \""
                        + SCHEMA_ORACLE + "\".\"" + TABLE_ORACLE_ALL_DATA_TYPES + "\" ORDER BY \""
                        + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"C1\" DESC NULLS LAST";
                assertAll(() -> assertExpressionExecutionStringResult(statementExasol, query, "aaaaaaaaaaaaaaaaaaaa"),
                        () -> assertExplainVirtual(statementExasol, query, expectedExplainVirtual));
            }
        }

        @Test
        void testOrderByExpressionJdbc() throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedActualTableName = VIRTUAL_SCHEMA_JDBC + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT C7 FROM " + qualifiedActualTableName + " ORDER BY ABS(C7) DESC NULLS FIRST";
                final ResultSet expected = getExpectedResultSet(statementExasol, "(A DECIMAL(36,5))", "(12355.12345), (12345.12345)");
                final ResultSet actual = statementExasol.executeQuery(query);
                final String expectedExplainVirtual = "SELECT \"" + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"C7\" FROM \""
                        + SCHEMA_ORACLE + "\".\"" + TABLE_ORACLE_ALL_DATA_TYPES + "\" ORDER BY ABS(\""
                        + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"C7\") DESC";
                assertAll(() -> assertThat(actual, matchesResultSet(expected)),
                        () -> assertExplainVirtual(statementExasol, query, expectedExplainVirtual));
            }
        }

        @Test
        void testLimit() throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedActualTableName = VIRTUAL_SCHEMA_JDBC + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT C7 FROM " + qualifiedActualTableName + " ORDER BY C7 LIMIT 2";
                final ResultSet expected = getExpectedResultSet(statementExasol, "(A DECIMAL(36,5))", "(12345.12345), (12355.12345)");
                final ResultSet actual = statementExasol.executeQuery(query);
                final String expectedExplainVirtual = "SELECT LIMIT_SUBSELECT.* FROM ( SELECT \""
                        + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"C7\" FROM \"" + SCHEMA_ORACLE + "\".\""
                        + TABLE_ORACLE_ALL_DATA_TYPES + "\" ORDER BY \"" + TABLE_ORACLE_ALL_DATA_TYPES
                        + "\".\"C7\"  ) LIMIT_SUBSELECT WHERE ROWNUM <= 2";
                assertAll(() -> assertThat(actual, matchesResultSet(expected)),
                        () -> assertExplainVirtual(statementExasol, query, expectedExplainVirtual));
            }
        }

        @Test
        void testLimitOffset() throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedActualTableName = VIRTUAL_SCHEMA_JDBC + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT C7 FROM " + qualifiedActualTableName + " ORDER BY C7 LIMIT 1 OFFSET 1";
                final ResultSet expected = getExpectedResultSet(statementExasol, "(A DECIMAL(36,5))", "(12355.12345)");
                final ResultSet actual = statementExasol.executeQuery(query);
                final String expectedExplainVirtual = "SELECT LIMIT_SUBSELECT.*, ROWNUM ROWNUM_SUB FROM ( SELECT \""
                        + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"C7\" AS c0 FROM \"" + SCHEMA_ORACLE + "\".\""
                        + TABLE_ORACLE_ALL_DATA_TYPES + "\" ORDER BY \"" + TABLE_ORACLE_ALL_DATA_TYPES
                        + "\".\"C7\"  ) LIMIT_SUBSELECT WHERE ROWNUM <= 2 ) WHERE ROWNUM_SUB > 1";
                assertAll(() -> assertThat(actual, matchesResultSet(expected)),
                        () -> assertExplainVirtual(statementExasol, query, expectedExplainVirtual));
            }
        }

        @ParameterizedTest
        @CsvSource(value = { //
                "VIRTUAL_SCHEMA_JDBC, C1, CHAR(50) UTF8, aaaaaaaaaaaaaaaaaaaa", //
                "VIRTUAL_SCHEMA_JDBC, C2, CHAR(50) UTF8, bbbbbbbbbbbbbbbbbbbb", //
                "VIRTUAL_SCHEMA_JDBC, C3, VARCHAR(50) UTF8, cccccccccccccccccccc", //
                "VIRTUAL_SCHEMA_JDBC, C4, VARCHAR(50) UTF8, dddddddddddddddddddd", //
                "VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING, C1, CHAR(50) UTF8, aaaaaaaaaaaaaaaaaaaa", //
                "VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING, C2, CHAR(50) UTF8, bbbbbbbbbbbbbbbbbbbb", //
                "VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING, C3, VARCHAR(50) UTF8, cccccccccccccccccccc", //
                "VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING, C4, VARCHAR(50) UTF8, dddddddddddddddddddd" //
        })
        void testCharactersColumns(final String virtualSchemaName, final String columnName,
                                   final String expectedColumnType, final String expectedColumnValue) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableName = virtualSchemaName + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT " + columnName + " FROM " + qualifiedTableName;
                assertAll(() -> assertExpressionExecutionStringResult(statementExasol, query, expectedColumnValue),
                        () -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, columnName),
                                equalTo(expectedColumnType)));
            }
        }

        @ParameterizedTest
        @CsvSource(value = { //
                "VIRTUAL_SCHEMA_JDBC, C18", //
                "VIRTUAL_SCHEMA_JDBC, C19", //
                "VIRTUAL_SCHEMA_JDBC, C20", //
                "VIRTUAL_SCHEMA_ORACLE, C18", //
                "VIRTUAL_SCHEMA_ORACLE, C19", //
                "VIRTUAL_SCHEMA_ORACLE, C20",//
        })
        void testBlobColumns(final String virtualSchemaName, final String columnName) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableName = virtualSchemaName + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT " + columnName + " FROM " + qualifiedTableName;
                final SQLException exception = assertThrows(SQLException.class, () -> statementExasol.execute(query));
                assertThat(exception.getMessage(), startsWith("object " + columnName + " not found"));
            }
        }

        @ParameterizedTest
        @CsvSource(value = { //
                "VIRTUAL_SCHEMA_JDBC | C5 | VARCHAR(4000) UTF8 | 123456789012345678901234567890123456", //
                "VIRTUAL_SCHEMA_JDBC | C_NUMBER36 | DECIMAL(36,0) | 123456789012345678901234567890123456", //
                "VIRTUAL_SCHEMA_JDBC | C6 | VARCHAR(38) UTF8 | 12345678901234567890123456789012345678", //
                "VIRTUAL_SCHEMA_JDBC | C7 | DECIMAL(10,5) | 12345.12345", //
                "VIRTUAL_SCHEMA_JDBC | C8 | VARCHAR(39) UTF8 | 123456789012345678901234567890123.45678", //
                "VIRTUAL_SCHEMA_JDBC | C9 | VARCHAR(40) UTF8 | 0.12345678901234567890123456789012345678", //
                "VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING | C5 | VARCHAR(4000) UTF8 | 123456789012345678901234567890123456", //
                "VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING | C_NUMBER36 | DECIMAL(36,0) | 123456789012345678901234567890123456", //
                "VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING | C6 | VARCHAR(38) UTF8 | 12345678901234567890123456789012345678", //
                "VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING | C7 | DECIMAL(10,5) | 12345.12345", //
                "VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING | C8 | VARCHAR(39) UTF8 | 123456789012345678901234567890123.45678", //
                "VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING | C9 | VARCHAR(40) UTF8 | 0.12345678901234567890123456789012345678", //
        }, delimiter = '|')
        void testNumberColumns(final String virtualSchemaName, final String columnName, final String expectedColumnType,
                               final String expectedValue) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableName = virtualSchemaName + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT " + columnName + " FROM " + qualifiedTableName;
                assertAll(() -> assertExpressionExecutionBigDecimalResult(statementExasol, query, new BigDecimal(expectedValue)),
                        () -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, columnName),
                                equalTo(expectedColumnType)));
            }
        }

        @ParameterizedTest
        @CsvSource(value = { //
                "VIRTUAL_SCHEMA_JDBC | C_BINFLOAT | VARCHAR(4000) UTF8 | 1234.1241723", //
                "VIRTUAL_SCHEMA_JDBC | C_FLOAT | VARCHAR(126) UTF8 | 12345.01982348239", //
                "VIRTUAL_SCHEMA_JDBC | C_FLOAT126 | VARCHAR(126) UTF8 | 12345678.01234567901234567890123456789", //
                "VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING | C_BINFLOAT | VARCHAR(4000) UTF8 | 1234.1241723", //
                "VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING | C_FLOAT | VARCHAR(126) UTF8 | 12345.01982348239", //
                "VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING | C_FLOAT126 | VARCHAR(126) UTF8 | 12345678.01234567901234567890123456789" //
        }, delimiter = '|')
        void testFloatNumbers(final String virtualSchemaName, final String columnName, final String expectedColumnType,
                              final String expectedValue) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableName = virtualSchemaName + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT " + columnName + " FROM " + qualifiedTableName;
                assertAll(() -> assertExpressionExecutionFloatResult(statementExasol, query, Float.parseFloat(expectedValue)),
                        () -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, columnName),
                                equalTo(expectedColumnType)));
            }
        }

        private void assertExpressionExecutionFloatResult(Statement statementExasol, final String query, final float expected)
                throws SQLException {
            final ResultSet result = statementExasol.executeQuery(query);
            result.next();
            final double actualResult = result.getFloat(1);
            assertEquals(expected, actualResult, 0.000000001);
        }

        @ParameterizedTest
        @ValueSource(strings = { VIRTUAL_SCHEMA_JDBC, VIRTUAL_SCHEMA_ORACLE_NUMBER_TO_DECIMAL })
        void testBinaryDouble(final String virtualSchemaName) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableName = virtualSchemaName + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT C_BINDOUBLE FROM " + qualifiedTableName;
                assertAll(() -> assertExpressionExecutionDoubleResult(statementExasol, query, Double.parseDouble("1234987.120871234")),
                        () -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, "C_BINDOUBLE"),
                                equalTo("VARCHAR(4000) UTF8")));
            }
        }

        private void assertExpressionExecutionDoubleResult(Statement statementExasol, final String query, final double expected)
                throws SQLException {
            final ResultSet result = statementExasol.executeQuery(query);
            result.next();
            final double actualResult = result.getDouble(1);
            MatcherAssert.assertThat(actualResult, equalTo(expected));
        }

        @Test
        void testLongJdbc() throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableName = VIRTUAL_SCHEMA_JDBC + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT C_LONG FROM " + qualifiedTableName;
                assertAll(() -> assertExpressionExecutionStringResult(statementExasol, query, "test long 123"),
                        () -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, "C_LONG"),
                                equalTo("VARCHAR(4000) UTF8")));
            }
        }

        @Test
        void testLongOra() throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableName = VIRTUAL_SCHEMA_ORACLE + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT C_LONG FROM " + qualifiedTableName;
                final SQLException exception = assertThrows(SQLException.class, () -> statementExasol.execute(query));
                assertThat(exception.getMessage(),
                        containsString("Unknown Oracle OCI column data type (8) found for column 'C_LONG'"));
            }
        }

        @ParameterizedTest
        @ValueSource(strings = { VIRTUAL_SCHEMA_JDBC, VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING })
        void testDate(final String virtualSchemaName) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableName = virtualSchemaName + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT C10 FROM " + qualifiedTableName;
                assertAll(() -> assertExpressionExecutionDateResult(statementExasol, query, Date.valueOf("2016-08-19")),
                        () -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, "C10"), equalTo("TIMESTAMP(0)")));
            }
        }

        private void assertExpressionExecutionDateResult(Statement statementExasol, final String query, final Date expected) throws SQLException {
            final ResultSet result = statementExasol.executeQuery(query);
            result.next();
            final Date actualResult = result.getDate(1);
            MatcherAssert.assertThat(actualResult, equalTo(expected));
        }

        @ParameterizedTest
        @CsvSource(value = { //
                "C11, 2013-03-11 17:30:15.123, TIMESTAMP(3)", //
                "C12, 2013-03-11 17:30:15.123, TIMESTAMP(6)", //
                "C13, 2013-03-11 17:30:15.123, TIMESTAMP(9)", //
                "C14, 2016-08-19 11:28:05.0, TIMESTAMP(6) WITH LOCAL TIME ZONE", //
                "C15, 2018-04-30 19:00:05.0, TIMESTAMP(6) WITH LOCAL TIME ZONE" //
        })
        void testTimestampsJdbc(final String columnName, final String expectedColumnValue, final String expectedColumnType) throws SQLException {
            assumeTrue(supportTimestampPrecision());
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableName = VIRTUAL_SCHEMA_JDBC + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT " + columnName + " FROM " + qualifiedTableName;
                final String expectedExplainVirtual = "SELECT TO_TIMESTAMP(TO_CHAR(\"" + TABLE_ORACLE_ALL_DATA_TYPES
                        + "\".\"" + columnName + "\", ''YYYY-MM-DD HH24:MI:SS.FF3''), "
                        + "''YYYY-MM-DD HH24:MI:SS.FF3'') FROM \"" + SCHEMA_ORACLE + "\".\"" + TABLE_ORACLE_ALL_DATA_TYPES
                        + "\"";
                assertAll(() -> assertExpressionExecutionTimestampResult(statementExasol, query, Timestamp.valueOf(expectedColumnValue)),
                        () -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, columnName), equalTo(expectedColumnType)),
                        () -> assertExplainVirtual(statementExasol, query, expectedExplainVirtual));
            }
        }

        @ParameterizedTest
        @CsvSource(value = { //
                "C11, 2013-03-11 17:30:15.123", //
                "C12, 2013-03-11 17:30:15.123", //
                "C13, 2013-03-11 17:30:15.123", //
                "C14, 2016-08-19 11:28:05.0", //
                "C15, 2018-04-30 19:00:05.0" //
        })
        void testTimestampsJdbcWithoutTimestampPrecision(final String columnName, final String expectedColumnValue) throws SQLException {
            assumeFalse(supportTimestampPrecision());
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableName = VIRTUAL_SCHEMA_JDBC + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT " + columnName + " FROM " + qualifiedTableName;
                final String expectedExplainVirtual = "SELECT TO_TIMESTAMP(TO_CHAR(\"" + TABLE_ORACLE_ALL_DATA_TYPES
                        + "\".\"" + columnName + "\", ''YYYY-MM-DD HH24:MI:SS.FF3''), "
                        + "''YYYY-MM-DD HH24:MI:SS.FF3'') FROM \"" + SCHEMA_ORACLE + "\".\"" + TABLE_ORACLE_ALL_DATA_TYPES
                        + "\"";
                assertAll(() -> assertExpressionExecutionTimestampResult(statementExasol, query, Timestamp.valueOf(expectedColumnValue)),
                        () -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, columnName), equalTo("TIMESTAMP")),
                        () -> assertExplainVirtual(statementExasol, query, expectedExplainVirtual));
            }
        }

        @ParameterizedTest
        @CsvSource(value = { //
                "C11, 2013-03-11 17:30:15.123, TIMESTAMP(3)", //
                "C12, 2013-03-11 17:30:15.123456, TIMESTAMP(6)", //
                "C13, 2013-03-11 17:30:15.123456789, TIMESTAMP(9)", //
                "C14, 2016-08-19 19:28:05.0, TIMESTAMP(6) WITH LOCAL TIME ZONE", //
                "C15, 2018-04-30 18:00:05.0, TIMESTAMP(6) WITH LOCAL TIME ZONE" //
        })
        void testTimestampOra(final String columnName, final String expectedColumnValue, final String expectedColumnType) throws SQLException {
            assumeTrue(supportTimestampPrecision());
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                statementExasol.execute("ALTER SESSION SET TIME_ZONE = 'UTC'");
                final String qualifiedTableName = VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT " + columnName + " FROM " + qualifiedTableName;
                final String expectedExplainVirtual = "SELECT \"" + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"" + columnName
                        + "\" FROM \"" + SCHEMA_ORACLE + "\".\"" + TABLE_ORACLE_ALL_DATA_TYPES + "\"";
                assertAll(() -> assertExpressionExecutionTimestampResult(statementExasol, query, Timestamp.valueOf(expectedColumnValue)),
                        () -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, columnName), equalTo(expectedColumnType)),
                        () -> assertExplainVirtual(statementExasol, query, expectedExplainVirtual));
            }
        }

        @ParameterizedTest
        @CsvSource(value = { //
                "C11, 2013-03-11 17:30:15.123", //
                "C12, 2013-03-11 17:30:15.123", //
                "C13, 2013-03-11 17:30:15.123", //
                "C14, 2016-08-19 19:28:05.0", //
                "C15, 2018-04-30 18:00:05.0" //
        })
        void testTimestampOraWithoutTimestampPrecision(final String columnName, final String expectedColumnValue) throws SQLException {
            assumeFalse(supportTimestampPrecision());
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                statementExasol.execute("ALTER SESSION SET TIME_ZONE = 'UTC'");
                final String qualifiedTableName = VIRTUAL_SCHEMA_ORACLE + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT " + columnName + " FROM " + qualifiedTableName;
                final String expectedExplainVirtual = "SELECT \"" + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"" + columnName
                        + "\" FROM \"" + SCHEMA_ORACLE + "\".\"" + TABLE_ORACLE_ALL_DATA_TYPES + "\"";
                assertAll(() -> assertExpressionExecutionTimestampResult(statementExasol, query, Timestamp.valueOf(expectedColumnValue)),
                        () -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, columnName), equalTo("TIMESTAMP")),
                        () -> assertExplainVirtual(statementExasol, query, expectedExplainVirtual));
            }
        }

        private void assertExpressionExecutionTimestampResult(Statement statementExasol, final String query, final Timestamp expected)
                throws SQLException {
            final ResultSet result = statementExasol.executeQuery(query);
            result.next();
            final Timestamp actual = result.getTimestamp(1);
            MatcherAssert.assertThat(actual, equalTo(expected));
        }

        @ParameterizedTest
        @ValueSource(strings = { VIRTUAL_SCHEMA_JDBC, VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING })
        void testIntervalYear(final String virtualSchemaName) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableName = virtualSchemaName + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT C16 FROM " + qualifiedTableName;
                final String expectedExplainVirtual = "SELECT CAST(TO_CHAR(\"" + TABLE_ORACLE_ALL_DATA_TYPES
                        + "\".\"C16\") AS VARCHAR(4000)) FROM \"" + SCHEMA_ORACLE + "\".\""
                        + TABLE_ORACLE_ALL_DATA_TYPES + "\"";
                assertAll(
                        () -> assertExpressionExecutionStringResult(statementExasol, query, "+54-02"),
                        () -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, "C16"),
                                equalTo("VARCHAR(4000) UTF8")),
                        () -> assertExplainVirtual(statementExasol, query, expectedExplainVirtual));
            }
        }

        @ParameterizedTest
        @ValueSource(strings = { VIRTUAL_SCHEMA_JDBC, VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING })
        void testIntervalDay(final String virtualSchemaName) throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                final String qualifiedTableName = virtualSchemaName + "." + TABLE_ORACLE_ALL_DATA_TYPES;
                final String query = "SELECT C17 FROM " + qualifiedTableName + " ORDER BY 1";
                final String expectedExplainVirtual = "SELECT CAST(TO_CHAR(\"" + TABLE_ORACLE_ALL_DATA_TYPES
                        + "\".\"C17\") AS VARCHAR(4000)) FROM \"" + SCHEMA_ORACLE + "\".\"" + TABLE_ORACLE_ALL_DATA_TYPES
                        + "\" ORDER BY CAST(TO_CHAR(\"" + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"C17\") AS VARCHAR(4000))";
                assertAll(() -> assertExpressionExecutionStringResults(statementExasol, query, "+01", "+02"),
                        () -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, "C17"),
                                equalTo("VARCHAR(4000) UTF8")),
                        () -> assertExplainVirtual(statementExasol, query, expectedExplainVirtual));
            }
        }

        @ParameterizedTest
        @CsvSource(value = {
                "VIRTUAL_SCHEMA_JDBC ! ('2018-01-01 11:00:00.0', '2018-01-01 11:00:00.0', '2018-01-01 11:00:00.000')", //
                "VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING ! ('2018-01-01 11:00:00.0', '2018-01-01 10:00:00.0', '2018-01-01 10:00:00.000')" }, //
                delimiter = '!')
        void testSelectAllTimestampColumns(final String virtualSchemaName, final String expectedColumnValue)
                throws SQLException {
            try (Connection connection = getExasolConnection();
                 Statement statementExasol = connection.createStatement()) {
                statementExasol.execute("ALTER SESSION SET TIME_ZONE = 'UTC'");
                final String qualifiedTableName = virtualSchemaName + "." + TABLE_ORACLE_TIMESTAMPS;
                final String query = "SELECT * FROM " + qualifiedTableName;
                final ResultSet expected = getExpectedResultSet(statementExasol, "(A TIMESTAMP, B TIMESTAMP, C TIMESTAMP)",
                        expectedColumnValue);
                final ResultSet actual = statementExasol.executeQuery(query);
                assertAll(() -> assertThat(actual, matchesResultSet(expected)),
                        () -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, "A"), equalTo("TIMESTAMP(6)")),
                        () -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, "B"), equalTo("TIMESTAMP(6) WITH LOCAL TIME ZONE")),
                        () -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, "C"), equalTo("TIMESTAMP(6) WITH LOCAL TIME ZONE")));
            }
        }
    }
}
