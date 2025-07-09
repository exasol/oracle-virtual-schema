package com.exasol.adapter.dialects.oracle;

import java.sql.Connection;

import com.exasol.ExaMetadata;
import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.dialects.BaseIdentifierConverter;
import com.exasol.adapter.dialects.IdentifierConverter;
import com.exasol.adapter.jdbc.AbstractRemoteMetadataReader;
import com.exasol.adapter.jdbc.ColumnMetadataReader;
import com.exasol.adapter.jdbc.TableMetadataReader;

/**
 * This class reads Oracle-specific database metadata.
 */
public class OracleMetadataReader extends AbstractRemoteMetadataReader {
    /**
     * Create a new instance of the {@link OracleMetadataReader}
     *
     * @param connection database connection through which the reader retrieves the metadata from the remote source
     * @param properties user-defined properties
     */
    public OracleMetadataReader(final Connection connection, final AdapterProperties properties, final ExaMetadata exaMetadata) {
        super(connection, properties, exaMetadata);
    }

    @Override
    protected TableMetadataReader createTableMetadataReader() {
        return new OracleTableMetadataReader(this.connection, getColumnMetadataReader(), this.properties,
                this.exaMetadata, super.getIdentifierConverter());
    }

    @Override
    protected ColumnMetadataReader createColumnMetadataReader() {
        return new OracleColumnMetadataReader(this.connection, this.properties, this.exaMetadata, getIdentifierConverter());
    }

    @Override
    protected IdentifierConverter createIdentifierConverter() {
        return BaseIdentifierConverter.createDefault();
    }
}