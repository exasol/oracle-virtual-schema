package com.exasol.adapter.dialects.oracle;

import static com.exasol.adapter.dialects.oracle.ExasolVersionCheck.assumeExasolVersion834OrLater;
import static com.exasol.adapter.dialects.oracle.IntegrationTestConstants.*;
import static com.exasol.adapter.dialects.oracle.OracleVirtualSchemaIntegrationTestSetup.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Path;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.testcontainers.junit.jupiter.Container;

import com.exasol.adapter.dialects.oracle.helper.ThrowsSqlConsumer;
import com.exasol.adapter.dialects.oracle.release.ExasolDbVersion;
import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.containers.ExasolContainer;
import com.exasol.containers.ExasolDockerImageReference;
import com.exasol.containers.ExasolService;
import com.exasol.dbbuilder.dialects.exasol.*;
import com.exasol.udfdebugging.UdfTestSetup;

/**
 * Abstract base class for Oracle-to-Exasol Virtual Schema integration test setup.
 * <p>
 * This class manages containerized Oracle and Exasol databases and provides methods to
 * initialize schemas, connections, and virtual schemas for integration testing of the Oracle Virtual Schema adapter.
 * It sets up:
 * <ul>
 *     <li>Test schemas and tables on an Oracle container</li>
 *     <li>JDBC and OCI connections in Exasol</li>
 *     <li>Virtual schemas using JDBC and Oracle native connectivity</li>
 *     <li>Exasol adapter scripts and dependencies in BucketFS</li>
 * </ul>
 *
 * <p>
 * This setup supports various test scenarios, including:
 * <ul>
 *     <li>Basic connectivity via JDBC and Oracle OCI</li>
 *     <li>Oracle NUMBER-to-DECIMAL handling in Exasol</li>
 *     <li>Pushdown capabilities for timestamps, functions, and joins</li>
 * </ul>
 *
 * <p>
 * Subclasses should call initialization methods, similar to {@link #initAllTables()} in {@code @BeforeAll}
 *
 * <p><strong>Note:</strong> This class assumes that Docker is available on the test machine.
 *
 */
abstract class CommonOracleIntegrationTestSetup {
    protected static final String ORACLE_CONTAINER_NAME = IntegrationTestConstants.ORACLE_CONTAINER_NAME;

    protected static final String SCHEMA_ORACLE = "SCHEMA_ORACLE_" + System.currentTimeMillis();

    protected static final String ORACLE_JDBC_CONNECTION_NAME = "JDBC_CONNECTION";
    protected static final String ORACLE_OCI_CONNECTION_NAME = "ORACLE_CONNECTION";

    protected static final String VIRTUAL_SCHEMA_JDBC = "VIRTUAL_SCHEMA_JDBC";
    protected static final String VIRTUAL_SCHEMA_ORACLE = "VIRTUAL_SCHEMA_ORACLE";
    protected static final String VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING = "VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING";
    protected static final String VIRTUAL_SCHEMA_JDBC_NUMBER_TO_DECIMAL = "VIRTUAL_SCHEMA_JDBC_NUMBER_TO_DECIMAL";
    protected static final String VIRTUAL_SCHEMA_ORACLE_NUMBER_TO_DECIMAL = "VIRTUAL_SCHEMA_ORACLE_NUMBER_TO_DECIMAL";
    protected static final String VIRTUAL_SCHEMA_ORACLE_NUMBER_TO_DECIMAL_JDBC_MAPPING = "VIRTUAL_SCHEMA_ORACLE_NUMBER_TO_DECIMAL_JDBC_MAPPING";
    protected static final String TABLE_ORACLE_ALL_DATA_TYPES = "TABLE_ORACLE_ALL_DATA_TYPES";
    protected static final String TABLE_ORACLE_NUMBER_HANDLING = "TABLE_ORACLE_NUMBER_HANDLING";
    protected static final String TABLE_ORACLE_TIMESTAMPS = "TABLE_ORACLE_TIMESTAMPS";

    @Container
    @SuppressWarnings("resource") // Will be closed by @Container
    private static final ExasolContainer<? extends ExasolContainer<?>> exasolContainer = new ExasolContainer<>(
            EXASOL_VERSION).withRequiredServices(ExasolService.BUCKETFS, ExasolService.UDF).withReuse(true);
    @Container
    protected static final OracleContainerDBA oracleContainer = new OracleContainerDBA(ORACLE_CONTAINER_NAME);

