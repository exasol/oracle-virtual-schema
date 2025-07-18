package com.exasol.adapter.dialects.oracle;

import static com.exasol.adapter.dialects.VisitorAssertions.assertSqlNodeConvertedToOne;
import static com.exasol.adapter.sql.AggregateFunction.*;
import static com.exasol.adapter.sql.ScalarFunction.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.lenient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.adapter.AdapterException;
import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.adapternotes.ColumnAdapterNotes;
import com.exasol.adapter.adapternotes.ColumnAdapterNotesJsonConverter;
import com.exasol.adapter.dialects.SqlDialect;
import com.exasol.adapter.dialects.rewriting.SqlGenerationContext;
import com.exasol.adapter.metadata.ColumnMetadata;
import com.exasol.adapter.metadata.DataType;
import com.exasol.adapter.metadata.TableMetadata;
import com.exasol.adapter.sql.*;
import com.exasol.sql.SqlNormalizer;

@ExtendWith(MockitoExtension.class)
class OracleSqlGenerationVisitorTest {
    private OracleSqlGenerationVisitor visitor;

    @BeforeEach
    void beforeEach() throws AdapterException {
        final SqlDialect dialect = new OracleSqlDialectFactory().createSqlDialect(null,
                AdapterProperties.emptyProperties(), null);
        final SqlGenerationContext context = new SqlGenerationContext("test_catalog", "test_schema", false);
        this.visitor = Mockito.spy(new OracleSqlGenerationVisitor(dialect, context));
        lenient().doReturn("test").when(this.visitor).getTypeName(Mockito.any());
    }

    @Test
    void testGetAggregateFunctionsCast() {
        assertThat(this.visitor.getAggregateFunctionsCast(), containsInAnyOrder(SUM, MIN, MAX, AVG, MEDIAN, FIRST_VALUE,
                LAST_VALUE, STDDEV, STDDEV_POP, STDDEV_SAMP, VARIANCE, VAR_POP, VAR_SAMP));
    }

    @Test
    void testGetScalarFunctionsCast() {
        assertThat(this.visitor.getScalarFunctionsCast(),
                containsInAnyOrder(ADD, SUB, MULT, FLOAT_DIV, NEG, ABS, ACOS, ASIN, ATAN, ATAN2, COS, COSH, COT,
                        DEGREES, EXP, GREATEST, LEAST, LN, LOG, MOD, POWER, RADIANS, SIN, SINH, SQRT, TAN, TANH));
    }

    @Test
    void testVisitSqlStatementSelect() throws AdapterException {
        final SqlSelectList selectList = SqlSelectList.createAnyValueSelectList();
        final TableMetadata tableMetadata = new TableMetadata("", "", Collections.emptyList(), "");
        final SqlTable fromClause = new SqlTable("test_table_name", tableMetadata);
        final SqlStatementSelect sqlStatementSelect = SqlStatementSelect.builder().selectList(selectList)
                .fromClause(fromClause).build();
        assertThat(this.visitor.visit(sqlStatementSelect),
                equalTo("SELECT 1 FROM \"test_schema\".\"test_table_name\""));
    }

    @Test
    void testVisitSqlStatementSelectWithLimitAnyValue() throws AdapterException {
        final SqlSelectList selectList = SqlSelectList.createAnyValueSelectList();
        final TableMetadata tableMetadata = new TableMetadata("", "", Collections.emptyList(), "");
        final SqlTable fromClause = new SqlTable("", tableMetadata);
        final SqlLimit limit = new SqlLimit(10, 3);
        final SqlStatementSelect sqlStatementSelect = SqlStatementSelect.builder().selectList(selectList)
                .fromClause(fromClause).limit(limit).build();

        assertThat(this.visitor.visit(sqlStatementSelect), equalTo("1"));
    }

    @Test
    void testVisitSqlStatementSelectWithLimitRegularSelectList() throws AdapterException {
        final SqlSelectList selectList = SqlSelectList
                .createRegularSelectList(Arrays.asList(new SqlLiteralBool(true), new SqlLiteralString("string")));
        final TableMetadata tableMetadata = new TableMetadata("", "", Collections.emptyList(), "");
        final SqlTable fromClause = new SqlTable("test_table_name", tableMetadata);
        final SqlLimit limit = new SqlLimit(10, 3);
        final SqlStatementSelect sqlStatementSelect = SqlStatementSelect.builder().selectList(selectList)
                .fromClause(fromClause).limit(limit).build();
        assertThat(this.visitor.visit(sqlStatementSelect),
                equalTo("SELECT c0, c1 FROM ( SELECT LIMIT_SUBSELECT.*, ROWNUM "
                        + "ROWNUM_SUB FROM ( SELECT true AS c0, 'string' AS c1 FROM \"test_schema\".\"test_table_name\""
                        + "  ) LIMIT_SUBSELECT WHERE ROWNUM <= 13 ) WHERE ROWNUM_SUB > 3"));
    }

