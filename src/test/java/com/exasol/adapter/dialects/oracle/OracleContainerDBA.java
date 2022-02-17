package com.exasol.adapter.dialects.oracle;

import java.sql.*;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.DockerImageName;

//reason for creating this class on top of oracle container is because this one,
// differently from the other ones (postgres, mysql, gives back a non-privileged user connection,
// I've added a method to this class that returns a dba connection .
//more info in the links below:
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
