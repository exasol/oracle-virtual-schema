package com.exasol.adapter.dialects.oracle;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.sql.*;

class ExasolVersionCheck {

    private ExasolVersionCheck() {
        // Not instantiable
    }

    /**
     * This is a temporary workaround until integration tests work with Exasol 8.
     */
    static void assumeExasolVersion7(final Connection connection) {
        final String version = getExasolMajorVersion(connection);
        assumeTrue("7".equals(version), "Expected Exasol version 7 but got '" + version + "'");
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
