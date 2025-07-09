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
 * This class implements Oracle-specific logic for reading and mapping JDBC column metadata
 * into Exasol-compatible {@link com.exasol.adapter.metadata.DataType}s.
 * <p>
 * It handles special Oracle types like INTERVALs, TIMESTAMP WITH TIME ZONE,
 * BINARY_FLOAT/DOUBLE, and custom NUMBER handling based on scale and precision.
 */
public class OracleColumnMetadataReader extends BaseColumnMetadataReader {

    /**
     * Oracle-specific JDBC type code for {@code TIMESTAMP WITH LOCAL TIME ZONE}.
     */
    private static final int ORACLE_TIMESTAMP_WITH_LOCAL_TIME_ZONE = -101;

    /**
     * Oracle-specific JDBC type code for {@code TIMESTAMP WITH TIME ZONE}.
     */
    private static final int ORACLE_TIMESTAMP_WITH_TIME_ZONE = -102;

    /**
     * Oracle-specific JDBC type code for {@code BINARY_FLOAT}.
     */
    private static final int ORACLE_BINARY_FLOAT = 100;

    /**
     * Oracle-specific JDBC type code for {@code BINARY_DOUBLE}.
     */
    private static final int ORACLE_BINARY_DOUBLE = 101;

    /**
     * Oracle-specific JDBC type code for {@code INTERVAL DAY TO SECOND}.
     */
    private static final int INTERVAL_DAY_TO_SECOND = -104;

    /**
     * Oracle-specific JDBC type code for {@code INTERVAL YEAR TO MONTH}.
     */
    private static final int INTERVAL_YEAR_TO_MONTH = -103;

    /**
     * Magic value used by Oracle to indicate undefined scale.
     */
    static final int ORACLE_MAGIC_NUMBER_SCALE = -127;

    /**
     * Maximum allowed size for Oracle VARCHAR types.
     */
    static final int MAX_ORACLE_VARCHAR_SIZE = 4000;

    /**
     * Constructs a new {@code OracleColumnMetadataReader}.
     *
     * @param connection          the JDBC connection to the Oracle database
     * @param properties          the adapter properties
     * @param exaMetadata         Exasol metadata object
     * @param identifierConverter converter to map identifiers between Exasol and Oracle
     */
    public OracleColumnMetadataReader(final Connection connection, final AdapterProperties properties,
                                      final ExaMetadata exaMetadata, final IdentifierConverter identifierConverter) {
        super(connection, properties, exaMetadata, identifierConverter);
    }

    /**
     * Maps Oracle-specific JDBC types into Exasol-compatible {@link DataType}s.
     *
     * @param jdbcTypeDescription the JDBC type description as read from metadata
     * @return the Exasol {@link DataType}
     */
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
            case Types.REAL:
            case Types.FLOAT:
            case Types.DOUBLE:
                return mapNumericType(jdbcTypeDescription);
            case ORACLE_TIMESTAMP_WITH_TIME_ZONE:
            case ORACLE_TIMESTAMP_WITH_LOCAL_TIME_ZONE:
                return convertExasolTimestamp(jdbcTypeDescription.getDecimalScale());
            case INTERVAL_YEAR_TO_MONTH:
            case INTERVAL_DAY_TO_SECOND:
            case ORACLE_BINARY_FLOAT:
            case ORACLE_BINARY_DOUBLE:
                return createExasolVarChar(MAX_ORACLE_VARCHAR_SIZE);
            default:
                return super.mapJdbcType(jdbcTypeDescription);
        }
    }

    /**
     * Creates a {@link DataType} for Exasol VARCHAR(UTF8) of the given size.
     *
     * @param size the maximum string length
     * @return the Exasol {@link DataType}
     */
    static DataType createExasolVarChar(int size) {
        return createVarChar(size, DataType.ExaCharset.UTF8);
    }

    /**
     * Converts Oracle VARCHAR types to an Exasol-compatible VARCHAR.
     *
     * @param size the reported column size
     * @return an Exasol {@link DataType} representing VARCHAR
     */
    private DataType convertOracleVarChar(final int size) {
        final DataType.ExaCharset charset = UTF8;
        if (size > 0 && size <= MAX_ORACLE_VARCHAR_SIZE) {
            return createVarChar(size, charset);
        } else {
            return createVarChar(MAX_ORACLE_VARCHAR_SIZE, charset);
        }
    }

    /**
     * Converts Oracle CHAR types to an Exasol CHAR or VARCHAR based on size.
     *
     * @param size the reported column size
     * @return an Exasol {@link DataType}
     */
    private DataType convertOracleChar(final int size) {
        final DataType.ExaCharset charset = UTF8;
        if (size > 0 && size <= MAX_ORACLE_VARCHAR_SIZE) {
            return createChar(size, charset);
        } else {
            return createVarChar(MAX_ORACLE_VARCHAR_SIZE, charset);
        }
    }

    /**
     * Maps Oracle {@code NUMBER} and floating-point types to Exasol decimal or VARCHAR types.
     *
     * @param jdbcTypeDescription JDBC type description with precision/scale
     * @return the matching Exasol {@link DataType}
     */
    protected DataType mapNumericType(final JDBCTypeDescription jdbcTypeDescription) {
        int decimalScale = jdbcTypeDescription.getDecimalScale();
        final int decimalPrecision = jdbcTypeDescription.getPrecisionOrSize();
        if (decimalPrecision <= 0) {
            return getOracleNumberTargetType(MAX_ORACLE_VARCHAR_SIZE);
        }
        if (decimalPrecision <= DataType.MAX_EXASOL_DECIMAL_PRECISION) {
            return DataType.createDecimal(decimalPrecision, decimalScale < 0 ? 0 : decimalScale);
        } else {
            return getOracleNumberTargetType(decimalPrecision + (decimalScale > 0 ? 1 : 0));
        }
    }

    /**
     * Determines the fallback type to use for large Oracle numbers,
     * depending on the adapter configuration.
     *
     * @param size the effective display size
     * @return the Exasol {@link DataType}
     */
    private DataType getOracleNumberTargetType(int size) {
        if (this.properties.containsKey(ORACLE_CAST_NUMBER_TO_DECIMAL_PROPERTY)) {
            return getNumberTypeFromProperty(ORACLE_CAST_NUMBER_TO_DECIMAL_PROPERTY);
        } else {
            return createExasolVarChar(size);
        }
    }

    /**
     * Converts an Oracle TIMESTAMP scale to an Exasol TIMESTAMP with appropriate fractional precision.
     *
     * @param decimalScale the Oracle fractional seconds precision
     * @return a {@link DataType} representing a TIMESTAMP
     */
    private DataType convertExasolTimestamp(final int decimalScale) {
        if (supportsTimestampsWithNanoPrecision()) {
            final int fractionalPrecision = Math.min(decimalScale, 9);
            return DataType.createTimestamp(true, fractionalPrecision);
        }
        return DataType.createTimestamp(true, 3);
    }
}