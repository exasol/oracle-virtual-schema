package com.exasol.adapter.dialects.oracle;

import com.exasol.ExaMetadata;
import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.dialects.SqlDialect;
import com.exasol.adapter.dialects.SqlDialectFactory;
import com.exasol.adapter.jdbc.ConnectionFactory;
import com.exasol.logging.VersionCollector;

/**
 * Factory for the Oracle SQL dialect.
 */
public class OracleSqlDialectFactory implements SqlDialectFactory {
    @Override
    public String getSqlDialectName() {
        return OracleSqlDialect.NAME;
    }

    @Override
    public SqlDialect createSqlDialect(final ConnectionFactory connectionFactory, final AdapterProperties properties, final ExaMetadata exaMetadata) {
        return new OracleSqlDialect(connectionFactory, properties, exaMetadata);
    }

    @Override
    public String getSqlDialectVersion() {
        final VersionCollector versionCollector = new VersionCollector(
                "META-INF/maven/com.exasol/oracle-virtual-schema/pom.properties");
        return versionCollector.getVersionNumber();
    }
}