package com.exasol.adapter.dialects.oracle;

import com.exasol.adapter.dialects.*;
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
    public SqlDialect createSqlDialect(final JDBCAdapterContext context) {
        return new OracleSqlDialect(context);
    }

    @Override
    public String getSqlDialectVersion() {
        final VersionCollector versionCollector = new VersionCollector(
                "META-INF/maven/com.exasol/oracle-virtual-schema/pom.properties");
        return versionCollector.getVersionNumber();
    }

    @Override
    public String getAdapterProjectShortTag() {
        return "VSORA";
    }
}
