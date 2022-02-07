package com.exasol.adapter.dialects.oracle;

import static com.exasol.adapter.dialects.oracle.IntegrationTestConstants.*;
import static com.exasol.adapter.dialects.oracle.IntegrationTestsHelperfunctions.getPropertyFromFile;
import static com.exasol.dbbuilder.dialects.exasol.AdapterScript.Language.JAVA;

import java.io.*;
import java.nio.file.Path;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.containers.ExasolContainer;
import com.exasol.containers.ExasolService;
import com.exasol.dbbuilder.dialects.exasol.*;
import com.exasol.dbbuilder.dialects.oracle.OracleObjectFactory;
import com.exasol.errorreporting.ExaError;
import com.exasol.udfdebugging.UdfTestSetup;
import com.github.dockerjava.api.model.ContainerNetwork;

/**
 * This class contains the common integration test setup for all Oracle virtual schemas.
 */
public class OracleVirtualSchemaIntegrationTestSetup implements Closeable {
    private static final Path PATH_TO_VIRTUAL_SCHEMAS_JAR = Path.of("target", VIRTUAL_SCHEMAS_JAR_NAME_AND_VERSION);
    private static final String SCHEMA_EXASOL = "SCHEMA_EXASOL";
    private static final String ADAPTER_SCRIPT_EXASOL = "ADAPTER_SCRIPT_EXASOL";
    private static final String EXASOL_DOCKER_IMAGE_REFERENCE = IntegrationTestConstants.EXASOL_DOCKER_IMAGE_REFERENCE;
    private static final String ORACLE_CONTAINER_NAME = IntegrationTestConstants.ORACLE_CONTAINER_NAME;

    private final Statement oracleStatement;
    private final OracleContainerDBA oracleContainer = new OracleContainerDBA(ORACLE_CONTAINER_NAME);
    private final ExasolContainer<? extends ExasolContainer<?>> exasolContainer = new ExasolContainer<>(
            EXASOL_DOCKER_IMAGE_REFERENCE).withRequiredServices(ExasolService.BUCKETFS, ExasolService.UDF)
            .withReuse(true);
    private final Connection exasolConnection;
    private final Statement exasolStatement;
    private final AdapterScript adapterScript;
    private final ConnectionDefinition connectionDefinition;
    private final ExasolObjectFactory exasolFactory;
    private final OracleObjectFactory oracleFactory;
    private final Connection oracleConnection;
    private int virtualSchemaCounter = 0;

    OracleVirtualSchemaIntegrationTestSetup() {
        try {
            this.exasolContainer.start();
            this.oracleContainer.start();
            uploadOracleJDBCDriverAndVSToBucket(exasolContainer.getDefaultBucket());
            this.exasolConnection = this.exasolContainer.createConnection("");
            this.exasolStatement = this.exasolConnection.createStatement();
            this.oracleConnection = this.oracleContainer.createConnectionDBA("");
            this.oracleStatement = this.oracleConnection.createStatement();
            final UdfTestSetup udfTestSetup = new UdfTestSetup(getTestHostIpFromInsideExasol(),
                    this.exasolContainer.getDefaultBucket(), this.exasolConnection);
            this.exasolFactory = new ExasolObjectFactory(this.exasolContainer.createConnection(""),
                    ExasolObjectConfiguration.builder().withJvmOptions(udfTestSetup.getJvmOptions()).build());
            final ExasolSchema exasolSchema = this.exasolFactory.createSchema(SCHEMA_EXASOL);
            this.oracleFactory = new OracleObjectFactory(this.oracleConnection);
            this.adapterScript = createAdapterScript(exasolSchema);
            // todo: check this (should be OK NOW)
            final String connectionString = "jdbc:oracle:thin:@" + this.exasolContainer.getHostIp() + ":"
                    + this.oracleContainer.getOraclePort() + "/" + this.oracleContainer.getDatabaseName();

            this.connectionDefinition = this.exasolFactory.createConnectionDefinition("ORACLE_CONNECTION",
                    connectionString, "SYSTEM", "test");

        } catch (final SQLException | BucketAccessException | TimeoutException exception) {
            throw new IllegalStateException("Failed to created Oracle test setup.", exception);
        } catch (final FileNotFoundException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Thread was interrupted");
        }
    }

    public static void uploadOracleJDBCDriverAndVSToBucket(final Bucket bucket) throws BucketAccessException, TimeoutException, FileNotFoundException {
        final String driverName = getPropertyFromFile(RESOURCES_FOLDER_DIALECT_NAME, "driver.name");

        final Path pathToSettingsFile = Path.of("src", "test", "resources", "integration", "driver",
                RESOURCES_FOLDER_DIALECT_NAME, JDBC_DRIVER_CONFIGURATION_FILE_NAME);

        //Upload the settings.cfg file for the driver that registers the driver.
        bucket.uploadFile(pathToSettingsFile, "drivers/jdbc/" + JDBC_DRIVER_CONFIGURATION_FILE_NAME);
        final String driverPath = getPropertyFromFile(RESOURCES_FOLDER_DIALECT_NAME, "driver.path");
        //Upload the driver itself
        bucket.uploadFile(Path.of(driverPath, driverName), "drivers/jdbc/" + driverName);
        //Upload the virtual schema jar to be able to use oracle virtual schemas
        bucket.uploadFile(PATH_TO_VIRTUAL_SCHEMAS_JAR, VIRTUAL_SCHEMAS_JAR_NAME_AND_VERSION);
    }

    public static AdapterScript createAdapterScript(final ExasolSchema schema) {
        final String content = "%scriptclass com.exasol.adapter.RequestDispatcher;\n" //
                + "%jar /buckets/bfsdefault/default/" + VIRTUAL_SCHEMAS_JAR_NAME_AND_VERSION + ";\n";
        return schema.createAdapterScript(ADAPTER_SCRIPT_EXASOL, JAVA, content);
    }

    /**
     * Returns the OracleFactory object
     *
     * @return OracleObjectFactory
     */
    public OracleObjectFactory getOracleFactory() {
        return this.oracleFactory;
    }

    public ExasolContainer<? extends ExasolContainer<?>> getExasolContainer() {
        return this.exasolContainer;
    }

    public VirtualSchema createVirtualSchema(final String forOracleSchema,
                                             final Map<String, String> additionalProperties) {

        final Map<String, String> properties = new HashMap<>(Map.of(// "CATALOG_NAME",
                // this.oracleContainer.getDatabaseName(), //
                "SCHEMA_NAME", forOracleSchema));
        properties.putAll(additionalProperties);

        return this.exasolFactory.createVirtualSchemaBuilder("ORACLE_VIRTUAL_SCHEMA_" + (this.virtualSchemaCounter++))
                .adapterScript(this.adapterScript)
                .connectionDefinition(this.connectionDefinition)
                .properties(properties)
                .build();
    }

    @Override
    public void close() throws IOException {
        try {
            this.exasolStatement.close();
            this.exasolConnection.close();
            this.oracleStatement.close();
            this.oracleConnection.close();
            this.exasolContainer.stop();
            this.oracleContainer.stop();
        } catch (final SQLException exception) {
            throw new IllegalStateException("Failed to stop test setup.", exception);
        }
    }

    private String getTestHostIpFromInsideExasol() {
        final Map<String, ContainerNetwork> networks = this.exasolContainer.getContainerInfo().getNetworkSettings()
                .getNetworks();
        if (networks.size() == 0) {
            return null;
        }
        return networks.values().iterator().next().getGateway();
    }
}
