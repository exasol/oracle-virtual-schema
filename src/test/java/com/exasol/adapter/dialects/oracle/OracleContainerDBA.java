package com.exasol.adapter.dialects.oracle;

import java.sql.*;
import java.util.Properties;

import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.DockerImageName;

//reason for this:
//rights:
//https://github.com/testcontainers/testcontainers-java/issues/4615
//https://github.com/gvenzl/oci-oracle-xe/issues/41

public class OracleContainerDBA extends OracleContainer {
    public OracleContainerDBA(final String dockerImageName) {
        super(DockerImageName.parse(dockerImageName));
        withReuse(true);
    }

    int connectTimeoutSeconds = 120;

    // Code smell but also how it's done in the class it's inheriting from.
    // Finding a more novel approach isn't the goal here.
    // The only purpose of this class is delivering a DBA user connection
    // (which isn't done by default like for the postgresql or mysql containers).
    @SuppressWarnings("java:S2925")
    public Connection createConnectionDBA(final String queryString) throws SQLException, NoDriverFoundException {
        final Properties info = new Properties();
        info.put("user", "SYSTEM");
        info.put("password", this.getPassword());
        final String url = this.constructUrlForConnection(queryString);
        final Driver jdbcDriverInstance = this.getJdbcDriverInstance();
        SQLException lastException = null;

        try {
            final long start = System.currentTimeMillis();

            while ((System.currentTimeMillis() < (start + TimeUnit.SECONDS.toMillis(this.connectTimeoutSeconds))) && this.isRunning()) {
                try {
                    // this.logger().debug("Trying to create JDBC connection using {} to {} with properties: {}", new
                    // Object[]{this.driver.getClass().getName(), url, info});
                    return jdbcDriverInstance.connect(url, info);
                } catch (final SQLException exception) {
                    lastException = exception;
                    Thread.sleep(100L);
                }
            }
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
        }

        throw new SQLException("Could not create new connection", lastException);
    }
}