    /**
     * Initializes the Oracle and Exasol database containers with test schemas and data.
     * <p>
     * This method prepares the Oracle database with test tables and the Exasol container
     * with adapter scripts, UDFs, and virtual schemas necessary for integration tests.
     *
     * @throws BucketAccessException if the adapter or drivers fail to upload to BucketFS
     * @throws TimeoutException      if a timeout occurs while accessing BucketFS
     * @throws SQLException          if database operations fail
     * @throws IOException           if driver or client downloads fail
     */
    static void initAllTables()
            throws BucketAccessException, TimeoutException, SQLException, IOException {
        setupOracleAllTables();
        setupExasolContainer();
    }

    /**
     * Cleans up the Exasol container after all tests have run.
     * <p>
     * This removes all database objects and ensures a clean state.
     */
    @AfterAll
    static void afterAll() {
        exasolContainer.purgeDatabase();
    }

    /**
     * Uploads the correct Oracle Instant Client version to the BucketFS based on Exasol version.
     *
     * @param bucket the BucketFS bucket to upload to
     * @return the same bucket instance, after the Instant Client has been uploaded
     * @throws BucketAccessException if uploading fails
     * @throws TimeoutException      if the upload operation times out
     * @throws IOException           if reading or downloading the zip fails
     */
    private static Bucket uploadInstantClientToBucket(Bucket bucket)
            throws BucketAccessException, TimeoutException, IOException {
        if (is832OrLater()) {
            return uploadInstantClient23(bucket);
        } else {
            return uploadInstantClient12(bucket);
        }
    }

    /**
     * Uploads Oracle Instant Client 12.1.0.2.0 to the given BucketFS path.
     *
     * @param bucket the BucketFS bucket to upload to
     * @return the same bucket instance, after upload
     * @throws BucketAccessException     if the upload fails
     * @throws TimeoutException          if the upload operation times out
     * @throws FileNotFoundException     if the Instant Client file is not found
     */
    private static Bucket uploadInstantClient12(Bucket bucket)
            throws BucketAccessException, TimeoutException, FileNotFoundException {
        final String instantClientName = "instantclient-basic-linux.x64-12.1.0.2.0.zip";
        final String instantClientPath = "src/test/resources/integration/driver/oracle";
        bucket.uploadFile(Path.of(instantClientPath, instantClientName), "drivers/oracle/" + instantClientName);
        return bucket;
    }

