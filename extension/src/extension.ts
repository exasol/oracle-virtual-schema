/* eslint-disable @typescript-eslint/no-unused-vars */
import {
    ExasolExtension,
    Parameter,
    SelectOption,
    registerExtension
} from "@exasol/extension-manager-interface";
import { ScriptDefinition, jarFileVersionExtractor } from "@exasol/extension-manager-interface/dist/base";
import { convertVirtualSchemaBaseExtension, createUserPasswordConnectionDefinition, createVirtualSchemaBuilder } from "@exasol/extension-manager-interface/dist/base-vs";
import { ADAPTER_SCRIPT_NAME } from "./common";
import { CONFIG } from "./extension-config";

const EXTENSION_NAME = "Oracle Virtual Schema";

export function createExtension(): ExasolExtension {
    const addressParameter: Parameter = {
        id: "connection", name: "Database connection", description: "This can be a JDBC connection string like 'jdbc:oracle:thin:@//<host>:<port>/<service name>' or a native connection like '(DESCRIPTION = (ADDRESS_LIST = ...))'",
        type: "string", multiline: true, required: true, placeholder: "jdbc:oracle:thin:@//<host>:<port>/<service name>"
    }
    const usernameParameter: Parameter = { id: "username", name: "Oracle username", description: "Name of the Oracle database user", type: "string", required: true, placeholder: "SYS" }
    const passwordParameter: Parameter = { id: "password", name: "Oracle password", description: "Password of the Oracle database user", type: "string", required: true, secret: true }
    return convertVirtualSchemaBaseExtension({
        name: EXTENSION_NAME,
        description: "Virtual Schema for Oracle",
        category: "jdbc-virtual-schema",
        version: CONFIG.version,
        files: [{ name: CONFIG.fileName, size: CONFIG.fileSizeBytes }, { name: "ojdbc8.jar" }],
        scripts: getUdfScriptDefinitions(),
        virtualSchemaAdapterScript: ADAPTER_SCRIPT_NAME,
        scriptVersionExtractor: jarFileVersionExtractor(/virtual-schema-dist-[\d.]+-oracle-(\d+\.\d+\.\d+).jar/),
        builder: createVirtualSchemaBuilder({
            connectionNameProperty: "CONNECTION_NAME",
            virtualSchemaParameters: getVirtualSchemaParameterDefinitions(),
            connectionDefinition: createUserPasswordConnectionDefinition(addressParameter, usernameParameter, passwordParameter)
        })
    })
}

function getUdfScriptDefinitions(): ScriptDefinition[] {
    return [
        {
            name: ADAPTER_SCRIPT_NAME,
            type: "ADAPTER",
            scriptClass: "com.exasol.adapter.RequestDispatcher"
        }
    ]
}

function getVirtualSchemaParameterDefinitions(): Parameter[] {
    return [
        { id: "SCHEMA_NAME", name: "Oracle Schema", description: "Name of the schema in the Oracle database", type: "string", required: true },
        { id: "IMPORT_FROM_ORA", name: "Use ORA connection", description: "Use native IMPORT FROM ORA connection", type: "boolean", required: false, default: "false" },
        { id: "ORA_CONNECTION_NAME", name: "Name of the ORA connection", description: "Name of the native ORA connection", type: "string", required: false, placeholder: "ORA_CONNECTION" },
        { id: "GENERATE_JDBC_DATATYPE_MAPPING_FOR_OCI", name: "Add explicit datatype mapping for ORA connection", description: "This will add explicit datatype mapping to the generated command when using IMPORT FROM ORA", type: "boolean", required: false, default: "false" },
        {
            id: "IMPORT_DATA_TYPES", name: "Import data types", default: "Configure how the VS determines data types", type: "select",
            options: [
                { id: "EXASOL_CALCULATED", name: "Use data types calculated by Exasol database from the query and connection metadata. (default)" },
                { id: "FROM_RESULT_SET", name: "Infer data types from values of the result set." }]
        },
        {
            id: "MAX_TABLE_COUNT", name: "Max. number of tables",
            description: "Max. number of tables to scan when creating the virtual schema. If more tables are found, error E-VSCJDBC-42 will be generated",
            type: "string", regex: "\\d+", required: false, default: "1000"
        },
        {
            id: "DEBUG_ADDRESS", name: "Debug address", description: "Network address and port to which to send debug output",
            type: "string", required: false, placeholder: "192.168.179.38:3000", default: ""
        }, {
            id: "LOG_LEVEL", name: "Log level", description: "Log level for debug output. Debug address must be defined for this to work.",
            type: "select", required: false, options: getJavaLogLevelOptions(), default: ""
        },
    ];
}

function getJavaLogLevelOptions(): SelectOption[] {
    const javaLogLevels: string[] = ["OFF", "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST"];
    return javaLogLevels.map(level => { return { id: level, name: level } })
}

registerExtension(createExtension())
