/* eslint @typescript-eslint/no-explicit-any: "off" */
import { Context, QueryResult, SqlClient } from '@exasol/extension-manager-interface';
import { ExaScriptsRow } from '@exasol/extension-manager-interface/dist/exasolSchema';
import * as jestMock from "jest-mock";
import { ADAPTER_SCRIPT_NAME } from './common';

const EXTENSION_SCHEMA_NAME = "ext-schema"

export function getInstalledExtension(): any {
    return (global as any).installedExtension
}

export type ContextMock = Context & {
    mocks: {
        sqlExecute: jestMock.Mock<(query: string, ...args: any) => void>,
        sqlQuery: jestMock.Mock<(query: string, ...args: any) => QueryResult>
        getScriptByName: jestMock.Mock<(scriptName: string) => ExaScriptsRow | null>
        simulateScripts: (adapterScript: ExaScriptsRow | null) => void
    }
}

export function createMockContext(): ContextMock {
    const execute = jestMock.fn<(query: string, ...args: any) => void>().mockName("sqlClient.execute()")
    const query = jestMock.fn<(query: string, ...args: any) => QueryResult>().mockName("sqlClient.query()")
    const getScriptByName = jestMock.fn<(scriptName: string) => ExaScriptsRow | null>().mockName("metadata.getScriptByName()")

    const sqlClient: SqlClient = {
        execute: execute,
        query: query
    }

    return {
        extensionSchemaName: EXTENSION_SCHEMA_NAME,
        sqlClient,
        bucketFs: {
            resolvePath(fileName: string) {
                return "/bucketfs/" + fileName;
            },
        },
        metadata: {
            getScriptByName
        },
        mocks: {
            sqlExecute: execute,
            sqlQuery: query,
            getScriptByName: getScriptByName,
            simulateScripts(adapterScript) {
                getScriptByName.mockImplementation((scriptName) => {
                    if (scriptName === ADAPTER_SCRIPT_NAME) {
                        return adapterScript
                    } else {
                        throw new Error(`Unexpected script name '${scriptName}'`)
                    }
                })
            },
        }
    }
}

export function script({ schema = "schema", name = "name", inputType, resultType, type = "", text = "", comment }: Partial<ExaScriptsRow>): ExaScriptsRow {
    return { schema, name, inputType, resultType, type, text, comment }
}
export function adapterScript({ name = "ORACLE_VS_ADAPTER", type = "ADAPTER", text = undefined }: Partial<ExaScriptsRow>): ExaScriptsRow {
    const version = "1.2.3"
    text = text ?? `CREATE ... %jar /path/to/virtual-schema-dist-0.0.0-oracle-${version}.jar;`
    return script({ name, type, text })
}
