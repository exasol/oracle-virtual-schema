package com.exasol.adapter.dialects.oracle;

import static com.exasol.adapter.dialects.oracle.OracleProperties.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.sql.*;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.adapter.AdapterException;
import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.dialects.*;
import com.exasol.adapter.dialects.rewriting.AbstractQueryRewriterTestBase;
import com.exasol.adapter.jdbc.ConnectionFactory;
import com.exasol.adapter.metadata.DataType;
import com.exasol.adapter.sql.TestSqlStatementFactory;

@ExtendWith(MockitoExtension.class)
public class OracleQueryRewriterTest extends AbstractQueryRewriterTestBase {
    private static final List<DataType> EMPTY_SELECT_LIST_DATA_TYPES = Collections.emptyList();

    @BeforeEach
    void beforeEach() {
        this.statement = TestSqlStatementFactory.createSelectOneFromDual();
    }

    @Test
    void testRewriteToImportFromOraWithConnectionDetailsInProperties(
            @Mock final ConnectionFactory connectionFactoryMock) throws AdapterException, SQLException {
        final AdapterProperties properties = new AdapterProperties(Map.of( //
                ORACLE_IMPORT_PROPERTY, "true", //
                ORACLE_CONNECTION_NAME_PROPERTY, "ora_connection"));
        final SqlDialectFactory dialectFactory = new OracleSqlDialectFactory();
        final SqlDialect dialect = dialectFactory.createSqlDialect(connectionFactoryMock, properties);
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
        final SqlDialect dialect = dialectFactory.createSqlDialect(connectionFactoryMock, properties);
        final var oracleMetadataReader = new OracleMetadataReader(mockConnection(), properties);
        final QueryRewriter queryRewriter = new OracleQueryRewriter(dialect, oracleMetadataReader, mockConnection(),
                properties);
        assertThat(queryRewriter.rewrite(this.statement, EMPTY_SELECT_LIST_DATA_TYPES, EXA_METADATA, properties),
                equalTo("IMPORT INTO (c1 DECIMAL(18, 0)) FROM ORA AT ora_connection STATEMENT 'SELECT TO_CHAR(1) FROM \"DUAL\"'"));
    }

    @Override
    protected Connection mockConnection() throws SQLException {
        final ResultSetMetaData metadataMock = Mockito.mock(ResultSetMetaData.class);
        Mockito.lenient().when(metadataMock.getColumnCount()).thenReturn(1);
        Mockito.lenient().when(metadataMock.getColumnType(1)).thenReturn(4);
        final PreparedStatement statementMock = Mockito.mock(PreparedStatement.class);
        Mockito.lenient().when(statementMock.getMetaData()).thenReturn(metadataMock);
        final Connection connectionMock = Mockito.mock(Connection.class);
        Mockito.lenient().when(connectionMock.prepareStatement((String) ArgumentMatchers.any()))
                .thenReturn(statementMock);
        return connectionMock;
    }
}