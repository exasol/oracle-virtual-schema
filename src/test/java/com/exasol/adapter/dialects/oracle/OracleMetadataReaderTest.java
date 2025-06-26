package com.exasol.adapter.dialects.oracle;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.lenient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.ExaMetadata;
import com.exasol.adapter.AdapterProperties;

class OracleMetadataReaderTest {
    private OracleMetadataReader reader;

    private ExaMetadata exaMetadataMock;

    @BeforeEach
    void beforeEach() {
        this.exaMetadataMock = Mockito.mock(ExaMetadata.class);
        lenient().when(exaMetadataMock.getDatabaseVersion()).thenReturn("8.34.0");
        this.reader = new OracleMetadataReader(null, AdapterProperties.emptyProperties(), exaMetadataMock);
    }

    @Test
    void testGetTableMetadataReader() {
        assertThat(this.reader.getTableMetadataReader(), instanceOf(OracleTableMetadataReader.class));
    }

    @Test
    void testGetColumnMetadataReader() {
        assertThat(this.reader.getColumnMetadataReader(), instanceOf(OracleColumnMetadataReader.class));
    }
}