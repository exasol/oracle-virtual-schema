package com.exasol.adapter.dialects.oracle;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.sql.*;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.ExaMetadata;
import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.dialects.BaseIdentifierConverter;
import com.exasol.adapter.jdbc.BaseColumnMetadataReader;
import com.exasol.adapter.jdbc.ColumnMetadataReader;
import com.exasol.adapter.jdbc.RemoteMetadataReaderException;

@ExtendWith(MockitoExtension.class)
class OracleResultSetMetadataReaderTest {
    @Mock
    private ResultSetMetaData resultSetMetadataMock;
    @Mock
    private Connection connectionMock;
    @Mock
    private ExaMetadata exaMetadataMock;
    @Mock
    private PreparedStatement statementMock;

    @Test
    void testDescribeColumn() throws SQLException {
        when(this.resultSetMetadataMock.getColumnCount()).thenReturn(2);
        when(this.resultSetMetadataMock.getColumnType(1)).thenReturn(Types.BOOLEAN);
        when(this.resultSetMetadataMock.getColumnType(2)).thenReturn(Types.VARCHAR);
        when(this.resultSetMetadataMock.getPrecision(1)).thenReturn(0);
        when(this.resultSetMetadataMock.getPrecision(2)).thenReturn(20);
        final String columnDescription = "c1 BOOLEAN, c2 VARCHAR(20) UTF8";
        assertThat(getReader().describeColumns("irrelevant", Collections.emptyList()), equalTo(columnDescription));
    }

    public OracleResultSetMetadataReader getReader() throws SQLException {
        when(this.exaMetadataMock.getDatabaseVersion()).thenReturn("8.34.0");
        when(this.statementMock.getMetaData()).thenReturn(this.resultSetMetadataMock);
        when(this.connectionMock.prepareStatement(any())).thenReturn(this.statementMock);
        final ColumnMetadataReader columnMetadataReader = new BaseColumnMetadataReader(this.connectionMock,
                AdapterProperties.emptyProperties(), exaMetadataMock, BaseIdentifierConverter.createDefault());
        return new OracleResultSetMetadataReader(this.connectionMock, columnMetadataReader);
    }

    @Test
    void testDescribeColumnThrowsExceptionIfUnsupportedColumnContained() throws SQLException {
        when(this.resultSetMetadataMock.getColumnCount()).thenReturn(4);
        when(this.resultSetMetadataMock.getColumnType(1)).thenReturn(Types.BOOLEAN);
        when(this.resultSetMetadataMock.getColumnType(2)).thenReturn(Types.BLOB);
        when(this.resultSetMetadataMock.getColumnType(3)).thenReturn(Types.DATE);
        when(this.resultSetMetadataMock.getColumnType(4)).thenReturn(Types.BLOB);
        final OracleResultSetMetadataReader reader = getReader();
        final RemoteMetadataReaderException thrown = assertThrows(RemoteMetadataReaderException.class,
                () -> reader.describeColumns("FOOBAR", Collections.emptyList()));
        assertThat(thrown.getMessage(),
                containsString("E-VSCJDBC-31: Unsupported data type(s) in column(s) in query: 2, 4"));
    }

    @Test
    void testEmptyMetadata() throws SQLException {
        when(this.connectionMock.prepareStatement(any())).thenReturn(this.statementMock);
        when(this.exaMetadataMock.getDatabaseVersion()).thenReturn("8.34.0");
        final ColumnMetadataReader columnMetadataReader = new BaseColumnMetadataReader(this.connectionMock,
                AdapterProperties.emptyProperties(), exaMetadataMock, BaseIdentifierConverter.createDefault());
        final OracleResultSetMetadataReader metadataReader = new OracleResultSetMetadataReader(this.connectionMock,
                columnMetadataReader);
        final RemoteMetadataReaderException exception = assertThrows(RemoteMetadataReaderException.class,
                () -> metadataReader.describeColumns("FOOBAR", Collections.emptyList()));
        assertThat(exception.getMessage(), containsString("F-VSCJDBC-34"));
    }
}
