package com.exasol.adapter.dialects.oracle;

import java.nio.file.Path;

public final class IntegrationTestConstants {
    public static final String VIRTUAL_SCHEMAS_JAR_NAME_AND_VERSION = "virtual-schema-dist-12.0.0-oracle-3.0.3.jar";
    public static final String ORACLE_CONTAINER_NAME = "gvenzl/oracle-xe:21.3.0-slim-faststart";
    public static final Path VIRTUAL_SCHEMA_JAR = Path.of("target", VIRTUAL_SCHEMAS_JAR_NAME_AND_VERSION);
    public static final String SCHEMA_EXASOL = "SCHEMA_EXASOL";
    public static final String TABLE_JOIN_1 = "TABLE_JOIN_1";
    public static final String TABLE_JOIN_2 = "TABLE_JOIN_2";
    public static final int ORACLE_PORT = 1521;
    public static final String RESOURCES_FOLDER_DIALECT_NAME = "oracle";

    private IntegrationTestConstants() {
        // intentionally left empty
    }
}
