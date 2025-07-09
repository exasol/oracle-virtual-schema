package com.exasol.adapter.dialects.oracle;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import com.exasol.ExaConnectionInformation;
import com.exasol.ExaMetadata;
import com.exasol.adapter.AdapterException;
import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.dialects.SqlDialect;
import com.exasol.adapter.dialects.SqlGenerator;
import com.exasol.adapter.dialects.rewriting.AbstractQueryRewriter;
import com.exasol.adapter.dialects.rewriting.SqlGenerationContext;
import com.exasol.adapter.dialects.rewriting.SqlGenerationHelper;
import com.exasol.adapter.jdbc.RemoteMetadataReader;
import com.exasol.adapter.metadata.DataType;
import com.exasol.adapter.properties.DataTypeDetection;
import com.exasol.adapter.sql.SqlStatement;
import com.exasol.errorreporting.ExaError;

/**
 * This class implements an Oracle-specific query rewriter.
 */
public class OracleQueryRewriter extends AbstractQueryRewriter {

    private final AdapterProperties properties;
    private static final Logger LOGGER = Logger.getLogger(OracleQueryRewriter.class.getName());

    /**
     * Create a new instance of the {@link OracleQueryRewriter}.
     *
     * @param dialect              Oracle SQl dialect
     * @param remoteMetadataReader reader for metadata from the remote data source
     * @param properties adapter properties
     */
    public OracleQueryRewriter(final SqlDialect dialect, final RemoteMetadataReader remoteMetadataReader, AdapterProperties properties) {
        super(dialect, remoteMetadataReader, new OracleConnectionDefinitionBuilder());
        this.properties = properties;
    }

    private boolean isGenerateJdbcDatatypeMappingForOCIEnabled() {
        return this.properties.isEnabled(OracleProperties.GENERATE_JDBC_DATATYPE_MAPPING_FOR_OCI_PROPERTY);
    }


    @Override
    protected String generateImportStatement(String connectionDefinition, String pushdownQuery) throws SQLException {
        return generateImportStatement(connectionDefinition, null, pushdownQuery);
    }

    @Override
    protected String generateImportStatement(final String connectionDefinition,
                                             List<DataType> selectListDataTypes, final String pushdownQuery) {
        if (isGenerateJdbcDatatypeMappingForOCIEnabled()) {
            final String columnDescription = this.createImportColumnsDescription(selectListDataTypes);
            return "IMPORT INTO (" + columnDescription + ") FROM ORA " + connectionDefinition + " STATEMENT '" + pushdownQuery.replace("'", "''") + "'";
        } else {
            return "IMPORT FROM ORA " + connectionDefinition + " STATEMENT '" + pushdownQuery.replace("'", "''") + "'";
        }
    }

    @Override
    public String rewrite(final SqlStatement statement, final List<DataType> selectListDataTypes,
                          final ExaMetadata exaMetadata, final AdapterProperties properties)
            throws AdapterException {
        final String pushdownQuery = buildPushdownQuery(statement, properties);
        final ExaConnectionInformation exaConnectionInformation = getConnectionInformation(exaMetadata,
                properties);
        final String connectionDefinition = this.connectionDefinitionBuilder
                .buildConnectionDefinition(properties, exaConnectionInformation);

        if (DataTypeDetection.from(properties).getStrategy() == DataTypeDetection.Strategy.EXASOL_CALCULATED) {
            String importStatement = generateImportStatement(connectionDefinition, selectListDataTypes,
                        pushdownQuery);
            LOGGER.finer(() -> "Import push-down statement:\n" + importStatement);
            return importStatement;
        } else {
            throw new AdapterException(ExaError.messageBuilder("E-VSORA-10").message(
                            "Property `IMPORT_DATA_TYPES` value 'FROM_RESULT_SET' is no longer supported.")
                    .mitigation("Please remove the `IMPORT_DATA_TYPES` property from the virtual schema so the default value 'EXASOL_CALCULATED' is used.")
                    .toString());
        }
    }

    private String buildPushdownQuery(final SqlStatement statement, final AdapterProperties properties)
            throws AdapterException {
        final SqlGenerationContext context = new SqlGenerationContext(properties.getCatalogName(),
                properties.getSchemaName(), false);
        final SqlGenerator sqlGenerator = this.dialect.getSqlGenerator(context);
        final String pushdownQuery = sqlGenerator.generateSqlFor(statement);
        LOGGER.finer(() -> "Push-down query generated with " + sqlGenerator.getClass().getSimpleName() + ":\n"
                + pushdownQuery);
        return pushdownQuery;
    }

    private String createImportColumnsDescription(List<DataType> selectListDataTypes) {
        final String columnsDescription = SqlGenerationHelper.createColumnsDescriptionFromDataTypes(selectListDataTypes);
        LOGGER.finer(() -> "columndescription: " + columnsDescription);
        return columnsDescription;
    }
}