    @Test
    void testVisitSqlStatementSelectWithLimitRegularSelectListWithoutOffset() throws AdapterException {
        final SqlSelectList selectList = SqlSelectList
                .createRegularSelectList(Arrays.asList(new SqlLiteralBool(true), new SqlLiteralString("string")));
        final TableMetadata tableMetadata = new TableMetadata("", "", Collections.emptyList(), "");
        final SqlTable fromClause = new SqlTable("test_table_name", tableMetadata);
        final SqlLimit limit = new SqlLimit(10);
        final SqlStatementSelect sqlStatementSelect = SqlStatementSelect.builder().selectList(selectList)
                .fromClause(fromClause).limit(limit).build();
        assertThat(this.visitor.visit(sqlStatementSelect),
                equalTo("SELECT LIMIT_SUBSELECT.* FROM ( SELECT true, 'string' FROM \"test_schema\""
                        + ".\"test_table_name\"  ) LIMIT_SUBSELECT WHERE ROWNUM <= 10"));
    }

    @Test
    void testVisitSqlSelectListRequiresAnyColumn() throws AdapterException {
        final SqlSelectList sqlSelectList = SqlSelectList.createAnyValueSelectList();
        assertSqlNodeConvertedToOne(sqlSelectList, this.visitor);
    }

    @Test
    void testVisitSqlSelectListRegularSelectList() throws AdapterException {
        final SqlSelectList selectList = SqlSelectList
                .createRegularSelectList(Arrays.asList(new SqlLiteralBool(true), new SqlLiteralString("string")));
        final TableMetadata tableMetadata = new TableMetadata("", "", Collections.emptyList(), "");
        final SqlTable fromClause = new SqlTable("", tableMetadata);
        final SqlNode select = SqlStatementSelect.builder().selectList(selectList).fromClause(fromClause).build();
        selectList.setParent(select);
        assertThat(this.visitor.visit(selectList), equalTo("true, 'string'"));
    }

    @Test
    void testVisitSqlPredicateLikeRegexp() throws AdapterException {
        final SqlPredicateLikeRegexp sqlSelectList = new SqlPredicateLikeRegexp(new SqlLiteralString("abcd"),
                new SqlLiteralString("a_d"));
        assertThat(this.visitor.visit(sqlSelectList), equalTo("REGEXP_LIKE('abcd', 'a_d')"));
    }

    @Test
    void testVisitSqlLiteralExactnumeric() {
        final SqlLiteralExactnumeric literalExactnumeric = new SqlLiteralExactnumeric(new BigDecimal("5.9"));
        assertThat(this.visitor.visit(literalExactnumeric), equalTo("5.9"));
    }

    @Test
    void testVisitSqlLiteralExactnumericInSelectList() {
        final SqlSelectList selectList = SqlSelectList.createAnyValueSelectList();
        final SqlLiteralExactnumeric literalExactnumeric = new SqlLiteralExactnumeric(new BigDecimal("5.9"));
        literalExactnumeric.setParent(selectList);
        assertThat(this.visitor.visit(literalExactnumeric), equalTo("CAST(TO_CHAR(5.9) AS VARCHAR(4000))"));
    }

    @Test
    void testVisitSqlLiteralDouble() {
        final SqlLiteralDouble literalDouble = new SqlLiteralDouble(10.6);
        assertThat(this.visitor.visit(literalDouble), equalTo("1.06E1"));
    }

    @Test
    void testVisitSqlLiteralDoubleInSelectList() {
        final SqlSelectList selectList = SqlSelectList.createAnyValueSelectList();
        final SqlLiteralDouble literalDouble = new SqlLiteralDouble(10.6);
        literalDouble.setParent(selectList);
        assertThat(this.visitor.visit(literalDouble), equalTo("CAST(TO_CHAR(1.06E1) AS VARCHAR(4000))"));
    }

    @Test
    void testVisitSqlFunctionAggregateGroupConcat() throws AdapterException {
        final SqlFunctionAggregateGroupConcat aggregateGroupConcat = SqlFunctionAggregateGroupConcat
                .builder(new SqlLiteralDouble(10.5)).separator(new SqlLiteralString("'")).build();
        assertThat(this.visitor.visit(aggregateGroupConcat),
                equalTo("LISTAGG(1.05E1, '''') WITHIN GROUP(ORDER BY 1.05E1)"));
    }

