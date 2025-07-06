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

public class OracleResultSetMetadataReader {

    private static final Logger LOGGER = Logger.getLogger(com.exasol.adapter.jdbc.ResultSetMetadataReader.class.getName());
    private final Connection connection;
    private final ColumnMetadataReader columnMetadataReader;

    /**
     * Create a new instance of a {@link com.exasol.adapter.jdbc.ResultSetMetadataReader}.
     *
     * @param connection           connection to the remote data source
     * @param columnMetadataReader column metadata reader used to translate the column types
     */
    public OracleResultSetMetadataReader(final Connection connection, final ColumnMetadataReader columnMetadataReader) {
        this.connection = connection;
        this.columnMetadataReader = columnMetadataReader;
    }

    /**
     * Generate a textual description of the result columns of the push-down query.
     *
     * @param query push-down query
     * @return string describing the columns (names and types)
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
            throw new RemoteMetadataReaderException(ExaError.messageBuilder("E-VSCJDBC-30").message(
                            "Unable to read remote metadata for push-down query trying to generate result column description.")
                    .mitigation("Please, make sure that you provided valid CATALOG_NAME "
                            + "and SCHEMA_NAME properties if required. Caused by: {{cause}}")
                    .parameter("cause", exception.getMessage()).toString(), exception);
        }
    }

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
            throw new RemoteMetadataReaderException(ExaError.messageBuilder("E-VSCJDBC-31")
                    .message("Unsupported data type(s) in column(s) in query: {{unsupportedColumns|uq}}.",
                            illegalColumns.stream().map(String::valueOf).collect(Collectors.joining(", ")))
                    .mitigation("Please remove those columns from your query:\n{{query|uq}}", query).toString());
        }
    }

    private List<DataType> mapResultMetadataToExasolDataTypes(final ResultSetMetaData metadata, List<DataType> selectListDataTypes) throws SQLException {
        validateMetadata(metadata);
        final int columnCount = metadata.getColumnCount();
        final List<DataType> types = new ArrayList<>(columnCount);
        boolean useSelectListDataTypes = useSelectListDataTypes(selectListDataTypes, columnCount);
        for (int columnNumber = 1; columnNumber <= columnCount; ++columnNumber) {
            if (useSelectListDataTypes) {
                final DataType selectListDataType = getSelectListDataType(selectListDataTypes, columnNumber);
                JDBCTypeDescription jdbcColumnMetadataDescription = getJdbcTypeDescription(metadata, columnNumber);
                if (shouldConvertDataType(selectListDataType, jdbcColumnMetadataDescription)) {
                    JDBCTypeDescription jdbcTypeDescription = getJdbcTypeDescription(selectListDataType, jdbcColumnMetadataDescription);
                    final DataType type = this.columnMetadataReader.mapJdbcType(jdbcTypeDescription);
                    types.add(type);
                } else {
                    types.add(selectListDataType);
                }
            } else {
                JDBCTypeDescription jdbcTypeDescription = getJdbcTypeDescription(metadata, columnNumber);
                final DataType type = this.columnMetadataReader.mapJdbcType(jdbcTypeDescription);
                types.add(type);
            }
        }
        return types;
    }

    private boolean shouldConvertDataType(DataType dataType, JDBCTypeDescription jdbcColumnMetadataDescription) {
        final int scale = dataType.getScale() > 0 ? dataType.getScale() : jdbcColumnMetadataDescription.getDecimalScale();
        final int precision = dataType.getPrecision() > 0 ? dataType.getPrecision() : jdbcColumnMetadataDescription.getPrecisionOrSize();
        return scale > 0 || precision > 0;
    }

    private JDBCTypeDescription getJdbcTypeDescription(DataType dataType, JDBCTypeDescription jdbcColumnMetadataDescription) {
        final int scale = dataType.getScale() > 0 ? dataType.getScale() : jdbcColumnMetadataDescription.getDecimalScale();
        final int precision = dataType.getPrecision() > 0 ? dataType.getPrecision() : jdbcColumnMetadataDescription.getPrecisionOrSize();
        final int byteSize = dataType.getByteSize() > 0 ? dataType.getByteSize() : jdbcColumnMetadataDescription.getByteSize();
        return new JDBCTypeDescription(jdbcColumnMetadataDescription.getJdbcType(),
                scale, precision, byteSize, jdbcColumnMetadataDescription.getTypeName());
    }

    private boolean useSelectListDataTypes(List<DataType> selectListDataTypes, int columnCount) {
        return selectListDataTypes != null && selectListDataTypes.size() == columnCount;
    }

    private DataType getSelectListDataType(List<DataType> selectListDataTypes, int columnNumber) {
        return selectListDataTypes.get(columnNumber - 1);
    }

    private void validateMetadata(final ResultSetMetaData metadata) {
        if (metadata == null) {
            throw new RemoteMetadataReaderException(ExaError.messageBuilder("F-VSCJDBC-34") //
                    .message(
                            "Metadata is missing in the ResultSet. This can happen if the generated query was incorrect,"
                                    + " but the JDBC driver didn't throw an exception.")
                    .ticketMitigation().toString());
        }
    }

    /**
     * Get the jdbc type description from result set metadata.
     *
     * @param metadata     result set metadata
     * @param columnNumber column number to read
     * @return JDBC type description
     * @throws SQLException if reading fails
     */
    protected static JDBCTypeDescription getJdbcTypeDescription(final ResultSetMetaData metadata,
                                                                final int columnNumber) throws SQLException {
        final int jdbcType = metadata.getColumnType(columnNumber);
        final int jdbcPrecisions = metadata.getPrecision(columnNumber);
        final int jdbcScales = metadata.getScale(columnNumber);
        return new JDBCTypeDescription(jdbcType, jdbcScales, jdbcPrecisions, 0,
                metadata.getColumnTypeName(columnNumber));
    }

    protected static JdbcColumnDescription getJdbcColumnDescription(final ResultSetMetaData metadata,
                                                                final int columnNumber) throws SQLException {
        final int jdbcType = metadata.getColumnType(columnNumber);
        final int jdbcPrecisions = metadata.getPrecision(columnNumber);
        final int jdbcScales = metadata.getScale(columnNumber);
        String columnName = metadata.getColumnName(columnNumber);
        JDBCTypeDescription jdbcTypeDescription = new JDBCTypeDescription(jdbcType, jdbcScales, jdbcPrecisions, 0,
                metadata.getColumnTypeName(columnNumber));
        return new JdbcColumnDescription(jdbcTypeDescription, columnName, buildRemoteColumnMetadata(metadata));
    }

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
