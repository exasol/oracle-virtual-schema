package com.exasol.adapter.dialects.oracle.extension;

import static com.exasol.matcher.ResultSetStructureMatcher.table;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import com.exasol.adapter.RequestDispatcher;
import com.exasol.adapter.dialects.oracle.IntegrationTestConstants;
import com.exasol.adapter.dialects.oracle.OracleContainerDBA;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.dbbuilder.dialects.exasol.AdapterScript.Language;
import com.exasol.dbbuilder.dialects.exasol.ExasolSchema;
import com.exasol.exasoltestsetup.ExasolTestSetup;
import com.exasol.exasoltestsetup.ExasolTestSetupFactory;
import com.exasol.extensionmanager.client.model.ParameterValue;
import com.exasol.extensionmanager.itest.ExasolVersionCheck;
import com.exasol.extensionmanager.itest.ExtensionManagerSetup;
import com.exasol.extensionmanager.itest.base.AbstractVirtualSchemaExtensionIT;
import com.exasol.extensionmanager.itest.base.ExtensionITConfig;
import com.exasol.extensionmanager.itest.builder.ExtensionBuilder;
import com.exasol.mavenprojectversiongetter.MavenProjectVersionGetter;

class ExtensionIT extends AbstractVirtualSchemaExtensionIT {
    private static final Path EXTENSION_SOURCE_DIR = Paths.get("extension").toAbsolutePath();
    private static final String PROJECT_VERSION = MavenProjectVersionGetter.getCurrentProjectVersion();
    private static final String EXTENSION_ID = "oracle-vs-extension.js";
    private static final String MAPPING_DESTINATION_TABLE = "DESTINATION_TABLE";
    private static final String ORACLE_SCHEMA_NAME = "EXTENSION_TEST_" + System.currentTimeMillis();
    private static final String ORACLE_HOST_IP = "172.17.0.1";

    private static ExasolTestSetup exasolTestSetup;
    private static ExtensionManagerSetup setup;
    private static OracleContainerDBA oracleContainer;

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

    @BeforeAll
    static void setup() throws FileNotFoundException, BucketAccessException, TimeoutException {
        if (System.getProperty("com.exasol.dockerdb.image") == null) {
            System.setProperty("com.exasol.dockerdb.image", "8.23.1");
        }
        exasolTestSetup = new ExasolTestSetupFactory(Path.of("no-cloud-setup")).getTestSetup();
        ExasolVersionCheck.assumeExasolVersion8(exasolTestSetup);
        setup = ExtensionManagerSetup.create(exasolTestSetup, ExtensionBuilder.createDefaultNpmBuilder(
                EXTENSION_SOURCE_DIR, EXTENSION_SOURCE_DIR.resolve("dist").resolve(EXTENSION_ID)));
        exasolTestSetup.getDefaultBucket().uploadFile(IntegrationTestConstants.VIRTUAL_SCHEMA_JAR,
                IntegrationTestConstants.VIRTUAL_SCHEMAS_JAR_NAME_AND_VERSION);
        oracleContainer = new OracleContainerDBA(IntegrationTestConstants.ORACLE_CONTAINER_NAME);
        oracleContainer.start();
    }

    @AfterAll
    static void teardownSetup() throws Exception {
        if (setup != null) {
            setup.close();
        }
        if (exasolTestSetup != null) {
            exasolTestSetup.close();
        }
        if (oracleContainer != null) {
            oracleContainer.stop();
        }
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

    }

    @Override
    protected void assertVirtualSchemaContent(final String virtualSchemaName) {
        final String virtualTable = "\"" + virtualSchemaName + "\".\"" + MAPPING_DESTINATION_TABLE + "\"";
        try (final ResultSet result = exasolTestSetup.createConnection().createStatement()
                .executeQuery("SELECT ID, NAME FROM " + virtualTable + " ORDER BY ID ASC")) {
            assertThat(result, table().row(1L, "abc").row(2L, "xyz").matches());
        } catch (final SQLException exception) {
            throw new AssertionError("Assertion query failed", exception);
        }
    }

    @Override
    protected Collection<ParameterValue> createValidParameterValues() {
        return List.of( //
                param("SCHEMA_NAME", ORACLE_SCHEMA_NAME), //
                param("connection", getJdbcConnectionString()), //
                param("username", "SYSTEM"), //
                param("password", oracleContainer.getPassword()));
    }

    private String getJdbcConnectionString() {
        return "jdbc:oracle:thin:@" + ORACLE_HOST_IP + ":" + oracleContainer.getOraclePort() + "/"
                + oracleContainer.getDatabaseName();
    }
}
