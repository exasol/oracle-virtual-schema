package com.exasol.adapter.dialects.oracle;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.exasol.adapter.dialects.SqlDialect;
import com.exasol.adapter.dialects.rewriting.AbstractQueryRewriter;
import com.exasol.adapter.dialects.rewriting.ImportIntoTemporaryTableQueryRewriter;
import com.exasol.adapter.jdbc.ColumnMetadataReader;
import com.exasol.adapter.jdbc.ConnectionFactory;
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
    Connection connection;
    private static final Logger LOGGER = Logger.getLogger(OracleQueryRewriter.class.getName());
    public OracleQueryRewriter(final SqlDialect dialect, final RemoteMetadataReader remoteMetadataReader, Connection connection) {
        super(dialect, remoteMetadataReader, new OracleConnectionDefinitionBuilder());
        this.connection = connection;
    }

    @Override
    protected String generateImportStatement(final String connectionDefinition, final String pushdownQuery)
            throws SQLException {
        final String columnDescription = this.createImportColumnsDescription(pushdownQuery);

        return "IMPORT INTO (" + columnDescription + ")  FROM ORA " + connectionDefinition + " STATEMENT '" + pushdownQuery.replace("'", "''") + "'";
    }
    private String createImportColumnsDescription(final String query) throws SQLException {

        final ColumnMetadataReader columnMetadataReader = this.remoteMetadataReader.getColumnMetadataReader();

        final ResultSetMetadataReader resultSetMetadataReader = new ResultSetMetadataReader(
                connection, columnMetadataReader);

        final String columnsDescription = resultSetMetadataReader.describeColumns(query);
        LOGGER.finer(() -> "columndescription: " + columnsDescription);
        return columnsDescription;
    }
}