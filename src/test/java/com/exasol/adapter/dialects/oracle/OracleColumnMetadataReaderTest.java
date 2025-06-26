package com.exasol.adapter.dialects.oracle;

import static com.exasol.adapter.metadata.DataType.createMaximumSizeVarChar;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.lenient;

import java.sql.Types;

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

    @BeforeEach
    void beforeEach() {
        this.exaMetadataMock = Mockito.mock(ExaMetadata.class);
        lenient().when(exaMetadataMock.getDatabaseVersion()).thenReturn("8.34.0");
        this.columnMetadataReader = createDefaultOracleColumnMetadataReader();
    }

    protected OracleColumnMetadataReader createDefaultOracleColumnMetadataReader() {
        return new OracleColumnMetadataReader(null, AdapterProperties.emptyProperties(),
                exaMetadataMock, BaseIdentifierConverter.createDefault());
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
                equalTo(createMaximumSizeVarChar(DataType.ExaCharset.UTF8)));
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
    void testMapColumnTypeWithZeroPrecision() {
        final int precision = 0;
        final int scale = 0;
        final JDBCTypeDescription typeDescription = createTypeDescriptionForNumeric(precision, scale);
        assertThat(this.columnMetadataReader.mapJdbcType(typeDescription),
                equalTo(DataType.createDecimal(DataType.MAX_EXASOL_DECIMAL_PRECISION, scale)));
    }
}