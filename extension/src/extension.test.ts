import { ExaMetadata, Installation, Instance, Row } from '@exasol/extension-manager-interface';
import { ExaScriptsRow } from '@exasol/extension-manager-interface/dist/exasolSchema';
import { describe, expect, it } from '@jest/globals';
import { createExtension } from "./extension";
import { CONFIG } from './extension-config';
import { adapterScript, createMockContext, getInstalledExtension, script } from './test-utils';

describe("Oracle VS Extension", () => {

    describe("installableVersions", () => {
        it("contains exactly one 'latest', non deprecated version", () => {
            const latestVersions = createExtension().installableVersions.filter(version => version.latest)
            expect(latestVersions).toHaveLength(1)
            expect(latestVersions[0].deprecated).toEqual(false)
            expect(latestVersions[0].name).toEqual(CONFIG.version)
        })
    })

    describe("extension registration", () => {
        it("creates an extension", () => {
            const ext = createExtension();
            expect(ext).not.toBeNull()
        })

        it("creates a new object for every call", () => {
            const ext1 = createExtension();
            const ext2 = createExtension();
            expect(ext1).not.toBe(ext2)
        })

        it("registers when loaded", () => {
            const installedExtension = getInstalledExtension();
            expect(installedExtension.extension).not.toBeNull()
            expect(typeof installedExtension.apiVersion).toBe('string');
            expect(installedExtension.apiVersion).not.toBe('');
        })
    })

    describe("findInstallations()", () => {
        function findInstallations(allScripts: ExaScriptsRow[]): Installation[] {
            const metadata: ExaMetadata = {
                allScripts: { rows: allScripts },
                virtualSchemaProperties: { rows: [] },
                virtualSchemas: { rows: [] }
            }
            const installations = createExtension().findInstallations(createMockContext(), metadata)
            expect(installations).toBeDefined()
            return installations
        }

        it("returns empty list when no adapter script is available", () => {
            expect(findInstallations([])).toHaveLength(0)
        })

        describe("returns expected installations", () => {
            function installation({ name = "Oracle Virtual Schema", version = "(unknown)" }: Partial<Installation>): Installation {
                return { name, version }
            }

            const tests: { name: string; scripts: ExaScriptsRow[], expected?: Installation }[] = [
                { name: "all values match", scripts: [adapterScript({})], expected: installation({ version: "1.2.3" }) },
                { name: "adapter has wrong name", scripts: [adapterScript({ name: "wrong" })], expected: undefined },
                { name: "adapter missing", scripts: [], expected: undefined },
                { name: "version found in filename", scripts: [adapterScript({ text: "CREATE ... %jar /path/to/virtual-schema-dist-0.0.0-oracle-1.2.3.jar; more text" })], expected: installation({ version: "1.2.3" }) },
                { name: "script contains LF", scripts: [adapterScript({ text: "CREATE ...\n %jar /path/to/virtual-schema-dist-0.0.0-oracle-1.2.3.jar; more text" })], expected: installation({ version: "1.2.3" }) },
                { name: "script contains CRLF", scripts: [adapterScript({ text: "CREATE ...\r\n %jar /path/to/virtual-schema-dist-0.0.0-oracle-1.2.3.jar; more text" })], expected: installation({ version: "1.2.3" }) },
            ]
            tests.forEach(test => {
                it(test.name, () => {
                    const actual = findInstallations(test.scripts)
                    if (test.expected) {
                        expect(actual).toHaveLength(1)
                        expect(actual[0].name).toStrictEqual(test.expected.name)
                        expect(actual[0].version).toStrictEqual(test.expected.version)
                    } else {
                        expect(actual).toHaveLength(0)
                    }
                })
            });
        })
    })

    describe("getInstanceParameters()", () => {
        it("fails for wrong version", () => {
            expect(() => { createExtension().getInstanceParameters(createMockContext(), "wrongVersion") })
                .toThrow(`Version 'wrongVersion' not supported, can only use '${CONFIG.version}'.`)
        })
        it("returns expected parameters", () => {
            const actual = createExtension().getInstanceParameters(createMockContext(), CONFIG.version)
            expect(actual).toHaveLength(12)
            expect(actual[0]).toStrictEqual({
                id: "baseVirtualSchemaName", name: "Virtual schema name", required: true, type: "string",
                description: "Name for the new virtual schema",
                placeholder: "MY_VIRTUAL_SCHEMA", regex: "[a-zA-Z][a-zA-Z0-9_]*"
            })
        })
    })

    describe("install()", () => {
        it("executes expected statements", () => {
            const context = createMockContext();
            createExtension().install(context, CONFIG.version);
            const executeCalls = context.mocks.sqlExecute.mock.calls
            expect(executeCalls.length).toBe(2)
            const adapterScript = executeCalls[0][0]
            expect(adapterScript).toContain(`CREATE OR REPLACE JAVA ADAPTER SCRIPT "ext-schema"."ORACLE_VS_ADAPTER" AS`)
            expect(adapterScript).toContain(`%jar /bucketfs/${CONFIG.fileName};`)
            const expectedComment = `Created by Extension Manager for Oracle Virtual Schema ${CONFIG.version}`
            expect(executeCalls[1]).toEqual([`COMMENT ON SCRIPT "ext-schema"."ORACLE_VS_ADAPTER" IS '${expectedComment}'`])
        })
        it("fails for wrong version", () => {
            expect(() => { createExtension().install(createMockContext(), "wrongVersion") })
                .toThrow(`Version 'wrongVersion' not supported, can only use '${CONFIG.version}'.`)
        })
    })

    describe("uninstall()", () => {
        it("executes query to check if schema exists", () => {
            const context = createMockContext()
            context.mocks.sqlQuery.mockReturnValue({ columns: [], rows: [] });
            createExtension().uninstall(context, CONFIG.version)
            const calls = context.mocks.sqlQuery.mock.calls
            expect(calls.length).toEqual(1)
            expect(calls[0]).toEqual(["SELECT 1 FROM SYS.EXA_ALL_SCHEMAS WHERE SCHEMA_NAME=?", "ext-schema"])
        })
        it("skips drop statements when schema does not exist", () => {
            const context = createMockContext()
            context.mocks.sqlQuery.mockReturnValue({ columns: [], rows: [] });
            createExtension().uninstall(context, CONFIG.version)
            expect(context.mocks.sqlExecute.mock.calls.length).toEqual(0)
        })
        it("executes expected statements", () => {
            const context = createMockContext()
            context.mocks.sqlQuery.mockReturnValue({ columns: [], rows: [[1]] });
            createExtension().uninstall(context, CONFIG.version)
            const calls = context.mocks.sqlExecute.mock.calls
            expect(calls.length).toEqual(1)
            expect(calls[0]).toEqual(['DROP ADAPTER SCRIPT "ext-schema"."ORACLE_VS_ADAPTER"'])
        })
        it("fails for wrong version", () => {
            expect(() => { createExtension().uninstall(createMockContext(), "wrongVersion") })
                .toThrow(`Version 'wrongVersion' not supported, can only use '${CONFIG.version}'.`)
        })
    })

    describe("addInstance()", () => {
        it("executes expected statements", () => {
            const context = createMockContext();
            context.mocks.sqlQuery.mockReturnValue({ columns: [], rows: [] });
            const parameters = [{ name: "baseVirtualSchemaName", value: "NEW_ORA_VS" }, { name: "connection", value: "ora-conn-url" }, { name: "username", value: "ora-user" }, { name: "password", value: "ora-password" }]
            const instance = createExtension().addInstance(context, CONFIG.version, { values: parameters });
            expect(instance.name).toBe("NEW_ORA_VS")
            const calls = context.mocks.sqlExecute.mock.calls
            expect(calls.length).toBe(4)
            expect(calls[0]).toEqual([`CREATE OR REPLACE CONNECTION "NEW_ORA_VS_CONNECTION" TO 'ora-conn-url' USER 'ora-user' IDENTIFIED BY 'ora-password'`])
            expect(calls[1]).toEqual([`CREATE VIRTUAL SCHEMA "NEW_ORA_VS" USING "ext-schema"."ORACLE_VS_ADAPTER" WITH CONNECTION_NAME = 'NEW_ORA_VS_CONNECTION'`])
            const comment = `Created by Extension Manager for Oracle Virtual Schema v${CONFIG.version} NEW_ORA_VS`
            expect(calls[2]).toEqual([`COMMENT ON CONNECTION "NEW_ORA_VS_CONNECTION" IS '${comment}'`])
            expect(calls[3]).toEqual([`COMMENT ON SCHEMA "NEW_ORA_VS" IS '${comment}'`])
        })

        it("returns id and name", () => {
            const context = createMockContext();
            context.mocks.sqlQuery.mockReturnValue({ columns: [], rows: [] });
            const parameters = [{ name: "baseVirtualSchemaName", value: "NEW_ORA_VS" }, { name: "username", value: "id" }]
            const instance = createExtension().addInstance(context, CONFIG.version, { values: parameters });
            expect(instance).toStrictEqual({ id: "NEW_ORA_VS", name: "NEW_ORA_VS" })
        })

        it("escapes single quotes", () => {
            const context = createMockContext();
            context.mocks.sqlQuery.mockReturnValue({ columns: [], rows: [] });
            const parameters = [{ name: "baseVirtualSchemaName", value: "vs'name" }]
            const instance = createExtension().addInstance(context, CONFIG.version, { values: parameters });
            expect(instance).toStrictEqual({ id: "vs'name", name: "vs'name", })
            const calls = context.mocks.sqlExecute.mock.calls
            expect(calls[1]).toEqual([`CREATE VIRTUAL SCHEMA "vs'name" USING "ext-schema"."ORACLE_VS_ADAPTER" WITH CONNECTION_NAME = 'VS''NAME_CONNECTION'`])
        })

        it("fails for wrong version", () => {
            expect(() => { createExtension().addInstance(createMockContext(), "wrongVersion", { values: [] }) })
                .toThrow(`Version 'wrongVersion' not supported, can only use '${CONFIG.version}'.`)
        })
    })

    describe("findInstances()", () => {
        function findInstances(rows: Row[]): Instance[] {
            const context = createMockContext();
            context.mocks.sqlQuery.mockReturnValue({ columns: [], rows });
            return createExtension().findInstances(context, CONFIG.version)
        }
        it("returns empty list for empty metadata", () => {
            expect(findInstances([])).toEqual([])
        })
        it("returns single instance", () => {
            expect(findInstances([["ora_vs"]]))
                .toEqual([{ id: "ora_vs", name: "ora_vs" }])
        })
        it("returns multiple instance", () => {
            expect(findInstances([["vs1"], ["vs2"], ["vs3"]]))
                .toEqual([{ id: "vs1", name: "vs1" }, { id: "vs2", name: "vs2" }, { id: "vs3", name: "vs3" }])
        })
        it("filters by schema and script name", () => {
            const context = createMockContext();
            context.mocks.sqlQuery.mockReturnValue({ columns: [], rows: [] });
            createExtension().findInstances(context, CONFIG.version)
            const queryCalls = context.mocks.sqlQuery.mock.calls
            expect(queryCalls.length).toEqual(1)
            expect(queryCalls[0]).toEqual(["SELECT SCHEMA_NAME FROM SYS.EXA_ALL_VIRTUAL_SCHEMAS WHERE ADAPTER_SCRIPT_SCHEMA = ? AND ADAPTER_SCRIPT_NAME = ? ORDER BY SCHEMA_NAME", "ext-schema", "ORACLE_VS_ADAPTER"])
        })
    })

    describe("deleteInstance()", () => {
        it("drops connection and virtual schema", () => {
            const context = createMockContext();
            createExtension().deleteInstance(context, CONFIG.version, "instId")
            const executeCalls = context.mocks.sqlExecute.mock.calls
            expect(executeCalls.length).toEqual(2)
            expect(executeCalls[0]).toEqual(["DROP VIRTUAL SCHEMA IF EXISTS \"instId\" CASCADE"])
            expect(executeCalls[1]).toEqual(["DROP CONNECTION IF EXISTS \"INSTID_CONNECTION\""])
        })
        it("fails for wrong version", () => {
            expect(() => { createExtension().deleteInstance(createMockContext(), "wrongVersion", "instId") })
                .toThrow(`Version 'wrongVersion' not supported, can only use '${CONFIG.version}'.`)
        })
    })

    describe("upgrade()", () => {
        function scriptWithVersion(name: string, version: string): ExaScriptsRow {
            return script({ name, text: `CREATE ... %jar /path/to/virtual-schema-dist-0.0.0-oracle-${version}.jar;` })
        }
        it("preconditions fail", () => {
            const context = createMockContext()
            context.mocks.simulateScripts(null)
            expect(() => createExtension().upgrade(context))
                .toThrow("Not all required scripts are installed: Validation failed: Script 'ORACLE_VS_ADAPTER' is missing")
        })
        describe("success", () => {
            it("returns versions", () => {
                const context = createMockContext()
                context.mocks.simulateScripts(scriptWithVersion("ORACLE_VS_ADAPTER", "1.2.3"))
                expect(createExtension().upgrade(context)).toStrictEqual({ previousVersion: "1.2.3", newVersion: CONFIG.version, })
            })
            it("executes CREATE SCRIPT statements", () => {
                const context = createMockContext()
                context.mocks.simulateScripts(scriptWithVersion("ORACLE_VS_ADAPTER", "1.2.3"))
                createExtension().upgrade(context)

                const executeCalls = context.mocks.sqlExecute.mock.calls
                expect(executeCalls.length).toBe(2)
                const adapterScript = executeCalls[0][0]
                expect(adapterScript).toContain(`CREATE OR REPLACE JAVA ADAPTER SCRIPT "ext-schema"."ORACLE_VS_ADAPTER" AS`)
                expect(adapterScript).toContain(`%jar /bucketfs/${CONFIG.fileName};`)
                const expectedComment = `Created by Extension Manager for Oracle Virtual Schema ${CONFIG.version}`
                expect(executeCalls[1]).toEqual([`COMMENT ON SCRIPT "ext-schema"."ORACLE_VS_ADAPTER" IS '${expectedComment}'`])
            })
        })
    })

    describe("readInstanceParameterValues()", () => {
        it("is not supported", () => {
            expect(() => { createExtension().readInstanceParameterValues(createMockContext(), "version", "instId") })
                .toThrow("Reading instance parameter values not supported")
        })
    })
})