    @Test
    void testVisitSqlFunctionAggregateGroupConcatWithOrderBy() throws AdapterException {
        final ColumnMetadata columnMetadata = ColumnMetadata.builder().name("test_column").type(DataType.createBool())
                .build();
        final ColumnMetadata columnMetadata2 = ColumnMetadata.builder().name("test_column2")
                .type(DataType.createDouble()).build();
        final List<SqlNode> orderByArguments = List.of(new SqlColumn(1, columnMetadata),
                new SqlColumn(2, columnMetadata2));
        final SqlOrderBy orderBy = new SqlOrderBy(orderByArguments, Stream.of(false, true).collect(Collectors.toList()),
                Stream.of(false, true).collect(Collectors.toList()));
        final SqlFunctionAggregateGroupConcat aggregateGroupConcat = SqlFunctionAggregateGroupConcat
                .builder(new SqlLiteralDouble(10.5)).separator(new SqlLiteralString("'")).orderBy(orderBy)
                .distinct(true).build();
        assertThat(this.visitor.visit(aggregateGroupConcat), equalTo(
                "LISTAGG(1.05E1, '''') WITHIN GROUP(ORDER BY \"test_column\" DESC NULLS FIRST, \"test_column2\")"));
    }

    @Test
    void testVisitSqlFunctionAggregate() throws AdapterException {
        final ColumnMetadata columnMetadata = ColumnMetadata.builder().name("test_column").type(DataType.createBool())
                .build();
        final List<SqlNode> arguments = List.of(new SqlColumn(1, columnMetadata));
        final SqlFunctionAggregate sqlFunctionAggregate = new SqlFunctionAggregate(AVG, arguments, true);
        assertThat(this.visitor.visit(sqlFunctionAggregate), equalTo("AVG(DISTINCT \"test_column\")"));
    }

    @Test
    void testVisitSqlFunctionAggregateInSelectList() throws AdapterException {
        final ColumnMetadata columnMetadata = ColumnMetadata.builder().name("test_column").type(DataType.createBool())
                .build();
        final List<SqlNode> arguments = List.of(new SqlColumn(1, columnMetadata));
        final SqlFunctionAggregate sqlFunctionAggregate = new SqlFunctionAggregate(AVG, arguments, false);
        final SqlNode selectList = SqlSelectList.createAnyValueSelectList();
        sqlFunctionAggregate.setParent(selectList);
        assertThat(this.visitor.visit(sqlFunctionAggregate), equalTo("AVG(\"test_column\")"));
    }

    @Test
    void testVisitSqlFunctionScalarLocateThreeArguments() throws AdapterException {
        final List<SqlNode> arguments = new ArrayList<>();
        arguments.add(new SqlLiteralString("ab "));
        arguments.add(new SqlLiteralString("ab cdef"));
        arguments.add(new SqlLiteralString("ab cdef rty"));
        final SqlFunctionScalar sqlFunctionScalar = new SqlFunctionScalar(LOCATE, arguments);
        assertThat(this.visitor.visit(sqlFunctionScalar), equalTo("INSTR('ab cdef', 'ab ', 'ab cdef rty')"));
    }

    @Test
    void testVisitSqlFunctionScalarTrimOneArgument() throws AdapterException {
        final List<SqlNode> arguments = new ArrayList<>();
        arguments.add(new SqlLiteralString("test"));
        final SqlFunctionScalar sqlFunctionScalar = new SqlFunctionScalar(TRIM, arguments);
        assertThat(this.visitor.visit(sqlFunctionScalar), equalTo("TRIM('test')"));
    }

    @Test
    void testVisitSqlFunctionScalarTrimOTwoArguments() throws AdapterException {
        final List<SqlNode> arguments = List.of(new SqlLiteralString("ab cdef"), new SqlLiteralString("ab"));
        final SqlFunctionScalar sqlFunctionScalar = new SqlFunctionScalar(TRIM, arguments);
        assertThat(this.visitor.visit(sqlFunctionScalar), equalTo("TRIM('ab' FROM 'ab cdef')"));
    }

