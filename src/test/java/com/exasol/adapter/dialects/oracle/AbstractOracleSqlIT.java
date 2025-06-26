package com.exasol.adapter.dialects.oracle;

import static com.exasol.adapter.dialects.oracle.IntegrationTestConstants.ORACLE_PORT;
import static com.exasol.adapter.dialects.oracle.IntegrationTestConstants.SCHEMA_EXASOL;
import static com.exasol.adapter.dialects.oracle.OracleVirtualSchemaIntegrationTestSetup.uploadAdapterToBucket;
import static com.exasol.adapter.dialects.oracle.OracleVirtualSchemaIntegrationTestSetup.uploadOracleJDBCDriverToBucket;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.junit.jupiter.Container;

import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.containers.ExasolContainer;
import com.exasol.containers.ExasolService;
import com.exasol.dbbuilder.dialects.exasol.ConnectionDefinition;
import com.exasol.dbbuilder.dialects.exasol.ExasolObjectConfiguration;
import com.exasol.dbbuilder.dialects.exasol.ExasolObjectFactory;
import com.exasol.udfdebugging.UdfTestSetup;
import com.github.dockerjava.api.model.ContainerNetwork;

abstract class AbstractOracleSqlIT {
    private static final String ORACLE_CONTAINER_NAME = IntegrationTestConstants.ORACLE_CONTAINER_NAME;

    protected static final String SCHEMA_ORACLE = "SCHEMA_ORACLE_" + System.currentTimeMillis();

    private static final String ORACLE_JDBC_CONNECTION_NAME = "JDBC_CONNECTION";
    protected static final String ORACLE_OCI_CONNECTION_NAME = "ORACLE_CONNECTION";

    @Container
    protected static final ExasolContainer<? extends ExasolContainer<?>> exasolContainer = new ExasolContainer<>() //
            .withRequiredServices(ExasolService.BUCKETFS, ExasolService.UDF).withReuse(true);
    @Container
    protected static final OracleContainerDBA oracleContainer = new OracleContainerDBA(ORACLE_CONTAINER_NAME);

    @BeforeAll
    static void beforeAll()
            throws BucketAccessException, TimeoutException, SQLException, FileNotFoundException, InterruptedException {
        setupCommonOracleDbContainer();
        setupCommonExasolContainer();
    }

    private static void uploadInstantClientToBucket()
            throws BucketAccessException, TimeoutException, FileNotFoundException {
        final Bucket bucket = exasolContainer.getDefaultBucket();
        final String instantClientName = "instantclient-basic-linux.x64-12.1.0.2.0.zip";
        final String instantClientPath = "src/test/resources/integration/driver/oracle";
        bucket.uploadFile(Path.of(instantClientPath, instantClientName), "drivers/oracle/" + instantClientName);
    }

    protected static String getTestHostIpFromInsideExasol(final ExasolContainer<?> exasolContainer) {
        final Map<String, ContainerNetwork> networks = exasolContainer.getContainerInfo().getNetworkSettings()
                .getNetworks();
        if (networks.size() == 0) {
            throw new IllegalStateException("Failed to get host IP from container network settings");
        }
        return networks.values().iterator().next().getGateway();
    }

    private static void setupCommonExasolContainer()
            throws BucketAccessException, TimeoutException, FileNotFoundException, SQLException, InterruptedException {
        uploadOracleJDBCDriverToBucket(exasolContainer);
        uploadAdapterToBucket(exasolContainer.getDefaultBucket());
        uploadInstantClientToBucket();
        final Connection exasolConnection = exasolContainer.createConnectionForUser(exasolContainer.getUsername(),
                exasolContainer.getPassword());
        ExasolVersionCheck.assumeExasolVersion7(exasolConnection);

        final UdfTestSetup udfTestSetup = new UdfTestSetup(getTestHostIpFromInsideExasol(exasolContainer),
                exasolContainer.getDefaultBucket(), exasolConnection);
        final ExasolObjectFactory exasolFactory = new ExasolObjectFactory(exasolContainer.createConnection(""),
                ExasolObjectConfiguration.builder().withJvmOptions(udfTestSetup.getJvmOptions()).build());

        final Integer mappedPort = oracleContainer.getMappedPort(ORACLE_PORT);
        final String oracleUsername = "SYSTEM";
        final String oraclePassword = "test";
        createOracleOCIConnection(exasolFactory, mappedPort, oracleUsername, oraclePassword);
    }

    private static ConnectionDefinition createOracleOCIConnection(final ExasolObjectFactory exasolFactory,
                                                                  final Integer mappedPort, final String oracleUsername, final String oraclePassword) {
        final String oraConnectionString = "(DESCRIPTION =" //
                + "(ADDRESS_LIST = (ADDRESS = (PROTOCOL = TCP)" //
                + "(HOST = " + exasolContainer.getHostIp() + " )" //
                + "(PORT = " + oracleContainer.getOraclePort() + ")))" //
                + "(CONNECT_DATA = (SERVER = DEDICATED)" //
                + "(SERVICE_NAME = " + oracleContainer.getDatabaseName() + ")))";
        return exasolFactory.createConnectionDefinition(ORACLE_OCI_CONNECTION_NAME, oraConnectionString, oracleUsername,
                oraclePassword);
    }

    protected static ConnectionDefinition createOracleJDBCConnection(final String oracleUsername,
                                                                   final String oraclePassword, final ExasolObjectFactory exasolFactory) {
        final String jdbcConnectionString = "jdbc:oracle:thin:@" + exasolContainer.getHostIp() + ":"
                + oracleContainer.getOraclePort() + "/" + oracleContainer.getDatabaseName();

        return exasolFactory.createConnectionDefinition(ORACLE_JDBC_CONNECTION_NAME, jdbcConnectionString,
                oracleUsername, oraclePassword);

    }

    private static void setupCommonOracleDbContainer() throws SQLException {
        final var oracleConnection = oracleContainer.createConnectionDBA("");
        final Statement statementOracle = oracleConnection.createStatement();
        createOracleUser(statementOracle);
        grantAdditionalRights(statementOracle);
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

    protected ResultSet getExpectedResultSet(final List<String> expectedColumns, final List<String> expectedRows)
            throws SQLException {
        final Connection connection = getExasolConnection();
        try (final Statement statement = connection.createStatement()) {
            final String expectedValues = expectedRows.stream().map(row -> "(" + row + ")")
                    .collect(Collectors.joining(","));
            final String qualifiedExpectedTableName = SCHEMA_EXASOL + ".EXPECTED";
            statement.execute("CREATE OR REPLACE TABLE " + qualifiedExpectedTableName + "("
                    + String.join(", ", expectedColumns) + ")");
            statement.execute("INSERT INTO " + qualifiedExpectedTableName + " VALUES" + expectedValues);
            return statement.executeQuery("SELECT * FROM " + qualifiedExpectedTableName);
        }
    }

    protected ResultSet getActualResultSet(final String query) throws SQLException {
        final Connection connection = getExasolConnection();
        try (final Statement statement = connection.createStatement()) {
            return statement.executeQuery(query);
        }
    }

    protected Connection getExasolConnection() throws SQLException {
        return exasolContainer.createConnection("");
    }
}