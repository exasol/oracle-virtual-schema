package com.exasol.adapter.dialects.oracle;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.bucketfs.BucketAccessException;

@Tag("integration")
@Testcontainers
public class OracleSqlDialectL3IssuesIT extends CommonOracleIntegrationTestSetup {

    @BeforeAll
    static void beforeAll() throws SQLException, BucketAccessException, TimeoutException, IOException {
        setupOracle(s -> createOracleTableNumericAndTimestamp(s));
        setupExasolContainer();
    }

    private static void createOracleTableNumericAndTimestamp(final Statement statementOracle) throws SQLException {
        final String qualifiedTableName = SCHEMA_ORACLE + "." + TABLE_ORACLE_ALL_DATA_TYPES;
        statementOracle.execute("CREATE TABLE " + qualifiedTableName + " (" //
                + "num10 number(10),	" //
                + "num38 number(38), " //
                + "varchar20 varchar2(20 byte), " //
                + "dates date, " //
                + "timestamps timestamp(6) " //
                + ")");
        statementOracle.execute("INSERT INTO " + qualifiedTableName + "(num10, num38, varchar20, dates, timestamps) VALUES (" //
                + "10, " // num10
                + "38, " // num38
                + "'abcdefghijabcdefghij', " // varchar20
                + "TO_DATE('12/06/2025 08:05:44', 'DD/MM/YYYY HH24:MI:SS'), " // dates
                + "TO_TIMESTAMP('12/06/2025 08:07:09.576581','DD/MM/YYYY HH24:MI:SS,FF')" // timestamps
                + ")");

    }

