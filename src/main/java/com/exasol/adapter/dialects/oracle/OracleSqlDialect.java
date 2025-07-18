package com.exasol.adapter.dialects.oracle;

import static com.exasol.adapter.AdapterProperties.SCHEMA_NAME_PROPERTY;
import static com.exasol.adapter.capabilities.AggregateFunctionCapability.*;
import static com.exasol.adapter.capabilities.LiteralCapability.*;
import static com.exasol.adapter.capabilities.MainCapability.*;
import static com.exasol.adapter.capabilities.PredicateCapability.*;
import static com.exasol.adapter.capabilities.ScalarFunctionCapability.*;
import static com.exasol.adapter.dialects.oracle.OracleProperties.*;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import com.exasol.ExaMetadata;
import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.capabilities.Capabilities;
import com.exasol.adapter.dialects.AbstractSqlDialect;
import com.exasol.adapter.dialects.ImportType;
import com.exasol.adapter.dialects.QueryRewriter;
import com.exasol.adapter.dialects.SqlGenerator;
import com.exasol.adapter.dialects.rewriting.ImportIntoTemporaryTableQueryRewriter;
import com.exasol.adapter.dialects.rewriting.SqlGenerationContext;
import com.exasol.adapter.jdbc.ConnectionFactory;
import com.exasol.adapter.jdbc.RemoteMetadataReader;
import com.exasol.adapter.jdbc.RemoteMetadataReaderException;
import com.exasol.adapter.metadata.DataType;
import com.exasol.adapter.properties.BooleanProperty;
import com.exasol.adapter.properties.CastNumberToDecimalProperty;
import com.exasol.adapter.properties.ImportProperty;
import com.exasol.adapter.sql.AggregateFunction;
import com.exasol.adapter.sql.ScalarFunction;
import com.exasol.errorreporting.ExaError;

/**
 * This class implements the Oracle SQL dialect.
 */
public class OracleSqlDialect extends AbstractSqlDialect {
    static final String NAME = "ORACLE";
    private static final Capabilities CAPABILITIES = createCapabilityList();

    private static Capabilities createCapabilityList() {
        return Capabilities.builder()
                .addMain(SELECTLIST_PROJECTION, SELECTLIST_EXPRESSIONS, FILTER_EXPRESSIONS, AGGREGATE_SINGLE_GROUP,
                        AGGREGATE_GROUP_BY_COLUMN, AGGREGATE_GROUP_BY_EXPRESSION, AGGREGATE_GROUP_BY_TUPLE,
                        AGGREGATE_HAVING, ORDER_BY_COLUMN, ORDER_BY_EXPRESSION, LIMIT, LIMIT_WITH_OFFSET, JOIN,
                        JOIN_TYPE_INNER, JOIN_TYPE_LEFT_OUTER, JOIN_TYPE_RIGHT_OUTER, JOIN_TYPE_FULL_OUTER,
                        JOIN_CONDITION_EQUI)
                .addPredicate(AND, OR, NOT, EQUAL, NOTEQUAL, LESS, LESSEQUAL, LIKE, LIKE_ESCAPE, REGEXP_LIKE, BETWEEN,
                        IN_CONSTLIST, IS_NULL, IS_NOT_NULL)
                .addLiteral(NULL, DATE, TIMESTAMP, TIMESTAMP_UTC, DOUBLE, EXACTNUMERIC, STRING, INTERVAL)
                .addAggregateFunction(COUNT, COUNT_STAR, COUNT_DISTINCT, GROUP_CONCAT, GROUP_CONCAT_SEPARATOR,
                        GROUP_CONCAT_ORDER_BY)
                .addAggregateFunction(SUM, SUM_DISTINCT, MIN, MAX, AVG, AVG_DISTINCT, MEDIAN, FIRST_VALUE, LAST_VALUE,
                        STDDEV, STDDEV_DISTINCT, STDDEV_POP, STDDEV_SAMP, VARIANCE, VARIANCE_DISTINCT, VAR_POP,
                        VAR_SAMP)
                .addScalarFunction(CEIL, DIV, FLOOR, SIGN)
                .addScalarFunction(ADD, SUB, MULT, FLOAT_DIV, NEG, ABS, ACOS, ASIN, ATAN, ATAN2, COS, COSH, COT,
                        DEGREES, EXP, GREATEST, LEAST, LN, LOG, MOD, POWER, RADIANS, SIN, SINH, SQRT, TAN, TANH)
                .addScalarFunction(ASCII, CHR, INSTR, LENGTH, LOCATE, LOWER, LPAD, LTRIM, REGEXP_INSTR, REGEXP_REPLACE,
                        REGEXP_SUBSTR, REPEAT, REPLACE, REVERSE, RPAD, RTRIM, SOUNDEX, SUBSTR, TRANSLATE, TRIM, UPPER,
                        ADD_DAYS, ADD_HOURS, ADD_MINUTES, ADD_MONTHS, ADD_SECONDS, ADD_WEEKS, ADD_YEARS, CURRENT_DATE,
                        CURRENT_TIMESTAMP, DBTIMEZONE, LOCALTIMESTAMP, NUMTODSINTERVAL, NUMTOYMINTERVAL,
                        SESSIONTIMEZONE, SYSDATE, SYSTIMESTAMP, CAST, TO_CHAR, TO_DATE, TO_DSINTERVAL, TO_YMINTERVAL,
                        TO_NUMBER, TO_TIMESTAMP, BIT_AND, BIT_TO_NUM, CASE, NULLIFZERO, ZEROIFNULL, INITCAP)
                .build();
    }

