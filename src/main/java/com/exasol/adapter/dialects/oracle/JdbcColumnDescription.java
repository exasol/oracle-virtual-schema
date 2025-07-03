package com.exasol.adapter.dialects.oracle;

import com.exasol.adapter.jdbc.JDBCTypeDescription;

public class JdbcColumnDescription extends JDBCTypeDescription {

    private final String columnName;

    private final String remoteColumnMetadata;

    public JdbcColumnDescription(JDBCTypeDescription jdbcTypeDescription, String columnName, String remoteColumnMetadata) {
        super(jdbcTypeDescription.getJdbcType(), jdbcTypeDescription.getDecimalScale(), jdbcTypeDescription.getPrecisionOrSize(), jdbcTypeDescription.getByteSize(), jdbcTypeDescription.getTypeName());
        this.columnName = columnName;
        this.remoteColumnMetadata = remoteColumnMetadata;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getRemoteColumnMetadata() {
        return remoteColumnMetadata;
    }
}