    @CsvSource({ "ADD_DAYS, '10' DAY", //
            "ADD_HOURS,  '10' HOUR", //
            "ADD_MINUTES,  '10' MINUTE", //
            "ADD_SECONDS,  '10' SECOND", //
            "ADD_YEARS,  '10' YEAR", //
            "ADD_WEEKS, '70' DAY" })
    @ParameterizedTest
    void testVisitSqlFunctionScalarAddDateValues(final ScalarFunction scalarFunction, final String expected)
            throws AdapterException {
        final ColumnMetadata columnMetadata = ColumnMetadata.builder().name("test_column")
                .adapterNotes("{\"jdbcDataType\":93, " + "\"typeName\":\"TIMESTAMP\"}")
                .type(DataType.createChar(20, DataType.ExaCharset.UTF8)).build();
        final List<SqlNode> arguments = List.of(new SqlColumn(1, columnMetadata),
                new SqlLiteralExactnumeric(new BigDecimal(10)));
        final SqlFunctionScalar sqlFunctionScalar = new SqlFunctionScalar(scalarFunction, arguments);
        assertThat(this.visitor.visit(sqlFunctionScalar), equalTo("(\"test_column\" + INTERVAL " + expected + ")"));
    }

    @CsvSource({ "CURRENT_DATE, CURRENT_DATE", //
            "CURRENT_TIMESTAMP,  CURRENT_TIMESTAMP", //
            "DBTIMEZONE,  DBTIMEZONE", //
            "LOCALTIMESTAMP,  LOCALTIMESTAMP", //
            "SESSIONTIMEZONE, SESSIONTIMEZONE", //
            "SYSDATE, TO_DATE(SYSDATE)", //
            "SYSTIMESTAMP, SYSTIMESTAMP" })
    @ParameterizedTest
    void testVisitSqlFunctionScalar1(final ScalarFunction scalarFunction, final String expected)
            throws AdapterException {
        final SqlFunctionScalar sqlFunctionScalar = new SqlFunctionScalar(scalarFunction, null);
        assertThat(this.visitor.visit(sqlFunctionScalar), equalTo(expected));
    }

    @CsvSource(value = { "BIT_AND : BITAND('left', 'right')", //
            "BIT_TO_NUM : BIN_TO_NUM('left', 'right')", //
            "NULLIFZERO : NULLIF('left', 0)", //
            "ZEROIFNULL : NVL('left', 0)", //
            "DIV : CAST(FLOOR('left' / 'right') AS NUMBER(36, 0))", //
            "COT : (1 / TAN('left'))", //
            "DEGREES : (('left') * 180 / ACOS(-1))", //
            "RADIANS : (('left') * ACOS(-1) / 180)", //
            "REPEAT : RPAD(TO_CHAR('left'), LENGTH('left') * ROUND('right'), 'left')", //
            "REVERSE : REVERSE(TO_CHAR('left'))" //
    }, delimiter = ':')
    @ParameterizedTest
    void testVisitSqlFunctionScalar2(final ScalarFunction scalarFunction, final String expected)
            throws AdapterException {
        final List<SqlNode> arguments = List.of(new SqlLiteralString("left"), new SqlLiteralString("right"));
        final SqlFunctionScalar sqlFunctionScalar = new SqlFunctionScalar(scalarFunction, arguments);
        assertThat(this.visitor.visit(sqlFunctionScalar), equalTo(expected));
    }

    @Test
    void testVisitSqlFunctionScalarInSelectList() throws AdapterException {
        final SqlSelectList selectList = SqlSelectList.createAnyValueSelectList();
        final List<SqlNode> arguments = List.of(new SqlLiteralString("test"), new SqlLiteralString(""));
        final SqlFunctionScalar sqlFunctionScalar = new SqlFunctionScalar(TANH, arguments);
        sqlFunctionScalar.setParent(selectList);
        assertThat(this.visitor.visit(sqlFunctionScalar), equalTo("TANH('test', '')"));
    }

    @Test
    void testSqlGeneratorWithLimit() throws AdapterException {
        final String expectedSql = "SELECT LIMIT_SUBSELECT.* FROM ( " + //
                "  SELECT \"USER_ID\", COUNT(\"URL\") " + //
                "    FROM \"test_schema\".\"CLICKS\"" + //
                "    WHERE 1 < \"USER_ID\"" + //
                "    GROUP BY \"USER_ID\"" + //
                "    HAVING 1 < COUNT(\"URL\")" + //
                "    ORDER BY \"USER_ID\" " + //
                ") LIMIT_SUBSELECT WHERE ROWNUM <= 10"; //
        final SqlStatementSelect testSqlNode = (SqlStatementSelect) getTestSqlNode();
        final String actualSql = this.visitor.visit(testSqlNode);
        assertEquals(SqlNormalizer.normalizeSql(expectedSql), SqlNormalizer.normalizeSql(actualSql));
    }