    /**
     * Downloads Oracle Instant Client 23.5 from Oracle and uploads it to BucketFS.
     *
     * @param bucket the BucketFS bucket to upload to
     * @return the same bucket instance, after upload
     * @throws BucketAccessException if the upload fails
     * @throws TimeoutException      if the upload operation times out
     * @throws IOException           if the file cannot be downloaded or written
     */
    private static Bucket uploadInstantClient23(Bucket bucket)
            throws BucketAccessException, TimeoutException, IOException {
        final String fileName = "instantclient-basic-linux.x64-23.5.0.24.07.zip";
        final String downloadUrl = "https://download.oracle.com/otn_software/linux/instantclient/2350000/" + fileName;

        // Download to a temporary file
        final File tempFile = File.createTempFile("instantclient", ".zip");
        tempFile.deleteOnExit();

        try (BufferedInputStream in = new BufferedInputStream(new URL(downloadUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
            in.transferTo(fileOutputStream);
        }

        // Upload to BucketFS path: drivers/oracle/<filename>
        bucket.uploadFile(tempFile.toPath(), "drivers/oracle/" + fileName);

        return bucket;
    }

    /**
     * Prepares the Exasol container for testing by uploading the Oracle JDBC driver, adapter script,
     * and creating virtual schemas using OCI and JDBC connections.
     *
     * @throws BucketAccessException if the upload to BucketFS fails
     * @throws TimeoutException      if uploading takes too long
     * @throws SQLException          if Exasol SQL operations fail
     * @throws IOException           if adapter or client upload fails
     */
    protected static void setupExasolContainer()
            throws BucketAccessException, TimeoutException, SQLException, IOException {
        final Connection exasolConnection = exasolContainer.createConnectionForUser(exasolContainer.getUsername(),
                exasolContainer.getPassword());
        assumeExasolVersion834OrLater(exasolContainer);
        Bucket bucket = uploadInstantClientToBucket(exasolContainer.getDefaultBucket());
        uploadOracleJDBCDriverToBucket(exasolContainer);
        uploadAdapterToBucket(bucket);

        final UdfTestSetup udfTestSetup = new UdfTestSetup(getTestHostIpFromInsideExasol(exasolContainer),
                bucket, exasolConnection);
        final ExasolObjectFactory exasolFactory = new ExasolObjectFactory(exasolContainer.createConnection(""),
                ExasolObjectConfiguration.builder().withJvmOptions(udfTestSetup.getJvmOptions()).build());
        final ExasolSchema schema = exasolFactory.createSchema(SCHEMA_EXASOL);

        final AdapterScript adapterScript = createAdapterScript(schema);

        final Integer mappedPort = oracleContainer.getMappedPort(ORACLE_PORT);
        final String oracleUsername = "SYSTEM";
        final String oraclePassword = "test";
        createOracleOCIConnection(exasolFactory, mappedPort, oracleUsername, oraclePassword);
        final ConnectionDefinition jdbcConnectionDefinition = createOracleJDBCConnection(oracleUsername, oraclePassword,
                exasolFactory);

        createVirtualSchemasOnExasolDbContainer(exasolFactory, adapterScript, jdbcConnectionDefinition);
    }

    /**
     * Creates an Oracle OCI connection definition in Exasol using the given credentials.
     *
     * @param exasolFactory   factory for creating Exasol database objects
     * @param mappedPort      mapped port of the Oracle container
     * @param oracleUsername  Oracle username
     * @param oraclePassword  Oracle password
     * @return a connection definition object for the OCI connection
     */
    private static ConnectionDefinition createOracleOCIConnection(final ExasolObjectFactory exasolFactory,
                                                                  final Integer mappedPort, final String oracleUsername, final String oraclePassword) {
        final String hostIp = getTestHostIpFromInsideExasol(exasolContainer);
        final String oraConnectionString = "(DESCRIPTION =" //
                + "(ADDRESS_LIST = (ADDRESS = (PROTOCOL = TCP)" //
                + "(HOST = " + hostIp + " )" //
                + "(PORT = " + mappedPort + ")))" //
                + "(CONNECT_DATA = (SERVER = DEDICATED)" //
                + "(SERVICE_NAME = " + oracleContainer.getDatabaseName() + ")))";
        return exasolFactory.createConnectionDefinition(ORACLE_OCI_CONNECTION_NAME, oraConnectionString, oracleUsername,
                oraclePassword);
    }

    /**
     * Creates a JDBC connection definition in Exasol pointing to the Oracle container.
     *
     * @param oracleUsername     Oracle username
     * @param oraclePassword     Oracle password
     * @param exasolFactory      factory for creating Exasol database objects
     * @return JDBC connection definition
     */
    private static ConnectionDefinition createOracleJDBCConnection(final String oracleUsername,
                                                                   final String oraclePassword, final ExasolObjectFactory exasolFactory) {
        final String hostIp = getTestHostIpFromInsideExasol(exasolContainer);
        final String jdbcConnectionString = "jdbc:oracle:thin:@" + hostIp + ":"
                + oracleContainer.getOraclePort() + "/" + oracleContainer.getDatabaseName();

        return exasolFactory.createConnectionDefinition(ORACLE_JDBC_CONNECTION_NAME, jdbcConnectionString,
                oracleUsername, oraclePassword);

    }

    /**
     * Creates various virtual schemas in Exasol, both using plain JDBC and Oracle OCI,
     * including number-to-decimal mapping configurations.
     *
     * @param exasolFactory            factory for creating virtual schemas
     * @param adapterScript            the adapter script object
     * @param jdbcConnectionDefinition JDBC connection to Oracle
     */
    private static void createVirtualSchemasOnExasolDbContainer(final ExasolObjectFactory exasolFactory,
                                                                final AdapterScript adapterScript, final ConnectionDefinition jdbcConnectionDefinition) {
        exasolFactory.createVirtualSchemaBuilder(VIRTUAL_SCHEMA_JDBC).adapterScript(adapterScript)
                .connectionDefinition(jdbcConnectionDefinition).properties(Map.of("SCHEMA_NAME", SCHEMA_ORACLE))
                .build();
        exasolFactory.createVirtualSchemaBuilder(VIRTUAL_SCHEMA_JDBC_NUMBER_TO_DECIMAL).adapterScript(adapterScript)
                .connectionDefinition(jdbcConnectionDefinition).properties(Map.of("SCHEMA_NAME", SCHEMA_ORACLE))
                .properties(Map.of("SCHEMA_NAME", SCHEMA_ORACLE,
                        "ORACLE_CAST_NUMBER_TO_DECIMAL_WITH_PRECISION_AND_SCALE", "36,1"))
                .build();
        exasolFactory.createVirtualSchemaBuilder(VIRTUAL_SCHEMA_ORACLE).adapterScript(adapterScript)
                .connectionDefinition(jdbcConnectionDefinition).properties(Map.of("SCHEMA_NAME", SCHEMA_ORACLE,
                        "IMPORT_FROM_ORA", "true", "ORA_CONNECTION_NAME", ORACLE_OCI_CONNECTION_NAME))
                .build();
        exasolFactory.createVirtualSchemaBuilder(VIRTUAL_SCHEMA_ORACLE_JDBC_MAPPING).adapterScript(adapterScript)
                .connectionDefinition(jdbcConnectionDefinition)
                .properties(Map.of("SCHEMA_NAME", SCHEMA_ORACLE, "IMPORT_FROM_ORA", "true", "ORA_CONNECTION_NAME",
                        ORACLE_OCI_CONNECTION_NAME, "GENERATE_JDBC_DATATYPE_MAPPING_FOR_OCI", "true"))
                .build();

        exasolFactory.createVirtualSchemaBuilder(VIRTUAL_SCHEMA_ORACLE_NUMBER_TO_DECIMAL).adapterScript(adapterScript)
                .connectionDefinition(jdbcConnectionDefinition)
                .properties(Map.of("SCHEMA_NAME", SCHEMA_ORACLE, "IMPORT_FROM_ORA", "true", "ORA_CONNECTION_NAME",
                        ORACLE_OCI_CONNECTION_NAME, "oracle_cast_number_to_decimal_with_precision_and_scale", "36,1"))
                .build();
        exasolFactory.createVirtualSchemaBuilder(VIRTUAL_SCHEMA_ORACLE_NUMBER_TO_DECIMAL_JDBC_MAPPING)
                .adapterScript(adapterScript).connectionDefinition(jdbcConnectionDefinition)
                .properties(Map.of("SCHEMA_NAME", SCHEMA_ORACLE, "IMPORT_FROM_ORA", "true",
                        "GENERATE_JDBC_DATATYPE_MAPPING_FOR_OCI", "true", "ORA_CONNECTION_NAME",
                        ORACLE_OCI_CONNECTION_NAME, "oracle_cast_number_to_decimal_with_precision_and_scale", "36,1"))
                .build();
        //
    }

    /**
     * Executes custom Oracle setup logic inside the Oracle container.
     *
     * @param tableCreator a function that accepts a {@link Statement} to create Oracle tables or other setup
     * @throws SQLException if executing SQL statements fails
     */
    protected static void setupOracle(ThrowsSqlConsumer<Statement> tableCreator) throws SQLException {
        try (Connection oracleConnection = oracleContainer.createConnectionDBA("");
             Statement statementOracle = oracleConnection.createStatement()) {
            createOracleUser(statementOracle);
            grantAdditionalRights(statementOracle);
            tableCreator.accept(statementOracle);
        }
    }

    /**
     * Creates all required test tables and schemas in the Oracle database.
     *
     * @throws SQLException if any SQL execution fails
     */
    private static void setupOracleAllTables() throws SQLException {
        try (Connection oracleConnection = oracleContainer.createConnectionDBA("");
             Statement statementOracle = oracleConnection.createStatement()) {
            createOracleUser(statementOracle);
            grantAdditionalRights(statementOracle);
            createOracleTableAllDataTypes(statementOracle);
            createOracleTableNumberHandling(statementOracle);
            createOracleTableTimestamps(statementOracle);
            createTestTablesForJoinTests(statementOracle, SCHEMA_ORACLE);
        }
    }

    private static void grantAdditionalRights(final Statement statementOracle) throws SQLException {
        statementOracle.execute("GRANT CONNECT TO test");
        statementOracle.execute("GRANT CREATE SESSION TO test");
        statementOracle.execute("GRANT UNLIMITED TABLESPACE TO test");
    }

    private static void createOracleUser(final Statement statementOracle) throws SQLException {
        final String username = SCHEMA_ORACLE;
        final String password = SCHEMA_ORACLE;
        statementOracle.execute("CREATE USER " + username + " IDENTIFIED BY " + password);
        statementOracle.execute("GRANT CONNECT TO " + username);
        statementOracle.execute("GRANT CREATE SESSION TO " + username);
        statementOracle.execute("GRANT UNLIMITED TABLESPACE TO " + username);
    }

    private static void createOracleTableAllDataTypes(final Statement statementOracle) throws SQLException {
        final String qualifiedTableName = SCHEMA_ORACLE + "." + TABLE_ORACLE_ALL_DATA_TYPES;
        statementOracle.execute("CREATE TABLE " + qualifiedTableName + " (" //
                + "c1 char(50),	" //
                + "c2 nchar(50), " //
                + "c3 varchar2(50), " //
                + "c4 nvarchar2(50), " //
                + "c5 number, " //
                + "c_number36 number(36), " //
                + "c6 number(38), " //
                + "c7 number(10,5), " //
                + "c8 number(38,5), " //
                + "c9 number(38,38), " //
                + "c_binfloat binary_float, " //
                + "c_bindouble binary_double, " //
                + "c10 date, " //
                + "c11 timestamp(3), " //
                + "c12 timestamp, " //
                + "c13 timestamp(9), " //
                + "c14 timestamp with time zone, " //
                + "c15 timestamp with local time zone, " //
                + "c16 interval year to month, " //
                + "c17 interval day to second, " //
                + "c18 blob, " //
                + "c19 clob,	" //
                + "c20 nclob, " //
                + "c_float float, " //
                + "c_float126 float(126), " //
                + "c_long long " //
                + ")");
        statementOracle.execute("INSERT INTO " + qualifiedTableName + " VALUES (" //
                + "'aaaaaaaaaaaaaaaaaaaa', " //
                + "'bbbbbbbbbbbbbbbbbbbb', " //
                + "'cccccccccccccccccccc', " //
                + "'dddddddddddddddddddd', " //
                + "123456789012345678901234567890123456, " // C5
                + "123456789012345678901234567890123456, " // c_number36
                + "12345678901234567890123456789012345678, " // C6
                + "12345.12345, " // C7
                + "123456789012345678901234567890123.45678, " // C8
                + "0.12345678901234567890123456789012345678, " // C9
                + "1234.1241723, " // C_BINFLOAT
                + "1234987.120871234, " // C_BINDOUBLE
                + "TO_DATE('2016-08-19', 'YYYY-MM-DD'), " // C10
                + "TO_TIMESTAMP('2013-03-11 17:30:15.123', 'YYYY-MM-DD HH24:MI:SS.FF'), " // C11
                + "TO_TIMESTAMP('2013-03-11 17:30:15.123456', 'YYYY-MM-DD HH24:MI:SS.FF'), " // C12
                + "TO_TIMESTAMP('2013-03-11 17:30:15.123456789', 'YYYY-MM-DD HH24:MI:SS.FF'), " // C13
                + "TO_TIMESTAMP_TZ('2016-08-19 11:28:05 -08:00', 'YYYY-MM-DD HH24:MI:SS TZH:TZM'), " // C14
                + "TO_TIMESTAMP_TZ('2018-04-30 10:00:05 -08:00', 'YYYY-MM-DD HH24:MI:SS TZH:TZM'), " // C15
                + "'54-2', " // C16
                + "'1 11:12:10.123', " // C17
                + "'0102030405060708090a0b0c0d0e0f', " // C18
                + "'0987asdlfkjq2222qawsf;lkja09ed8q2w;43lkrjasdf09uqaw43lkjra0-98sf[iqjw4,mfas[dpiuj[qa09w44', " // C19
                + "'0987asdlfkjq2222qawsf;lkja09ed8q2w;43lkrjasdf09uqaw43lkjra0-98sf[iqjw4,mfas[dpiuj[qa09w44', " // C20
                + "12345.01982348239, " // c_float
                + "12345678.01234567901234567890123456789, " // c_float126
                + "'test long 123' " // long
                + ")");
        statementOracle.execute("INSERT INTO " + qualifiedTableName + "(c3, c5, c7, c_binfloat, c17) VALUES (" //
                + "'cccccccccccccccccccc', " // C3
                + "1234567890.123456789, " // C5
                + "12355.12345, " // C7
                + "123.12345687987654321, " // C_BINFLOAT
                + "'2 02:03:04.123456' " // C17
                + ")");
    }

    private static void createOracleTableNumberHandling(final Statement statementOracle) throws SQLException {
        final String qualifiedTableName = SCHEMA_ORACLE + "." + TABLE_ORACLE_NUMBER_HANDLING;
        statementOracle.execute("CREATE TABLE " + qualifiedTableName + " (" //
                + "a number,	" //
                + "b number(38, 10), " //
                + "c number(36,2) " //
                + ")");
        statementOracle.execute("INSERT INTO " + qualifiedTableName + " VALUES (" //
                + "1234567890123456789012345678901234.56, " //
                + "1234567890123456789012345678.9012345678, " //
                + "1234567890123456789012345678901234.56 " //
                + ")");
    }

    private static void createOracleTableTimestamps(final Statement statementOracle) throws SQLException {
        final String qualifiedTableName = SCHEMA_ORACLE + "." + TABLE_ORACLE_TIMESTAMPS;
        statementOracle.execute("CREATE TABLE " + qualifiedTableName + " (" //
                + "a timestamp,	" //
                + "b timestamp with local time zone, " //
                + "c timestamp with time zone " //
                + ")");
        statementOracle.execute("INSERT INTO " + qualifiedTableName + " VALUES (" //
                + "timestamp '2018-01-01 11:00:00', " //
                + "timestamp '2018-01-01 11:00:00 +01:00', " //
                + "timestamp '2018-01-01 11:00:00 +01:00' " //
                + ")");
    }

    private static void createTestTablesForJoinTests(final Statement statementOracle, final String schemaName)
            throws SQLException {
        statementOracle.execute("CREATE TABLE " + schemaName + "." + TABLE_JOIN_1 + "(x INT, y VARCHAR(100))");
        statementOracle.execute("INSERT INTO " + schemaName + "." + TABLE_JOIN_1 + " VALUES (1,'aaa')");
        statementOracle.execute("INSERT INTO " + schemaName + "." + TABLE_JOIN_1 + " VALUES (2,'bbb')");
        statementOracle.execute("CREATE TABLE " + schemaName + "." + TABLE_JOIN_2 + "(x INT, y VARCHAR(100))");
        statementOracle.execute("INSERT INTO " + schemaName + "." + TABLE_JOIN_2 + " VALUES (2,'bbb')");
        statementOracle.execute("INSERT INTO " + schemaName + "." + TABLE_JOIN_2 + " VALUES (3,'ccc')");
    }

    protected ResultSet getExpectedResultSet(Statement statementExasol, final List<String> expectedColumns, final List<String> expectedRows)
            throws SQLException {
        final String expectedValues = expectedRows.stream().map(row -> "(" + row + ")")
                .collect(Collectors.joining(","));
        final String qualifiedExpectedTableName = SCHEMA_EXASOL + ".EXPECTED";
        statementExasol.execute("CREATE OR REPLACE TABLE " + qualifiedExpectedTableName + "("
                + String.join(", ", expectedColumns) + ")");
        statementExasol.execute("INSERT INTO " + qualifiedExpectedTableName + " VALUES" + expectedValues);
        return statementExasol.executeQuery("SELECT * FROM " + qualifiedExpectedTableName);
    }

    protected ResultSet getExpectedResultSet(Statement statementExasol, final String expectedColumnTypes, final String expectedValues)
            throws SQLException {
        final String qualifiedExpectedTableName = SCHEMA_EXASOL + "." + "EXPECTED";
        statementExasol.execute("CREATE OR REPLACE TABLE " + qualifiedExpectedTableName + expectedColumnTypes);
        statementExasol.execute("INSERT INTO " + qualifiedExpectedTableName + " VALUES" + expectedValues);
        return statementExasol.executeQuery("SELECT * FROM " + qualifiedExpectedTableName);
    }

    protected ResultSet getActualResultSet(Statement statementExasol, final String query) throws SQLException {
        return statementExasol.executeQuery(query);
    }

    protected Connection getExasolConnection() throws SQLException {
        return exasolContainer.createConnection("");
    }

    private static boolean is832OrLater() {
        return supportTimestampPrecision();
    }

    protected static boolean supportTimestampPrecision() {
        final ExasolDockerImageReference dockerImage = exasolContainer.getDockerImageReference();
        if (!dockerImage.hasMajor() || !dockerImage.hasMinor() || !dockerImage.hasFix()) {
            return false;
        }
        final ExasolDbVersion exasolDbVersion = ExasolDbVersion.of(dockerImage.getMajor(), dockerImage.getMinor(), dockerImage.getFixVersion());
        if (exasolDbVersion.isGreaterOrEqualThan(ExasolDbVersion.parse("8.32.0"))) {
            return true;
        }
        return false;
    }

    protected static String getColumnTypesOfTable(Statement statementExasol, final String tableName, final String columnName) throws SQLException {
        final ResultSet result = statementExasol.executeQuery("DESCRIBE " + tableName);
        while (result.next()) {
            String resultSetColumnName = result.getString("COLUMN_NAME").toUpperCase();
            String resultSetType = result.getString("SQL_TYPE").toUpperCase();
            if (resultSetColumnName.equals(columnName)) {
                return resultSetType;
            }
        }
        throw new IllegalArgumentException("Type for column " + columnName + " not found");
    }

    protected static void assertExpressionExecutionBigDecimalResult(Statement statementExasol, final String query, final BigDecimal expectedValue)
            throws SQLException {
        final ResultSet result = statementExasol.executeQuery(query);
        result.next();
        final BigDecimal actualResult = result.getBigDecimal(1);
        assertThat(actualResult.stripTrailingZeros(), equalTo(expectedValue));
    }

    protected static void assertBigDecimalResults(Statement statementExasol, final String query, final BigDecimal... expectedValues)
            throws SQLException {
        final ResultSet result = statementExasol.executeQuery(query);
        int i = 1;
        result.next();
        for (BigDecimal expectedValue: expectedValues) {
            final BigDecimal actualResult = result.getBigDecimal(i);
            assertThat(actualResult.compareTo(expectedValue), equalTo(0));
            i++;
        }
    }

    protected static void assertStringResults(Statement statementExasol, final String query, final String... expectedValues)
            throws SQLException {
        final ResultSet result = statementExasol.executeQuery(query);
        int i = 1;
        result.next();
        for (String expectedValue: expectedValues) {
            final String actualResult = result.getString(i);
            assertThat(actualResult, equalTo(expectedValue));
            i++;
        }
    }

    protected static void assertTimestampResultsLater(Statement statementExasol, final String query, final Timestamp... expectedValues)
            throws SQLException {
        final ResultSet result = statementExasol.executeQuery(query);
        int i = 1;
        result.next();
        for (Timestamp expectedValue: expectedValues) {
            final Timestamp actualResult = result.getTimestamp(i);
            assertThat(actualResult.compareTo(expectedValue), greaterThan(0));
            i++;
        }
    }

    protected static void assertTimestampResults(Statement statementExasol, final String query, final Timestamp... expectedValues)
            throws SQLException {
        final ResultSet result = statementExasol.executeQuery(query);
        int i = 1;
        result.next();
        for (Timestamp expectedValue: expectedValues) {
            final Timestamp actualResult = result.getTimestamp(i);
            assertThat(actualResult, equalTo(expectedValue));
            i++;
        }
    }

    protected static void assertExplainVirtual(Statement statementExasol, final String query, final String expected) throws SQLException {
        final ResultSet explainVirtual = statementExasol.executeQuery("EXPLAIN VIRTUAL " + query);
        explainVirtual.next();
        final String explainVirtualStringActual = explainVirtual.getString("PUSHDOWN_SQL");
        assertThat(explainVirtualStringActual, containsString(expected));
    }

}
