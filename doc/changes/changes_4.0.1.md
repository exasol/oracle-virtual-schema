# Oracle Virtual Schema 4.0.1, released 2025-??-??

Code name: Fix duplicate literals in select list

## Summary

Queries with duplicate literal values in the select list like `select 1 as a, 1 as b from oravs.tab` or `select 'a' as a, 'a' as b from oravs.tab` failed the following error:

```
ETL-4300: Oracle tool failed with error code '918' and message 'ORA-00918: column ambiguously defined
Help: https://docs.oracle.com/error-help/db/ora-00918/'
```

The virtual schema now generates column aliases in the pushdown query so that Oracle can distinguish these values.

## Bugfixes

* #76: Fixed duplicate literals in select list