    @Test
    void testSqlGeneratorWithLimitOffset() throws AdapterException {
        final String expectedSql = "SELECT c0, c1 FROM (" + //
                "  SELECT LIMIT_SUBSELECT.*, ROWNUM ROWNUM_SUB FROM ( " + //
                "    SELECT \"USER_ID\" AS c0, COUNT(\"URL\") AS c1 " + //
                "      FROM \"test_schema\".\"CLICKS\"" + //
                "      WHERE 1 < \"USER_ID\"" + //
                "      GROUP BY \"USER_ID\"" + //
                "      HAVING 1 < COUNT(\"URL\")" + //
                "      ORDER BY \"USER_ID\"" + //
                "  ) LIMIT_SUBSELECT WHERE ROWNUM <= 15 " + //
                ") WHERE ROWNUM_SUB > 5";
        final SqlStatementSelect testSqlNode = (SqlStatementSelect) getTestSqlNode();
        testSqlNode.getLimit().setOffset(5);
        final String actualSql = this.visitor.visit(testSqlNode);
        assertEquals(SqlNormalizer.normalizeSql(expectedSql), SqlNormalizer.normalizeSql(actualSql));
    }

    private static SqlNode getTestSqlNode() {
        return new DialectTestData().getTestSqlNode();
    }

    private static class DialectTestData {
        private SqlNode getTestSqlNode() {
            // SELECT USER_ID, count(URL) FROM CLICKS
            // WHERE 1 < USER_ID
            // GROUP BY USER_ID
            // HAVING 1 < COUNT(URL)
            // ORDER BY USER_ID
            // LIMIT 10;
            final TableMetadata clicksMeta = getClicksTableMetadata();
            final SqlTable fromClause = new SqlTable("CLICKS", clicksMeta);
            final SqlSelectList selectList = SqlSelectList.createRegularSelectList(List.of(
                    new SqlColumn(0, clicksMeta.getColumns().get(0)), new SqlFunctionAggregate(AggregateFunction.COUNT,
                            List.of(new SqlColumn(1, clicksMeta.getColumns().get(1))), false)));
            final SqlNode whereClause = new SqlPredicateLess(new SqlLiteralExactnumeric(BigDecimal.ONE),
                    new SqlColumn(0, clicksMeta.getColumns().get(0)));
            final SqlExpressionList groupBy = new SqlGroupBy(List.of(new SqlColumn(0, clicksMeta.getColumns().get(0))));
            final SqlNode countUrl = new SqlFunctionAggregate(AggregateFunction.COUNT,
                    List.of(new SqlColumn(1, clicksMeta.getColumns().get(1))), false);
            final SqlNode having = new SqlPredicateLess(new SqlLiteralExactnumeric(BigDecimal.ONE), countUrl);
            final SqlOrderBy orderBy = new SqlOrderBy(List.of(new SqlColumn(0, clicksMeta.getColumns().get(0))),
                    List.of(true), List.of(true));
            final SqlLimit limit = new SqlLimit(10);
            return SqlStatementSelect.builder().selectList(selectList).fromClause(fromClause).whereClause(whereClause)
                    .groupBy(groupBy).having(having).orderBy(orderBy).limit(limit).build();
        }

        private TableMetadata getClicksTableMetadata() {
            final ColumnAdapterNotesJsonConverter converter = ColumnAdapterNotesJsonConverter.getInstance();
            final List<ColumnMetadata> columns = new ArrayList<>();
            final ColumnAdapterNotes decimalAdapterNotes = ColumnAdapterNotes.builder() //
                    .jdbcDataType(3) //
                    .typeName("DECIMAL") //
                    .build();
            final ColumnAdapterNotes varcharAdapterNotes = ColumnAdapterNotes.builder() //
                    .jdbcDataType(12) //
                    .typeName("VARCHAR") //
                    .build();
            columns.add(ColumnMetadata.builder() //
                    .name("USER_ID") //
                    .adapterNotes(converter.convertToJson(decimalAdapterNotes)).type(DataType.createDecimal(18, 0)) //
                    .nullable(true) //
                    .identity(false) //
                    .defaultValue("") //
                    .comment("") //
                    .build());
            columns.add(ColumnMetadata.builder() //
                    .name("URL") //
                    .adapterNotes(converter.convertToJson(varcharAdapterNotes)) //
                    .type(DataType.createVarChar(10000, DataType.ExaCharset.UTF8)) //
                    .nullable(true) //
                    .identity(false) //
                    .defaultValue("") //
                    .comment("") //
                    .build());
            return new TableMetadata("CLICKS", "", columns, "");
        }
    }
}