# Oracle Dialect Developer Guide

## Known Issues

### DST-Sensitive Integration Tests

Some expected values depend on Daylight Saving Time (DST). Until this is fixed in issue [#86](https://github.com/exasol/oracle-virtual-schema/issues/86), adjust the expected values for tests marked with `https://github.com/exasol/oracle-virtual-schema/issues/86` when DST begins and ends.

### Oracle Instant Client Version

Tests using `IMPORT FROM ORA` require the Oracle Instant Client. If the client is missing **or has the wrong version**, queries against the virtual schema will fail with the following exception:

```
SQLException: Oracle instant client not available. Please ask your administrator to install it via EXAoperation.
```

To fix this, find the correct Instant Client version for the tested Exasol version in the [Exasol documentation](https://docs.exasol.com/db/latest/administration/on-premise/manage_drivers/oracle_instant_client.htm) for instructions to find the required version and update the setup method [CommonOracleIntegrationTestSetup.uploadInstantClientToBucket()](../../src/test/java/com/exasol/adapter/dialects/oracle/CommonOracleIntegrationTestSetup.java).

When updating the Instant Client, don't forget to also update user guide section [Testing Information](../user_guide/oracle_user_guide.md#testing-information)