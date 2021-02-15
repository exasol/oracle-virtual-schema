# Oracle SQL Dialect User Guide

[Oracle Database](https://www.oracle.com/database/) is a proprietary multi-model database management system produced and marketed by Oracle Corporation. It is a database commonly used for running online transaction processing (OLTP), data warehousing (DW) and mixed (OLTP & DW) database workloads.

## Registering the JDBC Driver in EXAOperation

First download the [Oracle JDBC driver](https://www.oracle.com/technetwork/database/application-development/jdbc/downloads/index.html).

Now register the driver in EXAOperation:

1. Click "Software"
1. Switch to tab "JDBC Drivers"
1. Click "Browse..."
1. Select JDBC driver file
1. Click "Upload"
1. Click "Add"
1. In dialog "Add EXACluster JDBC driver" configure the JDBC driver (see below)

You need to specify the following settings when adding the JDBC driver via EXAOperation.

| Parameter | Value                                               |
|-----------|-----------------------------------------------------|
| Name      | `ORACLE`                                           |
| Main      | `oracle.jdbc.driver.OracleDriver`               |
| Prefix    | `jdbc:oracle:thin:`                               
| Files     | `ojdbc<JDBC driver version>.jar`                |


## Uploading the JDBC Driver to EXAOperation

1. [Create a bucket in BucketFS](https://docs.exasol.com/administration/on-premise/bucketfs/create_new_bucket_in_bucketfs_service.htm)
1. Upload the driver to BucketFS

This step is necessary since the UDF container the adapter runs in has no access to the JDBC drivers installed via EXAOperation but it can access BucketFS.

## Installing the Adapter Script

Upload the latest available release of [Oracle Virtual Schema](https://github.com/exasol/oracle-virtual-schema/releases) to Bucket FS.

Then create a schema to hold the adapter script.

```sql
CREATE SCHEMA ADAPTER;
```

The SQL statement below creates the adapter script, defines the Java class that serves as entry point and tells the UDF framework where to find the libraries (JAR files) for Virtual Schema and database driver.

```sql
CREATE JAVA ADAPTER SCRIPT ADAPTER.JDBC_ADAPTER AS
  %scriptclass com.exasol.adapter.RequestDispatcher;
  %jar /buckets/<BFS service>/<bucket>/virtual-schema-dist-9.0.1-oracle-2.0.0.jar;
  %jar /buckets/<BFS service>/<bucket>/ojdbc<JDBC driver version>.jar;
/
;
```

## Defining a Named Connection

Define the connection to Oracle as shown below.

```sql
CREATE OR REPLACE CONNECTION ORACLE_JDBC_CONNECTION
  TO 'jdbc:oracle:thin:@//<host>:<port>/<service name>'
  USER '<user>'
  IDENTIFIED BY '<password>';
```

A quick option to test the `ORACLE_JDBC_CONNECTION` connection is to run an `IMPORT FROM JDBC` query. The connection works, if `42` is returned.

```sql
IMPORT FROM JDBC AT ORACLE_JDBC_CONNECTION
  STATEMENT 'SELECT 42 FROM DUAL';
```

## Creating a Virtual Schema

Below you see how an Oracle Virtual Schema is created. 

```sql
CREATE VIRTUAL SCHEMA <virtual schema name>
    USING ADAPTER.JDBC_ADAPTER
    WITH
    CONNECTION_NAME = 'ORACLE_JDBC_CONNECTION'
    SCHEMA_NAME     = '<schema name>';
```

## Using IMPORT FROM ORA Instead of IMPORT FROM JDBC

Exasol provides the `IMPORT FROM ORA` command for loading data from Oracle. It is possible to create a virtual schema that uses `IMPORT FROM ORA` instead of JDBC to communicate with Oracle. Both options are indented to support the same features. `IMPORT FROM ORA` almost always offers better performance since it is implemented natively.

This behavior is toggled by the Boolean `IMPORT_FROM_ORA` variable. Note that a JDBC connection to Oracle is still required to fetch metadata. In addition, a "direct" connection to the Oracle database is needed.

### Deploying the Oracle Instant Client

To be able to communicate with Oracle, you first need to supply Exasol with the Oracle Instant Client, which can be obtained [directly from Oracle](http://www.oracle.com/technetwork/database/database-technologies/instant-client/overview/index.html). Open EXAoperation, visit Software -> "Upload Oracle Instant Client" and select the downloaded package. The latest version of Oracle Instant Client we tested is `instantclient-basic-linux.x64-12.1.0.2.0`.

### Creating an Oracle Connection

Having deployed the Oracle Instant Client, a connection to your Oracle database can be set up.

```sql
CREATE CONNECTION ORA_CONNECTION
  TO '(DESCRIPTION =
		(ADDRESS_LIST = (ADDRESS = (PROTOCOL = TCP)
                                   (HOST = <host>)
                                   (PORT = <port>)))
		(CONNECT_DATA = (SERVER = DEDICATED)
                        (SERVICE_NAME = <service_name>)))'
	USER '<username>'
	IDENTIFIED BY '<password>';
```

This connection can be tested using, e.g., the following SQL expression.

```sql
IMPORT FROM ORA at ORA_CONNECTION
  STATEMENT 'SELECT 42 FROM DUAL';
```

### Creating a Virtual Schema USING an ORA CONNECTION

Assuming you already setup the JDBC connection `ORACLE_JDBC_CONNECTION` as shown in the previous section, you can continue with creating the virtual schema.

```sql
CREATE VIRTUAL SCHEMA <virtual schema name>
    USING ADAPTER.JDBC_ADAPTER
    WITH
    CONNECTION_NAME = 'ORACLE_JDBC_CONNECTION'
    SCHEMA_NAME     = '<schema name>'
    IMPORT_FROM_ORA = 'true'
    ORA_CONNECTION_NAME = 'ORA_CONNECTION';
```

## Supported Capabilities

The Oracle dialect does not support all capabilities. A complete list can be found in [OracleSqlDialect.getCapabilities()](../../src/main/java/com/exasol/adapter/dialects/oracle/OracleSqlDialect.java).

## Type Mappings and Limitations

| Orcale Data Type                                                                 | Supported | Converted Exasol Data Type | Comments                                                                                                                                      |
| -------------------------------------------------------------------------------- | --------- | -------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| BINARY\_DOUBLE                                                                   | ✓         | VARCHAR(2000000)           |                                                                                                                                               |
| BINARY\_FLOAT                                                                    | ✓         | VARCHAR(2000000)           |                                                                                                                                               |
| BLOB                                                                             | ×         |                            |                                                                                                                                               |
| BFILE                                                                            | ×         |                            |                                                                                                                                               |
| CHAR \[(size)\]                                                                  | ✓         | CHAR                       |                                                                                                                                               |
| CLOB                                                                             | ×         |                            |                                                                                                                                               |
| DATE                                                                             | ✓         | TIMESTAMP                  | This data type is only supported for positive year values, i.e., years > 0001                                                                 |
| FLOAT \[(p)\]                                                                    | ✓         | DOUBLE                     |                                                                                                                                               |
| INTERVAL DAY \[(day\_precision)\] TO SECOND \[(fractional\_seconds\_precision)\] | ✓         | VARCHAR(2000000)           |                                                                                                                                               |
| INTERVAL YEAR \[(year\_precision)\] TO MONTH                                     | ✓         | VARCHAR(2000000)           |                                                                                                                                               |
| LONG                                                                             | ✓         | VARCHAR(2000000)           | Casted to VARCHAR to prevent a loss of precision.                                                                                             |
| LONG RAW                                                                         | ×         |                            |                                                                                                                                               |
| NCLOB                                                                            | ×         |                            |                                                                                                                                               |
| NCHAR\[(size)\]                                                                  | ✓         | CHAR                       |                                                                                                                                               |
| NUMBER \[ (p \[, s\]) \]                                                         | ✓         | NUMBER or VARCHAR(2000000) | NUMBER with precision > 36 are casted to VARCHAR to prevent a loss of precision. [*](#Mapping-of-number-types)                                |
| NVARCHAR2(size)                                                                  | ✓         | VARCHAR                    |                                                                                                                                               |
| RAW(size)                                                                        | ×         |                            |                                                                                                                                               |
| ROWID                                                                            | ×         |                            |                                                                                                                                               |
| TIMESTAMP \[(fractional\_seconds\_precision)\]                                   | ✓         | TIMESTAMP                  |                                                                                                                                               |
| TIMESTAMP \[(fractional\_seconds\_precision)\] WITH TIME ZONE                    | ✓         | TIMESTAMP                  |                                                                                                                                               |
| UROWID \[(size)\]                                                                | ×         |                            |                                                                                                                                               |
| VARCHAR2(size)                                                                   | ✓         | VARCHAR                    |                                                                                                                                               |



### Mapping of Number Types:

`NUMBER`, `NUMBER with precision > 36` and `LONG` are casted to `VARCHAR` to prevent a loss of precision. 

If you want to return a DECIMAL type for these types you can set the property `ORACLE_CAST_NUMBER_TO_DECIMAL_WITH_PRECISION_AND_SCALE` to `<scale>,<precision>`.
This will cast values of such types to `DECIMAL(<scale>,<precision>)`.

For example:

```java
CREATE VIRTUAL SCHEMA <virtual schema name>
    USING ADAPTER.JDBC_ADAPTER
    WITH
    CONNECTION_NAME = 'ORACLE_JDBC_CONNECTION'
    SCHEMA_NAME     = '<schema name>'
    IMPORT_FROM_ORA = 'true'
    ORACLE_CAST_NUMBER_TO_DECIMAL_WITH_PRECISION_AND_SCALE = '2,18'
    ORA_CONNECTION_NAME = 'ORA_CONNECTION';
```
 
Keep in mind that this will yield errors if the data in the Oracle database does not fit into the specified DECIMAL type.

## Testing information

In the following matrix you find combinations of JDBC driver and dialect version that we tested.

| Virtual Schema Version | Oracle Version     | Driver Name               | Driver Version |
|------------------------|--------------------|---------------------------|----------------|
| 4.0.3                  | Oracle XE 11g      | ojdbc                     | 8              |
| 4.0.3                  | Oracle XE 11g      | instantclient-basic-linux | x64-12.1.0.2.0 |

## Executing Disabled Integration Tests

The integration tests are disabled by default, but it is possible to execute them locally. 
The reason for the tests being disabled is we can only deliver drivers where the license allows redistribution, and it's not the case with Oracle.
Therefore we cannot include the MySQL JDBC driver, so in order to execute the integration tests you need to download them manually.

### Starting Disabled Integration Test Locally

1. Download a JDBC driver and the Oracle instant client from: 
 - Oracle [`ojdbc8.jar`](https://www.oracle.com/database/technologies/appdev/jdbc-ucp-19c-downloads.html)
 - Oracle instant client [`instantclient-basic-linux.x64-12.1.0.2.0.zip`](https://www.oracle.com/database/technologies/instant-client/linux-x86-64-downloads.html).
 Please be aware that Exasol currently supports only mentioned version of the Oracle instant client.  
2. Temporarily put the files into `src/test/resources/integration/driver/oracle` directory.
3. If the files' names are different (you renamed the file, or it has a different version number, for example) from the mentioned above, edit `src/test/resources/integration/driver/oracle/oracle.properties` and `settings.cfg` files.
4. Run the tests from an IDE or temporarily add the integration test name into the `maven-failsafe-plugin`'s includes a section and execute `mvn verify` command.
5. Remove the driver after the test and **do not upload it to the GitHub repository**.
