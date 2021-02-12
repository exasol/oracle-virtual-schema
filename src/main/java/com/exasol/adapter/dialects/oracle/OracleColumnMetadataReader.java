package com.exasol.adapter.dialects.oracle;

import static com.exasol.adapter.dialects.oracle.OracleProperties.ORACLE_CAST_NUMBER_TO_DECIMAL_PROPERTY;

import java.sql.Connection;
import java.sql.Types;

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

    /**
     * Create a new instance of the {@link OracleColumnMetadataReader}
     *
     * @param connection          connection to the remote data source
     * @param properties          user-defined adapter properties
     * @param identifierConverter converter between source and Exasol identifiers
     */
    public OracleColumnMetadataReader(final Connection connection, final AdapterProperties properties,
            final IdentifierConverter identifierConverter) {
        super(connection, properties, identifierConverter);
    }

    @Override
    public DataType mapJdbcType(final JDBCTypeDescription jdbcTypeDescription) {
        switch (jdbcTypeDescription.getJdbcType()) {
        case Types.DECIMAL:
        case Types.NUMERIC:
            return mapNumericType(jdbcTypeDescription);
        case ORACLE_TIMESTAMP_WITH_TIME_ZONE:
        case ORACLE_TIMESTAMP_WITH_LOCAL_TIME_ZONE:
            return DataType.createTimestamp(false);
        case INTERVAL_YEAR_TO_MONTH:
        case INTERVAL_DAY_TO_SECOND:
        case ORACLE_BINARY_FLOAT:
        case ORACLE_BINARY_DOUBLE:
            return DataType.createMaximumSizeVarChar(DataType.ExaCharset.UTF8);
        default:
            return super.mapJdbcType(jdbcTypeDescription);
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
            return DataType.createMaximumSizeVarChar(DataType.ExaCharset.UTF8);
        }
    }
}