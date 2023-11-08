package com.exasol.adapter.dialects.oracle.extension;

import static com.exasol.matcher.ResultSetStructureMatcher.table;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.*;

import com.exasol.adapter.RequestDispatcher;
import com.exasol.adapter.dialects.oracle.IntegrationTestConstants;
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

    private static ExasolTestSetup exasolTestSetup;
    private static ExtensionManagerSetup setup;
    private static String s3BucketName;
    private String s3ImportPrefix;

    @BeforeEach
    void createS3ImportPrefix() {
        s3ImportPrefix = "vs-works-test-" + System.currentTimeMillis() + "/";
    }

    @Override
    protected ExtensionITConfig createConfig() {
        final String previousVersion = "2.8.2";
        return ExtensionITConfig.builder().projectName("s3-document-files-virtual-schema") //
                .extensionId(EXTENSION_ID) //
                .currentVersion(PROJECT_VERSION) //
                .expectedParameterCount(13) //
                .extensionName("S3 Virtual Schema") //
                .extensionDescription("Virtual Schema for document files on AWS S3") //
                .previousVersion(previousVersion) //
                .previousVersionJarFile("document-files-virtual-schema-dist-7.3.6-s3-" + previousVersion + ".jar")
                .build();
    }

    @BeforeAll
    static void setup() throws FileNotFoundException, BucketAccessException, TimeoutException {
        if (System.getProperty("com.exasol.dockerdb.image") == null) {
            System.setProperty("com.exasol.dockerdb.image", "8.23.0");
        }
        exasolTestSetup = new ExasolTestSetupFactory(Path.of("no-cloud-setup")).getTestSetup();
        ExasolVersionCheck.assumeExasolVersion8(exasolTestSetup);
        setup = ExtensionManagerSetup.create(exasolTestSetup, ExtensionBuilder.createDefaultNpmBuilder(
                EXTENSION_SOURCE_DIR, EXTENSION_SOURCE_DIR.resolve("dist").resolve(EXTENSION_ID)));
        s3BucketName = "extension-test.s3.virtual-schema-test-bucket-" + System.currentTimeMillis();
        exasolTestSetup.getDefaultBucket().uploadFile(IntegrationTestConstants.VIRTUAL_SCHEMA_JAR,
                IntegrationTestConstants.VIRTUAL_SCHEMAS_JAR_NAME_AND_VERSION);
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
        final List<ParameterValue> parameters = new ArrayList<>();

        return parameters;
    }

    @Override
    @Test
    public void upgradeFromPreviousVersion() {
        // This test can be removed once version 2.8.3 was released
        setup.client().assertRequestFails(super::upgradeFromPreviousVersion, containsString(
                "invalid parameters: Failed to validate parameter 'Name of the new virtual schema' (virtualSchemaName): This is a required parameter."),
                equalTo(400));
    }
}
