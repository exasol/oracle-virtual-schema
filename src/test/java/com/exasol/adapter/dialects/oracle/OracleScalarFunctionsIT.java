package com.exasol.adapter.dialects.oracle;

import com.exasol.adapter.commontests.scalarfunction.ScalarFunctionsTestBase;
import com.exasol.adapter.commontests.scalarfunction.TestSetup;
import com.exasol.adapter.commontests.scalarfunction.virtualschematestsetup.*;
import com.exasol.adapter.commontests.scalarfunction.virtualschematestsetup.request.Column;
import com.exasol.adapter.commontests.scalarfunction.virtualschematestsetup.request.TableRequest;
import com.exasol.adapter.metadata.DataType;
import com.exasol.closeafterall.CloseAfterAll;
import com.exasol.closeafterall.CloseAfterAllExtension;
import com.exasol.dbbuilder.dialects.Schema;
import com.exasol.dbbuilder.dialects.Table;
import com.exasol.dbbuilder.dialects.exasol.VirtualSchema;
import com.exasol.dbbuilder.dialects.oracle.OracleObjectFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static com.exasol.adapter.metadata.DataType.IntervalType.DAY_TO_SECOND;

@ExtendWith({CloseAfterAllExtension.class})
class OracleScalarFunctionsIT extends ScalarFunctionsTestBase {


    @CloseAfterAll
    private static final OracleVirtualSchemaIntegrationTestSetup SETUP = new OracleVirtualSchemaIntegrationTestSetup();
    static int idCounter = 0;

    protected static String getUniqueIdentifier() {
        ++idCounter;
        return "ID" + idCounter;
    }

    @BeforeAll
    static void assumeExasol71() throws SQLException {
        ExasolVersionCheck.assumeExasolVersion7(SETUP.getExasolContainer().createConnection());
    }

    @Override
    protected TestSetup getTestSetup() {
        final OracleObjectFactory oracleFactory = SETUP.getOracleFactory();
        return new OracleTestSetup(oracleFactory);
    }

    private static class OracleSingleTableVirtualSchemaTestSetup implements VirtualSchemaTestSetup {
        private final VirtualSchema virtualSchema;
        private final Schema oracleSchema;

        private OracleSingleTableVirtualSchemaTestSetup(final VirtualSchema virtualSchema, final Schema oracleSchema) {
            this.virtualSchema = virtualSchema;
            this.oracleSchema = oracleSchema;
        }

        @Override
        public String getFullyQualifiedName() {
            return this.virtualSchema.getFullyQualifiedName();
        }

        @Override
        public void close() {
            this.virtualSchema.drop();
            this.oracleSchema.drop();
        }
    }

    @BeforeAll
    static void beforeAll() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }


    public class OracleTestSetup implements TestSetup {
        final OracleObjectFactory oracleFactory;

        OracleTestSetup(OracleObjectFactory oracleFactory) {
            this.oracleFactory = oracleFactory;
        }

        @Override
        public VirtualSchemaTestSetupProvider getVirtualSchemaTestSetupProvider() {
            return (final CreateVirtualSchemaTestSetupRequest request) -> {
                //create schema in oracle DB with increasing ID, ID1, ID2, etc.
                final Schema oracleSchema = oracleFactory.createSchema(getUniqueIdentifier());

                for (final TableRequest tableRequest : request.getTableRequests()) {
                    createTableInSchema(oracleSchema, tableRequest);
                }
                //create a virtual schema with the same name as the oracle schema
                final VirtualSchema virtualSchema = SETUP.createVirtualSchema(oracleSchema.getName(),
                        Collections.emptyMap());

                return new OracleScalarFunctionsIT.OracleSingleTableVirtualSchemaTestSetup(virtualSchema, oracleSchema);
            };
        }

        //case sensitive!!! 1 on 1
        // .tolower() for the table and column names (brought over from postgresql) was causing trouble here.
        private void createTableInSchema(Schema oracleSchema, TableRequest tableRequest) {
            final Table.Builder tableBuilder = oracleSchema
                    .createTableBuilder(tableRequest.getName());
            for (final Column column : tableRequest.getColumns()) {
                tableBuilder.column(column.getName(), column.getType());
            }
            final Table table = tableBuilder.build();
            for (final List<Object> row : tableRequest.getRows()) {
                table.insert(row.toArray());
            }
        }

        //https://docs.exasol.com/db/latest/migration_guides/oracle/execution/datatypemapping.htm
        @Override
        public String getExternalTypeFor(final DataType exasolType) {
            switch (exasolType.getExaDataType()) {
                case VARCHAR:
                    return "VARCHAR2(" + exasolType.getSize() + " CHAR)";
                case CHAR:
                    return "NCHAR2(" + exasolType.getSize() + ")";
                case DATE:
                    return "DATE";
                case TIMESTAMP:
                    return "TIMESTAMP (" + exasolType.getPrecision() + ")";
                case DOUBLE:
                    return "DOUBLE PRECISION";
                case DECIMAL:
                    return "DECIMAL";
                case BOOLEAN:
                    return "NUMBER(1)";
                case HASHTYPE:
                    return "RAW(" + exasolType.getSize() + ")";
                case INTERVAL:
                    if (exasolType.getIntervalType() == DAY_TO_SECOND) {
                        return "INTERVAL DAY(" + exasolType.getPrecision() + ") TO SECOND(" + exasolType.getIntervalFraction() + ")";
                    } else {
                        return "INTERVAL YEAR(" + exasolType.getPrecision() + ") TO MONTH";
                    }
                default:
                    return exasolType.toString();
            }
        }

        @Override
        public Set<String> getDialectSpecificExcludes() {
            return Set.of("neg",
                    "upper(\"DOUBLE_PRECISION_C0\")",//oracle converts 0.5 to '.5' instead of '0.5'
                    "upper(\"DATE_C5\")",//different date formatting
                    "upper(\"TIMESTAMP_0_C6\")", //different timestamp formatting
                    "to_dsinterval", "numtoyminterval", "systimestamp", "cast", "current_timestamp", "numtodsinterval", "to_yminterval",
                    "character_length", "trim", "add_months", "char_length", "instr", "lower", "regexp_replace", "substr", "add_hours", "left", "mid", "add_weeks",
                    "add_minutes", "to_timestamp", "reverse", "regexp_instr", "soundex", "add_days", "add_years", "replace", "translate", "lpad", "ltrim", "regexp_substr", "ucase", "lcase",
                    "character_Length", "locate", "curdate", "substring", "rpad", "to_date", "to_char", "repeat", "to_number", "length", "rtrim", "add_seconds");
        }

        @Override
        public Connection createExasolConnection() throws SQLException {
            return SETUP.getExasolContainer().createConnection();
        }
    }
}
