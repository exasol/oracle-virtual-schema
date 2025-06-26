package com.exasol.adapter.dialects.oracle;

import static com.exasol.adapter.dialects.oracle.OracleProperties.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.lenient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.ExaMetadata;
import com.exasol.adapter.AdapterException;
import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.dialects.QueryRewriter;
import com.exasol.adapter.dialects.SqlDialect;
import com.exasol.adapter.dialects.SqlDialectFactory;
import com.exasol.adapter.dialects.rewriting.AbstractQueryRewriterTestBase;
import com.exasol.adapter.jdbc.ConnectionFactory;
import com.exasol.adapter.metadata.DataType;
import com.exasol.adapter.sql.TestSqlStatementFactory;

@ExtendWith(MockitoExtension.class)
public class OracleQueryRewriterTest extends AbstractQueryRewriterTestBase {
    private static final List<DataType> EMPTY_SELECT_LIST_DATA_TYPES = Collections.emptyList();

    private ExaMetadata exaMetadataMock;

    @BeforeEach
    void beforeEach() {
        this.statement = TestSqlStatementFactory.createSelectOneFromDual();
        this.exaMetadataMock = Mockito.mock(ExaMetadata.class);
        lenient().when(exaMetadataMock.getDatabaseVersion()).thenReturn("8.34.0");
    }

    @Test
    void testRewriteToImportFromOraWithConnectionDetailsInProperties(
            @Mock final ConnectionFactory connectionFactoryMock) throws AdapterException, SQLException {
        final AdapterProperties properties = new AdapterProperties(Map.of( //
                ORACLE_IMPORT_PROPERTY, "true", //
                ORACLE_CONNECTION_NAME_PROPERTY, "ora_connection"));
        final SqlDialectFactory dialectFactory = new OracleSqlDialectFactory();
        final SqlDialect dialect = dialectFactory.createSqlDialect(connectionFactoryMock, properties, null);
        final QueryRewriter queryRewriter = new OracleQueryRewriter(dialect, null,
                connectionFactoryMock.getConnection(), properties);
        assertThat(queryRewriter.rewrite(this.statement, EMPTY_SELECT_LIST_DATA_TYPES, EXA_METADATA, properties),
                equalTo("IMPORT FROM ORA AT ora_connection STATEMENT 'SELECT TO_CHAR(1) FROM \"DUAL\"'"));
    }

    @Test
    void testRewriteToImportFromOraWithConnectionDetailsInPropertiesJDBCDatatypeMapping(
            @Mock final ConnectionFactory connectionFactoryMock) throws AdapterException, SQLException {
        final AdapterProperties properties = new AdapterProperties(Map.of( //
                ORACLE_IMPORT_PROPERTY, "true", //
                ORACLE_CONNECTION_NAME_PROPERTY, "ora_connection", GENERATE_JDBC_DATATYPE_MAPPING_FOR_OCI_PROPERTY,
                "true"));
        final SqlDialectFactory dialectFactory = new OracleSqlDialectFactory();
        final SqlDialect dialect = dialectFactory.createSqlDialect(connectionFactoryMock, properties, null);
        final var oracleMetadataReader = new OracleMetadataReader(mockConnection(), properties, exaMetadataMock);
        final QueryRewriter queryRewriter = new OracleQueryRewriter(dialect, oracleMetadataReader, mockConnection(),
                properties);
        assertThat(queryRewriter.rewrite(this.statement, EMPTY_SELECT_LIST_DATA_TYPES, EXA_METADATA, properties),
                equalTo("IMPORT INTO (c1 DECIMAL(18, 0)) FROM ORA AT ora_connection STATEMENT 'SELECT TO_CHAR(1) FROM \"DUAL\"'"));
    }

    @Override
    protected Connection mockConnection() throws SQLException {
        final ResultSetMetaData metadataMock = Mockito.mock(ResultSetMetaData.class);
        lenient().when(metadataMock.getColumnCount()).thenReturn(1);
        lenient().when(metadataMock.getColumnType(1)).thenReturn(4);
        final PreparedStatement statementMock = Mockito.mock(PreparedStatement.class);
        lenient().when(statementMock.getMetaData()).thenReturn(metadataMock);
        final Connection connectionMock = Mockito.mock(Connection.class);
        lenient().when(connectionMock.prepareStatement((String) ArgumentMatchers.any()))
                .thenReturn(statementMock);
        return connectionMock;
    }
}