package com.exasol.adapter.dialects.oracle.extension;

import java.sql.Connection;
import java.sql.SQLException;

import org.testcontainers.containers.JdbcDatabaseContainer.NoDriverFoundException;

import com.exasol.adapter.dialects.oracle.OracleContainerDBA;
import com.exasol.dbbuilder.dialects.Schema;
import com.exasol.dbbuilder.dialects.oracle.OracleObjectFactory;

class OracleTestSetup implements AutoCloseable {
    private static final String ORACLE_HOST_IP = "172.17.0.1";
    private final OracleContainerDBA container;
    private final Connection connection;

    private OracleTestSetup(final OracleContainerDBA container, final Connection connection) {
        this.container = container;
        this.connection = connection;
    }

    public static OracleTestSetup start() {
        final OracleContainerDBA container = OracleContainerDBA.startDbaContainer();
        container.start();
        final Connection connection = createConnection(container);
        return new OracleTestSetup(container, connection);
    }

    private static Connection createConnection(final OracleContainerDBA container) throws AssertionError {
        try {
            return container.createConnectionDBA("");
        } catch (NoDriverFoundException | SQLException exception) {
            throw new IllegalStateException("Failed to connect to database", exception);
        }
    }

    public Schema createSchema(final String schemaName) {
        return new OracleObjectFactory(connection).createSchema(schemaName);
    }

    @Override
    public void close() {
        try {
            this.connection.close();
        } catch (final SQLException exception) {
            throw new IllegalStateException("Failed to close connection", exception);
        }
        this.container.close();
    }

    public String getUsername() {
        return "SYSTEM";
    }

    public String getPassword() {
        return "test";
    }

    public String getJdbcConnectionString() {
        return "jdbc:oracle:thin:@" + ORACLE_HOST_IP + ":" + container.getOraclePort() + "/"
                + container.getDatabaseName();
    }
}
