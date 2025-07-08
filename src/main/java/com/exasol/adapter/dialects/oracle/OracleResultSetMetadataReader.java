package com.exasol.adapter.dialects.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.exasol.adapter.dialects.rewriting.SqlGenerationHelper;
import com.exasol.adapter.jdbc.ColumnMetadataReader;
import com.exasol.adapter.jdbc.JDBCTypeDescription;
import com.exasol.adapter.jdbc.RemoteMetadataReaderException;
import com.exasol.adapter.metadata.DataType;
import com.exasol.errorreporting.ExaError;

/**
 * {@code OracleResultSetMetadataReader} is responsible for retrieving and translating metadata
 * of result sets for push-down SQL queries executed on an Oracle database.
 * <p>
 * It uses a {@link ColumnMetadataReader} to map JDBC metadata to Exasol {@link DataType}s and ensures
 * the result set schema is compatible with Exasol requirements. This class is typically used during
 * SQL pushdown planning to determine the structure of intermediate query results.
 */
public class OracleResultSetMetadataReader {

    private static final Logger LOGGER = Logger.getLogger(OracleResultSetMetadataReader.class.getName());
    private final Connection connection;
    private final ColumnMetadataReader columnMetadataReader;

    /**
     * Constructs a new instance of {@link OracleResultSetMetadataReader}.
     *
     * @param connection           JDBC connection to the Oracle database
     * @param columnMetadataReader implementation responsible for mapping JDBC column types to Exasol types
     */
    public OracleResultSetMetadataReader(final Connection connection, final ColumnMetadataReader columnMetadataReader) {
        this.connection = connection;
        this.columnMetadataReader = columnMetadataReader;
    }

    /**
     * Generates a column description string from the push-down query result's metadata.
     *
     * @param query                the SQL query to prepare and analyze
     * @param selectListDataTypes optional list of expected {@link DataType}s from the SELECT clause;
     *                            if provided, used to help infer correct types
     * @return Exasol column description string
     * @throws RemoteMetadataReaderException if metadata retrieval or validation fails
     */
    public String describeColumns(final String query, List<DataType> selectListDataTypes) {
        LOGGER.fine(() -> "Generating columns description for push-down query using "
                + this.columnMetadataReader.getClass().getSimpleName() + ":\n" + query);
        try (final PreparedStatement statement = this.connection.prepareStatement(query)) {
            final ResultSetMetaData metadata = statement.getMetaData();
            final List<DataType> types = mapResultMetadataToExasolDataTypes(metadata, selectListDataTypes);
            validateColumnTypes(types, query);
            final String columnsDescription = SqlGenerationHelper.createColumnsDescriptionFromDataTypes(types);
            LOGGER.fine(() -> "Columns description: " + columnsDescription);
            return columnsDescription;
        } catch (final SQLException exception) {
            throw new RemoteMetadataReaderException(ExaError.messageBuilder("E-VSORA-11").message(
                            "Unable to read remote metadata for push-down query trying to generate result column description.")
                    .mitigation("Please, make sure that you provided valid CATALOG_NAME "
                            + "and SCHEMA_NAME properties if required. Caused by: {{cause}}")
                    .parameter("cause", exception.getMessage()).toString(), exception);
        }
    }

    /**
     * Validates the list of mapped {@link DataType}s to ensure all types are supported by Exasol.
     *
     * @param types the list of data types to validate
     * @param query the associated SQL query (used for error reporting)
     * @throws RemoteMetadataReaderException if unsupported column types are found
     */
    private void validateColumnTypes(final List<DataType> types, final String query) {
        final List<Integer> illegalColumns = new ArrayList<>();
        int column = 1;
        for (final DataType type : types) {
            if (!type.isSupported()) {
                illegalColumns.add(column);
            }
            ++column;
        }
        if (!illegalColumns.isEmpty()) {
            throw new RemoteMetadataReaderException(ExaError.messageBuilder("E-VSORA-12")
                    .message("Unsupported data type(s) in column(s) in query: {{unsupportedColumns|uq}}.",
                            illegalColumns.stream().map(String::valueOf).collect(Collectors.joining(", ")))
                    .mitigation("Please remove those columns from your query:\n{{query|uq}}", query).toString());
        }
    }

    /**
     * Maps JDBC column metadata to a list of Exasol {@link DataType}s.
     *
     * @param metadata             JDBC metadata from {@link ResultSetMetaData}
     * @param selectListDataTypes optional list of types from SELECT clause for disambiguation
     * @return list of mapped {@link DataType}s
     * @throws SQLException if metadata access fails
     */
    private List<DataType> mapResultMetadataToExasolDataTypes(final ResultSetMetaData metadata, List<DataType> selectListDataTypes)
            throws SQLException {
        validateMetadata(metadata);
        final int columnCount = metadata.getColumnCount();
        final List<DataType> types = new ArrayList<>(columnCount);
        boolean useSelectListDataTypes = useSelectListDataTypes(selectListDataTypes, columnCount);

        for (int columnNumber = 1; columnNumber <= columnCount; ++columnNumber) {
            if (useSelectListDataTypes) {
                final DataType selectListDataType = getSelectListDataType(selectListDataTypes, columnNumber);
                JDBCTypeDescription jdbcColumnMetadataDescription = getJdbcTypeDescription(metadata, columnNumber);
                JDBCTypeDescription jdbcTypeDescription = getJdbcTypeDescription(selectListDataType, jdbcColumnMetadataDescription);
                final DataType type = this.columnMetadataReader.mapJdbcType(jdbcTypeDescription);
                types.add(mergeDataType(selectListDataType, type));
            } else {
                JDBCTypeDescription jdbcTypeDescription = getJdbcTypeDescription(metadata, columnNumber);
                types.add(this.columnMetadataReader.mapJdbcType(jdbcTypeDescription));
            }
        }
        return types;
    }

