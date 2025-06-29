package com.exasol.adapter.dialects.oracle;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class ExasolVersionCheck {

    private ExasolVersionCheck() {
        // Not instantiable
    }

    static String getExasolMajorVersion(final Connection connection) {
        try (Statement stmt = connection.createStatement()) {
            final ResultSet result = stmt
                    .executeQuery("SELECT PARAM_VALUE FROM SYS.EXA_METADATA WHERE PARAM_NAME='databaseMajorVersion'");
            assertTrue(result.next(), "no result");
            return result.getString(1);
        } catch (final SQLException exception) {
            throw new IllegalStateException("Failed to query Exasol version: " + exception.getMessage(), exception);
        }
    }
}
