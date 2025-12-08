# Oracle Virtual Schema 4.0.1, released 2025-12-08

Code name: Fix duplicate literals and NULL in select list

## Summary

This release fixes two issues with literal pushdown.

### Duplicate Literal Values

Queries with duplicate literal values in the select list like `select 1 as a, 1 as b from oravs.tab` or `select 'a' as a, 'a' as b from oravs.tab` failed with the following error:

```
ETL-4300: Oracle tool failed with error code '918' and message 'ORA-00918: column ambiguously defined
Help: https://docs.oracle.com/error-help/db/ora-00918/'
```

The virtual schema now generates column aliases in the pushdown query so that Oracle can distinguish these values.

**Note:** This only works with property `IMPORT_FROM_ORA=true` when also `GENERATE_JDBC_DATATYPE_MAPPING_FOR_OCI` is set to `true`. Otherwise queries will fail with the following error message:

```
[Code: 0, SQL State: 04000]  Adapter generated invalid pushdown query for virtual table TABLE_ORACLE_NUMBER_HANDLING: Data type mismatch in column number 1 (1-indexed). Expected DECIMAL(1,0), but got VARCHAR(1024) UTF8. (pushdown query: IMPORT FROM ORA AT ORACLE_CONNECTION STATEMENT 'SELECT LIMIT_SUBSELECT.* FROM ( SELECT 1 AS c0, 1 AS c1 FROM "SCHEMA_ORACLE_1764911831923"."TABLE_ORACLE_NUMBER_HANDLING"  ) LIMIT_SUBSELECT WHERE ROWNUM <= 1000')
```

### Null Literals

Queries with `NULL` in the select list like `SELECT null as a FROM oravs.tab` failed with the following error:

```
[Code: 0, SQL State: 42636] ETL-1299: Failed to create transformator for column=0 (starting from 0 for selected columns) [ETL-1202: Not implemented - Transformation for this combination of column types is not possible in this version. A solution for this problem can be perhaps the conversion in another datatype in the database. Otherwise please contact support for additional information] (Session: 1850562692756471808)
```

The reason for this problem was that the adapter generated the following pushdown query:

```sql
IMPORT INTO (c1 BOOLEAN) FROM ORA AT ORACLE_CONNECTION STATEMENT 'SELECT NULL FROM "schema"."tab"';
```

When importing into an Exasol `BOOLEAN`, Exasol only accepts a `NUMBER` from Oracle. However, the data type of `NULL` in Oracle is `VARCHAR2`. We solved this by casting `NULL` to `NUMBER` in the pushdown query:

```sql
IMPORT INTO (c1 BOOLEAN) FROM ORA AT ORACLE_CONNECTION STATEMENT 'SELECT CAST(NULL AS NUMBER) FROM "schema"."tab"';
```

**Note:** This only works with property `IMPORT_FROM_ORA=true` when also `GENERATE_JDBC_DATATYPE_MAPPING_FOR_OCI` is set to `true`. Otherwise queries will fail with the following error message:

```
Adapter generated invalid pushdown query for virtual table TABLE_ORACLE_NUMBER_HANDLING: Data type mismatch in column number 1 (1-indexed). Expected BOOLEAN, but got VARCHAR(1024) UTF8. (pushdown query: IMPORT FROM ORA AT ORACLE_CONNECTION STATEMENT 'SELECT CAST(NULL AS NUMBER) FROM "schema"."tab"')
```

## Bugfixes

* #76: Fixed duplicate literals in select list
* #77: Fixed null literal in select list

## Dependency Updates

### Virtual Schema for Oracle

#### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:7.2.0` to `7.2.1`
