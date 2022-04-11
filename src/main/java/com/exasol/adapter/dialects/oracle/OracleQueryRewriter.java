package com.exasol.adapter.dialects.oracle;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.dialects.SqlDialect;
import com.exasol.adapter.dialects.rewriting.AbstractQueryRewriter;
import com.exasol.adapter.jdbc.ColumnMetadataReader;
import com.exasol.adapter.jdbc.RemoteMetadataReader;
import com.exasol.adapter.jdbc.ResultSetMetadataReader;

/**
 * This class implements an Oracle-specific query rewriter.
 */
public class OracleQueryRewriter extends AbstractQueryRewriter {
    /**
     * Create a new instance of the {@link OracleQueryRewriter}.
     *
     * @param dialect              Oracle SQl dialect
     * @param remoteMetadataReader reader for metadata from the remote data source
     */
    private final Connection connection;
    private final AdapterProperties properties;

    private static final Logger LOGGER = Logger.getLogger(OracleQueryRewriter.class.getName());

    public OracleQueryRewriter(final SqlDialect dialect, final RemoteMetadataReader remoteMetadataReader, Connection connection, AdapterProperties properties) {
        super(dialect, remoteMetadataReader, new OracleConnectionDefinitionBuilder());
        this.connection = connection;
        this.properties = properties;
    }

    private boolean isGenerateJdbcDatatypeMappingForOCIEnabled() {
        return this.properties.isEnabled(OracleProperties.GENERATE_JDBC_DATATYPE_MAPPING_FOR_OCI_PROPERTY);
    }

    @Override
    protected String generateImportStatement(final String connectionDefinition, final String pushdownQuery)
            throws SQLException {
        if (isGenerateJdbcDatatypeMappingForOCIEnabled()) {
            final String columnDescription = this.createImportColumnsDescription(pushdownQuery);
            return "IMPORT INTO (" + columnDescription + ") FROM ORA " + connectionDefinition + " STATEMENT '" + pushdownQuery.replace("'", "''") + "'";
        } else {
            return "IMPORT FROM ORA " + connectionDefinition + " STATEMENT '" + pushdownQuery.replace("'", "''") + "'";
        }
    }

    private String createImportColumnsDescription(final String query) {
        final ColumnMetadataReader columnMetadataReader = this.remoteMetadataReader.getColumnMetadataReader();
        final ResultSetMetadataReader resultSetMetadataReader = new ResultSetMetadataReader(
                connection, columnMetadataReader);
        final String columnsDescription = resultSetMetadataReader.describeColumns(query);
        LOGGER.finer(() -> "columndescription: " + columnsDescription);
        return columnsDescription;
    }
}