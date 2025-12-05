# Oracle Virtual Schema 4.0.1, released 2025-12-05

Code name: Fix duplicate literals in select list

## Summary

Queries with duplicate literal values in the select list like `select 1 as a, 1 as b from oravs.tab` or `select 'a' as a, 'a' as b from oravs.tab` failed the following error:

```
ETL-4300: Oracle tool failed with error code '918' and message 'ORA-00918: column ambiguously defined
Help: https://docs.oracle.com/error-help/db/ora-00918/'
```

The virtual schema now generates column aliases in the pushdown query so that Oracle can distinguish these values.

**Note:** This only works with property `IMPORT_FROM_ORA=true` when also `GENERATE_JDBC_DATATYPE_MAPPING_FOR_OCI` is set to `true`. Else queries will fail with the following error message:

```
[Code: 0, SQL State: 04000]  Adapter generated invalid pushdown query for virtual table TABLE_ORACLE_NUMBER_HANDLING: Data type mismatch in column number 1 (1-indexed). Expected DECIMAL(1,0), but got VARCHAR(1024) UTF8. (pushdown query: IMPORT FROM ORA AT ORACLE_CONNECTION STATEMENT 'SELECT LIMIT_SUBSELECT.* FROM ( SELECT 1 AS c0, 1 AS c1 FROM "SCHEMA_ORACLE_1764911831923"."TABLE_ORACLE_NUMBER_HANDLING"  ) LIMIT_SUBSELECT WHERE ROWNUM <= 1000')
```

## Bugfixes

* #76: Fixed duplicate literals in select list

## Dependency Updates

### Virtual Schema for Oracle

#### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:7.2.0` to `7.2.1`