    @ParameterizedTest
    @ValueSource(strings = { VIRTUAL_SCHEMA_JDBC,
            VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING,
            VIRTUAL_SCHEMA_JDBC_NUMBER_TO_DECIMAL,
            VIRTUAL_SCHEMA_ORACLE_NUMBER_TO_DECIMAL_JDBC_MAPPING })
    void testToNumberTwice(final String virtualSchemaName) throws SQLException {
        try (Connection connection = getExasolConnection();
                Statement statementExasol = connection.createStatement()) {
            final String qualifiedTableName = virtualSchemaName + "." + TABLE_ORACLE_ALL_DATA_TYPES;
            final String query = "SELECT to_number(num10), to_number(num10) FROM " + qualifiedTableName;
            final String expectedExplainVirtualImport = "IMPORT INTO (c1 DECIMAL(10, 0), c2 DECIMAL(10, 0)) FROM ";
            final String expectedExplainVirtualSelect = "SELECT TO_NUMBER(\"" + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"NUM10\") as c0, "
                    + "TO_NUMBER(\"" + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"NUM10\") as c1 FROM \""
                    + SCHEMA_ORACLE + "\".\"" + TABLE_ORACLE_ALL_DATA_TYPES;
            assertAll(
                    () -> assertBigDecimalResults(statementExasol, query,
                            new BigDecimal("10"), new BigDecimal("10")),
                    () -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, "NUM10"),
                            equalTo("DECIMAL(10,0)")),
                    () -> assertExplainVirtual(statementExasol, query, expectedExplainVirtualImport),
                    () -> assertExplainVirtual(statementExasol, query, expectedExplainVirtualSelect));
        }
    }

    @ParameterizedTest
    @CsvSource(value = { "VIRTUAL_SCHEMA_JDBC | VARCHAR(38) UTF8", //
            "VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING | VARCHAR(38) UTF8", //
            "VIRTUAL_SCHEMA_JDBC_NUMBER_TO_DECIMAL | DECIMAL(36,1)", //
            "VIRTUAL_SCHEMA_ORACLE_NUMBER_TO_DECIMAL_JDBC_MAPPING | DECIMAL(36,1)" }, delimiter = '|')
    void testToNumberTwiceAndCheckOrder(final String virtualSchemaName, final String expectedNum38Type) throws SQLException {
        try (Connection connection = getExasolConnection();
                Statement statementExasol = connection.createStatement()) {
            final String qualifiedTableName = virtualSchemaName + "." + TABLE_ORACLE_ALL_DATA_TYPES;
            final String query = "SELECT to_number(num10), to_number(num38), to_number(num10), to_number(num38) FROM " + qualifiedTableName;
            assertAll(
                    () -> assertBigDecimalResults(statementExasol, query,
                            new BigDecimal("10"), new BigDecimal("38"), new BigDecimal("10"), new BigDecimal("38")),
                    () -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, "NUM10"),
                            equalTo("DECIMAL(10,0)")),
                    () -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, "NUM38"),
                            equalTo(expectedNum38Type)));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = { VIRTUAL_SCHEMA_JDBC, VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING, VIRTUAL_SCHEMA_JDBC_NUMBER_TO_DECIMAL,
            VIRTUAL_SCHEMA_ORACLE_NUMBER_TO_DECIMAL_JDBC_MAPPING })
    void testToChar(final String virtualSchemaName) throws SQLException {
        try (Connection connection = getExasolConnection();
                Statement statementExasol = connection.createStatement()) {
            final String qualifiedTableName = virtualSchemaName + "." + TABLE_ORACLE_ALL_DATA_TYPES;
            final String query = "SELECT to_char(num10) FROM " + qualifiedTableName;
            assertAll(
                    () -> assertStringResults(statementExasol, query, "10"),
                    () -> assertThat(getColumnTypesOfTable(statementExasol, qualifiedTableName, "NUM10"),
                            equalTo("DECIMAL(10,0)")));
        }
    }

    @Test
    void testCurrentTimestamp() throws SQLException {
        try (Connection connection = getExasolConnection();
                Statement statementExasol = connection.createStatement()) {
            final String qualifiedTableName = VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING + "." + TABLE_ORACLE_ALL_DATA_TYPES;
            final String query = "SELECT current_timestamp FROM " + qualifiedTableName;
            final Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            assertAll(
                    () -> assertTimestampResultsLater(statementExasol, query, currentTime));
        }
    }

    @Test
    void testToCharAndCurrentTimestamp() throws SQLException {
        try (Connection connection = getExasolConnection();
                Statement statementExasol = connection.createStatement()) {
            final String qualifiedTableName = VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING + "." + TABLE_ORACLE_ALL_DATA_TYPES;
            final String query = "SELECT to_char(num10), to_char(varchar20), current_timestamp FROM " + qualifiedTableName;
            final Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
            final String expectedExplainVirtualImport = "IMPORT INTO (c1 VARCHAR(11) UTF8, c2 VARCHAR(20) UTF8, c3 TIMESTAMP WITH LOCAL TIME ZONE) FROM ";
            final String expectedExplainVirtualSelect = "SELECT "
                    + "TO_CHAR(\"" + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"NUM10\"), "
                    + "TO_CHAR(\"" + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"VARCHAR20\"), "
                    + "CURRENT_TIMESTAMP FROM \"" + SCHEMA_ORACLE + "\".\"" + TABLE_ORACLE_ALL_DATA_TYPES;
            assertAll(
                    () -> assertTimestampAndStringResults(statementExasol, query, currentTimestamp, "10", "abcdefghijabcdefghij"),
                    () -> assertExplainVirtual(statementExasol, query, expectedExplainVirtualImport),
                    () -> assertExplainVirtual(statementExasol, query, expectedExplainVirtualSelect));
        }
    }

    @Test
    void testDateAndTimestamp() throws SQLException, ParseException {
        try (Connection connection = getExasolConnection();
                Statement statementExasol = connection.createStatement()) {
            final String qualifiedTableName = VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING + "." + TABLE_ORACLE_ALL_DATA_TYPES;
            final String query = "SELECT dates, timestamps FROM " + qualifiedTableName;

            final String expectedExplainVirtualImport = "IMPORT INTO (c1 TIMESTAMP(0), c2 TIMESTAMP(6)) FROM ";
            final String expectedExplainVirtualSelect = "SELECT \""
                    + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"DATES\", \""
                    + TABLE_ORACLE_ALL_DATA_TYPES + "\".\"TIMESTAMPS\""
                    + " FROM \"" + SCHEMA_ORACLE + "\".\"" + TABLE_ORACLE_ALL_DATA_TYPES;

            // Oracle TO_DATE('12/06/2025 08:05:44', 'DD/MM/YYYY HH24:MI:SS')
            final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            final java.util.Date parsedDate = dateFormat.parse("12/06/2025 08:05:44");
            final Timestamp timestamp1 = new Timestamp(parsedDate.getTime());

            // Oracle TO_TIMESTAMP('12/06/2025 08:07:09,576581','DD/MM/YYYY HH24:MI:SS,FF')
            final String oracleTimestampString = "12/06/2025 08:07:09.576581";
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSSSSS");

            final LocalDateTime localDateTime = LocalDateTime.parse(oracleTimestampString, formatter);
            final Timestamp timestamp2 = Timestamp.valueOf(localDateTime);

            assertAll(
                    () -> assertTimestampResults(statementExasol, query, timestamp1, timestamp2),
                    () -> assertExplainVirtual(statementExasol, query, expectedExplainVirtualImport),
                    () -> assertExplainVirtual(statementExasol, query, expectedExplainVirtualSelect));
        }
    }

    protected static void assertTimestampAndStringResults(final Statement statementExasol,
            final String query,
            final Timestamp expectedTimestamp,
            final String expectedString1,
            final String expectedString2)
            throws SQLException {
        final ResultSet result = statementExasol.executeQuery(query);
        result.next();
        final String actualStringResult1 = result.getString(1);
        assertThat(actualStringResult1, equalTo(expectedString1));
        final String actualStringResult2 = result.getString(2);
        assertThat(actualStringResult2, equalTo(expectedString2));
        final Timestamp actualTimestampResult = result.getTimestamp(3);
        assertThat(actualTimestampResult.compareTo(expectedTimestamp), greaterThan(0));
    }
}
