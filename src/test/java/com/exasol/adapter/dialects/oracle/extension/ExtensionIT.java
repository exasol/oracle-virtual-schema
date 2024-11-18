package com.exasol.adapter.dialects.oracle.extension;

import static com.exasol.matcher.ResultSetStructureMatcher.table;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.*;

import com.exasol.adapter.RequestDispatcher;
import com.exasol.adapter.dialects.oracle.IntegrationTestConstants;
import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.dbbuilder.dialects.Schema;
import com.exasol.dbbuilder.dialects.exasol.AdapterScript.Language;
import com.exasol.dbbuilder.dialects.exasol.ExasolSchema;
import com.exasol.drivers.ExasolDriverManager;
import com.exasol.drivers.JdbcDriver;
import com.exasol.exasoltestsetup.ExasolTestSetup;
import com.exasol.exasoltestsetup.ExasolTestSetupFactory;
import com.exasol.extensionmanager.client.model.ParameterValue;
import com.exasol.extensionmanager.itest.ExasolVersionCheck;
import com.exasol.extensionmanager.itest.ExtensionManagerSetup;
import com.exasol.extensionmanager.itest.base.AbstractVirtualSchemaExtensionIT;
import com.exasol.extensionmanager.itest.base.ExtensionITConfig;
import com.exasol.extensionmanager.itest.builder.ExtensionBuilder;
import com.exasol.matcher.ResultSetStructureMatcher.Builder;
import com.exasol.mavenprojectversiongetter.MavenProjectVersionGetter;

class ExtensionIT extends AbstractVirtualSchemaExtensionIT {
    private static final Path EXTENSION_SOURCE_DIR = Paths.get("extension").toAbsolutePath();
    private static final String PROJECT_VERSION = MavenProjectVersionGetter.getCurrentProjectVersion();
    private static final String EXTENSION_ID = "oracle-vs-extension.js";
    private static final String MAPPING_DESTINATION_TABLE = "DESTINATION_TABLE";

    private static ExasolTestSetup exasolTestSetup;
    private static ExtensionManagerSetup setup;
    private static OracleTestSetup oracleSetup;
    private String oracleSchemaName;

    @BeforeAll
    static void setup() throws FileNotFoundException, BucketAccessException, TimeoutException {
        if (System.getProperty("com.exasol.dockerdb.image") == null) {
            System.setProperty("com.exasol.dockerdb.image", "8.26.0");
        }
        exasolTestSetup = new ExasolTestSetupFactory(Path.of("no-cloud-setup")).getTestSetup();
        ExasolVersionCheck.assumeExasolVersion8(exasolTestSetup);
        setup = ExtensionManagerSetup.create(exasolTestSetup, ExtensionBuilder.createDefaultNpmBuilder(
                EXTENSION_SOURCE_DIR, EXTENSION_SOURCE_DIR.resolve("dist").resolve(EXTENSION_ID)));
        installJdbcDriver(exasolTestSetup.getDefaultBucket());
        oracleSetup = OracleTestSetup.start();
        exasolTestSetup.getDefaultBucket().uploadFile(IntegrationTestConstants.VIRTUAL_SCHEMA_JAR,
                IntegrationTestConstants.VIRTUAL_SCHEMAS_JAR_NAME_AND_VERSION);
    }

    private static void installJdbcDriver(final Bucket bucket) {
        final ExasolDriverManager driverManager = new ExasolDriverManager(bucket);
        driverManager
                .install(JdbcDriver.builder("ORACLE").mainClass("oracle.jdbc.OracleDriver").prefix("jdbc:oracle:thin:")
                        .sourceFile(Path.of("target/oracle-driver/ojdbc8.jar")).enableSecurityManager(false).build());
    }

    @BeforeEach
    void generateOracleSchemaName() {
        this.oracleSchemaName = "EXTENSION_TEST_" + System.currentTimeMillis();
    }

    @AfterAll
    static void teardownSetup() throws Exception {
        if (setup != null) {
            setup.close();
        }
        if (exasolTestSetup != null) {
            exasolTestSetup.close();
        }
        if (oracleSetup != null) {
            oracleSetup.close();
        }
    }

    @Override
    protected ExtensionITConfig createConfig() {
        return ExtensionITConfig.builder().projectName("oracle-virtual-schema") //
                .extensionId(EXTENSION_ID) //
                .currentVersion(PROJECT_VERSION) //
                .expectedParameterCount(12) //
                .extensionName("Oracle Virtual Schema") //
                .extensionDescription("Virtual Schema for Oracle") //
                .previousVersion(null) //
                .previousVersionJarFile(null).build();
    }

    @Override
    protected ExtensionManagerSetup getSetup() {
        return setup;
    }

    @Override
    protected void createScripts() {
        final String adapterJarBucketFsPath = "/buckets/bfsdefault/default/"
                + IntegrationTestConstants.VIRTUAL_SCHEMAS_JAR_NAME_AND_VERSION;
        final ExasolSchema schema = setup.createExtensionSchema();
        schema.createAdapterScriptBuilder("ORACLE_VS_ADAPTER")
                .bucketFsContent("com.exasol.adapter.RequestDispatcher", adapterJarBucketFsPath).language(Language.JAVA)
                .build();
    }

    @Override
    protected void assertScriptsExist() {
        final String jarDirective = "%jar /buckets/bfsdefault/default/"
                + IntegrationTestConstants.VIRTUAL_SCHEMAS_JAR_NAME_AND_VERSION + ";";
        final String comment = "Created by Extension Manager for Oracle Virtual Schema " + PROJECT_VERSION;
        setup.exasolMetadata()
                .assertScript(table()
                        .row("ORACLE_VS_ADAPTER", "ADAPTER", null, null,
                                allOf(containsString("%scriptclass " + RequestDispatcher.class.getName() + ";"), //
                                        containsString(jarDirective)),
                                comment) //
                        .matches());
    }

    @Override
    protected void prepareInstance() {
        final Schema schema = oracleSetup.createSchema(this.oracleSchemaName);
        schema.createTable(MAPPING_DESTINATION_TABLE, "ID", "DECIMAL", "NAME", "VARCHAR(10)").insert(1, "abc").insert(2,
                "xyz");
    }

    @Override
    protected void assertVirtualSchemaContent(final String virtualSchemaName) {
        final String virtualTable = "\"" + virtualSchemaName + "\".\"" + MAPPING_DESTINATION_TABLE + "\"";
        final String query = "SELECT ID, NAME FROM " + virtualTable + " ORDER BY ID ASC";
        assertQueryResult(query, table().row("1", "abc").row("2", "xyz"));
    }

    private void assertQueryResult(final String query, final Builder expectedRowBuilder) throws AssertionError {
        try (Connection connection = exasolTestSetup.createConnection();
                final ResultSet result = connection.createStatement().executeQuery(query)) {
            assertThat(result, expectedRowBuilder.matches());
        } catch (final SQLException exception) {
            throw new IllegalStateException("Assertion query '" + query + "' failed", exception);
        }
    }

    @Override
    protected Collection<ParameterValue> createValidParameterValues(final String extensionVersion) {
        return List.of( //
                param("SCHEMA_NAME", this.oracleSchemaName), //
                param("connection", oracleSetup.getJdbcConnectionString()), //
                param("username", oracleSetup.getUsername()), //
                param("password", oracleSetup.getPassword()), //
                param("IMPORT_DATA_TYPES", "EXASOL_CALCULATED"));
    }
}