    /**
     * Create a new instance of the {@link OracleSqlDialect}.
     *
     * @param connectionFactory factory for the JDBC connection to the remote data source
     * @param properties        user-defined adapter properties
     */
    public OracleSqlDialect(final ConnectionFactory connectionFactory, final AdapterProperties properties, final ExaMetadata exaMetadata) {
        super(connectionFactory, properties, exaMetadata,
                Set.of(SCHEMA_NAME_PROPERTY, ORACLE_IMPORT_PROPERTY, ORACLE_CONNECTION_NAME_PROPERTY,
                        ORACLE_CAST_NUMBER_TO_DECIMAL_PROPERTY, GENERATE_JDBC_DATATYPE_MAPPING_FOR_OCI_PROPERTY), //
                List.of(CastNumberToDecimalProperty.validator(ORACLE_CAST_NUMBER_TO_DECIMAL_PROPERTY), //
                        BooleanProperty.validator(ORACLE_IMPORT_PROPERTY), //
                        ImportProperty.validator(ORACLE_IMPORT_PROPERTY, ORACLE_CONNECTION_NAME_PROPERTY)));
        this.omitParenthesesMap.add(ScalarFunction.SYSDATE);
        this.omitParenthesesMap.add(ScalarFunction.SYSTIMESTAMP);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Capabilities getCapabilities() {
        return CAPABILITIES;
    }

    @Override
    public Map<AggregateFunction, String> getAggregateFunctionAliases() {
        return new EnumMap<>(AggregateFunction.class);
    }

    @Override
    public StructureElementSupport supportsJdbcCatalogs() {
        return StructureElementSupport.NONE;
    }

    @Override
    public StructureElementSupport supportsJdbcSchemas() {
        return StructureElementSupport.MULTIPLE;
    }

    DataType getOracleNumberTargetType(int size) {
        if (this.properties.containsKey(ORACLE_CAST_NUMBER_TO_DECIMAL_PROPERTY)) {
            return this.getOracleNumberTypeFromProperty();
        } else {
            return DataType.createVarChar(size, DataType.ExaCharset.UTF8);
        }
    }

    private DataType getOracleNumberTypeFromProperty() {
        final String oraclePrecisionAndScale = this.properties.get(ORACLE_CAST_NUMBER_TO_DECIMAL_PROPERTY);
        final List<String> precisionAndScaleList = Arrays.stream(oraclePrecisionAndScale.split(",")).map(String::trim)
                .collect(Collectors.toList());
        return DataType.createDecimal(Integer.parseInt(precisionAndScaleList.get(0)),
                Integer.parseInt(precisionAndScaleList.get(1)));
    }

    @Override
    public SqlGenerator getSqlGenerator(final SqlGenerationContext context) {
        return new OracleSqlGenerationVisitor(this, context);
    }

    @Override
    // https://docs.oracle.com/cd/B19306_01/server.102/b14200/sql_elements008.htm
    public String applyQuote(final String identifier) {
        return OracleIdentifier.of(identifier).quote();
    }

    @Override
    public boolean requiresCatalogQualifiedTableNames(final SqlGenerationContext context) {
        return false;
    }

    @Override
    public boolean requiresSchemaQualifiedTableNames(final SqlGenerationContext context) {
        return true;
    }

    @Override
    public NullSorting getDefaultNullSorting() {
        return NullSorting.NULLS_SORTED_HIGH;
    }

    @Override
    // https://docs.oracle.com/cd/B19306_01/server.102/b14200/sql_elements003.htm
    public String getStringLiteral(final String value) {
        return super.quoteLiteralStringWithSingleQuote(value);
    }

    /**
     * Return the type of import the Oracle dialect uses.
     *
     * @return import type
     */
    public ImportType getImportType() {
        if (this.properties.isEnabled(ORACLE_IMPORT_PROPERTY)) {
            return ImportType.ORA;
        } else {
            return ImportType.JDBC;
        }
    }

    @Override
    protected RemoteMetadataReader createRemoteMetadataReader() {
        try {
            return new OracleMetadataReader(this.connectionFactory.getConnection(), this.properties, this.exaMetadata);
        } catch (final SQLException exception) {
            throw new RemoteMetadataReaderException(ExaError.messageBuilder("E-VSORA-1")
                    .message("Unable to create Oracle remote metadata reader. Caused by: {{cause|uq}}")
                    .parameter("cause", exception.getMessage()).toString(), exception);
        }
    }

    @Override
    protected QueryRewriter createQueryRewriter() {
        if (this.isImportFromOraEnabled()) {
            return new OracleQueryRewriter(this, this.createRemoteMetadataReader(), this.properties);
        }
        return new ImportIntoTemporaryTableQueryRewriter(this, this.createRemoteMetadataReader(),
                this.connectionFactory);
    }

    private boolean isImportFromOraEnabled() {
        return this.properties.isEnabled(OracleProperties.ORACLE_IMPORT_PROPERTY);
    }
}
