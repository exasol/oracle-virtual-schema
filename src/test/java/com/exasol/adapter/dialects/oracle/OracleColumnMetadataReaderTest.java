package com.exasol.adapter.dialects.oracle;

import static com.exasol.adapter.dialects.oracle.OracleColumnMetadataReader.createExasolVarChar;
import static com.exasol.adapter.dialects.oracle.OracleProperties.ORACLE_CAST_NUMBER_TO_DECIMAL_PROPERTY;
import static com.exasol.adapter.metadata.DataType.ExaCharset.UTF8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.lenient;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.ExaMetadata;
import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.dialects.BaseIdentifierConverter;
import com.exasol.adapter.jdbc.JDBCTypeDescription;
import com.exasol.adapter.metadata.DataType;

class OracleColumnMetadataReaderTest {
    private OracleColumnMetadataReader columnMetadataReader;

    private ExaMetadata exaMetadataMock;

    private Map<String, String> propertyMap = new HashMap<>();

    @BeforeEach
    void beforeEach() {
        this.exaMetadataMock = Mockito.mock(ExaMetadata.class);
        lenient().when(exaMetadataMock.getDatabaseVersion()).thenReturn("8.34.0");
        this.columnMetadataReader = createDefaultOracleColumnMetadataReader();
        propertyMap.remove(ORACLE_CAST_NUMBER_TO_DECIMAL_PROPERTY);
    }

    protected OracleColumnMetadataReader createDefaultOracleColumnMetadataReader() {
        return new OracleColumnMetadataReader(null, new AdapterProperties(propertyMap),
                exaMetadataMock, BaseIdentifierConverter.createDefault());
    }

    private JDBCTypeDescription jdbcType(int type, int scale, int precision, String typeName) {
        return new JDBCTypeDescription(type, scale, precision, 0, typeName);
    }

    private JDBCTypeDescription createTypeDescriptionForNumeric(final int precision, final int scale) {
        final int octetLength = 10;
        return new JDBCTypeDescription(Types.NUMERIC, scale, precision, octetLength, "NUMERIC");
    }

    @Test
    void testMapColumnTypeWithMagicScale() {
        final int precision = 10;
        final int scale = OracleColumnMetadataReader.ORACLE_MAGIC_NUMBER_SCALE;
        final JDBCTypeDescription typeDescription = createTypeDescriptionForNumeric(precision, scale);
        assertThat(this.columnMetadataReader.mapJdbcType(typeDescription),
                equalTo(DataType.createDecimal(precision, 0)));
    }

    @Test
    void testMapNumericColumnTypeWithMaximumDecimalPrecision() {
        final int precision = DataType.MAX_EXASOL_DECIMAL_PRECISION;
        final int scale = 0;
        final JDBCTypeDescription typeDescription = createTypeDescriptionForNumeric(precision, scale);
        assertThat(this.columnMetadataReader.mapJdbcType(typeDescription),
                equalTo(DataType.createDecimal(precision, scale)));
    }

    @Test
    void testMapNumericColumnTypeWithMoreThanMaximumDecimalPrecision() {
        final int precision = DataType.MAX_EXASOL_DECIMAL_PRECISION + 1;
        final int scale = 0;
        final JDBCTypeDescription typeDescription = createTypeDescriptionForNumeric(precision, scale);
        assertThat(this.columnMetadataReader.mapJdbcType(typeDescription),
                equalTo(createExasolVarChar(DataType.MAX_EXASOL_DECIMAL_PRECISION + 1)));
    }

    @Test
    void testMapColumnTypeWithZeroPrecision() {
        final int precision = 0;
        final int scale = 0;
        propertyMap.put(ORACLE_CAST_NUMBER_TO_DECIMAL_PROPERTY, "36,0");
        final JDBCTypeDescription typeDescription = createTypeDescriptionForNumeric(precision, scale);
        assertThat(this.columnMetadataReader.mapJdbcType(typeDescription),
                equalTo(DataType.createDecimal(DataType.MAX_EXASOL_DECIMAL_PRECISION, scale)));
    }

    @Test
    void testMapVarcharColumnType() {
        JDBCTypeDescription type = jdbcType(Types.VARCHAR, 0, 100, "VARCHAR");
        assertThat(columnMetadataReader.mapJdbcType(type), equalTo(DataType.createVarChar(100, UTF8)));
    }

    @Test
    void testMapCharColumnType() {
        JDBCTypeDescription type = jdbcType(Types.CHAR, 0, 42, "CHAR");
        assertThat(columnMetadataReader.mapJdbcType(type), equalTo(DataType.createChar(42, UTF8)));
    }

    @Test
    void testMapOverlyLongCharColumnTypeFallsBackToVarchar() {
        JDBCTypeDescription type = jdbcType(Types.CHAR, 0, 8000, "CHAR");
        assertThat(columnMetadataReader.mapJdbcType(type), equalTo(DataType.createVarChar(4000, UTF8)));
    }

    @Test
    void testMapOracleTimestampWithTimezone() {
        JDBCTypeDescription type = jdbcType(-102, 9, 0, "TIMESTAMP WITH TIME ZONE");
        DataType result = columnMetadataReader.mapJdbcType(type);
        assertThat(result.getExaDataType(), equalTo(DataType.ExaDataType.TIMESTAMP));
        assertThat(result.getPrecision(), equalTo(9));
    }

    @Test
    void testMapOracleBinaryFloatToVarchar() {
        JDBCTypeDescription type = jdbcType(100, 0, 0, "BINARY_FLOAT");
        assertThat(columnMetadataReader.mapJdbcType(type), equalTo(DataType.createVarChar(4000, UTF8)));
    }

    @Test
    void testMapOracleBinaryDoubleToVarchar() {
        JDBCTypeDescription type = jdbcType(101, 0, 0, "BINARY_DOUBLE");
        assertThat(columnMetadataReader.mapJdbcType(type), equalTo(DataType.createVarChar(4000, UTF8)));
    }

    @Test
    void testMapOracleIntervalYearToMonthToVarchar() {
        JDBCTypeDescription type = jdbcType(-103, 0, 0, "INTERVAL YEAR TO MONTH");
        assertThat(columnMetadataReader.mapJdbcType(type), equalTo(DataType.createVarChar(4000, UTF8)));
    }

    @Test
    void testMapOracleIntervalDayToSecondToVarchar() {
        JDBCTypeDescription type = jdbcType(-104, 0, 0, "INTERVAL DAY TO SECOND");
        assertThat(columnMetadataReader.mapJdbcType(type), equalTo(DataType.createVarChar(4000, UTF8)));
    }

    @Test
    void testMapUnknownTypeFallsBackToSuper() {
        JDBCTypeDescription type = jdbcType(Types.OTHER, 0, 0, "OTHER");
        // Note: this may return a default fallback VARCHAR(4000) depending on BaseColumnMetadataReader
        DataType fallback = columnMetadataReader.mapJdbcType(type);
        assertThat(fallback.getExaDataType(), notNullValue());
    }
}