package com.exasol.adapter.dialects.oracle;

import com.exasol.adapter.jdbc.JDBCTypeDescription;

/**
 * Represents a column description for a JDBC source.
 * <p>
 * This class extends {@link JDBCTypeDescription} and includes additional metadata about the column,
 * such as the column name and the raw metadata string as returned by the JDBC driver.
 * </p>
 */
public class JdbcColumnDescription extends JDBCTypeDescription {

    /** The name of the column in the remote table. */
    private final String columnName;

    /**
     * Raw string metadata about the column returned by the remote JDBC source.
     * This may include type declarations or other database-specific information.
     */
    private final String remoteColumnMetadata;

    /**
     * Constructs a new {@code JdbcColumnDescription} based on an existing {@link JDBCTypeDescription}
     * and adds additional metadata such as the column name and remote metadata string.
     *
     * @param jdbcTypeDescription      the base type description (JDBC type, scale, precision, etc.)
     * @param columnName               the name of the column
     * @param remoteColumnMetadata     raw metadata description string from the JDBC source
     */
    public JdbcColumnDescription(JDBCTypeDescription jdbcTypeDescription, String columnName, String remoteColumnMetadata) {
        super(jdbcTypeDescription.getJdbcType(),
                jdbcTypeDescription.getDecimalScale(),
                jdbcTypeDescription.getPrecisionOrSize(),
                jdbcTypeDescription.getByteSize(),
                jdbcTypeDescription.getTypeName());
        this.columnName = columnName;
        this.remoteColumnMetadata = remoteColumnMetadata;
    }

    /**
     * Gets the name of the column.
     *
     * @return the column name
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Gets the raw metadata string returned by the remote JDBC source for this column.
     *
     * @return the remote column metadata string
     */
    public String getRemoteColumnMetadata() {
        return remoteColumnMetadata;
    }

    /**
     * Returns a string representation of this {@code JdbcColumnDescription}, including
     * the column name, remote metadata, and base JDBC type description values.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "JdbcColumnDescription{" +
                "columnName='" + columnName + '\'' +
                ", remoteColumnMetadata='" + remoteColumnMetadata + '\'' +
                ", jdbcType=" + getJdbcType() +
                ", decimalScale=" + getDecimalScale() +
                ", precisionOrSize=" + getPrecisionOrSize() +
                ", byteSize=" + getByteSize() +
                ", typeName='" + getTypeName() + '\'' +
                '}';
    }
}