    /**
     * Returns the better-fitting {@link DataType} by combining SELECT clause information and actual JDBC metadata.
     *
     * @param selectListDataType expected type from SELECT clause
     * @param columnDataType     type inferred from JDBC metadata
     * @return merged {@link DataType}
     */
    private DataType mergeDataType(DataType selectListDataType, DataType columnDataType) {
        if (selectListDataType.getExaDataType() == DataType.ExaDataType.DOUBLE
                && columnDataType.getExaDataType() == DataType.ExaDataType.VARCHAR) {
            return selectListDataType;
        }
        return columnDataType;
    }

    /**
     * Combines type information from SELECT list and JDBC metadata to build a complete {@link JDBCTypeDescription}.
     *
     * @param dataType                   expected data type
     * @param jdbcColumnMetadataDescription JDBC metadata fallback values
     * @return resolved JDBC type description
     */
    private JDBCTypeDescription getJdbcTypeDescription(DataType dataType, JDBCTypeDescription jdbcColumnMetadataDescription) {
        final int scale = dataType.getScale() > 0 ? dataType.getScale() : jdbcColumnMetadataDescription.getDecimalScale();
        final int precision = dataType.getPrecision() > 0 ? dataType.getPrecision() : jdbcColumnMetadataDescription.getPrecisionOrSize();
        final int byteSize = dataType.getByteSize() > 0 ? dataType.getByteSize() : jdbcColumnMetadataDescription.getByteSize();
        return new JDBCTypeDescription(jdbcColumnMetadataDescription.getJdbcType(), scale, precision, byteSize,
                jdbcColumnMetadataDescription.getTypeName());
    }

    private boolean useSelectListDataTypes(List<DataType> selectListDataTypes, int columnCount) {
        return selectListDataTypes != null && selectListDataTypes.size() == columnCount;
    }

    private DataType getSelectListDataType(List<DataType> selectListDataTypes, int columnNumber) {
        return selectListDataTypes.get(columnNumber - 1);
    }

    private void validateMetadata(final ResultSetMetaData metadata) {
        if (metadata == null) {
            throw new RemoteMetadataReaderException(ExaError.messageBuilder("E-VSORA-13") //
                    .message("Metadata is missing in the ResultSet. This can happen if the generated query was incorrect,"
                            + " but the JDBC driver didn't throw an exception.")
                    .ticketMitigation().toString());
        }
    }

    /**
     * Reads the JDBC type description for a given column.
     *
     * @param metadata     result set metadata
     * @param columnNumber column index (1-based)
     * @return a new {@link JDBCTypeDescription} for the column
     * @throws SQLException if metadata access fails
     */
    protected static JDBCTypeDescription getJdbcTypeDescription(final ResultSetMetaData metadata,
                                                                final int columnNumber) throws SQLException {
        final int jdbcType = metadata.getColumnType(columnNumber);
        final int jdbcPrecision = metadata.getPrecision(columnNumber);
        final int jdbcScale = metadata.getScale(columnNumber);
        return new JDBCTypeDescription(jdbcType, jdbcScale, jdbcPrecision, 0, metadata.getColumnTypeName(columnNumber));
    }

    /**
     * Reads full JDBC column description including column name and remote metadata string.
     *
     * @param metadata     result set metadata
     * @param columnNumber column index (1-based)
     * @return a {@link JdbcColumnDescription} including the column name and type info
     * @throws SQLException if metadata access fails
     */
    protected static JdbcColumnDescription getJdbcColumnDescription(final ResultSetMetaData metadata,
                                                                    final int columnNumber) throws SQLException {
        final int jdbcType = metadata.getColumnType(columnNumber);
        final int jdbcPrecision = metadata.getPrecision(columnNumber);
        final int jdbcScale = metadata.getScale(columnNumber);
        final String columnName = metadata.getColumnName(columnNumber);
        JDBCTypeDescription jdbcTypeDescription = new JDBCTypeDescription(jdbcType, jdbcScale, jdbcPrecision, 0,
                metadata.getColumnTypeName(columnNumber));
        return new JdbcColumnDescription(jdbcTypeDescription, columnName, buildRemoteColumnMetadata(metadata));
    }

    /**
     * Builds a string representation of the JDBC metadata for logging or debugging.
     *
     * @param meta result set metadata
     * @return string with column metadata information
     * @throws SQLException if metadata access fails
     */
    private static String buildRemoteColumnMetadata(ResultSetMetaData meta) throws SQLException {
        int columnCount = meta.getColumnCount();
        StringBuilder remoteColumnStringBuilder = new StringBuilder("Column Metadata: [");
        for (int i = 1; i <= columnCount; i++) {
            String columnName = meta.getColumnName(i);
            Object value = meta.getColumnTypeName(i);
            remoteColumnStringBuilder.append(columnName)
                    .append("=")
                    .append(value)
                    .append(i < columnCount ? ", " : "");
        }
        remoteColumnStringBuilder.append("]");
        return remoteColumnStringBuilder.toString();
    }
}
