package com.exasol.adapter.dialects.oracle;

import static com.exasol.adapter.dialects.oracle.IntegrationTestConstants.*;
import static com.exasol.dbbuilder.dialects.exasol.AdapterScript.Language.JAVA;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeoutException;

import org.testcontainers.containers.GenericContainer;

import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.containers.ExasolContainer;
import com.exasol.containers.ExasolService;
import com.exasol.dbbuilder.dialects.exasol.*;
import com.exasol.dbbuilder.dialects.oracle.OracleObjectFactory;
import com.exasol.drivers.JdbcDriver;
import com.exasol.udfdebugging.UdfTestSetup;
import com.github.dockerjava.api.model.ContainerNetwork;

/**
 * This class contains the common integration test setup for all Oracle virtual schemas.
 */
public class OracleVirtualSchemaIntegrationTestSetup implements Closeable {
    private static final String SCHEMA_EXASOL = "SCHEMA_EXASOL";
    private static final String ADAPTER_SCRIPT_EXASOL = "ADAPTER_SCRIPT_EXASOL";
    private static final String ORACLE_JDBC_DRIVER_NAME = "ojdbc8.jar";

    private final Statement oracleStatement;
    private final OracleContainerDBA oracleContainer = OracleContainerDBA.startDbaContainer();
    @SuppressWarnings("resource") // Will be closed in close()
    private final ExasolContainer<? extends ExasolContainer<?>> exasolContainer = new ExasolContainer<>(EXASOL_VERSION)
            .withRequiredServices(ExasolService.BUCKETFS, ExasolService.UDF).withReuse(true);
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
            uploadOracleJDBCDriverToBucket(this.exasolContainer);
            uploadAdapterToBucket(this.exasolContainer.getDefaultBucket());
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
            final String connectionString = buildOracleConnectionString();

            this.connectionDefinition = this.exasolFactory.createConnectionDefinition("ORACLE_CONNECTION",
                    connectionString, "SYSTEM", "test");

        } catch (final SQLException | BucketAccessException | TimeoutException | FileNotFoundException exception) {
            throw new IllegalStateException("Failed to created Oracle test setup.", exception);
        }
    }

    private String buildOracleConnectionString() {
        final String hostIp = getTestHostIpFromInsideExasol();
        return "jdbc:oracle:thin:@" + hostIp + ":"
                + this.oracleContainer.getOraclePort() + "/" + this.oracleContainer.getDatabaseName();
    }

    public static void uploadOracleJDBCDriverToBucket(final ExasolContainer<? extends ExasolContainer<?>> container)
            throws BucketAccessException, TimeoutException, FileNotFoundException {
        container.getDriverManager()
                .install(JdbcDriver.builder("ORACLE").enableSecurityManager(false).mainClass("oracle.jdbc.OracleDriver")
                        .prefix("jdbc:oracle:thin:")
                        .sourceFile(Path.of("target/oracle-driver", ORACLE_JDBC_DRIVER_NAME)).build());
    }

    public static void uploadAdapterToBucket(final Bucket bucket)
            throws BucketAccessException, TimeoutException, FileNotFoundException {
        bucket.uploadFile(VIRTUAL_SCHEMA_JAR, VIRTUAL_SCHEMAS_JAR_NAME_AND_VERSION);
    }

    public static AdapterScript createAdapterScript(final ExasolSchema schema) {
        final String content = "%scriptclass com.exasol.adapter.RequestDispatcher;\n" //
                + "%jar /buckets/bfsdefault/default/" + VIRTUAL_SCHEMAS_JAR_NAME_AND_VERSION + ";\n" //
                + "%jar /buckets/bfsdefault/default/drivers/jdbc/ojdbc8.jar;\n";
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

        final Map<String, String> properties = new HashMap<>(Map.of("SCHEMA_NAME", forOracleSchema));
        properties.putAll(additionalProperties);

        return this.exasolFactory //
                .createVirtualSchemaBuilder("ORACLE_VIRTUAL_SCHEMA_" + (this.virtualSchemaCounter++)) //
                .adapterScript(this.adapterScript) //
                .connectionDefinition(this.connectionDefinition) //
                .properties(properties) //
                .build();
    }

    @Override
    public void close() {
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
        return getTestHostIpFromInsideExasol(exasolContainer);
    }

    /**
     * Determines the host IP address that should be used inside the Exasol container
     * to refer to the test host (typically the machine running the integration tests).
     * <p>
     * This method supports two environments:
     * <ul>
     * <li><b>CI environments (e.g., GitHub Actions):</b> It retrieves the Docker gateway IP address
     * from the Exasol container's network settings.</li>
     * <li><b>Local macOS environments:</b> If the environment variable {@code LOCAL_MACOS_ENV=true}
     * is set, it attempts to find the first available non-loopback IPv4 address of the local machine.</li>
     * </ul>
     *
     * @param container the running Exasol container used to access network configuration
     * @return the IP address as seen from inside the Exasol container, or {@code null} if it cannot be determined
     */
    public static String getTestHostIpFromInsideExasol(final GenericContainer<?> container) {
        final String localMacosEnv = System.getenv("LOCAL_MACOS_ENV");

        if (localMacosEnv == null || !localMacosEnv.equalsIgnoreCase("true")) {
            // This works inside GitHub Actions container environment
            final Map<String, ContainerNetwork> networks = container.getContainerInfo().getNetworkSettings().getNetworks();
            if (networks.isEmpty()) {
                return null;
            }
            return networks.values().iterator().next().getGateway();
        } else {
            // Fallback for local machine: find a non-loopback IPv4 address
            try {
                final Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
                for (final NetworkInterface netint : Collections.list(nets)) {
                    if (netint.isUp() && !netint.isLoopback()) {
                        final Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                        for (final InetAddress inetAddress : Collections.list(inetAddresses)) {
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof java.net.Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                }
            } catch (final Exception e) {
                throw new IllegalStateException("Failed to determine local host IP address in macOS environment", e);
            }
            // If everything fails, return null or throw
            return null;
        }
    }
}
