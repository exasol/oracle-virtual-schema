package com.exasol.adapter.dialects.oracle;

import static com.exasol.adapter.dialects.oracle.OracleProperties.ORACLE_CAST_NUMBER_TO_DECIMAL_PROPERTY;
import static com.exasol.adapter.metadata.DataType.createChar;
import static com.exasol.adapter.metadata.DataType.createVarChar;
import static com.exasol.adapter.metadata.DataType.ExaCharset.UTF8;

import java.sql.Connection;
import java.sql.Types;

import com.exasol.ExaMetadata;
import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.dialects.IdentifierConverter;
import com.exasol.adapter.jdbc.BaseColumnMetadataReader;
import com.exasol.adapter.jdbc.JDBCTypeDescription;
import com.exasol.adapter.metadata.DataType;

/**
 * This class implements Oracle-specific reading of column metadata.
 */
public class OracleColumnMetadataReader extends BaseColumnMetadataReader {
    private static final int ORACLE_TIMESTAMP_WITH_LOCAL_TIME_ZONE = -101;
    private static final int ORACLE_TIMESTAMP_WITH_TIME_ZONE = -102;
    private static final int ORACLE_BINARY_FLOAT = 100;
    private static final int ORACLE_BINARY_DOUBLE = 101;
    private static final int INTERVAL_DAY_TO_SECOND = -104;
    private static final int INTERVAL_YEAR_TO_MONTH = -103;
    static final int ORACLE_MAGIC_NUMBER_SCALE = -127;

    static final int MAX_ORACLE_VARCHAR_SIZE = 4000;

    /**
     * Create a new instance of the {@link OracleColumnMetadataReader}
     *
     * @param connection          connection to the remote data source
     * @param properties          user-defined adapter properties
     * @param identifierConverter converter between source and Exasol identifiers
     */
    public OracleColumnMetadataReader(final Connection connection, final AdapterProperties properties,
                                      final ExaMetadata exaMetadata, final IdentifierConverter identifierConverter) {
        super(connection, properties, exaMetadata, identifierConverter);
    }

    @Override
    public DataType mapJdbcType(final JDBCTypeDescription jdbcTypeDescription) {
        switch (jdbcTypeDescription.getJdbcType()) {
            case Types.VARCHAR:
            case Types.NVARCHAR:
            case Types.LONGVARCHAR:
            case Types.LONGNVARCHAR:
                return convertOracleVarChar(jdbcTypeDescription.getPrecisionOrSize());
            case Types.CHAR:
            case Types.NCHAR:
                return convertOracleChar(jdbcTypeDescription.getPrecisionOrSize());
            case Types.DECIMAL:
            case Types.NUMERIC:
                return mapNumericType(jdbcTypeDescription);
            case ORACLE_TIMESTAMP_WITH_TIME_ZONE:
            case ORACLE_TIMESTAMP_WITH_LOCAL_TIME_ZONE:
                return convertExasolTimestamp(jdbcTypeDescription.getDecimalScale());
            case INTERVAL_YEAR_TO_MONTH:
            case INTERVAL_DAY_TO_SECOND:
            case ORACLE_BINARY_FLOAT:
            case ORACLE_BINARY_DOUBLE:
                return createOracleMaximumSizeVarChar();
            default:
                return super.mapJdbcType(jdbcTypeDescription);
        }
    }

    private DataType createOracleMaximumSizeVarChar() {
        return createVarChar(MAX_ORACLE_VARCHAR_SIZE, DataType.ExaCharset.UTF8);
    }

    private DataType convertOracleVarChar(final int size) {
        final DataType.ExaCharset charset = UTF8;
        if (size <= MAX_ORACLE_VARCHAR_SIZE) {
            final int precision = size == 0 ? MAX_ORACLE_VARCHAR_SIZE : size;
            return createVarChar(precision, charset);
        } else {
            return createVarChar(MAX_ORACLE_VARCHAR_SIZE, charset);
        }
    }

    private DataType convertOracleChar(final int size) {
        final DataType.ExaCharset charset = UTF8;
        if (size <= MAX_ORACLE_VARCHAR_SIZE) {
            return createChar(size, charset);
        } else {
            if (size <= MAX_ORACLE_VARCHAR_SIZE) {
                return createVarChar(size, charset);
            } else {
                return createVarChar(MAX_ORACLE_VARCHAR_SIZE, charset);
            }
        }
    }

    protected DataType mapNumericType(final JDBCTypeDescription jdbcTypeDescription) {
        final int decimalScale = jdbcTypeDescription.getDecimalScale();
        if (decimalScale == ORACLE_MAGIC_NUMBER_SCALE) {
            return workAroundNumberWithoutScaleAndPrecision();
        }
        final int decimalPrecision = jdbcTypeDescription.getPrecisionOrSize() == 0
                ? DataType.MAX_EXASOL_DECIMAL_PRECISION
                : jdbcTypeDescription.getPrecisionOrSize();
        if (decimalPrecision <= DataType.MAX_EXASOL_DECIMAL_PRECISION) {
            return DataType.createDecimal(decimalPrecision, decimalScale);
        } else {
            return workAroundNumberWithoutScaleAndPrecision();
        }
    }

    /**
     * @return Oracle JDBC driver returns scale -127 if NUMBER data type was specified without scale and precision.
     *         Convert to VARCHAR. See http://docs.oracle.com/cd/B28359_01/server.111/b28318/datatype.htm#i16209 and
     *         https://docs.oracle.com/cd/E19501-01/819-3659/gcmaz/
     */
    private DataType workAroundNumberWithoutScaleAndPrecision() {
        return getOracleNumberTargetType();
    }

    private DataType getOracleNumberTargetType() {
        if (this.properties.containsKey(ORACLE_CAST_NUMBER_TO_DECIMAL_PROPERTY)) {
            return getNumberTypeFromProperty(ORACLE_CAST_NUMBER_TO_DECIMAL_PROPERTY);
        } else {
            return createOracleMaximumSizeVarChar();
        }
    }

    /**
     * Converts a given decimal scale into an Exasol {@link DataType} representing a TIMESTAMP.
     * <p>
     * If the system supports timestamps with nanosecond precision, the fractional precision
     * of the timestamp will be set to the minimum of the provided decimal scale and 9
     * (since nanosecond precision supports up to 9 fractional digits).
     * Otherwise, a default fractional precision of 3 (milliseconds) is used.
     *
     * @param decimalScale the number of fractional digits to use for the TIMESTAMP precision
     * @return a {@link DataType} representing a TIMESTAMP with the appropriate fractional precision
     */
    private DataType convertExasolTimestamp(final int decimalScale) {
        if (supportsTimestampsWithNanoPrecision()) {
            final int fractionalPrecision = Math.min(decimalScale, 9);
            return DataType.createTimestamp(true, fractionalPrecision);
        }
        return DataType.createTimestamp(true, 3);
    }
}